import processing.serial.*;

/* korrekture der achsen durch multiplikation unten stehender werte */
float correctX = 1.32;
float correctY = 1.15; //1.65;
float correctZ = 4.0;

/* enthält die positionsdaten aus den scenen files */
Data data;
/* positions Objekte, from... und to... sind für die interpolation zwischen zwei szenen gedacht */
FPoint position;
FPoint fromPosition;
FPoint toPosition;

/* serial port/arduino einstellungen */
int SERIAL_BAUD_RATE = 115200;
int inByte;
Serial myPort;    
Serial myPortSensor;
String portNameMotor = "/dev/tty.usbserial-A700e02R";
String portNameSensor = "/dev/tty.usbserial-A9007LGe";

/* angbaen über szenen/frames anzahl und die aktuelle position */
int frameNum = 0;
int sceneNum = 0;
int framesCount = 0;
int scenesCount = 0;
/* die Txts haben mehr Werte als es Frames bei der Kamera gibt, wir muesen also Frames verwerfen, 
 im Arraz steht der wievielte Frame einer Szene verworfen werden muss (Werte durch Messung der Laufzeit ermittelt)*/
int[] framesCorrection = { 
  24, 16, 17, 18, 24, 31, 32, 87, 37, 99, 55, 64, 45, 80, 42, 34
};

/* arduino übertragungseinstwellungen */
int[] val = new int[3];                     
int numberOfIntsToSend = 3;    
int numberOfStartBytes = 2;      
int startByte = 255;
int bytesInMessage = (2*numberOfIntsToSend)+numberOfStartBytes; 
byte[] dumpit = new byte[bytesInMessage];        

int wheel = 0;
int wheelOld = 0;
int frameTime = 0;
int startvideoframe = 0;

int ticks = 0;

/* livemode auf true setzen wenn die arduinos verbunden sind, sollte aber auch automatisch funktionieren */
boolean livemode = false;
boolean play     = false;
boolean rev      = false;
boolean loadStart= true; 

/* ticks entspricht den bildern zwischen 2 szenen bzw. vom anfang ... eventuell muss ier noch getuned werden*/
int ticksToTriggerNext  = 44; //a;ter wert 44
int ticksToTriggerStart = 70; //ALTER WERT war 57
int ticksToDefaultPosition = 250;
int ticksToTrigger      = 0; // kann entweder ticksToTriggerNext o. ticksToTriggerStart annehmen ... siehe weiter unten 
int ticksUntilNext      = 0;
int ticksUntilDefault   = 0;
int ticksToTriggerDefault = 0;
int filmDir             = 1;

/* realsteps entspricht der auflösung der microstepper */
private int realStepsWidth  = 6400*5;
private int realStepsHeight = 3200; //ALTER WERT 3200!!!!!!!
private int realStepsDepth  = 940;

private int screenStepsWidth = 4140/2;
private int screenStepsHeight = 720/2;

/* boolean variablen für die zustände begin, ende eines films bzw. ob gerade interpoliert wird */
boolean filmEnded       = false;
boolean filmBegins      = true;
boolean triggerNext     = false;
boolean interpolate     = false;
boolean moveToStartPos  = false;

boolean printFrameCount = false;
boolean printedFrameCount = false;

int beginCounter = 0;

void setup() 
{
  size(screenStepsWidth, screenStepsHeight);
  /* generelle framerate - bitte so lassen */
  frameRate(200);


  println(Serial.list());

  try {

    boolean pluggedSensor = false;
    boolean pluggedMotor  = false; 

    for (int i = 0; i < Serial.list().length; i++) {
      if (Serial.list()[i].equals(new String(portNameSensor)) ) pluggedSensor = true;
      if (Serial.list()[i].equals(new String(portNameMotor)) )  pluggedMotor = true;
    }

    if (pluggedSensor && pluggedMotor) {
      myPort = new Serial(this, portNameMotor, SERIAL_BAUD_RATE);
      myPortSensor =  new Serial(this, portNameSensor, SERIAL_BAUD_RATE);

      /* initialize Start Bytes buffer */
      for (int i=0; i< numberOfStartBytes; i++) {
        dumpit[i] = (byte)startByte;
      }

      livemode = true;
      play = false;
    } 
    else {
      println("warning - arduino(s) not connected!");
    }
  } 
  catch(Exception e) {
    println("warning - error in Serial connection");
  }

  if (!livemode) {
    frameRate(10);
    play = true;
    println("warning - livemode not set! Serial ports not initialized.");
  }

  /* initialisierung der kamera positionsangaben */
  initializePositions();
}

