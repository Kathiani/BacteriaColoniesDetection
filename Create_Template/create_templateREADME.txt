Walter O'Dell PhD,  wodell@rochester.edu,   6/29/05
overview:
   The purpose of this plugin is to perform template matching for detection of objects in an image. 
   This plugin was created initially as a teaching tool for an image processing class. The idea
   of template matching is to create a model of an object of interest (the template, or kernel) 
   and then to search over the image of interest for objects that match the template.  The 
   underlying math here computes, for each pixel in the image of interest, the normalized 
   cross-correlation coefficient (NCCC) between the template and the underlying pixels in the image 
   of interest.  A perfect match would give an NCCC value of +1; comparison with an exact negative 
   (inverted grayscale values) gives an NCC of -1; while a comparison with a completely unrelated 
   template gives an NCCC of 0.  The NCCC computation inherently corrects for any background image
   intensity variations and differences in lighting and overall contrast.  It keys in only on the 
   relative size and relative intensity contrast of the pixels under the template.
  
Create Disk Template button:
   The classic example is detection of cells.  An example image is provided.  Running the plugin 
   brings up an interface window that allows one to create circular templates of any size. This is 
   handy if your objects somewhat resemble disks, which they do for these cells.  Here the cells 
   are ~30 pixels in diameter so I would create a disk that is ~15 pixels in radius.  

Inverty template button:
   In this example the template image should be inverted so that the template has a bright center
   with a darker background to match the appearance of the cells. 

Crop Template from Image button
   If there are many like-objects to be detected then it is useful to draw an ROI around one 
   representative object and then to use the Crop Template from Image button to apply that ROI as 
   the template.  This works amazingly well. If you want to use this Cropped ROI for other images 
   then just save that kernel image to file using the regular ImageJ file saving menu buttons.

Load Template from File button
   If there is an ROI image already saved to a file, or if you decided to create a template using 
   an external program, then you can load that pre-existing template from file and use it as the
   current tempalte. 

Perform Statistical Correlation
   Hitting this button then invokes the computation of the NCCC based on the current template image. 
   The Correlation image is then shown. The intensity assigned to each pixel represents the NCCC 
   value computed with the template image centered over that pixel location.  Beware that this is 
   essentiall an O(n^3) operation so having a very large base image or template image will cause this 
   computation to take a few minutes. For testing I often shrink my base image by a factor of 2.

set threshold and get particle analyzer results
   The particle counter and analyzer functions already in ImageJ are integrated here for convenience. 
   The threshold levels and particle sizes are those of the built-in particle analyzer and are applied
   to the correlation image, as evident by the program turning the corresponding pixels 'red'.  The
   qualifying particles are then outlined with a green box on both the correlation and the original
   image. The results of the number of counts and their pixel locations are reported in the Results 
   text window.

Compiling notes:
1. Place the following files into your plugins folder:
       Point2D.java
       ArrayDisplay.java
       ImageTools.java
       GenericRecallableDialog.java
       Create_A_Template_.java
2. Open your image of interest
3. Now just ask ImageJ to compile and run the Create_A_Template_.java file and all the 
  rest will automatically be taken care of.

After doing this the first time the plugin command Create_A_Template will exist in your plugins
directory.

The following class files will be generated although you need not worry about them:
        Point2D.java
        ArrayDisplay.class              (main image display class)
        ScreenSelectorClass.class       (add functionality to the ArrayDisplay class ..)
        ScreenSelectorCanvas.class      (by letting you interact with the image for point seletion)
        ImageTools.class                (library of general math and image processing routines)
 	GenericDialog2.class      	(my version of GenericDialog.java)
	GenericDialogPlus.class   	(extended version of GenericDialog.java)
	GenericRecallableDialog.class 	(complete extended version)
        Create_A_Template_.class


Of Interest to Developers: 2 things of general interest:

1. The ArrayDisplay class is really quite a useful addition. It greatly simplifies the creation and 
usage of image windows and display of 2 and 3 dimensional data.  The coder no longer has to worry
about keeping track of the ImagePlus and ImageProcessing classes and their associated windows and 
graphics and canvas classes, as the ArrayDisplay class makes all these transparent.  It also has 
so much overloading of functions it can handle just about any type of 2D and 3D dataset. In my
modified version of ImageJ at home I have an ArrayDisplay that has greater functionality and I have 
an ArrayColorDisplay to enable overlaying of two images in different colors -- useful for evaluating
the quality of image regitration.

2. The ImageTools class is a collection of useful general math as well as image processing routines.
Even some simple things like the square root function for floats does not exist in basic Java, so 
this has those and many others. Again I have a more complete version at home but the functions here
suffice for the Create_A_Template plugin.
