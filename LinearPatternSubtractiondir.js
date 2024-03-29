#include <pjsr/ReadTextOptions.jsh>
#include <pjsr/ImageOp.jsh>
#include <pjsr/SampleType.jsh>
#include <pjsr/UndoFlag.jsh>
#include "PSCommonFunctions.jsh"
#include "LPS_UI.jsh"

#feature-id Utilities > LinearPatternSubtraction
#define TITLE "LPS_by_oldwexi"
#define VERSION "1.0.0"
#define ID "LPS"

/*

LinearPatternSubtraction Script.

Script to correct residual column or row patterns in an image.
This script reuses some code written by Georg Viehoever
in the CanonBandingReduction script and applies the column or row correction
in the multiscale context. The engine also includes outlier rejection for a
more robust statistical evaluation. The GUI has been removed and the script
is configured by modifying the properties of the Config function.

The script corrects entire rows or columns, but it can also correct partial
rows or columns by reading a defect table generated by CosmeticCorrection.
Thus, if you want to correct partial rows or columns, you should first run
CosmeticCorrection, manually create the defect list, and save it to a text file.

Copyright (C) 2019 Vicent Peris (OAUV).
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

--------------------------------------------------------------------------------


** Script functions:

   235 Config
   260 DefineWindowsandImages
   355 GlobalRejection
   378 PatternSubtraction
   408 LPSEngine
   555 ExecutionStart
   618 main

   -- Associated functions from CommonFunctions.jsh:
         395 IterativeStatistics
         421 BackgroundStatistics
         428 MultiscaleProcessing
         474 LineList


** Script structure:

   main --- Config
    | |
    | ----- ExecutionStart
    |
    ------- LPSEngine --- DefineWindowsAndImages
            | | | | |
            | | | | ----- BackgroundStatistics
            | | | |
            | | | ------- MultiscaleProcessing
            | | |
            | | --------- GlobalRejection
            | |
            | ----------- LineList
            |
            ------------- PatternSubtraction --- IterativeStatistics


** Working image windows and images by function:

   -- main:
         ImageWindow.activeWindow

   -- Config:
         none

   -- ExecutionStart:
         ImageWindow.activeWindow

   -- LPSEngine & DefineWindowsAndImages:
         targetImageWindow
         SSImageWindow
         LSImageWindow
         patternImageWindow
         clippingMaskWindow
         targetImage
         SSImage
         LSImage
         patternImage
         clippingMask

   -- BackgroundStatistics:
         targetImage

   -- MultiscaleProcessing:
         SSImage
         LSImage

   -- GlobalRejection:
         targetImage
         SSImage
         LSImage
         clippingMask

   -- LineList:
         none

   -- PatternSubtraction:
         targetImage
         SSImage
         patternImage


** Guidelines:

Before using this script you need to configure the Config function. These are
the configuration parameters:

- You can apply the script to the currently active image or to a set of images
inside a directory. In the first case, set the targetIsActiveImage to true.
In the latter case, set this property to false; then, specify the data directory
in the inputDir property and the file saving directory in the outputDir property.
inputDir and outputDir can be the same if you specify a file name postfix.

- When running the script multiple times on the active image, you can choose
to automatically close the working images from the last run by setting
the closeFormerWorkingImages property to true; only the target image
will remain open.

- If you process an entire directory, please change the image extension as well
in the "targetImageExtension" parameter and specify a filename postfix to avoid
overwriting the target images. Please don't write the point before the file
extension! Be aware that the file extension is case sensitive.

- If you want to correct a column pattern, set the "correctColumns" property to
true. If you want to correct a row pattern, set it to false.

- You can correct all the columns or row in the images, or you can correct only
specific ones. This is controlled by the correctEntireImage property. In case
you set it to false, you'll need to specify a defect list file in the
defectsFilePath property. This file can contain entire or partial rows or columns.
Only these will be corrected if you set correctEntireImage to false.

- If you set correctEntireImage to true, you'll still need to specify a defect
file list to correct partial columns or rows, since the script will correct
only entire ones until it finds a partial one in the defect list.

- The algorithm isolates the column or row structures from the large-scale
structures of the image. This scale separation is defined by the
"layersToRemove" parameter. The parameters defines the scale size at wich we
separate the small and large structures, in a dyadic sequence. Thus, the default
value of 8 means that we will remove the structures up to the scale of 2^(8-1)
pixels (128).

- After the scale separation, we also reject the bright pixels in the columns
or rows, mostly coming from the stars. This rejection is performed by
calculating the statistics of each column or row and defining a rejection limit
in sigmas with the rejectionLimit property. A lower value means a more
restrictive rejection.

- We can adjust the local contrast of the columns or rows, depending on the
local illumination level of the large-scale component image, by setting the
smallScaleNormalization property to true. This is usually needed because
the line structures lose contrast in higher illuminated areas, thus affecting
the statistics of the line.

- An additional pixel rejection is available through the globalRejection
property. It works by rejecting pixels of the original target image based
on their pixel value. It works by measuring the background sky noise and setting
a rejection limit in sigma values towards the higher pixel values. This way,
all those pixels having higher values than the defined limit will be rejected
in the small-scale component image, preventing to affect the statistics
of the lines. This is useful to reject big areas of bright objects light
a galaxy or a nebula. The rejection limit is defined by the globalRejectionLimit
property, in sigma units above the median sky background level.
The globalRejectionLimit property  should usually be much hgher than
the rejectionLimit; it could be tipically between 10 - 30 sigma units.
If it's set too low, it can create an artificial banding pattern; if set
too high, it won't reject the large diffuse objects and it will be prone
to create illuminated bands along the image in the direction of the rows
or columns (depending on what you're correcting).

- If the above normalization  or global rejection are activated, you need
to specify a background sky reference area by setting
the backgroundReferenceLeft, backgroundReferenceTop, backgroundReferenceWidth
 and backgroundReferenceHeight properties. The easier way to set these values
is to create a preview and check the numbers in PREVIEW > Modify Preview...


** Product images:

   -- pattern:
         The calculated line pattern present in the reference image.

   -- clipping_mask:
         The image used to reject the pixels defined by the globalRejectionLimit
         parameter. The white pixels will be rejected and won't contribute to
         the line statistics.

   -- LS:
         The large-scale component image used to isolate the small-scale
         components of the image.

   -- SS:
         The small-scale component image used to calculate the line statistics.
         In this image, the rejected pixels by the global rejection are set to
         white,in the same way as in the clipping_mask image. This way, we
         reject those pixels since they are always above the rejectionHigh value
         of ImageStatistics. These white pixels will be displayed only if
         globalRejection is enabled.

*/



