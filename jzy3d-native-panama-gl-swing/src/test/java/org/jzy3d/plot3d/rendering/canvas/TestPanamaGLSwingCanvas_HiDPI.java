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
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.factories.PanamaGLSwingChartFactory;
import org.jzy3d.chart.factories.PanamaGLSwingPainterFactory;
import org.jzy3d.maths.Coord2d;
import panamagl.canvas.GLCanvasSwing;
import panamagl.canvas.PixelScale;
import panamagl.factory.PanamaGLFactory;
import panamagl.offscreen.AOffscreenRenderer;
import panamagl.offscreen.FBO;
import panamagl.offscreen.FBOReader_AWT;
import panamagl.opengl.GL;
import panamagl.opengl.GLContext;

public class TestPanamaGLSwingCanvas_HiDPI {

  private static PanamaGLFactory mockPanamaGL() {
    PanamaGLFactory f = mock(PanamaGLFactory.class);
    when(f.newOffscreenRenderer(any())).thenReturn(new AOffscreenRenderer(f, new FBOReader_AWT()));
    when(f.newGL()).thenReturn(mock(GL.class));
    when(f.newGLContext()).thenReturn(mock(GLContext.class));
    when(f.newFBO(anyInt(), anyInt())).thenReturn(mock(FBO.class));
    return f;
  }

  private static PanamaGLSwingCanvas newCanvas(GLCanvasSwing glCanvas) {
    PanamaGLSwingChartFactory factory = new PanamaGLSwingChartFactory();
    ((PanamaGLSwingPainterFactory) factory.getPainterFactory()).setPanamaGLFactory(mockPanamaGL());
    glCanvas.setOffscreenRenderer(mockPanamaGL().newOffscreenRenderer(new FBOReader_AWT()));
    return new PanamaGLSwingCanvas(factory, factory.newScene(false), Quality.Advanced(), glCanvas);
  }

  @Test
  public void getPixelScale_forwardsToGLCanvas() {
    GLCanvasSwing glCanvas = spy(GLCanvasSwing.class);
    when(glCanvas.getPixelScale()).thenReturn(new PixelScale(2.0, 2.0));

    PanamaGLSwingCanvas c = newCanvas(glCanvas);

    Coord2d scale = c.getPixelScale();
    Assert.assertEquals(2.0, scale.x, 0.0);
    Assert.assertEquals(2.0, scale.y, 0.0);
  }

  @Test
  public void setPixelScale_identity_disablesHiDPI() {
    GLCanvasSwing glCanvas = spy(GLCanvasSwing.class);
    PanamaGLSwingCanvas c = newCanvas(glCanvas);

    c.setPixelScale(new float[] {1f, 1f});

    verify(glCanvas).setHiDPIEnabled(false);
  }

  @Test
  public void setPixelScale_nonIdentity_enablesHiDPI() {
    GLCanvasSwing glCanvas = spy(GLCanvasSwing.class);
    PanamaGLSwingCanvas c = newCanvas(glCanvas);

    c.setPixelScale(new float[] {2f, 2f});

    verify(glCanvas).setHiDPIEnabled(true);
  }

  /**
   * Verify the bridge between PanamaGL's {@code PixelScaleListener} and Jzy3D's
   * {@code ICanvasListener.pixelScaleChanged}: registered Jzy3D listeners are dispatched via the
   * {@code firePixelScaleChanged} method that the constructor wires onto the GLCanvas.
   */
  @Test
  public void firePixelScaleChanged_dispatchesToICanvasListeners() {
    GLCanvasSwing glCanvas = new GLCanvasSwing();
    PanamaGLSwingCanvas c = newCanvas(glCanvas);

    AtomicReference<double[]> received = new AtomicReference<>();
    c.addCanvasListener((px, py) -> received.set(new double[] {px, py}));

    c.firePixelScaleChanged(2.0, 2.0);

    double[] r = received.get();
    Assert.assertNotNull("ICanvasListener should have received an event", r);
    Assert.assertEquals(2.0, r[0], 0.0);
    Assert.assertEquals(2.0, r[1], 0.0);
  }
}
