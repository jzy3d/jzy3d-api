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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jzy3d.chart.factories.PanamaGLJavaFXChartFactory;
import org.jzy3d.chart.factories.PanamaGLJavaFXPainterFactory;
import org.jzy3d.javafx.canvas.ResizableCanvas;
import org.jzy3d.maths.Coord2d;
import panamagl.canvas.GLCanvasJFX;
import panamagl.canvas.PixelScale;
import panamagl.factory.PanamaGLFactory;
import panamagl.offscreen.AOffscreenRenderer;
import panamagl.offscreen.FBO;
import panamagl.offscreen.FBOReader_JFX;
import panamagl.opengl.GL;
import panamagl.opengl.GLContext;

public class TestPanamaGLJavaFXCanvas_HiDPI {

  @BeforeClass
  public static void initJavaFX() {
    try {
      Platform.startup(() -> {});
    } catch (IllegalStateException alreadyRunning) {
      // OK
    }
  }

  private static PanamaGLFactory newMockPanamaGLFactory() {
    PanamaGLFactory f = mock(PanamaGLFactory.class);
    when(f.newOffscreenRenderer(any())).thenReturn(new AOffscreenRenderer(f, new FBOReader_JFX()));
    when(f.newGL()).thenReturn(mock(GL.class));
    when(f.newGLContext()).thenReturn(mock(GLContext.class));
    when(f.newFBO(anyInt(), anyInt())).thenReturn(mock(FBO.class));
    return f;
  }

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
  public void getPixelScale_forwardsToGLCanvas() throws Exception {
    PanamaGLFactory f = newMockPanamaGLFactory();
    PanamaGLJavaFXChartFactory factory = new PanamaGLJavaFXChartFactory();
    ((PanamaGLJavaFXPainterFactory) factory.getPainterFactory()).setPanamaGLFactory(f);

    runOnFxThreadAndWait(() -> {
      ResizableCanvas fxCanvas = new ResizableCanvas();
      GLCanvasJFX glCanvas = spy(new GLCanvasJFX(f, fxCanvas));
      when(glCanvas.getPixelScale()).thenReturn(new PixelScale(2.0, 2.0));
      glCanvas.setOffscreenRenderer(f.newOffscreenRenderer(new FBOReader_JFX()));

      PanamaGLJavaFXCanvas c = new PanamaGLJavaFXCanvas(factory, factory.newScene(false),
          Quality.Advanced(), fxCanvas, glCanvas);

      Coord2d scale = c.getPixelScale();
      Assert.assertEquals(2.0, scale.x, 0.0);
      Assert.assertEquals(2.0, scale.y, 0.0);
    });
  }

  @Test
  public void setPixelScale_identity_disablesHiDPI() throws Exception {
    PanamaGLFactory f = newMockPanamaGLFactory();
    PanamaGLJavaFXChartFactory factory = new PanamaGLJavaFXChartFactory();
    ((PanamaGLJavaFXPainterFactory) factory.getPainterFactory()).setPanamaGLFactory(f);

    runOnFxThreadAndWait(() -> {
      ResizableCanvas fxCanvas = new ResizableCanvas();
      GLCanvasJFX glCanvas = spy(new GLCanvasJFX(f, fxCanvas));
      glCanvas.setOffscreenRenderer(f.newOffscreenRenderer(new FBOReader_JFX()));

      PanamaGLJavaFXCanvas c = new PanamaGLJavaFXCanvas(factory, factory.newScene(false),
          Quality.Advanced(), fxCanvas, glCanvas);

      c.setPixelScale(new float[] {1f, 1f});

      verify(glCanvas).setHiDPIEnabled(false);
    });
  }

  @Test
  public void firePixelScaleChanged_dispatchesToICanvasListeners() throws Exception {
    PanamaGLFactory f = newMockPanamaGLFactory();
    PanamaGLJavaFXChartFactory factory = new PanamaGLJavaFXChartFactory();
    ((PanamaGLJavaFXPainterFactory) factory.getPainterFactory()).setPanamaGLFactory(f);

    AtomicReference<double[]> received = new AtomicReference<>();

    runOnFxThreadAndWait(() -> {
      ResizableCanvas fxCanvas = new ResizableCanvas();
      GLCanvasJFX glCanvas = new GLCanvasJFX(f, fxCanvas);
      glCanvas.setOffscreenRenderer(f.newOffscreenRenderer(new FBOReader_JFX()));

      PanamaGLJavaFXCanvas c = new PanamaGLJavaFXCanvas(factory, factory.newScene(false),
          Quality.Advanced(), fxCanvas, glCanvas);
      c.addCanvasListener((px, py) -> received.set(new double[] {px, py}));
      c.firePixelScaleChanged(2.0, 2.0);
    });

    double[] r = received.get();
    Assert.assertNotNull("ICanvasListener should have received an event", r);
    Assert.assertEquals(2.0, r[0], 0.0);
    Assert.assertEquals(2.0, r[1], 0.0);
  }
}
