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
 * Can work in two modes
 * <ul>
 * <li>Export GIF with a regular - user defined - frame rate, in order to deal with high frame rate
 * animations without generating too large GIF files. Frames should be either skipped or repeated if
 * they aren't generated exactly at GIF frame rate.
 * <li>Export GIF with a variable frame rate, in order to deal with variable frame rate animations
 * without skipping or repeating frames.
 * </ul>
 * 
 * 
 * <h2>Export with global frame rate</h2> The interframe delay is globally configured from
 * constructor.
 * 
 * Once initialized, the exporter is ready to receive images. It will start counting time as soon as
 * a first image is received via a call to {@link #export(BufferedImage)}. Following calls to
 * {@link #export(BufferedImage)} will arrange a regular image registration : if the second (and
 * following) image is exported too early w.r.t. to the global delay, it will be skipped. If the it
 * arrives too late, the previously submitted image will be used for export. If it arrives more than
 * 2 times the expected delay, it will repeated to fill the gaps.
 * 
 * Sizing examples :
 * <ul>
 * <li>10 seconds at 40 FPS (hence 25ms per frame) on 800x600 and no HiDPI lead to approx 26MB.
 * <li>10 seconds at 10 FPS (hence 100ms per frame) on 800x600 and no HiDPI lead to approx 7MB
 * </ul>
 * 
 * <h2>Export with frame-wise duration</h2>
 *
 * Here there is no image interpolation. All images are exported and we simply write the inter-frame
 * delay for each frame.
 *
 * <h2>Terminating export</h2>
 * 
 * Once the export is finished, one should invoke
 * {@link #terminate(long, java.util.concurrent.TimeUnit)} where the parameters indicate how long
 * time you accept to wait before timeout. Indeed, depending on the number and size of exported
 * images, a stack of save tasks may be still be pending.
 * <ul>
 * <li>If save completes before timeout, then everything is ok.
 * <li>If timeout is reached before the file is saved, then the file is in an unknown state (maybe
 * readable but at least incomplete).
 * </ul>
 * 
 * To get information about the remaining work, call {@link #progress()} or
 * {@link #progressToConsole()}.
 * 
 * <h2>Background color</h2>
 * 
 * The exporter supports a background color which can be defined to ensure a translucent object
 * won't be altered by the background color of the gif viewer. Setting this background to the
 * chart's background color is the best choice.
 * 
 * Warning : Number of image show that timing is not accurate! 1sec of real time is replayed a bit
 * faster than 1 sec (approx 10%). This may depend on timing to execute the screenshot, hence on the
 * user computer.
 * 
 * @see {@link AbstractImageExporter}
 * 
 * @author Martin Pernollet
 */
public class GifExporter extends AbstractImageExporter implements AWTImageExporter {
  protected static int DEFAULT_MAX_FPS = 10;

  protected File outputFile;
  protected AnimatedGifEncoder encoder;

  protected Color backgroundColor = null;
  // protected boolean applyWhiteBackground = true;

  protected TicToc timer = new TicToc();

  public GifExporter(File outputFile) {
    this(outputFile, FrameRate.VariableRate(DEFAULT_MAX_FPS)); 
  }

  public GifExporter(File outputFile, FrameRate outputFrameRate) {
    super(outputFrameRate);

    this.outputFile = outputFile;

    if (!outputFile.getParentFile().exists()) {
      outputFile.getParentFile().mkdirs();
    }

    this.encoder = new AnimatedGifEncoder();
    this.encoder.start(outputFile.getAbsolutePath());
    this.encoder.setDelay((int)outputFrameRate.getDuration());
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

    addFrameToEncoder(image, isLastImage);
  }

  @Override
  protected synchronized void doAddFrameByRunnable(BufferedImage image, int interframeDelay,
      boolean isLastImage) throws IOException {

    if (debug) {
      System.out.println("Update delay to " + interframeDelay);
    }

    encoder.setDelay(interframeDelay);

    addFrameToEncoder(image, isLastImage);
  }

  protected void addFrameToEncoder(BufferedImage image, boolean isLastImage) throws IOException {
    if (debug) {
      timer.toc();
      System.out.println("GifExporter : Adding image to GIF " + numberSubmittedImages.get() + " at "
          + timer.elapsedSecond());
    }

    // If a background color is defined, create an image with this background color before export
    if (backgroundColor != null) {
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

  /** Return the delay in milisecond as currently set on the encoder. */
  public int getDelay() {
    return encoder.getDelay() * 10;
  }
}
