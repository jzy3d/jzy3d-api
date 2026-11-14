package org.jzy3d.chart;

import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;

/**
 * Renders a canvas continuously from a dedicated thread, until {@link #stop()} is called.
 *
 * Each thread only runs while it is the current thread of the animator and is not interrupted.
 * This guarantees that {@link #stop()} always ends the thread started by the previous
 * {@link #start()}, even if it is invoked before that thread had a chance to run, and that a
 * second {@link #start()} never leaves the previous thread running.
 */
public class EmulGLAnimator implements IAnimator {
  private static final int RENDERING_LOOP_PAUSE = 1000/DEFAULT_FRAME_PER_SECOND;
  protected EmulGLCanvas canvas;
  protected volatile Thread t;
  protected static int id = 0;


  public EmulGLAnimator(EmulGLCanvas canvas) {
    this.canvas = canvas;
  }

  @Override
  public synchronized void start() {
    stop();

    t = new Thread(this::animate, "org.jzy3d.chart.EmulGLAnimator thread " + (id++));
    t.start();
  }

  /** Render the canvas until the calling thread is not the animator thread anymore. */
  protected void animate() {
    Thread current = Thread.currentThread();

    while (isAnimating(current)) {
      if (canvas != null) {
        synchronized (canvas) {
          canvas.doRender();
        }
      }
      try {
        Thread.sleep(RENDERING_LOOP_PAUSE);
      } catch (InterruptedException e) {
        // stop() interrupts the thread to end the pause : terminate
        return;
      }
    }
  }

  protected boolean isAnimating(Thread thread) {
    return t == thread && !thread.isInterrupted();
  }

  /** Return true if a rendering thread was started and not stopped. */
  public boolean isAnimating() {
    return t != null;
  }

  @Override
  public synchronized void stop() {
    Thread thread = t;
    if (thread != null) {
      t = null;
      thread.interrupt();
    }
  }
}