/* initialisierung der kamera positionsangaben */
void initializePositions() {

  background(220);
  wheel = 0;
  wheelOld = 0;
  /* initialisiere das positions (data) objekt */
  data = new Data();  
  data.resetScenes(); /* szene an den anfang setzen */
  scenesCount = data.getNumScenes(); /* auslesen wie viele szenen insgesamt vorhanden */

  /* triggert die erste überblendung - explizit die für film begin */
  triggerNext = filmBegins = true;

  /* setzen der ticks count down variablen */
  ticksUntilNext = ticksToTriggerStart;
  ticksToTrigger = ticksToTriggerStart; 

  /* auslesen der aktuellen scenen bzw. frame position */
  sceneNum = data.nextScene();
  frameNum = data.nextFrame(); 
  
  position     = new FPoint(0,0,0);
  fromPosition = new FPoint(0,0,0);
  toPosition   = new FPoint(0,0,0);

  println("BEGINNING OF THE FILM!");
}

void resetPositions() {
    
    if ( filmEnded ) {
        background(255, 0, 0);
    
        filmDir      = 1;
        fromPosition = data.getVeryLastPosition(); //position;
        toPosition   = data.getVeryFirstPosition();
        
        //sceneNum = frameNum = 0;
    
        moveToStartPos = true; filmEnded = false;
        ticksUntilDefault     = ticksToDefaultPosition;
        ticksToTriggerDefault = ticksToDefaultPosition;
    
        println("MOVE TO DEFAULT START POSITIONS!");
    }
}


void draw() 
{
  if (moveToStartPos) {
      
      if ( frameCount%10 == 0 ) {
          ticksUntilDefault--;
          position = data.interpolate(fromPosition, toPosition, ticksToTriggerDefault, ticksUntilDefault);
          
          if (ticksUntilDefault == 0) {
              filmEnded = false;
              moveToStartPos = false;
              initializePositions();
          } else {

            println ("Moving back to default start position - counting "+ ticksUntilNext);

            //int steps_y = int(map(position.y, 0, data.stepsHeight, 0, realStepsHeight) * correctY); 
            /* daten für die x, y und z achse */
            val[0] = int(map(position.x, 0, data.stepsWidth, 0, realStepsWidth) * correctX);
            val[1] = int(map(position.y, 0, data.stepsHeight, 0, realStepsHeight) * correctY);
            val[2] = -1 * int(map(position.z, 0, data.stepsDepth, 0, realStepsDepth) * correctZ);

            println("SERIAL " + val[0] + " " + val[1] + " " + val[2] );

            for (int j=0; j<numberOfIntsToSend; j++) {
              dumpit[numberOfStartBytes+(2*j)] = (byte)((val[j]>>8)&0xFF);  
              dumpit[numberOfStartBytes+(2*j)+1] = (byte)(val[j]&0xFF);
            } 
            myPort.write(dumpit);
        }
          
      }
  }
  
  if (play) {
    if (rev) {
      wheel--; 
      ticks--;
      filmDir = -1;
    } 
    else { 
      wheel++; 
      ticks++;
      filmDir = +1;
    }
  }

  if( (wheel - wheelOld >= 2) ) {
    println("MISSED FRAMES: " + (wheel-wheelOld-1) + " frameNum: " + frameNum);
  }


  if ( printFrameCount && !printedFrameCount) {
    printedFrameCount = true;
    printFrameCount = false;
    int frameDiff = (data.framesCount - ticks);
    int frameRelation = (int)(ticks/(data.framesCount-1)*100);
    float frameRelationDec = (ticks/(data.framesCount-1));
    println("Scene " + sceneNum + " AbsoluteFramesCam " + wheel + " CurrentFramesCam " + ticks + " CurrentFramesScene " + frameNum + " TotalFramesScene " + data.framesCount + " Diff " + frameDiff + " Faktor " + frameRelationDec);
    ticks = 0;
  }


  if ( (wheel > wheelOld) ) {

    if (beginCounter > 0) beginCounter--; // wirkt dem random trigger vom blauen sensor entgegen.
    wheelOld = wheel;

    /* wenn "triggerNext" true ist, decrementieren wir ticksUntilNext 
     bis es 1 ist und springen dann zur nächsten szene
     */
    if ( triggerNext ) {
      /* wenn ticksUntilNext == 1, also der countdown timer abgelaufen ist
       gehen wir zur nächsten szene indem wir triggerNext wieder auf false setzen
       */
      if ( ticksUntilNext == 1 ) {
        beginCounter = 0;
        triggerNext = false;
        loadStart = true;
        filmEnded = true;
        printedFrameCount = false;
        print("TRIGGER SCENE NUMBER: ");
        println(sceneNum);
      } 

      /* beim ersten sprung in die triggerNext interpolationsschlaufe 
       ( wenn ticksUntilNext == 0) initialisieren wir die variablen 
       und holen uns die nächsten szenen postionsangaben
       */
      if ( ticksUntilNext == 0 ) {
        ticksUntilNext = ticksToTriggerNext;
        ticksToTrigger = ticksToTriggerNext; 

        sceneNum = data.nextScene();
        frameNum = data.nextFrame(); 

        toPosition = data.getPosition(frameNum); 
        fromPosition = new FPoint(position.x, position.y, position.z);
      } 
      else {
        ticksUntilNext--;
      }
    }

    /* befinden wir uns NICHT in der interpolationsschleife holen wir 
     uns für jeden wheel increment (drehsensor schritt) eine neue positionsangabe (position)
     aus dem aktuellen szenen file ... allerdings nur solange wheel increment <= der
     positionsangaben in dem file entspricht, ansonsten bleibt er bei dem letzten frame
     (position) in der aktuellen szene stehen - das passiert z.b. wenn er den triggerNext
     durch den sensor nicht bekommt! ... hier könnte z.b. auch eins script rein um alle
     x frames auszulassen falls mehr positionsangaben vorhanden sind als wirklich bilder auf
     dem projektor (einfach 'frameNum = data.nextFrame()' mehrmals aufrufen)
     */
    if ( !triggerNext ) {
      printedFrameCount = false;
      frameNum = data.nextFrame();

      /*Korrektur um die unterschiedlichen Frameanzahlen von Kamera und Software anzupassen, bei jeder Szene wird ein nter Frame verworfen
       das Array framesCorrection fuert die jeweiligen Verzoegerungen pro Szene*/
      if ( (frameNum % framesCorrection[sceneNum] == 0) ) {
        frameNum = data.nextFrame();
      }

      if (frameNum == -1) data.nextScene();
      //springt zur naechsten Szene, wenn das Ende erreciht ist
      //if (frameNum == data.getNumFrames() - 1) triggerNext=true;
      position = data.getPosition(frameNum);
    }
    else if(sceneNum > 0) {
      /* hier befinden wir uns in der interpolationsschleife, ein cos interpolations
       wert zwischen fromPosition und toPosition wird zurück gegeben 
       */
      position = data.interpolate(fromPosition, toPosition, ticksToTrigger, ticksUntilNext);
      println("interpolate step " + ticksUntilNext);
    }
    
    if ( moveToStartPos ) {
        position = data.interpolate(fromPosition, toPosition, ticksToTriggerDefault, ticksUntilDefault);
        if (ticksUntilDefault == 0) {
            filmEnded = false;
            moveToStartPos = false;
            initializePositions();
        }
        
        println ("Moving back to default start position - counting "+ ticksUntilNext);
    }


    /* sende positions daten an den arduino */
    if (livemode && filmDir == 1) {
      //int steps_y = int(map(position.y, 0, data.stepsHeight, 0, realStepsHeight) * correctY); 
      /* daten für die x, y und z achse */
      val[0] = int(map(position.x, 0, data.stepsWidth, 0, realStepsWidth) * correctX);
      val[1] = int(map(position.y, 0, data.stepsHeight, 0, realStepsHeight) * correctY);
      val[2] = -1 * int(map(position.z, 0, data.stepsDepth, 0, realStepsDepth) * correctZ);

      //println("SERIAL " + val[0] + " " + val[1] + " " + val[2] );

      for (int j=0; j<numberOfIntsToSend; j++) {
        dumpit[numberOfStartBytes+(2*j)] = (byte)((val[j]>>8)&0xFF);  
        dumpit[numberOfStartBytes+(2*j)+1] = (byte)(val[j]&0xFF);
      } 
      myPort.write(dumpit);
    }

    data.draw(screenStepsWidth, screenStepsHeight);

    /* ausgabe der positionsangaben in die java console */
    toConsole();
  }
}

