#define START_BYTE 255

#define NUMBER_OF_START_BYTES_AT_BEGINNING_OF_MESSAGE 2
#define NUMBER_OF_BYTES_IN_INTEGER 2
#define NUMBER_OF_INTEGERS 3

#define NUMBER_OF_INTEGER_BYTES NUMBER_OF_BYTES_IN_INTEGER*NUMBER_OF_INTEGERS
#define NUMBER_OF_BYTES_PER_INCOMING_MESSAGE NUMBER_OF_START_BYTES_AT_BEGINNING_OF_MESSAGE+NUMBER_OF_INTEGER_BYTES

#define LED_PIN 13
#define SERIAL_BAUD_RATE 115200

int inByte = 0;         // incoming serial byte
int numberOfStartBytesInARow = 0;
int receiveBufferPosition = 0;
boolean fullMessageReceived = false;



// Incoming byte buffer.
byte stepReceiveBytesBuffer[10];

// Stores the 3 step values for X, Y, Z.
//int positions[6];

int positions[6];
int positionsOld[6];

int stepsX = 0;
int stepsY = 0;
int stepsZ = 0;

int positionsX = 0;
int positionsY = 0;
int positionsZ = 0;

long zeitaltX = 0;
long zeitaltY = 0;
long zeitaltZ = 0;

boolean positionReset = true;
int lastPositionTime = 0;
int positionResetTime = 1000; //Zeit nach der die Kamera erkennt, das sie stillstand und positionReset auf tru setzt

float lastFrameTime = 0;
float frameTime = 0;
float rawFrameTime = 0;
long allFrameTimes = 0;
float rawFrameTimes[10];

int motorXpin2 = 2;
int motorYpin10 = 4; 
int motorZpin12 = 12; 

int richtungXpin3 = 3; 
int richtungYpin11 = 5;
int richtungZpin13 = 13;


int endSensorYup7 = 7; 
int endSensorYdown6 = 6; 


int motor_statusX = 0;
int motor_statusY = 0;
int motor_statusZ = 0;

int speedLimit = 2000;

int stepCountX = 0; // step counter for counting down the stapes.
int stepCountY = 0;
int stepCountZ = 0;

long testSpan = 1500;
int testX = 0;

int k = 0; //iterator for the speed normalisation

int bigCounter = 0;
int procCounter = 0;
int pakets = 0;

void setup()
{
  // start serial port at SERIAL_BAUD_RATE bps:
  Serial.begin(SERIAL_BAUD_RATE);

  pinMode(motorXpin2, OUTPUT); 
  pinMode(motorYpin10, OUTPUT); 
  pinMode(motorZpin12, OUTPUT);
  pinMode(richtungXpin3, OUTPUT);  
  pinMode(richtungYpin11, OUTPUT); 
  pinMode(richtungZpin13, OUTPUT); 
  pinMode(6, INPUT); 
  pinMode(7, INPUT); 
  pinMode(8, INPUT); 
  pinMode(9, INPUT); 





  for(int i = 0; i < 10; i++){
    rawFrameTimes[i] = 66666;
  }

}

