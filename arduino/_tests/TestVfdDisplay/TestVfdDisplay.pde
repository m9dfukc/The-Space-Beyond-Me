void setup()
{
  Serial3.begin(9600);
  clearscreen();
  homepos();
}

void loop()
{
 homepos();
 Serial3.print("test"); 
 delay(100);
}


void clearscreen()
{ 
  Serial3.print(0xC, BYTE);
}
void clearline()
{
  Serial3.print(0x18, BYTE);
}
void homepos()
{
   Serial3.print(0xB, BYTE);
}
//Helligkeit regulieren: 1=40%,2=60%,3=80%,4=100% Standard:4
void brightness(int bright)
{ 
  Serial3.print(0x1F, BYTE);
  Serial3.print(0x58, BYTE);
  Serial3.print(bright, BYTE);
}
