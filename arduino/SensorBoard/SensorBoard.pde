

//----------------BEGIN INCLUDES---------------------

#include <stdlib.h>

//----------------END INCLUDES---------------------



//----------------END DEFINES---------------------


// Define one OR the other = results in #defines for number of bits
//#define MEGATRON_MAB18_10_HS_5_SER 10   // 10-Bit, 5V, SSI
#define MEGATRON_MAB18_12_HS_5_SER 12   // 12-Bit, 5V, SSI

#ifdef MEGATRON_MAB18_10_HS_5_SER
#define ENCODER_NUMBER_OF_DATA_BITS 10
#define ENCODER_NUMBER_OF_STATUS_BITS 5
#define ENCODER_NUMBER_OF_PARITY_BITS 1
#endif

#ifdef MEGATRON_MAB18_12_HS_5_SER
#define ENCODER_NUMBER_OF_DATA_BITS 12
#define ENCODER_NUMBER_OF_STATUS_BITS 5
#define ENCODER_NUMBER_OF_PARITY_BITS 1
#endif

#define ENCODER_SSI_CS_BAR_PIN 5
#define ENCODER_SSI_CLK_PIN 6
#define ENCODER_SSI_DATA_PIN 7

#define ACTIVE true
#define IDLE false

#define REQUESTED true
#define SERVICED false

#define RISING_EDGE true
#define FALLING_EDGE false

#define LED_PIN 13


//-- ------------rot - sensor stuff ----------------------

int rotPin = 1;
int blauPin = 2;  
int relaisPin2 = 2;  // potentiometer wiper (middle terminal) connected to analog pin 3
// outside leads to ground and +5V
float rot = 0;           // variable to store the value read
float blau = 0; 
float faktor = 1.5;
int red = 0;
int blue = 0;

//----------------END DEFINES---------------------



//----------------BEGIN VARIABLE DEFINITIONS----------------


long int rawPosition = 0;              // The 12-bit encoder output.
long int interval = 512;
long int lastSent = 0;
int dir = 0;

boolean Sent = false;

long lastFrame = 0;
byte frameTime = 0;
//----------------END VARIABLE DEFINITIONS----------------


void setup() {
  pinMode(LED_PIN, OUTPUT);                // declare the ledPin as an OUTPUT
  Serial.begin(115200);                    // start serial communications.


  pinMode(relaisPin2, OUTPUT); 

  pinMode(ENCODER_SSI_DATA_PIN, INPUT);
  pinMode(ENCODER_SSI_CLK_PIN, OUTPUT);
  pinMode(ENCODER_SSI_CS_BAR_PIN, OUTPUT);

  digitalWrite(ENCODER_SSI_CLK_PIN, HIGH);
  digitalWrite(ENCODER_SSI_CS_BAR_PIN, HIGH);

}

