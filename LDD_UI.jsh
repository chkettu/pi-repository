/*
 * LDD_UI.jsh
 */

#include <pjsr/DataType.jsh>
#include <pjsr/StdButton.jsh>
#include <pjsr/FrameStyle.jsh>
#include <pjsr/TextAlign.jsh>
#include <pjsr/ButtonCodes.jsh>
#include <pjsr/NumericControl.jsh>
#include <pjsr/StdDialogCode.jsh>
#include <pjsr/FileMode.jsh>

showConfigDialog.prototype = new Dialog;

function Tooltips() {
   this.cbDetectColumns =
               "If you want to correct a column pattern, set the \"detectColumns\" \n"+
               "property to true. If you want to correct a row pattern, set it to false.";
   this.cbDetectPartialLines =
               "The partial line defect detection algorithm works by shifting a\n"+
                "clone of the target image and subtracting one from the other. \n"+
                "This leaves short lines in the image that indicate the origin\n"+
                "of the defects. A too small value will confuse these lines with\n"+
                "residual noise in the processed image. \n"+
                "Usually a value between 50 - 100 works fine.\n"+
                "You can separately configure the detection threshold of the partial\n"+
                "line defects with the partialLineDetectionThreshold property.";
   this.neImageShift =
               "The partial line defect detection algorithm works by shifting a \n"+
               "clone of the target image and subtracting one from the other. This\n"+
               "leaves short lines in the image that indicate the origin of the \n"+
               "defects. A too small value will confuse these lines with residual \n"+
               "noise in the processed image.\n"+
               "Usually a value between 50 - 100 works fine.";
   this.cbCloseFormerWorkingImages =
               "When running the script multiple times on the active image, you\n"+
               +can choose to automatically close the working images from the last\n"+
               "run by setting the closeFormerWorkingImages property to true; only\n"+
               "the target image will remain open.";
   this.neLayersToRemove =
               "The algorithm isolates the small-scale structures from the large-scale\n"+
               "structures in the pattern and target images before finding the matching\n"+
               "scaling factor. This scale separation is enabled with the removeScales\n"+
               "property.\n"+
               "This scale separation is defined by the layersToRemove property. This \n"+
               "property defines the scale size at wich we separate the small and large \n"+
               "structures, in a dyadic sequence. Thus, a value of 8 means that we will \n"+
               "remove the structures up to the scale of 2^(8-1) pixels (128).";
   this.neRejectionLimit =
               "After the scale separation, we also reject the bright pixels in\n"+
               "the columns or rows, mostly coming from the stars. This rejection \n"+
               "is performed by calculating the statistics of each column or row \n"+
               "and defining a rejection limit in sigmas with the rejectionLimit \n"+
               "property. A lower value means a more restrictive rejection.";
   this.neDetectionThreshold =
               "The linear defect detection is driven by the detectionThreshold property.\n"+
               "Its value is in sigmas, so a lower value will detect more defects.";
   this.nePartialLineDetectionThreshold =
               "You can separately configure the detection threshold of the partial line\n"+
               "defects with the partialLineDetectionThreshold property. \n"+
               "Its value is also in sigmas, so a lower value will detect more defects.";
   this.tbOutputDir =
               "Directory where a CosmeticCorrection-compatible defect table could be\n"+
               "written by the script.\n"+
               "Be careful not to add a slash \"/\" after the directory name.\n"+
               "So entering \"C:/LDD\" is correct and will write the defect column table\n";
   this.btOutputDir =
               "Choose directory ..";
}

