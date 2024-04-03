// this file contains the classes:
//  public class ArrayDisplay 
//    class ScreenSelectorClass 
//    class ScreenSelectorCanvas extends ImageCanvas implements KeyListener
// 
import java.awt.*;
import java.io.*;  // for PrintWriter
import java.util.*; // for Properties class
import java.awt.image.*;
import java.awt.event.*; // needed for KeyListener
import ij.*;
import ij.gui.*;
import ij.io.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

// display ala 3D image stack
public class ArrayDisplay {
  protected ImagePlus working_display;
  protected ImageProcessor[] working_ip_stack;
  protected Object working_window; // can be either an ImageWindow or a StackWindow
  protected boolean onlyOneImage = false; // false then using stackwindow
  protected ImageStack working_stack;
  protected ImageCanvas working_canvas;
  protected Graphics working_graphics;
  public static int DEPTH = 0, HEIGHT = 1, WIDTH = 2;
  public static int X=0, Y=1;
  protected String Label;
  protected String[] SliceLabels;
  protected int SliderDim, Depth, Height, Width;
  protected float alpha = 0.5f;

  // 3/21/02 assume vals array is indexed as [d][w][h]
  // in base ImageJ stacks do not have properties[]
  public ArrayDisplay(byte[][] vals) {
    this(vals, (String)null, (Properties)null);
  }    
  public ArrayDisplay(byte[][] vals, String label) {
    this(vals, label, (Properties)null);
  }    
  public ArrayDisplay(byte[][] vals, String label, Properties props) {
    Label = new String(label);
    SliceLabels = new String[1];
    SliceLabels[0] = new String(Label);
    SliderDim = DEPTH;
    Width = vals.length;
    Height = vals[0].length;
    Depth = 1;
    onlyOneImage = true;
    byte[][][] vals3d = new byte[1][Width][Height];
    vals3d[0] = vals;
    if (working_stack == null)  initAsByte((Properties)null);
    setPixels(vals3d);
    if (props != null) working_display.setProperty("Info",props.get("Info"));
    working_display.updateAndDraw();
  }
  
  public ArrayDisplay(short[][] vals) {
    this(vals, (String)null, (Properties)null);
  }
  public ArrayDisplay(short[][] vals, String label) {
    this(vals, label, (Properties)null);
  }
  public ArrayDisplay(short[][] vals, String label, Properties props) {
    Label = new String(label);
    SliceLabels = new String[1];
    SliceLabels[0] = new String(Label);
    SliderDim = DEPTH;
    Width = vals.length;
    Height = vals[0].length;
    Depth = 1;
    onlyOneImage = true;
    short[][][] vals3d = new short[1][Width][Height];
    vals3d[0] = vals;
    if (working_stack == null)  initAsShort((Properties)null);
    setPixels(vals3d);
    if (props != null) working_display.setProperty("Info",props.get("Info"));
    working_display.updateAndDraw();
  }

  public ArrayDisplay(float[][] vals) {
   this(vals, (String)null, (Properties)null);
  }
  public ArrayDisplay(float[][] vals, String label) {
    this(vals, label, (Properties)null);
  }    
  public ArrayDisplay(float[][] vals, String label, Properties props) {
    Label = new String(label);
    SliceLabels = new String[1];
    SliceLabels[0] = new String(Label);
    SliderDim = DEPTH;
    Width = vals.length;
    Height = vals[0].length;
    Depth = 1;
    onlyOneImage = true;
    float[][][]vals3d = new float[1][Width][Height];
    for (int w=0; w<Width; w++)
      for (int h=0; h<Height; h++)  vals3d[0][w][h] = vals[w][h];
    if (working_stack == null)  initAsFloat((Properties)null);
    setPixels(vals3d);
    if (props != null) working_display.setProperty("Info",props.get("Info"));
    working_display.updateAndDraw();
  }

  public String toString() {
    return("Array Display: '"+Label+"'   dims "+Width+":"+Height+":"+Depth);
  }
  
  public void Copy(ArrayDisplay A) {
    this.working_display = A.working_display;
    this.working_ip_stack = A.working_ip_stack;
    this.working_window = A.working_window;
    this.onlyOneImage = A.onlyOneImage;
    this.working_stack = A.working_stack;
    this.working_canvas = A.working_canvas;
    this.working_graphics = A.working_graphics;
    this.Label = A.Label;
    this.SliceLabels = A.SliceLabels;
    this.SliderDim = A.SliderDim;
    this.Depth = A.Depth;
    this.Height = A.Height;
    this.Width = A.Width;
  }
  
