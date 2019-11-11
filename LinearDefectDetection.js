

#feature-id Utilities > LinearDefectDetection

#define TITLE "LDD_by DFG"
#define VERSION "1.0.0"
#define ID "LDD"

#include <pjsr/ImageOp.jsh>
#include <pjsr/SampleType.jsh>
#include <pjsr/UndoFlag.jsh>
#include "PSCommonFunctions.jsh"
#include "LDD_UI.jsh"

/*

LinearDefectDetection Script.

Script to detect defective columns or rows in a reference image.
The script is configured by modifying the properties of the Config function.

Copyright (C) 2019 Vicent Peris (OAUV),
                   Christian Liska (UI)
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

   180 Config
   198 DefineWindowsandImages
   257 LDDEngine
   379 Output
   435 ExecutionStart
   475 main

   -- Associated functions from CommonFunctions.jsh:
         265 MultiscaleProcessing
         272 PartialLineDetection
         288 LineList
         314 IterativeStatistics


** Script structure:

   main --- Config
   | | |
   | | ----- ExecutionStart
   | |
   | ------- LDDEngine --- DefineWindowsAndImages
   |         | | | |
   |         | | | ------- MultiscaleProcessing
   |         | | |
   |         | | --------- PartialLineDetection
   |         | |
   |         | ----------- LineList
   |         |
   |         ------------- IterativeStatistics
   |
   --------- Output


** Working image windows and images by function:

   -- main:
         none

   -- Config:
         none

   -- ExecutionStart:
         ImageWindow.activeWindow

   -- LPSEngine & DefineWindowsAndImages:
         referenceImageWindow
         lineModelWindow
         lineDetectionWindow
         referenceImage
         lineModelImage
         lineDetectionImage
         referenceSSImage

   -- MultiscaleProcessing:
         referenceSSImage

   -- PartialLineDetection:
         referenceImageCopy

   -- LineList:
         none

   -- IterativeStatistics:
         referenceSSImage

   -- Output:
         none


** Guidelines:

This script is executed on the active image. Before using this script you need
to configure the Config function. These are the configuration parameters:

- If you want to correct a column pattern, set the "detectColumns" property to
true. If you want to correct a row pattern, set it to false.

- When running the script multiple times on the active image, you can choose
to automatically close the working images from the last run by setting
the closeFormerWorkingImages property to true; only the target image
will remain open.

- The algorithm isolates the small-scale structures from the large-scale
structures in the pattern and target images before finding the matching
scaling factor. This scale separation is enabled with the removeScales property.
This scale separation is defined by the layersToRemove property. This property
defines the scale size at wich we separate the small and large structures,
in a dyadic sequence. Thus, a value of 8 means that we will remove
the structures up to the scale of 2^(8-1) pixels (128).

- After the scale separation, we also reject the bright pixels in the columns
or rows, mostly coming from the stars. This rejection is performed by
calculating the statistics of each column or row and defining a rejection limit
in sigmas with the rejectionLimit property. A lower value means a more
restrictive rejection.

- The linear defect detection is driven by the detectionThreshold property. Its
value is in sigmas, so a lower value will detect more defects.

- You can separately configure the detection threshold of the partial line
defects with the partialLineDetectionThreshold property.

- The partial line defect detection algorithm works by shifting a clone of the
target image and subtracting one from the other. This leaves short lines in the
image that indicate the origin of the defects. A too small value will confuse
these lines with residual noise in the processed image. Usually a value between
50 - 100 works fine.

- The script writes a CosmeticCorrection-compatible defect table to disk. Please
specify the output directory in the outputDir property.


** Product images:

   -- line_model:
         The calculated line pattern present in the reference image. It will
         contain the entire and partial defective lines, depending on the setup.

   -- partial_line_detection:
         Image used to detect the partial defective lines. This image contains
         small lines that point to the origin of the defective lines. The origin
         is located at the bottom or the right end of the lines, depending on
         the column or row correction, respectively. The smaller lines are
         generated randomly by the image noise; only the larger ones are
         detected as defective columns.

   -- line_detection:
         The calculated lines sorted by sigmas. In this image, the lines with a
         value at a distance of less than one sigma from the median are
         displayed as black. Those lines at a distance equal or greater than the
         configured detection threshold are displayed as white. The lines at
         intermediate distances have grey values evenly distributed by integer
         sigma values. Only the white lines will be detected by the script.

*/


