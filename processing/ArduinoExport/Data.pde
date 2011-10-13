
class FPoint {
  public float x = 0.0;
  public float y = 0.0;
  public float z = 0.0; 
  public float drawZ = 0.0;

  FPoint() { 
  }

  FPoint(float _x, float _y, float _z) {
    x = _x;
    y = _y;
    z = _z; 
  }
  
  FPoint(float _x, float _y, float _z, float _drawZ) {
    x = _x;
    y = _y;
    z = _z; 
    drawZ = _drawZ;
  }
}
class Data 
{
  public PFont fnt;

  /* steps == auflösung der 'controller app' bühne (*10 für höhere auflösung) */
  public int stepsWidth  = 4140*10;
  public int stepsHeight = 720*10;
  public int stepsDepth  = 940;
  public int scenePos = -1;

  private ArrayList pos;
  private ArrayList posDraw;
  private ArrayList scenes;
  private ArrayList images;

  private int sceneCount = 0;
  private int framesCount = 0;
  private int framesPos = -1;
  private int imagesCount = 0;
  private int imagesPos = -1;

  private double interPosX = 0.0;
  private double interPosY = 0.0;
  private double interPosZ = 0.0;

  Data() 
  {
    fnt = loadFont("Helvetica-10.vlw");
    textFont(fnt, 10); 
    fill(0);
  
    File path = sketchFile("data");
    try {
      File[] files = path.listFiles();

      scenes = new ArrayList();
      pos    = new ArrayList();
      images = new ArrayList();

      for (int i=0;i<files.length;i++)
      {
        if (files[i].isDirectory() && files[i].getName().startsWith("scene")) {
          sceneCount++;
          scenes.add((String) files[i].getName());
        } 
      }
      scenes.trimToSize();
    } 
    catch(Exception e) {
      println("data folder not available!");
      exit();
    }
  }


  /* load next scene */
  int nextScene() 
  {
    stroke(random(255), random(255), random(255));
    
    if (sceneCount - 1 > scenePos ) {

      scenePos++;
      framesPos = -1;
      framesCount = 0;
      pos.clear();
      images.clear();
      
      /* get actual scene name */
      String sceneName = (String) scenes.get(scenePos);
      //println(sceneName);

      /* load position data for the scene */
      String lines[] = loadStrings("data/"+sceneName+"/"+sceneName+".txt");
      
      try {
        output.flush(); // Write the remaining data
        output.close(); // Finish the file
        
      } catch(Exception e) {
        
      }
      output = createWriter("data/export/"+sceneName+".txt");
      
      for (int i=0; i < lines.length; i++) 
      {
        //println(lines[i]);
        String[] lpiece = split(lines[i], ' ');
        if (lpiece.length >= 5) {
          framesCount++;
          
          /*die Formel bei steps_z bassiert auf einerm Wertebereich von x von [2, 19.5] y [0,940], deshalb muessen die Werte aus der txt auf diesen Bereich linear gemappt werden*/
          float zoom = float(lpiece[4]);
          //zoom = zoom - 0.05;
          float z_map = map(zoom, 0.11, 1.0, 2.0, 19.5);
          float steps_z = (-212.633 + 117.681 * z_map - 6.14326 * pow(z_map, 2.0) + 0.238081 * pow(z_map, 3.0) - 0.00399534 * pow(z_map, 4.0));
          
          //TEST y wert wird skalier
          float x_scale = float(lpiece[2]);
          float y_scale = float(lpiece[3]);
          
          int _tx = (int) constrain(x_scale, 0, stepsWidth);
          int _ty = (int) constrain(y_scale, 0, stepsHeight);
          int _tz = (int) constrain(steps_z, 0, stepsDepth);
          
          FPoint actualPos = new FPoint(float(_tx), float(_ty), float(_tz), zoom);
          pos.add((FPoint) actualPos);
        }
      }
      
      pos.trimToSize();

    } else {
     try {
        output.flush(); // Write the remaining data
        output.close(); // Finish the file
        
      } catch(Exception e) {
        
      } 
    }
    
    return scenePos;
  }

