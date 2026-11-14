/*******************************************************************************
 * Copyright (c) 2022, 2023 Martin Pernollet & contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 *******************************************************************************/
package org.jzy3d.chart;

import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;

/**
 * Repaints a canvas continuously from a dedicated thread, until {@link #stop()} is called.
 *
 * Each thread only runs while it is the current thread of the animator and is not interrupted.
 * This guarantees that {@link #stop()} always ends the thread started by the previous
 * {@link #start()}, even if it is invoked before that thread had a chance to run, and that a
 * second {@link #start()} never leaves the previous thread running.
 */
public class PanamaGLAnimator implements IAnimator {
  /** Default pause between two renderings, in milliseconds (about 60 frames per second). */
  public static final int RENDERING_LOOP_PAUSE = 16;

  protected int pause = RENDERING_LOOP_PAUSE;
  protected IScreenCanvas canvas;
  protected volatile Thread t;
  protected static int id = 0;


  public PanamaGLAnimator(IScreenCanvas canvas) {
    this.canvas = canvas;
  }

  @Override
  public synchronized void start() {
    stop();

    t = new Thread(this::animate, "org.jzy3d.chart.PanamaGLAnimator thread " + (id++));
    t.start();
  }

  /** Repaint the canvas until the calling thread is not the animator thread anymore. */
  protected void animate() {
    Thread current = Thread.currentThread();

    while (isAnimating(current)) {
      if (canvas != null) {
        synchronized (canvas) {
          canvas.forceRepaint();
        }
      }
      try {
        Thread.sleep(pause);
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

  /** Pause between two renderings, in milliseconds. */
  public int getPause() {
    return pause;
  }

  public void setPause(int pause) {
    this.pause = pause;
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
