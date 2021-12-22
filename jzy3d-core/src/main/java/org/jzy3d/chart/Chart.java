package org.jzy3d.chart;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.events.IViewPointChangedListener;
import org.jzy3d.events.ViewPointChangedEvent;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Scale;
import org.jzy3d.maths.Statistics;
import org.jzy3d.maths.TicToc;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.primitives.Wireframeable;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import org.jzy3d.plot3d.rendering.view.lod.LODCandidates;
import org.jzy3d.plot3d.rendering.view.lod.LODPerf;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/**
 * {@link Chart} is a convenient object that gather all components required to render a 3d scene for
 * plotting.
 * 
 * @author Martin Pernollet
 */
public class Chart {
  protected static Logger logger = LogManager.getLogger(Chart.class);

  private static final int MOUSE_PICK_SIZE_DEFAULT = 10;
  private static final String DEFAULT_WINDOW_TITLE = "Jzy3d";
  public static final Quality DEFAULT_QUALITY = Quality.Intermediate();

  protected static final int LOD_BOUNDS_ONLY_RENDER_TIME_MS = 30;
  protected static final int LOD_EVAL_TRIALS = 3;
  protected static final int LOD_EVAL_MAX_EVAL_DURATION_MS = 500; // ms

  protected IChartFactory factory;

  protected Quality quality;
  protected ChartScene scene;
  protected View view;
  protected ICanvas canvas;

  protected Coord3d previousViewPointFree;
  protected Coord3d previousViewPointTop;
  protected Coord3d previousViewPointProfile;

  protected ArrayList<AbstractCameraController> controllers;

  protected ICameraMouseController mouse;
  protected IMousePickingController mousePicking;
  protected ICameraKeyController keyboard;
  protected IScreenshotKeyController screenshotKey;

  protected Light lightOnCamera;
  protected Light[] lightPairOnCamera;


  public Chart(IChartFactory factory, Quality quality) {
    this.factory = factory;
    this.quality = quality;

    // Set up controllers
    controllers = new ArrayList<>(1);

    // Set up the scene and 3d canvas
    scene = factory.newScene(quality.isAlphaActivated());
    canvas = factory.getPainterFactory().newCanvas(factory, scene, quality);

    // Set up the view
    view = canvas.getView();
    view.setBackgroundColor(Color.WHITE);
    view.setChart(this);
  }

  protected Chart() {

  }

  /* HELPERS TO PRETTIFY CHARTS */

  public Chart black() {
    return color(Color.BLACK, Color.WHITE);
  }
  
  public Chart white() {
    return color(Color.WHITE, Color.BLACK);
  }

  public Chart color(Color background, Color axis) {
    getView().setBackgroundColor(background);
    getAxisLayout().setGridColor(axis);
    getAxisLayout().setMainColor(axis);
    return this;
  }

  
  public Chart view2d() {
    IAxisLayout axe = getAxisLayout();
    axe.setZAxeLabelDisplayed(false);
    axe.setTickLineDisplayed(false);

    View view = getView();
    view.setViewPositionMode(ViewPositionMode.TOP);
    view.setSquared(true);
    view.getCamera().setViewportMode(ViewportMode.STRETCH_TO_FILL);
    return this;
  }

  /** Alias for {@link display()} */
  public IFrame show(Rectangle rectangle, String title) {
    return display(rectangle, title);
  }

  public IFrame display(Rectangle rectangle, String title) {
    return getFactory().getPainterFactory().newFrame(this, rectangle, title);
  }

  public void clear() {
    scene.clear();
    view.shoot();
  }

  public void dispose() {
    setAnimated(false);

    clearControllerList();
    if (canvas != null)
      canvas.dispose();
    if (scene != null)
      scene.dispose(); // view is disposed by scene
    canvas = null;
    scene = null;
  }

  /**
   * Trigger a chart rendering. Only usefull if chart Quality.is
   */
  public void render() {
    view.shoot();
  }

  public void setAnimated(boolean status) {
    getQuality().setAnimated(status);

    updateAnimationThreadWithQualitySettings();
  }

  protected void updateAnimationThreadWithQualitySettings() {

    // Start or stop animator on canvas
    if (getCanvas() instanceof IScreenCanvas) {
      IScreenCanvas screenCanvas = (IScreenCanvas) getCanvas();

      if (getQuality().isAnimated()) {
        screenCanvas.getAnimation().start();
      } else {
        screenCanvas.getAnimation().stop();
      }
    }

    // Apply relevant animation setting to controller so they behave accordingly
    configureMouseWithAnimator();
    configureKeyboardWithAnimator();
  }

