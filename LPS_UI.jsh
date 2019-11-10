#include <pjsr/DataType.jsh>
#include <pjsr/StdButton.jsh>
#include <pjsr/FrameStyle.jsh>
#include <pjsr/TextAlign.jsh>
#include <pjsr/ButtonCodes.jsh>
#include <pjsr/NumericControl.jsh>
#include <pjsr/StdDialogCode.jsh>
#include <pjsr/FileMode.jsh>

showConfigDialog.prototype = new Dialog;

function toolTips() {
   this.cbTargetIsActiveImage =
               "You can apply the script to the currently active image or to a \n"+
               "set of images inside a directory. In the first case, the \n"+
               "targetIsActiveImage is used. In the latter case, specify the \n"+
               "data directory in the Input/Output directory field. inputDir \n"+
               "and outputDir need to be the same if you specify a file name postfix.";
   this.cbCloseFormerWorkingImages =
               "When running the script multiple times on the active image, you\n"+
               "can choose to automatically close the working images from the \n"+
               "last run by checking the Close former working images field; \n"+
               "only the target image will remain open.";;
   this.tbDir =
               "Directory for input files and output files. If you process an\n"+
               "entire directory, please change the image extension as well in\n"+
               "the \"targetImageExtension\" parameter and specify a filename \n"+
               "postfix to avoid overwriting the target images. Please don't \n"+
               "write the point before the file extension!\n"+
               "Be aware that the file extension is case sensitive.";
   this.btDir =
               "Choose directory ...";
   this.cbCorrectColumns =
               "If you want to correct a column pattern, check the \"correctColumns\"\n"+
               "field. If you want to correct a row pattern, dont check it.";
   this.cbCorrectEntireImage =
               "You can correct all the columns or rows in the images, or you\n"+
               "can correct only specific ones. This is controlled by the correct\n"+
               "entire image field. \n"+
               "In case you dont check it, you'll need to specify a defect list\n"+
               "file in the \"Partial defects file path\"";
   this.tbPartialDefectsFilePath =
               "This file should contain entire or partial rows or columns\n"+
               "created by the script LinearDefectDetection. Only these \n"+
               "columns/rows will be corrected if you check the field correct\n"+
               "entire image.\n"+
               "But, if you check correct entire image, you'll still need to \n"+
               "specify a defect file list to correct partial columns or rows, \n"+
               "since the script will correct only entire ones until it finds a\n"+
               "partial one in the defect list.";
   this.btPartialDefectsFilePath =
               "Choose directory ...";
   this.tbTargetImageExtension =
               "If you process an entire directory, please change the image \n"+
               "extension as well in the \"Target image extension\" field to \n"+
               "\"xisf\" and specify a filename postfix \"lps\" to avoid over-\n"+
               "writing the target images. \n"+
               "Please don't write the point before the file extension! Be aware\n"+
               "that the file extension is case sensitive.";
   this.neLayersToRemove =
               "The algorithm isolates the column or row structures from the\n"+
               "large-scale structures of the image. This scale separation is\n"+
               "defined by the \"layersToRemove\" field.\n"+
               "The parameter defines the scale size at wich we separate the\n"+
               "small and large structures, in a dyadic sequence. Thus, the \n"+
               "default value of 8 means that we will remove the structures up\n"+
               "to the scale of 2^(8-1) pixels (128). If you select 9 the first\n"+
               "8 layers in the internal MulscaleMedianTransform process will be\n"+
               "removed to the largescale structures. means only the Residuals \n"+
               "are left.";
   this.neRejectionLimit =
               "Function to perform a pixel rejection in the small-scale image\n"+
               "based on the pixel values of the target image. The rejected pixel\n"+
               "values are set to 1 in the small-scale image; this way, they will\n"+
               "be all rejected when performing the in-line pixel rejection in \n"+
               "the PatternSubtraction function since the rejection high value \n"+
               "will be always below 1";
   this.cbGlobalRejection =
               "Function to perform a pixel rejection in the small-scale image\n"+
               "based on the pixel values of the target image. The rejected pixel\n"+
               "values are set to 1 in the small-scale image; this way, they will\n"+
               "be all rejected when performing the in-line pixel rejection in \n"+
               "the PatternSubtraction function since the rejection high value \n"+
               "will be always below 1";
   this.neGlobalRejectionLimit =
               "After the scale separation, we also reject the bright pixels in \n"+
               "the columns or rows, mostly coming from the stars. This rejection\n"+
               "is performed by calculating the statistics of each column or row\n"+
               "and defining a rejection limit in sigmas with the rejectionLimit\n"+
               "property. A lower value means a more restrictive rejection.";
   this.backgroundReference =
               "If the above normalization  or global rejection are activated,\n"
               "you need to specify a background sky reference area by setting \n"+
               "the backgroundReferenceLeft, backgroundReferenceTop, \n"+
               "backgroundReferenceWidth and backgroundReferenceHeight properties.\n"+
               "The easier way to set these values is to create a preview and\n"+
               "check the numbers in PREVIEW > Modify Preview...";


}

