#include <pjsr/DataType.jsh>
#include <pjsr/StdButton.jsh>
#include <pjsr/FrameStyle.jsh>
#include <pjsr/TextAlign.jsh>
#include <pjsr/ButtonCodes.jsh>
#include <pjsr/NumericControl.jsh>
#include <pjsr/StdDialogCode.jsh>
#include <pjsr/FileMode.jsh>

showDialog.prototype = new Dialog;

function showDialog(CONFIG) {
   this.__base__ = Dialog;
   this.__base__();
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
      checked = false;
      onCheck = function(checked) {
         Console.writeln("cbTargetIsActiveImage pressed");
         this.targetIsActiveImage = checked;
      }
   }

//   this.closeFormerWorkingImages = true;
   this.cbCloseFormerWorkingImages = new CheckBox(this);
   with(this.cbCloseFormerWorkingImages) {
      enabled = true;
      text = "Close former working images";
      checked = true;
      onCheck = function(checked) {
         Console.writeln("cbCloseFormerWorkingImages pressed");
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
   with (this.tbDir)
   {
      resize( 250, 19 );
      text = "J:/itelescope/ck18w020/20191031";
      onEditCompleted = function(text) {
         Console.writeln("dir edited");
         CONFIG.dir = text;
      }
   }

   this.btDir = new PushButton(this);
   with(this.btDir) {
      text = "...";
      onClick = function() {
         var dirDialog = new GetDirectoryDialog();
         dirDialog.caption = "Choose path ...";
         if (!dirDialog.execute()) {
            return false;
         }
         CONFIG.dir = dirDialog.directory;
         tbDir.text = dirDialog.directory;
      }
   }

//   this.correctColumns = true;
   this.cbCorrectColumns = new CheckBox(this);
   with(this.cbCorrectColumns) {
      enabled = true;
      text = "correct columns";
      checked = true;
      onCheck = function(checked) {
         Console.writeln("cbCorrectColumns pressed");
         CONFIG.correctColumns = checked;
      }
   }

//   this.correctEntireImage = true;
   this.cbCorrectEntireImage = new CheckBox(this);
   with(this.cbCorrectEntireImage) {
      enabled = true;
      text = "correct entire image";
      checked = true;
      onCheck = function(checked) {
         Console.writeln("cbCorrectEntireImage pressed");
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
   with (this.tbPartialDefectsFilePath)
   {
      resize( 250, 19 );
      text = "J:/main/detected-columns_5-sigma.txt";
      onEditCompleted = function(text) {
         Console.writeln("partial defects file path edited");
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
         tbPartialDefectsFilePath.text = dirDialog.directory;
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
      text = "xisf";
      onEditCompleted = function(text) {
         Console.writeln("target image extension edited");
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
      text = "_lps";
      onEditCompleted = function(text) {
         Console.writeln("postfix edited");
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
      setValue(9);
      onValueUpdated = function(value) {
         Console.writeln("layers to remove edited");
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
      setValue(3);
      onValueUpdated = function(value) {
         Console.writeln("rejection limit edited");
         CONFIG.rejectionLimit = value;
      }
   }

//   this.smallScaleNormalization = true;
   this.cbSmallScaleNormalization = new CheckBox(this);
   with(this.cbSmallScaleNormalization) {
      enabled = true;
      text = "Small scale normalization";
      checked = true;
      onCheck = function(checked) {
         Console.writeln("cbSmallScaleNormalization pressed");
         CONFIG.smallScaleNormalization = checked;
      }
   }

//   this.globalRejection = true;
   this.cbGlobalRejection = new CheckBox(this);
   with(this.cbGlobalRejection) {
      enabled = true;
      text = "Global rejection";
      checked = true;
      onCheck = function(checked) {
         Console.writeln("cbGlobalRejection pressed");
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
      setValue(7);
      onValueUpdated = function(value) {
         Console.writeln("global rejection limit edited");
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
      setValue(1024);
      onValueUpdated = function(value) {
         Console.writeln("background reference left edited");
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
      setValue(1024);
      onValueUpdated = function(value) {
         Console.writeln("background reference top edited");
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
      setValue(400);
      onValueUpdated = function(value) {
         Console.writeln("background reference width edited");
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
      setValue(400);
      onValueUpdated = function(value) {
         Console.writeln("background reference height edited");
         CONFIG.backgroundReferenceHeight = value;
      }
   }

   this.btnExecute = new PushButton(this);
   with(this.btnExecute) {
      text = "Execute";
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

function main() {
   Console.writeln("Test!!");

   var dialog = new showDialog();
	dialog.execute();
}

main();