  public void startAnimation() {
    setAnimated(true);
  }

  public void stopAnimation() {
    setAnimated(false);
  }

  public void stopAllThreads() {
    getMouse().getThread().stop();
    stopAnimation();
  }


  /**
   * Compute screenshot and save to file
   */
  public void screenshot(File file) throws IOException {
    canvas.screenshot(file);
  }

  public Object screenshot() throws IOException {
    return canvas.screenshot();
  }

  public void updateProjectionsAndRender() {
    getView().shoot();
    getView().project();
    render();
  }

  public View newView() {
    View v = scene.newView(canvas, quality);
    v.setSlave(true);
    return v;
  }

  /* CONTROLLERS */

  public ICameraMouseController addMouseCameraController() {
    if (mouse == null) {
      mouse = getFactory().getPainterFactory().newMouseCameraController(this);
      configureMouseWithAnimator();
    }
    return mouse;
  }

  /**
   * Switch between on demand/continuous rendering keep to false if animated to avoid double
   * rendering keep to true otherwise the mouse does not update
   */
  protected void configureMouseWithAnimator() {
    if (mouse == null)
      return;

    mouse.setUpdateViewDefault(!getQuality().isAnimated());

    // Rotation thread
    CameraThreadController rotation = mouse.getThread();
    rotation.setUpdateViewDefault(!getQuality().isAnimated());
  }

  public IMousePickingController addMousePickingController(int clickWidth) {
    if (mousePicking == null) {
      mousePicking = getFactory().getPainterFactory().newMousePickingController(this, clickWidth);
    }
    return mousePicking;
  }

  public ICameraKeyController addKeyboardCameraController() {
    if (keyboard == null) {
      keyboard = getFactory().getPainterFactory().newKeyboardCameraController(this);

      configureKeyboardWithAnimator();
    }

    return keyboard;
  }

  /**
   * Switch between on demand/continuous rendering keep to false if animated to avoid double
   * rendering keep to true otherwise the mouse does not update
   */
  protected void configureKeyboardWithAnimator() {
    if (keyboard != null)
      keyboard.setUpdateViewDefault(!getQuality().isAnimated());
  }

  public IScreenshotKeyController addKeyboardScreenshotController() {
    if (screenshotKey == null) {
      screenshotKey = getFactory().getPainterFactory().newKeyboardScreenshotController(this);
    }
    return screenshotKey;
  }

  public ICameraMouseController getMouse() {
    if (mouse == null)
      return addMouseCameraController();
    else
      return mouse;
  }

  public CameraThreadController getThread() {
    return getMouse().getThread();
  }

  public IMousePickingController getMousePicking() {
    if (mousePicking == null)
      return addMousePickingController(MOUSE_PICK_SIZE_DEFAULT);
    else
      return mousePicking;
  }

  public ICameraKeyController getKeyboard() {
    if (keyboard == null)
      return addKeyboardCameraController();
    else
      return keyboard;
  }

  public IScreenshotKeyController getScreenshotKey() {
    if (screenshotKey == null)
      return addKeyboardScreenshotController();
    else
      return screenshotKey;
  }

  /**
   * Add a {@link AbstractCameraController} to this {@link Chart}. Warning: the {@link Chart} is not
   * the owner of the controller. Disposing the chart thus just unregisters the controllers, but
   * does not handle stopping and disposing controllers.
   */
  public void addController(AbstractCameraController controller) {
    controller.register(this);
    controllers.add(controller);
  }

  public void removeController(AbstractCameraController controller) {
    controller.unregister(this);
    controllers.remove(controller);
  }

  protected void clearControllerList() {
    for (AbstractCameraController controller : controllers)
      controller.unregister(this);
    controllers.clear();
  }

  /* FRAME */

  public IFrame open() {
    return open(DEFAULT_WINDOW_TITLE, new Rectangle(0, 0, 800, 600));
  }

  public IFrame open(String title) {
    return open(title, new Rectangle(0, 0, 800, 600));
  }

  public IFrame open(String title, int width, int height) {
    return open(title, new Rectangle(0, 0, width, height));
  }

  public IFrame open(int width, int height) {
    return open(DEFAULT_WINDOW_TITLE, new Rectangle(0, 0, width, height));
  }

