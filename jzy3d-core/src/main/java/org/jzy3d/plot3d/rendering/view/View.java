package org.jzy3d.plot3d.rendering.view;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.events.IViewEventListener;
import org.jzy3d.events.IViewLifecycleEventListener;
import org.jzy3d.events.IViewPointChangedListener;
import org.jzy3d.events.ViewIsVerticalEvent;
import org.jzy3d.events.ViewLifecycleEvent;
import org.jzy3d.events.ViewPointChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Parallelepiped;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.primitives.axis.IAxis;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.selectable.Selectable;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.ICanvasListener;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.LightSet;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.transform.Scale;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;
import org.jzy3d.plot3d.transform.squarifier.ISquarifier;

/**
 * A {@link View} holds a {@link Scene}, a {@link LightSet}, an {@link ICanvas} to render into. It
 * is the responsability to layout a set of concrete {@link AbstractViewportManager}s such as the
 * {@Camera} rendering the scene or an {@link AWTImageViewport} for displaying an image in the same
 * window. On can control the {@link Camera} with a {@ViewController } and get notifyed by a
 * {@link IViewPointChangedListener} that the view point has changed. The control is relative to the
 * center of the {@link Scene} and is defined using polar coordinates.
 * 
 * The {@link View} supports post rendering through the addition of {@link Renderer2d}s whose
 * implementation can define Java2d calls to render on top on OpenGL2.
 * 
 * Last, the {@link View} offers the ability to get an {@link AxisBox} for embedding the
 * {@link Scene} and getting values along axes.
 * 
 * @author Martin Pernollet 
 */
public class View {

  protected static Logger LOGGER = LogManager.getLogger(View.class);


  // view setting
  protected CameraMode cameraMode;
  protected ViewPositionMode viewMode;
  protected ViewBoundMode boundsMode;
  protected Color backgroundColor = Color.BLACK;
  protected boolean axisDisplayed = true;
  protected boolean squared = true;
  protected float cameraRenderingSphereRadiusFactor = 1f;
  protected float cameraRenderingSphereRadiusFactorOnTop = 0.25f;
  /**
   * force to have all object maintained in screen, meaning axebox won't always keep the same size.
   */
  protected boolean maintainAllObjectsInView = false;
  /**
   * display a magenta parallelepiped around the "whole bounds" of the axis (box + labels) for
   * debugging purpose.
   */
  protected boolean displayAxisWholeBounds = false;


  // view objects
  protected Camera cam;
  protected IAxis axis;
  protected Quality quality;
  protected Scene scene;
  protected ICanvas canvas;
  protected IPainter painter;
  protected Scene annotations;
  protected Coord3d viewpoint;
  protected Coord3d center;
  protected Coord3d scaling;
  // the actual bounds, either auto or manual
  protected BoundingBox3d viewBounds;
  protected Chart chart;

  // view listeners
  protected List<IViewPointChangedListener> viewPointChangedListeners;
  protected List<IViewEventListener> viewEventListeners;
  protected List<IViewLifecycleEventListener> viewLifecycleListeners;
  protected boolean wasOnTopAtLastRendering;

  // view states
  protected boolean first = true;
  protected HiDPI hidpi = HiDPI.OFF;
  protected Coord2d pixelScale;
  protected boolean initialized = false;

  // constants
  public static final float PI_div2 = (float) Math.PI / 2;
  public static final float DISTANCE_DEFAULT = 2000;


  /** A viewpoint allowing to have min X and Y values near viewer, growing toward horizon. */
  public static final Coord3d VIEWPOINT_X_Y_MIN_NEAR_VIEWER =
      new Coord3d((Math.PI) + (Math.PI / 3), Math.PI / 3, DISTANCE_DEFAULT);

  /** A viewpoint where two corners of the axis box touch top and bottom lines of the canvas. */
  public static final Coord3d VIEWPOINT_AXIS_CORNER_TOUCH_BORDER =
      new Coord3d(Math.PI / 4, Math.PI / 3, DISTANCE_DEFAULT);

  /** A nice viewpoint to start the chart */
  public static final Coord3d VIEWPOINT_DEFAULT = VIEWPOINT_X_Y_MIN_NEAR_VIEWER;

  /** A nice viewpoint to start the chart */
  public static final Coord3d VIEWPOINT_DEFAULT_OLD =
      new Coord3d(Math.PI / 3, Math.PI / 3, DISTANCE_DEFAULT);



  protected boolean dimensionDirty = false;
  /**
   * can be set to true by the Renderer3d so that the View knows it is rendering due to a canvas
   * size change
   */
  protected boolean viewDirty = false;

  //protected BoundingBox3d initBounds;

  /**
   * Applies a factor to the default camera distance which is set to the radius of the scene bounds.
   * Changing this value also change the camera clipping planes.
   */
  protected float factorViewPointDistance = 2;

  /** A slave view won't clear its color and depth buffer before rendering */
  protected boolean slave = false;
  protected SpaceTransformer spaceTransformer = new SpaceTransformer();
  private ISquarifier squarifier;

  protected IViewOverlay viewOverlay;

  /**
   * Create a view attached to a Scene, with its own Camera and Axe. The initial view point is set
   * at {@link View.DEFAULT_VIEW}.
   * <p/>
   * The {@link Quality} allows setting the rendering capabilities that are set one time by the
   * init() method.
   */
  public View(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    initInstance(factory, scene, canvas, quality);
  }

