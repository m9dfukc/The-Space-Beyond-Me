
void keyPressed() {
  
  if(key == 's') {
    start_end.flush();
    start_end.close();
    println("saved start and end positions to file");
  }

  if(key == 'j') {
    wheel--;
  } 

  if(key == 'k') {
    println("manual trigger next frame");
    wheel++; 
  }

  if(key == 'n') {
    println("manual trigger next scene");
    triggerNext  = true;
  } 

  if(key == 'u') {
    println("USERMODE TRIGGERT");
    play = !play; 
    if ( play ) {
      frameRate(500);
    } 
    else {
      frameRate(500); 
    }
  }

  // temporäre manuelle reset lösung bis der blaue sensor wieder funktiniert */
  if(key == ' ') {
    initializePositions();
  }
  
  if(key == 'r') {
    resetPositions();
  }

  if(key == '<') {
    println(beginCounter);
    filmDir = -1;
    frameTime = 3; 

    /* emulation sensor event: */
    if (filmDir == -1 || filmBegins) {
      beginCounter++;
      /* blauer muss mindestens 300 mal 'anschlagen' bevor (beginCounter)
       die position resetet wird und er muss mindestens die interpolation 
       zur ersten scene bzw. ersten position volendet haben bevor er neu 
       getriggert/resetet werden kann ('loadStart == true', siehe 'ticksUntilNext == 1').
       */
      if( loadStart && beginCounter > 300) {
        initializePositions();
        loadStart = false;
      }

    }
  }
}





