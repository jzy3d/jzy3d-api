package org.jzy3d.io;



import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.jzy3d.io.gif.FrameRate;
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

  protected BufferedImage previousImage = null;
  protected TicToc timer;

  protected AtomicInteger numberSubmittedImages = new AtomicInteger(0);
  protected AtomicInteger numberOfPendingImages = new AtomicInteger(0);
  protected AtomicInteger numberOfSkippedImages = new AtomicInteger(0);
  protected AtomicInteger numberOfSavedImages = new AtomicInteger(0);

  protected ExecutorService executor;

  protected FrameRate outputFrameRate;
  
  protected boolean debug = false;

  public AbstractImageExporter(FrameRate outputFrameRate) {
    this.outputFrameRate = outputFrameRate;
    this.timer = new TicToc();
    this.executor = Executors.newSingleThreadExecutor();
  }

  @Override
  public void export(BufferedImage image) {

    
      // -----------------------------
      // Case of regular delay
      if (isContinuousFrameRate()) {
        
        // Export first image
        if (previousImage == null) {
          
          // Start counting time
          timer.tic();

          // Export the first image
          scheduleImageExport(image);
          
          // Remember this first image
          previousImage = image;

        }
        
        // Export following image
        else {
          exportWithContinuousFrameRate(image);
        }
      } 
      
      // -----------------------------
      // Case of frame-wise delay      
      
      else {
        
        // Store first image 
        if (previousImage == null) {
          
          // Start counting time
          timer.tic();

          // Will add image next time to know the elapsed time
          // with its following image
          //scheduleImageExport(image, 0);

          // Remember this first image
          previousImage = image;
        }
        
        // Export following image
        else {
          exportWithVariableFrameRate(image);
        }
      }
  }

  protected void exportWithVariableFrameRate(BufferedImage image) {

    // Check time spent since image changed

    timer.toc();
    double elapsed = timer.elapsedMilisecond();
    
    // ---------------------------------------------------
    // If more time than minimum duration has elapsed
    
    if(elapsed<outputFrameRate.getDuration()) {
      previousImage = image;

      numberOfSkippedImages.incrementAndGet();
    }
    
    // ---------------------------------------------------
    //
    
    else {
      // Since the elapsed time is about the previous frame duration,
      // we export the previous frame NOW and provide it the elapsed time
      //scheduleImageExport(previousImage, (int)elapsed);
      scheduleImageExport(previousImage, (int)elapsed);

      // The current frame is stored and will be exported at next frame
      // OR at export termination.
      previousImage = image;

      // Reset counter
      timer.tic();
      
    }

  }

  /**
   * Handle images that should be exported regularly.
   * 
   * Skip image if comes too early w.r.t. global delay, repeat image if it comes too late w.r.t
   * global delay, simply add it otherwise.
   * 
   * @param image
   */
  protected void exportWithContinuousFrameRate(BufferedImage image) {

    // ---------------------------------------------------------------
    // Check time spent since image changed

    timer.toc();
    double elapsed = timer.elapsedMilisecond();
    int elapsedGifFrames = (int) Math.floor(elapsed / outputFrameRate.getDuration());

    // ---------------------------------------------------------------
    // Image pops too early, skip it

    if (elapsedGifFrames == 0) {
      previousImage = image;

      numberOfSkippedImages.incrementAndGet();

    }

    // ---------------------------------------------------------------
    // Image is in [gifFrameRateMs; 2*gifFrameRateMs], schedule export

    else if (elapsedGifFrames == 1) {
      scheduleImageExport(previousImage);

      previousImage = image;

      timer.tic();

    }

    // ---------------------------------------------------------------
    // Time as elapsed for more than 1 image, schedule multiple export
    // of the same image to fill the gap

    else {
      for (int i = 0; i < elapsedGifFrames; i++) {
        scheduleImageExport(previousImage);
      }

      previousImage = image;

      timer.tic();

    }
  }



  // -----------------------------------------------------------------------
  // EXPORT IMAGES WITH CONTINUOUS FRAME RATE
  
  protected void scheduleImageExport(BufferedImage image) {
    scheduleImageExport(image, false);
  }

  protected void scheduleImageExport(BufferedImage image, boolean isLastImage) {
    // Record number of submitted images
    numberSubmittedImages.incrementAndGet();

    // Execute - as soon as possible - the addition of the frame to the output file
    executor.execute(new Runnable() {
      @Override
      public void run() {

        // System.out.println("Adding image to GIF (pending tasks " + pendingTasks.get() + ")");
        // pendingTasks.incrementAndGet();
        try {
          doAddFrameByRunnable(image, isLastImage);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    });
  }
  
  protected abstract void doAddFrameByRunnable(BufferedImage image, boolean isLastImage)
      throws IOException;

  // -----------------------------------------------------------------------
  // EXPORT IMAGES WITH VARIABLE FRAME RATE

  protected void scheduleImageExport(BufferedImage image, int interFrameDelay) {
    scheduleImageExport(image, interFrameDelay, false);
  }

  protected void scheduleImageExport(BufferedImage image, int interFrameDelay, boolean isLastImage) {
    // Record number of submitted images
    numberSubmittedImages.incrementAndGet();

    // Execute - as soon as possible - the addition of the frame to the output file
    executor.execute(new Runnable() {
      @Override
      public void run() {

        // System.out.println("Adding image to GIF (pending tasks " + pendingTasks.get() + ")");
        // pendingTasks.incrementAndGet();
        try {
          doAddFrameByRunnable(image, interFrameDelay, isLastImage);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    });
  }


  protected abstract void doAddFrameByRunnable(BufferedImage image, int interFrameDelay, boolean isLastImage)
      throws IOException;

  
  // -----------------------------------------------------------------------
  
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
      if(isContinuousFrameRate()) {
        scheduleImageExport(previousImage, true);
      }
      else {
        scheduleImageExport(previousImage, 0, true);        
      }
      
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
  
  protected abstract void closeOutput() throws IOException;

  
  // -----------------------------------------------------------------------
  
  /**
   * Returns true if the delay is set globally or if it is defined per frame.
   * 
   */
  public boolean isContinuousFrameRate() {
    return outputFrameRate.isContinuous();
  }

  public TicToc getTimer() {
    return timer;
  }

  public void setTimer(TicToc timer) {
    this.timer = timer;
  }

  public AtomicInteger getNumberSubmittedImages() {
    return numberSubmittedImages;
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
