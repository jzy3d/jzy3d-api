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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import org.junit.Test;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;

public class TestPanamaGLAnimator {
  static final long JOIN_TIMEOUT_MS = 2000;

  /**
   * A stop() invoked right after start(), before the rendering thread runs, must still end that
   * thread : it used to set its loop flag back to true when it started, and render forever.
   */
  @Test
  public void stopRightAfterStartEndsTheThread() throws InterruptedException {
    PanamaGLAnimator animator = new PanamaGLAnimator(mock(IScreenCanvas.class));
    animator.setPause(1);

    for (int i = 0; i < 200; i++) {
      animator.start();
      Thread thread = animator.t;
      animator.stop();

      thread.join(JOIN_TIMEOUT_MS);
      assertFalse("Thread still rendering after stop, at iteration " + i, thread.isAlive());
    }
    assertFalse(animator.isAnimating());
  }

  /** A second start() must end the thread of the first one. */
  @Test
  public void restartEndsThePreviousThread() throws InterruptedException {
    PanamaGLAnimator animator = new PanamaGLAnimator(mock(IScreenCanvas.class));
    animator.setPause(1);

    animator.start();
    Thread first = animator.t;
    animator.start();
    Thread second = animator.t;

    assertNotSame(first, second);
    first.join(JOIN_TIMEOUT_MS);
    assertFalse("First thread still rendering after restart", first.isAlive());
    assertTrue(second.isAlive());

    animator.stop();
    second.join(JOIN_TIMEOUT_MS);
    assertFalse(second.isAlive());
  }

  @Test
  public void rendersContinuouslyUntilStopped() throws InterruptedException {
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    PanamaGLAnimator animator = new PanamaGLAnimator(canvas);
    animator.setPause(1);

    animator.start();
    assertTrue(animator.isAnimating());
    verify(canvas, timeout(JOIN_TIMEOUT_MS).atLeast(3)).forceRepaint();

    Thread thread = animator.t;
    animator.stop();
    thread.join(JOIN_TIMEOUT_MS);
    assertFalse(thread.isAlive());
  }
}