//------------------------------------------------------------------------------
// Script configuration function.
// PLEASE READ THE ABOVE TEXT BEFORE RUNNING THE SCRIPT.

function Config()
{
   this.targetIsActiveImage = false;
   this.closeFormerWorkingImages = true;
   this.dir="J:/itelescope/ck18w020/20191031";
   this.correctColumns = true;
   this.correctEntireImage = true;
   this.partialDefectsFilePath = "J:/main/detected-columns_5-sigma.txt";
   this.targetImageExtension="xisf";
   this.postfix="_lps";
   this.layersToRemove = 9;
   this.rejectionLimit = 3;
   this.smallScaleNormalization = true;
   this.globalRejection = true;
   this.globalRejectionLimit = 7;
   this.backgroundReferenceLeft=1024;
   this.backgroundReferenceTop=1024;
   this.backgroundReferenceWidth=400;
   this.backgroundReferenceHeight=400;
}

//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
// These are the image windows and images that will be used by the script engine.
function DefineWindowsAndImages( globalRejection )
{
   // Define the working image windows and images.

   this.targetImage = null;
   this.LSImage = null;
   this.SSImage = null;
   this.patternImage = null;

   this.targetImageWindow = ImageWindow.activeWindow;

   this.targetImage = new Image( this.targetImageWindow.mainView.image.width,
                                 this.targetImageWindow.mainView.image.height,
                                 this.targetImageWindow.mainView.image.numberOfChannels,
                                 this.targetImageWindow.mainView.image.colorSpace,
                                 this.targetImageWindow.mainView.image.bitsPerSample,
                                 SampleType_Real );

   this.targetImage.apply( this.targetImageWindow.mainView.image );

   // The large-scale component image.
   // The initial image is a clone of the target image.
   // The content of this image will be created
   // by the MultiscaleProcessing function.
   this.LSImageWindow = new ImageWindow( this.targetImage.width,
                                         this.targetImage.height,
                                         this.targetImage.numberOfChannels,
                                         32, true, false, "LS" );

   this.LSImage = new Image( this.targetImageWindow.mainView.image.width,
                             this.targetImageWindow.mainView.image.height,
                             this.targetImageWindow.mainView.image.numberOfChannels,
                             this.targetImageWindow.mainView.image.colorSpace,
                             this.targetImageWindow.mainView.image.bitsPerSample,
                             SampleType_Real );

   // The small-scale component image.
   // We'll get the column statistics from this image.
   // We copy the targetImage content to this image;
   // the large-scale components of this image
   // will be removed by the MultiscaleProcessing function.
   this.SSImageWindow = new ImageWindow( this.targetImage.width,
                                         this.targetImage.height,
                                         this.targetImage.numberOfChannels,
                                         32, true, false, "SS" );

   this.SSImage = new Image( this.targetImage.width,
                             this.targetImage.height,
                             this.targetImage.numberOfChannels,
                             this.targetImage.colorSpace,
                             this.targetImage.bitsPerSample,
                             SampleType_Real );

   this.SSImage.apply( this.targetImageWindow.mainView.image );

   // The image containing the line pattern.
   // The pattern is calculated and subtracted
   // from the target image by the PatternSubtraction function.
   this.patternImageWindow = new ImageWindow( this.targetImage.width,
                                              this.targetImage.height,
                                              this.targetImage.numberOfChannels,
                                              32, true, false, "pattern" );

   this.patternImage = new Image( this.targetImage.width,
                                  this.targetImage.height,
                                  this.targetImage.numberOfChannels,
                                  this.targetImage.colorSpace,
                                  this.targetImage.bitsPerSample,
                                  SampleType_Real );

   this.patternImage.fill( 0 );

   if ( globalRejection )
      this.clippingMaskWindow = new ImageWindow( this.targetImage.width,
                                                 this.targetImage.height,
                                                 this.targetImage.numberOfChannels,
                                                 32, true, false, "clipping_mask" );

      this.clippingMask = new Image( this.targetImage.width,
                                     this.targetImage.height,
                                     this.targetImage.numberOfChannels,
                                     this.targetImage.colorSpace,
                                     this.targetImage.bitsPerSample,
                                     SampleType_Real );

      this.clippingMask.apply( this.targetImageWindow.mainView.image );
}
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
// Function to perform a pixel rejection in the small-scale image based on the
// pixel values of the target image. The rejected pixel values are set to 1
// in the small-scale image; this way, they will be all rejected when performing
// the in-line pixel rejection in the PatternSubtraction function since
// the rejection high value will be always below 1.
function GlobalRejection( targetImage, SSImage, LSImage, clippingMask,
                          backgroundMedian, backgroundMAD, globalRejectionLimit )
{
   let globalRejectionHigh = backgroundMedian + ( backgroundMAD * globalRejectionLimit );
   clippingMask.binarize( globalRejectionHigh );
   // Before applying the maximum operation between the clipping mask and
   // the small-scale component image, we need to add the median of
   // the sky background to the latter, since the median value of that image
   // is zero because we already subtracted the large-scale components.
   SSImage.apply( backgroundMedian, ImageOp_Add );
   SSImage.apply( clippingMask, ImageOp_Max );
   // After applying the maximum operation, we should subtract again the sky
   // background pedestal to keep the median value of the small-component image
   // to zero. This is needed because this image is used specifically
   // to calculate the defect deviations that should be removed from
   // the target image.
   SSImage.apply( backgroundMedian, ImageOp_Sub );
   Console.writeln( format( "Global rejection high value: %9.7f", globalRejectionHigh ) );
}
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
// Generate the pattern image and subtract it from the target image.
function PatternSubtraction( correctColumns, lines, targetImage, SSImage, patternImage, rejectionLimit )
{
   // Generate the line-patterned image.
   for ( let i = 0; i < lines.columnOrRow.length; ++i )
   {
      // Select the line specified in the line list.
      if ( correctColumns )
      {
         var lineRect = new Rect( 1, lines.endPixel[i] - lines.startPixel[i] + 1 );
         lineRect.moveTo( lines.columnOrRow[i], lines.startPixel[i] );
      }
      else
      {
         var lineRect = new Rect( lines.endPixel[i] - lines.startPixel[i] + 1, 1 );
         lineRect.moveTo( lines.startPixel[i], lines.columnOrRow[i] );
      }
      // Calculate the line statistics.
      var lineStatistics = new IterativeStatistics( SSImage, lineRect, rejectionLimit );
      // Store line median value into patternImage.
      // Please note that we apply the same line selection to this image,
      // so we fill only that line with its median value.
      patternImage.apply( lineStatistics.median, 1, lineRect );
   }
   // Finally we subtract the pattern image from the target image.
   targetImage.apply( patternImage, ImageOp_Sub );
}
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
// Script engine
function LPSEngine( outputFilePath,
                    correctColumns, correctEntireImage, partialDefectsFilePath,
                    layersToRemove, smallScaleNormalization,
                    backgroundReferenceLeft, backgroundReferenceTop,
                    backgroundReferenceWidth, backgroundReferenceHeight,
                    rejectionLimit, globalRejection, globalRejectionLimit )
{
   let engineTime = new ElapsedTime;

   // Define the needed image windows and images for the entire engine.
   var WI = new DefineWindowsAndImages( globalRejection );

   // Calculate the median and MAD values of the background reference area.
   var background = new BackgroundStatistics( WI.targetImage,
                                              backgroundReferenceLeft, backgroundReferenceTop,
                                              backgroundReferenceWidth, backgroundReferenceHeight);

   // Generate the small and large-scale component images.
   Console.writeln( "Isolating multiscale components" );
   Console.flush();
   MultiscaleProcessing( WI.SSImage, WI.LSImage, layersToRemove );

   // Apply the small scale normalization
   // to the already processed small-scale component image.
   if ( smallScaleNormalization )
   {
      WI.SSImage.apply( WI.LSImage, ImageOp_Mul );
      WI.SSImage.apply( background.median, ImageOp_Div );
   }

   // Perform a pixel rejection in the entire image
   // based on the pixel value of the target image.
   if ( globalRejection )
      GlobalRejection( WI.targetImage, WI.SSImage,
                       WI.LSImage, WI.clippingMask,
                       background.median, background.MAD, globalRejectionLimit );

   // Build the list of lines to be corrected.
   // This list includes both entire and partial columns or rows.
   Console.writeln( "Building the list of pattern lines" );
   Console.flush();
   if ( correctColumns )
   {
      var maxPixelPara = WI.targetImage.height - 1;
      var maxPixelPerp = WI.targetImage.width - 1;
   }
   else
   {
      var maxPixelPara = WI.targetImage.width - 1;
      var maxPixelPerp = WI.targetImage.height - 1;
   }

   // Read the defect table.
   let tableLines = File.readLines( partialDefectsFilePath, ReadTextOptions_RemoveEmptyLines | ReadTextOptions_TrimLines );
   var partialColumnOrRow = new Array;
   var partialStartPixel = new Array;
   var partialEndPixel = new Array;
   for ( let iTable = 0; iTable < tableLines.length; ++iTable )
   {
      let tokens = tableLines[iTable].split( " " );
      partialColumnOrRow.push( parseInt( tokens[1] ) );
      partialStartPixel.push( parseInt( tokens[2] ) );
      partialEndPixel.push( parseInt( tokens[3] ) );
   }

   // Create the list of lines to be corrected.
   var lines = new LineList( correctEntireImage,
                             partialColumnOrRow, partialStartPixel, partialEndPixel,
                             maxPixelPara, maxPixelPerp )

   // Generate the line pattern and subtract it from the target image.
   Console.writeln( "Correcting the line pattern" );
   Console.flush();
   PatternSubtraction( correctColumns, lines,
                       WI.targetImage, WI.SSImage, WI.patternImage,
                       rejectionLimit );

   // Show the working images if the target image is the active image.
   if ( outputFilePath == null )
   {
      // Add the median pixel value of the large-scale component image
      // to the small-scale, the pattern and the target images.
      // This ensures that the median signal intensity in the product
      // images is the same than the original image before the script
      // execution. This also prevents having pixels values out of the [0,1]
      // range in these images, which is necessary for a correct visualization
      // once they are applied to their corresponding image windows.
      let LSImageMedian = WI.LSImage.median();
      WI.SSImage.apply( LSImageMedian, ImageOp_Add );
      WI.SSImage.truncate( 0, 1 );
      WI.patternImage.apply( LSImageMedian, ImageOp_Add );
      WI.patternImage.truncate( 0, 1 );
      var targetImageMedian = WI.targetImage.median();
      WI.targetImage.apply( targetImageMedian, ImageOp_Sub );
      WI.targetImage.apply( LSImageMedian, ImageOp_Add );
      WI.targetImage.truncate( 0, 1 );

      // Apply the working images to their corresponding image windows.
      WI.targetImageWindow.mainView.beginProcess();
      WI.targetImageWindow.mainView.image.apply( WI.targetImage );
      WI.targetImageWindow.mainView.endProcess();
      WI.SSImageWindow.mainView.beginProcess( UndoFlag_NoSwapFile );
      WI.SSImageWindow.mainView.image.apply( WI.SSImage );
      WI.SSImageWindow.mainView.endProcess();
      WI.LSImageWindow.mainView.beginProcess( UndoFlag_NoSwapFile );
      WI.LSImageWindow.mainView.image.apply( WI.LSImage );
      WI.LSImageWindow.mainView.endProcess();
      WI.patternImageWindow.mainView.beginProcess( UndoFlag_NoSwapFile );
      WI.patternImageWindow.mainView.image.apply( WI.patternImage );
      WI.patternImageWindow.mainView.endProcess();
      WI.clippingMaskWindow.mainView.beginProcess( UndoFlag_NoSwapFile );
      WI.clippingMaskWindow.mainView.image.apply( WI.clippingMask );
      WI.clippingMaskWindow.mainView.endProcess();

      // Show the working images.
      WI.LSImageWindow.show();
      WI.SSImageWindow.show();
      WI.patternImageWindow.show();
      WI.clippingMaskWindow.show();
   }
   // Save the processed image if the target is an image list.
   else
   {
      WI.targetImageWindow.mainView.beginProcess( UndoFlag_NoSwapFile );
      WI.targetImageWindow.mainView.image.apply( WI.targetImage );
      WI.targetImageWindow.mainView.endProcess();
      Console.writeln( "Saving processed image to disk" );
      Console.flush();
      WI.targetImageWindow.saveAs( outputFilePath, false, false, false, false );
      WI.targetImageWindow.forceClose();
      WI.LSImageWindow.forceClose();

      if ( File.exists( outputFilePath ) )
      {
         Console.writeln( "Image saved to " + outputFilePath );
         Console.flush();
      }
      else
         throw new Error( "*** File I/O Error: Could not save image to " + outputFilePath );
   }
   Console.writeln( "Engine processing time: " + ElapsedTime.toString( engineTime.value ) );
}
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
// Console messages at the start of the execution
// with the currently working configuration options.
function ExecutionStart( CONFIG )
{
   processEvents();
   Console.show();

   if ( CONFIG.closeFormerWorkingImages )
   {
      if ( ! ImageWindow.windowById( "pattern" ).isNull )
         ImageWindow.windowById( "pattern" ).forceClose();
      if ( ! ImageWindow.windowById( "SS" ).isNull )
         ImageWindow.windowById( "SS" ).forceClose();
      if ( ! ImageWindow.windowById( "LS" ).isNull )
         ImageWindow.windowById( "LS" ).forceClose();
      if ( ! ImageWindow.windowById( "clipping_mask" ).isNull )
         ImageWindow.windowById( "clipping_mask" ).forceClose();
   }

   Console.writeln( "" );
   Console.noteln( "===============================" );
   Console.noteln( "LinearPatternSubtraction script" );
   Console.noteln( " (C) 2019 Vicent Peris (OAUV)" );
   Console.noteln( "===============================" );
   Console.writeln( "" );
   Console.noteln( "Working config options:" );
   if( CONFIG.targetIsActiveImage )
   {
      Console.noteln( "Target image is active image" );
      if( CONFIG.closeFormerWorkingImages )
         Console.noteln( "Set to close former working images" );
   }
   if( CONFIG.correctColumns )
      Console.noteln( "Correcting columns" );
   else
      Console.noteln( "Correcting rows" );
   if ( CONFIG.correctEntireImage )
      Console.noteln( "Correcting entire image" );
   else
      Console.noteln( "Correcting specific defects" );
   if( CONFIG.partialDefectsFilePath != null )
      Console.noteln( "Partial columns table: " + CONFIG.partialDefectsFilePath );
   if( ! CONFIG.targetIsActiveImage )
   {
      Console.noteln( "Data dir: " + CONFIG.dir )
      Console.noteln( "Image extension: " + CONFIG.targetImageExtension );
      Console.noteln( "File name postfix: " + CONFIG.postfix );
   }
   Console.noteln( "Multiscale layers to remove: " + CONFIG.layersToRemove );
   if ( CONFIG.smallScaleNormalization )
   {
      Console.noteln( "Applying small-scale component normalization" );
      Console.noteln( "Background reference ( L, T, W, H ): " + CONFIG.backgroundReferenceLeft + ", "
                                                              + CONFIG.backgroundReferenceTop + ", "
                                                              + CONFIG.backgroundReferenceWidth + ", "
                                                              + CONFIG.backgroundReferenceHeight )
   }
   if ( CONFIG.globalRejection )
      Console.noteln( "Global rejection (sigmas):" + CONFIG.globalRejectionLimit );
   Console.noteln( "High rejection limit (sigmas): " + CONFIG.rejectionLimit );
   Console.flush();
}
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
function main()
{
   let T = new ElapsedTime;

   // Load the script configuration values on top.
   var CONFIG = new Config();

   var settings = Settings.read(ID + "/parms", DataType_String);
   if (settings != null) {
      Console.writeln("Configuration read from settings file");
      CONFIG = JSON.parse(settings);
   }

   var dialog = new showConfigDialog(CONFIG);
   dialog.onReturn = function(ret) {
      Console.writeln("Config from dialog");
      Console.writeln("==================");
      Console.writeln("targetIsActiveImage: "+CONFIG.targetIsActiveImage);
      Console.writeln("closeFormerWorkingImages: "+CONFIG.closeFormerWorkingImages);
      Console.writeln("dir: "+CONFIG.dir);
      Console.writeln("correctColumns: "+CONFIG.correctColumns);
      Console.writeln("correctEntireImage: "+CONFIG.correctEntireImage);
      Console.writeln("partialDefectsFilePath: "+CONFIG.partialDefectsFilePath);
      Console.writeln("targetImageExtension: "+CONFIG.targetImageExtension);
      Console.writeln("postfix: "+CONFIG.postfix);
      Console.writeln("layersToRemove: "+CONFIG.layersToRemove);
      Console.writeln("rejectionLimit: "+CONFIG.rejectionLimit);
      Console.writeln("smallScaleNormalization: "+CONFIG.smallScaleNormalization);
      Console.writeln("globalRejection: "+CONFIG.globalRejection);
      Console.writeln("globalRejectionLimit: "+CONFIG.globalRejectionLimit);
      Console.writeln("backgroundReferenceLeft: "+CONFIG.backgroundReferenceLeft);
      Console.writeln("backgroundReferenceTop: "+CONFIG.backgroundReferenceTop);
      Console.writeln("backgroundReferenceWidth: "+CONFIG.backgroundReferenceWidth);
      Console.writeln("backgroundReferenceHeight: "+CONFIG.backgroundReferenceHeight);
      Console.writeln();
   }
	let ret = dialog.execute();

   if (ret == 1) {
      Settings.write(ID + "/parms", DataType_String, JSON.stringify(CONFIG));

      ExecutionStart( CONFIG );

      // Process only the active image. The processed image is not saved to disk.
      if ( CONFIG.targetIsActiveImage )
      {
         Console.writeln( "" );
         Console.noteln( "Processing active image" );
         Console.flush();

         let outputFilePath = null;
         LPSEngine( outputFilePath,
                    CONFIG.correctColumns,
                    CONFIG.correctEntireImage,
                    CONFIG.partialDefectsFilePath,
                    CONFIG.layersToRemove,
                    CONFIG.smallScaleNormalization,
                    CONFIG.backgroundReferenceLeft,
                    CONFIG.backgroundReferenceTop,
                    CONFIG.backgroundReferenceWidth,
                    CONFIG.backgroundReferenceHeight,
                    CONFIG.rejectionLimit,
                    CONFIG.globalRejection,
                    CONFIG.globalRejectionLimit );
      }
      // Process an entire directory. Each image is saved to disk
      // with the postfix specified in the Config function.
      else
      {
         // Exit script if the output path is not specified.
         if ( CONFIG.dir == "" )
            throw new Error( "Output directory is not specified." );

         // Create list of images to process.
         var imageList = new Array;
         let f = new FileFind;
         if ( f.begin( CONFIG.dir + "/*." + CONFIG.targetImageExtension ) )
            do
               if ( f.isFile )
                  imageList.push( CONFIG.dir + "/" + f.name );
         while ( f.next() );

         // Exit script if there are no images to be processed.
         if ( imageList.length == 0 )
            throw new Error( "There are no images to be processed. Please check the input directory and file extension." );

         // Process each image in the file list.
         for ( let i = 0; i < imageList.length; ++i )
         {
            Console.writeln( "" );
            Console.noteln( "Processing image " + ( i + 1 ) + " of " + imageList.length );
            Console.flush();

            // Get file name of each image.
            let fileName = File.extractName( imageList[i] );
            // Construct the output file path of the processed image.
            let outputFilePath = CONFIG.dir + "/" + fileName + CONFIG.postfix + "." + CONFIG.targetImageExtension;

            ImageWindow.open( imageList[i] )[0].show();
            LPSEngine( outputFilePath,
                       CONFIG.correctColumns,
                       CONFIG.correctEntireImage,
                       CONFIG.partialDefectsFilePath,
                       CONFIG.layersToRemove,
                       CONFIG.smallScaleNormalization,
                       CONFIG.backgroundReferenceLeft,
                       CONFIG.backgroundReferenceTop,
                       CONFIG.backgroundReferenceWidth,
                       CONFIG.backgroundReferenceHeight,
                       CONFIG.rejectionLimit,
                       CONFIG.globalRejection,
                       CONFIG.globalRejectionLimit );

            processEvents();
            if ( Console.abortRequested )
            {
               Console.writeln( "" );
               Console.criticalln( "Script aborted" );
               return;
            }
         }
      }
      processEvents();
   } else if (ret == 0) {
      Settings.remove(ID + "/parms");
   }
   Console.writeln( "" );
   Console.writeln( "Script processing time: " + ElapsedTime.toString( T.value ) );
}

main();
