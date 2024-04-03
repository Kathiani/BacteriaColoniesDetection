import java.awt.*; // needed for rectangle Roi
import ij.*;
import ij.process.*; // needed for ImageProcessor
import ij.gui.*;
import ij.io.*;  // needed for Opener
import ij.plugin.*;  // need for plugin

public class Create_A_Template_ implements PlugIn {
  String Label;
  private GenericRecallableDialog gd;
  int kwidth, kheight, krad, krady;
  Point2D[] particles;  // stores results of particle analysis
  Point2D[] oparticles; // stores particle coords w/r/2 original image
  float[][] origimg;
  float[][] kernel;
  float[][] corrimg;
  ArrayDisplay origAd, corrAd, kernelAd;
  
  public void run(String arg)  {
     if (arg.equals("about"))  {showAbout(); return;}
     origimg = ImageTools.getCurrentImageMatrix(origimg);
     /* Removes the original image window from the window list and dispose of it */
     ImageWindow curImgWindow = WindowManager.getCurrentWindow();
     WindowManager.removeWindow(curImgWindow);
     curImgWindow.setVisible(false);
     curImgWindow.dispose();
     origAd = new ArrayDisplay(origimg,"original image");
     ImageTools.autoSetWindowLevel();
     corrimg = new float[origimg.length][origimg[0].length];
     krad = 7; krady = 7;
     gd = new GenericRecallableDialog("Create Kernel and do Correlation",
     IJ.getInstance());
     gd.addButton("Create Circular Template");
     gd.addScrollBar("disk radius [pixels]", krad, 0, 1, 65); 
     gd.addButton("Invert Template");
     gd.addButton("Crop Template from Image");
     gd.addButton("Load Template from File");
     gd.addButton("Perform Statistical Correlation");
     gd.beginRowOfItems();
     gd.addNumericField("Threshold Min",0.5, 2);
       //  params: label, default value, ndigits
     gd.addNumericField(" Max",1.0, 2);
     gd.endRowOfItems();
     gd.beginRowOfItems();
     gd.addNumericField("Particle Size Min",1, 0);
     gd.addNumericField(" Max",999, 0);
     gd.endRowOfItems();
     gd.addButton("set threshold & get particle analyzer results");
     gd.addButton("Redisplay Overlays");
     gd.showDialog(); 
     
     // arrange windows on screen. Have to move after dialog window is displayed
     Dimension screen = Toolkit.getDefaultToolkit().getScreenSize(); 
     gd.setLocation(screen.width -10 -gd.getSize().width, screen.height/8);
     IJ.getInstance().setLocation(screen.width/4, 2); // move the original ImageJ window
     // move the results window to the side of the main window
     IJ.getTextPanel().getParent().setLocation(screen.width -10- gd.getSize().width, 
                    screen.height/8+ gd.getSize().height); 
     // arrange image window nicely on screen to left of input window and below ImageJ Main
     origAd.setScreenLocation(screen.width -20- gd.getSize().width - origAd.getScreenSize().width, 
             screen.height/8);
//     IJ.wait(500); // give system time to initialize GUI
     while (!(gd.wasCanceled())) { // continuously runs through loop
       if (gd.getFocusOwner() == null) IJ.wait(500); 
       if (gd.getButtonValue(0)) {
         krad = (int)(gd.getScrollBarValue(0));
         kernel = ImageTools.calcCircKernel(krad);
         ImageTools.invertKernel(kernel);
         displayKernel(kernel);
       }
       if (gd.getButtonValue(1)) { // invert kernel
         float kernelMin = ImageTools.min(kernel);
         float kernelMax = ImageTools.max(kernel);
         ImageTools.scale(kernel, kernelMax, kernelMin); // reversing max/min
         ImageTools.normalize(kernel); // important to re-normalize
         displayKernel(kernel);
       }
       if (gd.getButtonValue(2)) {
         kernel = cropForKernel(); 
         displayKernel(kernel);
       }
       if (gd.getButtonValue(3)) { // load kernel as image file
         kernel = loadKernelFromFile();
         displayKernel(kernel);
       }
       if (gd.getButtonValue(4)) { 
          IJ.write("  Computing statistical correlation of image...");
          IJ.showStatus("Computing statistical correlation of image...");
          long startTime = System.currentTimeMillis();
          // get statistical correlation in values from -1.0 to +1.0
          corrimg = ImageTools.statsCorrelation(origimg, kernel);
          long computingTime = (System.currentTimeMillis() - startTime)/1000;
          int minutes = (int)computingTime/60;
          int seconds = (int)(computingTime-(minutes*60));
          IJ.write("   Done computing correlation:\n      processing time "+
                  minutes+ " minutes, "+ seconds +" seconds");
          IJ.showStatus("Done Computing correlation");
          if (corrAd == null) corrAd = new ArrayDisplay(corrimg, "Correlation Image");
          else corrAd.setPixels(corrimg);
               // arrange corr image window nicely on screen below orig image
          corrAd.setScreenLocation(screen.width -20- gd.getSize().width - origAd.getScreenSize().width, 
             screen.height/8 +10+ origAd.getScreenSize().height);
          ImageTools.autoSetWindowLevel();
       }
       if (gd.getButtonValue(5)) { // get particle analyzer results
          double threshMin = gd.getNumericValue(0);
          double threshMax = gd.getNumericValue(1);
          int particleMin = ImageTools.Int(gd.getNumericValue(2)); // rounds it correctly
          int particleMax = ImageTools.Int(gd.getNumericValue(3));
          ImageTools.setThreshold(corrAd, threshMin, threshMax);
          // get x,y, and corr values for all particles
          particles = ImageTools.getResults(corrAd, particleMin, particleMax);
          // each particles element has an x,y, and value attribute: pts[i].x, pts[i].y
        
          // get coords with respect to original image for better display
          oparticles = new Point2D[particles.length];
          for (int i=0; i<particles.length; i++) {
            oparticles[i] = new Point2D(particles[i]); // copies previous particles[i] values
            oparticles[i].x += (float)(kernel.length)/2.0;
            oparticles[i].y += (float)(kernel[0].length)/2.0;
          }
         redisplayOverlays();
       }
       if (gd.getButtonValue(6)) { // redisplay overlays
            redisplayOverlays();
       }
     }    // end while
  }   // end run{}
   
