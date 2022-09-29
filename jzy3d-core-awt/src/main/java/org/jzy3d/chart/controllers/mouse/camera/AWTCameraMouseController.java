package org.jzy3d.chart.controllers.mouse.camera;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.RateLimiter;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.mouse.AWTMouseUtilities;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController.MouseSelectionSettings.ZoomAreaStyle;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot2d.rendering.AWTGraphicsUtils;
import org.jzy3d.plot3d.primitives.axis.Axis;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.AbstractAWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.AbstractViewportManager;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.View2DLayout;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;


public class AWTCameraMouseController extends AbstractCameraController
    implements MouseListener, MouseWheelListener, MouseMotionListener {

  protected RateLimiter rateLimiter;

  protected Coord2d prevMouse = Coord2d.ORIGIN;

  MouseMoveRenderer moveRenderer = new MouseMoveRenderer();
  MouseDragRenderer dragRenderer = new MouseDragRenderer();

  MouseSelection mouseSelection = new MouseSelection();
  MousePosition mousePosition = new MousePosition();

  MouseSelectionSettings selectionSettings = new MouseSelectionSettings();

  // Behaviour

  protected boolean maintainInAxis = true;
  
  protected boolean scaled = false;
  

  public AWTCameraMouseController() {}

  public AWTCameraMouseController(Chart chart) {
    register(chart);
    addThread(chart.getFactory().newCameraThreadController(chart));
  }

  public AWTCameraMouseController(Chart chart, RateLimiter limiter) {
    this(chart);
    setRateLimiter(limiter);
  }

  @Override
  public void register(Chart chart) {
    super.register(chart);
    
    chart.getCanvas().addMouseController(this);

    configureScaler(chart);
    
    //if(AbstractViewportManager.apply_WindowsHiDPI_Workaround(null, prevMouse))
    
    if (chart.getView() instanceof AWTView) {
      ((AWTView) chart.getView()).addRenderer2d(moveRenderer);
      ((AWTView) chart.getView()).addRenderer2d(dragRenderer);
    }

  }

  /** This deal with HiDPI, EMulGL and Windows+AWT */
  protected void configureScaler(Chart chart) {
    // This allows dealing with a difference
    // between emulgl and native
    // TODO : https://github.com/jzy3d/jzy3d-api/issues/304
    scaled = !chart.getCanvas().isNative();

    // This allows dealing with a difference
    // between Win+AWT and the other cases when 
    // using HiDPI monitors
    // https://github.com/jzy3d/jzy3d-api/issues/101
    IPainter painter = chart.getPainter();
    if(painter.getOS().isWindows() && painter.getWindowingToolkit().isAWT()) {
      scaled = true;
    }
  }

  public void unregister(Chart chart) {   
    chart.getCanvas().removeMouseController(this);

    if (chart.getView() instanceof AWTView) {
      ((AWTView) chart.getView()).removeRenderer2d(moveRenderer);
      ((AWTView) chart.getView()).removeRenderer2d(dragRenderer);
    }
    
    super.unregister(chart);
  }

  @Override
  public void dispose() {
    unregister(target);
      // chart.getCanvas().removeMouseController(this);
    super.dispose();
  }

  /**
   * Get the rate limiter to only consider mouse events at a given rate.
   * 
   * Mainly useful for EmulGL to deal with liveness effect.
   * 
   * @see https://github.com/jzy3d/jzy3d-api/tree/master/jzy3d-emul-gl-awt#integrating-in-awt
   */
  public RateLimiter getRateLimiter() {
    return rateLimiter;
  }

  /**
   * Provide a rate limiter to only consider mouse events at a given rate.
   * 
   * Mainly useful for EmulGL to deal with liveness effect.
   * 
   * @see https://github.com/jzy3d/jzy3d-api/tree/master/jzy3d-emul-gl-awt#integrating-in-awt
   */
  public void setRateLimiter(RateLimiter rateLimiter) {
    this.rateLimiter = rateLimiter;
  }

  /** Get the rendering settings of the mouse annotations (2D) */
  public MouseSelectionSettings getSelectionSettings() {
    return selectionSettings;
  }

  /** Set all rendering settings of the mouse annotations (2D) at once */
  public void setSelectionSettings(MouseSelectionSettings selectionSettings) {
    this.selectionSettings = selectionSettings;
  }


  // ----------------------------------------------------------------------------
  // MOUSE EVENT LISTENERS SECTION
  // ----------------------------------------------------------------------------


  /**
   * When a mouse button is pressed
   * 
   * <ul>
   * <li>In 3D, if double click, start or stop the camera rotation thread if there is any attached
   * <li>In 2D, memorize the starting point (top left) of the selection rectangle
   * </ul>
   */
  @Override
  public void mousePressed(MouseEvent e) {

    prevMouse.x = x(e);
    prevMouse.y = y(e);

    // 3D mode
    if (getChart().getView().is3D()) {
      if (handleSlaveThread(e)) {
        return;
      }
    }

    // 2D mode
    else {

      Coord2d startMouse = prevMouse.clone();

      if (maintainInAxis)
        maintainInAxis(startMouse);



      // stop displaying mouse position on roll over
      mousePosition = new MousePosition();

      // start creating a selection
      mouseSelection.start2D = startMouse;
      mouseSelection.start3D = screenToModel(startMouse.x, startMouse.y);

      if (mouseSelection.start3D == null)
        System.err.println("Mouse.onMousePressed projection is null ");


      // screenToModel(bounds3d.getCorners())

    }
  }

  /**
   * When mouse is dragged
   * 
   * <ul>
   * <li>In 3D, if left button is hold, rotates the view around the center of the scene
   * <li>In 3D, if right button is hold, shift the view along Z axis
   * <li>In 2D, update the memory of the stop point (bottom right) of the selection rectangle
   * </ul>
   */
  @Override
  public void mouseDragged(MouseEvent e) {

    // Check if mouse rate limiter wish to forbid this mouse drag instruction
    if (rateLimiter != null && !rateLimiter.rateLimitCheck()) {
      return;
    }

    // Apply mouse drag
    Coord2d mouse = xy(e);

    // 3D mode
    if (getChart().getView().is3D()) {
      // Rotate if left button down
      if (AWTMouseUtilities.isLeftDown(e)) {
        Coord2d move = mouse.sub(prevMouse).div(100);
        rotate(move);
      }

      // Shift if right button down
      else if (AWTMouseUtilities.isRightDown(e)) {
        Coord2d move = mouse.sub(prevMouse);
        if (move.y != 0)
          shift(move.y / 500);
      }

    }

    // 2D mode
    else {
      // Record the mouse selection in progress
      Coord2d dragMouse = xy(e);

      if (maintainInAxis)
        maintainInAxis(dragMouse);


      mouseSelection.stop2D = dragMouse;
      mouseSelection.stop3D = screenToModel(dragMouse.x, dragMouse.y);

      getChart().getView().shoot();

    }

    prevMouse = mouse;
  }

  /**
   * When mouse button is released
   * 
   * <ul>
   * <li>In 3D, does nothing
   * <li>In 2D, perform the zoom according to the selection
   * </ul>
   */
  @Override
  public void mouseReleased(MouseEvent e) {

    // 2D mode
    if (getChart().getView().is2D()) {
      View view = getChart().getView();
      applyMouse2DSelection(view);
    }
  }

  /**
   * When mouse wheel rotates
   * 
   * <ul>
   * <li>In 3D, Update Z scale
   * <li>In 2D, does nothing
   * </ul>
   * 
   */
  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (getChart().getView().is2D()) {
      return;
    }

    // Check if mouse rate limiter wish to forbid this mouse drag instruction
    if (rateLimiter != null && !rateLimiter.rateLimitCheck()) {
      return;
    }

    stopThreadController();
    float factor = 1 + (e.getWheelRotation() / 10.0f);
    zoomZ(factor);
  }

  /**
   * When mouse moves on the canvas
   * <ul>
   * <li>In 3D, drop mouse position as it should not be displayed
   * <li>In 2D, store mouse position to overlay coordinates
   * </ul>
   */
  @Override
  public void mouseMoved(MouseEvent e) {
    View view = getChart().getView();

    //
    if (getChart().getView().is3D()) {
      mousePosition.event = null;
      mousePosition.projection = null;
    } else {
      mousePosition.event = e;
      mousePosition.projection = screenToModel(e);
    }
    view.shoot();

  }

  /** When mouse goes out of the canvas */
  @Override
  public void mouseExited(MouseEvent e) {

    // Reset mouse position memory as mouse exit the canvas
    mousePosition = new MousePosition();

    // Update display
    if (getChart() != null) {
      getChart().getView().shoot();

    }
  }

  /** When mouse is clicked, i.e. when button is pressed and released fast */
  @Override
  public void mouseClicked(MouseEvent e) {}

  /** When mouse enters the canvas */
  @Override
  public void mouseEntered(MouseEvent e) {}


  // ----------------------------------------------------------------------------
  // MOUSE 2D PROCESSING
  // ----------------------------------------------------------------------------

  /**
   * Perform the actual 2D ZOOM or UNZOOM according to the recorded mouse gesture.
   */
  protected void applyMouse2DSelection(View view) {

    boolean allowCrop = true;

    if (!mouseSelection.complete()) {
      return;
    }


    // Reset selection to UNZOOM
    if (!mouseSelection.growing()) {
      // getChart().getScene().getGraph().setClipBox(null);

      if (allowCrop)
        getChart().getScene().getGraph().setClipBox(null);

      view.setBoundMode(ViewBoundMode.AUTO_FIT);

    }
    // Or apply selection to ZOOM
    else {
      BoundingBox3d bounds = view.getBounds().clone();

      configureZoomAccordingTo2DView(view, bounds, mouseSelection);

      // Reset mouse selection now so that next rendering will hide it from screen
      mouseSelection = new MouseSelection();

      // Won't do anything for irrelevant bounds leading to a flat graph
      if (bounds.getXRange().getRange() <= 0 
       || bounds.getYRange().getRange() <= 0
       || bounds.getZRange().getRange() <= 0)
        return;

      //System.out.println("Will zoom to " + bounds);

      // Crop and zoom
      if (allowCrop) {
        boolean includeLimits = false; // to avoid inacurrate box scale
        getChart().getScene().getGraph().setClipBox(bounds, includeLimits, false);
      }
      view.setBoundsManual(bounds);


    }

  }

  /** Configure zoom according mouse selection, bounds, and current 2D (XY, XZ, etc) */
  protected void configureZoomAccordingTo2DView(View view, BoundingBox3d bounds,
      MouseSelection mouseSelection) {
    if (view.is2D_XY()) {
      bounds.setXmin(mouseSelection.min3DX());
      bounds.setXmax(mouseSelection.max3DX());
      bounds.setYmin(mouseSelection.min3DY());
      bounds.setYmax(mouseSelection.max3DY());
    } else if (view.is2D_XZ()) {
      bounds.setXmin(mouseSelection.min3DX());
      bounds.setXmax(mouseSelection.max3DX());
      bounds.setZmin(mouseSelection.min3DZ());
      bounds.setZmax(mouseSelection.max3DZ());
    } else if (view.is2D_YZ()) {
      bounds.setYmin(mouseSelection.min3DY());
      bounds.setYmax(mouseSelection.max3DY());
      bounds.setZmin(mouseSelection.min3DZ());
      bounds.setZmax(mouseSelection.max3DZ());
    } else {
      throw new RuntimeException("Unexpected 2D view");
    }

    // System.out.println("Mouse.Bounds : " + bounds);
  }

  /**
   * Modify the input coord2D to ensure the mouse value never stand outside of the axis bounds.
   */
  protected void maintainInAxis(Coord2d mouse) {
    View view = getChart().getView();
    View2DLayout layout = view.get2DLayout();

    BoundingBox3d bounds3d = getChart().getView().getBounds();



    // Get corners
    Coord3d cornerMin3D = null;
    Coord3d cornerMax3D = null;


    if (view.is2D_XY()) {
      // no flip : take X min for bottom left corner
      if (layout.isNoAxisFlipped()) {
        cornerMin3D = bounds3d.getCorners().getXminYminZmin();
        cornerMax3D = bounds3d.getCorners().getXmaxYmaxZmax();
      }
      // horizontal flip : take X max for bottom left corner
      else if (layout.isHorizontalAxisFlipOnly()) {
        cornerMin3D = bounds3d.getCorners().getXmaxYminZmin();
        cornerMax3D = bounds3d.getCorners().getXminYmaxZmax();
      }
      // vertical flip : take Y max for bottom left corner
      else if (layout.isVerticalAxisFlipOnly()) {
        cornerMin3D = bounds3d.getCorners().getXminYmaxZmin();
        cornerMax3D = bounds3d.getCorners().getXmaxYminZmax();
      }
      // both of the two above
      else if (layout.isBothAxisFlipped()) {
        cornerMin3D = bounds3d.getCorners().getXmaxYmaxZmin();
        cornerMax3D = bounds3d.getCorners().getXminYminZmax();
      }
    }

    else if (view.is2D_XZ()) {
      // no flip : take X min for bottom left corner
      if (layout.isNoAxisFlipped()) {
        cornerMin3D = bounds3d.getCorners().getXminYminZmin();
        cornerMax3D = bounds3d.getCorners().getXmaxYmaxZmax();
      }
      // horizontal flip : take X max for bottom left corner
      else if (layout.isHorizontalAxisFlipOnly()) {
        cornerMin3D = bounds3d.getCorners().getXmaxYminZmin();
        cornerMax3D = bounds3d.getCorners().getXminYmaxZmax();
      }
      // vertical flip : take Y max for bottom left corner
      else if (layout.isVerticalAxisFlipOnly()) {
        cornerMin3D = bounds3d.getCorners().getXminYminZmax();
        cornerMax3D = bounds3d.getCorners().getXmaxYmaxZmin();
      }
      // both of the two above
      else if (layout.isBothAxisFlipped()) {
        cornerMin3D = bounds3d.getCorners().getXmaxYminZmax();
        cornerMax3D = bounds3d.getCorners().getXminYmaxZmin();
      }
    }

    else if (view.is2D_YZ()) {
      // no flip : take X min for bottom left corner
      if (layout.isNoAxisFlipped()) {
        cornerMin3D = bounds3d.getCorners().getXminYminZmin();
        cornerMax3D = bounds3d.getCorners().getXmaxYmaxZmax();
      }
      // horizontal flip : take X max for bottom left corner
      else if (layout.isHorizontalAxisFlipOnly()) {
        cornerMin3D = bounds3d.getCorners().getXminYmaxZmin();
        cornerMax3D = bounds3d.getCorners().getXmaxYminZmax();
      }
      // vertical flip : take Y max for bottom left corner
      else if (layout.isVerticalAxisFlipOnly()) {
        cornerMin3D = bounds3d.getCorners().getXminYminZmax();
        cornerMax3D = bounds3d.getCorners().getXmaxYmaxZmin();
      }
      // both of the two above
      else if (layout.isBothAxisFlipped()) {
        cornerMin3D = bounds3d.getCorners().getXminYmaxZmax();
        cornerMax3D = bounds3d.getCorners().getXmaxYminZmin();
      }
    }

    // System.out.println("Corner min 3D : " + cornerMin3D);
    // System.out.println("Corner max 3D : " + cornerMax3D);

    // Project 3D bounds to 2D
    Coord3d cornerMin2D = modelToScreen(cornerMin3D); // bottom left
    Coord3d cornerMax2D = modelToScreen(cornerMax3D); // top right

    Coord2d scale = scaled?new Coord2d(1,1):getChart().getView().getPixelScale();
    
    cornerMin2D.x /= scale.x; 
    cornerMin2D.y /= scale.y; 
    cornerMax2D.x /= scale.x; 
    cornerMax2D.y /= scale.y; 
    
    // System.out.println(mouse + " corner min " + cornerMin2D + " corner max " + cornerMax2D);

    // System.out.println("Corner min 2D : " + cornerMin);
    // System.out.println("Corner max 2D : " + cornerMax);

    // Crop on X bounds
    if (mouse.x < cornerMin2D.x)
      mouse.x = cornerMin2D.x;
    if (mouse.x > cornerMax2D.x)
      mouse.x = cornerMax2D.x;

    // Crop on Y bounds
    int height = (int)(getChart().getCanvas().getRendererHeight() / scale.y);

    float flipedCornerMinY = height - cornerMin2D.y;
    float flipedCornerMaxY = height - cornerMax2D.y;
    float flipedMouseY = height - mouse.y;

    if (flipedMouseY < cornerMin2D.y)
      mouse.y = flipedCornerMinY;
    if (flipedMouseY > cornerMax2D.y)
      mouse.y = flipedCornerMaxY;
  }

  // ----------------------------------------------------------------------------
  // TRIGGER SLAVE THREAD
  // ----------------------------------------------------------------------------

  /**
   * Handles toggle between mouse rotation/auto rotation: double-click starts the animated rotation,
   * while simple click stops it.
   */
  protected boolean handleSlaveThread(MouseEvent e) {
    if (AWTMouseUtilities.isDoubleClick(e)) {
      if (threadController != null) {
        threadController.start();
        return true;
      }
    }
    if (threadController != null)
      threadController.stop();
    return false;
  }


  // ----------------------------------------------------------------------------
  // SCREEN TO MODEL, MORE SMARTLY THAN IN CAMERA, USING COLORBAR AND MARGINS
  // ----------------------------------------------------------------------------

  protected Coord2d xy(MouseEvent e) {
    return new Coord2d(x(e), y(e));
  }

  protected int y(MouseEvent e) {
    return e.getY();
  }

  protected int x(MouseEvent e) {
    return e.getX();
  }

  /**
   * Perform model to screen projection smartly, considering the effect of a colorbar on the actual
   * viewport.
   */
  protected Coord3d modelToScreen(Coord3d model) {
    IPainter painter = getChart().getPainter();

    painter.acquireGL();

    int[] viewport = getViewportExcludingColorbar();
    float modelView[] = painter.getModelViewAsFloat();
    float projection[] = painter.getProjectionAsFloat();

    Coord3d screen = painter.modelToScreen(model, viewport, modelView, projection);
    painter.releaseGL();

    return screen;
  }

  protected Coord3d screenToModel(MouseEvent e) {
    int x = x(e);
    int y = y(e);

    return screenToModel(x, y);
  }

  protected Coord3d screenToModel(float x, float y) {
    
    Coord2d scale = scaled?new Coord2d(1,1):getChart().getView().getPixelScale();
    
    // Flip the Y axis, cancelling the effect of hidpi to later get accurate mouse
    // position
    int height = (int)(getChart().getCanvas().getRendererHeight() / scale.y);
    Coord3d mouse = new Coord3d(x, height - y, 0);

    // Now scale according to HiDPI
    mouse.x *= scale.x;
    mouse.y *= scale.y;

    
    // Project to 3D
    Coord3d model = screenToModel(mouse);

    if (model == null) {
      // System.err.println("MouseEvent can not be projected for " + mouse);
    }

    return model;
  }

  /**
   * Perform screen to model projection smartly, considering the effect of a colorbar on the actual
   * viewport.
   */
  protected Coord3d screenToModel(Coord3d mouse) {
    IPainter painter = getChart().getPainter();

    painter.acquireGL();

    int[] viewport = getViewportExcludingColorbar();
    float modelView[] = painter.getModelViewAsFloat();
    float projection[] = painter.getProjectionAsFloat();

    Coord3d model = painter.screenToModel(mouse, viewport, modelView, projection);

    /*
     * if(model==null) { System.err.println("Mouse.screenToModel : output is null");
     * Array.print("Viewport", viewport); Array.print("Modelview", modelView);
     * Array.print("Projection", projection); }
     */

    painter.releaseGL();

    return model;
  }

  protected int[] getViewportExcludingColorbar() {

    int viewport[] = new int[4];

    // If there is a layout that allows something else than the 3D view to be displayed
    // (e.g. a colorbar), then we only get the viewport of this part

    IViewportLayout layout = getChart().getView().getLayout();

    if (layout instanceof ViewAndColorbarsLayout) {
      ViewAndColorbarsLayout viewLayout = (ViewAndColorbarsLayout) layout;
      ViewportConfiguration viewportConf = viewLayout.getSceneViewport();

      // if (viewportConf.getHeight() < mouse.y || viewportConf.getWidth() < mouse.x)
      // return null;

      // Get real viewport, i.e. bypass colorbar
      viewport[0] = 0;
      viewport[1] = 0;
      viewport[2] = viewportConf.getWidth();
      viewport[3] = viewportConf.getHeight();
    }
    // Otherwise we simply use the default viewport
    else {
      IPainter painter = getChart().getPainter();
      viewport = painter.getViewPortAsInt();
    }
    return viewport;
  }

  // ----------------------------------------------------------------------------
  // VALUE LABEL FORMAT AND DRAW
  // ----------------------------------------------------------------------------

  /**
   * Drawing primitive for mouse tooltips. Will format according to axis tick renderers and labels.
   * 
   * @param g2d
   * @param screenPosition
   * @param modelPosition
   * @param interline
   * @param leftAlign
   */
  protected void drawCoord(Graphics2D g2d, Coord2d screenPosition, Coord3d modelPosition,
      int interline, boolean leftAlign) {
    String d1 = null;
    String d2 = null;

    if (getChart().getView().is2D_XY()) {
      d1 = format(Axis.X, modelPosition.x);
      d2 = format(Axis.Y, modelPosition.y);
    } else if (getChart().getView().is2D_XZ()) {
      d1 = format(Axis.X, modelPosition.x);
      d2 = format(Axis.Z, modelPosition.y);
    } else if (getChart().getView().is2D_YZ()) {
      d1 = format(Axis.Y, modelPosition.x);
      d2 = format(Axis.Z, modelPosition.y);
    }

    drawCoord(g2d, screenPosition, interline, d1, d2, leftAlign);
  }

  protected void drawCoord(Graphics2D g2d, Coord2d screenPosition, int interline, String d1,
      String d2, boolean leftAlign) {
    int d1W = AWTGraphicsUtils.stringWidth(g2d, d1);
    int d2W = AWTGraphicsUtils.stringWidth(g2d, d2);

    int offsetX = 0;

    if (leftAlign)
      offsetX = Math.max(d1W, d2W);

    int space = interline + g2d.getFont().getSize();


    g2d.drawString(d1, screenPosition.x - offsetX, screenPosition.y);
    g2d.drawString(d2, screenPosition.x - offsetX, screenPosition.y + space);
  }

  protected String format(Axis axis, float value) {
    String label;
    ITickRenderer renderer;

    switch (axis) {

      case X:
        label = getChart().getAxisLayout().getXAxisLabel();
        renderer = getChart().getAxisLayout().getXTickRenderer();

        if (label == null)
          label = "x";

        if (renderer == null)
          return label + "=" + value;
        else
          return label + "=" + renderer.format(value);

      case Y:
        label = getChart().getAxisLayout().getYAxisLabel();
        renderer = getChart().getAxisLayout().getYTickRenderer();

        if (label == null)
          label = "y";

        if (renderer == null)
          return label + "=" + value;
        else
          return label + "=" + renderer.format(value);

      case Z:
        label = getChart().getAxisLayout().getZAxisLabel();
        renderer = getChart().getAxisLayout().getZTickRenderer();

        if (label == null)
          label = "z";

        if (renderer == null)
          return label + "=" + value;
        else
          return label + "=" + renderer.format(value);
      default:
        return "" + value;
    }
  }


  // ----------------------------------------------------------------------------
  // SHOW MOUSE MOVES WITH TOOLTIP
  // ----------------------------------------------------------------------------

  /**
   * Render a 2D position
   */
  class MouseMoveRenderer extends AbstractAWTRenderer2d {
    @Override
    public void paint(Graphics g, int canvasWidth, int canvasHeight) {
      Graphics2D g2d = (Graphics2D) g;

      // g2d.setColor(java.awt.Color.BLACK);

      selectionSettings.apply(g2d);


      if (mousePosition.event != null) {
        g2d.drawLine(mousePosition.event.getX() - 1, mousePosition.event.getY(),
            mousePosition.event.getX() + 1, mousePosition.event.getY());
        g2d.drawLine(mousePosition.event.getX(), mousePosition.event.getY() - 1,
            mousePosition.event.getX(), mousePosition.event.getY() + 1);

        if (mousePosition.projection != null && selectionSettings.annotateWithValues) {

          drawCoord(g2d, xy(mousePosition.event), mousePosition.projection,
              selectionSettings.annotationInterline, false);
        }
      }
    }
  }

  /**
   * Model for a mouse position.
   * 
   * Holds the 2D point of a position, and the projection of this 2D point in the 3D world.
   */
  class MousePosition {
    MouseEvent event;
    Coord3d projection;
  }

  // ----------------------------------------------------------------------------
  // MODEL AND RENDERER FOR RECTANGULAR SELECTION IN 2D
  // ----------------------------------------------------------------------------

  /**
   * Render a rectangular 2D selection in progress
   */
  class MouseDragRenderer extends AbstractAWTRenderer2d {

    @Override
    public void paint(Graphics g, int canvasWidth, int canvasHeight) {
      if (mouseSelection == null) {
        return;
      }
      // Do not render selection if start or stop point is missing
      if (!mouseSelection.complete()) {
        return;
      }
      // Do not render selection if a diagonal was drawn
      if (!mouseSelection.growing()) {
        return;
      }

      Graphics2D g2d = (Graphics2D) g;

      // Configure G2D with selection drawing settings
      selectionSettings.apply(g2d);

      // Draws rectangular selection
      int x = (int) (mouseSelection.min2DX());
      int y = (int) (mouseSelection.min2DY());
      int w = (int) (mouseSelection.max2DX() - mouseSelection.min2DX());
      int h = (int) (mouseSelection.max2DY() - mouseSelection.min2DY());

      if (ZoomAreaStyle.BORDER.equals(selectionSettings.zoomStyle)) {
        g2d.drawRect(x, y, w, h);
      } else if (ZoomAreaStyle.FILL.equals(selectionSettings.zoomStyle)) {
        Color rectColor = selectionSettings.color.alpha(selectionSettings.zoomRectangleAlpha);

        g2d.setColor(AWTColor.toAWT(rectColor));
        g2d.fillRect(x, y, w, h);
        g2d.setColor(AWTColor.toAWT(selectionSettings.color));
      }

      // Draw start coordinates on the top left corner
      if (mouseSelection.start3D != null && selectionSettings.annotateWithValues) {
        drawCoord(g2d, mouseSelection.start2D, mouseSelection.start3D,
            selectionSettings.annotationInterline, true);
      }

      // Draw stop coordinates on the bottom right corner
      if (mouseSelection.stop3D != null && selectionSettings.annotateWithValues) {
        drawCoord(g2d, mouseSelection.stop2D, mouseSelection.stop3D,
            selectionSettings.annotationInterline, false);
      }
    }
  }

  /**
   * Model for a mouse selection in progress.
   * 
   * Holds the two 2D points of a selection, and the projection of these 2D points in the 3D world.
   */
  static class MouseSelection {
    Coord3d start3D;
    Coord3d stop3D;
    Coord2d start2D;
    Coord2d stop2D;

    /** return true if start 2D is smaller than stop 2D */
    boolean growing(boolean bothDimensions) {
      if (complete()) {
        if (bothDimensions)
          return (start2D.x < stop2D.x) && (start2D.y <= stop2D.y);
        else
          return (start2D.x < stop2D.x) || (start2D.y <= stop2D.y);
      } else
        return true;
    }

    boolean growing() {
      return growing(false);
    }

    /** return true if selection is complete, i.e. has a starting and stopping point */
    boolean complete() {
      return start2D != null && stop2D != null;// && start3D!=null && stop3D!=null;
    }

    // ---------

    /** The min X value of the 2D point */
    public float min2DX() {
      return Math.min(stop2D.x, start2D.x);
    }

    /** The max X value of the 2D point */
    public float max2DX() {
      return Math.max(stop2D.x, start2D.x);
    }

    /** The min Y value of the 2D point */
    public float min2DY() {
      return Math.min(stop2D.y, start2D.y);
    }

    /** The max Y value of the 2D point */
    public float max2DY() {
      return Math.max(stop2D.y, start2D.y);
    }

    // ---------

    /** The min X value of the 2D point projected to 3D */
    public float min3DX() {
      return Math.min(stop3D.x, start3D.x);
    }

    /** The max X value of the 2D point projected to 3D */
    public float max3DX() {
      return Math.max(stop3D.x, start3D.x);
    }

    /** The min Y value of the 2D point projected to 3D */
    public float min3DY() {
      return Math.min(stop3D.y, start3D.y);
    }

    /** The max Y value of the 2D point projected to 3D */
    public float max3DY() {
      return Math.max(stop3D.y, start3D.y);
    }

    /** The min Z value of the 2D point projected to 3D */
    public float min3DZ() {
      return Math.min(stop3D.z, start3D.z);
    }

    /** The max Z value of the 2D point projected to 3D */
    public float max3DZ() {
      return Math.max(stop3D.z, start3D.z);
    }

  }

  // ----------------------------------------------------------------------------
  // SETTINGS FOR MOUSE ROLLOVER RENDERING
  // ----------------------------------------------------------------------------

  /**
   * Rendering setting of mouse selection
   */
  public static class MouseSelectionSettings {
    public static enum ZoomAreaStyle {
      BORDER, FILL
    }


    // Colors
    protected Color color = Color.GRAY.clone();
    protected float zoomRectangleAlpha = 0.5f;

    // Zoom
    ZoomAreaStyle zoomStyle = ZoomAreaStyle.FILL;

    // Border
    protected float[] borderDashPattern = {2f, 0f, 2f};
    protected BasicStroke borderStroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_ROUND, 1.0f, borderDashPattern, 2f);

    // Text
    protected boolean annotateWithValues = true;
    protected float annotationFontSizeFactor = 1f;
    protected int annotationInterline = 2;

    public void apply(Graphics2D g2d) {

      // Configure G2D with selection drawing settings
      g2d.setColor(AWTColor.toAWT(color));

      // Text
      String name = g2d.getFont().getFontName();
      int size = g2d.getFont().getSize();
      Font f = new Font(name, Font.PLAIN, (int) (size * annotationFontSizeFactor));
      g2d.setFont(f);

      // Stroke
      g2d.setStroke(borderStroke);
    }

    public Color getColor() {
      return color;
    }

    public void setColor(Color color) {
      this.color = color;
    }

    public float getZoomRectangleAlpha() {
      return zoomRectangleAlpha;
    }

    public void setZoomRectangleAlpha(float zoomRectangleAlpha) {
      this.zoomRectangleAlpha = zoomRectangleAlpha;
    }

    public ZoomAreaStyle getZoomStyle() {
      return zoomStyle;
    }

    public void setZoomStyle(ZoomAreaStyle zoomStyle) {
      this.zoomStyle = zoomStyle;
    }

    public float[] getBorderDashPattern() {
      return borderDashPattern;
    }

    public void setBorderDashPattern(float[] borderDashPattern) {
      this.borderDashPattern = borderDashPattern;
    }

    public BasicStroke getBorderStroke() {
      return borderStroke;
    }

    public void setBorderStroke(BasicStroke borderStroke) {
      this.borderStroke = borderStroke;
    }

    public boolean isAnnotateWithValues() {
      return annotateWithValues;
    }

    public void setAnnotateWithValues(boolean annotateWithValues) {
      this.annotateWithValues = annotateWithValues;
    }

    public float getAnnotationFontSizeFactor() {
      return annotationFontSizeFactor;
    }

    public void setAnnotationFontSizeFactor(float annotationFontSizeFactor) {
      this.annotationFontSizeFactor = annotationFontSizeFactor;
    }

    public int getAnnotationInterline() {
      return annotationInterline;
    }

    public void setAnnotationInterline(int annotationInterline) {
      this.annotationInterline = annotationInterline;
    }
  }

}
