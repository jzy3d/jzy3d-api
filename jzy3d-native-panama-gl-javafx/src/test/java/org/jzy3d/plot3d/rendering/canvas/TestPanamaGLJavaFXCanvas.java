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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

  @Test
  public void forceRepaint() throws Exception {
    // Mock panamaGL factory
    PanamaGLFactory f = mock(PanamaGLFactory.class);
    when(f.newOffscreenRenderer(any())).thenReturn(new AOffscreenRenderer(f, new FBOReader_JFX()));
    when(f.newGL()).thenReturn(mock(GL.class));
    when(f.newGLContext()).thenReturn(mock(GLContext.class));
    when(f.newFBO(anyInt(), anyInt())).thenReturn(mock(FBO.class));

    // Build a chart factory with the mock panamaGL factory.
    PanamaGLJavaFXChartFactory factory = new PanamaGLJavaFXChartFactory();
    PanamaGLJavaFXPainterFactory p = (PanamaGLJavaFXPainterFactory) factory.getPainterFactory();
    p.setPanamaGLFactory(f);

    // Build a spyable GLCanvasJFX around a ResizableCanvas on the JavaFX thread, then assert
    // that forceRepaint() triggers GLCanvas.display().
    final Throwable[] errorHolder = new Throwable[1];
    java.util.concurrent.CountDownLatch done = new java.util.concurrent.CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        ResizableCanvas fxCanvas = new ResizableCanvas();
        GLCanvasJFX glCanvas = spy(new GLCanvasJFX(f, fxCanvas));
        glCanvas.setOffscreenRenderer(f.newOffscreenRenderer(new FBOReader_JFX()));

        PanamaGLJavaFXCanvas c = new PanamaGLJavaFXCanvas(factory, factory.newScene(false),
            Quality.Advanced(), fxCanvas, glCanvas);

        verify(glCanvas, times(0)).display();
        c.forceRepaint();
        verify(glCanvas, times(1)).display();
      } catch (Throwable t) {
        errorHolder[0] = t;
      } finally {
        done.countDown();
      }
    });
    done.await();
    if (errorHolder[0] != null) {
      throw new RuntimeException(errorHolder[0]);
    }
  }
}