  /**
   * Initialize a view object. Invoked by constructor
   * 
   * Method name is made clear to avoid confusion with {@link View#init()} which initialize open GL
   * context.
   * 
   * @param factory
   * @param scene
   * @param canvas
   * @param quality
   */
  public void initInstance(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    BoundingBox3d sceneBounds = getSceneGraphBounds(scene);

    this.viewpoint = VIEWPOINT_DEFAULT.clone();
    this.center = sceneBounds.getCenter();
    this.scaling = Coord3d.IDENTITY.clone();
    //this.viewBounds = null;
    this.viewBounds = new BoundingBox3d(-1, 1, -1, 1, -1, 1);
    this.viewMode = ViewPositionMode.FREE;
    this.boundsMode = ViewBoundMode.AUTO_FIT;
    this.cameraMode = CameraMode.ORTHOGONAL;

    //this.axis = factory.newAxe(sceneBounds, this);
    this.axis = factory.newAxe(viewBounds, this);
    
    this.cam = factory.newCamera(center);
    this.painter = factory.getPainterFactory().newPainter();
    this.painter.setCamera(cam);
    this.painter.setView(this);
    this.viewOverlay = factory.getPainterFactory().newViewOverlay();

    this.scene = scene;
    this.canvas = canvas;
    this.quality = quality;
    this.annotations = factory.newScene(false);

    this.viewEventListeners = new ArrayList<>();
    this.viewPointChangedListeners = new ArrayList<>();
    this.viewLifecycleListeners = new ArrayList<>();
    this.wasOnTopAtLastRendering = false;

    this.scene.getGraph().getStrategy().setView(this);

    this.spaceTransformer = new SpaceTransformer(); // apply no transform

    this.pixelScale = new Coord2d(1, 1);

    // applyHiDPIToFonts(quality.isHiDPIEnabled()?HiDPI.ON:HiDPI.OFF);
    configureHiDPIListener(canvas);
  }

  /**
   * Upon pixel scale change, either at startup or during execution of the program, the listener
   * will reconfigure the default font according to current HiDPI settings. This will reconfigure
   * anything that draws based on {@link AxisLayout#getFont()}, hence:
   * <ul>
   * <li>the font of the axis text renderer
   * <li>the font of the colorbar
   * </ul>
   */
  protected void configureHiDPIListener(ICanvas canvas) {
    canvas.addCanvasListener(new ICanvasListener() {
      @Override
      public void pixelScaleChanged(double pixelScaleX, double pixelScaleY) {
        pixelScale.x = (float) pixelScaleX;
        pixelScale.y = (float) pixelScaleY;

        if (pixelScaleX <= 1) {
          hidpi = HiDPI.OFF;
        } else {
          hidpi = HiDPI.ON;
        }

        axis.getLayout().applyFontSizePolicy();
      }
    });
  }

  /**
   * Return a copy of the currently known pixel scale as notified by the canvas.
   * 
   * If the View received no pixel scale change event, the pixel scale will be 0.
   */
  public Coord2d getPixelScale() {
    return pixelScale.clone();
  }

  /**
   * Return HiDPI status as ACTUALLY possible by the ICanvas on the current screen and computer,
   * regardless of the {@link Quality#setHiDPIEnabled(true)}.
   * 
   * Will always return {@link HiDPI.OFF} is chart is set to {@link Quality#setHiDPIEnabled(false)}
   * as this forces the canvas to NOT make use of HiDPI.
   * 
   * @return
   */
  public HiDPI getHiDPI() {
    return hidpi;
  }

  public IPainter getPainter() {
    return painter;
  }

  public Chart getChart() {
    return chart;
  }

  public void setChart(Chart chart) {
    this.chart = chart;
  }

  public boolean isSlave() {
    return slave;
  }

  public void setSlave(boolean slave) {
    this.slave = slave;
  }

  public void dispose() {
    axis.dispose();
    cam = null;
    viewEventListeners.clear();
    scene = null;
    canvas = null;
    quality = null;
  }

  /**
   * Current view selection into the mother Scene, and call to target canvas rendering.
   */
  public void shoot() {
    if (canvas != null)
      canvas.forceRepaint();
  }

  /**
   * Perform the 2d projection of all {@link Selectable} objects of the scene.
   *
   * The result of the projection can be retrieved on the objects's instances.
   */
  public void project() {
    painter.acquireGL();
    scene.getGraph().project(painter, cam);
    painter.releaseGL();
  }

  /** Perform the 3d projection of a 2d coordinate. */
  public Coord3d projectMouse(int x, int y) {
    painter.acquireGL();
    Coord3d p = cam.screenToModel(painter, new Coord3d(x, y, 0));
    painter.releaseGL();
    return p;
  }

  /**
   * Might be invoked by a {@link IScreenCanvas} to indicate that dimension changed and that
   * elements should be reprocessed at next rendering, e.g. 2d projections.
   */
  public void markDimensionDirty() {
    dimensionDirty = true;
  }

  /*******************************
   * GENERAL DISPLAY CONTROLS
   ***********************************/

  public void rotate(final Coord2d move) {
    rotate(move, true);
  }

  public void rotate(final Coord2d move, boolean updateView) {
    Coord3d eye = getViewPoint();
    eye.x -= move.x;
    eye.y += move.y;

    setViewPoint(eye, updateView);
    // fireControllerEvent(ControllerType.ROTATE, eye);
  }

  public void shift(final float factor) {
    shift(factor, true);
  }

