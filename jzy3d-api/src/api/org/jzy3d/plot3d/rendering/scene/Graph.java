package org.jzy3d.plot3d.rendering.scene;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.TicToc;
import org.jzy3d.plot3d.primitives.AbstractComposite;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.primitives.selectable.Selectable;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.legends.AWTLegend;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.ordering.DefaultOrderingStrategy;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;

/**
 * The scene's {@link Graph} basically stores the scene content and facilitate
 * objects control
 * 
 * The graph may decompose all {@link AbstractComposite} into a list of their
 * {@link AbstractDrawable}s primitives if constructor is called with parameters
 * enabling sorting.
 * 
 * The list of primitives is ordered using either the provided
 * {@link DefaultOrderingStrategy} or an other specified
 * {@link AbstractOrderingStrategy}. Sorting is usefull for handling
 * transparency properly.
 * 
 * The {@link Graph} maintains a reference to its mother {@link Scene} in order
 * to inform the {@link View}s when its content has change and that repainting
 * is required.
 * 
 * The add() method allows adding a {@link AbstractDrawable} to the scene Graph
 * and updates all views' viewpoint in order to target the center of the scene.
 * 
 * @author Martin Pernollet
 */
public class Graph {
    public Graph(Scene scene) {
        this(scene, new DefaultOrderingStrategy(), true);
    }

    public Graph(Scene scene, boolean sort) {
        this(scene, new DefaultOrderingStrategy(), sort);
    }

    public Graph(Scene scene, AbstractOrderingStrategy strategy) {
        this(scene, strategy, true);
    }

    public Graph(Scene scene, AbstractOrderingStrategy strategy, boolean sort) {
        this.scene = scene;
        this.strategy = strategy;
        this.sort = sort;
        this.components = new ArrayList<AbstractDrawable>();
        // components = Collections.synchronizedList(new
        // ArrayList<AbstractDrawable>());
    }

    public synchronized void dispose() {
        // synchronized(components){
        for (AbstractDrawable c : components)
            if (c != null)
                c.dispose();
        // }
        components.clear();
        scene = null;
    }

    /* */

    /**
     * Add a Drawable to the graph and call all views' so that they update their
     * bounds according to their mode (automatic or manual).
     * 
     * Addition is to the graph is synchronized.
     * 
     * @param drawable
     *            : The drawable that must be added to the scene graph.
     * @param update
     *            : should be true if you wish to have all the views updated
     *            with old bounds including drawable bounds
     */
    public void add(AbstractDrawable drawable, boolean updateViews) {
        synchronized (this) {
            components.add(drawable);
        }

        if (updateViews)
            for (View view : scene.views)
                view.updateBounds();
    }

    public void add(AbstractDrawable drawable) {
        add(drawable, true);
    }

    public void add(List<? extends AbstractDrawable> drawables, boolean updateViews) {
        for (AbstractDrawable d : drawables)
            add(d, false);
        if (updateViews)
            for (View view : scene.views)
                view.updateBounds();
    }

    public void add(List<? extends AbstractDrawable> drawables) {
        add(drawables, true);
    }

    /**
     * Delete a Drawable from the SceneGraph and update all views' viewpoint in
     * order to target the center of the scene.
     * 
     * @param drawable
     *            The drawable that must be deleted from the scene graph.
     */
    public boolean remove(AbstractDrawable drawable, boolean updateViews) {
        boolean output = false;
        synchronized (this) {
            output = components.remove(drawable);
        }
        BoundingBox3d bbox = getBounds();
        for (View view : scene.views) {
            view.lookToBox(bbox);
            if (updateViews)
                view.shoot();
        }
        return output;
    }

    public boolean remove(AbstractDrawable drawable) {
        return remove(drawable, true);
    }

    public List<AbstractDrawable> getAll() {
        return components;
    }

    public synchronized List<IGLBindedResource> getAllGLBindedResources() {
        List<IGLBindedResource> out = new ArrayList<IGLBindedResource>();
        for (AbstractDrawable c : components) {
            if (c instanceof IGLBindedResource) {
                out.add((IGLBindedResource) c);
            }
        }
        return out;
    }

    public void mountAllGLBindedResources(GL gl) {
        final List<IGLBindedResource> all = getAllGLBindedResources();
        for (IGLBindedResource r : all)
            if (!r.hasMountedOnce())
                r.mount(gl);
    }

    /* */

    /**
     * Decompose all {@link AbstractComposite} objects, and sort the extracted
     * monotype (i.e. non-{@link AbstractComposite} {@link AbstractDrawable}s)
     * in order to render them according to the default -or defined-
     * {@link AbstractOrderingStrategy}.
     */
    public void draw(GL gl, GLU glu, Camera camera) {
        draw(gl, glu, camera, components, sort);
    }

    protected TicToc t = new TicToc();

    protected synchronized void draw(GL gl, GLU glu, Camera camera, List<AbstractDrawable> components, boolean sort) {
        glMatrixMode(gl);
        if (!sort) {
            drawSimple(gl, glu, camera, components);
        } else {
            drawDecomposition(gl, glu, camera);
        }
    }

