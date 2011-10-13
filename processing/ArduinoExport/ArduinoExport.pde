PrintWriter output;
PrintWriter start_end;
PrintWriter scene_length;


/* korrekture der achsen durch multiplikation unten stehender werte */
float correctX = 1.32;
float correctY = 1.15; //1.65;
float correctZ = 2.0;

/* enthält die positionsdaten aus den scenen files */
Data data;
/* positions Objekte, from... und to... sind für die interpolation zwischen zwei szenen gedacht */
FPoint position;
FPoint fromPosition;
FPoint toPosition;

/* angbaen über szenen/frames anzahl und die aktuelle position */
int frameNum = 0;
int lastFrameNum = -1;
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
int[] export = {0,0,0};  
int[] last = new int[3];     

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
boolean nextExit = false;

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
boolean firsttime = false;

int beginCounter = 0;
int frameCounter = 0;

void setup() 
{
  /* generelle framerate - bitte so lassen */
  frameRate(500);
  play = true;

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
  start_end = createWriter("data/export/start_end.txt");
  scene_length = createWriter("data/export/scene_length.txt");
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
      if ( (frameNum % framesCorrection[data.scenePos] == 0) ) {
        frameNum = data.nextFrame();
      }
     

      if (frameNum == -1) data.nextScene();
      //springt zur naechsten Szene, wenn das Ende erreciht ist
      //if (frameNum == data.getNumFrames() - 1) triggerNext=true;
      position = data.getPosition(frameNum);
      
      
      if (frameNum != lastFrameNum) {
        
        int x_ = 0; int y_ = 0; int z_ = 0;
        
        export[0] = int(map(position.x, 0.0f, float(data.stepsWidth), 0.0f, float(realStepsWidth)) * correctX);
        export[1] = int(map(position.y, 0.0f, float(data.stepsHeight), 0.0f, float(realStepsHeight)) * correctY);
        export[2] = int(map(position.z, 0.0f, float(data.stepsDepth), 0.0f, float(realStepsDepth)) * correctZ);
        
        if(firsttime) {
           x_ = last[0] - export[0];
           y_ = last[1] - export[1];
           z_ = last[2] - export[2];
           
           if (((x_ > 128) || (x_ < -128)) && frameCounter != 0) { println("warning x is clipping: " + x_); delay(5000); }
           if (((y_ > 128) || (y_ < -128)) && frameCounter != 0) { println("warning y is clipping: " + y_); delay(5000); }
           if (((z_ > 128) || (z_ < -128)) && frameCounter != 0) { println("warning z is clipping: " + z_); delay(5000); }
        } else {
          println("START TO EXPORT!");
          delay(2000); 
        }
        
        firsttime = true;
        
        last[0] = export[0];
        last[1] = export[1];
        last[2] = export[2];
        
        if (frameCounter == 0) { 
          start_end.print("Scene " + (data.scenePos+1) + " { " + export[0] + "," +  export[1] + "," + export[2] + " }, ");
          x_ = y_ = z_ = 0;
        }
        
        lastFrameNum = frameNum;
        frameCounter++;
        print("Scene ");
        print((data.scenePos+1));
        print("  Frame Number " + frameCounter);
        print("  Steps: ");
        print(x_ + ", ");
        print(y_ + ", ");
        print(z_ + " ");
        print("  Position: ");
        print(export[0] + ", ");
        print(export[1] + ", ");
        println(export[2]);
        
        
        output.print( x_ + "," + y_ + "," + z_ + ", " );
        
      } else {
        if (data.scenePos == 15 && !nextExit) {
          nextExit = true;
        } else if(data.scenePos == 15) {
          start_end.flush();
          start_end.close();
          scene_length.flush();
          scene_length.close();
          exit(); 
        }
        start_end.println("{ " + export[0] + "," +  export[1] + "," + export[2] + " }");
        scene_length.print(frameCounter +", ");
        frameCounter = 0;
        data.nextScene(); 
      }
      
    }
    else if(sceneNum > 0) {
      /* hier befinden wir uns in der interpolationsschleife, ein cos interpolations
       wert zwischen fromPosition und toPosition wird zurück gegeben 
       */
      position = data.interpolate(fromPosition, toPosition, ticksToTrigger, ticksUntilNext);
      println("interpolate step " + ticksUntilNext);
    }

    /* ausgabe der positionsangaben in die java console */
    // toConsole();
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