  public void shift(final float factor, boolean updateView) {
    org.jzy3d.maths.Scale current = getScale();
    org.jzy3d.maths.Scale newScale = current.add(factor * current.getRange());
    setScale(newScale, updateView);
    // fireControllerEvent(ControllerType.SHIFT, newScale);
  }

  public void zoom(final float factor) {
    zoom(factor, true);
  }

  public void zoom(final float factor, boolean updateView) {
    org.jzy3d.maths.Scale current = getScale();
    double range = current.getMax() - current.getMin();

    if (range <= 0)
      return;

    float center = (current.getMax() + current.getMin()) / 2;
    float zmin = center + (current.getMin() - center) * (factor);
    float zmax = center + (current.getMax() - center) * (factor);

    // set min/max according to bounds
    org.jzy3d.maths.Scale scale = null;
    if (zmin < zmax)
      scale = new org.jzy3d.maths.Scale(zmin, zmax);
    else {
      if (factor < 1) // forbid to have zmin = zmax if we zoom in
        scale = new org.jzy3d.maths.Scale(center, center);
    }

    if (scale != null) {
      setScale(scale, updateView);
      // fireControllerEvent(ControllerType.ZOOM, scale);
    }
  }

  public void zoomX(final float factor) {
    zoomX(factor, true);
  }

  public void zoomX(final float factor, boolean updateView) {
    double range = getBounds().getXmax() - getBounds().getXmin();
    if (range <= 0)
      return;
    float center = (getBounds().getXmax() + getBounds().getXmin()) / 2;
    float min = center + (getBounds().getXmin() - center) * factor;
    float max = center + (getBounds().getXmax() - center) * factor;

    org.jzy3d.maths.Scale scale = null;
    if (min < max)
      scale = new org.jzy3d.maths.Scale(min, max);
    else {
      if (factor < 1) // forbid to have zmin = zmax if we zoom in
        scale = new org.jzy3d.maths.Scale(center, center);
    }
    if (scale != null) {
      BoundingBox3d bounds = getBounds();
      bounds.setXmin(scale.getMin());
      bounds.setXmax(scale.getMax());
      setBoundManual(bounds);
      if (updateView)
        shoot();
      // fireControllerEvent(ControllerType.ZOOM, scale);
    }
  }

  public void zoomY(final float factor) {
    zoomY(factor, true);
  }

  public void zoomY(final float factor, boolean updateView) {
    double range = getBounds().getYmax() - getBounds().getYmin();
    if (range <= 0)
      return;
    float center = (getBounds().getYmax() + getBounds().getYmin()) / 2;
    float min = center + (getBounds().getYmin() - center) * factor;
    float max = center + (getBounds().getYmax() - center) * factor;

    org.jzy3d.maths.Scale scale = null;
    if (min < max)
      scale = new org.jzy3d.maths.Scale(min, max);
    else {
      if (factor < 1) // forbid to have zmin = zmax if we zoom in
        scale = new org.jzy3d.maths.Scale(center, center);
    }
    if (scale != null) {
      BoundingBox3d bounds = getBounds();
      bounds.setYmin(scale.getMin());
      bounds.setYmax(scale.getMax());
      setBoundManual(bounds);
      if (updateView)
        shoot();
      // fireControllerEvent(ControllerType.ZOOM, scale);
    }
  }

  public void zoomZ(final float factor) {
    zoomZ(factor, true);
  }

  public void zoomZ(final float factor, boolean updateView) {
    double range = getBounds().getZmax() - getBounds().getZmin();
    if (range <= 0)
      return;
    float center = (getBounds().getZmax() + getBounds().getZmin()) / 2;
    float min = center + (getBounds().getZmin() - center) * factor;
    float max = center + (getBounds().getZmax() - center) * factor;

    org.jzy3d.maths.Scale scale = null;
    if (min < max)
      scale = new org.jzy3d.maths.Scale(min, max);
    else {
      if (factor < 1) // forbid to have zmin = zmax if we zoom in
        scale = new org.jzy3d.maths.Scale(center, center);
    }
    if (scale != null) {
      BoundingBox3d bounds = getBounds();
      bounds.setZmin(scale.getMin());
      bounds.setZmax(scale.getMax());
      setBoundManual(bounds);
      if (updateView)
        shoot();
      // fireControllerEvent(ControllerType.ZOOM, scale);
    }
  }

  /**
   * Z scale.
   * 
   * @see setScaleX, setScaleY, setScaleZ
   */
  public void setScale(org.jzy3d.maths.Scale scale) {
    setScaleZ(scale, true);
  }

  /**
   * Z scale.
   * 
   * @see setScaleX, setScaleY, setScaleZ
   */
  public void setScale(org.jzy3d.maths.Scale scale, boolean notify) {
    setScaleZ(scale, notify);
  }

  public void setScaleX(org.jzy3d.maths.Scale scale) {
    setScaleX(scale, true);
  }

  public void setScaleX(org.jzy3d.maths.Scale scale, boolean notify) {
    BoundingBox3d bounds = getBounds();
    bounds.setXmin(scale.getMin());
    bounds.setXmax(scale.getMax());
    setBoundManual(bounds);
    if (notify)
      shoot();
  }

  public void setScaleY(org.jzy3d.maths.Scale scale) {
    setScaleY(scale, true);
  }

  public void setScaleY(org.jzy3d.maths.Scale scale, boolean notify) {
    BoundingBox3d bounds = getBounds();
    bounds.setYmin(scale.getMin());
    bounds.setYmax(scale.getMax());
    setBoundManual(bounds);
    if (notify)
      shoot();
  }

