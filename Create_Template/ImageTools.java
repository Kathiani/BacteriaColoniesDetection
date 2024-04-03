// this file contains the classes:
//   public class ImageTools
//

import java.awt.*;
import java.util.*; // for Properties class
import ij.*;
import ij.gui.*;
import ij.io.*;
import ij.process.*;
import ij.measure.*;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.*;  // need for contrastAdjuster


// common image processing routines
public class ImageTools {

  /** the default (int) function simply truncates any decimal 
    * points: (int)(1.9) = 1.  This function rounds to nearest int */
  public static int Int(float x) { 
    int isign = 1;
    if (x<0f) { isign = -1; x = -x; } 
    return(isign*(int)(x+0.5f)); 
  }
  public static int Int(double x) { 
    int isign = 1;
    if (x<0.0) { isign = -1; x = -x; } 
    return(isign*(int)(x+0.5)); 
  }

  /** math does not have float versions */
  public static double exp(double x) {
    return Math.exp(x);
  }
  public static float exp(float x) {
    return (float)(Math.exp((double)x));
  }
 
  public static float sqr( float x ) {
     return(x*x);
  }
 
  public static double sqrt( double x ) {
     return(Math.sqrt(x));
  }
  public static float sqrt( float x ) {
     return (float)(Math.sqrt((double)x));
  }
  
  public static float asin(float theta) {
     return (float)(Math.asin((double)theta));
  }

  public static float avg(float[][] A) {
     return avg(A, 0,0, A.length, A[0].length);
  }
  public static float avg(float[][] A, int sx, int sy, int width, int height) {
     if ((sx+width>A.length) || (sy+height>A[0].length)) {
       IJ.write("  Error in Arrays.avg() 2d: out of array bounds");
       return -1f;
     }
     float sum = 0.0f;
     for (int i=sx; i<sx+width; i++) 
       for (int j=sy; j<sy+height; j++) 
         sum += A[i][j];
     sum /= (float)(width*height);
     return sum;
  }
  
  public static int max(short[][] A) {
     return max(A, A.length, A[0].length);
  }
  public static int max(short[][] A, int n, int m) {
     int val = -Integer.MAX_VALUE;
     for (int i=0; i<n; i++) 
       for (int j=0; j<m; j++) 
         if ((int)A[i][j] > val) val = (int)A[i][j];
     return val;
  }
  public static int max(short[][][] A) {
     return max(A, A.length, A[0].length, A[0][0].length);
  }
  public static int max(short[][][] A, int n, int m, int l) {
     int val = -Integer.MAX_VALUE;
     for (int i=0; i<n; i++) 
       for (int j=0; j<m; j++) 
         for (int k=0; k<l; k++) 
           if ((int)A[i][j][k] > val) val = (int)A[i][j][k];
     return val;
  }

  public static float max(float[][] A) {
    return max(A, A.length, A[0].length);
  }
  public static float max(float[][] A, int n, int m) {
     float val = -Float.MAX_VALUE;
     for (int i=0; i<n; i++) 
       for (int j=0; j<m; j++) 
         if (A[i][j] > val) val = A[i][j];
     return val;
  }

  public static short min(short[][] A) {
     short val = Short.MAX_VALUE;
     for (int i=0; i<A.length; i++) 
        for (int j=0; j<A[0].length; j++) 
           if (A[i][j] < val) val = A[i][j];
     return val;
  }
  public static int min(short[][][] A) {
     short val = Short.MAX_VALUE;
     for (int i=0; i<A.length; i++) 
       for (int j=0; j<A[0].length; j++) 
         for (int k=0; k<A[0][0].length; k++) 
           if (A[i][j][k] < val) val = A[i][j][k];
    return val;
  }
  public static float min(float[][] A) {
     float val = Float.MAX_VALUE;
     for (int i=0; i<A.length; i++) 
       for (int j=0; j<A[0].length; j++) 
         if (A[i][j] < val) val = A[i][j];
     return val;
  }
   
   public static byte[][] toByte(float[][] A) {
      int n = A.length;
      int m = A[0].length;
      byte[][] byteA = new byte[n][m];
      for (int i=0; i<n; i++) 
        for (int j=0; j<m; j++) 
          byteA[i][j] = (byte)(Int(A[i][j]));
      return byteA;
   }

