/*
 * Copyright (c) Since 2017, Martin Pernollet All rights reserved.
 *
 * Redistribution in binary form, with or without modification, is permitted. Edition of source
 * files is allowed. Redistribution of original or modified source files is FORBIDDEN.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jzy3d.plot3d.primitives.axes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.mocks.jogl.GLMock;
import org.jzy3d.mocks.jzy3d.MockTextBitmapRenderer;
import org.jzy3d.mocks.jzy3d.Mocks;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.primitives.axis.AxisTickProcessor;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.View2DLayout;
import org.jzy3d.plot3d.rendering.view.View2DProcessing;

/**
 * 
 * @author Martin Pernollet
 *
 */
public class TestAxisBox {
  protected static float delta = 0.00001f;

  @Test
  public void axisLabelsDisplayedAtMiddleOfAxis() {

    // -------------------
    // Given
    
    View view = Mocks.View();
    when(view.is2D()).thenReturn(false);
    when(view.is3D()).thenReturn(true);
    
    MockTextBitmapRenderer mockText = new MockTextBitmapRenderer();
    Camera mockCamera = new Camera();

    BoundingBox3d testBounds = new BoundingBox3d(0, 10, 0, 10, 0, 1000);

    AxisBox axis = new AxisBox(testBounds);
    axis.setTextRenderer(mockText);
    axis.setView(view);
    
    
    // -------------------
    // When
    
    GLMock glMock = new GLMock();
    NativeDesktopPainter painter = new NativeDesktopPainter();
    painter.setGL(glMock);
    painter.setCamera(mockCamera);
    axis.draw(painter);

    // -------------------
    // Then
    
    Map<String, Object> xArgs = mockText.getCallArguments_whereTextEquals("X");
    Map<String, Object> yArgs = mockText.getCallArguments_whereTextEquals("Y");
    Map<String, Object> zArgs = mockText.getCallArguments_whereTextEquals("Z");

    Assert.assertNotNull(xArgs);
    Assert.assertNotNull(yArgs);
    Assert.assertNotNull(zArgs);

    Assert.assertEquals("X label in the middle of X axis", testBounds.getXRange().getRange() / 2,
        ((Coord3d) xArgs.get("position")).x, delta);
    Assert.assertEquals("Y label in the middle of Y axis", testBounds.getYRange().getRange() / 2,
        ((Coord3d) yArgs.get("position")).y, delta);
    Assert.assertEquals("Z label in the middle of Z axis", testBounds.getZRange().getRange() / 2,
        ((Coord3d) zArgs.get("position")).z, delta);
  }
  
  @Test
  public void whenView2D_XY_ThenAxisTicksAndLabelsOfTheVisibleAxisAreInvoked() {

    // -------------------
    // Given

    // Mock
    ICanvas canvas = mock(ICanvas.class);
    when(canvas.isNative()).thenReturn(false);

    // Mock
    View2DProcessing proc = spy(View2DProcessing.class);
    when(proc.getModelToScreen()).thenReturn(new Coord2d(1,1));

    // Config
    View2DLayout layout = new View2DLayout();
    
    // Mock
    View view = Mocks.View();
    when(view.get2DLayout()).thenReturn(layout);
    when(view.get2DProcessing()).thenReturn(proc);
    when(view.getCanvas()).thenReturn(canvas);
    
    // Mock
    GLMock glMock = new GLMock();
    NativeDesktopPainter painter = new NativeDesktopPainter();
    painter.setGL(glMock);
    painter.setCamera(new Camera());

    // Object under test
    AxisBox axis = spy(AxisBox.class);
    axis.setAxe(new BoundingBox3d(0, 10, 0, 10, 0, 1000));
    axis.setTextRenderer(new MockTextBitmapRenderer());
    axis.setView(view);

    // Mock
    AxisTickProcessor tickProcessor = spy(AxisTickProcessor.class);
    tickProcessor.setAxis(axis);
    axis.setTickProcessor(tickProcessor);
    
    // -------------------
    // When 2D XY
    
    when(view.is2D_XY()).thenReturn(true);
    when(view.is3D()).thenReturn(false);
    
    layout.setHorizontalAxisFlip(false);
    
    axis.draw(painter);


    // Then
    
    verify(tickProcessor, times(1)).drawTicks(any(), anyInt(), eq(AxisBox.AXE_X), any(), any(), any());
    verify(tickProcessor, times(1)).drawTicks(any(), eq(AxisBox.EDGE_2), eq(AxisBox.AXE_Y), any(), any(), any());
    verify(tickProcessor, times(0)).drawTicks(any(), anyInt(), eq(AxisBox.AXE_Z), any(), any(), any());
    
    // -------------------
    // When 2D XY with flipped X axis, selected edge will change
    
    layout.setHorizontalAxisFlip(true);
    
    axis.draw(painter);


    // Then
    
    verify(tickProcessor, times(2)).drawTicks(any(), anyInt(), eq(AxisBox.AXE_X), any(), any(), any());
    verify(tickProcessor, times(1)).drawTicks(any(), eq(AxisBox.EDGE_0), eq(AxisBox.AXE_Y), any(), any(), any());
    verify(tickProcessor, times(0)).drawTicks(any(), anyInt(), eq(AxisBox.AXE_Z), any(), any(), any());

  }
  