  public void setScaleZ(org.jzy3d.maths.Scale scale) {
    setScaleZ(scale, true);
  }

  public void setScaleZ(org.jzy3d.maths.Scale scale, boolean notify) {
    BoundingBox3d bounds = getBounds();
    bounds.setZmin(scale.getMin());
    bounds.setZmax(scale.getMax());
    setBoundManual(bounds);
    if (notify)
      shoot();
  }

  public org.jzy3d.maths.Scale getScale() {
    return new org.jzy3d.maths.Scale(getBounds().getZmin(), getBounds().getZmax());
  }

  /**
   * Set the surrounding {@link AxisBox} dimensions, the {@link Camera} target and the colorbar
   * range.
   */
  public void lookToBox(BoundingBox3d box) {
    if (box.isReset()) {
      return;
    }
    
    center = box.getCenter();
    axis.setAxe(box);
    viewBounds = box;
  }

  /**
   * Return the central point of the view scene, that is the last bounding box center set by
   * {@link lookToBox(BoundingBox3d box)}
   */
  public Coord3d getCenter() {
    return center;
  }

  /** Get the {@link AxisBox}'s {@link BoundingBox3d} */
  public BoundingBox3d getBounds() {
    return axis.getBounds();
  }

  public ViewBoundMode getBoundsMode() {
    return boundsMode;
  }

  /** Set the {@link ViewPositionMode} applied to this view. */
  public void setViewPositionMode(ViewPositionMode mode) {
    this.viewMode = mode;
  }

  /** Return the {@link ViewPositionMode} applied to this view. */
  public ViewPositionMode getViewMode() {
    return viewMode;
  }

  /** Return the stretch ratio applied to the view */
  public Coord3d getScaling() {
    return scaling;
  }

  /**
   * Set the viewpoint using polar coordinates relative to the target (i.e. the center of the
   * scene). Only X and Y dimensions are required, as the distance to center will be computed
   * automatically by {@link updateCamera()}.
   * 
   * The input coordinate is polar and considers
   * <ul>
   * <li>x is azimuth in [0;2xPI]
   * <li>y is elevation in [-PI/2;+PI/2]. Will be clamped if out of bounds
   * <li>z is range (distance to center) but ignored there as it is processed automatically 
   * </ul>
   */
  public void setViewPoint(Coord3d polar, boolean updateView) {
    viewpoint = polar;
    viewpoint.y = viewpoint.y < -PI_div2 ? -PI_div2 : viewpoint.y;
    viewpoint.y = viewpoint.y > PI_div2 ? PI_div2 : viewpoint.y;
    if (updateView)
      shoot();

    fireViewPointChangedEvent(new ViewPointChangedEvent(this, polar));
  }

  /**
   * Set the viewpoint and query a view update.
   * 
   * 
   * @see {@link setViewPoint(Coord3d polar, boolean updateView)}
   */
  public void setViewPoint(Coord3d polar) {
    setViewPoint(polar, true);
  }

  /**
   * Get the viewpoint. The Z dimension is the one defined by {@link updateCamera()}, which depends
   * on the view scaling.
   * 
   * @see {@link setViewPoint(Coord3d polar, boolean updateView)}
   */
  public Coord3d getViewPoint() {
    return viewpoint;
  }

  /**
   * Return the last used view scaling that was set according to the {@link setSquared(boolean v)}
   * status.
   */
  public Coord3d getLastViewScaling() {
    return scaling;
  }

  /* CONTROLS ANNOTATIONS & GENERAL RENDERING */

  public void setAxis(IAxis axis) {
    this.axis = axis;
    updateBounds();
  }

  public IAxis getAxis() {
    return axis;
  }

  public boolean getSquared() {
    return this.squared;
  }

  public void setSquared(boolean status) {
    this.squared = status;
  }

  public boolean isAxisDisplayed() {
    return axisDisplayed;
  }

  public void setAxisDisplayed(boolean axisDisplayed) {
    this.axisDisplayed = axisDisplayed;
  }

