#include "WProgram.h"
void setup();
void loop();
#include <Flash.h>

/* named arrays */
FLASH_TABLE( unsigned int, scene_1, 3,
  {4000, 2000, 333},
  {2550, 300, 2555},
  {100, 2002, 400}
);

FLASH_TABLE( unsigned int, scene_2, 3,
  {2000, 4000, 3333333222},
  {6550, 3300, 24455}
);

_FLASH_TABLE<unsigned int> scenes[] = { scene_1, scene_2 };
int scene_count = sizeof(scenes) / sizeof(_FLASH_TABLE<unsigned int>);

//scenes[0] = scene_1;

void setup() {
  Serial.begin(9600);  
  
  Serial.print("RAM Table: "); Serial.println(sizeof(_FLASH_TABLE<unsigned int>));
  Serial.print("RAM scenes: "); Serial.println(sizeof(scenes));
  
  Serial.print("Mem: "); Serial.println(availableMemory());

  // Example 2: Named flash strings
  // 2.b Find the length of flash string (internally uses strlen_P)
  Serial.print("Rows s1: "); Serial.println(scene_1.rows());
  Serial.print("RAM s1: "); Serial.println(sizeof(scene_1));
  
  Serial.print("Rows s2: "); Serial.println(scene_2.rows());
  Serial.print("RAM s2: "); Serial.println(sizeof(scene_2));
  
  for(int s=0; s < scene_count; ++s) {
   for(int i=0; i < scenes[s].rows(); ++i) {
      _FLASH_ARRAY<unsigned int> scene = scenes[s][i];
      Serial.print("Scene: "); 
      Serial.print(s);
      Serial.print(", _x: ");
      Serial.print(scene[0]);
      Serial.print(", _y: ");
      Serial.print(scene[1]);
      Serial.print(", _z: ");
      Serial.println(scene[2]);
   }
  }
}

void loop() {
}

int availableMemory() 
{
  int size = 1024;
  byte *buf;
  while ((buf = (byte *) malloc(--size)) == NULL);
  free(buf);
  return size;
}