  @Test
  public void whenView2D_XZ_ThenAxisTicksAndLabelsOfTheVisibleAxisAreInvoked() {

    // -------------------
    // Given

    // Mock
    ICanvas canvas = mock(ICanvas.class);
    when(canvas.isNative()).thenReturn(false);

    // Mock
    View2DProcessing proc = spy(View2DProcessing.class);
    when(proc.getModelToScreen()).thenReturn(new Coord2d(1,1));

    // Config
    View2DLayout layout = new View2DLayout();

    // Mock
    View view = Mocks.View();
    when(view.get2DLayout()).thenReturn(layout);
    when(view.get2DProcessing()).thenReturn(proc);
    when(view.getCanvas()).thenReturn(canvas);
    
    // Mock
    GLMock glMock = new GLMock();
    NativeDesktopPainter painter = new NativeDesktopPainter();
    painter.setGL(glMock);
    painter.setCamera(new Camera());

    // Object under test
    AxisBox axis = spy(AxisBox.class);
    axis.setAxe(new BoundingBox3d(0, 10, 0, 10, 0, 1000));
    axis.setTextRenderer(new MockTextBitmapRenderer());
    axis.setView(view);

    // Mock
    AxisTickProcessor tickProcessor = spy(AxisTickProcessor.class);
    tickProcessor.setAxis(axis);
    axis.setTickProcessor(tickProcessor);
    
    // -------------------
    // When 2D XY
    
    when(view.is2D_XZ()).thenReturn(true);
    when(view.is3D()).thenReturn(false);
    
    axis.draw(painter);


    // Then
    
    verify(tickProcessor, atLeast(1)).drawTicks(any(), anyInt(), eq(AxisBox.AXE_X), any(), any(), any());
    verify(tickProcessor, times(0)).drawTicks(any(), anyInt(), eq(AxisBox.AXE_Y), any(), any(), any());
    verify(tickProcessor, atLeast(1)).drawTicks(any(), anyInt(), eq(AxisBox.AXE_Z), any(), any(), any());
  }
  
  @Test
  public void whenView3D_ThenAxisTicksAndLabelsOfTheVisibleAxisAreInvoked() {

    // -------------------
    // Given

    // Mock
    ICanvas canvas = mock(ICanvas.class);
    when(canvas.isNative()).thenReturn(false);

    // Mock
    View2DProcessing proc = spy(View2DProcessing.class);
    when(proc.getModelToScreen()).thenReturn(new Coord2d(1,1));

    // Config
    View2DLayout layout = new View2DLayout();

    // Mock
    View view = Mocks.View();
    when(view.get2DLayout()).thenReturn(layout);
    when(view.get2DProcessing()).thenReturn(proc);
    when(view.getCanvas()).thenReturn(canvas);
    
    // Mock
    GLMock glMock = new GLMock();
    NativeDesktopPainter painter = new NativeDesktopPainter();
    painter.setGL(glMock);
    painter.setCamera(new Camera());

    // Object under test
    AxisBox axis = spy(AxisBox.class);
    axis.setAxe(new BoundingBox3d(0, 10, 0, 10, 0, 1000));
    axis.setTextRenderer(new MockTextBitmapRenderer());
    axis.setView(view);

    // Mock
    AxisTickProcessor tickProcessor = spy(AxisTickProcessor.class);
    tickProcessor.setAxis(axis);
    axis.setTickProcessor(tickProcessor);
    
    // -------------------
    // When 3D
    
    when(view.is2D()).thenReturn(false);
    when(view.is3D()).thenReturn(true);

    axis.draw(painter);
    
    // Then
    
    verify(tickProcessor, atLeast(1)).drawTicks(any(), anyInt(), eq(AxisBox.AXE_X), any());
    verify(tickProcessor, atLeast(1)).drawTicks(any(), anyInt(), eq(AxisBox.AXE_Y), any());
    verify(tickProcessor, atLeast(1)).drawTicks(any(), anyInt(), eq(AxisBox.AXE_Z), any());
    
    

  }
}
