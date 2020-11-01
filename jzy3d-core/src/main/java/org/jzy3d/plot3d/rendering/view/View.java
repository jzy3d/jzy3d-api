package org.jzy3d.plot3d.rendering.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.events.IViewIsVerticalEventListener;
import org.jzy3d.events.IViewLifecycleEventListener;
import org.jzy3d.events.IViewPointChangedListener;
import org.jzy3d.events.ViewIsVerticalEvent;
import org.jzy3d.events.ViewLifecycleEvent;
import org.jzy3d.events.ViewPointChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.primitives.selectable.Selectable;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
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

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;

/**
 * A {@link View} holds a {@link Scene}, a {@link LightSet}, an {@link ICanvas}
 * to render into. It is the responsability to layout a set of concrete
 * {@link AbstractViewportManager}s such as the {@Camera} rendering the
 * scene or an {@link AWTImageViewport} for displaying an image in the same
 * window. On can control the {@link Camera} with a {@ViewController
 * } and get notifyed by a {@link IViewPointChangedListener}
 * that the view point has changed. The control is relative to the center of the
 * {@link Scene} and is defined using polar coordinates.
 * 
 * The {@link View} supports post rendering through the addition of
 * {@link Renderer2d}s whose implementation can define Java2d calls to render on
 * top on OpenGL2.
 * 
 * Last, the {@link View} offers the ability to get an {@link AxeBox} for
 * embedding the {@link Scene} and getting values along axes.
 * 
 * @author Martin Pernollet
 */
public class View {
    protected static Logger LOGGER = Logger.getLogger(View.class);

    /** A view may optionnaly know its parent chart. */
    protected Chart chart;
    public static float STRETCH_RATIO = 0.25f;

    /**
     * force to have all object maintained in screen, meaning axebox won't
     * always keep the same size.
     */
    protected boolean MAINTAIN_ALL_OBJECTS_IN_VIEW = false;
    /** display a magenta parallelepiped (debug). */
    protected boolean DISPLAY_AXE_WHOLE_BOUNDS = false;
    protected boolean axeBoxDisplayed = true;
    protected boolean squared = true;
    protected Camera cam;
    protected IAxe axe;
    protected Quality quality;
    protected Scene scene;
    protected ICanvas canvas;
    protected Painter painter;
    protected Scene annotations;
    protected Coord3d viewpoint;
    protected Coord3d center;
    protected Coord3d scaling;
    protected BoundingBox3d viewbounds;
    protected CameraMode cameraMode;
    protected ViewPositionMode viewmode;
    protected ViewBoundMode boundmode;
    protected Color bgColor = Color.BLACK;
    protected List<IViewPointChangedListener> viewPointChangedListeners;
    protected List<IViewIsVerticalEventListener> viewOnTopListeners;
    protected List<IViewLifecycleEventListener> viewLifecycleListeners;
    protected boolean wasOnTopAtLastRendering;

    protected static final float PI_div2 = (float) Math.PI / 2;
    
    public static final float DEFAULT_DISTANCE = 2000;
    public static final Coord3d DEFAULT_VIEW = new Coord3d(Math.PI / 3, Math.PI / 3, DEFAULT_DISTANCE);

    protected boolean dimensionDirty = false;
    /**
     * can be set to true by the Renderer3d so that the View knows it is
     * rendering due to a canvas size change
     */
    protected boolean viewDirty = false;
    protected static View current;
    protected BoundingBox3d initBounds;

    /**
     * Applies a factor to the default camera distance which is set to the
     * radius of the scene bounds. Changing this value also change the camera
     * clipping planes.
     */
    protected float factorViewPointDistance = 2;

    /** A slave view won't clear its color and depth buffer before rendering */
    protected boolean slave = false;
    protected SpaceTransformer spaceTransformer = new SpaceTransformer();
    private ISquarifier squarifier;
    
