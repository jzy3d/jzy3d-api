package org.jzy3d.chart;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLCapabilities;

import org.jzy3d.bridge.IFrame;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.lights.LightKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.factories.ChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.view.Renderer2d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

/**
 * {@link Chart} is a convenient object that gather all components required to
 * render a 3d scene for plotting.
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
        this(components, quality, DEFAULT_WINDOWING_TOOLKIT, org.jzy3d.global.Settings.getInstance().getGLCapabilities());
    }

    public Chart(Quality quality, String windowingToolkit) {
        this(new ChartComponentFactory(), quality, windowingToolkit, org.jzy3d.global.Settings.getInstance().getGLCapabilities());
    }

    public Chart(IChartComponentFactory factory, Quality quality, String windowingToolkit) {
        this(factory, quality, windowingToolkit, org.jzy3d.global.Settings.getInstance().getGLCapabilities());
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

    public IFrame display(Rectangle rectangle, String title) {
        return getFactory().newFrame(this, rectangle, title);
    }

    public void clear() {
        scene.clear();
        view.shoot();
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
        canvas.dispose();
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

    public BufferedImage screenshot() {
        return canvas.screenshot();
    }

    /**
     * Compute screenshot and save to file
     */
    public BufferedImage screenshot(String filename) throws IOException {
        BufferedImage s = screenshot();
        ImageIO.write(s, "png", new File(filename));
        return s;
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

    /* */

    public ICameraMouseController addMouseController() {
        return getFactory().newMouseController(this);
    }

    public ICameraKeyController addKeyController() {
        return getFactory().newKeyController(this);
    }

    public IScreenshotKeyController addScreenshotKeyController() {
        return getFactory().newScreenshotKeyController(this);
    }

    public IFrame open(String title, int width, int height) {
        return open(title, new Rectangle(0, 0, width, height));
    }

    public IFrame open(String title, Rectangle rect) {
        return getFactory().newFrame(this, rect, title);
    }

    /**
     * Add a {@link AbstractCameraController} to this {@link Chart}. Warning:
     * the {@link Chart} is not the owner of the controller. Disposing the chart
     * thus just unregisters the controllers, but does not handle stopping and
     * disposing controllers.
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

    public List<AbstractCameraController> getControllers() {
        return controllers;
    }

    public void addDrawable(AbstractDrawable drawable) {
        getScene().getGraph().add(drawable);
    }

    public void addDrawable(AbstractDrawable drawable, boolean updateViews) {
        getScene().getGraph().add(drawable, updateViews);
    }

    public void addDrawable(List<? extends AbstractDrawable> drawables, boolean updateViews) {
        getScene().getGraph().add(drawables, updateViews);
    }

    public void addDrawable(List<? extends AbstractDrawable> drawables) {
        getScene().getGraph().add(drawables);
    }

    public void removeDrawable(AbstractDrawable drawable) {
        getScene().getGraph().remove(drawable);
    }

    public void removeDrawable(AbstractDrawable drawable, boolean updateViews) {
        getScene().getGraph().remove(drawable, updateViews);
    }

    public void addRenderer(Renderer2d renderer2d) {
        view.addRenderer2d(renderer2d);
    }

    public void removeRenderer(Renderer2d renderer2d) {
        view.removeRenderer2d(renderer2d);
    }

    public Light addLight(Coord3d position) {
        return addLight(position, Color.BLUE, new Color(0.8f, 0.8f, 0.8f), Color.WHITE, 1);
    }
    
    public Light addLight(Coord3d position, Color ambiant, Color diffuse, Color specular, int radius) {
        Light light = new Light();
        light.setPosition(position);
        light.setAmbiantColor(ambiant);
        light.setDiffuseColor(diffuse);
        light.setSpecularColor(specular);
        light.setRepresentationRadius(radius);
        getScene().add(light);
        return light;
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

    public void setAxeDisplayed(boolean status) {
        view.setAxeBoxDisplayed(status);
        view.shoot();
    }

    public IChartComponentFactory getFactory() {
        return factory;
    }

    public String getWindowingToolkit() {
        return windowingToolkit;
    }

    public void setViewPoint(Coord3d viewPoint) {
        view.setViewPoint(viewPoint);
        view.shoot();
    }

    public Coord3d getViewPoint() {
        return view.getViewPoint();
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

    public ViewPositionMode getViewMode() {
        return view.getViewMode();
    }

    /* */

    public void setScale(org.jzy3d.maths.Scale scale, boolean notify) {
        view.setScale(scale, notify);
    }

    public void setScale(Scale scale) {
        setScale(scale, true);
    }

    public Scale getScale() {
        return new Scale(view.getBounds().getZmin(), view.getBounds().getZmax());
    }

    public float flip(float y) {
        return canvas.getRendererHeight() - y;
    }

    public GLCapabilities getCapabilities() {
        return capabilities;
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