function showConfigDialog(CONFIG) {
   let TT = new toolTips();
   this.__base__ = Dialog;
   this.__base__();
   var tbw = 163;
   var tbsw = 50;
   this.height = 600;
   this.width = 525;
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
      "<p><b>Linear Pattern Substraction V1.00</b>" +
      "<br/>" +
      "Script to correct residual column or row patterns in an image. "+
      "<br/><br/>" +
      "Copyright &copy; 2019 Vicent Peris (script) and Christian Liska (UI).</p>";
   }

//   this.targetIsActiveImage = false;
   this.cbTargetIsActiveImage = new CheckBox(this);
   with(this.cbTargetIsActiveImage) {
      enabled = true;
      text = "Target is active image";
      checked = CONFIG.targetIsActiveImage;
      toolTip = TT.cbTargetIsActiveImage;
      onCheck = function(checked) {
         //Console.writeln("cbTargetIsActiveImage pressed");
         CONFIG.targetIsActiveImage = checked;
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
         //Console.writeln("cbCloseFormerWorkingImages pressed");
         CONFIG.closeFormerWorkingImages = checked;
      }
   }

//   this.dir="J:/itelescope/ck18w020/20191031";
   this.lblDir = new Label(this);
   with (this.lblDir)
   {
      text = "Directory:";
      resize( tbw , height);
      textAlignment = TextAlign_Left;
   }
   this.tbDir = new Edit(this);
   var vdir = this.tbDir;
   with (this.tbDir)
   {
      resize( 250, 19 );
      text = CONFIG.dir;
      toolTip = TT.tbDir;
      onEditCompleted = function(text) {
         //Console.writeln("dir edited");
         CONFIG.dir = text;
      }
   }

   this.btDir = new PushButton(this);
   with(this.btDir) {
      text = "...";
      toolTip = TT.btDir;
      onClick = function() {
         var dirDialog = new GetDirectoryDialog();
         dirDialog.caption = "Choose path ...";
         if (!dirDialog.execute()) {
            return false;
         }
         CONFIG.dir = dirDialog.directory;
         vdir.text = CONFIG.dir;
      }
   }

//   this.correctColumns = true;
   this.cbCorrectColumns = new CheckBox(this);
   with(this.cbCorrectColumns) {
      enabled = true;
      text = "correct columns";
      checked = CONFIG.correctColumns;
      toolTip = TT.cbCorrectColumns;
      onCheck = function(checked) {
         // Console.writeln("cbCorrectColumns pressed");
         CONFIG.correctColumns = checked;
      }
   }

//   this.correctEntireImage = true;
   this.cbCorrectEntireImage = new CheckBox(this);
   with(this.cbCorrectEntireImage) {
      enabled = true;
      text = "correct entire image";
      checked = CONFIG.correctEntireImage;
      toolTip = TT.cbCorrectEntireImage;
      onCheck = function(checked) {
         // Console.writeln("cbCorrectEntireImage pressed");
         CONFIG.correctEntireImage = checked;
      }
   }