    /**
     * Create a view attached to a Scene, with its own Camera and Axe. The
     * initial view point is set at {@link View.DEFAULT_VIEW}.
     * <p/>
     * The {@link Quality} allows setting the rendering capabilities that are
     * set one time by the init() method.
     */
    public View(IChartComponentFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        BoundingBox3d sceneBounds = getSceneGraphBounds(scene);

        this.viewpoint = DEFAULT_VIEW.clone();
        this.center = sceneBounds.getCenter();
        this.scaling = Coord3d.IDENTITY.clone();
        this.viewbounds = null; // sceneBounds might not be ready yet //new BoundingBox3d(0, 1, 0, 1, 0, 1);
        this.viewmode = ViewPositionMode.FREE;
        this.boundmode = ViewBoundMode.AUTO_FIT;
        this.cameraMode = CameraMode.ORTHOGONAL;

        this.axe = factory.newAxe(sceneBounds, this);
        this.cam = factory.newCamera(center);
        this.painter = factory.newPainter();
        this.painter.setCamera(cam);
        
        this.scene = scene;
        this.canvas = canvas;
        this.quality = quality;
        this.annotations = factory.newScene(false);

        this.viewOnTopListeners = new ArrayList<IViewIsVerticalEventListener>();
        this.viewPointChangedListeners = new ArrayList<IViewPointChangedListener>();
        this.viewLifecycleListeners = new ArrayList<IViewLifecycleEventListener>();
        this.wasOnTopAtLastRendering = false;

        this.scene.getGraph().getStrategy().setView(this);

        this.spaceTransformer = new SpaceTransformer(); // apply no transform
        current = this;
    }
    
    public Painter getPainter() {
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
        axe.dispose();
        cam = null;
        viewOnTopListeners.clear();
        scene = null;
        canvas = null;
        quality = null;
    }

    /**
     * Current view selection into the mother Scene, and call to target canvas
     * rendering.
     */
    public void shoot() {
        canvas.forceRepaint();
    }

    /** 
     * Perform the 2d projection of all {@link Selectable} objects of the scene.
     *
     * The result of the projection can be retrieved on the objects's instances. 
     */
    public void project() {
    	//((NativeDesktopPainter)painter).getCurrentGL(canvas);
        scene.getGraph().project(painter, cam);
        //((NativeDesktopPainter)painter).getCurrentContext(canvas).release();
    }

    /** Perform the 3d projection of a 2d coordinate.*/
    public Coord3d projectMouse(int x, int y) {
    	//((NativeDesktopPainter)painter).getCurrentGL(canvas);
    	Coord3d p = cam.screenToModel(painter, new Coord3d(x, y, 0));
        //((NativeDesktopPainter)painter).getCurrentContext(canvas).release();
        return p;
    }
    
    /** 
     * Might be invoked by a {@link IScreenCanvas} to indicate that dimension changed and that
     * elements should be reprocessed at next rendering, e.g. 2d projections.
     */
    public void markDimensionDirty() {
    	dimensionDirty = true;
    }

    /******************************* GENERAL DISPLAY CONTROLS ***********************************/

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
     * Set the surrounding {@link AxeBox} dimensions, the {@link Camera} target and the
     * colorbar range.
     */
    public void lookToBox(BoundingBox3d box) {
        if (box.isReset())
            return;
        center = box.getCenter();
        axe.setAxe(box);
        viewbounds = box;
    }

    /**
     * Return the central point of the view scene, that is the last bounding box
     * center set by {@link lookToBox(BoundingBox3d box)}
     */
    public Coord3d getCenter() {
        return center;
    }

    /** Get the {@link AxeBox}'s {@link BoundingBox3d} */
    public BoundingBox3d getBounds() {
        return axe.getBoxBounds();
    }

    public ViewBoundMode getBoundsMode() {
        return boundmode;
    }

    /** Set the {@link ViewPositionMode} applied to this view. */
    public void setViewPositionMode(ViewPositionMode mode) {
        this.viewmode = mode;
    }

    /** Return the {@link ViewPositionMode} applied to this view. */
    public ViewPositionMode getViewMode() {
        return viewmode;
    }

