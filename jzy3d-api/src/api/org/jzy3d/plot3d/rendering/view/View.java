package org.jzy3d.plot3d.rendering.view;

import java.util.ArrayList;
import java.util.List;

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
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.lights.LightSet;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.transform.Scale;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
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
        this.viewmode = ViewPositionMode.FREE;
        this.boundmode = ViewBoundMode.AUTO_FIT;
        this.cameraMode = CameraMode.ORTHOGONAL;

        this.axe = factory.newAxe(sceneBounds, this);
        this.cam = factory.newCamera(center);
        this.scene = scene;
        this.canvas = canvas;
        this.quality = quality;
        this.annotations = factory.newScene(false);

        this.viewOnTopListeners = new ArrayList<IViewIsVerticalEventListener>();
        this.viewPointChangedListeners = new ArrayList<IViewPointChangedListener>();
        this.viewLifecycleListeners = new ArrayList<IViewLifecycleEventListener>();
        this.wasOnTopAtLastRendering = false;

        this.glu = new GLU();

        this.scene.getGraph().getStrategy().setView(this);

        this.spaceTransformer = new SpaceTransformer(); // apply no transform
        current = this;
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

    public void project() {
        GL gl = getCurrentGL();
        scene.getGraph().project(gl, glu, cam);
        getCurrentContext().release();
    }

    public Coord3d projectMouse(int x, int y) {
        GL gl = getCurrentGL();
        Coord3d p = cam.screenToModel(gl, glu, new Coord3d(x, y, 0));
        getCurrentContext().release();
        return p;
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
     * Set the surrounding AxeBox dimensions and the Camera target, and the
     * colorbar range.
     */
    public void lookToBox(BoundingBox3d box) {
        if (box.isReset())
            return;
        center = box.getTransformedCenter(spaceTransformer);
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
        // viewbounds = bounds;
        boundmode = ViewBoundMode.MANUAL;
        lookToBox(bounds);
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
    protected Coord3d squarify() {
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
            xLen = spaceTransformer.getX().compute(bounds.getXmax()) - spaceTransformer.getX().compute(bounds.getXmin());
            yLen = spaceTransformer.getY().compute(bounds.getYmax()) - spaceTransformer.getY().compute(bounds.getYmin());
            zLen = spaceTransformer.getZ().compute(bounds.getZmax()) - spaceTransformer.getZ().compute(bounds.getZmin());
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
        float xscale = (float) ((double) lmax / (double) xLen);
        float yscale = (float) ((double) lmax / (double) yLen);
        float zscale = (float) ((double) lmax / (double) zLen);
        return new Coord3d(xscale, yscale, zscale);
    }

    /* GL */

    public GL getCurrentGL() {
        getCurrentContext().makeCurrent();
        return getCanvasAsGLAutoDrawable().getGL().getGL2();
    }

    public GLContext getCurrentContext() {
        GLAutoDrawable c = getCanvasAsGLAutoDrawable();
        GLContext context = c.getContext();
        return context;
    }

    protected GLAutoDrawable getCanvasAsGLAutoDrawable() {
        if (canvas instanceof GLAutoDrawable) {
            // this covers AWT and Swing
            return ((GLAutoDrawable) canvas);
        } else if (canvas.getDrawable() instanceof GLAutoDrawable) {
            // this also covers NEWT and Offscreen
            return ((GLAutoDrawable) canvas.getDrawable());
        } else
            throw new RuntimeException("Unexpected instance type : " + canvas.getClass().toString());
    }

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
    public void init(GL gl) {
        initQuality(gl);
        initLights(gl);
        initResources(gl);

        if (viewbounds == null) {
            if (initBounds == null)
                setBoundManual(getScene().getGraph().getBounds());
            else
                setBoundManual(initBounds);
            // boundmode = ViewBoundMode.AUTO_FIT;
        } else {
            lookToBox(viewbounds);
        }

        fireViewLifecycleHasInit(null);
    }

    public BoundingBox3d getInitBounds() {
        return initBounds;
    }

    public void setInitBounds(BoundingBox3d initBounds) {
        this.initBounds = initBounds;
    }

    public void initQuality(GL gl) {
        // Activate Depth buffer
        if (quality.isDepthActivated()) {
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glDepthFunc(GL.GL_LEQUAL);
        } else
            gl.glDisable(GL.GL_DEPTH_TEST);

        // Blending
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        // on/off is handled by each viewport (camera or image)

        // Activate tranparency
        if (quality.isAlphaActivated()) {
            gl.glEnable(GL2ES1.GL_ALPHA_TEST);
            if (quality.isDisableDepthBufferWhenAlpha())
                /* seams better for transparent polygons since they're sorted */
                gl.glDisable(GL.GL_DEPTH_TEST); // gl.glDepthFunc(GL2.GL_ALWAYS);

            // gl.glAlphaFunc(GL2.GL_EQUAL,1.0f);
        } else {
            gl.glDisable(GL2ES1.GL_ALPHA_TEST);
        }

        // Make smooth colors for polygons (interpolate color between points)
        if (gl.isGL2()) {
            if (quality.isSmoothColor())
                gl.getGL2().glShadeModel(GLLightingFunc.GL_SMOOTH);
            else
                gl.getGL2().glShadeModel(GLLightingFunc.GL_FLAT);
        }

        // Make smoothing setting
        if (quality.isSmoothPolygon()) {
            gl.glEnable(GL2GL3.GL_POLYGON_SMOOTH);
            gl.glHint(GL2GL3.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
        } else
            gl.glDisable(GL2GL3.GL_POLYGON_SMOOTH);

        if (quality.isSmoothLine()) {
            gl.glEnable(GL.GL_LINE_SMOOTH);
            gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
        } else
            gl.glDisable(GL.GL_LINE_SMOOTH);

        if (quality.isSmoothPoint()) {
            gl.glEnable(GL2ES1.GL_POINT_SMOOTH);
            gl.glHint(GL2ES1.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
            // gl.glDisable(GL2.GL_BLEND);
            // gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
        } else
            gl.glDisable(GL2ES1.GL_POINT_SMOOTH);
    }

    public void initLights(GL gl) {
        // Init light
        scene.getLightSet().init(gl);
        scene.getLightSet().enableLightIfThereAreLights(gl);
        // scene.getLightSet().enable(gl, true);
    }

    public void initResources(GL gl) {
        getScene().getGraph().mountAllGLBindedResources(gl);
    }

    /** Clear the color and depth buffer. */
    public void clear(GL gl) {
        clearColorAndDepth(gl);
    }

    public void clearColorAndDepth(GL gl) {
        gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        gl.glClearDepth(1);

        if (slave) {
            return;
        } else {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // or
                                                                         // use
                                                                         // GL2
        }
    }

    /* */

    public void render(GL gl, GLU glu) {
        fireViewLifecycleWillRender(null);

        // init(gl);
        renderBackground(gl, glu, 0f, 1f);
        renderScene(gl, glu);
        renderOverlay(gl);

        if (dimensionDirty)
            dimensionDirty = false;

        cam.show(gl, new Transform(new Scale(scaling)), scaling);
    }

    /**
     * To be implemented (see AWTView)
     */
    public void renderBackground(GL gl, GLU glu2, float f, float g) {
    }

    /**
     * To be implemented (see AWTView)
     */
    public void renderBackground(GL gl, GLU glu2, ViewportConfiguration backgroundViewPort) {
    }

    public void renderScene(GL gl, GLU glu) {
        renderScene(gl, glu, new ViewportConfiguration(canvas.getRendererWidth(), canvas.getRendererHeight()));
    }

    public void renderScene(GL gl, GLU glu, float left, float right) {
        ViewportConfiguration vc = ViewportBuilder.column(canvas.getRendererWidth(), canvas.getRendererHeight(), left, right);
        renderScene(gl, glu, vc);
    }

    public void renderScene(GL gl, GLU glu, ViewportConfiguration viewport) {
        updateQuality(gl);
        BoundingBox3d scaling = computeScaledViewBounds();
        updateCamera(gl, glu, viewport, scaling);
        renderAxeBox(gl, glu);
        renderSceneGraph(gl, glu);
        renderAnnotations(gl, glu);
    }

    public void updateQuality(GL gl) {
        if (quality.isAlphaActivated())
            gl.glEnable(GL2.GL_BLEND);
        else
            gl.glDisable(GL2.GL_BLEND);
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
        if (squared)
            return squarify();
        else
            return Coord3d.IDENTITY.clone();
    }

    public void updateCamera(GL gl, GLU glu, ViewportConfiguration viewport, BoundingBox3d boundsScaled) {
        // before LOG was : (float)bounds.getRadius() * factorViewPointDistance;
        float sceneRadius = (float) boundsScaled.getTransformedRadius(spaceTransformer);
        updateCamera(gl, glu, viewport, boundsScaled, sceneRadius);
    }

    public void updateCamera(GL gl, GLU glu, ViewportConfiguration viewport, BoundingBox3d bounds, float sceneRadiusScaled) {
        viewpoint.z = sceneRadiusScaled * factorViewPointDistance;
        cam.setTarget(computeCameraTarget());
        cam.setUp(computeCameraUpAndTriggerEvents());
        cam.setEye(computeCameraEye(cam.getTarget()));
        computeCameraRenderingSphereRadius(gl, glu, viewport, bounds);
        cam.setViewPort(viewport);
        cam.shoot(gl, glu, cameraMode);
    }

    public Coord3d computeCameraTarget() {
        return center.mul(scaling);
    }

    public Coord3d computeCameraEye(Coord3d target) {
        Coord3d eye;
        if (viewmode == ViewPositionMode.FREE) {
            eye = computeCameraEyeFree(viewpoint, target);
        } else if (viewmode == ViewPositionMode.TOP) {
            eye = computeCameraEyeTop(viewpoint, target);
        } else if (viewmode == ViewPositionMode.PROFILE) {
            eye = computeCameraEyeProfile(viewpoint, target);
        } else
            throw new RuntimeException("Unsupported ViewMode: " + viewmode);
        return eye;
    }

    public static Coord3d computeCameraEyeProfile(Coord3d viewpoint, Coord3d target) {
        Coord3d eye;
        eye = viewpoint;
        eye.y = 0;
        eye = eye.cartesian().add(target);
        return eye;
    }

    public static Coord3d computeCameraEyeTop(Coord3d viewpoint, Coord3d target) {
        Coord3d eye;
        eye = viewpoint;
        eye.x = -(float) Math.PI / 2; // on x
        eye.y = (float) Math.PI / 2; // on top
        eye = eye.cartesian().add(target);
        return eye;
    }

    public static Coord3d computeCameraEyeFree(Coord3d viewpoint, Coord3d target) {
        Coord3d eye;
        eye = viewpoint.cartesian().add(target);
        return eye;
    }

    public Coord3d computeCameraUpAndTriggerEvents() {
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

    public Coord3d computeCameraUp() {
        Coord2d direction = new Coord2d(viewpoint.x, viewpoint.z).cartesian();
        Coord3d up;
        if (viewpoint.y > 0) // on top
            up = new Coord3d(-direction.x, -direction.y, 0);
        else
            up = new Coord3d(direction.x, direction.y, 0);
        return up;
    }

    public void computeCameraRenderingSphereRadius(GL gl, GLU glu, ViewportConfiguration viewport, BoundingBox3d bounds) {
        if (viewmode == ViewPositionMode.TOP) {
            float xdiam = bounds.getXRange().getRange();
            float ydiam = bounds.getYRange().getRange();
            float radius = Math.max(xdiam, ydiam) / 2;
            cam.setRenderingSphereRadius(radius);
            correctCameraPositionForIncludingTextLabels(gl, glu, viewport);
        } else {
            cam.setRenderingSphereRadius((float) bounds.getTransformedRadius(spaceTransformer));
        }
    }

    public void renderAxeBox(GL gl, GLU glu) {
        if (axeBoxDisplayed) {
            glModelView(gl);
            scene.getLightSet().disable(gl);
            axe.setScale(scaling);
            axe.draw(gl, glu, cam);
            scene.getLightSet().enableLightIfThereAreLights(gl);
        }
    }

    public void glModelView(GL gl) {
        if (gl.isGL2()) {
            gl.getGL2().glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        } else {
            GLES2CompatUtils.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        }
    }

    public void renderSceneGraph(GL gl, GLU glu) {
        renderSceneGraph(gl, glu, true);
    }

    public void renderSceneGraph(GL gl, GLU glu, boolean light) {
        if (light) {
            scene.getLightSet().apply(gl, scaling);
            // gl.glEnable(GL2.GL_LIGHTING);
            // gl.glEnable(GL2.GL_LIGHT0);
            // gl.glDisable(GL2.GL_LIGHTING);
        }

        Transform transform = new Transform(new Scale(scaling));
        scene.getGraph().setTransform(transform);
        scene.getGraph().draw(gl, glu, cam);
    }

    public void renderOverlay(GL gl) {
        renderOverlay(gl, new ViewportConfiguration(canvas));
    }

    public void renderOverlay(GL gl, ViewportConfiguration viewportConfiguration) {
    }

    public void renderAnnotations(GL gl, GLU glu) {
        Transform transform = new Transform(new Scale(scaling));
        annotations.getGraph().setTransform(transform);
        annotations.getGraph().draw(gl, glu, null);
    }

    protected void correctCameraPositionForIncludingTextLabels(GL gl, GLU glu, ViewportConfiguration viewport) {
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

    /* */

    /** A view may optionnaly know its parent chart. */
    protected Chart chart;

    protected GLU glu;

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

    protected Scene annotations;

    protected Coord3d viewpoint;
    protected Coord3d center;
    protected Coord3d scaling;
    protected BoundingBox3d viewbounds;

    protected CameraMode cameraMode;
    protected ViewPositionMode viewmode;
    protected ViewBoundMode boundmode;

    // protected BoundingBox3d targetBox;

    protected Color bgColor = Color.BLACK;

    protected List<IViewPointChangedListener> viewPointChangedListeners;
    protected List<IViewIsVerticalEventListener> viewOnTopListeners;
    protected List<IViewLifecycleEventListener> viewLifecycleListeners;
    protected boolean wasOnTopAtLastRendering;

    protected static final float PI_div2 = (float) Math.PI / 2;

    public static final Coord3d DEFAULT_VIEW = new Coord3d(Math.PI / 3, Math.PI / 3, 2000);

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

}