    public static short[][] toShort(float[][] A) {
      int n = A.length;
      int m = A[0].length;
      short[][] shortA = new short[n][m];
      for (int i=0; i<n; i++) 
        for (int j=0; j<m; j++) 
          shortA[i][j] = (short)Int(A[i][j]);
      return shortA;
    }
    public static short[][][] toShort(float[][][] A) {
      int n = A.length;
      int m = A[0].length;
      int p = A[0][0].length;
      short[][][] shortA = new short[n][m][p];
      for (int i=0; i<n; i++) 
        for (int j=0; j<m; j++) 
          for (int k=0; k<p; k++) 
            shortA[i][j][k] =  (short)Int(A[i][j][k]);
      return shortA;
    }

   public static void scale(short[][] A) {
      scale(A, 0, 255);
    }
    public static void scale(short[][] A, int minval, int maxval) {
      int n = A.length;
      int m = A[0].length;
      float Amin = (float) min(A);
      float Amax = (float) max(A);
      float scale = (float)(maxval-minval)/(Amax-Amin);
      for (int i=0; i<n; i++) 
        for (int j=0; j<m; j++) 
          A[i][j] = (short)(minval + (((float)A[i][j] - Amin)*scale));
    }
    public static void scale(short[][][] A) {
      scale(A, 0, 255);
    }
    public static void scale(short[][][] A, int minval, int maxval) {
      int depth = A.length;
      int width = A[0].length;
      int height = A[0][0].length;
      float Amin = (float) min(A);
      float Amax = (float) max(A);
      float scale = (float)(maxval-minval)/(Amax-Amin);
      for (int d=0; d<depth; d++) 
        for (int w=0; w<width; w++) 
          for (int h=0; h<height; h++) 
             A[d][w][h] = (short)(minval + (((float)A[d][w][h] - Amin)*scale));
    }
 
  public static void scale(float[][] A) {
     scale(A, 0, 255);
  }
  public static void scale(float[][] A, float min, float max) {
     int n = A.length;
     int m = A[0].length;
     float Amin = min(A);
     float Amax = max(A);
     float scale = (max-min)/(Amax-Amin);
     for (int i=0; i<n; i++) 
       for (int j=0; j<m; j++) 
          A[i][j] = min + (A[i][j] - Amin)*scale;
  }

  // normalizs an arrays values (e.g. for a kernel)
  // does not do vector normalization
  public static void normalize(float[][] A) {
     int width = A.length;
     int height = A[0].length;
     float norm = avg(A)*(float)(width*height);
     for (int i=0; i<width; i++) 
       for (int j=0; j<height; j++) 
         A[i][j] /= norm;
  }

  // linear regression: y = ax + b, assume x=0,1,2,...,N-1, returns values[2]
  public static float linRegrSlope(float[] A) {
    int n = A.length;
    float sumx = 0f, avgx, delsqrx = 0f, sumy = 0f, a = 0f, b;
    for (int i=0; i<n; i++) { sumy += A[i];  sumx += i; }
      avgx = sumx/(float)n;
    for (int i=0; i<n; i++) delsqrx += ((float)i - avgx)*((float)i - avgx);
    for (int i=0; i<n; i++) a += A[i]*((float)i - avgx)/delsqrx;
    b = (sumy - a*sumx)/(float)n;
    return a;
  }
    
  public static float[][] gradient(float[][] A) {
    return(gradient(A, new Rectangle(0,0,A.length,A[0].length)));
  }
  public static float[][] gradient(float[][] A, Rectangle roi) {
    int n = A.length;
    int m = A[0].length;
    float[][] gradimg = new float[n][m];
    float[] xtestarray, ytestarray;
    float ygrad, xgrad;
    int i,j,k,t, ii,jj;
    xtestarray = new float[3];
    ytestarray = new float[3];
    for (i=roi.x; i<roi.x+roi.width; i++) 
      for (j=roi.y; j<roi.y+roi.height; j++) {
        for (t=0, k=-1; k<=1; k++, t++) {
          ii = i+k;  jj = j+k; 
          if (ii<0) ii = 0;
          if (ii==n) ii= n-1;
          if (jj<0) jj = 0;
          if (jj==m) jj= m-1;
          xtestarray[t] = A[ii][j];
          ytestarray[t] = A[i][jj];
        }
        xgrad = linRegrSlope(xtestarray);
        ygrad = linRegrSlope(ytestarray);
        gradimg[i][j] = sqrt(ygrad*ygrad + xgrad*xgrad); 
      }    
    return gradimg;
  }
   