    /**
     * Set the viewpoint using polar coordinates relative to the target (i.e.
     * the center of the scene). Only X and Y dimensions are required, as the
     * distance to center will be computed automatically by {@link
     * updateCamera()}.
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
     * @see {@link setViewPoint(Coord3d polar, boolean updateView)}
     */
    public void setViewPoint(Coord3d polar) {
        setViewPoint(polar, true);
    }

    /**
     * Get the viewpoint. The Z dimension is the one defined by {@link
     * updateCamera()}, which depends on the view scaling.
     * 
     * @see {@link setViewPoint(Coord3d polar, boolean updateView)}
     */
    public Coord3d getViewPoint() {
        return viewpoint;
    }

    /**
     * Return the last used view scaling that was set according to the {@link
     * setSquared(boolean v)} status.
     */
    public Coord3d getLastViewScaling() {
        return scaling;
    }

    /* CONTROLS ANNOTATIONS & GENERAL RENDERING */

    public void setAxe(IAxe ax) {
        axe = ax;
        updateBounds();
    }

    public IAxe getAxe() {
        return axe;
    }

    public boolean getSquared() {
        return this.squared;
    }

    public void setSquared(boolean status) {
        this.squared = status;
    }

    public boolean isAxeBoxDisplayed() {
        return axeBoxDisplayed;
    }

    public void setAxeBoxDisplayed(boolean axeBoxDisplayed) {
        this.axeBoxDisplayed = axeBoxDisplayed;
    }

    public void setBackgroundColor(Color color) {
        bgColor = color;
    }

    public Color getBackgroundColor() {
        return bgColor;
    }

    public Camera getCamera() {
        return cam;
    }

    /**
     * Set the projection of this view, either Camera.ORTHOGONAL or
     * Camera.PERSPECTIVE.
     */
    public void setCameraMode(CameraMode mode) {
        this.cameraMode = mode;
    }

    /**
     * Get the projection of this view, either CameraMode.ORTHOGONAL or
     * CameraMode.PERSPECTIVE.
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

    public boolean addViewOnTopEventListener(IViewIsVerticalEventListener listener) {
        return viewOnTopListeners.add(listener);
    }

    public boolean removeViewOnTopEventListener(IViewIsVerticalEventListener listener) {
        return viewOnTopListeners.remove(listener);
    }

    protected void fireViewOnTopEvent(boolean isOnTop) {
        ViewIsVerticalEvent e = new ViewIsVerticalEvent(this);

        if (isOnTop)
            for (IViewIsVerticalEventListener listener : viewOnTopListeners)
                listener.viewVerticalReached(e);
        else
            for (IViewIsVerticalEventListener listener : viewOnTopListeners)
                listener.viewVerticalLeft(e);
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
     * Select between an automatic bounding (that allows fitting the entire
     * scene graph), or a custom bounding.
     */
    public void setBoundMode(ViewBoundMode mode) {
        boundmode = mode;
        updateBounds();
    }

    /**
     * Set the bounds of the view according to the current {@link ViewBoundMode}
     * , and orders a {@link Camera.shoot()}.
     */
    public void updateBounds() {
        if (boundmode == ViewBoundMode.AUTO_FIT)
            lookToBox(getSceneGraphBounds()); // set axe and camera
        else if (boundmode == ViewBoundMode.MANUAL)
            lookToBox(viewbounds); // set axe and camera
        else
            throw new RuntimeException("Unknown bounds");
        shoot();
    }

    protected BoundingBox3d getSceneGraphBounds() {
        return getSceneGraphBounds(scene);
    }

    protected BoundingBox3d getSceneGraphBounds(Scene scene) {
        return scene.getGraph().getBounds();
    }

    /**
     * Update the bounds according to the scene graph whatever is the current
     * {@link ViewBoundMode}, and orders a {@link Camera.shoot()}
     */
    public void updateBoundsForceUpdate(boolean refresh) {
        lookToBox(getSceneGraphBounds());
        if (refresh)
            shoot();
    }

