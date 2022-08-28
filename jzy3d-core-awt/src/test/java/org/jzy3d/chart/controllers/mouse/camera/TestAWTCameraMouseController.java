package org.jzy3d.chart.controllers.mouse.camera;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.awt.event.InputEvent;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.mocks.jzy3d.MouseMock;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.view.View;

public class TestAWTCameraMouseController {
  @Test
  public void given3DView_WhenMouseDragWithLeftButton_ThenViewRotate() {
    
    // Given
    View view = spy(View.class);
    when(view.is2D()).thenReturn(false); // 3D mode
    when(view.getViewPoint()).thenReturn(new Coord3d(0,0,10));
    
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);
    
    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    
    AWTCameraMouseController mouse = new AWTCameraMouseController(chart);
    
    // When mouse click, drag and release
    mouse.mousePressed(MouseMock.event(10, 10, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseDragged(MouseMock.event(15, 15, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseDragged(MouseMock.event(20, 20, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseReleased(MouseMock.event(25, 25, InputEvent.BUTTON1_DOWN_MASK));
    
    // Then two rotations are invoked
    verify(view, times(2)).rotate(eq(new Coord2d(0.05, 0.05)), eq(false));
    
  }
  
  @Test
  public void given3DView_WhenMouseDragWithRightButton_ThenViewShift() {
    
    // Given
    View view = mock(View.class);
    when(view.is2D()).thenReturn(false); // 3D mode
    //when(view.getViewPoint()).thenReturn(new Coord3d(0,0,10));
    when(view.getBounds()).thenReturn(new BoundingBox3d(0,1,0,1,0,1));
    when(view.getScale()).thenReturn(new Scale(0,1));
    //when(view.setScale()).thenReturn(new Scale(0,1));
    
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);
    
    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    
    AWTCameraMouseController mouse = new AWTCameraMouseController(chart);
    
    // When mouse click, drag and release
    mouse.mousePressed(MouseMock.event(10, 10, InputEvent.BUTTON3_DOWN_MASK));
    mouse.mouseDragged(MouseMock.event(15, 15, InputEvent.BUTTON3_DOWN_MASK));
    mouse.mouseDragged(MouseMock.event(20, 20, InputEvent.BUTTON3_DOWN_MASK));
    mouse.mouseReleased(MouseMock.event(25, 25, InputEvent.BUTTON3_DOWN_MASK));
    
    // Then two rotations are invoked
    verify(view, times(2)).shift(eq(0.01f), eq(false));
    
  }
  
  @Test
  public void given3DView_WhenMouseWheel_ThenViewScale() {
    
    // Given
    View view = mock(View.class);
    when(view.is2D()).thenReturn(false); // 3D mode
    //when(view.getViewPoint()).thenReturn(new Coord3d(0,0,10));
    //when(view.getBounds()).thenReturn(new BoundingBox3d(0,1,0,1,0,1));
    //when(view.getScale()).thenReturn(new Scale(0,1));
    //when(view.setScale()).thenReturn(new Scale(0,1));
    
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);
    
    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    
    AWTCameraMouseController mouse = new AWTCameraMouseController(chart);
    
    // When mouse click, drag and release
    mouse.mouseWheelMoved(MouseMock.wheel(10));
    mouse.mouseWheelMoved(MouseMock.wheel(10));
    
    // Then two rotations are invoked
    verify(view, times(2)).zoomZ(eq(2f), eq(false));
    
  }
}
