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
   //TODO add tooltips
   this.cbDetectColumns =
               "TOOLTIP cbDetectColumns";
   this.cbDetectPartialLines =
               "TOOLTIP cbDetectPartialLines";
   this.cbCloseFormerWorkingImages =
               "TOOLTIP cbCloseFormerWorkingImages";
   this.tbOutputDir =
               "TOOLTIP tbOutputDir";
   this.btOutputDir =
               "Choose directory ..";
   this.neImageShift =
               "TOOLTIP neImageShift";
   this.cbCloseFormerWorkingImages =
               "TOOLTIP cbCloseFormerWorkingImages";
   this.neImageShift =
               "TOOLTIP neImageShift";
   this.neLayersToRemove =
               "TOOLTIP neLayersToRemove";
   this.neRejectionLimit =
               "TOOLTIP neRejectionLimit";
   this.neDetectionThreshold =
               "TOOLTIP neDetectionThreshold";
   this.nePartialLineDetectionThreshold =
               "TOOLTIP nePartialLineDetectionThreshold";
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
