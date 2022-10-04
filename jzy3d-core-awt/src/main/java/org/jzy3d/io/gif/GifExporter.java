package org.jzy3d.io.gif;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.io.AWTImageExporter;
import org.jzy3d.io.AbstractImageExporter;
import org.jzy3d.maths.TicToc;
import org.jzy3d.maths.Utils;

/**
 * An image exporter able to create a gif animation out of frame exported by a renderer.
 * 
 * Delay between frames should remain superior to 50 miliseconds.
 * 
 * @see {@link AbstractImageExporter}
 * 
 * @author Martin Pernollet
 *
 */
public class GifExporter extends AbstractImageExporter implements AWTImageExporter {
  protected File outputFile;
  protected AnimatedGifEncoder encoder;

  protected Color backgroundColor = null;
//  protected boolean applyWhiteBackground = true;

  protected TicToc timer = new TicToc();

  public GifExporter(File outputFile) {
    this(outputFile, DEFAULT_FRAME_RATE_MS); // 1 frame per sec
  }

  public GifExporter(File outputFile, int gifFrameDelayMs) {
    super(gifFrameDelayMs);

    this.outputFile = outputFile;

    if (!outputFile.getParentFile().exists()) {
      outputFile.getParentFile().mkdirs();
    }

    this.encoder = new AnimatedGifEncoder();
    this.encoder.start(outputFile.getAbsolutePath());
    this.encoder.setDelay(gifFrameDelayMs);
    this.encoder.setRepeat(1000);
    this.encoder.setQuality(8);

    this.timer.tic();
  }

  /**
   * This method does the effective job of adding the image to an encoder.
   * 
   * It is invoked by the superclass which will call it inside a dedicated thread to avoid slowing
   * down the caller thread.
   * 
   * Although the executor used to invoke a sequence of this method is made of a single thread,
   * visual glitches, showing two images rendered into one have been observed. Making this method
   * synchronized surprisingly solved the issue.
   */
  @Override
  protected synchronized void doAddFrameByRunnable(BufferedImage image, boolean isLastImage)
      throws IOException {

    if (debug) {
      timer.toc();
      System.out.println("GifExporter : Adding image to GIF " + numberSubmittedImages.get() + " at "
          + timer.elapsedSecond());
    }

    // If a background color is defined, create an image with this background color before export
    if (backgroundColor!=null) {
      java.awt.Color awtColor = AWTColor.toAWT(backgroundColor);
      
      BufferedImage imageWithBg =
          new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
      
      Graphics2D g = (Graphics2D) imageWithBg.getGraphics();
      g.setColor(awtColor);
      g.fillRect(0, 0, image.getWidth(), image.getHeight());
      g.drawImage(image, 0, 0, null);
      g.dispose();
      
      image = imageWithBg;
    } 

    // Do export as animated gif frame
    encoder.addFrame(image);


    if (debug) {
      timer.toc();
      System.out.println("GifExporter : Adding image to GIF " + numberSubmittedImages.get() + " at "
          + timer.elapsedSecond());
    }

    // Close output if this is our last image
    if (isLastImage) {
      closeOutput();
    }

    // Update counters to monitor task progress
    numberOfSavedImages.incrementAndGet();
  }

  @Override
  protected void closeOutput() throws IOException {
    encoder.finish();

    if (debug)
      System.out.println("GifExporter : Saved " + outputFile.getAbsolutePath());

  }

  public File getOutputFile() {
    return outputFile;
  }

  public double progress() {
    int submit = getNumberSubmittedImages().intValue();
    int saved = getNumberOfSavedImages().intValue();
    return 100f * saved / submit;
  }

  public void progressToConsole() {
    int submit = getNumberSubmittedImages().intValue();
    int saved = getNumberOfSavedImages().intValue();
    double progress = 100f * saved / submit;

    System.out.println(Utils.num2str(progress, 3) + " % progress : " + saved + " saved / " + submit
        + " submitted");

  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }
  
  


}