     // 2D convolution
   // assumes kernel is already scaled(normalized) properly
   // WO 10/15/02 crop out the ring of undefined pixel values at the 
   // periphery of the image
   public static float[][] convolve(float[][] pixels, float[][] kernel) {
      int kw = kernel.length;
      int kh = kernel[0].length;
      int width = pixels.length;
      int height = pixels[0].length; // this gives correct width and height
      int uc = kw/2;    
      int vc = kh/2;
      float[][] pixels2 = new float[width-2*uc][height-2*vc];
      float sum;
      for(int x=uc; x<width-uc; x++)
         for(int y=vc; y<height-vc; y++) {
          sum = 0f;
            for(int u=-uc; u <= uc; u++) 
               for(int v=-vc; v <= vc; v++) 
                sum += pixels[x+u][y+v] * kernel[uc-u][vc-v];
            pixels2[x-uc][y-vc] = sum;
         }
      return pixels2;
  }

   public static float[][] filterGaussian(float[][] A) {
     // get 3x3 gaussian kernel for filter 
      float[][] gaussianKernel = gaussianKernel(3,3);
      float[][] filteredA = convolve(A,gaussianKernel);
      return filteredA;
   }
   
      
   // the statistical correlation coefficient is similar to image correlation, but
   // uses a few more comptuations and is better at finding image features
   // returns correlation image as 2D array
   // indeices arrays as [depth][width][height]
   public static float[][] statsCorrelation(float[][] pixels, float[][] kernel) {
      int kw = kernel.length;
      int kh = kernel[0].length;
      float[][][][] kernels3D = new float[1][1][kw][kh]; 
      kernels3D[0][0] = kernel;
      int width = pixels.length;
      int height = pixels[0].length; 
      float[][][] pixels3D = new float[1][width][height];
      pixels3D[0] = pixels;
      float[][][][] corr3D = statsCorrelation(pixels3D, kernels3D);
      return corr3D[0][0];
   }
  // PW, 04-01-2002 3D statistical correlation
  // WO 4/24/02 added ability to do multiple 3D kernels at once
  // also, knock off the blank edge image elements (slices, columns and rows), thus
  //  resulting corrImage is smaller than original image
  public static float[][][][] statsCorrelation(float[][][] pixels, float[][][][] kernels) {
      int nkernels = kernels.length;
      int kd = kernels[0].length; // kernel dims
      int kw = kernels[0][0].length;
      int kh = kernels[0][0][0].length;
      int depth = pixels.length;  // image dims
      int width = pixels[0].length; 
      int height = pixels[0][0].length;
      int ew = kw/2;  // width of edge (no corr value) 
      int eh = kh/2;
      int ed = kd/2;
      int cd = depth-2*ed; // corr image dims (removing edges)
      int cw = width-2*ew;
      int ch = height-2*eh;
      float[][][][] corrImg = new float[nkernels][cd][cw][ch];
      float Sxx, localAvg, corr;
      float Sxy[] = new float[nkernels]; // cross correlation
      float Syy[] = new float[nkernels]; // self corr of each kernel
      float kernelAvg[] = new float[nkernels];
            
      for (int n=0; n<nkernels; n++) {
        kernelAvg[n] = Syy[n] = 0f; // self correlation of kernel
        for (int i=0; i<kw; i++)
          for (int j=0; j<kh; j++)
            for (int k=0; k<kd; k++)
              kernelAvg[n] += kernels[n][k][i][j]; 
        kernelAvg[n] /= (float)(kw*kh*kd);
          for (int i=0; i<kw; i++)
          for (int j=0; j<kh; j++)
            for(int k=0; k<kd; k++)
              Syy[n] += sqr(kernels[n][k][i][j] - kernelAvg[n]); 
      }
      for(int x=ew; x<width-ew; x++)
        for(int y=eh; y<height-eh; y++) 
          for(int z=ed; z<depth-ed; z++) { // loop over all pixels in image
            localAvg = 0f; // average of image pixels covered by kernel
             for(int dd=-ed; dd <= ed; dd++)
               for(int ww=-ew; ww <= ew; ww++) 
                 for(int hh=-eh; hh <= eh; hh++) 
                   localAvg += (float)(pixels[z+dd][x+ww][y+hh]);
            localAvg /= (float)(kd*kw*kh);
            Sxx = 0f; // self corr of image
             for(int dd=-ed; dd <= ed; dd++)
               for(int ww=-ew; ww <= ew; ww++) 
                 for(int hh=-eh; hh <= eh; hh++) 
                   Sxx += sqr((float)(pixels[z+dd][x+ww][y+hh]) - localAvg);

            for (int n=0; n<nkernels; n++) {
              Sxy[n] = 0f; // cross corr of image with kernel
               for(int dd=-ed; dd <= ed; dd++)
                 for(int ww=-ew; ww <= ew; ww++) 
                   for(int hh=-eh; hh <= eh; hh++) 
                     Sxy[n] += ((float)(pixels[z+dd][x+ww][y+hh]) - localAvg)*
                          (kernels[n][dd+ed][ww+ew][hh+eh] - kernelAvg[n]);
             if (Sxy[n] == 0f) corr = 0f; // if both Sxy and Sxx == 0, then gives NAN
               else corr = Sxy[n]/sqrt(Sxx*Syy[n]); 
               // correlation values range from -1 ... +1
             corrImg[n][z-ed][x-ew][y-eh] = corr;
            } // end nkernel loop
          } // end x,y loops
      return corrImg;
    } // end 3D byte statsCorrelation of multiple kernels

