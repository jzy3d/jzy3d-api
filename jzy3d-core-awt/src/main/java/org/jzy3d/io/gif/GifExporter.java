package org.jzy3d.io.gif;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Executors;
import org.jzy3d.io.AWTImageExporter;
import org.jzy3d.io.AbstractImageExporter;
import org.jzy3d.maths.TicToc;

public class GifExporter extends AbstractImageExporter implements AWTImageExporter {
  protected File outputFile;
  protected AnimatedGifEncoder encoder;

  public GifExporter(File outputFile) {
    this(outputFile, DEFAULT_FRAME_RATE_MS); // 1 frame per sec
  }

  public GifExporter(File outputFile, int gifFrameRateMs) {
    super(gifFrameRateMs);

    this.outputFile = outputFile;

    this.encoder = new AnimatedGifEncoder();
    this.encoder.start(outputFile.getAbsolutePath());
    this.encoder.setDelay(gifFrameRateMs);
    this.encoder.setRepeat(1000);
    this.encoder.setQuality(8);
  }

  protected void doAddFrameByRunnable(BufferedImage image, boolean isLastImage) {
    if (debug)
      System.out.println("GifExporter : Adding image to GIF " + numberSubmittedImages.get());


    encoder.addFrame(image);

    if (isLastImage) {
      closeOutput();
    }
    
    numberOfSavedImages.incrementAndGet();
  }

  protected void closeOutput() {
    encoder.finish();

    if (debug)
      System.out.println("GifExporter : Saved " + outputFile.getAbsolutePath());

  }

  public File getOutputFile() {
    return outputFile;
  }
  
  
}
