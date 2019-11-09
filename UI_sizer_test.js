#include <pjsr/DataType.jsh>
#include <pjsr/StdButton.jsh>
#include <pjsr/FrameStyle.jsh>
#include <pjsr/TextAlign.jsh>
#include <pjsr/ButtonCodes.jsh>
#include <pjsr/NumericControl.jsh>
#include <pjsr/StdDialogCode.jsh>
#include <pjsr/FileMode.jsh>
#include <pjsr/Sizer.jsh>

showDialog.prototype = new Dialog;

function showDialog() {
   this.__base__ = Dialog;
   this.__base__();
   this.minHeight = 400;
   this.minWidth = 500;

   this.buttonAdd = new PushButton(this);
   with(this.buttonAdd) {
      text = "Add";
   }

   this.buttonDel = new PushButton(this);
   with(this.buttonDel) {
      text = "Delete";
   }

   this.buttonExit = new PushButton(this);
   with(this.buttonExit) {
      text = "Exit";
   }

   this.lblOk = new Label(this);
   with(this.lblOk) {
      text = "Okay: ";
   }

   this.buttonsFrameLeft = new VerticalSizer();
   with(this.buttonsFrameLeft) {
      margin = 16;
      add (this.buttonAdd);
      addSpacing(4);
      add(this.buttonDel);
      addStretch();
      addSpacing(16);
      add(this.buttonExit);
   }

   this.frameRight = new VerticalSizer();
   with(this.frameRight) {
      margin = 16;
      add(this.lblOk);
   }

   this.buttonsFrame = new HorizontalSizer();
   with(this.buttonsFrame) {
      margin = 16;
      add(this.buttonsFrameLeft);
      addStretch();
      addSpacing(32);
      add(this.frameRight);
   }

   this.allFrame = new Frame(this);
   with(this.allFrame) {
      sizer = this.buttonsFrame;
   }

/*
   this.labelWidth = this.font.width("Maximum magnitude:M");
   this.userResizable = false;
   var dialog 			= this;

   this.lblOk = new Label(this);
   with(this.lblOk) {
      text = "Okay: ";
   }

   this.btOk = new PushButton(this);
   with(this.btOk) {
      text = "OK";
      maxWidth = 50;

      onClick = function() {
         Console.writeln("ok");
      }
   }

   this.btCancel = new PushButton(this);
   with(this.btCancel) {
      text = "Cancel";
      maxWidth = 50;

      onClick = function() {
         Console.writeln("cancel");
      }
   }

   this.sizer_1 = new VerticalSizer;
   with(this.sizer_1) {
      margin = 16;
      add(this.btCancel);
      addSpacing(32);
      add(this.btOk);
   }


   this.lblSizer = new HorizontalSizer;
   with(this.lblSizer) {
      margin = 6;
      add(this.sizer_1);
      addStretch();
      addSpacing(32);
      add(this.lblOk);
   }
*/
/*
   this.btSizer = new HorizontalSizer;
   with(this.btSizer) {
      margin = 6;
      spacing = 30;
      add(this.btOk);
   }

   this.sizer = new VerticalSizer;
   with(this.sizer) {
      margin = 6;
      spacing = 6;
      add(this.lblSizer);
      add(this.btSizer);
   }
   */
}



function main() {
   Console.writeln("Test!!");

   var dialog = new showDialog();
	dialog.execute();
}

main();