    /**
     * Set a manual bounding box and switch the bounding mode to
     * {ViewBoundMode.MANUAL}, meaning that any call to {@link updateBounds()}
     * will update view bounds to the current bounds.
     */
    public void setBoundManual(BoundingBox3d bounds) {
        boundmode = ViewBoundMode.MANUAL;
        lookToBox(bounds);
    }

    /* GL */


    /**
     * The initialization function:
     * <ul>
     * <li>specifies general GL settings that impact the rendering quality and
     * performance (computation speed).
     * <li>enable light management
     * <li>load all required texture resources
     * <li>fix the current view bounds to the whole scene graph bounds
     * </ul>
     * <p/>
     * The rendering settings are set by the {@link Quality} given in the
     * constructor parameters.
     */
    public void init() {
        initQuality();
        initLights();
        initResources();
        initBounds(scene, viewbounds, initBounds);
        fireViewLifecycleHasInit(null);
    }
    
    protected void initBounds(Scene scene, BoundingBox3d viewbounds, BoundingBox3d initBounds){
        if (viewbounds == null) {
            if (initBounds == null)
                setBoundManual(scene.getGraph().getBounds());
            else
                setBoundManual(initBounds);
            // boundmode = ViewBoundMode.AUTO_FIT;
        } else {
            lookToBox(viewbounds);
        }
    }

    public BoundingBox3d getInitBounds() {
        return initBounds;
    }

    public void setInitBounds(BoundingBox3d initBounds) {
        this.initBounds = initBounds;
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
    }

