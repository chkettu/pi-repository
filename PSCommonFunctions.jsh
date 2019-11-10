#include <pjsr/ImageOp.jsh>
#include <pjsr/SampleType.jsh>
#include <pjsr/UndoFlag.jsh>

/*

CommonFunctions JavaScript Header.

This file includes several functions used by the pattern correction scripts.

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


** Header functions:

    98 BackgroundStatistics
   115 MultiscaleProcessing
   144 LineList
   205 IterativeStatistics
   244 PartialLineDetection


** Scripts by function:

   -- MultiscaleProcessing:
         LinearDefectDetection
         LinearPatternSubtraction
         PatternSubtraction

   -- LineList:
         LinearDefectDetection
         LinearPatternSubtraction

   -- IterativeStatistics:
         LinearDefectDetection
         LinearPatternSubtraction

   -- PartialLineDetection;
         PartialLineDetection
         LinearDefectDetection


** Function description:

   -- BackgroundStatistics:
         Function to calculate the median and MAD values of a background
         region in an image.

   -- MultiscaleProcessing:
         Function to subtract the large-scale components from an image
         using the median wavelet transform.

   -- LineList:
         Function to create a list of vertical or horizontal lines in an image.
         It can combine entire rows or columns and fragmented ones, if an array
         of partial sections is specified in the input parameters. This list is
         used to input the selected regions in the IterativeStatistics function.

   -- IterativeStatistics:
         Function to calculate the median and MAD of a selected image area
         with iterative outlier rejection in the high end of the distribution.
         Useful to reject bright objects in a background-dominated image,
         specially if the input image is the output image
         of MultiscaleProcessing.

   -- PartialLineDetection:
         Function to detect defective partial columns or rows in an image.

*/