  public float[][][] getPixels() {
    return(getPixels(new float[1][1][1]));
  }
  public float[][][] getPixels(float[][][] typeFlag) {
    float[][][] vals = new float[Depth][Width][Height];
    if (SliderDim==WIDTH) // put with 2D shown image height x depth, width as slider
      for (int w=1; w<=Width; w++) 
        for (int h=0; h<Height; h++)
          for (int d=0; d<Depth; d++) 
            vals[d][w-1][h] = working_ip_stack[w].getPixelValue(h,d);
    else if (SliderDim==HEIGHT) // put with shown image width x depth, height as slider
      for (int h=1; h<=Height; h++)
        for (int w=0; w<Width; w++) 
          for (int d=0; d<Depth; d++) 
            vals[d][w][h-1] = working_ip_stack[h].getPixelValue(w,d);
    else if (SliderDim==DEPTH) // put with shown image width x height, depth as slider
      for (int d=1; d<=Depth; d++) 
        for (int w=0; w<Width; w++) 
          for (int h=0; h<Height; h++)
            vals[d-1][w][h] = working_ip_stack[d].getPixelValue(w,h);
    return vals;
  }

  public short[][][] getPixels(short[][][] typeFlag) {
    short[][][] vals = new short[Depth][Width][Height];
    if (SliderDim==WIDTH) // put with 2D shown image height x depth, width as slider
      for (int w=1; w<=Width; w++) 
        for (int h=0; h<Height; h++)
          for (int d=0; d<Depth; d++) 
            vals[d][w-1][h] = (short)(working_ip_stack[w].getPixel(h,d));
    else if (SliderDim==HEIGHT) // put with shown image width x depth, height as slider
      for (int h=1; h<=Height; h++)
        for (int w=0; w<Width; w++) 
          for (int d=0; d<Depth; d++) 
            vals[d][w][h-1] = (short)(working_ip_stack[h].getPixel(w,d));
    else if (SliderDim==DEPTH) // put with shown image width x height, depth as slider
      for (int d=1; d<=Depth; d++) 
        for (int w=0; w<Width; w++) 
          for (int h=0; h<Height; h++)
            vals[d-1][w][h] = (short)(working_ip_stack[d].getPixel(w,h));
    return vals;
  }
  public byte[][][] getPixels(byte[][][] typeFlag) {
    byte[][][] vals = new byte[Depth][Width][Height];
    if (SliderDim==WIDTH) // put with 2D shown image height x depth, width as slider
      for (int w=1; w<=Width; w++) 
        for (int h=0; h<Height; h++)
          for (int d=0; d<Depth; d++) 
            vals[d][w-1][h] = (byte)(working_ip_stack[w].getPixel(h,d));
    else if (SliderDim==HEIGHT) // put with shown image width x depth, height as slider
      for (int h=1; h<=Height; h++)
        for (int w=0; w<Width; w++) 
          for (int d=0; d<Depth; d++) 
            vals[d][w][h-1] = (byte)(working_ip_stack[h].getPixel(w,d));
    else if (SliderDim==DEPTH) // put with shown image width x height, depth as slider
      for (int d=1; d<=Depth; d++) 
        for (int w=0; w<Width; w++) 
          for (int h=0; h<Height; h++)
            vals[d-1][w][h] = (byte)(working_ip_stack[d].getPixel(w,h));
    return vals;
  }