  public static float[][] getCurrentImageMatrix(float[][] gettypeofA) {
     boolean useRoi = false; // defualt if nothing selected
     return getCurrentImageMatrix(gettypeofA, useRoi);
  }
  public static float[][] getCurrentImageMatrix(float[][] gettypeofA, boolean useRoi) {
     int ImgWidth, ImgHeight, ImgTLx, ImgTLy; // bounds of image being retrieved
     ImagePlus imp = WindowManager.getCurrentImage();
     if (imp==null) {IJ.noImage(); return null;}
     Roi roi = imp.getRoi();
     if (useRoi && roi!=null) { // get just the Roi
       Rectangle roiRect;
       roiRect = roi.getBoundingRect();
       ImgWidth = roiRect.width;
       ImgHeight = roiRect.height;
         ImgTLx = roiRect.x;   ImgTLy = roiRect.y;
      }
      else { // get whole image
       ImgWidth = imp.getWidth(); 
       ImgHeight = imp.getHeight(); 
         ImgTLx = 0;   ImgTLy = 0;
      }
      ImageProcessor ip = imp.getProcessor();
      float[][] A = new float[ImgWidth][ImgHeight];
      for(int w=0; w<ImgWidth; w++)
        for(int h=0; h<ImgHeight; h++) 
          A[w][h] = (float)ip.getPixel(w+ImgTLx, h+ImgTLy);
    return A;
  } 
 
   // get particle coordinates from particle analyzer routine
      // /mysource/math_dir/Point2D.java
   // note how one can use the same function name repeatedly since a function 
   // is identified not only by its name but also its parameter list.  Thus, 
   // you can use getResults() with either just ArrayDisplay as input or an 
   // with additional parameters.
   public static Point2D[] getResults(ArrayDisplay curAD, int minSize, int maxSize) {
      ImagePlus curImp = curAD.getImagePlus();
      ResultsTable rt = new ResultsTable();
      rt.setPrecision(2 /* int precision */ );
      int options = ParticleAnalyzer.SHOW_RESULTS; // + ParticleAnalyzer.SHOW_OUTLINES;
      int measurements = Measurements.CENTROID + Measurements.MIN_MAX + Measurements.AREA;
         //  ij/measure/Measurements.java
      ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, rt, minSize, maxSize);
         //  ij/plugin/filter/ParticleAnalyzer.java
      pa.analyze(curImp);

      float[] xCoords = rt.getColumn(rt.X_CENTROID);
      float[] yCoords = rt.getColumn(rt.Y_CENTROID);
      float[] maxValues = rt.getColumn(rt.MAX);
      float[] minValues = rt.getColumn(rt.MIN);
      int nParticles = xCoords.length;
      Point2D[] pts = new Point2D[nParticles];
      // if correlation valeus are positive, store maximum value
      // if negative, store absolute value of minimum value (largest megative)
      if (maxValues[0] > 0f) 
         for (int i=0; i<nParticles; i++)
            pts[i] = new Point2D(xCoords[i], yCoords[i], maxValues[i]);
      else 
         for (int i=0; i<nParticles; i++)
            pts[i] = new Point2D(xCoords[i], yCoords[i], -minValues[i]);
            