//------------------------------------------------------------------------------
// This function calculates the median and the MAD of a background area.
// These values will be used by the MultiscaleProcessing
// and GlobalRejection functions in LinearPatternSubtraction.
function BackgroundStatistics( targetImage,
                               backgroundReferenceLeft, backgroundReferenceTop,
                               backgroundReferenceWidth, backgroundReferenceHeight)
{
   var backgroundRectangle = new Rect( backgroundReferenceWidth, backgroundReferenceHeight );
   backgroundRectangle.moveTo( backgroundReferenceLeft, backgroundReferenceTop );
   this.median = targetImage.median( backgroundRectangle );
   this.MAD = targetImage.MAD( this.median, backgroundRectangle );
   Console.writeln( format( "Background level: %9.7f +- %9.7f", this.median, this.MAD ) );
   if ( this.median < 0.0000001 )
      throw new Error( "*** Error: Background level is zero. Please select a background " +
                       "area with a higher signal level. We recommend checking your " +
                       "image since it could have a large proportion of clipped data. " );
}
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
function MultiscaleProcessing( image, LSImage, layersToRemove )
{
   // Generate the large-scale components image.
   // First we generate the array that defines
   // the states (enabled / disabled) of the scale layers.
   var scales = new Array;
   for ( let i = 0; i < layersToRemove; ++i )
      scales.push( 1 );
   // The scale layers are an array of images.
   // We use the medianWaveletTransform. This algorithm is less prone
   // to show vertical patterns in the large-scale components.
   var multiscaleTransform = new Array;
      Console.writeln( " Layers",layersToRemove-1 );
   multiscaleTransform = image.medianWaveletTransform( layersToRemove-1, 0,  scales );

   // We subtract the last layer to the image.
   // Please note that this image has negative pixel values.
   image.apply( multiscaleTransform[layersToRemove-1], ImageOp_Sub );
   // Generate a large-scale component image
   // if the respective input image is not null.
   if ( LSImage != null )
      LSImage.apply( multiscaleTransform[layersToRemove-1] );
   // Remove the multiscale layers from memory.
   for ( let i = 0; i < multiscaleTransform.length; ++i )
      multiscaleTransform[i].free();
}
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
// Function to build the list of lines to be corrected. This list includes
// entire rows or columns and sections of partial row or column defects.
function LineList( correctEntireImage, partialColumnOrRow, partialStartPixel, partialEndPixel, maxPixelPara, maxPixelPerp )
{
      this.columnOrRow = new Array;
      this.startPixel = new Array;
      this.endPixel = new Array;

   if ( ! correctEntireImage )
   {
      this.columnOrRow = partialColumnOrRow;
      this.startPixel = partialStartPixel;
      this.endPixel = partialEndPixel;
   }
   else
   {
      if ( partialColumnOrRow.length == 0 )
         partialColumnOrRow.push( maxPixelPerp + 1 );

      let iPartial = 0;
      for ( let i = 0; i <= maxPixelPerp; ++i )
      {
         if ( iPartial < partialColumnOrRow.length )
         {
            if ( i < partialColumnOrRow[iPartial] && correctEntireImage )
            {
               this.columnOrRow.push( i );
               this.startPixel.push( 0 );
               this.endPixel.push( maxPixelPara );
            }
            // Get the partial column or row.
            else
            {
               this.columnOrRow.push( partialColumnOrRow[iPartial] );
               this.startPixel.push( partialStartPixel[iPartial] );
               this.endPixel.push( partialEndPixel[iPartial] );
               if ( partialStartPixel[iPartial] > 0 )
               {
                  this.columnOrRow.push( partialColumnOrRow[iPartial] );
                  this.startPixel.push( 0 );
                  this.endPixel.push( partialStartPixel[iPartial] - 1 );
               }
               if ( partialEndPixel[iPartial] < maxPixelPara )
               {
                  this.columnOrRow.push( partialColumnOrRow[iPartial] );
                  this.startPixel.push( partialEndPixel[iPartial] + 1 );
                  this.endPixel.push( maxPixelPara );
               }
               // In some cases, there can be more than one section of
               // the same column or row in the partial defect list.
               // In that case, i (which is the current column or row number)
               // shouldn't increase because we are repeating
               // the same column or row.
               i = partialColumnOrRow[iPartial];
               ++iPartial;
            }
         }
         else if ( correctEntireImage )
         {
            this.columnOrRow.push( i );
            this.startPixel.push( 0 );
            this.endPixel.push( maxPixelPara );
         }
      }
   }
}
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
function IterativeStatistics( image, rectangle, rejectionLimit )
{
   image.selectedRect = rectangle;
   var formerHighRejectionLimit = 1000;
   var currentHighRejectionLimit = 1;
   var j = 0;
   while ( formerHighRejectionLimit / currentHighRejectionLimit > 1.001 || j < 10 )
   {
      // Construct the statistics object to rectangle statistics.
      // These statistics are updated with the new high rejection limit
      // calculated at the end of the iteration.
      var iterativeRectangleStatistics = new ImageStatistics;
      with ( iterativeRectangleStatistics )
      {
         medianEnabled = true;

         lowRejectionEnabled = false;
         highRejectionEnabled = true;
         rejectionHigh = currentHighRejectionLimit;
      }
      iterativeRectangleStatistics.generate( image );
      this.median = iterativeRectangleStatistics.median;
      this.MAD = iterativeRectangleStatistics.mad;
      formerHighRejectionLimit = currentHighRejectionLimit;
      currentHighRejectionLimit = parseFloat( iterativeRectangleStatistics.median
                                  + ( iterativeRectangleStatistics.mad * 1.4826 * rejectionLimit ) );
      ++j;
   }
   image.resetSelections();
}
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
function PartialLineDetection( detectColumns, image, layersToRemove, imageShift, threshold )
{
   if ( ( detectColumns ? image.height : image.width ) < imageShift * 4 )
      throw new Error( "imageShift parameter too high for the current image size" );


   // Create a small-scale component image and its image window.
   // SSImage will be the main view of the small-scale component
   // image window because we need to apply a
   // MorphologicalTransformation instance to it.
   this.SSImageWindow = new ImageWindow( image.width,
                                         image.height,
                                         image.numberOfChannels,
                                         32, true, false,
                                         "partial_line_detection" );

   // The initial small-scale component image is the input image.
   this.SSImage = new Image( image.width,
                             image.height,
                             image.numberOfChannels,
                             image.colorSpace,
                             image.bitsPerSample,
                             SampleType_Real );

   this.SSImage.apply( image );

   // Subtract the large-scale components to the image.
   Console.noteln( "" );
   Console.noteln( "Isolating small-scale image components" );
   Console.flush();
   MultiscaleProcessing( this.SSImage, null, layersToRemove );

   // The clipping mask is an image to reject the highlights
   // of the processed small-scale component image. The initial
   // state of this image is the small-scale component image
   // after removing the large-scale components. We simply
   // binarize this image at 5 sigmas above the image median.
   // This way, the bright structures are white and the rest
   // of the image is pure black. We'll use this image
   // at the end of the processing.
   var clippingMask = new Image( image.width,
                                 image.height,
                                 image.numberOfChannels,
                                 image.colorSpace,
                                 image.bitsPerSample,
                                 SampleType_Real );

   clippingMask.apply( this.SSImage );
   clippingMask.binarize( clippingMask.MAD() * 5 );

   // Apply a morphological transformation process
   // to the small-scale component image.
   // The structuring element is a line in the direction
   // of the lines to be detected.
   Console.noteln( "" );
   Console.noteln( "Processing the small-scale component image" );
   Console.flush();
   if ( detectColumns )
      var structure =
      [[
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0
      ]];
   else
      var structure =
      [[
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
      ]];

   Console.writeln( "Applying morphological median transformation" );
   Console.flush();
   for ( let i = 0; i < 5; ++i )
      this.SSImage.morphologicalTransformation( 4, structure, 0, 0, 1 );

   // Shift a clone of the small-scale component image
   // after the morphological transformation. We then subtract
   // the shifted image from its parent image. In the resulting
   // image, those linear structures with a sudden change
   // of contrast over the column or row will result in a bright
   // line at the origin of the defect. This lets us
   // to detect the defective partial columns or rows.
   var shiftedSSImage = new Image( image.width,
                                   image.height,
                                   image.numberOfChannels,
                                   image.colorSpace,
                                   32, SampleType_Real );

   shiftedSSImage.apply( this.SSImage );
   detectColumns ? shiftedSSImage.shiftBy( 0, - ( imageShift ) )
                 : shiftedSSImage.shiftBy( imageShift, 0 );
   this.SSImage.apply( shiftedSSImage, ImageOp_Sub );
   shiftedSSImage.free();

   // Subtract again the large-scale components
   // of this processed small-scale component image.
   // This will give a cleaner result before binarizing.
   Console.writeln( "Isolating small-scale image components" );
   Console.flush();
   MultiscaleProcessing( this.SSImage, null, layersToRemove - 1 );

   // Binarize the image to isolate the partial line detection structures.
   Console.writeln( "Isolating the partial line defects" );
   Console.flush();
   var imageMedian = this.SSImage.median();
   var imageMAD = this.SSImage.MAD();
   this.SSImage.binarize( imageMedian + imageMAD * threshold );
   // Now, wew subtract the binarized the clipping mask from this processed
   // small-scale component image. This removes the surviving linear structures
   // coming from bright objects in the image.
   this.SSImage.apply( clippingMask, ImageOp_Sub );
   this.SSImage.truncate( 0, 1 );

   // We apply a closing operation with the same structuring element.
   // This process removes short surviving lines coming from
   // the image noise while keeping the long ones
   Console.writeln( "Applying morphological closing transformation" );
   Console.flush();
   this.SSImage.morphologicalTransformation( 2, structure, 0, 0, 1 );

   // Detect the defective partial rows or columns. We select
   // those columns or rows having a minimum number of white pixels.
   // The minimum is half of the image shift and it is calculated
   // by comparing the mean pixel value to the length of the line.
   // Then, we find the maximum position to set the origin of the defect.
   // The maximum position is the start of the white line but the origin
   // of the defect is the end of the white line. To solve this,
   // we first mirror the image.
   Console.noteln( "" );
   Console.noteln( "Detecting the partial line defects" );
   Console.flush();
   if ( detectColumns )
   {
      this.SSImage.mirrorVertical();
      var maxPixelPerp = this.SSImage.width - 1;
      var maxPixelPara = this.SSImage.height - 1;
      var lineRect = new Rect( 1, this.SSImage.height );
   }
   else
   {
      this.SSImage.mirrorHorizontal();
      var maxPixelPerp = this.SSImage.height - 1;
      var maxPixelPara = this.SSImage.width - 1;
      var lineRect = new Rect( this.SSImage.width, 1 );
   }

   this.columnOrRow = new Array;
   this.startPixel = new Array;
   this.endPixel = new Array;
   for ( let i = 0; i <= maxPixelPerp; ++i )
   {
      detectColumns ? lineRect.moveTo( i, 0 )
                    : lineRect.moveTo( 0, i );

      var lineMeanPixelValue = this.SSImage.mean( lineRect );
      // The equation at right sets the minimum length of the line
      // to trigger a defect detection.
      if ( lineMeanPixelValue > ( imageShift / ( ( maxPixelPara + 1 - imageShift * 2 ) * 2 ) ) )
      {
         this.columnOrRow.push( i )
         detectColumns  ? this.startPixel.push( maxPixelPara - parseInt( this.SSImage.maximumPosition( lineRect ).toArray()[1] ) )
                        : this.startPixel.push( maxPixelPara - parseInt( this.SSImage.maximumPosition( lineRect ).toArray()[0] ) );
         this.endPixel.push( maxPixelPara );
      }
   }

   detectColumns ? this.SSImage.mirrorVertical() : this.SSImage.mirrorHorizontal();

   this.SSImageWindow.mainView.beginProcess();
   this.SSImageWindow.mainView.image.apply( this.SSImage );
   this.SSImageWindow.mainView.endProcess();
   this.SSImageWindow.show();
}
//------------------------------------------------------------------------------
