#include <pjsr/DataType.jsh>
#include <pjsr/StdButton.jsh>
#include <pjsr/FrameStyle.jsh>
#include <pjsr/TextAlign.jsh>
#include <pjsr/ButtonCodes.jsh>
#include <pjsr/NumericControl.jsh>
#include <pjsr/StdDialogCode.jsh>
#include <pjsr/FileMode.jsh>

showDialog.prototype = new Dialog;

function config() {
   this.d = false;
}

function showDialog(CONFIG) {
   this.__base__ = Dialog;
   this.__base__();
   this.height = 600;
   this.width = 525;

   this.btnExecute = new PushButton(this);
   with(this.btnExecute) {
      text = "Execute";
      onClick = function(clicked) {
         CONFIG.d = true;
         dialog.done(0);
      }
   }
}

function main() {
   Console.writeln("Test!!");
   var cfg = new config();
   var dialog = new showDialog(cfg);
   dialog.onReturn = function(ret) {
      Console.writeln(cfg.d);
   }
	dialog.execute();
}

main();