      return pts;
   }
        
        // elliptical gaussian kernel
   public static float[][] gaussianKernel(int nx, int ny) {
      if (nx%2 ==0 || ny%2 ==0) {
        IJ.write("  error in gaussianKernal(2D): must have odd-valued dimensions");
        nx++; ny++; // give a value that will work
      }
      float sigmax = (float)(nx-1) / 6f;  //standard deviation of gaussian filter
      float sigmay = (float)(ny-1) / 6f; 
      return gaussianKernel(sigmax, sigmay);
   } 
   public static float[][] gaussianKernel(float sigma) {
      return (float[][]) gaussianKernel(sigma, sigma);
   } 
   public static float[][] gaussianKernel(float sigmax, float sigmay) {
      int nx = Int(6f*sigmax); // pixel dimension to enclose 6 sigma on each end
      int ny = Int(6f*sigmay);
      if (nx%2 ==0) nx += 1;  // must have odd number
      if (ny%2 ==0) ny += 1;
      float[][] gauss_kernel = new float[nx][ny];
      float locx, locy, scale = 0f;
      int i,j;
      // define nx x ny kernel
      // fix problem of 0/0 when sigma==0 (don't use Float.MIN_VALUE since ^2=0)
      if (sigmax == 0) sigmax = 0.00001f;
      if (sigmay == 0) sigmay = 0.00001f;
      for (i=0; i<nx; i++) {
         locx = (float) (i- ((nx-1)/2));
         for (j=0; j<ny; j++) {
            locy = (float) (j- ((ny-1)/2));
            gauss_kernel[i][j] = (float)(exp
               (-0.5f*(locx*locx/(sigmax*sigmax) +locy*locy/(sigmay*sigmay))));
            scale += gauss_kernel[i][j];
         }
      }
      for (i=0; i<nx; i++) // ensure integral == 1.0
        for (j=0; j<ny; j++)  
            gauss_kernel[i][j] /= scale;
      return gauss_kernel;
   } 

      
   // WO 5/9/02 new arc integral calc versions .. gives a more 
   // realistic digitized circle with partial volumes are edges
  static public float[][] calcCircKernel(int krad) {
      int kwidth, kheight;
      kwidth = kheight = krad*2 + 5; // +5 to give big margin area
      float[][] newkernel = new float[kwidth][kheight];
      float norm = 0f;
       for (int j=-krad; j<=krad; j++) 
         for (int i=-krad; i<=krad; i++) {
            newkernel[i+kwidth/2][j+kheight/2] = 
               computePixelArcArea((float)i -0.5f, (float)i +0.5f,
                    (float)j -0.5f, (float)j + 0.5f, (float)krad );
            norm += newkernel[i+kwidth/2][j+kheight/2];
        }
       for (int j=-krad; j<=krad; j++) 
         for (int i=-krad; i<=krad; i++) 
            newkernel[i+kwidth/2][j+kheight/2] /= norm;
        return newkernel;
  } // end calCircKernel
        
  // assumes original kernel min==0
  static public void invertKernel(float[][] A) {
     int width = A.length;
     int height = A[0].length;
     for (int w=0; w<width; w++) 
       for (int h=0; h<height; h++) 
          A[w][h] *= -1f;
     scale(A); // scale it 0..255
     normalize(A);
  }
  
 // WO 5/9/02 area under an arc: arc eqn: y^2 = sqrt(R^2 - x^2)
   // area = integral{ sqrt(R^2 - x^2)  dx}
   public static float areaIntegral(float x, float R) {
          return(0.5f*x*sqrt(R*R-x*x) + 0.5f*R*R*asin(x/R));
   }
   public static float computeAreaUnderArc(float X1, float X2, float R) {
       float area1 = areaIntegral(X1, R);
       float area2 = areaIntegral(X2, R);
       return (area2 - area1);
  }
   public static float computePixelArcArea(float Xp1, float Xp2, 
                                           float Yp1, float Yp2, float R) {
       float cenX = (Xp1 + Xp2)/2f;
       float cenY = (Yp1 + Yp2)/2f;
       float tmpc;
       // put into format such that pixel is in +x,+y quadrant
       if (cenX < 0f) { tmpc= Xp1; Xp1= -Xp2; Xp2= -tmpc; } // pixel in -x half-plane
       if (cenY < 0f) { tmpc= Yp1; Yp1= -Yp2; Yp2= -tmpc; } // pixel in -y half-plane
       float Rtr = sqrt(Xp2*Xp2 + Yp2*Yp2); 
       float Rtl = sqrt(Xp1*Xp1 + Yp2*Yp2);
       float Rbl = sqrt(Xp1*Xp1 + Yp1*Yp1); 
       float Rbr = sqrt(Xp2*Xp2 + Yp1*Yp1);

       float Xs1, Xs2; // region of computation (where arc intersects pixel edges)
       float arcVol = 0f;
       if (R <= Rbl) return 0f;
       if (R >= Rtr) {
          arcVol = (Xp2-Xp1)*(Yp2-Yp1);
          return arcVol;
       }
       if (R >= Rtl) {
          Xs1 = sqrt(R*R - Yp2*Yp2);
          arcVol = (Xs1-Xp1)*(Yp2-Yp1);
       }
       else Xs1 = Xp1;
       if (R <= Rbr)
          Xs2 = sqrt(R*R - Yp1*Yp1);
       else Xs2 = Xp2;
       arcVol += computeAreaUnderArc(Xs1, Xs2, R) - (Xs2-Xs1)*Yp1;
       return arcVol;
  }

  public static void clearOverlays(ImagePlus curImp) {
     curImp.updateAndDraw();
     IJ.wait(100); // give system time to draw
  }

  public static void overlayPoints(Graphics curGr, ImageCanvas curCan, Point2D[] points, int marksize) {
    for (int i=0; i<points.length; i++) 
      overlayPoint(curGr, curCan, points[i].x, points[i].y, marksize, "green");
  }
  public static void overlayPoints(Graphics curGr, ImageCanvas curCan, Point2D[] points, int marksize, String color) {
    for (int i=0; i<points.length; i++) {
      overlayPoint(curGr, curCan, points[i].x, points[i].y, marksize, color);
      if (i>0) 
        overlayLine(curGr, curCan, points[i-1].x, points[i-1].y,
                points[i].x, points[i].y,  color);
      else
        overlayLine(curGr, curCan, points[0].x, points[0].y,
                points[points.length-1].x, points[points.length-1].y, color);
    }
  }
  public static void overlayPoint(Graphics curGr, ImageCanvas curCan, Point2D point, int marksize) {
     overlayPoint(curGr, curCan, point.x, point.y, marksize, "green");
  }
  public static void overlayPoint(Graphics curGr, ImageCanvas curCan, Point2D point, int marksize, String color) {
     overlayPoint(curGr, curCan, point.x, point.y, marksize, color);
  }  
  public static void overlayPoint(Graphics curGr, ImageCanvas curCan, float x, float y, int marksize, String color) {
    int scrTLx = Int(curCan.screenX(Int(x + 0.5f)) -(float)marksize/2f); // shift by half pixel
    int scrTLy = Int(curCan.screenY(Int(y + 0.5f)) -(float)marksize/2f);
    curGr.setColor(ArrayDisplay.getColor(color));  
    curGr.fillOval(scrTLx, scrTLy, marksize, marksize);
    //    curGr.drawRect(scrxTL, scryTL, scrW, scrH);
  }
  public static void overlayLine(Graphics curGr, ImageCanvas curCan, float x1, float y1, float x2, float y2, String color) {
    int scrx1 = Int(curCan.screenX(Int(x1 + 0.5f)));
              // shift by half pixel else appears at pixel top left
    int scry1 = Int(curCan.screenY(Int(y1 + 0.5f)));
    int scrx2 = Int(curCan.screenX(Int(x2 + 0.5f)));
    int scry2 = Int(curCan.screenY(Int(y2 + 0.5f)));
    curGr.setColor(ArrayDisplay.getColor(color));  
    curGr.drawLine(scrx1, scry1, scrx2, scry2);
  }

   public static void setThreshold(ArrayDisplay curAD, 
                  double minThreshold, double maxThreshold) {
      ImagePlus curImp = curAD.getImagePlus();
      ImageProcessor ip = curImp.getProcessor();
      int lutColor = ImageProcessor.RED_LUT;
      ip.setThreshold(minThreshold, maxThreshold, lutColor);
   }

   public static void autoSetWindowLevel() {
       // applied auto window-level adjustment to top window
       ContrastAdjuster autoCA = new ContrastAdjuster();
       autoCA.run("wl");
       autoCA.close();
   }
} // end class