  void showAbout()  {
      IJ.showMessage("About Create A template...",
         "This plug-in filter allows one to create a circular kernel\n" +
         " and to use this to search for similarly sized objects in the image\n");
  } // end showAbout()
  
  private void redisplayOverlays() {
     // display results as overlay onto correlation image
     origAd.clearOverlays();
     origAd.overlayPoints(oparticles, kernel.length /* marker size[pixels] */ );
     // and on to original image
     corrAd.clearOverlays();
     corrAd.overlayPoints(particles, kernel.length );
     // the system often does not complete the drawing of all objects, so force it to
     corrAd.overlayPoints(particles, kernel.length );
  } 

  private void displayKernel(float[][] kernel) {
     // display kernel 
     if (kernelAd != null) kernelAd.destroy(); // clear old window
     kernelAd = new ArrayDisplay(kernel,"Kernel");
     // arrange new window nicely on screen to right of main ImageJ window
     Dimension screen = Toolkit.getDefaultToolkit().getScreenSize(); 
     kernelAd.setScreenLocation(
             screen.width/4 + 10 + IJ.getInstance().getSize().width, 2);
     ImageTools.autoSetWindowLevel();
  }
  
  public float[][] cropForKernel() {
     // get Roi
     Roi curRoi = WindowManager.getCurrentImage().getRoi();
     Rectangle RoiRect = curRoi.getBoundingRect();
     int roiTLx = RoiRect.x;
     int roiTLy = RoiRect.y;
     int roiWidth = RoiRect.width;
     int roiHeight = RoiRect.height;
     if (roiWidth%2==0) roiWidth++;  // make sure it is an odd number
     if (roiHeight%2==0) roiHeight++;
     // grab pixels under Roi
     float[][] newkernel = new float[roiWidth][roiHeight];
     for (int i=0; i<roiWidth; i++)
        for (int j=0; j<roiHeight; j++) 
           newkernel[i][j] = origimg[roiTLx + i][roiTLy + j];
     return newkernel;
  } // end cropForKernel

  public float[][] loadKernelFromFile() {
    // get Gui to find/browse file
    Opener o = new Opener();
    o.open(); 
    ImagePlus newimp = WindowManager.getCurrentImage();
    int width = newimp.getWidth();
    int height = newimp.getHeight();
    // force kernel image to have odd-valued width and height
    if (width%2==0) width--;  
    if (height%2==0) height--;
      
    float[][] newkernel = new float[width][height];
    // Converter changes imp so call this first then work with imp
    ImageProcessor ip = newimp.getProcessor();
    if (ip.isInvertedLut()) ip.invert();
    for (int w=0; w<width; w++) 
      for (int h=0; h<height; h++) 
        newkernel[w][h] = ip.getPixelValue(w,h);    
    //remove that windowmanager
    newimp.getWindow().dispose();
    return (newkernel);
  }

  public String toString() {
     return ("Create Kernel"+Label);
  }
  
} // end class createKernel