void loop()
{

  
  //------------------geting the x.y.z. data with bitshifting--------------------

  if (Serial.available() > 0) {
    // get incoming byte:
    inByte = Serial.read();
    if (inByte==START_BYTE) {
      numberOfStartBytesInARow++;
      if (numberOfStartBytesInARow >= NUMBER_OF_START_BYTES_AT_BEGINNING_OF_MESSAGE) {
        receiveBufferPosition = 0;
      } 
      else {
        stepReceiveBytesBuffer[receiveBufferPosition] = (byte)inByte;
        receiveBufferPosition++;        
      }
    } 
    else {
      numberOfStartBytesInARow = 0;
      stepReceiveBytesBuffer[receiveBufferPosition] = (byte)inByte;
      receiveBufferPosition++;
    }

    if ((receiveBufferPosition>=NUMBER_OF_INTEGER_BYTES) && (!fullMessageReceived)) {
      fullMessageReceived = true;    
    }
  }


  if (fullMessageReceived) {
    pakets++;
    
    lastPositionTime = millis();
    digitalWrite(LED_PIN, HIGH);
    for (int i=0; i<NUMBER_OF_INTEGER_BYTES; i+=2) {
      positions[i] = (int)stepReceiveBytesBuffer[i+1] | (int)((stepReceiveBytesBuffer[i]<<8));
    }


    stepsX = (positionsOld[0] - positions[0]);
    stepCountX = abs(stepsX*2); 

    positionsOld[0] = positions[0];

    stepsY = positionsOld[2] - positions[2];
    procCounter += stepsY;
    stepCountY = abs(stepsY*2); 

    positionsOld[2] = positions[2];

    stepsZ = positionsOld[4] - positions[4];
    stepCountZ = abs(stepsZ*3); 

    positionsOld[4] = positions[4];




    if(stepsX < 0){ //setting the motor direction
      digitalWrite(richtungXpin3, HIGH); 
    }
    else
    {
      digitalWrite(richtungXpin3, LOW); 
    } 

    if(stepsY < 0){
      digitalWrite(richtungYpin11, HIGH); 

    }
    else
    {

      digitalWrite(richtungYpin11, LOW); 
    } 

    if(stepsZ < 0){
      digitalWrite(richtungZpin13, HIGH); 

    }
    else
    {

      digitalWrite(richtungZpin13, LOW); 
    } 


    //----------------- speed Limit ---------------

    if(abs(stepsX) > speedLimit || abs(stepsY) > speedLimit){
      stepsX = speedLimit;
      stepsY = speedLimit;
    } 
    //----------------- speed Limit ende---------------


    receiveBufferPosition = 0;
    fullMessageReceived = false;

    //-----------Getting the speed-------------------

    rawFrameTime = micros() - lastFrameTime;
    lastFrameTime = micros();
    rawFrameTimes[k] = rawFrameTime;
    if(k < 10){
      k++;
    } 
    else{
      k = 0;
    } 

    allFrameTimes =0;  
    for(int i = 0; i < 10; i++){
      allFrameTimes = allFrameTimes + rawFrameTimes[i];
    }
    frameTime = allFrameTimes/20;
  }


  if(lastPositionTime - millis() >= positionResetTime){
    positionReset = true;
  }
  
  if(positionReset){
    positionsOld[0] = positions[0];
    positionsOld[2] = positions[2];
    positionsOld[4] = positions[4];
    positionReset = false;
  }

  //------------- motor control--------------------

  if (positionsOld[2] >= 40) {
    Serial.println("pr: "); Serial.println(abs(procCounter));
    Serial.println("mt: "); Serial.println(abs(bigCounter));
    Serial.println("pk: "); Serial.println(pakets);
    
    delay(1000);
  }
  if(stepCountX > 0){
    if (micros() - zeitaltX >= (frameTime/abs(stepsX))) {
      zeitaltX = micros();
      if (motor_statusX == 1){
        digitalWrite(motorXpin2, LOW);
        stepCountX--;
        motor_statusX = 0;
      }
      else
      {
        digitalWrite(motorXpin2, HIGH);
        testX++;
        motor_statusX = 1;
      }
    }
  }
  if(stepCountY > 0){
    if (micros() - zeitaltY >= (frameTime/abs(stepsY)) ) {
      zeitaltY= micros();
      if (motor_statusY == 1){
        digitalWrite(motorYpin10, LOW);
        bigCounter++;
        stepCountY--;
        motor_statusY = 0;
      }
      else
      {
        digitalWrite(motorYpin10, HIGH);
        motor_statusY = 1;
      }
    }
  }
  if(stepCountZ > 0){
    if (micros() - zeitaltZ >= (frameTime/abs(stepsZ)) ) {
      zeitaltZ= micros();
      if (motor_statusZ == 1){
        digitalWrite(motorZpin12, LOW);
        stepCountZ--;
        motor_statusZ = 0;
      }
      else
      {
        digitalWrite(motorZpin12, HIGH);
        motor_statusZ = 1;
      }
    }
  }
}
