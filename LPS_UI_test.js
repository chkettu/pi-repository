#include "LPS_UI.jsh"

function Config()
{
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
}

function main() {
   Console.writeln("Test!!");
   var cfg = new Config();
   var dialog = new showDialog(cfg);
   dialog.onReturn = function(ret) {
      Console.writeln("Config from dialog");
      Console.writeln("==================");
      Console.writeln("targetIsActiveImage: "+cfg.targetIsActiveImage);
      Console.writeln("closeFormerWorkingImages: "+cfg.closeFormerWorkingImages);
      Console.writeln("dir: "+cfg.dir);
      Console.writeln("correctColumns: "+cfg.correctColumns);
      Console.writeln("correctEntireImage: "+cfg.correctEntireImage);
      Console.writeln("partialDefectsFilePath: "+cfg.partialDefectsFilePath);
      Console.writeln("targetImageExtension: "+cfg.targetImageExtension);
      Console.writeln("postfix: "+cfg.postfix);
      Console.writeln("layersToRemove: "+cfg.layersToRemove);
      Console.writeln("rejectionLimit: "+cfg.rejectionLimit);
      Console.writeln("smallScaleNormalization: "+cfg.smallScaleNormalization);
      Console.writeln("globalRejection: "+cfg.globalRejection);
      Console.writeln("globalRejectionLimit: "+cfg.globalRejectionLimit);
      Console.writeln("backgroundReferenceLeft: "+cfg.backgroundReferenceLeft);
      Console.writeln("backgroundReferenceTop: "+cfg.backgroundReferenceTop);
      Console.writeln("backgroundReferenceWidth: "+cfg.backgroundReferenceWidth);
      Console.writeln("backgroundReferenceHeight: "+cfg.backgroundReferenceHeight);
      Console.writeln();
   }
	dialog.execute();
}

main();