//   this.partialDefectsFilePath = "J:/main/detected-columns_5-sigma.txt";
   this.lblPartialDefectsFilePath = new Label(this);
   with (this.lblPartialDefectsFilePath)
   {
      text = "Partial defects file path:";
      resize( tbw , height);
      textAlignment = TextAlign_Left;
   }
   this.tbPartialDefectsFilePath = new Edit(this);
   var vPartialDefectsFilePath = this.tbPartialDefectsFilePath;
   with (this.tbPartialDefectsFilePath)
   {
      resize( 250, 19 );
      text = CONFIG.partialDefectsFilePath;
      toolTip = TT.tbPartialDefectsFilePath;
      onEditCompleted = function(text) {
         // Console.writeln("partial defects file path edited");
         CONFIG.partialDefectsFilePath = text;
      }
   }
   this.btPartialDefectsFilePath = new PushButton(this);
   with(this.btPartialDefectsFilePath) {
      text = "...";
      maxWidth = 30;
      onClick = function() {
         var dirDialog = new GetDirectoryDialog();
         dirDialog.caption = "Choose path ...";
         if (!dirDialog.execute()) {
            return false;
         }
         CONFIG.partialDefectsFilePath = dirDialog.directory;
         vPartialDefectsFilePath.text = dirDialog.directory;
      }
   }

//   this.targetImageExtension="xisf";
   this.lblTargetImageExtension = new Label(this);
   with (this.lblTargetImageExtension)
   {
      text = "Target image extension:";
      resize( tbw , height);
      textAlignment = TextAlign_Left;
   }
   this.tbTargetImageExtension = new Edit(this);
   with (this.tbTargetImageExtension)
   {
      resize( 50, 19 );
      text = CONFIG.targetImageExtension;
      toolTip = TT.tbTargetImageExtension;
      onTextUpdated = function(text) {
         Console.writeln("target image extension edited: " + text);
         CONFIG.targetImageExtension = text;
      }
   }
//   this.postfix="_lps";
   this.lblPostfix = new Label(this);
   with (this.lblPostfix)
   {
      text = "Postfix:";
      resize( tbw , height);
      textAlignment = TextAlign_Left;
   }
   this.tbPostfix = new Edit(this);
   with (this.tbPostfix)
   {
      resize( 50, 19 );
      width = 50;
      text = CONFIG.postfix;
      toolTip = TT.tbTargetImageExtension; //same as for targetImageExtension
      onTextUpdated = function(text) {
         // Console.writeln("postfix edited");
         CONFIG.postfix = text;
      }
   }

//   this.layersToRemove = 9;
   this.lblLayersToRemove = new Label(this);
   with (this.lblLayersToRemove)
   {
      text = "Layers to remove:";
      resize( tbw , height);
      textAlignment = TextAlign_Left;
   }
   this.neLayersToRemove = new NumericEdit(this);
   with (this.neLayersToRemove)
   {
      setRange(0, 15);
      resize( 50, 19 );
      setPrecision(0);
      setValue(CONFIG.layersToRemove);
      toolTip = TT.neLayersToRemove;
      onValueUpdated = function(value) {
         // Console.writeln("layers to remove edited");
         CONFIG.layersToRemove = value;
      }
   }

//   this.rejectionLimit = 3;
   this.lblRejectionLimit = new Label(this);
   with (this.lblRejectionLimit)
   {
      text = "Rejection limit:";
      resize( tbw , height);
      textAlignment = TextAlign_Left;
   }
   this.neRejectionLimit = new NumericEdit(this);
   with (this.neRejectionLimit)
   {
      resize( 50, 19 );
      setRange(0, 15);
      setPrecision(0);
      setValue(CONFIG.rejectionLimit);
      toolTip = TT.neRejectionLimit;
      onValueUpdated = function(value) {
         // Console.writeln("rejection limit edited");
         CONFIG.rejectionLimit = value;
      }
   }

//   this.smallScaleNormalization = true;
   this.cbSmallScaleNormalization = new CheckBox(this);
   with(this.cbSmallScaleNormalization) {
      enabled = true;
      text = "Small scale normalization";
      checked = CONFIG.smallScaleNormalization;
      onCheck = function(checked) {
         // Console.writeln("cbSmallScaleNormalization pressed");
         CONFIG.smallScaleNormalization = checked;
      }
   }