function showConfigDialog(CONFIG) {
   let TT = new Tooltips();
   this.__base__ = Dialog;
   this.__base__();
   var tbw = 163;
   var tbsw = 50;
   this.height = 360;
   this.width = 450;
   this.restyle();
   this.labelWidth = this.font.width("Maximum magnitude:M");
   this.userResizable = false;
   var dialog 			= this;

   this.lblIntro = new Label(this);
   with(this.lblIntro) {
      frameStyle = FrameStyle_Box;
      minWidth = 47 * font.width('M');
      wordWrapping = true;
      useRichText = true;
      text =
      "<p><b>Linear Defect Detection V1.00</b>" +
      "<br/>" +
      "Script to detect defective columns or rows in a reference image. "+
      "<br/><br/>" +
      "Copyright &copy; 2019 Vicent Peris (script) and Christian Liska (UI).</p>";
   }

   //   this.detectColumns = true;
   this.cbDetectColumns = new CheckBox(this);
   with(this.cbDetectColumns) {
      enabled = true;
      text = "Detect columns";
      checked = CONFIG.detectColumns;
      toolTip = TT.cbDetectColumns;
      onCheck = function(checked) {
         CONFIG.detectColumns = checked;
      }
   }

   //   this.detectPartialLines = true;
   this.cbDetectPartialLines = new CheckBox(this);
   with(this.cbDetectPartialLines) {
      enabled = true;
      text = "detect partial lines";
      checked = CONFIG.detectPartialLines;
      toolTip = TT.cbDetectPartialLines;
      onCheck = function(checked) {
         CONFIG.detectPartialLines = checked;
      }
   }

   //   this.imageShift = 100;
   this.lblImageShift = new Label(this);
   with (this.lblImageShift)
   {
      text = "image shift:";
      resize( tbsw , height);
      textAlignment = TextAlign_Left;
   }
   this.neImageShift = new NumericEdit(this);
   with (this.neImageShift)
   {
      resize( 50, 19 );
      setRange(0, 4096);
      setPrecision(0);
      setValue(CONFIG.imageShift);
      toolTip = TT.neImageShift;
      onValueUpdated = function(value) {
         CONFIG.imageShift = value;
      }
   }

   //   this.closeFormerWorkingImages = true;
   this.cbCloseFormerWorkingImages = new CheckBox(this);
   with(this.cbCloseFormerWorkingImages) {
      enabled = true;
      text = "Close former working images";
      checked = CONFIG.closeFormerWorkingImages;
      toolTip = TT.cbCloseFormerWorkingImages;
      onCheck = function(checked) {
         CONFIG.closeFormerWorkingImages = checked;
      }
   }

   //   this.layersToRemove = 9;
   this.lblLayersToRemove = new Label(this);
   with (this.lblLayersToRemove)
   {
      text = "layers to remove:";
      resize( tbsw , height);
      textAlignment = TextAlign_Left;
   }
   this.neLayersToRemove = new NumericEdit(this);
   with (this.neLayersToRemove)
   {
      resize( 50, 19 );
      setRange(0, 4096);
      setPrecision(0);
      setValue(CONFIG.layersToRemove);
      toolTip = TT.neLayersToRemove;
      onValueUpdated = function(value) {
         CONFIG.layersToRemove = value;
      }
   }
   //   this.rejectionLimit = 3;
   this.lblRejectionLimit = new Label(this);
   with (this.lblRejectionLimit)
   {
      text = "rejection limit:";
      resize( tbsw , height);
      textAlignment = TextAlign_Left;
   }
   this.neRejectionLimit = new NumericEdit(this);
   with (this.neRejectionLimit)
   {
      resize( 50, 19 );
      setRange(0, 4096);
      setPrecision(0);
      setValue(CONFIG.rejectionLimit);
      toolTip = TT.neRejectionLimit;
      onValueUpdated = function(value) {
         CONFIG.rejectionLimit = value;
      }
   }

   //   this.detectionThreshold = 5;
   this.lblDetectionThreshold = new Label(this);
   with (this.lblDetectionThreshold)
   {
      text = "detection threshold:";
      resize( tbsw , height);
      textAlignment = TextAlign_Left;
   }
   this.neDetectionThreshold = new NumericEdit(this);
   with (this.neDetectionThreshold)
   {
      resize( 50, 19 );
      setRange(0, 4096);
      setPrecision(0);
      setValue(CONFIG.detectionThreshold);
      toolTip = TT.neDetectionThreshold;
      onValueUpdated = function(value) {
         CONFIG.detectionThreshold = value;
      }
   }

   //   this.partialLineDetectionThreshold = 5;
   this.lblPartialLineDetectionThreshold = new Label(this);
   with (this.lblPartialLineDetectionThreshold)
   {
      text = "partial line detection threshold:";
      resize( tbsw , height);
      textAlignment = TextAlign_Left;
   }
   this.nePartialLineDetectionThreshold = new NumericEdit(this);
   with (this.nePartialLineDetectionThreshold)
   {
      resize( 50, 19 );
      setRange(0, 4096);
      setPrecision(0);
      setValue(CONFIG.partialLineDetectionThreshold);
      toolTip = TT.nePartialLineDetectionThreshold;
      onValueUpdated = function(value) {
         CONFIG.partialLineDetectionThreshold = value;
      }
   }

   //   this.outputDir = "J:/main";
   this.lblOutputDir = new Label(this);
   with (this.lblOutputDir)
   {
      text = "Output directory:";
      resize( tbw , height);
      textAlignment = TextAlign_Left;
   }
   this.tbOutputDir = new Edit(this);
   var vdir = this.tbOutputDir;
   with (this.tbOutputDir)
   {
      resize( 250, 19 );
      text = CONFIG.outputDir;
      toolTip = TT.tbOutputDir;
      onTextUpdated = function(t) {
         CONFIG.outputDir = t;
         text = t;
      }
   }

   this.btOutputDir = new PushButton(this);
   with(this.btOutputDir) {
      text = "...";
      toolTip = TT.btOutputDir;
      onClick = function() {
         var dirDialog = new GetDirectoryDialog();
         dirDialog.caption = "Choose path ...";text
         if (!dirDialog.execute()) {
            return false;
         }
         CONFIG.outputDir = dirDialog.directory;
         vdir.text = CONFIG.outputDir;
         return true;
      }
   }

   this.btnExecute = new PushButton(this);
   with(this.btnExecute) {
      text = "Execute";
      onClick = function(clicked) {
         dialog.done(1);
      }
   }

   // *** Sizers ***
   this.imageShiftFrame = new HorizontalSizer();
   with(this.imageShiftFrame) {
      add(this.lblImageShift);
      addStretch();
      add(this.neImageShift);
      addSpacing(200);
   }

   this.layersToRemoveFrame = new HorizontalSizer();
   with(this.layersToRemoveFrame) {
      add(this.lblLayersToRemove);
      addStretch();
      add(this.neLayersToRemove);
      addSpacing(200);
   }

   this.rejectionLimitFrame = new HorizontalSizer();
   with(this.rejectionLimitFrame) {
      add(this.lblRejectionLimit);
      addStretch();
      add(this.neRejectionLimit);
      addSpacing(200);
   }

   this.detectionThresholdFrame = new HorizontalSizer();
   with(this.detectionThresholdFrame) {
      add(this.lblDetectionThreshold);
      addStretch();
      add(this.neDetectionThreshold);
      addSpacing(200);
   }

   this.partialLineDetectionThresholdFrame = new HorizontalSizer();
   with(this.partialLineDetectionThresholdFrame) {
      add(this.lblPartialLineDetectionThreshold);
      addStretch();
      add(this.nePartialLineDetectionThreshold);
      addSpacing(200);
   }

   this.outputDirFrame = new HorizontalSizer();
   with(this.outputDirFrame) {
      add(this.lblOutputDir);
      addSpacing(10);
      add(this.tbOutputDir);
      addSpacing(10);
      add(this.btOutputDir);
   }

   this.layoutFrame = new VerticalSizer();
   with(this.layoutFrame) {
      margin = 16;
      add(this.lblIntro);
      addSpacing(5);
      add(this.cbDetectColumns);
      addSpacing(5);
      add(this.cbDetectPartialLines);
      addSpacing(5);
      add(this.imageShiftFrame);
      addSpacing(5);
      add(this.cbCloseFormerWorkingImages);
      addSpacing(5);
      add(this.layersToRemoveFrame);
      addSpacing(5);
      add(this.rejectionLimitFrame);
      addSpacing(5);
      add(this.detectionThresholdFrame);
      addSpacing(5);
      add(this.partialLineDetectionThresholdFrame);
      addSpacing(5);
      add(this.outputDirFrame);
      addSpacing(32);
      add(this.btnExecute);
   }

   // *** MAIN FRAME ***

   this.allFrame = new Frame(this);
   this.allFrame.sizer = this.layoutFrame;
}
