package org.jzy3d.io;



import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.jzy3d.maths.TicToc;

/**
 * The {@link AbstractImageExporter} can receive {@link BufferedImage} image from a renderer to
 * export them somewhere.
 * 
 * The exporter as a target frame rate. If it receive images at a higher rate, exceeding images will
 * be dropped. If it receive images as a lower rate, it will repeat the last known image until the
 * new one arrives.
 *
 * The exporter uses its own thread to handle export image queries without slowing down the caller
 * thread.
 * 
 * 
 * @author Martin Pernollet
 *
 */
public abstract class AbstractImageExporter implements AWTImageExporter {
  protected static int DEFAULT_FRAME_RATE_MS = 1000;

  protected BufferedImage previousImage = null;
  protected TicToc timer;
  protected ExecutorService executor;
  protected AtomicInteger numberSubmittedImages = new AtomicInteger(0);
  protected AtomicInteger numberOfPendingImages = new AtomicInteger(0);
  protected AtomicInteger numberOfSkippedImages = new AtomicInteger(0);
  protected AtomicInteger numberOfSavedImages = new AtomicInteger(0);

  protected int frameRateMs;

  protected boolean debug = false;


  // protected int numberOfSubmittedImages = 0;

  public AbstractImageExporter(int frameRateMs) {
    this.frameRateMs = frameRateMs;
    this.timer = new TicToc();
    this.executor = Executors.newSingleThreadExecutor();
  }

  @Override
  public void export(BufferedImage image) {
    // init timer
    if (previousImage == null) {
      timer.tic();

      scheduleImageExport(image);
    }

    // or check time spent since image changed
    else {
      timer.toc();
      double elapsed = timer.elapsedMilisecond();
      // System.out.println("ELAPSED : " + elapsed);
      int elapsedGifFrames = (int) Math.floor(elapsed / frameRateMs);

      // Image pops too early
      if (elapsedGifFrames == 0) {
        previousImage = image;

        numberOfSkippedImages.incrementAndGet();
      }

      // Image is in [gifFrameRateMs; 2*gifFrameRateMs]
      else if (elapsedGifFrames == 1) {
        scheduleImageExport(previousImage);

        previousImage = image;

        timer.tic();

      } else {
        for (int i = 0; i < elapsedGifFrames; i++) {
          scheduleImageExport(previousImage);
        }

        previousImage = image;

        timer.tic();

      }
    }

    previousImage = image;
  }

  /**
   * Await completion of all image addition, add the last image, and finish the file.
   * 
   * If a time unit is given, the timeout
   * 
   * Returns true if all images have been flushed before the timeout ends, false otherwise
   */
  @Override
  public boolean terminate(long timeout, TimeUnit unit) {
    try {
      // Export last image
      scheduleImageExport(previousImage, true);

      if (debug)
        System.err.println("AbstractImageExporter : Stop accepting new export request, and wait "
            + timeout + " " + unit + " before forcing the termination");
      executor.shutdown();

      // Wait for task to terminate for a while if time unit not null
      if (unit != null) {
        executor.awaitTermination(timeout, unit);
      }

      // Force shutdown and report unfinished export task
      int remaining = executor.shutdownNow().size();
      if (remaining > 0) {
        if (debug)
          System.err.println("AbstractImageExporter : " + remaining
              + " were still pending. Try growing timeout or reducing gif framerate.");
        return false;
      } else {
        if (debug)
          System.out
              .println("AbstractImageExporter : All image have been flushed! Export is complete");
        return true;
      }


    } catch (InterruptedException e1) {
      e1.printStackTrace();
      return false;
    }
  }

  /**
   * Wait for the process to finish and return when done.
   * 
   * @return
   *
   *         public boolean awaitTermination() { return terminate(0, null); }
   */

  protected void scheduleImageExport(BufferedImage image) {
    scheduleImageExport(image, false);
  }

  protected void scheduleImageExport(BufferedImage image, boolean isLastImage) {
    // try {
    executor.execute(new Runnable() {
      @Override
      public void run() {

        numberSubmittedImages.incrementAndGet();
        numberOfPendingImages.incrementAndGet();

        // System.out.println("Adding image to GIF (pending tasks " + pendingTasks.get() + ")");
        // pendingTasks.incrementAndGet();

        /*
         * Graphics2D graphics = (Graphics2D)image.getGraphics();
         * AWTGraphicsUtils.configureRenderingHints(graphics);
         * graphics.setColor(java.awt.Color.GRAY);
         * graphics.drawString("Powered by Jzy3D (martin@jzy3d.org)", image.getWidth()/2+40,
         * image.getHeight()-20); graphics.dispose();
         */

        doAddFrameByRunnable(image, isLastImage);

        numberOfPendingImages.decrementAndGet();

      }

    });
    /*
     * } catch (RejectedExecutionException e) { System.err.
     * println("AbstractImageExporter : should stop appending image (unregister me from canvas!"); }
     */
  }

  protected abstract void doAddFrameByRunnable(BufferedImage image, boolean isLastImage);

  protected abstract void closeOutput();

  public TicToc getTimer() {
    return timer;
  }

  public void setTimer(TicToc timer) {
    this.timer = timer;
  }

  public AtomicInteger getNumberSubmittedImages() {
    return numberSubmittedImages;
  }

  public AtomicInteger getNumberOfPendingImages() {
    return numberOfPendingImages;
  }

  public AtomicInteger getNumberOfSkippedImages() {
    return numberOfSkippedImages;
  }

  public AtomicInteger getNumberOfSavedImages() {
    return numberOfSavedImages;
  }

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }


}
