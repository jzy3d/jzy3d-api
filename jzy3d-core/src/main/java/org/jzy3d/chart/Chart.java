package org.jzy3d.chart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Scale;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/**
 * {@link Chart} is a convenient object that gather all components required to render a 3d scene for
 * plotting.
 * 
 * @author Martin Pernollet
 */
public class Chart {
  private static final int MOUSE_PICK_SIZE_DEFAULT = 10;
  private static final String DEFAULT_WINDOW_TITLE = "Jzy3d";
  public static final Quality DEFAULT_QUALITY = Quality.Intermediate();

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
    getView().setBackgroundColor(Color.BLACK);
    getAxisLayout().setGridColor(Color.WHITE);
    getAxisLayout().setMainColor(Color.WHITE);
    return this;
  }

  public Chart white() {
    getView().setBackgroundColor(Color.WHITE);
    getAxisLayout().setGridColor(Color.BLACK);
    getAxisLayout().setMainColor(Color.BLACK);
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
    getMouse().getSlaveThreadController().stop();
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
    return getMouse().getSlaveThreadController();
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
    return open(DEFAULT_WINDOW_TITLE, new Rectangle(0, 0, 600, 600));
  }

  public IFrame open(String title) {
    return open(title, new Rectangle(0, 0, 600, 600));
  }

  public IFrame open(String title, int width, int height) {
    return open(title, new Rectangle(0, 0, width, height));
  }

  public IFrame open(int width, int height) {
    return open(DEFAULT_WINDOW_TITLE, new Rectangle(0, 0, width, height));
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
      frame = getFactory().getPainterFactory().newFrame(this, rect, title);
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
   *        multiple drawable at the same time.
   * @return
   */
  public Chart add(Drawable drawable, boolean updateView) {
    drawable.setSpaceTransformer(getView().getSpaceTransformer());
    getScene().getGraph().add(drawable, updateView);
    return this;
  }


  public void remove(Drawable drawable) {
    getScene().getGraph().remove(drawable);
  }

  public void remove(Drawable drawable, boolean updateViews) {
    getScene().getGraph().remove(drawable, updateViews);
  }

  /* ADDING LIGHTS */

  public Light addLight(Coord3d position) {
    return addLight(position, Color.BLUE, new Color(0.8f, 0.8f, 0.8f), Color.WHITE, 1);
  }

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