  public void setBackgroundColor(Color color) {
    backgroundColor = color;
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public Camera getCamera() {
    return cam;
  }

  /**
   * Set the projection of this view, either Camera.ORTHOGONAL or Camera.PERSPECTIVE.
   */
  public void setCameraMode(CameraMode mode) {
    this.cameraMode = mode;
  }

  /**
   * Get the projection of this view, either CameraMode.ORTHOGONAL or CameraMode.PERSPECTIVE.
   */
  public CameraMode getCameraMode() {
    return cameraMode;
  }

  public void setMaximized(boolean status) {
    if (status)
      this.cam.setViewportMode(ViewportMode.STRETCH_TO_FILL);
    else
      this.cam.setViewportMode(ViewportMode.RECTANGLE_NO_STRETCH);
  }


  public static float CAMERA_RENDERING_SPHERE_RADIUS_FACTOR_VIEW_ON_TOP = 0.25f;


  /**
   * @see setter
   */
  public float getCameraRenderingSphereRadiusFactor() {
    return cameraRenderingSphereRadiusFactor;
  }

  /**
   * This allows zooming the 3d scene by editing the camera rendering sphere.
   * <ul>
   * <li>A value of 1 allows having an AxisBox with corners touching the top/bottom part of the
   * canvas.
   * <li>A value greater than 1 makes the rendering sphere bigger, hence the AxisBox appears
   * smaller.
   * </ul>
   */
  public void setCameraRenderingSphereRadiusFactor(float cameraRenderingSphereRadiusFactor) {
    this.cameraRenderingSphereRadiusFactor = cameraRenderingSphereRadiusFactor;
  }

  /**
   * @see setter
   */
  public float getCameraRenderingSphereRadiusFactorOnTop() {
    return cameraRenderingSphereRadiusFactorOnTop;
  }

  /**
   * This allows stretching the camera rendering sphere when the camera is on top of the scene
   * (meaning we are drawing in 2D).
   */
  public void setCameraRenderingSphereRadiusFactorOnTop(
      float cameraRenderingSphereRadiusFactorOnTop) {
    this.cameraRenderingSphereRadiusFactorOnTop = cameraRenderingSphereRadiusFactorOnTop;
  }

  public boolean isMaintainAllObjectsInView() {
    return maintainAllObjectsInView;
  }

  public void setMaintainAllObjectsInView(boolean maintainAllObjectsInView) {
    this.maintainAllObjectsInView = maintainAllObjectsInView;
  }

  public boolean isDisplayAxisWholeBounds() {
    return displayAxisWholeBounds;
  }

  public void setDisplayAxisWholeBounds(boolean displayAxisWholeBounds) {
    this.displayAxisWholeBounds = displayAxisWholeBounds;
  }

  public Scene getScene() {
    return scene;
  }

  public Rectangle getSceneViewportRectangle() {
    return cam.getRectangle();
  }

  public ICanvas getCanvas() {
    return canvas;
  }

  public Graph getAnnotations() {
    return annotations.getGraph();
  }

  public boolean addViewEventListener(IViewEventListener listener) {
    return viewEventListeners.add(listener);
  }

  public boolean removeViewOnTopEventListener(IViewEventListener listener) {
    return viewEventListeners.remove(listener);
  }

  protected void fireViewOnTopEvent(boolean isOnTop) {
    ViewIsVerticalEvent e = new ViewIsVerticalEvent(this);

    if (isOnTop)
      for (IViewEventListener listener : viewEventListeners)
        listener.viewVerticalReached(e);
    else
      for (IViewEventListener listener : viewEventListeners)
        listener.viewVerticalLeft(e);
  }

  protected void fireViewFirstRenderStarts() {
    for (IViewEventListener listener : viewEventListeners)
      listener.viewFirstRenderStarts();
  }

  public boolean addViewPointChangedListener(IViewPointChangedListener listener) {
    return viewPointChangedListeners.add(listener);
  }

  public boolean removeViewPointChangedListener(IViewPointChangedListener listener) {
    return viewPointChangedListeners.remove(listener);
  }

  protected void fireViewPointChangedEvent(ViewPointChangedEvent e) {
    for (IViewPointChangedListener vp : viewPointChangedListeners)
      vp.viewPointChanged(e);
  }

  public boolean addViewLifecycleChangedListener(IViewLifecycleEventListener listener) {
    return viewLifecycleListeners.add(listener);
  }

  public boolean removeViewLifecycleChangedListener(IViewLifecycleEventListener listener) {
    return viewLifecycleListeners.remove(listener);
  }

  protected void fireViewLifecycleHasInit(ViewLifecycleEvent e) {
    for (IViewLifecycleEventListener vp : viewLifecycleListeners)
      vp.viewHasInit(e);
  }

  protected void fireViewLifecycleWillRender(ViewLifecycleEvent e) {
    for (IViewLifecycleEventListener vp : viewLifecycleListeners)
      vp.viewWillRender(e);
  }

  /* */

  /**
   * Select between an automatic bounding (that allows fitting the entire scene graph), or a custom
   * bounding.
   */
  public void setBoundMode(ViewBoundMode mode) {
    boundsMode = mode;
    updateBounds();
  }

  /**
   * Set the bounds of the view according to the current {@link ViewBoundMode} , and orders a
   * {@link Camera.shoot()}.
   */
  public void updateBounds() {
    if (boundsMode == ViewBoundMode.AUTO_FIT)
      lookToBox(getSceneGraphBounds()); // set axe and camera
    else if (boundsMode == ViewBoundMode.MANUAL)
      lookToBox(viewBounds); // set axe and camera
    else
      throw new RuntimeException("Unknown bounds");
    shoot();
  }

  protected BoundingBox3d getSceneGraphBounds() {
    return getSceneGraphBounds(scene);
  }

  protected BoundingBox3d getSceneGraphBounds(Scene scene) {
    return squarifyGetSceneGraphBounds(scene);
  }

  /**
   * Update the bounds according to the scene graph whatever is the current {@link ViewBoundMode},
   * and orders a {@link Camera.shoot()}
   */
  public void updateBoundsForceUpdate(boolean refresh) {
    lookToBox(getSceneGraphBounds());
    if (refresh)
      shoot();
  }

  /**
   * Set a manual bounding box and switch the bounding mode to {ViewBoundMode.MANUAL}, meaning that
   * any call to {@link updateBounds()} will update view bounds to the current bounds.
   */
  public void setBoundManual(BoundingBox3d bounds) {
    boundsMode = ViewBoundMode.MANUAL;
    lookToBox(bounds);
  }

  /* GL */

  /**
   * The initialization function:
   * <ul>
   * <li>specifies general GL settings that impact the rendering quality and performance
   * (computation speed).
   * <li>enable light management
   * <li>load all required texture resources
   * <li>fix the current view bounds to the whole scene graph bounds
   * </ul>
   * <p/>
   * The rendering settings are set by the {@link Quality} given in the constructor parameters.
   */
  public void init() {
    initQuality();
    initLights();
    initResources();
    initBounds();
    
    initialized = true;
    
    fireViewLifecycleHasInit(null);
  }

  protected void initBounds() {
    if(viewBounds==null) {
      viewBounds = squarifyGetSceneGraphBounds(scene);
    }
    lookToBox(viewBounds);
  }

  public void initQuality() {
    painter.configureGL(quality);
  }

  public void initLights() {
    initLights(scene);
  }

  public void initLights(Scene scene) {
    scene.getLightSet().init(painter);
    scene.getLightSet().enableLightIfThereAreLights(painter);
  }

  public void initResources() {
    getScene().getGraph().mountAllGLBindedResources(painter);
    
    // refresh bounds as we may have mount VBO objects which NOW have bounds defined
    updateBounds();
  }

  /** Clear the color and depth buffer. */
  public void clear() {
    painter.clearColor(backgroundColor);
    painter.glClearDepth(1);

    //Console.println("View.clear with color", backgroundColor);
    
    if (!slave) {
      painter.glClearColorAndDepthBuffers();
    }
  }

  /* RENDERING */

  public void render() {
    fireViewLifecycleWillRender(null);
    
    renderBackground(0f, 1f);
    renderScene();
    renderOverlay();

    if (dimensionDirty)
      dimensionDirty = false;
  }

  /**
   * To be implemented (see AWTView)
   */
  public void renderBackground(float left, float right) {}

  /**
   * To be implemented (see AWTView)
   */
  public void renderBackground(ViewportConfiguration backgroundViewPort) {}

  public void renderScene() {
    renderScene(new ViewportConfiguration(canvas.getRendererWidth(), canvas.getRendererHeight()));
  }

  public void renderScene(float left, float right) {
    ViewportConfiguration vc =
        ViewportBuilder.column(canvas.getRendererWidth(), canvas.getRendererHeight(), left, right);
    renderScene(vc);
  }

  public void renderScene(ViewportConfiguration viewport) {

    // synchronized(this) {
    if (first) {
      fireViewFirstRenderStarts();
      first = false;
    }
    // }

    BoundingBox3d scaling = computeScaledViewBounds();
    updateCamera(viewport, scaling);
    
    painter.glShadeModel(quality.getColorModel());
    
    renderAxeBox();
    renderSceneGraph();
    renderAnnotations(cam);
  }

  /**
   * Not called anymore as alpha and blending setting are not supposed to change during lifetime of
   * a chart.
   * 
   * If this should change, then the entire {@link View#initQuality()} method should be invoked
   */
  @Deprecated
  public void updateQuality() {
    if (quality.isAlphaActivated())
      painter.glEnable_Blend();
    else
      painter.glDisable_Blend();
  }

  /* SCALE PROCESSING */

  protected Coord3d squarify() {
    return squarify(scene, boundsMode, viewBounds, spaceTransformer);
  }

  /**
   * Return a 3d scaling factor that allows scaling the scene into a square box, according to the
   * current ViewBoundMode.
   * <p/>
   * If the scene bounds are Infinite, NaN or zero, for a given dimension, the scaler will be set to
   * 1 on the given dimension.
   * 
   * @return a scaling factor for each dimension.
   */
  protected Coord3d squarify(Scene scene, ViewBoundMode boundmode, BoundingBox3d manualViewBounds,
      SpaceTransformer spaceTransformer) {
    // Get the view bounds
    BoundingBox3d bounds;
    if (boundmode == ViewBoundMode.AUTO_FIT) {
      bounds = squarifyGetSceneGraphBounds(scene);
    } else if (boundmode == ViewBoundMode.MANUAL) {
      /*
       * if(scene.getGraph().getClipBox()!=null) { bounds = squarifyGetSceneGraphBounds(scene); }
       * else {
       */
      bounds = manualViewBounds;
      // }
    } else {
      throw new RuntimeException("Unknown bounds mode");
    }

    // Compute factors
    float xLen = 1;
    float yLen = 1;
    float zLen = 1;
    float lmax = 1;

    if (bounds != null) {
      Coord3d range = squarifyComputeBoundsRanges(bounds);
      xLen = range.x;
      yLen = range.y;
      zLen = range.z;

      lmax = Math.max(Math.max(xLen, yLen), zLen);
    }

    if (Float.isInfinite(xLen) || Float.isNaN(xLen) || xLen == 0)
      xLen = 1;
    if (Float.isInfinite(yLen) || Float.isNaN(yLen) || yLen == 0)
      yLen = 1;
    if (Float.isInfinite(zLen) || Float.isNaN(zLen) || zLen == 0)
      zLen = 1;
    if (Float.isInfinite(lmax) || Float.isNaN(lmax) || lmax == 0)
      lmax = 1;

    // Return a scaler
    if (squarifier != null) {
      return squarifier.scale(xLen, yLen, zLen);
    } else {
      float xscale = (float) ((double) lmax / (double) xLen);
      float yscale = (float) ((double) lmax / (double) yLen);
      float zscale = (float) ((double) lmax / (double) zLen);
      return new Coord3d(xscale, yscale, zscale);
    }
  }

  /* LAYOUT */

  protected BoundingBox3d squarifyGetSceneGraphBounds(Scene scene) {
    return scene.getGraph().getBounds();
  }

  protected Coord3d squarifyComputeBoundsRanges(BoundingBox3d bounds) {
    if (spaceTransformer == null) {
      return bounds.getRange();
    } else {
      float xLen = spaceTransformer.getX().compute(bounds.getXmax())
          - spaceTransformer.getX().compute(bounds.getXmin());
      float yLen = spaceTransformer.getY().compute(bounds.getYmax())
          - spaceTransformer.getY().compute(bounds.getYmin());
      float zLen = spaceTransformer.getZ().compute(bounds.getZmax())
          - spaceTransformer.getZ().compute(bounds.getZmin());

      return new Coord3d(xLen, yLen, zLen);
    }
  }

  public BoundingBox3d computeScaledViewBounds() {
    scaling = computeSceneScaling();

    // -- Compute the bounds for computing cam distance, clipping planes,
    if (viewBounds == null)
      viewBounds = new BoundingBox3d(0, 1, 0, 1, 0, 1);

    BoundingBox3d boundsScaled = new BoundingBox3d();
    boundsScaled.add(viewBounds.scale(scaling));

    if (maintainAllObjectsInView) {
      boundsScaled.add(getSceneGraphBounds().scale(scaling));
      boundsScaled.add(axis.getWholeBounds().scale(scaling));
    }
    return boundsScaled;
  }

  public Coord3d computeSceneScaling() {
    return computeSceneScaling(scene, squared, boundsMode, viewBounds, spaceTransformer);
  }

  public Coord3d computeSceneScaling(Scene scene, boolean squared, ViewBoundMode boundmode,
      BoundingBox3d manualViewBounds, SpaceTransformer spaceTransformer) {
    if (squared)
      return squarify(scene, boundmode, manualViewBounds, spaceTransformer);
    else
      return Coord3d.IDENTITY.clone();
  }

  /* CAMERA PROCESSING */

  public void updateCamera(ViewportConfiguration viewport, BoundingBox3d boundsScaled) {
    float sceneRadius = (float) boundsScaled.getRadius() * factorViewPointDistance;
    updateCamera(viewport, boundsScaled, sceneRadius);
  }

  public void updateCamera(ViewportConfiguration viewport, BoundingBox3d bounds,
      float sceneRadiusScaled) {
    updateCamera(viewport, bounds, sceneRadiusScaled, viewMode, viewpoint, cam, cameraMode,
        factorViewPointDistance, center, scaling);
  }

  public void updateCamera(ViewportConfiguration viewport, BoundingBox3d bounds,
      float sceneRadiusScaled, ViewPositionMode viewmode, Coord3d viewpoint, Camera cam,
      CameraMode cameraMode, float factorViewPointDistance, Coord3d center, Coord3d scaling) {

    viewpoint.z = computeViewpointDistance(bounds, sceneRadiusScaled, factorViewPointDistance);

    Coord3d cameraTarget = computeCameraTarget(center, scaling);
    Coord3d cameraUp = computeCameraUp(viewpoint);
    Coord3d cameraEye = computeCameraEye(cameraTarget, viewmode, viewpoint);
    cam.setPosition(cameraEye, cameraTarget, cameraUp, scaling);

    triggerCameraUpEvents(viewpoint);

    computeCameraRenderingSphereRadius(cam, viewport, bounds);

    cam.setViewPort(viewport);
    cam.shoot(painter, cameraMode);
  }

  /**
   * Update the camera configuration without triggering the
   * {@link Camera#shoot(IPainter, CameraMode)} method.
   * 
   * This is useful in rare case where one need to manually invoke only a subset of OpenGL methods
   * that are invoked by shoot method.
   */
  public void updateCameraWithoutShooting(ViewportConfiguration viewport, BoundingBox3d bounds,
      float sceneRadiusScaled, ViewPositionMode viewmode, Coord3d viewpoint, Camera cam,
      float factorViewPointDistance, Coord3d center, Coord3d scaling) {

    viewpoint.z = computeViewpointDistance(bounds, sceneRadiusScaled, factorViewPointDistance);

    Coord3d cameraTarget = computeCameraTarget(center, scaling);
    Coord3d cameraUp = computeCameraUp(viewpoint);
    Coord3d cameraEye = computeCameraEye(cameraTarget, viewmode, viewpoint);
    cam.setPosition(cameraEye, cameraTarget, cameraUp, scaling);

    triggerCameraUpEvents(viewpoint);

    computeCameraRenderingSphereRadius(cam, viewport, bounds);
  }


  public float computeViewpointDistance(BoundingBox3d bounds, float sceneRadiusScaled,
      float factorViewPointDistance) {
    return (float) spaceTransformer.compute(bounds).getRadius();
  }

  protected Coord3d computeCameraTarget() {
    return computeCameraTarget(center, scaling);
  }

  protected Coord3d computeCameraEye(Coord3d target) {
    return computeCameraEye(target, viewMode, viewpoint);
  }

  protected Coord3d computeCameraTarget(Coord3d center, Coord3d scaling) {
    return center.mul(scaling);
  }

  protected Coord3d computeCameraEye(Coord3d target, ViewPositionMode viewmode, Coord3d viewpoint) {
    if (viewmode == ViewPositionMode.FREE) {
      return computeCameraEyeFree(viewpoint, target);
    } else if (viewmode == ViewPositionMode.TOP) {
      return computeCameraEyeTop(viewpoint, target);
    } else if (viewmode == ViewPositionMode.PROFILE) {
      return computeCameraEyeProfile(viewpoint, target);
    } else
      throw new RuntimeException("Unsupported ViewMode: " + viewmode);
  }

  protected Coord3d computeCameraEyeProfile(Coord3d viewpoint, Coord3d target) {
    Coord3d eye = viewpoint;
    eye.y = 0;
    eye = eye.cartesian().add(target);
    return eye;
  }

  protected Coord3d computeCameraEyeTop(Coord3d viewpoint, Coord3d target) {
    Coord3d eye = viewpoint;
    eye.x = -(float) Math.PI / 2; // on x
    eye.y = (float) Math.PI / 2; // on top
    eye = eye.cartesian().add(target);
    return eye;
  }

  protected Coord3d computeCameraEyeFree(Coord3d viewpoint, Coord3d target) {
    return viewpoint.cartesian().add(target);
  }

  protected void triggerCameraUpEvents(Coord3d viewpoint) {
    if (Math.abs(viewpoint.y) == PI_div2) { // handle "on-top" events
      if (!wasOnTopAtLastRendering) {
        wasOnTopAtLastRendering = true;
        fireViewOnTopEvent(true);
      }
    } else // handle "on-top" events
    if (wasOnTopAtLastRendering) {
      wasOnTopAtLastRendering = false;
      fireViewOnTopEvent(false);
    }
  }

  protected Coord3d computeCameraUp(Coord3d viewpoint) {
    if (Math.abs(viewpoint.y) == PI_div2) { // handle on top
      Coord2d direction = new Coord2d(viewpoint.x, viewpoint.z).cartesian();
      if (viewpoint.y > 0) {
        return new Coord3d(-direction.x, -direction.y, 0);
      } else {
        return new Coord3d(direction.x, direction.y, 0);
      }
    } else {
      return new Coord3d(0, 0, 1); // use z axis as up vector, if not on top
    }
  }

  public void computeCameraRenderingSphereRadius(Camera cam, ViewportConfiguration viewport,
      BoundingBox3d bounds) {
    if (viewMode == ViewPositionMode.TOP) {
      if (spaceTransformer != null)
        bounds = spaceTransformer.compute(bounds);

      float xdiam = bounds.getXRange().getRange();
      float ydiam = bounds.getYRange().getRange();
      float radius = Math.max(xdiam, ydiam) / 2;

      radius += (radius * CAMERA_RENDERING_SPHERE_RADIUS_FACTOR_VIEW_ON_TOP);

      cam.setRenderingSphereRadius(radius);
      correctCameraPositionForIncludingTextLabels(painter, viewport);
    } else {
      if (spaceTransformer != null) {
        bounds = spaceTransformer.compute(bounds);
      }
      double radius = bounds.getRadius();

      cam.setRenderingSphereRadius((float) radius * cameraRenderingSphereRadiusFactor);
    }
  }

  protected void correctCameraPositionForIncludingTextLabels(IPainter painter,
      ViewportConfiguration viewport) {

  }

  /* AXE BOX RENDERING */

  protected void renderAxeBox() {
    renderAxeBox(axis, scene, cam, scaling, axisDisplayed);
  }

  protected void renderAxeBox(IAxis axe, Scene scene, Camera camera, Coord3d scaling,
      boolean axeBoxDisplayed) {
    if (axeBoxDisplayed) {

      painter.glMatrixMode_ModelView();

      scene.getLightSet().disable(painter);

      axe.setScale(scaling);
      axe.draw(painter);

      if (displayAxisWholeBounds) { // for debug
        AxisBox abox = (AxisBox) axe;
        BoundingBox3d box = abox.getWholeBounds();
        Parallelepiped p = new Parallelepiped(box);
        p.setFaceDisplayed(false);
        p.setWireframeColor(Color.MAGENTA);
        p.setWireframeDisplayed(true);
        p.draw(painter);
      }

      scene.getLightSet().enableLightIfThereAreLights(painter);
    }
  }

  /* SCENE GRAPH RENDERING */

  public void renderSceneGraph() {
    renderSceneGraph(true);
  }

  public void renderSceneGraph(boolean light) {
    renderSceneGraph(light, cam, scene, scaling);
  }

  public void renderSceneGraph(boolean light, Camera camera, Scene scene, Coord3d scaling) {
    if (light) {
      scene.getLightSet().apply(painter, scaling);
    }

    Transform transform = new Transform(new Scale(scaling));
    scene.getGraph().setTransform(transform);
    scene.getGraph().draw(painter);
  }

  /* OVERLAY RENDERING */

  public void renderOverlay() {
    renderOverlay(new ViewportConfiguration(canvas));
  }

  public void renderOverlay(ViewportConfiguration viewportConfiguration) {
    if(viewOverlay!=null) {
      viewOverlay.render(this, viewportConfiguration, painter);
    }
  }

  public void renderAnnotations(Camera camera) {
    Transform transform = new Transform(new Scale(scaling));
    annotations.getGraph().setTransform(transform);
    annotations.getGraph().draw(painter);
  }

  /* */

  public SpaceTransformer getSpaceTransformer() {
    return spaceTransformer;
  }

  public void setSpaceTransformer(SpaceTransformer transformer) {
    this.spaceTransformer = transformer;
  }

  public void setSquarifier(ISquarifier squarifier) {
    this.squarifier = squarifier;
  }

  public ISquarifier getSquarifier() {
    return squarifier;
  }

  public boolean isInitialized() {
    return initialized;
  }
  
  
}