  public void setPixels(byte[][][] vals) {
    if (Depth!=vals.length || vals[0].length!=Width || vals[0][0].length!=Height) {
    // resize window  
      Depth=vals.length; Width=vals[0].length;  Height=vals[0][0].length;
      initAsByte(working_display.getProperties());
    }
    if (SliderDim==WIDTH) // put with 2D shown image height x depth, width as slider
      for (int w=1; w<=Width; w++) 
        for (int h=0; h<Height; h++)
          for (int d=0; d<Depth; d++) 
            working_ip_stack[w].putPixel(h,d, vals[d][w-1][h]);
    else if (SliderDim==HEIGHT) // put with shown image width x depth, height as slider
      for (int h=1; h<=Height; h++)
        for (int w=0; w<Width; w++) 
          for (int d=0; d<Depth; d++) 
            working_ip_stack[h].putPixel(w,d, vals[d][w][h-1]);
    else if (SliderDim==DEPTH) // put with shown image width x height, depth as slider
      for (int d=1; d<=Depth; d++) 
        for (int w=0; w<Width; w++) 
          for (int h=0; h<Height; h++)
            working_ip_stack[d].putPixel(w,h, vals[d-1][w][h]);
    working_display.updateAndDraw(); 
  }   
  public void setPixels(short[][][] vals) {
    if (Depth!=vals.length || vals[0].length!=Width || vals[0][0].length!=Height) {
    // resize window  
      Depth=vals.length; Width=vals[0].length;  Height=vals[0][0].length;
      initAsShort(working_display.getProperties());
    }
    if (SliderDim==WIDTH) // put with 2D shown image height x depth, width as slider
      for (int w=1; w<=Width; w++) 
        for (int h=0; h<Height; h++)
          for (int d=0; d<Depth; d++) 
            working_ip_stack[w].putPixel(h,d, vals[d][w-1][h]);
    else if (SliderDim==HEIGHT) // put with shown image width x depth, height as slider
      for (int h=1; h<=Height; h++)
        for (int w=0; w<Width; w++) 
          for (int d=0; d<Depth; d++) 
            working_ip_stack[h].putPixel(w,d, vals[d][w][h-1]);
    else if (SliderDim==DEPTH) // put with shown image width x height, depth as slider
      for (int d=1; d<=Depth; d++) 
        for (int w=0; w<Width; w++) 
          for (int h=0; h<Height; h++)
            working_ip_stack[d].putPixel(w,h, vals[d-1][w][h]);
    working_display.updateAndDraw(); 
  }   
  public void setPixels(float[][][] vals) {
    if (Depth!=vals.length || vals[0].length!=Width || vals[0][0].length!=Height) {
      // resize window  
      Depth=vals.length; Width=vals[0].length;  Height=vals[0][0].length;
      initAsFloat(working_display.getProperties());
    }
    if (SliderDim==WIDTH) // put with 2D shown image height x depth, width as slider
      for (int w=1; w<=Width; w++) 
        for (int h=0; h<Height; h++)
          for (int d=0; d<Depth; d++) 
            working_ip_stack[w].putPixel(h,d, Float.floatToIntBits(vals[d][w-1][h]));
    else if (SliderDim==HEIGHT) // put with shown image width x depth, height as slider
      for (int h=1; h<=Height; h++)
        for (int w=0; w<Width; w++) 
          for (int d=0; d<Depth; d++) 
            working_ip_stack[h].putPixel(w,d, Float.floatToIntBits(vals[d][w][h-1]));
    else if (SliderDim==DEPTH) // put with shown image width x height, depth as slider
      for (int d=1; d<=Depth; d++) 
        for (int w=0; w<Width; w++) 
          for (int h=0; h<Height; h++)
            working_ip_stack[d].putPixel(w,h, Float.floatToIntBits(vals[d-1][w][h]));
    working_display.updateAndDraw(); 
  }    
 
  public void setPixels(byte[][] vals) {
    byte[][][]vals3d = new byte[1][vals.length][vals[0].length];
    vals3d[0] = vals;
    setPixels(vals3d);
  }   
  public void setPixels(short[][] vals) {
    short[][][]vals3d = new short[1][vals.length][vals[0].length];
    vals3d[0] = vals;
    setPixels(vals3d);
  }   
  public void setPixels(float[][] vals) {
    float[][][]vals3d = new float[1][vals.length][vals[0].length];
    vals3d[0] = vals;
    setPixels(vals3d);
  }   
  
  private void initAsByte(Properties props) {
    IJ.showStatus("Initializing 3D byte array display: "+Label);
    if (working_display != null) { // clear previous display
      ((ImageWindow)working_window).dispose();
      working_graphics.dispose();
      System.gc();
    }
   // if (props==null) props = new Properties[MathI.max(Depth, Width, Height)];
    if (SliderDim == WIDTH) {
      working_stack = new ImageStack(Height, Depth); 
      working_ip_stack = new ByteProcessor[Width + 1];
       for (int i=1; i<=Width; i++) {
        working_ip_stack[i] = new ByteProcessor(Height, Depth);
        working_stack.addSlice(SliceLabels[i-1], working_ip_stack[i]); //, props[i-1]);
      }
    } 
    else if (SliderDim == HEIGHT) {
      working_stack = new ImageStack(Width, Depth); 
      working_ip_stack = new ByteProcessor[Height + 1];
      for (int i=1; i<=Height; i++) {
        working_ip_stack[i] = new ByteProcessor(Width, Depth);
        working_stack.addSlice(SliceLabels[i-1], working_ip_stack[i]); //, props[i-1]);
      }
    } 
    else if (SliderDim == DEPTH) {
      working_stack = new ImageStack(Width, Height); 
      working_ip_stack = new ByteProcessor[Depth + 1];
      for (int i=1; i<=Depth; i++) {
        working_ip_stack[i] = new ByteProcessor(Width, Height);
        working_stack.addSlice(SliceLabels[i-1], working_ip_stack[i]); //, props[i-1]);
      }
    }
    if (onlyOneImage == true) 
      working_display = new ImagePlus(Label, working_ip_stack[1]);
    else working_display = new ImagePlus(Label, working_stack);
    if (props != null) 
      working_display.setProperty("Info",props.get("Info"));
    working_canvas = new ImageCanvas(working_display);
    if (onlyOneImage == true) 
      working_window = new ImageWindow(working_display, working_canvas);
    else working_window = new StackWindow(working_display, working_canvas);
    working_graphics = working_canvas.getGraphics();
    // working_canvas.zoomIn(0,0); // make images larger
    IJ.showStatus("");
  }

