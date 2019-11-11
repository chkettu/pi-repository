/*
 * LDD_UI_test.js
 */

#include "LDD_UI.jsh"

#define ID "LDD_UI"

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
   this.outputDir = "J:/main";
}

function main() {
   var cfg = new Config();

   var setts = Settings.read(ID + "/parms", DataType_String );
   if (setts != null)
   {
      cfg = JSON.parse(setts);
      Console.writeln("config from settings file");
   }

   var dialog = new showConfigDialog(cfg);
   dialog.onReturn = function(ret) {
      Console.writeln("Config from dialog");
      Console.writeln("==================");
      Console.writeln("detectColumns: "+cfg.detectColumns);
      Console.writeln("detectPartialLines: "+cfg.detectPartialLines);
      Console.writeln("imageShift: "+cfg.imageShift);
      Console.writeln("closeFormerWorkingImages: "+cfg.closeFormerWorkingImages);
      Console.writeln("layersToRemove: "+cfg.layersToRemove);
      Console.writeln("rejectionLimit: "+cfg.rejectionLimit);
      Console.writeln("detectionThreshold: "+cfg.detectionThreshold);
      Console.writeln("partialLineDetectionThreshold: "+cfg.partialLineDetectionThreshold);
      Console.writeln("outputDir: "+cfg.outputDir);
      Console.writeln();
   }
   let ret = dialog.execute();
   if (ret == 1) {
      Console.writeln("store config");
      Settings.write(ID + "/parms", DataType_String, JSON.stringify(cfg));
   }
}

main();
