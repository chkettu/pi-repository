#include <pjsr/DataType.jsh>
#include <pjsr/StdButton.jsh>
#include <pjsr/FrameStyle.jsh>
#include <pjsr/TextAlign.jsh>
#include <pjsr/ButtonCodes.jsh>
#include <pjsr/NumericControl.jsh>
#include <pjsr/StdDialogCode.jsh>
#include <pjsr/FileMode.jsh>

/*
 GroupBox
 VerticalSizer
 HorizontalSizer
 Label
 Dialog
 */

/*
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
*/

showDialog.prototype = new Dialog;

function showDialog() {
   var x = 8;
   var y = 10;
   var ls = 18;
   var tbw = 163;
   var tbsw = 50;
   var tbs = 2;

   Console.writeln("showDialog");
   this.__base__ = Dialog;
   this.__base__();
   this.restyle();
   this.labelWidth = this.font.width("Maximum magnitude:M");
   this.userResizable = false;
   var dialog 			= this;

   var lblIntro = new Label(this);
   with(lblIntro) {
      frameStyle = FrameStyle_Box;
      position = new Point(x, y);
      margin = 6;
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
   var cbTargetIsActiveImage = new CheckBox(this);
   with(cbTargetIsActiveImage) {
      enabled = true;
      y = lblIntro.frameRect.bottom + ls + 25;
      position = new Point(x, y);
      text = "Target is active image";
      checked = false;
      onCheck = function(checked) {
         Console.writeln("cbTargetIsActiveImage pressed");
         this.targetIsActiveImage = checked;
      }
   }

//   this.closeFormerWorkingImages = true;
   var cbCloseFormerWorkingImages = new CheckBox(this);
   with(cbCloseFormerWorkingImages) {
      enabled = true;
      y = cbTargetIsActiveImage.position.y + ls;
      position = new Point(x, y);
      text = "Close former working images";
      checked = true;
      onCheck = function(checked) {
         Console.writeln("cbCloseFormerWorkingImages pressed");
         this.closeFormerWorkingImages = checked;
      }
   }

//   this.dir="J:/itelescope/ck18w020/20191031";
   var lblDir = new Label(this);
   with (lblDir)
   {
      text = "Directory:";
      resize( tbw , height);
      y = cbCloseFormerWorkingImages.position.y + ls;
      position = new Point(x, y);
      textAlignment = TextAlign_Left;
   }
   var tbDir = new Edit(this);
   with (tbDir)
   {
      position = new Point(lblDir.frameRect.right + 8, y);
      resize( 250, 19 );
      text = "J:/itelescope/ck18w020/20191031";
      onEditCompleted = function(text) {
         Console.writeln("dir edited");
         this.dir = text;
      }
   }

//   this.correctColumns = true;
   var cbCorrectColumns = new CheckBox(this);
   with(cbCorrectColumns) {
      enabled = true;
      y = lblDir.position.y + ls;
      position = new Point(x, y);
      text = "correct columns";
      checked = true;
      onCheck = function(checked) {
         Console.writeln("cbCorrectColumns pressed");
         this.correctColumns = checked;
      }
   }

//   this.correctEntireImage = true;
   var cbCorrectEntireImage = new CheckBox(this);
   with(cbCorrectEntireImage) {
      enabled = true;
      y = cbCorrectColumns.position.y + ls;
      position = new Point(x, y);
      text = "correct entire image";
      checked = true;
      onCheck = function(checked) {
         Console.writeln("cbCorrectEntireImage pressed");
         this.correctEntireImage = checked;
      }
   }

//   this.partialDefectsFilePath = "J:/main/detected-columns_5-sigma.txt";
   var lblPartialDefectsFilePath = new Label(this);
   with (lblPartialDefectsFilePath)
   {
      text = "Partial defects file path:";
      resize( tbw , height);
      y = cbCorrectEntireImage.position.y + ls;
      position = new Point(x, y);
      textAlignment = TextAlign_Left;
   }
   var tbPartialDefectsFilePath = new Edit(this);
   with (tbPartialDefectsFilePath)
   {
      position = new Point(lblPartialDefectsFilePath.frameRect.right + 8, y);
      resize( 250, 19 );
      text = "J:/main/detected-columns_5-sigma.txt";
      onEditCompleted = function(text) {
         Console.writeln("partial defects file path edited");
         this.partialDefectsFilePath = text;
      }
   }

//   this.targetImageExtension="xisf";
   var lblTargetImageExtension = new Label(this);
   with (lblTargetImageExtension)
   {
      text = "Target image extension:";
      resize( tbw , height);
      y = lblPartialDefectsFilePath.position.y + ls;
      position = new Point(x, y);
      textAlignment = TextAlign_Left;
   }
   var tbTargetImageExtension = new Edit(this);
   with (tbTargetImageExtension)
   {
      position = new Point(lblTargetImageExtension.frameRect.right + 8, y);
      resize( 50, 19 );
      text = "xisf";
      onEditCompleted = function(text) {
         Console.writeln("target image extension edited");
         this.targetImageExtension = text;
      }
   }

//   this.postfix="_lps";
   var lblPostfix = new Label(this);
   with (lblPostfix)
   {
      text = "Postfix:";
      resize( tbw , height);
      y = lblTargetImageExtension.position.y + ls;
      position = new Point(x, y);
      textAlignment = TextAlign_Left;
   }
   var tbPostfix = new Edit(this);
   with (tbPostfix)
   {
      position = new Point(lblPostfix.frameRect.right + 8, y);
      resize( 50, 19 );
      text = "_lps";
      onEditCompleted = function(text) {
         Console.writeln("postfix edited");
         this.postfix = text;
      }
   }
//   this.layersToRemove = 9;
   var lblLayersToRemove = new Label(this);
   with (lblLayersToRemove)
   {
      text = "Layers to remove:";
      resize( tbw , height);
      y = lblPostfix.position.y + ls+ 20;
      position = new Point(x, y);
      textAlignment = TextAlign_Left;
   }
   var tbLayersToRemove = new NumericControl(this);
   with (tbLayersToRemove)
   {
      integer = true;
      position = new Point(lblLayersToRemove.frameRect.right + 8, y);
      resize( 50, 19 );
      value = 9;
      onEditCompleted = function(value) {
         Console.writeln("layers to remove edited");
         this.layersToRemove = value;
      }
   }

//   this.rejectionLimit = 3;
   var lblRejectionLimit = new Label(this);
   with (lblRejectionLimit)
   {
      text = "Rejection limit:";
      resize( tbw , height);
      y = lblLayersToRemove.position.y + ls;
      position = new Point(x, y);
      textAlignment = TextAlign_Left;
   }
   var tbRejectionLimit = new NumericControl(this);
   with (tbRejectionLimit)
   {
      integer = true;
      position = new Point(lblRejectionLimit.frameRect.right + 8, y);
      resize( 50, 19 );
      value = 3;
      onEditCompleted = function(value) {
         Console.writeln("rejection limit edited");
         this.rejectionLimit = value;
      }
   }

//   this.smallScaleNormalization = true;
   var cbSmallScaleNormalization = new CheckBox(this);
   with(cbSmallScaleNormalization) {
      enabled = true;
      y = lblRejectionLimit.position.y + ls;
      position = new Point(x, y);
      text = "Small scale normalization";
      checked = true;
      onCheck = function(checked) {
         Console.writeln("cbSmallScaleNormalization pressed");
      }
   }

//   this.globalRejection = true;
   var cbGlobalRejection = new CheckBox(this);
   with(cbGlobalRejection) {
      enabled = true;
      y = cbSmallScaleNormalization.position.y + ls + 20;
      position = new Point(x, y);
      text = "Global rejection";
      checked = true;
      onCheck = function(checked) {
         Console.writeln("cbGlobalRejection pressed");
      }
   }

//   this.globalRejectionLimit = 7;
   var lblGlobalRejectionLimit = new Label(this);
   with (lblGlobalRejectionLimit)
   {
      text = "Global rejection limit:";
      resize( tbw , height);
      y = cbGlobalRejection.position.y + ls;
      position = new Point(x, y);
      textAlignment = TextAlign_Left;
   }
   var tbGlobalRejectionLimit = new Edit(this);
   with (tbGlobalRejectionLimit)
   {
      position = new Point(lblGlobalRejectionLimit.frameRect.right + 8, y);
      resize( 50, 19 );
      text = "7";
      onEditCompleted = function(value) {
         Console.writeln("global rejection limit edited");
         this.globalRejectionLimit = value;
      }
   }

//   this.backgroundReferenceLeft=1024;
   var lblBackgroundReference = new Label(this);
   with (lblBackgroundReference) {
      text = "Background reference:";
      resize(tbw, height);
      y = lblGlobalRejectionLimit.position.y + ls + 20;
      position = new Point(x, y);
      textAlignment = TextAlign_Left;
   }

   var lblBackgroundReferenceLeft = new Label(this);
   with (lblBackgroundReferenceLeft)
   {
      text = "left:";
      resize( tbsw , height);
      y = lblBackgroundReference.position.y + ls;
      position = new Point(x + 20, y);
      textAlignment = TextAlign_Left;
   }
   var tbBackgroundReferenceLeft = new Edit(this);
   with (tbBackgroundReferenceLeft)
   {
      position = new Point(lblBackgroundReferenceLeft.frameRect.right + 8, y);
      resize( 50, 19 );
      text = "1024";
      onEditCompleted = function(value) {
         Console.writeln("background reference left edited");
         this.backgroundReferenceLeft = value;
      }
   }

//   this.backgroundReferenceTop=1024;
   var lblBackgroundReferenceTop = new Label(this);
   with (lblBackgroundReferenceTop)
   {
      text = "top:";
      resize( tbsw , height);
      y = lblBackgroundReferenceLeft.position.y + ls;
      position = new Point(x + 20, y);
      textAlignment = TextAlign_Left;
   }
   var tbBackgroundReferenceTop = new Edit(this);
   with (tbBackgroundReferenceTop)
   {
      position = new Point(lblBackgroundReferenceTop.frameRect.right + 8, y);
      resize( 50, 19 );
      text = "1024"
      onEditCompleted = function(value) {
         Console.writeln("background reference top edited");
         this.backgroundReferenceTop = value;
      }
   }

//   this.backgroundReferenceWidth=400;
   var lblBackgroundReferenceWidth = new Label(this);
   with (lblBackgroundReferenceWidth)
   {
      text = "width:";
      resize( tbsw , height);
      y = lblBackgroundReferenceTop.position.y + ls;
      position = new Point(x + 20, y);
      textAlignment = TextAlign_Left;
   }
   var tbBackgroundReferenceWidth = new Edit(this);
   with (tbBackgroundReferenceWidth)
   {
      position = new Point(lblBackgroundReferenceWidth.frameRect.right + 8, y);
      resize( 50, 19 );
      text = "400";
      onEditCompleted = function(value) {
         Console.writeln("background reference width edited");
         this.backgroundReferenceWidth = value;
      }
   }

//   this.backgroundReferenceHeight=400;
   var lblBackgroundReferenceHeight = new Label(this);
   with (lblBackgroundReferenceHeight)
   {
      text = "height:";
      resize( tbsw , height);
      y = lblBackgroundReferenceWidth.position.y + ls;
      position = new Point(x+20, y);
      textAlignment = TextAlign_Left;
   }
   var tbBackgroundReferenceHeight = new Edit(this);
   with (tbBackgroundReferenceHeight)
   {
      position = new Point(lblBackgroundReferenceHeight.frameRect.right + 8, y);
      resize( 50, 19 );
      text = "400";
      onEditCompleted = function(value) {
         Console.writeln("background reference height edited");
         this.backgroundReferenceHeight = value;
      }
   }

   var btnExecute = new PushButton(this);
   with(btnExecute) {
      text = "Execute";
      position = new Point(x + 310, tbBackgroundReferenceHeight.position.y + ls);
   }
}

function main() {
   Console.writeln("Test!!");

   var dialog = new showDialog();
	dialog.execute();
}

main();