  private void initAsShort(Properties props) {
    IJ.showStatus("Initializing 3D short array display: "+Label);
    if (working_display != null) { // clear previous display
      ((ImageWindow)working_window).dispose();
      working_graphics.dispose();
      System.gc();
    }
    // if (props==null) props = new Properties[MathI.max(Depth, Width, Height)];
    if (SliderDim == WIDTH) {
      working_stack = new ImageStack(Height, Depth); 
      working_ip_stack = new ShortProcessor[Width + 1];
       for (int i=1; i<=Width; i++) {
        working_ip_stack[i] = new ShortProcessor(Height, Depth);
        working_stack.addSlice(SliceLabels[i-1], working_ip_stack[i]); //, props[i-1]);
      }
    } 
    else if (SliderDim == HEIGHT) {
      working_stack = new ImageStack(Width, Depth); 
      working_ip_stack = new ShortProcessor[Height + 1];
      for (int i=1; i<=Height; i++) {
        working_ip_stack[i] = new ShortProcessor(Width, Depth);
        working_stack.addSlice(SliceLabels[i-1], working_ip_stack[i]); //, props[i-1]);
      }
    } 
    else if (SliderDim == DEPTH) {
      working_stack = new ImageStack(Width, Height); 
      working_ip_stack = new ShortProcessor[Depth + 1];
      for (int i=1; i<=Depth; i++) {
        working_ip_stack[i] = new ShortProcessor(Width, Height);
        working_stack.addSlice(SliceLabels[i-1], working_ip_stack[i]); //, props[i-1]);
      }
    }
    if (onlyOneImage == true) 
      working_display = new ImagePlus(Label, working_ip_stack[1]);
    else working_display = new ImagePlus(Label, working_stack);
    if (props != null) 
      working_display.setProperty("Info",props.get("Info"));
    working_canvas = new ImageCanvas(working_display);
    if (onlyOneImage == true) 
      working_window = new ImageWindow(working_display, working_canvas);
    else working_window = new StackWindow(working_display, working_canvas);
    working_graphics = working_canvas.getGraphics();
    // working_canvas.zoomIn(0,0); // make images larger
     IJ.showStatus("");
  }
  
  private void initAsFloat(Properties props) {
    IJ.showStatus("Initializing 3D array display: "+Label);
    if (working_display != null) { // clear previous display
      ((ImageWindow)working_window).dispose();
      working_graphics.dispose();
      System.gc();
    }
    // if (props==null) props = new Properties[MathI.max(Depth, Width, Height)];
    if (SliderDim == WIDTH) {
      working_stack = new ImageStack(Height, Depth); 
      working_ip_stack = new FloatProcessor[Width + 1];
      for (int i=1; i<=Width; i++) {
        working_ip_stack[i] = new FloatProcessor(Height, Depth);
        working_stack.addSlice(SliceLabels[i-1], working_ip_stack[i]); //, props[i-1]);
      }
    } 
    else if (SliderDim == HEIGHT) {
      working_stack = new ImageStack(Width, Depth); 
      working_ip_stack = new FloatProcessor[Height + 1];
      for (int i=1; i<=Height; i++) {
        working_ip_stack[i] = new FloatProcessor(Width, Depth);
        working_stack.addSlice(SliceLabels[i-1], working_ip_stack[i]); //, props[i-1]);
      }
    } 
    else if (SliderDim == DEPTH) {
      working_stack = new ImageStack(Width, Height); 
      working_ip_stack = new FloatProcessor[Depth + 1];
      for (int i=1; i<=Depth; i++) {
        working_ip_stack[i] = new FloatProcessor(Width, Height);
        working_stack.addSlice(SliceLabels[i-1], working_ip_stack[i]); //, props[i-1]);
      }
    }
    if (onlyOneImage == true) 
      working_display = new ImagePlus(Label, working_ip_stack[1]);
    else working_display = new ImagePlus(Label, working_stack);
    if (props != null) 
      working_display.setProperty("Info",props.get("Info"));
    working_canvas = new ImageCanvas(working_display);
    if (onlyOneImage == true) 
      working_window = new ImageWindow(working_display, working_canvas);
    else working_window = new StackWindow(working_display, working_canvas);
    working_graphics = working_canvas.getGraphics();
    // working_canvas.zoomIn(0,0); // make images larger
    IJ.showStatus("");
  }
  
