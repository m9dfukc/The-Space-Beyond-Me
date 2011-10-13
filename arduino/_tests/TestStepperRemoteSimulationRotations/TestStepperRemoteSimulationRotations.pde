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

long testimeOld = 0;
long testSpan = 1500;
int testX = 0;

int lastTick = 0;

int k = 0; //iterator for the speed normalisation

void setup()
{
  // start serial port at SERIAL_BAUD_RATE bps:
  Serial.begin(SERIAL_BAUD_RATE);
  pinMode(motorYpin10, OUTPUT); 




}

void loop()
{

  if (millis() >= lastTick + 50) {
    positions[2] += 20; 
    lastTick = millis();
    stepsY = positionsOld[2] - positions[2];
    positionsOld[2] = positions[2];


    rawFrameTime = 50000;
  }


  //------------- motor control--------------------
  if (positions[2] <= 420) {
    if (micros() - zeitaltY >= ((rawFrameTime/2)/abs(stepsY)) ) {
      //Serial.println(((rawFrameTime/2)/abs(stepsY)));
      zeitaltY= micros();
      if (motor_statusY == 1){
        digitalWrite(motorYpin10, LOW);
        motor_statusY = 0;
      }
      else
      {
        digitalWrite(motorYpin10, HIGH);
        motor_statusY = 1;
      }
    }
  } else {
    delay(3000); 
    stepCountY = 0;
    
    /*
    positions[2] = 0;
    positionsOld[2] = 0;
    */
  }
}






