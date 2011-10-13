

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

int rotPin = 2;
int blauPin = 1;  
int relaisPin2 = 2;  // potentiometer wiper (middle terminal) connected to analog pin 3
// outside leads to ground and +5V
float rot = 0;           // variable to store the value read
float blau = 0; 
float faktor = 1.5;
int red = 0;
int blue = 0;
int roter[44];
int blauer[44];
float glattR = 0;
float glattB = 0;
int k =0;
int j =0;

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
  pinMode(9, OUTPUT); 
  pinMode(ENCODER_SSI_DATA_PIN, INPUT);
  pinMode(ENCODER_SSI_CLK_PIN, OUTPUT);
  pinMode(ENCODER_SSI_CS_BAR_PIN, OUTPUT);

  digitalWrite(ENCODER_SSI_CLK_PIN, HIGH);
  digitalWrite(ENCODER_SSI_CS_BAR_PIN, HIGH);

}

void loop() {

  rot = analogRead(rotPin); 
  blau = analogRead(blauPin); 
  roter[k] = rot;
  blauer[j] = blau;

  if (k>40){
    k = 0; 
  } 
  else {
    if (rot > 0){
      k++;
    }
  }
  if (j>40){
    j = 0; 
  } 
  else {
    if (blau > 0){
      j++;
    }
  }

  for(int i = 0; i < 40; i++){
    glattR = glattR + roter[i];
  }

  for(int i = 0; i < 40; i++){
    glattB = glattB + blauer[i];
  }

  glattR = glattR/40;
  glattB = glattB/28;

  float faktor = glattR/glattB;
  // Serial.print("rot "); 
  //Serial.println(glattR); 
  // Serial.print("blauuuuuuuuu "); 
  //Serial.println(glattB); 
  //  Serial.print("faaaaaaaafrfghkjgfgdfh "); 
  //Serial.println(faktor); 

  /*
   Serial.println();
   for(int i = 0; i < glattR/10; i++){
   Serial.print("."); 
   }
   Serial.println();
   
   for(int i = 0; i < glattB/10; i++){
   Serial.print("-"); 
   }
   Serial.println();
   Serial.println(faktor);
   Serial.println(glattR+glattB);
   
   
   
   
   
   Serial.println();
   for(int i = 0; i < rot/10; i++){
   Serial.print("."); 
   }
   Serial.println();
   
   for(int i = 0; i < blau/10; i++){
   Serial.print("-"); 
   }
   Serial.println();
   
   */





  if(glattR > 200 | glattB > 200){
    if (faktor > 1.9){
      if (red <= 10)red++;
    }   
    else{
      if (faktor < 0.7){

        if (blue <= 20)blue++;
        //Serial.println("bbb"); 

      } 
      else {
        if (red > 0)red--;
        if (blue > 0)blue--;

      }
    }
  }

  else{
    if (red > 0)red--;
    // if (glattR+glattB < 350){
    if (blue > 0)blue--;
    //Serial.println("III "); 
    //}
  }


  glattR = 0;
  glattB = 0;


  if (red > 3){
    // Serial.println("rot"); 
    Serial.print((byte)2, BYTE);
  }
  if (blue > 6){


    // Serial.print("blauuuuuu "); 

    Serial.print((byte)3, BYTE); 
    digitalWrite(relaisPin2, LOW);
    digitalWrite(9, HIGH);
  }
  if (blue == 0 && red == 0){
    digitalWrite(relaisPin2, HIGH);
    if (dir == 0){
      digitalWrite(9, LOW);
    }
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
          digitalWrite(9, HIGH);
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