//   this.globalRejection = true;
   this.cbGlobalRejection = new CheckBox(this);
   with(this.cbGlobalRejection) {
      enabled = true;
      text = "Global rejection";
      checked = CONFIG.globalRejection;
      toolTip = TT.cbGlobalRejection;
      onCheck = function(checked) {
         // Console.writeln("cbGlobalRejection pressed");
         CONFIG.globalRejection = checked;
      }
   }

//   this.globalRejectionLimit = 7;
   this.lblGlobalRejectionLimit = new Label(this);
   with (this.lblGlobalRejectionLimit)
   {
      text = "Global rejection limit:";
      resize( tbw , height);
      textAlignment = TextAlign_Left;
   }
   this.neGlobalRejectionLimit = new NumericEdit(this);
   with (this.neGlobalRejectionLimit)
   {
      resize( 50, 19 );
      setRange(0, 15);
      setPrecision(0);
      setValue(CONFIG.globalRejectionLimit);
      toolTip = TT.neGlobalRejectionLimit;
      onValueUpdated = function(value) {
         // Console.writeln("global rejection limit edited");
         CONFIG.globalRejectionLimit = value;
      }
   }

//   this.backgroundReferenceLeft=1024;
   this.lblBackgroundReference = new Label(this);
   with (this.lblBackgroundReference) {
      text = "Background reference:";
      resize(tbw, height);
      textAlignment = TextAlign_Left;
   }

   this.lblBackgroundReferenceLeft = new Label(this);
   with (this.lblBackgroundReferenceLeft)
   {
      text = "left:";
      resize( tbsw , height);
      textAlignment = TextAlign_Left;
   }

   this.neBackgroundReferenceLeft = new NumericEdit(this);
   with (this.neBackgroundReferenceLeft)
   {
      resize( 50, 19 );
      setRange(0, 4096);
      setPrecision(0);
      setValue(CONFIG.backgroundReferenceLeft);
      toolTip = TT.backgroundReference;
      onValueUpdated = function(value) {
         // Console.writeln("background reference left edited");
         CONFIG.backgroundReferenceLeft = value;
      }
   }

//   this.backgroundReferenceTop=1024;
   this.lblBackgroundReferenceTop = new Label(this);
   with (this.lblBackgroundReferenceTop)
   {
      text = "top:";
      resize( tbsw , height);
      textAlignment = TextAlign_Left;
   }
   this.neBackgroundReferenceTop = new NumericEdit(this);
   with (this.neBackgroundReferenceTop)
   {
      resize( 50, 19 );
      setRange(0, 4096);
      setPrecision(0);
      setValue(CONFIG.backgroundReferenceTop);
      toolTip = TT.backgroundReference;
      onValueUpdated = function(value) {
         // Console.writeln("background reference top edited");
         CONFIG.backgroundReferenceTop = value;
      }
   }

//   this.backgroundReferenceWidth=400;
   this.lblBackgroundReferenceWidth = new Label(this);
   with (this.lblBackgroundReferenceWidth)
   {
      text = "width:";
      resize( tbsw , height);
      textAlignment = TextAlign_Left;
   }
   this.neBackgroundReferenceWidth = new NumericEdit(this);
   with (this.neBackgroundReferenceWidth)
   {
      resize( 50, 19 );
      setRange(0, 4096);
      setPrecision(0);
      setValue(CONFIG.backgroundReferenceWidth);
      toolTip = TT.backgroundReference;
      onValueUpdated = function(value) {
         // Console.writeln("background reference width edited");
         CONFIG.backgroundReferenceWidth = value;
      }
   }

