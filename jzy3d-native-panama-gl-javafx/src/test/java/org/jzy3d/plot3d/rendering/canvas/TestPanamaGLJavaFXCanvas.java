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
package org.jzy3d.plot3d.rendering.canvas;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.concurrent.CountDownLatch;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jzy3d.chart.factories.PanamaGLJavaFXChartFactory;
import org.jzy3d.chart.factories.PanamaGLJavaFXPainterFactory;
import org.jzy3d.javafx.canvas.ResizableCanvas;
import javafx.application.Platform;
import panamagl.canvas.GLCanvasJFX;
import panamagl.factory.PanamaGLFactory;
import panamagl.offscreen.AOffscreenRenderer;
import panamagl.offscreen.FBO;
import panamagl.offscreen.FBOReader_JFX;
import panamagl.opengl.GL;
import panamagl.opengl.GLContext;

public class TestPanamaGLJavaFXCanvas {

  @BeforeClass
  public static void initJavaFX() {
    // JavaFX needs its toolkit initialized before creating Canvas/Pane instances.
    try {
      Platform.startup(() -> {});
    } catch (IllegalStateException alreadyRunning) {
      // Another test already started it — fine.
    }
  }

  /** @return a PanamaGLFactory whose offscreen/GL/context/FBO returns are mocked. */
  private static PanamaGLFactory newMockPanamaGLFactory() {
    PanamaGLFactory f = mock(PanamaGLFactory.class);
    when(f.newOffscreenRenderer(any())).thenReturn(new AOffscreenRenderer(f, new FBOReader_JFX()));
    when(f.newGL()).thenReturn(mock(GL.class));
    when(f.newGLContext()).thenReturn(mock(GLContext.class));
    when(f.newFBO(anyInt(), anyInt())).thenReturn(mock(FBO.class));
    return f;
  }

  /** Run a JavaFX-thread body and propagate assertion failures to the JUnit thread. */
  private static void runOnFxThreadAndWait(Runnable body) throws Exception {
    Throwable[] err = new Throwable[1];
    CountDownLatch done = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        body.run();
      } catch (Throwable t) {
        err[0] = t;
      } finally {
        done.countDown();
      }
    });
    done.await();
    if (err[0] != null) {
      throw new RuntimeException(err[0]);
    }
  }

  @Test
  public void forceRepaint() throws Exception {
    PanamaGLFactory f = newMockPanamaGLFactory();

    PanamaGLJavaFXChartFactory factory = new PanamaGLJavaFXChartFactory();
    ((PanamaGLJavaFXPainterFactory) factory.getPainterFactory()).setPanamaGLFactory(f);

    // Build a spyable GLCanvasJFX around a ResizableCanvas on the JavaFX thread, then assert
    // that forceRepaint() triggers GLCanvas.display().
    runOnFxThreadAndWait(() -> {
      ResizableCanvas fxCanvas = new ResizableCanvas();
      GLCanvasJFX glCanvas = spy(new GLCanvasJFX(f, fxCanvas));
      glCanvas.setOffscreenRenderer(f.newOffscreenRenderer(new FBOReader_JFX()));

      PanamaGLJavaFXCanvas c = new PanamaGLJavaFXCanvas(factory, factory.newScene(false),
          Quality.Advanced(), fxCanvas, glCanvas);

      verify(glCanvas, times(0)).display();
      c.forceRepaint();
      verify(glCanvas, times(1)).display();
    });
  }

  /**
   * Regression guard for the "Incomplete framebuffer" crash on the first JavaFX layout pass.
   *
   * <p>A freshly-constructed JavaFX {@link javafx.scene.canvas.Canvas} sits at 0x0. The first
   * layout eventually calls {@link ResizableCanvas#resize(double, double)}, which runs
   * {@code setWidth(W)} then {@code setHeight(H)} sequentially. {@link GLCanvasJFX}'s
   * {@code ResizeHandler} listens on {@code widthProperty()} and {@code heightProperty()}
   * independently, so the first notification fires with {@code width=W, height=0} (or vice
   * versa). The offscreen renderer then attempts to prepare a FBO with a zero dimension,
   * which fails on macOS with:
   *
   * <pre>
   * java.lang.RuntimeException: Incomplete framebuffer, not supporting current FBO config :
   *     36054 != GL_FRAMEBUFFER_COMPLETE (36053)
   *     at panamagl.platform.macos.FBO_macOS.prepare(FBO_macOS.java:176)
   * </pre>
   *
   * <p>{@code PanamaGLJavaFXCanvas.newInitiallySizedCanvas()} pre-sizes the inner canvas to
   * 1x1 so every intermediate notification has strictly positive dimensions. This test locks
   * that precondition in place.
   */
  @Test
  public void innerCanvasIsPreSizedToAtLeastOnePixelInEachDimension() throws Exception {
    PanamaGLFactory f = newMockPanamaGLFactory();

    PanamaGLJavaFXChartFactory factory = new PanamaGLJavaFXChartFactory();
    ((PanamaGLJavaFXPainterFactory) factory.getPainterFactory()).setPanamaGLFactory(f);

    runOnFxThreadAndWait(() -> {
      // Go through the public constructor — not the test-only one — so the
      // newInitiallySizedCanvas() helper is exercised.
      PanamaGLJavaFXCanvas c = new PanamaGLJavaFXCanvas(factory, factory.newScene(false),
          Quality.Advanced(), f);

      double w = c.fxCanvas.getWidth();
      double h = c.fxCanvas.getHeight();

      assertTrue("Inner fxCanvas width must be >= 1 right after construction to prevent "
          + "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT on the first JavaFX layout pass; got " + w,
          w >= 1.0);
      assertTrue("Inner fxCanvas height must be >= 1 right after construction to prevent "
          + "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT on the first JavaFX layout pass; got " + h,
          h >= 1.0);
    });
  }
}