    /** Clear the color and depth buffer. */
    public void clear() {
        painter.clearColor(bgColor);
        painter.glClearDepth(1);

        if (slave) {
            return;
        } else {
            painter.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); 
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
    public void renderBackground(float left, float right) {
    }

    /**
     * To be implemented (see AWTView)
     */
    public void renderBackground(ViewportConfiguration backgroundViewPort) {
    }

    public void renderScene() {
        renderScene(new ViewportConfiguration(canvas.getRendererWidth(), canvas.getRendererHeight()));
    }

    public void renderScene(float left, float right) {
        ViewportConfiguration vc = ViewportBuilder.column(canvas.getRendererWidth(), canvas.getRendererHeight(), left, right);
        renderScene(vc);
    }

    public void renderScene(ViewportConfiguration viewport) {
        updateQuality();
        BoundingBox3d scaling = computeScaledViewBounds();
        updateCamera(viewport, scaling);
        renderAxeBox();
        renderSceneGraph();
        renderAnnotations(cam);
    }

    public void updateQuality() {
        if (quality.isAlphaActivated())
            painter.glEnable(GL.GL_BLEND);
        else
        	painter.glDisable(GL.GL_BLEND);
        	//painter.glDisable(GL2.GL_BLEND);
    }
    
    /* SCALE PROCESSING */
    
    protected Coord3d squarify() {
        return squarify(scene, boundmode, viewbounds, spaceTransformer);
    }

    /**
     * Return a 3d scaling factor that allows scaling the scene into a square
     * box, according to the current ViewBoundMode.
     * <p/>
     * If the scene bounds are Infinite, NaN or zero, for a given dimension, the
     * scaler will be set to 1 on the given dimension.
     * 
     * @return a scaling factor for each dimension.
     */
    protected Coord3d squarify(Scene scene, ViewBoundMode boundmode, BoundingBox3d viewbounds, SpaceTransformer spaceTransformer) {
        // Get the view bounds
        BoundingBox3d bounds;
        if (boundmode == ViewBoundMode.AUTO_FIT)
            bounds = scene.getGraph().getBounds();
        else if (boundmode == ViewBoundMode.MANUAL)
            bounds = viewbounds;
        else {
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
        }
        else{
            float xscale = (float) ((double) lmax / (double) xLen);
            float yscale = (float) ((double) lmax / (double) yLen);
            float zscale = (float) ((double) lmax / (double) zLen);
            return new Coord3d(xscale, yscale, zscale);
        }
    }
    
    /* LAYOUT */

    protected Coord3d squarifyComputeBoundsRanges(BoundingBox3d bounds) {
        if(spaceTransformer==null){
            return bounds.getRange();
        }
        else{
            float xLen = spaceTransformer.getX().compute(bounds.getXmax()) - spaceTransformer.getX().compute(bounds.getXmin());
            float yLen = spaceTransformer.getY().compute(bounds.getYmax()) - spaceTransformer.getY().compute(bounds.getYmin());
            float zLen = spaceTransformer.getZ().compute(bounds.getZmax()) - spaceTransformer.getZ().compute(bounds.getZmin());

            return new Coord3d(xLen, yLen, zLen);
        }
    }

    public BoundingBox3d computeScaledViewBounds() {
        scaling = computeSceneScaling();
        
        // -- Compute the bounds for computing cam distance, clipping planes,
        if (viewbounds == null)
            viewbounds = new BoundingBox3d(0, 1, 0, 1, 0, 1);
        BoundingBox3d boundsScaled = new BoundingBox3d();
        boundsScaled.add(viewbounds.scale(scaling));
        if (MAINTAIN_ALL_OBJECTS_IN_VIEW)
            boundsScaled.add(getSceneGraphBounds().scale(scaling));
        return boundsScaled;
    }

    public Coord3d computeSceneScaling() {
        return computeSceneScaling(scene, squared, boundmode, viewbounds, spaceTransformer);
    }
    
    public Coord3d computeSceneScaling(Scene scene, boolean squared, ViewBoundMode boundmode, BoundingBox3d viewbounds, SpaceTransformer spaceTransformer) {
        if (squared)
            return squarify(scene, boundmode, viewbounds, spaceTransformer);
        else
            return Coord3d.IDENTITY.clone();
    }
    
    /* CAMERA PROCESSING */

    public void updateCamera(ViewportConfiguration viewport, BoundingBox3d boundsScaled) {
        float sceneRadius = (float)boundsScaled.getRadius() * factorViewPointDistance;
        updateCamera(viewport, boundsScaled, sceneRadius);
    }

    public void updateCamera(ViewportConfiguration viewport, BoundingBox3d bounds, float sceneRadiusScaled) {
        updateCamera(viewport, bounds, sceneRadiusScaled, viewmode, viewpoint, cam, cameraMode, factorViewPointDistance, center, scaling);
    }
    
    public void updateCamera(ViewportConfiguration viewport, BoundingBox3d bounds, float sceneRadiusScaled, ViewPositionMode viewmode, Coord3d viewpoint, Camera cam, CameraMode cameraMode, float factorViewPointDistance, Coord3d center, Coord3d scaling) {
        viewpoint.z = computeViewpointDistance(bounds, sceneRadiusScaled, factorViewPointDistance);
        
        cam.setTarget(computeCameraTarget(center, scaling));
        cam.setUp(computeCameraUpAndTriggerEvents(viewpoint));
        cam.setEye(computeCameraEye(cam.getTarget(), viewmode, viewpoint));
        
        computeCameraRenderingSphereRadius(cam, viewport, bounds);
        
        cam.setViewPort(viewport);
        cam.shoot(painter, cameraMode);
    }

    public float computeViewpointDistance(BoundingBox3d bounds, float sceneRadiusScaled, float factorViewPointDistance) {
        return (float)spaceTransformer.compute(bounds).getRadius();
    }

    protected Coord3d computeCameraTarget() {
        return computeCameraTarget(center, scaling);
    }
    
    protected Coord3d computeCameraEye(Coord3d target) {
        return computeCameraEye(target, viewmode, viewpoint);
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

    protected Coord3d computeCameraUpAndTriggerEvents() {
        return computeCameraUpAndTriggerEvents(viewpoint);
    }
    
    protected Coord3d computeCameraUpAndTriggerEvents(Coord3d viewpoint) {
        Coord3d up;

        if (Math.abs(viewpoint.y) == (float) Math.PI / 2) {
            up = computeCameraUp();

            // handle "on-top" events
            if (!wasOnTopAtLastRendering) {
                wasOnTopAtLastRendering = true;
                fireViewOnTopEvent(true);
            }
        } else {
            // handle up vector
            up = new Coord3d(0, 0, 1);

            // handle "on-top" events
            if (wasOnTopAtLastRendering) {
                wasOnTopAtLastRendering = false;
                fireViewOnTopEvent(false);
            }
        }
        return up;
    }

    protected Coord3d computeCameraUp() {
        Coord2d direction = new Coord2d(viewpoint.x, viewpoint.z).cartesian();
        Coord3d up;
        if (viewpoint.y > 0) // on top
            up = new Coord3d(-direction.x, -direction.y, 0);
        else
            up = new Coord3d(direction.x, direction.y, 0);
        return up;
    }

    public void computeCameraRenderingSphereRadius(Camera cam, ViewportConfiguration viewport, BoundingBox3d bounds) {
        if (viewmode == ViewPositionMode.TOP) {
            if(spaceTransformer!=null)
                bounds = spaceTransformer.compute(bounds);

            float xdiam = bounds.getXRange().getRange();
            float ydiam = bounds.getYRange().getRange();
            float radius = Math.max(xdiam, ydiam) / 2;
            
            cam.setRenderingSphereRadius(radius);
            correctCameraPositionForIncludingTextLabels(painter, viewport);
        } else {
            if(spaceTransformer!=null)
                bounds = spaceTransformer.compute(bounds);
            
            cam.setRenderingSphereRadius((float) bounds.getRadius() * CAMERA_RENDERING_SPHERE_RADIUS_FACTOR);
        }
    }
    
    public static final float CAMERA_RENDERING_SPHERE_RADIUS_FACTOR = 1.2f;


    /*protected void computeCameraRenderingSphereRadius(Camera cam, GL gl, GLU glu, ViewportConfiguration viewport, BoundingBox3d bounds) {
        if (viewmode == ViewPositionMode.TOP) {
            float xdiam = bounds.getXRange().getRange();
            float ydiam = bounds.getYRange().getRange();
            float radius = Math.max(xdiam, ydiam) / 2;
            cam.setRenderingSphereRadius(radius);
            correctCameraPositionForIncludingTextLabels(gl, glu, viewport);
        } else {
            cam.setRenderingSphereRadius((float) bounds.getRadius());
            //cam.setRenderingSphereRadius((float) bounds.getTransformedRadius(spaceTransformer));
        }
    }*/
    
    protected void correctCameraPositionForIncludingTextLabels(Painter painter, ViewportConfiguration viewport) {
    }


    /* AXE BOX RENDERING */
    
    protected void renderAxeBox() {
        renderAxeBox(axe, scene, cam, scaling, axeBoxDisplayed);
    }
    
    protected void renderAxeBox(IAxe axe, Scene scene, Camera camera, Coord3d scaling, boolean axeBoxDisplayed) {
        if (axeBoxDisplayed) {
            painter.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

            scene.getLightSet().disable(painter);
            axe.setScale(scaling);
            axe.draw(painter, null, null, camera);
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
    	GL gl = null;
    	GLU glu = null;
    	
        if (light) {
            scene.getLightSet().apply(painter, scaling);
        }

        Transform transform = new Transform(new Scale(scaling));
        scene.getGraph().setTransform(transform);
        scene.getGraph().draw(painter, gl, glu, camera);
    }

    /* OVERLAY RENDERING */

    public void renderOverlay() {
        renderOverlay(new ViewportConfiguration(canvas));
    }

    public void renderOverlay(ViewportConfiguration viewportConfiguration) {
    }

    public void renderAnnotations(Camera camera) {
    	GL gl = null;
        GLU glu = null;

        Transform transform = new Transform(new Scale(scaling));
        annotations.getGraph().setTransform(transform);
        annotations.getGraph().draw(painter, gl, glu, camera);
    }


    /* */

    public static View current() {
        return current;
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
}