//------------------------------------------------------------------------------
// Script configuration function.
// Please modify its properties to your specific needs.
function Config()
{
   this.detectColumns = true;
   this.detectPartialLines = true;
   this.imageShift = 100;
   this.closeFormerWorkingImages = true;
   this.layersToRemove = 9;
   this.rejectionLimit = 3;
   this.detectionThreshold = 5;
   this.partialLineDetectionThreshold = 5;
   this.imageShift = 100;
   this.outputDir = "J:/main";
}
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
// These are the image windows and images
// that will be used by the script engine.
function DefineWindowsAndImages( detectPartialLines )
{
   // Define the working image windows and images.
   this.referenceImageWindow = ImageWindow.activeWindow;

   this.referenceImage = new Image( this.referenceImageWindow.mainView.image.width,
                                    this.referenceImageWindow.mainView.image.height,
                                    this.referenceImageWindow.mainView.image.numberOfChannels,
                                    this.referenceImageWindow.mainView.image.colorSpace,
                                    32, SampleType_Real );

   this.referenceImage.apply( this.referenceImageWindow.mainView.image );

   if ( detectPartialLines )
   {
      this.referenceImageCopy = new Image( this.referenceImageWindow.mainView.image.width,
                                           this.referenceImageWindow.mainView.image.height,
                                           this.referenceImageWindow.mainView.image.numberOfChannels,
                                           this.referenceImageWindow.mainView.image.colorSpace,
                                           32, SampleType_Real );

      this.referenceImageCopy.apply( this.referenceImageWindow.mainView.image );
   }

   this.referenceSSImage = new Image( this.referenceImage.width,
                                      this.referenceImage.height,
                                      this.referenceImage.numberOfChannels,
                                      this.referenceImage.colorSpace,
                                      32, SampleType_Real );

   this.referenceSSImage.apply( this.referenceImage );

   this.lineModelWindow = new ImageWindow( this.referenceImage.width,
                                           this.referenceImage.height,
                                           this.referenceImage.numberOfChannels,
                                           32, true, false, "line_model" );

   this.lineModelImage = new Image( this.referenceImage.width,
                                    this.referenceImage.height,
                                    this.referenceImage.numberOfChannels,
                                    this.referenceImage.colorSpace,
                                    32, SampleType_Real );

   this.lineDetectionWindow = new ImageWindow( this.referenceImage.width,
                                               this.referenceImage.height,
                                               this.referenceImage.numberOfChannels,
                                               32, true, false, "line_detection" );

   this.lineDetectionImage = new Image( this.referenceImage.width,
                                        this.referenceImage.height,
                                        this.referenceImage.numberOfChannels,
                                        this.referenceImage.colorSpace,
                                        32, SampleType_Real );
}
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
// Script engine
function LDDEngine( detectColumns, detectPartialLines,
                    layersToRemove, rejectionLimit, imageShift,
                    detectionThreshold, partialLineDetectionThreshold )
{
   var WI = new DefineWindowsAndImages( detectPartialLines );

   // Generate the small-scale image by subtracting
   // the large-scale components of the image.
   MultiscaleProcessing( WI.referenceSSImage, null, layersToRemove );

   // Build a list of lines in the image.
   // This can include entire or partial rows or columns.
   if ( layersToRemove  < 7 )
      layersToRemove = 7;
   if ( detectPartialLines )
      var partialLines = new PartialLineDetection( detectColumns, WI.referenceImageCopy,
                                                   layersToRemove - 3, imageShift,
                                                   partialLineDetectionThreshold );

   if ( detectColumns )
   {
      var maxPixelPara = WI.referenceImage.height - 1;
      var maxPixelPerp = WI.referenceImage.width - 1;
   }
   else
   {
      var maxPixelPara = WI.referenceImage.width - 1;
      var maxPixelPerp = WI.referenceImage.height - 1;
   }

   if ( detectPartialLines )
      var lines = new LineList( true,
                                partialLines.columnOrRow,
                                partialLines.startPixel,
                                partialLines.endPixel,
                                maxPixelPara, maxPixelPerp );
   else
      var lines = new LineList( true, [], [], [], maxPixelPara, maxPixelPerp );

   // Calculate the median value of each line in the image.
   // Create a model image with the lines filled
   // by their respective median values.
   Console.writeln( "Analyzing " + lines.columnOrRow.length + " lines in the image" );
   Console.writeln( "" );
   var lineValues = new Array;
   for ( let i = 0; i < lines.columnOrRow.length; ++i )
   {
      if ( detectColumns )
      {
         var lineRect = new Rect( 1, lines.endPixel[i] - lines.startPixel[i] + 1 );
         lineRect.moveTo( lines.columnOrRow[i], lines.startPixel[i] );
      }
      else
      {
         var lineRect = new Rect( lines.endPixel[i] - lines.startPixel[i] + 1, 1 );
         lineRect.moveTo( lines.startPixel[i], lines.columnOrRow[i] );
      }
      var lineStatistics = new IterativeStatistics( WI.referenceSSImage, lineRect, rejectionLimit );
      WI.lineModelImage.selectedRect = lineRect;
      WI.lineModelImage.apply( lineStatistics.median );
      lineValues.push( lineStatistics.median );
   }
   WI.referenceSSImage.resetSelections();
   WI.lineModelImage.resetSelections();

   // Build the detection map image
   // and the list of detected line defects.
   this.detectedColumnOrRow = new Array;
   this.detectedStartPixel = new Array;
   this.detectedEndPixel = new Array;
   var lineModelMedian = WI.lineModelImage.median();
   var lineModelMAD = WI.lineModelImage.MAD();
   for (let i = 0; i < lineValues.length; ++i )
   {
      if ( detectColumns )
      {
         var lineRect = new Rect( 1, lines.endPixel[i] - lines.startPixel[i] + 1 );
         lineRect.moveTo( lines.columnOrRow[i], lines.startPixel[i] );
      }
      else
      {
         var lineRect = new Rect( lines.endPixel[i] - lines.startPixel[i] + 1, 1 );
         lineRect.moveTo( lines.startPixel[i], lines.columnOrRow[i] );
      }
      WI.lineDetectionImage.selectedRect = lineRect;
      let sigma = Math.abs( lineValues[i] - lineModelMedian ) / ( lineModelMAD * 1.4826 );
      WI.lineDetectionImage.apply( parseInt( sigma ) / ( detectionThreshold + 1 ) );
      if ( sigma >= detectionThreshold )
      {
         this.detectedColumnOrRow.push( lines.columnOrRow[i] );
         this.detectedStartPixel.push( lines.startPixel[i] );
         this.detectedEndPixel.push( lines.endPixel[i] );
      }
   }

   // Transfer the resulting images to their respective windows.
   WI.lineDetectionImage.resetSelections();
   WI.lineDetectionImage.truncate( 0, 1 );
   WI.lineModelImage.apply( WI.referenceImage.median(), ImageOp_Add );

   WI.lineModelWindow.mainView.beginProcess();
   WI.lineModelWindow.mainView.image.apply( WI.lineModelImage );
   WI.lineModelWindow.mainView.endProcess();

   WI.lineDetectionWindow.mainView.beginProcess();
   WI.lineDetectionWindow.mainView.image.apply( WI.lineDetectionImage );
   WI.lineDetectionWindow.mainView.endProcess();

   // Free memory space taken by working images.
   WI.referenceImage.free();
   WI.referenceSSImage.free();
   WI.lineModelImage.free();
   WI.lineDetectionImage.free();
   if ( detectPartialLines )
      WI.referenceImageCopy.free();
   WI.lineModelWindow.show();
   WI.lineDetectionWindow.show();
}
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
// Output the list of detected lines to console and text file.
function Output( detectColumns, detectedLines, threshold, outputDir )
{
   if ( detectedLines.detectedColumnOrRow.length > 0 )
   {
      if ( detectColumns )
      {
         var outputFileName = "/detected-columns_" + threshold + "-sigma.txt";
         var columnOrRow = "Col";
      }
      else
      {
         var outputFileName = "/detected-rows_" + threshold + "-sigma.txt";
         var columnOrRow = "Row";
      }


      var outputPath = outputDir + outputFileName;
      let defectListTable = new File();
      defectListTable.createForWriting( outputPath );
      Console.noteln( "" );
      Console.noteln( "Detected lines:" );
      Console.noteln( "---------------" );
      for (let i = 0; i < detectedLines.detectedColumnOrRow.length; ++i )
      {
         defectListTable.outTextLn( columnOrRow + " " +
                                    detectedLines.detectedColumnOrRow[i] + " " +
                                    detectedLines.detectedStartPixel[i] + " " +
                                    detectedLines.detectedEndPixel[i] );

         Console.noteln( columnOrRow + " " +
                         detectedLines.detectedColumnOrRow[i] + " " +
                         detectedLines.detectedStartPixel[i] + " " +
                         detectedLines.detectedEndPixel[i] );
      }
      Console.noteln( "" );
      Console.noteln( "Detected defect lines: " + detectedLines.detectedColumnOrRow.length );
      defectListTable.close();
      Console.writeln( "" );
      if ( File.exists( outputPath ) )
         Console.writeln( "Defect list saved to " + outputPath );
      else
         throw new Error( "*** File I/O Error: Could not output defect list to " + outputPath );
   }
   else
   {
      Console.noteln( "" );
      Console.noteln( "No defect was detected. Try lowering the threshold value." );
   }
}
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
// Console messages at the start of the execution
// with the currently working configuration options.
// This function also checks some setup errors
// coming from the Config function.
function ExecutionStart( CONFIG )
{
   Console.show();

   if ( CONFIG.closeFormerWorkingImages )
   {
      if ( ! ImageWindow.windowById( "partial_line_detection" ).isNull )
         ImageWindow.windowById( "partial_line_detection" ).forceClose();
      if ( ! ImageWindow.windowById( "line_model" ).isNull )
         ImageWindow.windowById( "line_model" ).forceClose();
      if ( ! ImageWindow.windowById( "line_detection" ).isNull )
         ImageWindow.windowById( "line_detection" ).forceClose();
   }

   Console.noteln( "" );
   Console.noteln( "============================" );
   Console.noteln( "LinearDefectDetection script" );
   Console.noteln( "(C) 2019 Vicent Peris (OAUV)" );
   Console.noteln( "============================" );
   Console.noteln( "" );
   Console.noteln( "Working config options:" );
   CONFIG.detectColumns ? Console.noteln( "Correcting columns" )
                        : Console.noteln( "Correcting rows" );
   Console.noteln( "Multiscale layers to remove: " + CONFIG.layersToRemove );
   Console.noteln( "In-column high rejection limit (sigmas): " + CONFIG.rejectionLimit );
   Console.noteln( "Column detection threshold (sigmas): " + CONFIG.detectionThreshold );
   Console.noteln( "Output directory: " + CONFIG.outputDir );
   Console.noteln( "" );

   // Check set up errors.
   if ( ImageWindow.activeWindow.isNull )
      throw new Error( "Non-existent active image window" );
   if ( CONFIG.outputDir == "" )
      throw new Error( "Ouput directory is not specified." );
   if ( ! File.directoryExists( File.extractDirectory( CONFIG.outputDir ) ) )
      throw new Error( "Output directory does not exist." );
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
      Console.writeln("detectColumns: "+CONFIG.detectColumns);
      Console.writeln("detectPartialLines: "+CONFIG.detectPartialLines);
      Console.writeln("imageShift: "+CONFIG.imageShift);
      Console.writeln("closeFormerWorkingImages: "+CONFIG.closeFormerWorkingImages);
      Console.writeln("layersToRemove: "+CONFIG.layersToRemove);
      Console.writeln("rejectionLimit: "+CONFIG.rejectionLimit);
      Console.writeln("detectionThreshold: "+CONFIG.detectionThreshold);
      Console.writeln("partialLineDetectionThreshold: "+CONFIG.partialLineDetectionThreshold);
      Console.writeln("outputDir: "+CONFIG.outputDir);
      Console.writeln();
   }

   let retDialog = dialog.execute();
   if (retDialog == 1) {
      Settings.write(ID + "/parms", DataType_String, JSON.stringify(CONFIG));

      var start = new ExecutionStart( CONFIG );

      var detectedLines = new LDDEngine( CONFIG.detectColumns,
                                         CONFIG.detectPartialLines,
                                         CONFIG.layersToRemove,
                                         CONFIG.rejectionLimit,
                                         CONFIG.imageShift,
                                         CONFIG.detectionThreshold,
                                         CONFIG.partialLineDetectionThreshold );

      Output( CONFIG.detectColumns, detectedLines, CONFIG.detectionThreshold, CONFIG.outputDir );
   } else if (retDialog == 0) {
      Settings.remove(ID + "/parms");
   }

   Console.writeln( "" );
   Console.writeln( ElapsedTime.toString( T.value ) );
}

main();