    public void glMatrixMode(GL gl) {
        if (gl.isGL2()) {
            gl.getGL2().glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        } else {
            GLES2CompatUtils.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        }
    }

    public void drawSimple(GL gl, GLU glu, Camera camera, List<AbstractDrawable> components) {
        // render all items of the graph
        // synchronized(components){
        for (AbstractDrawable d : components)
            if (d.isDisplayed())
                d.draw(gl, glu, camera);
        // }
    }

    public void drawDecomposition(GL gl, GLU glu, Camera camera) {
        // Render sorted monotypes
        List<AbstractDrawable> monotypes = getDecomposition();
        strategy.sort(monotypes, camera);

        for (AbstractDrawable d : monotypes) {
            if (d.isDisplayed())
                d.draw(gl, glu, camera);
        }
    }

    /**
     * Expand all {@link AbstractComposites} instance into a list of atomic
     * {@link AbstractDrawable} types and return all the current Graph
     * primitives decomposition.
     */
    public List<AbstractDrawable> getDecomposition() {
        ArrayList<AbstractDrawable> monotypes;
        synchronized (components) {
            monotypes = Decomposition.getDecomposition(components);
        }
        return monotypes;
    }

    /** Update all interactive {@link AbstractDrawable} projections */
    public synchronized void project(GL gl, GLU glu, Camera camera) {
        // synchronized(components){
        for (AbstractDrawable d : components) {
            if (d instanceof Selectable)
                ((Selectable) d).project(gl, glu, camera);
        }
        // }
    }

    /* */

    /** Get the {@link @Drawable} ordering strategy. */
    public AbstractOrderingStrategy getStrategy() {
        return strategy;
    }

    /** Set the {@link @Drawable} ordering strategy. */
    public void setStrategy(AbstractOrderingStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Delegate transforming iteratively to all Drawable of this graph and
     * stores the given transform for keeping the ability of retrieving it.
     */
    public synchronized void setTransform(Transform transform) {
        this.transform = transform;

        synchronized (components) {
            for (AbstractDrawable c : components) {
                if (c != null)
                    c.setTransform(transform);
            }
        }
    }

    /** Return the transform that was affected to this composite. */
    public Transform getTransform() {
        return transform;
    }

    /**
     * Creates and return a BoundingBox3d that embed all Drawable bounds, among
     * those that have a defined bounding box.
     */
    public synchronized BoundingBox3d getBounds() {
        if (components.size() == 0)
            return new BoundingBox3d(0, 0, 0, 0, 0, 0);
        else {
            BoundingBox3d box = new BoundingBox3d();

            for (AbstractDrawable c : components) {
                if (c != null && c.getBounds() != null) {
                    BoundingBox3d drawableBounds = c.getBounds();
                    if (!drawableBounds.isReset()) {
                        box.add(drawableBounds);
                    }
                }
            }
            return box;
        }
    }

    /* */

    /**
     * Return the list of available {@link AbstractDrawable}'s {@link AWTLegend}
     * .
     */
    public synchronized List<ILegend> getLegends() {
        List<ILegend> list = new ArrayList<ILegend>();

        // synchronized(components){
        for (AbstractDrawable c : components)
            if (c != null)
                if (c.hasLegend() && c.isLegendDisplayed())
                    list.add(c.getLegend());
        // }
        return list;
    }

    /**
     * Return true if the {@link Graph} contains at least one
     * {@link AbstractDrawable} that has {@link AWTLegend} that must be
     * displayed.
     */
    public synchronized int hasLegends() {
        int k = 0;

        // synchronized(components){
        for (AbstractDrawable c : components)
            if (c != null)
                if (c.hasLegend() && c.isLegendDisplayed())
                    k++;
        // }
        return k;
    }

    /* */

    /** Print out information concerning all Drawable of this composite. */
    @Override
    public synchronized String toString() {
        String output = "(Graph) #elements:" + components.size() + ":\n";

        int k = 0;
        synchronized(components){
            for (AbstractDrawable c : components) {
                if (c != null)
                    output += " Graph element [" + (k++) + "]:" + c.toString(1) + "\n";
                else
                    output += " Graph element [" + (k++) + "] (null)\n";
            }
        }
        return output;
    }
    
    

    /* */

    public boolean isSort() {
        return sort;
    }

    /**
     * Set sort to false to desactivate decomposition of drawable. 
     * 
     * This bypass ranking polygons w.r.t. camera. This will produce visual cue
     * if the scene is dynamic (changing the list of polygons or viewpoints).
     * @param sort
     */
    public void setSort(boolean sort) {
        this.sort = sort;
    }

    public Scene getScene() {
        return scene;
    }

    protected List<AbstractDrawable> components;
    protected Scene scene;
    protected Transform transform;
    // protected OrderingStrategy strategy;

    protected boolean VERBOSE = false;
    protected AbstractOrderingStrategy strategy;
    protected boolean sort = true;
}