  /* reset go to the beginning */
  void resetScenes() 
  {
    scenePos = -1;
    framesPos = -1;
    framesCount = 0; 

    //resetFrames();
  }

  /* load next frame */
  int nextFrame() 
  {
    if (framesCount - 1 > framesPos) {
      framesPos++;
      // print(framesCount); print(" "); println(framesPos);
    }
    return framesPos;
  }

  void resetFrames() 
  {
    framesPos = -1;
  }

  int getNumFrames() 
  {
    return this.framesCount;  
  }

  int getNumScenes() 
  {
    return this.sceneCount;  
  }

  FPoint getPosition(int frame) 
  {
    if ( frame == -1) frame = 0;
    if ( frame < framesCount) return (FPoint) pos.get(frame);
    else {
      if (framesCount - 1 > 0) return (FPoint) pos.get(framesCount - 1);
      else return new FPoint(0.0, 0.0, 0.0);
    }
  }

  FPoint getVeryLastPosition()
  {
    String sceneName = (String) scenes.get((scenes.size()-1));
    /* load the very last scene.txt */
    String lines[] = loadStrings("data/"+sceneName+"/"+sceneName+".txt");
    
    for (int i=lines.length-1; i > 0 ; i--) 
    {
      String[] lpiece = split(lines[i], ' ');
      if (lpiece.length >= 5) {
        float zoom = float(lpiece[4]);
        float z_map = map(zoom, 0.11, 1.0, 2.0, 19.5);
        float steps_z = (-212.633 + 117.681 * z_map - 6.14326 * pow(z_map, 2.0) + 0.238081 * pow(z_map, 3.0) - 0.00399534 * pow(z_map, 4.0));
        
        float x_scale = float(lpiece[2]);
        float y_scale = float(lpiece[3]);
        
        int _tx = (int) constrain(x_scale, 0, stepsWidth);
        int _ty = (int) constrain(y_scale, 0, stepsHeight);
        int _tz = (int) constrain(steps_z, 0, stepsDepth);
        
        return new FPoint(float(_tx), float(_ty), float(_tz), zoom);
      }
    }
    
    return new FPoint(0.0, 0.0, 0.0);
  }
  
    FPoint getVeryFirstPosition()
  {
    String sceneName = (String) scenes.get(0);
    /* load the very last scene.txt */
    String lines[] = loadStrings("data/"+sceneName+"/"+sceneName+".txt");
    
    for (int i = 0; i < lines.length; i++) 
    {
      String[] lpiece = split(lines[i], ' ');
      if (lpiece.length >= 5) {
        float zoom = float(lpiece[4]);
        float z_map = map(zoom, 0.11, 1.0, 2.0, 19.5);
        float steps_z = (-212.633 + 117.681 * z_map - 6.14326 * pow(z_map, 2.0) + 0.238081 * pow(z_map, 3.0) - 0.00399534 * pow(z_map, 4.0));
        
        float x_scale = float(lpiece[2]);
        float y_scale = float(lpiece[3]);
        
        int _tx = (int) constrain(x_scale, 0, stepsWidth);
        int _ty = (int) constrain(y_scale, 0, stepsHeight);
        int _tz = (int) constrain(steps_z, 0, stepsDepth);
        
        return new FPoint(float(_tx), float(_ty), float(_tz), zoom);
      }
    }
    
    return new FPoint(0.0, 0.0, 0.0);
  }

  FPoint interpolate(FPoint fromPosition, FPoint toPosition, int ticksToTriggerNext, int ticksUntilNext) 
  {
    float mu = 1.0 - float(ticksUntilNext) / float(ticksToTriggerNext);
    //println(mu); 

    int tx = round(CosineInterpolate(fromPosition.x, toPosition.x, mu));
    int ty = round(CosineInterpolate(fromPosition.y, toPosition.y, mu));
    int tz = round(CosineInterpolate(fromPosition.z, toPosition.z, mu));

    return new FPoint (float(tx), float(ty), float(tz));
  }

  float CosineInterpolate(float y1, float y2, float mu) {
    float mu2;
    mu2 = (1.0-cos(mu*PI))/2.0;
    return(y1*(1.0-mu2)+y2*mu2);
  }

} 




