  public ImageProcessor getImageProcessor() {
    return working_ip_stack[1];
  }
  public ImagePlus getImagePlus() {
    return working_display;
  }
  public ImageStack getImageStack() {
    return working_stack;
  }
  public Dimension getScreenSize() {
    // usage: Dimension.width, .height
    return ((ImageWindow)working_window).getSize();
  }
  public Point getScreenLocation() {
    // usage: Point.x, .y
    return ((ImageWindow)working_window).getLocation();
  }
  public void setScreenLocation(int topleftx, int toplefty) {
    ((ImageWindow)working_window).setLocation(topleftx, toplefty);
  }
  public StackWindow getStackWindow() {
    return ((StackWindow)working_window);
  }
  public ImageProcessor[] getIPStack() {
    return working_ip_stack;
  }
  public Graphics getGraphics() {
    return working_graphics;
  }
  public ImageCanvas getImageCanvas() {
    return working_canvas;
  }
  
  public void invertLUT() {
    for(int i=1; i<working_ip_stack.length; i++)
      working_ip_stack[i].invert();
  }
  
  public int getCurrentSlice() { // indexed 1...N
    return working_display.getCurrentSlice();
  }
  public void setCurrentSlice(int curSlice) { // indexed 1...N
    working_display.setSlice(curSlice);
    ((StackWindow)working_window).showSlice(curSlice);
  }
  
  public Roi getRoi() { // indexed 1...N
    return (working_display.getRoi());
  }
  public void setRoi(Roi roi) {
    working_display.setRoi(roi);
  }
  public void killRoi() {
    working_display.killRoi();
  }

  public void setAlpha(float newAlpha) {
    alpha = newAlpha;  
  } 
  public float getAlpha() {
    return alpha;  
  }
  
  public void clearOverlays() {
    working_display.updateAndDraw();
    IJ.wait(100); // give system time to draw
  }
  
  public void overlayPoints(Point2D[] points, int marksize) {
    for (int i=0; i<points.length; i++) 
      overlayPoint(points[i].x, points[i].y, marksize, "green");
  }
  public void overlayPoints(Point2D[] points, int marksize, String color) {
    for (int i=0; i<points.length; i++) {
      overlayPoint(points[i].x, points[i].y, marksize, color);
      if (i>0) 
        overlayLine(points[i-1], points[i], color);
      else
        overlayLine(points[0], points[points.length-1], color);
    }
  }
  public void overlayPoint(Point2D point, int marksize) {
    overlayPoint(point.x, point.y, marksize, "green");
  }
  public void overlayPoint(Point2D point, int marksize, String color) {
    overlayPoint(point.x, point.y, marksize, color);
  }  
  
  public void overlayPoints(float[] point, int marksize) {
    overlayPoint(point[X], point[Y], marksize, "green");
  }
  public void overlayPoints(float[] point, int marksize, String color) {
    overlayPoint(point[X], point[Y], marksize, color);
  }
  public void overlayPoints(float[][] points, int marksize) {
    for (int i=0; i<points.length; i++) 
      overlayPoint(points[i][X], points[i][Y], marksize, "green");
  }
  public void overlayPoints(float[][] points, int marksize, String color) {
    for (int i=0; i<points.length; i++) 
      overlayPoint(points[i][X], points[i][Y], marksize, color);
  }
  public void overlayPoint(float x, float y, int marksize) {
    overlayPoint(x,y, marksize, "green");
  }
  public void overlayPoint(float x, float y, int marksize, String color) {
    int scrTLx = ImageTools.Int(working_canvas.screenX(ImageTools.Int(x + 0.5f)) -(float)marksize/2f); // shift by half pixel
    int scrTLy = ImageTools.Int(working_canvas.screenY(ImageTools.Int(y + 0.5f)) -(float)marksize/2f);
    working_graphics.setColor(getColor(color));  
    // from the java man pages: "If you draw a figure that covers a given rectangle, 
    //     that figure occupies one extra row of pixels on the right and bottom edges 
    //     as compared to filling a figure that is bounded by that same rectangle."
    // working_graphics.fillOval(scrTLx, scrTLy, marksize, marksize);
    working_graphics.drawRect(scrTLx-1, scrTLy-1, marksize, marksize);
    // force it to draw as it often does not get to the last of a series of drawings
 //   working_graphics.drawRect(scrTLx-1, scrTLy-1, marksize, marksize);
  }

