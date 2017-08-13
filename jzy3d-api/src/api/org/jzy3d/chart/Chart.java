package org.jzy3d.chart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.bridge.IFrame;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.chart.factories.ChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.util.texture.TextureData;

/**
 * {@link Chart} is a convenient object that gather all components required to render a 3d scene for plotting.
 * 
 * @author Martin Pernollet
 */
public class Chart {
    public static Quality DEFAULT_QUALITY = Quality.Intermediate;
    public static String DEFAULT_WINDOWING_TOOLKIT = "awt";

    public Chart() {
        this(DEFAULT_QUALITY, DEFAULT_WINDOWING_TOOLKIT);
    }

    public Chart(Quality quality) {
        this(quality, DEFAULT_WINDOWING_TOOLKIT);
    }

    public Chart(String windowingToolkit) {
        this(DEFAULT_QUALITY, windowingToolkit);
    }

    public Chart(IChartComponentFactory components, Quality quality) {
        this(components, quality, DEFAULT_WINDOWING_TOOLKIT, org.jzy3d.chart.Settings.getInstance().getGLCapabilities());
    }

    public Chart(Quality quality, String windowingToolkit) {
        this(new ChartComponentFactory(), quality, windowingToolkit, org.jzy3d.chart.Settings.getInstance().getGLCapabilities());
    }

    public Chart(IChartComponentFactory factory, Quality quality, String windowingToolkit) {
        this(factory, quality, windowingToolkit, org.jzy3d.chart.Settings.getInstance().getGLCapabilities());
    }

    public Chart(IChartComponentFactory factory, Quality quality, String windowingToolkit, GLCapabilities capabilities) {
        this.capabilities = capabilities;
        this.windowingToolkit = windowingToolkit;
        this.factory = factory;
        this.quality = quality;

        // Set up controllers
        controllers = new ArrayList<AbstractCameraController>(1);

        // Set up the scene and 3d canvas
        scene = factory.newScene(quality.isAlphaActivated());
        canvas = factory.newCanvas(scene, quality, windowingToolkit, capabilities);

        // Set up the view
        view = canvas.getView();
        view.setBackgroundColor(Color.WHITE);
        view.setChart(this);
    }

    /* HELPERS TO PRETTIFY CHARTS */

    public Chart black() {
        getView().setBackgroundColor(Color.BLACK);
        getAxeLayout().setGridColor(Color.WHITE);
        getAxeLayout().setMainColor(Color.WHITE);
        return this;
    }

    public Chart white() {
        getView().setBackgroundColor(Color.WHITE);
        getAxeLayout().setGridColor(Color.BLACK);
        getAxeLayout().setMainColor(Color.BLACK);
        return this;
    }

    public Chart view2d() {
        IAxeLayout axe = getAxeLayout();
        axe.setZAxeLabelDisplayed(false);
        axe.setTickLineDisplayed(false);

        View view = getView();
        view.setViewPositionMode(ViewPositionMode.TOP);
        view.setSquared(true);
        view.getCamera().setViewportMode(ViewportMode.STRETCH_TO_FILL);
        return this;
    }

    public void setAnimated(boolean status) {
        getQuality().setAnimated(status);

        if (getCanvas() instanceof IScreenCanvas) {
            IScreenCanvas sCanvas = ((IScreenCanvas) getCanvas());

            if (status) {
                sCanvas.getAnimator().start();
            } else {
                sCanvas.getAnimator().stop();
            }
        }
    }

    /** Alias for {@link display()} */
    public IFrame show(Rectangle rectangle, String title) {
        return display(rectangle, title);
    }

    public IFrame display(Rectangle rectangle, String title) {
        return getFactory().newFrame(this, rectangle, title);
    }

    public void clear() {
        scene.clear();
        view.shoot();
    }

    public void pauseAnimator() {
        if (canvas != null && canvas instanceof IScreenCanvas) {
            GLAnimatorControl control = ((IScreenCanvas) canvas).getAnimator();
            if (control != null && control.isAnimating()) {
                control.pause();
            }
        }
    }

    public void resumeAnimator() {
        if (canvas != null && canvas instanceof IScreenCanvas) {
            GLAnimatorControl control = ((IScreenCanvas) canvas).getAnimator();
            if (control != null && control.isPaused()) {
                control.resume();
            }
        }
    }

    public void startAnimator() {
        if (canvas != null && canvas instanceof IScreenCanvas) {
            GLAnimatorControl control = ((IScreenCanvas) canvas).getAnimator();
            if (control != null && !control.isStarted()) {
                control.start();
            }
        }
    }

    public void stopAnimator() {
        if (canvas != null && canvas instanceof IScreenCanvas) {
            GLAnimatorControl control = ((IScreenCanvas) canvas).getAnimator();
            if (control != null)
                control.stop();
        }
    }

