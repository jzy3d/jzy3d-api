package org.jzy3d.chart.controllers.mouse.camera;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.awt.event.InputEvent;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController.MouseSelection;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.mocks.jzy3d.MouseMock;
import org.jzy3d.os.OperatingSystem;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.Axis;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.DefaultDecimalTickRenderer;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ScientificNotationTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
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
    
    IPainter painter = mock(IPainter.class);
    when(painter.getOS()).thenReturn(OperatingSystem.MACOS);
    
    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getPainter()).thenReturn(painter);
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
    
    IPainter painter = mock(IPainter.class);
    when(painter.getOS()).thenReturn(OperatingSystem.MACOS);

    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    when(chart.getPainter()).thenReturn(painter);
    
    // Given a view behavior 
    when(view.is3D()).thenReturn(true); // 3D mode
    when(view.getBounds()).thenReturn(new BoundingBox3d(0,1,0,1,0,1));
    when(view.getScale()).thenReturn(new Scale(0,1));
    
    // Mock painter to return the default viewport
    int [] defViewport = {0, 0, 400, 200};
    when(painter.getViewPortAsInt()).thenReturn(defViewport); 


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
    
    IPainter painter = mock(IPainter.class);
    when(painter.getOS()).thenReturn(OperatingSystem.MACOS);

    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getPainter()).thenReturn(painter);
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
  
  @Test
  public void given3DView_WhenMouseMove_ThenNoMousePositionIsStored() {
    // Given a set of mocked classes
    View view = mock(View.class);
    
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);
    
    IPainter painter = mock(IPainter.class);
    when(painter.getOS()).thenReturn(OperatingSystem.MACOS);

    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    when(chart.getPainter()).thenReturn(painter);
    
    // Given a view behavior 
    when(view.is3D()).thenReturn(true); // 3D mode
    
    // Given a mouse controller UNDER TEST
    AWTCameraMouseController mouse = new AWTCameraMouseController(chart);
    
    // When mouse click, drag and release
    mouse.mouseMoved(MouseMock.event(10, 10));
    mouse.mouseMoved(MouseMock.event(10, 12));
    
    // Then two rotations are invoked
    //verify(view, times(2)).zoomZ(eq(2f), eq(false));

    Assert.assertNull(mouse.mousePosition.event);
    Assert.assertNull(mouse.mousePosition.projection);    
  }
  
  // ------------------------------------------------------------------
  // 2D interactions
    
  // TODO TEST : when mouse goes on colorbar : do not continue to drag
  // TODO TEST : when mouse release out of canvas
  // TODO TEST : valider que le mouse move ne provoque pas de display dans le cas d'une vue 3D.
  
  /**
   * Perform a sequence of mouse gesture and verify mouse properly invoke the view.
   * 
   * <ul>
   * <li>Drag toward bottom right to make a zoom
   * <li>Drag toward top left to revert to default zoom
   * <li>Drag again toward bottom right by disabling colorbar for viewport config side effects
   * </ul>
   */
  @Test
  public void given2D_XY_View_WhenMouseDragWithLeftButton_ThenZoom2D() {
    
    // Given
    ViewAndColorbarsLayout viewportLayout = mock(ViewAndColorbarsLayout.class);
    View2DLayout layout = mock(View2DLayout.class);

    View view = mock(View.class);
    when(view.get2DLayout()).thenReturn(layout);
    when(view.getLayout()).thenReturn(viewportLayout);
    
    Graph graph = mock(Graph.class);
    Scene scene = mock(Scene.class);
    when(scene.getGraph()).thenReturn(graph);
    
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);
    
    IPainter painter = mock(IPainter.class);
    when(painter.getOS()).thenReturn(OperatingSystem.MACOS);

    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getScene()).thenReturn(scene);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    when(chart.getPainter()).thenReturn(painter);
    
    // -------------------------------------------------------------------------
    // Given a view behavior
    
    
    when(view.is2D()).thenReturn(true); // 2D mode
    when(view.is2D_XY()).thenReturn(true);
    when(view.getViewPoint()).thenReturn(new Coord3d(0,0,10));
    when(view.getBounds()).thenReturn(new BoundingBox3d(0,1,0,1,0,1));
    when(view.getPixelScale()).thenReturn(new Coord2d(1,1));
    
    when(layout.isHorizontalAxisFlip()).thenReturn(false);
    when(layout.isVerticalAxisFlip()).thenReturn(false);
    
    ViewportConfiguration viewport = new ViewportConfiguration(800, 600, 0, 0);
    when(viewportLayout.getSceneViewport()).thenReturn(viewport);

    
    // Given a painter behavior, returning predefined mouse 2D projections to 3D
    // We pre flip Y component since mouse implementation will flip the received Y component
    // We provide the viewport that will be provided to the painter, which should not be the
    // complete canvas one if a legend is activated
    
    // Mock painter to answer to a Zoom gesture
    // (otherwise the mock painter will return null coords, leading to an error)
    
    when(painter.screenToModel(eq(new Coord3d(10,-10,0)), eq(viewport.toArray()), any(), any())).thenReturn(new Coord3d(1,11,0)); 
    when(painter.screenToModel(eq(new Coord3d(15,-15,0)), eq(viewport.toArray()), any(), any())).thenReturn(new Coord3d(2,22,0));
    when(painter.screenToModel(eq(new Coord3d(20,-20,0)), eq(viewport.toArray()), any(), any())).thenReturn(new Coord3d(3,33,0));
    when(painter.screenToModel(eq(new Coord3d(25,-25,0)), eq(viewport.toArray()), any(), any())).thenReturn(new Coord3d(4,44,0));
    
    // Mock painter to answer to an UnZoom gesture
    when(painter.screenToModel(eq(new Coord3d(50,50,0)), eq(viewport.toArray()), any(), any())).thenReturn(new Coord3d(4,44,0));
    when(painter.screenToModel(eq(new Coord3d(45,-45,0)), eq(viewport.toArray()), any(), any())).thenReturn(new Coord3d(4,44,0));
    
    // Given a mouse controller UNDER TEST, finally <<<<<<<<<<<<<<<
    AWTCameraMouseController mouse = new AWTCameraMouseController(chart);
    
    mouse.maintainInAxis = false; // disable this processing to simplify mocking
    
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
    

    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    // Verify if we can deal with FLIPPED AXIS, 
    
    when(layout.isHorizontalAxisFlip()).thenReturn(true);
    when(layout.isVerticalAxisFlip()).thenReturn(true);

    // using new coordinates for different
    // bounds hence test independence
    when(painter.screenToModel(eq(new Coord3d(80,-10,0)), eq(viewport.toArray()), any(), any())).thenReturn(new Coord3d(8,11,0)); 
    when(painter.screenToModel(eq(new Coord3d(85,-15,0)), eq(viewport.toArray()), any(), any())).thenReturn(new Coord3d(9,22,0));
    when(painter.screenToModel(eq(new Coord3d(90,-20,0)), eq(viewport.toArray()), any(), any())).thenReturn(new Coord3d(10,33,0));
    when(painter.screenToModel(eq(new Coord3d(95,-25,0)), eq(viewport.toArray()), any(), any())).thenReturn(new Coord3d(11,44,0));

    // When
    mouse.mousePressed(MouseMock.event(80, 10, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseDragged(MouseMock.event(85, 15, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseDragged(MouseMock.event(90, 20, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseReleased(MouseMock.event(95, 25, InputEvent.BUTTON1_DOWN_MASK));
    
    // Then selection is performed on the following bounding box
    verify(view, times(1)).setBoundsManual(eq(new BoundingBox3d(8,10,11,33,0,1)));

    
    
    
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    // Verify it still works if layout is not of type ViewAndColorbarLayout
    // i.e. when use default painter.getViewPortAsInt()

    // Disable layout mock
    when(view.getLayout()).thenReturn(null);
    
    // Mock painter to return the default viewport
    int [] defViewport = {0, 0, 400, 200};
    when(painter.getViewPortAsInt()).thenReturn(defViewport); 

    // Mock painter to return the match this viewport 
    // (otherwise the mock painter will return null coords, leading to an error)
    // we use different return value to ensure the later verify on view can not depend on the previous 
    // test
    when(painter.screenToModel(eq(new Coord3d(10,-10,0)), eq(defViewport), any(), any())).thenReturn(new Coord3d(10,1100,0)); 
    when(painter.screenToModel(eq(new Coord3d(15,-15,0)), eq(defViewport), any(), any())).thenReturn(new Coord3d(20,2200,0));
    when(painter.screenToModel(eq(new Coord3d(20,-20,0)), eq(defViewport), any(), any())).thenReturn(new Coord3d(30,3300,0));
    when(painter.screenToModel(eq(new Coord3d(25,-25,0)), eq(viewport.toArray()), any(), any())).thenReturn(new Coord3d(40,4400,0));

    // -------------------------------------------------------------------------
    // When mouse click, drag and release
    
    mouse.mousePressed(MouseMock.event(10, 10, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseDragged(MouseMock.event(15, 15, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseDragged(MouseMock.event(20, 20, InputEvent.BUTTON1_DOWN_MASK));
    mouse.mouseReleased(MouseMock.event(25, 25, InputEvent.BUTTON1_DOWN_MASK));
    
    // Then selection is performed on the following bounding box
    verify(view, times(1)).setBoundsManual(eq(new BoundingBox3d(10,30,1100,3300,0,1)));

    
  }
  
  @Test
  public void givenAxisLayout_WhenAnnotateZoom_ThenLabelsApplyAxisLabelAndTickRenderer() {
    // Given
    AxisLayout layout = mock(AxisLayout.class);
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);

    IPainter painter = mock(IPainter.class);
    when(painter.getOS()).thenReturn(OperatingSystem.MACOS);

    Chart chart = mock(Chart.class);
    when(chart.getAxisLayout()).thenReturn(layout);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    when(chart.getPainter()).thenReturn(painter);
    
    // -----------------------------------
    // When no specific config for axis
    AWTCameraMouseController mouse = new AWTCameraMouseController(chart);

    Assert.assertEquals("x=1.2345678", mouse.format(Axis.X, 1.2345678f));
    Assert.assertEquals("y=2.2345679", mouse.format(Axis.Y, 2.2345678f)); 
    Assert.assertEquals("z=3.2345679", mouse.format(Axis.Z, 3.2345678f));
    // watch expected value string without formatter, differ from input!!
    
    // -----------------------------------
    // When specific axis names
    when(layout.getXAxisLabel()).thenReturn("The X axis");
    when(layout.getYAxisLabel()).thenReturn("The Y axis");
    when(layout.getZAxisLabel()).thenReturn("The Z axis");

    Assert.assertEquals("The X axis=1.2345678", mouse.format(Axis.X, 1.2345678f));
    Assert.assertEquals("The Y axis=2.2345679", mouse.format(Axis.Y, 2.2345678f));
    Assert.assertEquals("The Z axis=3.2345679", mouse.format(Axis.Z, 3.2345678f));
    // watch expected value string without formatter, differ from input!!

    // -----------------------------------
    // When specific tick renderer
    
    when(layout.getXTickRenderer()).thenReturn(new DefaultDecimalTickRenderer(3));
    when(layout.getYTickRenderer()).thenReturn(new ScientificNotationTickRenderer());
    when(layout.getZTickRenderer()).thenReturn(new ScientificNotationTickRenderer());

    Assert.assertEquals("The X axis=1,23", mouse.format(Axis.X, 1.2345678f));
    Assert.assertEquals("The Y axis=2,2e+00", mouse.format(Axis.Y, 2.2345678f));
    Assert.assertEquals("The Z axis=3,2e+00", mouse.format(Axis.Z, 3.2345678f));

  }
  
  @Test
  public void given_XY_XZ_or_YZ_View_WhenZoom_ThenUpdateBoundsProperly() {
    float min_H = -1f;
    float max_H = +1f;
    float min_V = -10f;
    float max_V = +10f;
    float min = -100f;
    float max = -100f;
    
    // Given a mouse selection
    MouseSelection selection = mock(MouseSelection.class);
    
    // Given XY view
    View view = mock(View.class);
    
    // Given a chart with related factory
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);

    IPainter painter = mock(IPainter.class);
    when(painter.getOS()).thenReturn(OperatingSystem.MACOS);

    Chart chart = mock(Chart.class);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    when(chart.getPainter()).thenReturn(painter);
    
    // Argument to mouse later
    BoundingBox3d existingBounds;

    // Given a mouse controller UNDER TEST
    AWTCameraMouseController mouse = new AWTCameraMouseController(chart);

    // --------------------------
    // When XY

    when(view.is2D_XY()).thenReturn(true); // <<
    when(view.is2D_XZ()).thenReturn(false);
    when(view.is2D_YZ()).thenReturn(false);
    
    when(selection.min3DX()).thenReturn(min_H);
    when(selection.max3DX()).thenReturn(max_H);
    when(selection.min3DY()).thenReturn(min_V);
    when(selection.max3DY()).thenReturn(max_V);


    existingBounds = new BoundingBox3d(min, max, min, max, min, max);

    mouse.configureZoomAccordingTo2DView(view, existingBounds, selection);
    
    // Then
    
    BoundingBox3d expectedZoom = new BoundingBox3d(min_H, max_H, min_V, max_V, min, max);
    Assert.assertEquals(expectedZoom, existingBounds);
    
    // --------------------------
    // When XZ

    when(view.is2D_XY()).thenReturn(false);
    when(view.is2D_XZ()).thenReturn(true); // <<
    when(view.is2D_YZ()).thenReturn(false);
    
    when(selection.min3DX()).thenReturn(min_H); // on X
    when(selection.max3DX()).thenReturn(max_H);
    when(selection.min3DZ()).thenReturn(min_V); // on Z
    when(selection.max3DZ()).thenReturn(max_V);

    
    existingBounds = new BoundingBox3d(min, max, min, max, min, max);

    mouse.configureZoomAccordingTo2DView(view, existingBounds, selection);
    
    // Then
    
    expectedZoom = new BoundingBox3d(min_H, max_H, min, max, min_V, max_V);
    Assert.assertEquals(expectedZoom, existingBounds);
    
    // --------------------------
    // When YZ

    when(view.is2D_XY()).thenReturn(false);
    when(view.is2D_XZ()).thenReturn(false);
    when(view.is2D_YZ()).thenReturn(true); // <<
    
    when(selection.min3DY()).thenReturn(min_H); // on Y
    when(selection.max3DY()).thenReturn(max_H);
    when(selection.min3DZ()).thenReturn(min_V); // on Z
    when(selection.max3DZ()).thenReturn(max_V);

    
    existingBounds = new BoundingBox3d(min, max, min, max, min, max);

    mouse.configureZoomAccordingTo2DView(view, existingBounds, selection);
    
    // Then
    
    expectedZoom = new BoundingBox3d(min, max, min_H, max_H, min_V, max_V);
    Assert.assertEquals(expectedZoom, existingBounds);
    
  }
  
  @Test
  public void testMouseSelection() {
    float minX = -1;
    float maxX = +1;
    float minY = -10;
    float maxY = +10;
    float minZ = -100;
    float maxZ = +100;
    float delta = 0.1f;
    
    MouseSelection select = new AWTCameraMouseController.MouseSelection();
    
    // mix min and max in start and stop position of selection
    select.start2D = new Coord2d(maxX, minY);
    select.stop2D = new Coord2d(minX, maxY);
    select.start3D = new Coord3d(maxX, minY, maxZ);
    select.stop3D = new Coord3d(minX, maxY, minZ);

    // to later verify we really get min/max and not start/stop
    Assert.assertEquals(minX, select.min2DX(), delta);
    Assert.assertEquals(maxX, select.max2DX(), delta);
    Assert.assertEquals(minY, select.min2DY(), delta);
    Assert.assertEquals(maxY, select.max2DY(), delta);
    
    Assert.assertEquals(minX, select.min3DX(), delta);
    Assert.assertEquals(maxX, select.max3DX(), delta);
    Assert.assertEquals(minY, select.min3DY(), delta);
    Assert.assertEquals(maxY, select.max3DY(), delta);
    Assert.assertEquals(minZ, select.min3DZ(), delta);
    Assert.assertEquals(maxZ, select.max3DZ(), delta);
  }
  
  @Test
  public void whenMouseClickOutsideAxis_ThenSelectionIsCropped() {
    
    int HEIGHT = 600;
    int WIDTH = 800;
    int MARGIN = 10;
    
    // Given
    View2DLayout layout = mock(View2DLayout.class);

    View view = mock(View.class);
    when(view.get2DLayout()).thenReturn(layout);
    when(view.is2D()).thenReturn(true); // 2D mode
    
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    when(canvas.getRendererHeight()).thenReturn(HEIGHT);
    
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);
    
    IPainter painter = mock(IPainter.class);
    when(painter.getOS()).thenReturn(OperatingSystem.MACOS);

    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    when(chart.getPainter()).thenReturn(painter);
    

    // Mouse controller UNDER TEST <<<<<<<<
    AWTCameraMouseController mouse = new AWTCameraMouseController(chart);

    // --------------------------------------------------------------
    // When 2D XY View, with no axis flipped, we have the following 
    // projection of bounds on canvas corners

    BoundingBox3d bounds = new BoundingBox3d(0,1,0,1,0,1);

    when(view.getBounds()).thenReturn(bounds);
    
    when(view.getPixelScale()).thenReturn(new Coord2d(1,1));
    
    when(view.is2D_XY()).thenReturn(true); // XY
    when(view.is2D_XZ()).thenReturn(false);
    when(view.is2D_YZ()).thenReturn(false);
    
    when(layout.isNoAxisFlipped()).thenReturn(true);
    when(layout.isHorizontalAxisFlipOnly()).thenReturn(false);
    when(layout.isVerticalAxisFlipOnly()).thenReturn(false);
    when(layout.isBothAxisFlipped()).thenReturn(false); 


    // bottom left corner
    when(painter.modelToScreen(eq(bounds.getCorners().getXminYminZmin()), any(), any(), any())).thenReturn(new Coord3d(MARGIN, MARGIN));
    // top right corner
    when(painter.modelToScreen(eq(bounds.getCorners().getXmaxYmaxZmax()), any(), any(), any())).thenReturn(new Coord3d(WIDTH-MARGIN, HEIGHT-MARGIN));
    
    // -----------------------------------------
    // When click outside axis (TOP LEFT)
    Coord2d click = new Coord2d(1, 1);

    mouse.maintainInAxis(click);
    
    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(10, 10), click);

    
    // -----------------------------------------
    // When click outside axis (BOTTOM RIGHT)
    click = new Coord2d(799, 599);
    
    mouse.maintainInAxis(click);
    
    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(790, 590), click);

    
    // -----------------------------------------
    // When click outside axis (BOTTOM LEFT)
    click = new Coord2d(800, 1);
    
    mouse.maintainInAxis(click);

    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(790, 10), click);

    // -----------------------------------------
    // When click outside axis (TOP RIGHT)
    click = new Coord2d(1, 599);
    
    mouse.maintainInAxis(click);
    
    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(10, 590), click);

    // -----------------------------------------
    // When click inside axis (MIDDLE)
    click = new Coord2d(400, 300);
    
    mouse.maintainInAxis(click);
    
    // Then coordinates is kept as is
    Assert.assertEquals(new Coord2d(400, 300), click);
    
    

    // --------------------------------------------------------------
    // When 2D XZ View, with both axis flipped, we have the following 
    // projection of bounds on canvas corners

    when(view.is2D_XY()).thenReturn(false);
    when(view.is2D_XZ()).thenReturn(true); // XZ
    when(view.is2D_YZ()).thenReturn(false);
    
    when(layout.isNoAxisFlipped()).thenReturn(false);
    when(layout.isHorizontalAxisFlipOnly()).thenReturn(false);
    when(layout.isVerticalAxisFlipOnly()).thenReturn(false);
    when(layout.isBothAxisFlipped()).thenReturn(true); // both flipped

    // bottom left corner
    when(painter.modelToScreen(eq(bounds.getCorners().getXmaxYminZmax()), any(), any(), any())).thenReturn(new Coord3d(MARGIN, MARGIN));
    // top right corner
    when(painter.modelToScreen(eq(bounds.getCorners().getXminYmaxZmin()), any(), any(), any())).thenReturn(new Coord3d(WIDTH-MARGIN, HEIGHT-MARGIN));
    
    // -----------------------------------------
    // When click outside axis (TOP LEFT)
    click = new Coord2d(1, 1);

    mouse.maintainInAxis(click);
    
    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(10, 10), click);

    
    // -----------------------------------------
    // When click outside axis (BOTTOM RIGHT)
    click = new Coord2d(799, 599);
    
    mouse.maintainInAxis(click);
    
    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(790, 590), click);

    
    // -----------------------------------------
    // When click outside axis (BOTTOM LEFT)
    click = new Coord2d(800, 1);
    
    mouse.maintainInAxis(click);

    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(790, 10), click);

    // -----------------------------------------
    // When click outside axis (TOP RIGHT)
    click = new Coord2d(1, 599);
    
    mouse.maintainInAxis(click);
    
    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(10, 590), click);

    // -----------------------------------------
    // When click inside axis (MIDDLE)
    click = new Coord2d(400, 300);
    
    mouse.maintainInAxis(click);
    
    // Then coordinates is kept as is
    Assert.assertEquals(new Coord2d(400, 300), click);
    
    // --------------------------------------------------------------
    // When 2D YZ View, with only vertical axis flipped, we have the following 
    // projection of bounds on canvas corners

    when(view.is2D_XY()).thenReturn(false);
    when(view.is2D_XZ()).thenReturn(false); 
    when(view.is2D_YZ()).thenReturn(true); // YZ
    
    when(layout.isNoAxisFlipped()).thenReturn(false);
    when(layout.isHorizontalAxisFlipOnly()).thenReturn(false);
    when(layout.isVerticalAxisFlipOnly()).thenReturn(true);
    when(layout.isBothAxisFlipped()).thenReturn(false); 

    // bottom left corner
    when(painter.modelToScreen(eq(bounds.getCorners().getXminYminZmax()), any(), any(), any())).thenReturn(new Coord3d(MARGIN, MARGIN));
    // top right corner
    when(painter.modelToScreen(eq(bounds.getCorners().getXmaxYmaxZmin()), any(), any(), any())).thenReturn(new Coord3d(WIDTH-MARGIN, HEIGHT-MARGIN));
    
    // -----------------------------------------
    // When click outside axis (TOP LEFT)
    click = new Coord2d(1, 1);

    mouse.maintainInAxis(click);
    
    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(10, 10), click);

    
    // -----------------------------------------
    // When click outside axis (BOTTOM RIGHT)
    click = new Coord2d(799, 599);
    
    mouse.maintainInAxis(click);
    
    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(790, 590), click);

    
    // -----------------------------------------
    // When click outside axis (BOTTOM LEFT)
    click = new Coord2d(800, 1);
    
    mouse.maintainInAxis(click);

    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(790, 10), click);

    // -----------------------------------------
    // When click outside axis (TOP RIGHT)
    click = new Coord2d(1, 599);
    
    mouse.maintainInAxis(click);
    
    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(10, 590), click);

    // -----------------------------------------
    // When click inside axis (MIDDLE)
    click = new Coord2d(400, 300);
    
    mouse.maintainInAxis(click);
    
    // Then coordinates is kept as is
    Assert.assertEquals(new Coord2d(400, 300), click);
    
  }
  
  @Test
  public void whenMouseClickOutsideAxis_WithPixelScale_ThenSelectionIsCropped() {
    
    int HEIGHT = 600;
    int WIDTH = 800;
    int MARGIN = 10;
    
    // Given
    View2DLayout layout = mock(View2DLayout.class);

    View view = mock(View.class);
    when(view.get2DLayout()).thenReturn(layout);
    when(view.is2D()).thenReturn(true); // 2D mode
    
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    when(canvas.getRendererHeight()).thenReturn(HEIGHT);
    when(canvas.isNative()).thenReturn(true); // otherwise change calculation
    
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);
    
    IPainter painter = mock(IPainter.class);
    when(painter.getOS()).thenReturn(OperatingSystem.MACOS);

    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    when(chart.getPainter()).thenReturn(painter);
    

    // Mouse controller UNDER TEST <<<<<<<<
    AWTCameraMouseController mouse = new AWTCameraMouseController(chart);

    // --------------------------------------------------------------
    // When 2D XY View, with no axis flipped, we have the following 
    // projection of bounds on canvas corners

    BoundingBox3d bounds = new BoundingBox3d(0,1,0,1,0,1);

    when(view.getBounds()).thenReturn(bounds);
    
    // IMPORTANT CHANGE HERE!
    when(view.getPixelScale()).thenReturn(new Coord2d(2,2));
    
    when(view.is2D_XY()).thenReturn(true); // XY
    when(view.is2D_XZ()).thenReturn(false);
    when(view.is2D_YZ()).thenReturn(false);
    
    when(layout.isNoAxisFlipped()).thenReturn(true);
    when(layout.isHorizontalAxisFlipOnly()).thenReturn(false);
    when(layout.isVerticalAxisFlipOnly()).thenReturn(false);
    when(layout.isBothAxisFlipped()).thenReturn(false); 


    // bottom left corner
    when(painter.modelToScreen(eq(bounds.getCorners().getXminYminZmin()), any(), any(), any())).thenReturn(new Coord3d(MARGIN, MARGIN));
    // top right corner
    when(painter.modelToScreen(eq(bounds.getCorners().getXmaxYmaxZmax()), any(), any(), any())).thenReturn(new Coord3d(WIDTH-MARGIN, HEIGHT-MARGIN));
    
    // -----------------------------------------
    // When click outside axis (TOP LEFT)
    Coord2d click = new Coord2d(1, 1);

    mouse.maintainInAxis(click);
    
    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(5, 5), click);

    
    // -----------------------------------------
    // When click outside axis (BOTTOM RIGHT)
    click = new Coord2d(799, 599);
    
    mouse.maintainInAxis(click);
    
    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(197.5, 297.5), click);

    
    // -----------------------------------------
    // When click outside axis (BOTTOM RIGHT)
    click = new Coord2d(800, 1);
    
    mouse.maintainInAxis(click);

    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(98.75, 226.25), click);

    // -----------------------------------------
    // When click outside axis (TOP LEFT)
    click = new Coord2d(1, 599);
    
    mouse.maintainInAxis(click);
    
    // Then coordinate is modified to stand inside axis
    Assert.assertEquals(new Coord2d(1, 299.375), click);
    
  }
  
  @Test
  public void whenZoomHasRangeZero_ThenDoNotApplyBounds() {
    // Given
    View view = mock(View.class);
    when(view.is2D()).thenReturn(true); // 2D mode
    
    Graph graph = mock(Graph.class);
    Scene scene = mock(Scene.class);
    when(scene.getGraph()).thenReturn(graph);
    
    IScreenCanvas canvas = mock(IScreenCanvas.class);
    
    IChartFactory factory = mock(IChartFactory.class);
    when(factory.newCameraThreadController(null)).thenReturn(null);
    
    IPainter painter = mock(IPainter.class);
    when(painter.getOS()).thenReturn(OperatingSystem.MACOS);

    Chart chart = mock(Chart.class);
    when(chart.getView()).thenReturn(view);
    when(chart.getScene()).thenReturn(scene);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getFactory()).thenReturn(factory);
    when(chart.getPainter()).thenReturn(painter);
    

    // Mouse controller UNDER TEST <<<<<<<<
    AWTCameraMouseController mouse = new AWTCameraMouseController(chart);

    // --------------------------------------------------------------
    // When non relevant Z bounds, do NOT set bounds

    BoundingBox3d bounds = new BoundingBox3d(0,1,0,1,0.5f,0.5f);

    when(view.getBounds()).thenReturn(bounds);
    when(view.is2D_XY()).thenReturn(true); // XY
    
    MouseSelection selection = mock(MouseSelection.class);
    when(selection.growing()).thenReturn(true);
    when(selection.complete()).thenReturn(true);
    
    mouse.mouseSelection = selection;
    
    
    mouse.applyMouse2DSelection(view);
    
    verify(view, times(0)).setBoundsManual(any());
  }

}