  /**
   * A one liner to wait a bit, mainly for test purpose, when needing to have a frame opened and a
   * chart initialized
   */
  public void sleep(int mili) {
    try {
      Thread.sleep(mili);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Open the frame if it was not opened before
   * 
   * @param title
   * @param rect
   * @return
   */
  public IFrame open(String title, Rectangle rect) {
    if (frame == null) {
      IPainterFactory painterFactory = getFactory().getPainterFactory();
      if(!painterFactory.isOffscreen()) {
        frame = getFactory().getPainterFactory().newFrame(this, rect, title);
      }
      else {
        logger.warn("Chart is configured for being offscreen. Did not open any frame. May disable call to open");
      }
    }

    // start animator according to quality
    updateAnimationThreadWithQualitySettings();

    return frame;
  }


  IFrame frame = null;

  /* ADDING DRAWABLES */

  /**
   * Add a list of drawables and refresh the view of the scene once they are all added.
   * 
   * @param drawables
   * @return
   */
  public Chart add(List<? extends Drawable> drawables) {
    for (Drawable drawable : drawables) {
      add(drawable, false);
    }
    getView().updateBounds();
    updateLightsOnCameraPositions();
    return this;
  }

  /**
   * Add a drawable and refresh the view of the scene once it is added.
   * 
   * @param drawable
   * @return
   */
  public Chart add(Drawable drawable) {
    add(drawable, true);
    return this;
  }

  /**
   * Add a drawable to the scene graph of the chart.
   * 
   * If the view holds a {@link SpaceTransformer}, then it will be applied to the drawable. This can
   * be reset by later calling {@link Drawable#setSpaceTransformer(null)}
   * 
   * @param drawable
   * @param updateView states if the view should be updated immediately. Should be false if adding
   *        multiple drawable at the same time. The effect of not updating view is that view bounds
   *        won't be updated as well
   * @return
   */
  public Chart add(Drawable drawable, boolean updateView) {
    drawable.setSpaceTransformer(getView().getSpaceTransformer());

    // 1. Add object
    getScene().getGraph().add(drawable, false);

    // 2. Mount if it requires a GPU context
    if (drawable instanceof IGLBindedResource) {

      if (view.isInitialized()) {
        //logger.warn(
        //    drawable + " must be added to chart before the view has initialized, hence before the chart is open.");

        // We are in here in the application thread, no in AWT thread, so we have to acquire
        // GL context, so that the drawable gets mounted with a usable GL instance
        getPainter().acquireGL();

        ((IGLBindedResource) drawable).mount(getPainter());

        // And we kindly release GL to let AWT render again
        getPainter().releaseGL();

        //logger.warn("Chart.add binded resource with box " + drawable.getBounds());

      } else {
        logger.warn(
            drawable + " will be initialized later since the view is not initialized yet. "
            + "Calling chart.getView().getBounds() won't return anything relevant until chart.open() gets called");
      }

      // getView().initResources(); // invoke loading GL binded resource in case this drawable
      // requires it

    }

    // 3. Update the bounds ONLY AFTER vbo have been mounted, since their bounds are only
    // available at that time
    if (updateView) {
      getView().updateBounds();
    }

    updateLightsOnCameraPositions();
    return this;
  }

  /**
   * Add a drawable by first evaluating its rendering performance onscreen from worse
   * ({@link LODSetting.Bounds#ON} to most good looking rendering. This method is useful when using
   * facing low performance rendering, e.g. because one chose the fallback EmulGL renderer over
   * native.
   * 
   * <h2>Using dynamic level of details</h2>
   * 
   * This requires to have a properly configured {@link AdaptiveMouseController} as shown below
   * 
   * <pre>
   * <code>
   * chart.add(myDrawable, new LODCandidates());
   * 
   * AdaptiveRenderingPolicy policy = new AdaptiveRenderingPolicy();
   * policy.optimizeForRenderingTimeLargerThan = 80;// ms
   * policy.optimizeByPerformanceKnowledge = true;
   * 
   * EmulGLSkin skin = EmulGLSkin.on(chart);
   * skin.getMouse().setPolicy(policy);
   * </code>
   * </pre>
   * 
   * <h2>Algorithm</h2>
   * 
   * Each Level of Detail configuration is described by a {@link LODSetting}. All {@link LODSetting}
   * are ranked in a {@link LODCandidates} instance that indicates which are the most good looking
   * settings. We define the best with lowest ID, the worse with highest ID.
   * 
   * <h3>Training</h3> LOD are evaluated in reverse order, as soon as the drawable is added to the
   * chart. Evaluation store a rendering time for each {@link LODSetting}
   * <ul>
   * <li>LOD 3 took 40ms
   * <li>LOD 2 took 60ms
   * <li>LOD 1 took 80ms
   * <li>LOD 0 took 100ms
   * </ul>
   * 
   * <h3>Applying</h3>
   * 
   * As soon as the mouse start dragging camera, the mouse controller will seek an acceptable LOD to
   * reach the target
   * <ul>
   * <li>LOD 0 took 100ms // rejected
   * <li>LOD 1 took 80ms // rejected
   * <li>LOD 2 took 60ms // selected
   * <li>LOD 3 took 40ms // ignored
   * </ul>
   * Now if the target time is 30ms and none match the setting, then the fastest configuration is
   * applied
   * <ul>
   * <li>LOD 0 took 100ms // rejected
   * <li>LOD 1 took 80ms // rejected
   * <li>LOD 2 took 60ms // rejected
   * <li>LOD 3 took 40ms // selected : minimal rendering time above threshold
   * </ul>
   * 
   * If no LOD configuration is given in the list, then nothing will be applied.
   * 
   * Note that a mouse is generated on the fly to register all rendering performance and later
   * decide the best to use when rotating. The mouse is initialized after evaluating performance to
   * ensure the user won't try to trigger rotations before evaluation finishes.
   * 
   * In addition to call this method, one should enable the mouse policy allowing to use these
   * performance evaluations.
   * 
   * @param drawable
   * @return
   */
  public Chart add(Drawable drawable, LODCandidates candidates) {
    add(drawable, true);
    // getView().updateBounds();

    Wireframeable w = drawable.asWireframeable();

    if (w != null) {
      LODPerf perf = new LODPerf(candidates);

      TicToc t = new TicToc();
      for (LODSetting lodSetting : perf.getCandidates().getReverseRank()) {

        // Do not evaluate bounding box rendering which is always short
        // so we simply keep a low constant
        if (lodSetting.isBoundsOnly()) {
          perf.setScore(lodSetting, LOD_BOUNDS_ONLY_RENDER_TIME_MS);
        }

        // Evaluate other LOD config
        else {
          lodSetting.apply(w);

          getQuality().setColorModel(lodSetting.getColorModel());

          int trials = 3;

          double[] values = new double[trials];
          int k = 0;

          // Evaluate tree times and keep the mean, unless
          // we encounter a poor rendering time
          for (int i = 0; i < trials; i++) {
            t.tic();
            render();
            t.toc();
            values[k++] = t.elapsedMilisecond();

            // Skip full evaluation if rendering time is too high
            if (values[i] > LOD_EVAL_MAX_EVAL_DURATION_MS) {
              perf.setScore(lodSetting, values[i]);
            }
            logger.debug(lodSetting.getName() + " (" + i + ") took " + values[i] + "ms");
          }

          if (k == trials) {
            perf.setScore(lodSetting, Statistics.median(values, true));
          }
        }
      }

      // Wait for the end of evaluation to add mouse.
      ICameraMouseController mouse = addMouseCameraController();

      mouse.setLODPerf(perf);
    }
    /*
     * MouseListener m = (MouseListener)mouse; MouseMotionListener m2 = (MouseMotionListener)mouse;
     * 
     * int n = 500; int i = 20;
     * 
     * m.mousePressed(mouseEvent((Component)getCanvas(), i, i)); for (i = i+1; i < n; i++) {
     * m2.mouseDragged(mouseEvent((Component)getCanvas(), i, i)); render(); }
     * m.mouseReleased(mouseEvent((Component)getCanvas(), n, n));
     */
    // mouse.

    // getCanvas().forceRepaint();
    ((Component) getCanvas()).repaint();
    ((Component) getCanvas()).repaint();

    return this;
  }

  protected static MouseEvent mouseEvent(Component sourceCanvas, int x, int y) {
    return new MouseEvent(sourceCanvas, 0, 0, 0, x, y, 100, 100, 1, false, 0);
  }



  public void remove(Drawable drawable) {
    getScene().getGraph().remove(drawable);
  }

  public void remove(Drawable drawable, boolean updateViews) {
    getScene().getGraph().remove(drawable, updateViews);
  }

  public void remove(List<? extends Drawable> drawables) {
    for (Drawable drawable : drawables) {
      remove(drawable, false);
    }
    getView().updateBounds();
  }

  /* ADDING LIGHTS */


  /**
   * Add a light at the given position, using the {@link Light#DEFAULT_COLOR} for the three coloring
   * settings.
   * 
   * Warning : The default color being white, any polygon in pure RED, pure GREEN or pure BLUE will
   * have the exact same color when using a light. See {@link Light} documentation for this, or
   * change the light color or object color a bit.
   */
  public Light addLight(Coord3d position) {
    return addLight(position, Light.DEFAULT_COLOR.clone(), Light.DEFAULT_COLOR.clone(),
        Light.DEFAULT_COLOR.clone());
  }

  /**
   * Add a light at the given position.
   * 
   * @param ambiant
   * @param diffuse
   * @param specular
   * @return
   */
  public Light addLight(Coord3d position, Color ambiant, Color diffuse, Color specular) {
    return addLight(position, ambiant, diffuse, specular, 1);
  }

  public Light addLight(Coord3d position, Color colorForAll) {
    return addLight(position, colorForAll, colorForAll, colorForAll);
  }

  /**
   * Add a light at the given position.
   * 
   * @param ambiant
   * @param diffuse
   * @param specular
   * @return
   */
  public Light addLight(Coord3d position, Color ambiant, Color diffuse, Color specular,
      float radius) {
    Light light = new Light();
    light.setPosition(position);
    light.setAmbiantColor(ambiant);
    light.setDiffuseColor(diffuse);
    light.setSpecularColor(specular);
    light.setRepresentationRadius(radius);
    getScene().add(light);
    return light;
  }

  /**
   * Add a light that is attached to camera, which is moved as soon as the viewpoint changes, using
   * the {@link Light#DEFAULT_COLOR} for the three coloring settings.
   * 
   * Warning : The default color being white, any polygon in pure RED, pure GREEN or pure BLUE will
   * have the exact same color when using a light. See {@link Light} documentation for this, or
   * change the light color or object color a bit.
   */
  public Light addLightOnCamera() {
    return addLightOnCamera(Light.DEFAULT_COLOR.clone());
  }

  public Light addLightOnCamera(Color colorForAll) {
    return addLightOnCamera(colorForAll, colorForAll, colorForAll);
  }

  /**
   * Add a light that is attached to camera, which is moved as soon as the viewpoint changes.
   * 
   * If this light was already created, the initial instance is returned, even if the color setting
   * do not match.
   * 
   * @param ambiant
   * @param diffuse
   * @param specular
   * @return
   */
  public Light addLightOnCamera(Color ambiant, Color diffuse, Color specular) {
    if (lightOnCamera == null) {
      Coord3d position = getView().getCamera().getEye();
      lightOnCamera = addLight(position, ambiant, diffuse, specular);

      getView().addViewPointChangedListener(new IViewPointChangedListener() {
        @Override
        public void viewPointChanged(ViewPointChangedEvent e) {
          updateLightOnCameraPosition();
        }
      });
    }

    return lightOnCamera;
  }

  protected void updateLightOnCameraPosition() {
    lightOnCamera.setPosition(getView().getCamera().getEye());
  }


  public Light[] addLightPairOnCamera() {
    return addLightPairOnCamera(Light.DEFAULT_COLOR.clone());
  }

  public Light[] addLightPairOnCamera(Color colorForAll) {
    return addLightPairOnCamera(colorForAll, colorForAll, colorForAll);
  }

  /**
   * Add a light pair syncronized to camera. Top light is 45° above the camera, bottom light is 45°
   * below the camera.
   * 
   * If these lights were already created, the initial instances are returned, even if the color
   * setting do not match.
   * 
   * @param ambiant
   * @param diffuse
   * @param specular
   * @return
   */

  public Light[] addLightPairOnCamera(Color ambiant, Color diffuse, Color specular) {
    if (lightPairOnCamera == null) {
      Coord3d viewCenter = getView().getCenter(); // cartesian
      Coord3d viewPointPolar = getView().getViewPoint(); // polar coords
      Coord3d lightPointUpPolar = viewPointPolar.add(0, (float) Math.PI / 2, 0); // polar coords
      Coord3d lightPointDownPolar = viewPointPolar.add(0, -(float) Math.PI / 2, 0); // polar coords
      Coord3d lightPointUp = lightPointUpPolar.cartesian().addSelf(viewCenter); // cartesian
      Coord3d lightPointDown = lightPointDownPolar.cartesian().addSelf(viewCenter); // cartesian

      Light lightUp = addLight(lightPointUp, ambiant, diffuse, specular);
      Light lightDown = addLight(lightPointDown, ambiant, diffuse, specular);

      lightPairOnCamera = new Light[2];
      lightPairOnCamera[0] = lightUp;
      lightPairOnCamera[1] = lightDown;

      getView().addViewPointChangedListener(new IViewPointChangedListener() {
        @Override
        public void viewPointChanged(ViewPointChangedEvent e) {
          updateLightPairOnCameraPosition();
        }
      });
    }
    return lightPairOnCamera;
  }

  protected void updateLightPairOnCameraPosition() {
    Coord3d viewCenter = getView().getCenter(); // cartesian
    Coord3d viewPointPolar = getView().getViewPoint(); // polar coords
    Coord3d lightPointUpPolar = viewPointPolar.add(0, (float) Math.PI / 2, 0); // polar coords
    Coord3d lightPointDownPolar = viewPointPolar.add(0, -(float) Math.PI / 2, 0); // polar
                                                                                  // coords
    Coord3d lightPointUp = lightPointUpPolar.cartesian().addSelf(viewCenter); // cartesian
    Coord3d lightPointDown = lightPointDownPolar.cartesian().addSelf(viewCenter); // cartesian

    lightPairOnCamera[0].setPosition(lightPointUp);
    lightPairOnCamera[1].setPosition(lightPointDown);
  }

  protected void updateLightsOnCameraPositions() {
    if (lightOnCamera != null)
      updateLightOnCameraPosition();
    if (lightPairOnCamera != null)
      updateLightPairOnCameraPosition();
  }


  /* SHORTCUTS */

  public void setAxeDisplayed(boolean status) {
    view.setAxisDisplayed(status);
    view.shoot();
  }

  public void setViewPoint(Coord3d viewPoint) {
    view.setViewPoint(viewPoint);
    view.shoot();
  }

  public Coord3d getViewPoint() {
    return view.getViewPoint();
  }

  public ViewPositionMode getViewMode() {
    return view.getViewMode();
  }

  public void setScale(org.jzy3d.maths.Scale scale, boolean notify) {
    view.setScale(scale, notify);
  }

  public void setScale(Scale scale) {
    setScale(scale, true);
  }

  public Scale getScale() {
    return new Scale(view.getBounds().getZmin(), view.getBounds().getZmax());
  }

  public IPainter getPainter() {
    return getView().getPainter();
  }

  /* */

  public void setViewMode(ViewPositionMode mode) {
    // Store current view mode and view point in memory
    ViewPositionMode previous = view.getViewMode();
    if (previous == ViewPositionMode.FREE)
      previousViewPointFree = view.getViewPoint();
    else if (previous == ViewPositionMode.TOP)
      previousViewPointTop = view.getViewPoint();
    else if (previous == ViewPositionMode.PROFILE)
      previousViewPointProfile = view.getViewPoint();

    // Set new view mode and former view point
    view.setViewPositionMode(mode);
    if (mode == ViewPositionMode.FREE)
      view.setViewPoint(
          previousViewPointFree == null ? View.VIEWPOINT_DEFAULT.clone() : previousViewPointFree);
    else if (mode == ViewPositionMode.TOP)
      view.setViewPoint(
          previousViewPointTop == null ? View.VIEWPOINT_DEFAULT.clone() : previousViewPointTop);
    else if (mode == ViewPositionMode.PROFILE)
      view.setViewPoint(previousViewPointProfile == null ? View.VIEWPOINT_DEFAULT.clone()
          : previousViewPointProfile);

    view.shoot();
  }

  /* */

  public float flip(float y) {
    return canvas.getRendererHeight() - y;
  }

  /* GETTERS */

  public View view() {
    return getView();
  }

  public View getView() {
    return view;
  }

  public ChartScene getScene() {
    return scene;
  }

  public ICanvas getCanvas() {
    return canvas;
  }

  public IAxisLayout getAxisLayout() {
    return getView().getAxis().getLayout();
  }

  public IChartFactory getFactory() {
    return factory;
  }

  public List<AbstractCameraController> getControllers() {
    return controllers;
  }

  public Quality getQuality() {
    return quality;
  }

  public void setQuality(Quality quality) {
    this.quality = quality;
  }


}
