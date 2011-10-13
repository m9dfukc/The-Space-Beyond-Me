#define START_BYTE 255

#define NUMBER_OF_START_BYTES_AT_BEGINNING_OF_MESSAGE 2
#define NUMBER_OF_BYTES_IN_INTEGER 2
#define NUMBER_OF_INTEGERS 3

#define NUMBER_OF_INTEGER_BYTES NUMBER_OF_BYTES_IN_INTEGER*NUMBER_OF_INTEGERS
#define NUMBER_OF_BYTES_PER_INCOMING_MESSAGE NUMBER_OF_START_BYTES_AT_BEGINNING_OF_MESSAGE+NUMBER_OF_INTEGER_BYTES

#define LED_PIN 13
#define SERIAL_BAUD_RATE 115200


#include <InTimerFive.h>
#include <InTimerOne.h>
#include <InTimerThree.h>


int counter1 = 0;
int counter3 = 0;
int counter5 = 0;
boolean out1 = false; 
boolean out3 = false;
boolean out5 = false;



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
long lastPositionTime = 0;
int positionResetTime = 1000; //Zeit nach der die Kamera erkennt, das sie stillstand und positionReset auf tru setzt

float lastFrameTime = 0;
int frameTime = 0;
float rawFrameTime = 0;
long allFrameTimes = 0;
float rawFrameTimes[10];

int motorXpin2 = 2;
int motorYpin10 = 4; 
int motorZpin12 = 8; 

int richtungXpin3 = 3; 
int richtungYpin11 = 5;
int richtungZpin13 = 9;


int endSensorYup7 = 7; 
int endSensorYdown6 = 6; 


int motor_statusX = 0;
int motor_statusY = 0;
int motor_statusZ = 0;

int speedLimit = 2000;

int stepCountX = 0; // step counter for counting down the stapes.
int stepCountY = 0;
int stepCountZ = 0;

long testimeOld = 0;
int testX = 0;

boolean start;

int k = 0; //iterator for the speed normalisation

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
  //pinMode(6, INPUT); 
  //pinMode(7, INPUT); 
  // pinMode(8, INPUT); 
  // pinMode(9, INPUT); 

  Timer1.initialize(500000);         // initialize timer1, and set for every 500 ms
  Timer1.attachInterrupt(callback1, 10000);
  Timer1.stop();
  Timer3.initialize(500000);         // initialize timer3, and set for every 750 ms
  Timer3.attachInterrupt(callback3, 2000000);
  Timer3.stop();
  Timer5.initialize(500000);         // initialize timer5, and set for every 1000 ms
  Timer5.attachInterrupt(callback5, 1000000);
  Timer5.stop();
  start = false;


  for(int i = 0; i < 10; i++){
    rawFrameTimes[i] = 66666;
  }

}


void callback1()
{
  digitalWrite(motorXpin2, motor_statusX=!motor_statusX);
}

void callback3()
{
  digitalWrite(motorYpin10, motor_statusY=!motor_statusY);
}

void callback5()
{
  digitalWrite(motorZpin12, motor_statusZ=!motor_statusZ);
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
    lastPositionTime = millis();
    digitalWrite(LED_PIN, HIGH);
    for (int i=0; i<NUMBER_OF_INTEGER_BYTES; i+=2) {
      positions[i] = (int)stepReceiveBytesBuffer[i+1] | (int)((stepReceiveBytesBuffer[i]<<8));
    }

    stepsX = positionsOld[0] - positions[0];
    stepCountX = abs(stepsX*2); 
    positionsOld[0] = positions[0];
    //positionsOld[0] = positions[0];

    stepsY = positionsOld[2] - positions[2];
    stepCountY = abs(stepsY*2); 
    positionsOld[2] = positions[2];
    //positionsOld[2] = positions[2];

    stepsZ = positionsOld[4] - positions[4];
    stepCountZ = abs(stepsZ*2); 
    positionsOld[4] = positions[4];

    //positionsOld[4] = positions[4];


    //Serial.println(positions[4]);


    //Serial.print(stepsX);Serial.print(" ");Serial.print(stepsY);Serial.print(" ");Serial.print(stepsZ);



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
    frameTime = int(allFrameTimes/20);

  if(millis() - lastPositionTime >= positionResetTime || start){
    positionsOld[0] = positions[0];
    positionsOld[2] = positions[2];
    positionsOld[4] = positions[4];
    stepsX = 0;
    stepsY = 0;
    stepsZ = 0;
    Timer1.stop();
    Timer3.stop();
    Timer5.stop();
  }


    if(stepsX != 0){
      Timer1.setPeriod(abs(frameTime/stepsX));
      Timer1.start();
    }
    else 
    {
      Timer1.stop();
    }

    if(stepsY != 0){
      Timer3.setPeriod(abs(frameTime/stepsY));
      Timer3.start();
    }
    else 
    {
      Timer3.stop();
    }
    if(stepsZ != 0){
      Timer5.setPeriod(abs(frameTime/stepsZ));
      Timer5.start();
    }
    else 
    {
      Timer5.stop();
    }

  }

  if(millis() - lastPositionTime >= positionResetTime || start){
    positionsOld[0] = positions[0];
    positionsOld[2] = positions[2];
    positionsOld[4] = positions[4];
    stepsX = 0;
    stepsY = 0;
    stepsZ = 0;
    Timer1.stop();
    Timer3.stop();
    Timer5.stop();
  }

  if (positions[0] == 0 && positions[2] == 0 && positions[4] == 0) start = true; 
  else start = false;

  //------------- motor control--------------------


  /* if(stepCountX > 0 && stepsX != 0){
   if (micros() - zeitaltX >= (frameTime/abs(stepsX))) {
   zeitaltX = micros();
   if (motor_statusX == 1){
   digitalWrite(motorXpin2, LOW);
   if (stepsX < 0) {
   positionsOld[0]++; 
   } else if (stepsX > 0) {
   positionsOld[0]--;
   }
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
   
   
   if(stepCountY > 0 && stepsY != 0){
   if (micros() - zeitaltY >= (frameTime/abs(stepsY)) ) {
   zeitaltY= micros();
   if (motor_statusY == 1){
   digitalWrite(motorYpin10, LOW);
   if (stepsY < 0) {
   positionsOld[2]++; 
   } 
   else if (stepsY > 0) {
   positionsOld[2]--;
   }
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
   if(stepCountZ > 0 && stepsZ != 0){
   if (micros() - zeitaltZ >= (frameTime/abs(stepsZ)) ) {
   zeitaltZ= micros();
   if (motor_statusZ == 1){
   digitalWrite(motorZpin12, LOW);
   if (stepsZ < 0) {
   positionsOld[4]++; 
   } 
   else if (stepsZ > 0) {
   positionsOld[4]--;
   }
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
   */
}