  public void overlayLine(Point2D point1, Point2D point2, String color) {
    overlayLine(point1.x, point1.y, point2.x, point2.y, color);
  }
  public void overlayLines(Point2D[] pts) {
    overlayLines(pts, false /*wrap around*/, "green");
  }
  public void overlayLines(Point2D[] pts, String color) {
    overlayLines(pts, false /*wrap around*/, color);
  }
  public void overlayLines(Point2D[] pts, boolean wrap) {
    overlayLines(pts, wrap, "green");
  }
  public void overlayLines(Point2D[] pts, boolean wrap, String color) {
    int i;
    for (i=0; i<pts.length-1; i++) 
      overlayLine(pts[i].x, pts[i].y, pts[i+1].x, pts[i+1].y, color);
    if (wrap)
      overlayLine(pts[i].x, pts[i].y, pts[0].x, pts[0].y, color);
  }
  public void overlayLines(float[][] pts, boolean wrap) {
    overlayLines(pts, wrap, "green");
  }
  public void overlayLines(float[][] pts, String color) {
    overlayLines(pts, false /*wrap around*/, color);
  }
  public void overlayLines(float[][] pts, boolean wrap, String color) {
    int i;
    for (i=0; i<pts.length-1; i++) 
      overlayLine(pts[i][X], pts[i][Y], pts[i+1][X], pts[i+1][Y], color);
    if (wrap)
      overlayLine(pts[i][X], pts[i][Y], pts[0][X], pts[0][Y], color);
  }
  public void overlayLine(float x1, float y1, float x2, float y2, String color) {
    int scrx1 = ImageTools.Int(working_canvas.screenX(ImageTools.Int(x1 + 0.5f)));
              // shift by half pixel else appears at pixel top left
    int scry1 = ImageTools.Int(working_canvas.screenY(ImageTools.Int(y1 + 0.5f)));
    int scrx2 = ImageTools.Int(working_canvas.screenX(ImageTools.Int(x2 + 0.5f)));
    int scry2 = ImageTools.Int(working_canvas.screenY(ImageTools.Int(y2 + 0.5f)));
    working_graphics.setColor(getColor(color));  
    working_graphics.drawLine(scrx1, scry1, scrx2, scry2);
  }

  // WO 1/5/05 need to either pass in orig or store back-up internally as you curropt the 
  // displayed version each time an overlay is made.  For color displays the backup is known
  // in the R component of the RGB values. 
  public void ImageOverlay(byte[][] origimg, byte[][] img2) {
    ImageOverlay(origimg,img2,0,0);
  }
  public void ImageOverlay(byte[][] origimg, byte[][] img2, int wshift, int hshift) {
    byte[][] vals = new byte[Width][Height];
    for (int w = 0; w < Width; w++) 
      for (int h = 0; h < Height; h++) 
        vals[w][h] = (byte)((float)(origimg[w][h]&0xff)*(1.0f - alpha));
    int width2 = img2.length;
    int height2 = img2[0].length;
    for (int w = 0; w < width2; w++) 
      for (int h = 0; h < height2; h++) 
        // if the shifted portion of image 2 falls onto original image 
        if (w+wshift >= 0 && w+wshift < Width 
            && h+hshift >= 0 && h+hshift < Height) 
          vals[w+wshift][h+hshift] += (byte)((float)(img2[w][h]&0xff)*alpha);
    setPixels(vals);
  }
  public void ImageOverlay(byte[][][] origimg, byte[][][] img2) {
    ImageOverlay(origimg,img2,0,0,0);
  }
  public void ImageOverlay(byte[][][] origimg, byte[][][] img2, int dshift, int wshift, int hshift) {
    byte[][][] vals = new byte[Depth][Width][Height];
    for (int d = 0; d < Depth; d++) 
      for (int w = 0; w < Width; w++) 
        for (int h = 0; h < Height; h++) 
          vals[d][w][h] = (byte)((float)(origimg[d][w][h]&0xff)*(1.0f - alpha));
    int depth2 = img2.length;
    int width2 = img2[0].length;
    int height2 = img2[0][0].length;
    for (int d = 0; d < depth2; d++) 
     for (int w = 0; w < width2; w++) 
      for (int h = 0; h < height2; h++) 
        // if the shifted portion of image 2 falls onto original image 
        if (d+dshift >= 0 && d+dshift < Depth 
            && w+wshift >= 0 && w+wshift < Width 
            && h+hshift >= 0 && h+hshift < Height) 
          vals[d+dshift][w+wshift][h+hshift] += (byte)((float)(img2[d][w][h]&0xff)*alpha);
    setPixels(vals);
  }

