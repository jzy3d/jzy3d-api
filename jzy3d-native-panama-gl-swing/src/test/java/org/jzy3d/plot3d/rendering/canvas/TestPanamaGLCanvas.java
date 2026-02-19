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
import org.junit.Test;
import org.jzy3d.chart.factories.PanamaGLChartFactory;
import org.jzy3d.chart.factories.PanamaGLPainterFactory;
import panamagl.canvas.GLCanvasSwing;
import panamagl.factory.PanamaGLFactory;
import panamagl.offscreen.AOffscreenRenderer;
import panamagl.offscreen.FBO;
import panamagl.offscreen.FBOReader_AWT;
import panamagl.opengl.GL;
import panamagl.opengl.GLContext;

public class TestPanamaGLCanvas {
  @Test
  public void forceRepaint() {
    
    // Mock panamaGL factory
    PanamaGLFactory f = mock(PanamaGLFactory.class);
    when(f.newOffscreenRenderer(any())).thenReturn(new AOffscreenRenderer(f, new FBOReader_AWT()));
    when(f.newGL()).thenReturn(mock(GL.class));
    when(f.newGLContext()).thenReturn(mock(GLContext.class));
    when(f.newFBO(anyInt(),anyInt())).thenReturn(mock(FBO.class));
    
    // Add this mock factory to the chart painter factory
    PanamaGLChartFactory factory = new PanamaGLChartFactory();
    PanamaGLPainterFactory p = (PanamaGLPainterFactory)factory.getPainterFactory();
    p.setPanamaGLFactory(f);


    // Spy the component on which we will perform tests
    GLCanvasSwing glCanvas = spy(GLCanvasSwing.class);
    glCanvas.setOffscreenRenderer(f.newOffscreenRenderer(new FBOReader_AWT()));
    
    // Given
    PanamaGLCanvas c = new PanamaGLCanvas(factory, factory.newScene(false), Quality.Advanced(), glCanvas);
    
    // Then
    verify(glCanvas, times(0)).display();
    
    // When
    c.forceRepaint();// check invoke GLCanvas.update and 
    
    // Then
    verify(glCanvas, times(1)).display();
  }
}