//   this.backgroundReferenceHeight=400;
   this.lblBackgroundReferenceHeight = new Label(this);
   with (this.lblBackgroundReferenceHeight)
   {
      text = "height:";
      resize( tbsw , height);
      textAlignment = TextAlign_Left;
   }
   this.neBackgroundReferenceHeight = new NumericEdit(this);
   with (this.neBackgroundReferenceHeight)
   {
      resize( 50, 19 );
      setRange(0, 4096);
      setPrecision(0);
      setValue(CONFIG.backgroundReferenceHeight);
      toolTip = TT.backgroundReference;
      onValueUpdated = function(value) {
         // Console.writeln("background reference height edited");
         CONFIG.backgroundReferenceHeight = value;
      }
   }

   this.btnExecute = new PushButton(this);
   with(this.btnExecute) {
      text = "Execute";
      onClick = function(clicked) {
         dialog.done(0);
      }
   }

   // *** SIZERS ***
   this.dirFrame = new HorizontalSizer();
   with(this.dirFrame) {
      add(this.lblDir);
      addSpacing(10);
      add(this.tbDir);
      addSpacing(10);
      add(this.btDir);
   }

   this.partialDefectsFilePathFrame = new HorizontalSizer();
   with(this.partialDefectsFilePathFrame) {
      add(this.lblPartialDefectsFilePath);
      addSpacing(10);
      add(this.tbPartialDefectsFilePath);
      addSpacing(10);
      add(this.btPartialDefectsFilePath);
   }

   this.targetImageExtensionFrame = new HorizontalSizer();
   with(this.targetImageExtensionFrame) {
      add(this.lblTargetImageExtension);
      addSpacing(20);
      addStretch();
      add(this.tbTargetImageExtension);
      addSpacing(200);
   }

   this.postfixFrame = new HorizontalSizer();
   with(this.postfixFrame) {
      add(this.lblPostfix);
      addSpacing(20);
      addStretch();
      add(this.tbPostfix);
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

   this.globalRejectionLimitFrame = new HorizontalSizer();
   with(this.globalRejectionLimitFrame) {
      add(this.lblGlobalRejectionLimit);
      addStretch();
      add(this.neGlobalRejectionLimit);
      addSpacing(200);
   }

   this.backgroundReferenceTopFrame = new HorizontalSizer();
   with(this.backgroundReferenceTopFrame) {
      addSpacing(15);
      add(this.lblBackgroundReferenceTop);
      addStretch();
      add(this.neBackgroundReferenceTop);
      addSpacing(200);
   }

   this.backgroundReferenceLeftFrame = new HorizontalSizer();
   with(this.backgroundReferenceLeftFrame) {
      addSpacing(15);
      add(this.lblBackgroundReferenceLeft);
      addStretch();
      add(this.neBackgroundReferenceLeft);
      addSpacing(200);
   }

   this.backgroundReferenceWidthFrame = new HorizontalSizer();
   with(this.backgroundReferenceWidthFrame) {
      addSpacing(15);
      add(this.lblBackgroundReferenceWidth);
      addStretch();
      add(this.neBackgroundReferenceWidth);
      addSpacing(200);
   }

   this.backgroundReferenceHeightFrame = new HorizontalSizer();
   with(this.backgroundReferenceHeightFrame) {
      addSpacing(15);
      add(this.lblBackgroundReferenceHeight);
      addStretch();
      add(this.neBackgroundReferenceHeight);
      addSpacing(200);
   }

   this.layoutFrame = new VerticalSizer();
   with(this.layoutFrame) {
      margin = 16;
      add(this.lblIntro);
      addSpacing(5);
      add(this.cbTargetIsActiveImage);
      addSpacing(5);
      add(this.cbCloseFormerWorkingImages);
      addSpacing(5);
      add(this.dirFrame);
      addSpacing(5);
      add(this.cbCorrectColumns);
      add(this.cbCorrectEntireImage);
      addSpacing(5);
      add(this.partialDefectsFilePathFrame);
      addSpacing(5);
      add(this.targetImageExtensionFrame);
      addSpacing(5);
      add(this.postfixFrame);
      addSpacing(5);
      add(this.layersToRemoveFrame);
      addSpacing(5);
      add(this.rejectionLimitFrame);
      addSpacing(5);
      add(this.cbSmallScaleNormalization);
      addSpacing(10);
      add(this.cbGlobalRejection);
      addSpacing(5);
      add(this.globalRejectionLimitFrame);
      addSpacing(5);
      add(this.lblBackgroundReference);
      addSpacing(10);
      add(this.backgroundReferenceLeftFrame);
      add(this.backgroundReferenceTopFrame);
      add(this.backgroundReferenceWidthFrame);
      add(this.backgroundReferenceHeightFrame);
      addSpacing(32);
      add(this.btnExecute);
   }

   // *** MAIN FRAME ***

   this.allFrame = new Frame(this);
   this.allFrame.sizer = this.layoutFrame;
}