  public void ImageOverlay(short[][] origimg, short[][] img2) {
    ImageOverlay(origimg,img2,0,0);
  }
  public void ImageOverlay(short[][] origimg, short[][] img2, int wshift, int hshift) {
    short[][] vals = new short[Width][Height];
    for (int w = 0; w < Width; w++) 
      for (int h = 0; h < Height; h++) 
         vals[w][h] = (short)((float)origimg[w][h]*(1.0f - alpha));
    int width2 = img2.length;
    int height2 = img2[0].length;
    for (int w = 0; w < width2; w++) 
      for (int h = 0; h < height2; h++) 
        // if the shifted portion of image 2 falls onto original image 
        if (w+wshift >= 0 && w+wshift < Width 
            && h+hshift >= 0 && h+hshift < Height) 
          vals[w+wshift][h+hshift] += (short)((float)img2[w][h]*alpha);
    setPixels(vals);
  }
  public void ImageOverlay(short[][][] origimg, short[][][] img2) {
    ImageOverlay(origimg,img2,0,0,0);
  }
  public void ImageOverlay(short[][][] origimg, short[][][] img2, int dshift, int wshift, int hshift) {
    short[][][] vals = new short[Depth][Width][Height];
    for (int d = 0; d < Depth; d++) 
      for (int w = 0; w < Width; w++) 
        for (int h = 0; h < Height; h++) 
          vals[d][w][h] = (short)((float)origimg[d][w][h]*(1.0f - alpha));
    int depth2 = img2.length;
    int width2 = img2[0].length;
    int height2 = img2[0][0].length;
    for (int d = 0; d < depth2; d++) 
     for (int w = 0; w < width2; w++) 
      for (int h = 0; h < height2; h++) 
        // if the shifted portion of image 2 falls onto original image 
        if (d+dshift >= 0 && d+dshift < Depth 
            && w+wshift >= 0 && w+wshift < Width 
            && h+hshift >= 0 && h+hshift < Height) 
          vals[d+dshift][w+wshift][h+hshift] += (short)((float)img2[d][w][h]*alpha);
    setPixels(vals);
  }
  public void ImageOverlay(float[][][] origimg, float[][][] img2) {
    ImageOverlay(origimg,img2,0,0,0);
  }
  public void ImageOverlay(float[][][] origimg, float[][][] img2, int dshift, int wshift, int hshift) {
    short[][][] vals = ImageTools.toShort(origimg);
    ImageTools.scale(vals, 0,1000);
    for (int d = 0; d < Depth; d++) 
      for (int w = 0; w < Width; w++) 
        for (int h = 0; h < Height; h++) 
           vals[d][w][h] = (short)((float)vals[d][w][h]*(1.0f - alpha));
    short[][][] vals2 = ImageTools.toShort(img2);
    ImageTools.scale(vals2, 0,1000);
    int depth2 = img2.length;
    int width2 = img2[0].length;
    int height2 = img2[0][0].length;
    for (int d = 0; d < depth2; d++) 
     for (int w = 0; w < width2; w++) 
      for (int h = 0; h < height2; h++) 
        // if the shifted portion of image 2 falls onto original image 
        if (d+dshift >= 0 && d+dshift < Depth 
            && w+wshift >= 0 && w+wshift < Width 
            && h+hshift >= 0 && h+hshift < Height) 
          vals[d+dshift][w+wshift][h+hshift] += (short)((float)vals2[d][w][h]*alpha);
    setPixels(vals);
  }

  public static Color getColor(String color) {
    if (color.equals("green")) 
      return java.awt.Color.green;  
    if (color.equals("blue")) 
      return java.awt.Color.blue;  
    if (color.equals("red")) 
      return java.awt.Color.red;  
    if (color.equals("black")) 
      return java.awt.Color.black;  
    if (color.equals("yellow")) 
      return java.awt.Color.yellow;  
    if (color.equals("white")) 
      return java.awt.Color.white;  
    if (color.equals("orange")) 
      return java.awt.Color.orange;  
    if (color.equals("cyan")) 
      return java.awt.Color.cyan;  
    if (color.equals("gray")) 
      return java.awt.Color.gray;  
    if (color.equals("magenta")) 
      return java.awt.Color.magenta;  
    if (color.equals("pink")) 
      return java.awt.Color.pink;  
    if (color.equals("lightgray")) 
      return java.awt.Color.lightGray;  
    // else
    return java.awt.Color.orange;
  }
  
