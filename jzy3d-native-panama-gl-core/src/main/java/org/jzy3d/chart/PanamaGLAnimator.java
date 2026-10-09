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

public class PanamaGLAnimator implements IAnimator {
  /** Default pause between two renderings, in milliseconds (about 60 frames per second). */
  public static final int RENDERING_LOOP_PAUSE = 16;

  protected int pause = RENDERING_LOOP_PAUSE;
  protected IScreenCanvas canvas;
  protected Thread t;
  protected boolean loop = false;
  protected static int id = 0;


  public PanamaGLAnimator(IScreenCanvas canvas) {
    this.canvas = canvas;
  }

  @Override
  public void start() {
    stop();

    t = new Thread(new Runnable() {

      @Override
      public void run() {
        loop = true;

        while (loop) {
          synchronized (canvas) {
            if (canvas != null)
              canvas.forceRepaint();
          }
          try {
            Thread.sleep(pause);
          } catch (InterruptedException e) {
          }
        }
      }

    }, "org.jzy3d.chart.PanamaGLAnimator thread " + (id++));
    t.start();
  }

  /** Pause between two renderings, in milliseconds. */
  public int getPause() {
    return pause;
  }

  public void setPause(int pause) {
    this.pause = pause;
  }

  @Override
  public void stop() {
    if (t != null) {
      loop = false;
      // t.stop();
      t.interrupt();
      t = null;
    }
  }
}
