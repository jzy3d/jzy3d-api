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
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.View2DLayout;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

public class TestAWTCameraMouseController {
  
  // 3D interactions
  
  @Test
  public void given3DView_WhenMouseDragWithLeftButton_ThenViewRotate() {
    
    // Given
    View view = spy(View.class);
    
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);
    
    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    
    // Given a view behavior 
    when(view.is2D()).thenReturn(false); // 3D mode
    when(view.getViewPoint()).thenReturn(new Coord3d(0,0,10));

    // Given a mouse controller UNDER TEST
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
    
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);
    
    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    
    // Given a view behavior 
    when(view.is2D()).thenReturn(false); // 3D mode
    when(view.getBounds()).thenReturn(new BoundingBox3d(0,1,0,1,0,1));
    when(view.getScale()).thenReturn(new Scale(0,1));

    // Given a mouse controller UNDER TEST
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
    
    // Given a set of mocked classes
    View view = mock(View.class);
    
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);
    
    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);

    // Given a view behavior 
    when(view.is2D()).thenReturn(false); // 3D mode
    
    // Given a mouse controller UNDER TEST
    AWTCameraMouseController mouse = new AWTCameraMouseController(chart);
    
    // When mouse click, drag and release
    mouse.mouseWheelMoved(MouseMock.wheel(10));
    mouse.mouseWheelMoved(MouseMock.wheel(10));
    
    // Then two rotations are invoked
    verify(view, times(2)).zoomZ(eq(2f), eq(false));
    
  }
  
  // ------------------------------------------------------------------
  // 2D interactions
    
  
  @Test
  public void given2DView_WhenMouseDragWithLeftButton_ThenZoom2D() {
    
    // Given
    ViewAndColorbarsLayout viewportLayout = mock(ViewAndColorbarsLayout.class);
    View2DLayout layout = mock(View2DLayout.class);

    View view = mock(View.class);
    when(view.get2DLayout()).thenReturn(layout);
    when(view.getLayout()).thenReturn(viewportLayout);
    
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);
    
    IPainter painter = mock(IPainter.class);
    
    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    when(chart.getPainter()).thenReturn(painter);
    
    // -------------------------------------------------------------------------
    // Given a view behavior
    when(view.is2D()).thenReturn(true); // 2D mode
    when(view.is2D_XY()).thenReturn(true);
    when(view.getViewPoint()).thenReturn(new Coord3d(0,0,10));
    when(view.getBounds()).thenReturn(new BoundingBox3d(0,1,0,1,0,1));

    when(layout.isHorizontalAxisFlip()).thenReturn(false);
    when(layout.isVerticalAxisFlip()).thenReturn(false);
    when(viewportLayout.getSceneViewport()).thenReturn(new ViewportConfiguration(800, 600, 0, 0));

    
    // Given a painter behavior, returning predefined mouse 2D projections to 3D
    // We pre flip Y component since implementation will flip the received Y component
    when(painter.screenToModel(new Coord3d(10,-10,0))).thenReturn(new Coord3d(1,11,0)); 
    when(painter.screenToModel(new Coord3d(15,-15,0))).thenReturn(new Coord3d(2,22,0));
    when(painter.screenToModel(new Coord3d(20,-20,0))).thenReturn(new Coord3d(3,33,0));
    when(painter.screenToModel(new Coord3d(25,-25,0))).thenReturn(new Coord3d(4,44,0));

    
    // Given a mouse controller UNDER TEST
    AWTCameraMouseController mouse = new AWTCameraMouseController(chart);

    // -------------------------------------------------------------------------
    // When mouse click, drag and release
    
    mouse.mousePressed(MouseMock.event(10, 10, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseDragged(MouseMock.event(15, 15, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseDragged(MouseMock.event(20, 20, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseReleased(MouseMock.event(25, 25, InputEvent.BUTTON1_DOWN_MASK));
    
    // Then selection is performed on the following bounding box
    verify(view, times(1)).setBoundsManual(eq(new BoundingBox3d(1,3,11,33,0,1)));
    

    // -------------------------------------------------------------------------
    // When mouse click, drag and release from bottom right to top left
    
    mouse.mousePressed(MouseMock.event(50, 50, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseDragged(MouseMock.event(45, 45, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseReleased(MouseMock.event(40, 40, InputEvent.BUTTON1_DOWN_MASK));
    
    // Then bounds are reset to auto bounds
    verify(view, times(1)).setBoundMode(eq(ViewBoundMode.AUTO_FIT));

    
    
  }

}