void loop() {

  rot = analogRead(rotPin); 
  blau = analogRead(blauPin); 

  //digitalWrite(relaisPin2, HIGH);
  faktor = rot/blau;
  
  
  if(rot > 450 | blau > 200){

    if (faktor > 2.7 ){
     // Serial.println("ROT  -----------------------------------------rot- "); 
     // Serial.println(rot); 
     // Serial.println(faktor); 
     //Serial.print((byte)2, BYTE);
     if (red <= 10)red++;

    }   
    else{
      

      if (faktor < 0.9){
     //   Serial.print((byte)3, BYTE);
       // Serial.println("BLAU  - "); 
        //Serial.println(blau); 
        //Serial.println(faktor); 
       //digitalWrite(relaisPin2, LOW);
     if (blue <= 10)blue++;
    
      } 
      else {
       // digitalWrite(relaisPin2, HIGH);
        //Serial.println("weiss"); 
        //Serial.println(blau); 
        //Serial.println(faktor); 
      if (red > 0)red--;
      if (blue > 0)blue--;
      }
    }
  }
  else{
       //   Serial.println("black"); 
        //Serial.println(blau);
       //Serial.println(rot);  
        //Serial.println(faktor); 
    //digitalWrite(relaisPin2, HIGH);
     if (red > 0)red--;
    if (blue > 0)blue--;
  }
  
  if (red > 3){
   // Serial.println("rot"); 
    Serial.print((byte)2, BYTE);
  }
    if (blue > 3){
   // Serial.println("blau");
   Serial.print((byte)3, BYTE); 
    digitalWrite(relaisPin2, LOW);
  }
  if (blue == 0 && red == 0){
    digitalWrite(relaisPin2, HIGH);
  }




  rawPosition = (int)getEncoderPosition()/interval;
  if (rawPosition != lastSent) {

    Sent = false;
    frameTime = millis() - lastFrame;
    lastFrame = millis();
    // println(frameTime);

  }


  if (!Sent) {
    digitalWrite(LED_PIN, HIGH);  // turn the LED on
    Serial.flush();
    if(rawPosition < lastSent || (rawPosition == 7 && lastSent == 0)){ // es dreht sic
      if(lastSent == 7 && rawPosition == 0)
      {
        if(dir == 1){
          Serial.print((byte)1, BYTE);
          //  Serial.print("<"); 
          //Serial.println(rawPosition, DEC); 
        } 
        else{
          dir = 1;
        }
      } 
      else{
        if(dir == 0){
          Serial.print((byte)0, BYTE);
          //  Serial.print(">"); 
          //Serial.println(rawPosition, DEC); 
        } 
        else{
          dir = 0;
        }
      }
    }
    else {
      //  
      if(dir == 1){
        Serial.print((byte)1, BYTE);
        // Serial.print("<"); 
        //Serial.println(rawPosition, DEC); 
      } 
      else{
        dir = 1;
      }
    }

    Serial.flush();
    lastSent = rawPosition;
    Sent = true;
  }
}



long int getEncoderPosition()
{
  long int position = 0;
  //shift in our data  
  digitalWrite(ENCODER_SSI_CS_BAR_PIN, LOW);
  //  delayMicroseconds(1);
  long int d1 = shiftIn(ENCODER_SSI_DATA_PIN, ENCODER_SSI_CLK_PIN, ENCODER_NUMBER_OF_DATA_BITS, FALLING_EDGE);
  int d2 = shiftIn(ENCODER_SSI_DATA_PIN, ENCODER_SSI_CLK_PIN, ENCODER_NUMBER_OF_STATUS_BITS, FALLING_EDGE);
  int d3 = shiftIn(ENCODER_SSI_DATA_PIN, ENCODER_SSI_CLK_PIN, ENCODER_NUMBER_OF_PARITY_BITS, FALLING_EDGE);
  //  delayMicroseconds(1);
  digitalWrite(ENCODER_SSI_CS_BAR_PIN, HIGH);
  //  delayMicroseconds(1);

  //get our position variable
  position = d1;

  //check the offset compensation flag: 1 == started up
  if (!(d2 & B00010000))
    position = -1;

  //check the cordic overflow flag: 1 = error
  if (d2 & B00001000)
    position = -2;

  //check the linearity alarm: 1 = error
  if (d2 & B0000100)
    position = -3;

  //check the magnet range: 11 = error
  if ((d2 & B00000011) == B00000011)
    position = -4;

  return position;
}

//read in a byte of data from the digital input of the board. read data on falling-edge of CLK.
long int shiftIn(byte dataPin, byte clockPin, byte bits, boolean sampleEdge)
{
  long int data = 0;
  if ((bits > 0) && (bits <= 32)) {
    for (int i=bits-1; i>=0; i--)
    {
      if (sampleEdge == FALLING_EDGE) {
        digitalWrite(clockPin, LOW);
        //        delayMicroseconds(1);
        digitalWrite(clockPin, HIGH);
        //        delayMicroseconds(1);
      }

      if (sampleEdge == RISING_EDGE) {
        digitalWrite(clockPin, HIGH);
        delayMicroseconds(1);
        digitalWrite(clockPin, LOW);
        delayMicroseconds(1);
      }      
      byte bitly = digitalRead(dataPin);
      data |= (bitly << i);
    }
  } 
  else {
    data = -1;
  }
  return data;
}