void serialEvent(Serial myPortSensor) 
{
  if ( livemode ) {
    frameTime = char(myPortSensor.read());

    /* drehgeber, ein bild zurück */
    if(frameTime == 1) {
      wheel--;
      ticks--;
      filmDir = -1;
    }
    /* drehgeber, ein bild weiter */
    if(frameTime == 0) {
      wheel++;
      ticks++;
      filmDir = 1;
    } 

    /* roter sensor, eine szene weiter */
    if(frameTime == 2) {
      if ( filmDir == 1 ) triggerNext  = true;
      printFrameCount = true;
    } 

    /* blauer sensor, film ende oder anfang */
    if(frameTime == 3) {

      printFrameCount = true;
      /* wird nur getriggert wenn der film vorher im rücklauf war oder
       das programm soeben erst gestartet wurde
       */

      // if (filmDir == -1 || filmBegins) {
      //println("sensor detects film beginning");
      beginCounter++;
      if(wheel > 20) {
        loadStart = true;
      }
      if(wheel < -20) {
        loadStart = true;
      }
      /* blauer muss mindestens 300 mal 'anschlagen' bevor (beginCounter)
       die position resetet wird und er muss mindestens die interpolation 
       zur ersten scene bzw. ersten position volendet haben bevor er neu 
       getriggert/resetet werden kann ('loadStart == true', siehe 'ticksUntilNext == 1').
       */

      if( loadStart && beginCounter > 300) {

        //println("============================= reset reset reset reset =============================");
        if (filmDir == 1) resetPositions();
        else initializePositions();
        loadStart = false;
        beginCounter = 0;
      }
    }
  }
}

void toConsole() 
{
  print(sceneNum);
  print(" ");
  print(frameNum);
  print(" ");
  print(position.x);
  print(" ");
  print(position.y);
  print(" ");
  println(position.z);
}