    public void dispose() {
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

    public TextureData screenshot() {
        return canvas.screenshot();
    }

    /**
     * Compute screenshot and save to file
     */
    public TextureData screenshot(File file) throws IOException {
        return canvas.screenshot(file);
    }

    public void updateProjectionsAndRender() {
        getView().shoot();
        getView().project();
        render();
    }

    public View newView() {
        View v = scene.newView(canvas, quality);// factory.newView(scene,
                                                // canvas, quality);
        v.setSlave(true);
        return v;
    }

    /* CONTROLLERS */

    public ICameraMouseController addMouseCameraController() {
        return getFactory().newMouseCameraController(this);
    }

    public IMousePickingController addMousePickingController(int clickWidth) {
        return getFactory().newMousePickingController(this, clickWidth);
    }

    public ICameraKeyController addKeyboardCameraController() {
        return getFactory().newKeyboardCameraController(this);
    }

    public IScreenshotKeyController addKeyboardScreenshotController() {
        return getFactory().newKeyboardScreenshotController(this);
    }

    /**
     * Add a {@link AbstractCameraController} to this {@link Chart}. Warning: the {@link Chart} is not the owner of the controller. Disposing the chart thus just unregisters the controllers, but does not handle stopping and disposing controllers.
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

    public IFrame open(String title, int width, int height) {
        return open(title, new Rectangle(0, 0, width, height));
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
            frame = getFactory().newFrame(this, rect, title);
        }
        return frame;
    }

    IFrame frame = null;

    /* ADDING DRAWABLES */

    public Chart add(List<? extends AbstractDrawable> drawables) {
        for (AbstractDrawable drawable : drawables) {
            add(drawable, false);
        }
        getView().updateBounds();
        return this;
    }

    public Chart add(AbstractDrawable drawable) {
        add(drawable, true);
        return this;
    }

    /**
     * Add a drawable to the scene graph of the chart.
     * 
     * If the view holds a {@link SpaceTransformer}, then it will be applied to the drawable. This can be reset by later calling {@link AbstractDrawable#setSpaceTransformer(null)}
     * 
     * @param drawable
     * @param updateView
     *            states if the view should be updated immediately. Should be false if adding multiple drawable at the same time.
     * @return
     */
    public Chart add(AbstractDrawable drawable, boolean updateView) {
        getScene().getGraph().add(drawable, updateView);
        drawable.setSpaceTransformer(getView().getSpaceTransformer());
        return this;
    }

    @Deprecated
    public void addDrawable(AbstractDrawable drawable) {
        getScene().getGraph().add(drawable);
    }

    @Deprecated
    public void addDrawable(AbstractDrawable drawable, boolean updateViews) {
        getScene().getGraph().add(drawable, updateViews);
    }

    @Deprecated
    public void addDrawable(List<? extends AbstractDrawable> drawables, boolean updateViews) {
        getScene().getGraph().add(drawables, updateViews);
    }

    @Deprecated
    public void addDrawable(List<? extends AbstractDrawable> drawables) {
        getScene().getGraph().add(drawables);
    }

    public void removeDrawable(AbstractDrawable drawable) {
        getScene().getGraph().remove(drawable);
    }

    public void removeDrawable(AbstractDrawable drawable, boolean updateViews) {
        getScene().getGraph().remove(drawable, updateViews);
    }

    /* ADDING LIGHTS */

    public Light addLight(Coord3d position) {
        return addLight(position, Color.BLUE, new Color(0.8f, 0.8f, 0.8f), Color.WHITE, 1);
    }

    public Light addLight(Coord3d position, Color ambiant, Color diffuse, Color specular, float radius) {
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
        view.setAxeBoxDisplayed(status);
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
            view.setViewPoint(previousViewPointFree == null ? View.DEFAULT_VIEW.clone() : previousViewPointFree);
        else if (mode == ViewPositionMode.TOP)
            view.setViewPoint(previousViewPointTop == null ? View.DEFAULT_VIEW.clone() : previousViewPointTop);
        else if (mode == ViewPositionMode.PROFILE)
            view.setViewPoint(previousViewPointProfile == null ? View.DEFAULT_VIEW.clone() : previousViewPointProfile);

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

    public IAxeLayout getAxeLayout() {
        return getView().getAxe().getLayout();
    }

    public IChartComponentFactory getFactory() {
        return factory;
    }

    public String getWindowingToolkit() {
        return windowingToolkit;
    }

    public GLCapabilities getCapabilities() {
        return capabilities;
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

    /* */

    protected IChartComponentFactory factory;

    protected Quality quality;
    protected GLCapabilities capabilities;
    protected String windowingToolkit;

    protected ChartScene scene;
    protected View view;
    protected ICanvas canvas;

    protected Coord3d previousViewPointFree;
    protected Coord3d previousViewPointTop;
    protected Coord3d previousViewPointProfile;

    protected ArrayList<AbstractCameraController> controllers;
}