  public void destroy() {  // remove the window and clear arrays
    working_ip_stack = null;
    working_stack = null;
    working_display = null;
    working_canvas = null;
    working_graphics = null;
    /* Removes image window from the window list and dispose */
    WindowManager.removeWindow((ImageWindow)working_window);
    ((ImageWindow)working_window).setVisible(false);
    ((ImageWindow)working_window).dispose();
    System.gc();
  } 
    
} // end class ArrayDisplay

class ScreenSelectorClass {
	public void UpdateSelection(int x, int y, String s) { return; }
	public void UpdateScreen() { return; }
}

class ScreenSelectorCanvas extends ImageCanvas implements KeyListener{
   ScreenSelectorClass local_selector_class;
   
   public ScreenSelectorCanvas(ImagePlus imp) {
      super(imp);
      addKeyListener(this);
   }
   public ScreenSelectorCanvas(ImagePlus imp, ScreenSelectorClass fpc) {
      super(imp);
      setSelectorClass(fpc);
      addKeyListener(this);
   }
   public void setSelectorClass( ScreenSelectorClass fpc ) {
      local_selector_class = fpc;
   }
   public void update( Graphics g ) {
      super.update(g);
      if (local_selector_class != null)
           local_selector_class.UpdateScreen();
   }

   public void keyPressed(KeyEvent e) {
      int keyCode = e.getKeyCode();
      IJ.setKeyDown(keyCode);
      int flags = e.getModifiers();
      // be aware of left and right arrow keys for input
      if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_NUMPAD4) 
        local_selector_class.UpdateSelection(0,0,"Left Arrow");
      if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_NUMPAD6)
        local_selector_class.UpdateSelection(0,0,"Right Arrow");
      if (keyCode == KeyEvent.VK_S && 
               ( (flags&Event.ALT_MASK) != 0 || (flags&Event.CTRL_MASK) != 0 ))
        local_selector_class.UpdateSelection(0,0,"Cntl S");
   }
   public void keyTyped(KeyEvent e) {};
   public void keyReleased(KeyEvent e) {};

   public void mousePressed(MouseEvent e) {
      // need some features of mouse action from super(), like zoom,
      // but not the WAND/CROSSHAIR action
      if (Toolbar.getToolId()==Toolbar.WAND || 
            Toolbar.getToolId()==Toolbar.CROSSHAIR) {
        int x = e.getX(); // current screen coords (not image pixels)
        int y = e.getY();
        Toolbar.getInstance().setTool(Toolbar.CROSSHAIR);
        if (local_selector_class != null) {
           int flags = e.getModifiers();
           if ( (flags&MouseEvent.BUTTON1_MASK) != 0) { // left mouse button
              if ( (flags&Event.ALT_MASK) != 0) { // alt button down
                 local_selector_class.UpdateSelection(x,y,"Alt Left Mouse");
              }
              else if ( (flags&Event.SHIFT_MASK) != 0) { // shift button down
                 local_selector_class.UpdateSelection(x,y,"Shift Left Mouse");
              }
              else
                  local_selector_class.UpdateSelection(x,y,"Left Mouse");
             }
           // there is no middle button for PC's
           if ( (flags&MouseEvent.BUTTON3_MASK) != 0)  // right mouse button
              local_selector_class.UpdateSelection(x,y,"Right Mouse");
        }
      }
      else 
         super.mousePressed(e);
   }
   public void mouseDragged(MouseEvent e) {
      if (Toolbar.getToolId()==Toolbar.WAND || 
         Toolbar.getToolId()==Toolbar.CROSSHAIR) 
        mousePressed(e); 
      else super.mouseDragged(e);
   }

   public void mouseReleased(MouseEvent e) {
      if (Toolbar.getToolId()==Toolbar.WAND || 
            Toolbar.getToolId()==Toolbar.CROSSHAIR) {
        if (local_selector_class != null) {
           int flags = e.getModifiers();
           if ( (flags&MouseEvent.BUTTON1_MASK) != 0) // left mouse button
           if ( (flags&Event.ALT_MASK) == 0)  // not alt button also
              local_selector_class.UpdateSelection(0,0,"Left Mouse Up");
         }
      }
      else 
         super.mouseReleased(e);
      update(getGraphics());
   }
   
   // want if keyboard arrow keys pressed go to next slice
}