package org.jzy3d.plot3d.rendering.scene;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.primitives.selectable.Selectable;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.ordering.DefaultOrderingStrategy;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.transform.Transform;

/**
 * The scene's {@link Graph} basically stores the scene content and facilitate objects control.
 * 
 * The graph may decompose all {@link Composite} into a list of their {@link Drawable}s primitives
 * if constructor is called with parameters enabling sorting.
 * 
 * The list of primitives is ordered using either the provided {@link DefaultOrderingStrategy} or an
 * other specified {@link AbstractOrderingStrategy}. Sorting is usefull for handling transparency
 * properly.
 * 
 * The {@link Graph} maintains a reference to its mother {@link Scene} in order to inform the
 * {@link View}s when its content has change and that repainting is required.
 * 
 * The add() method allows adding a {@link Drawable} to the scene Graph and updates all views'
 * viewpoint in order to target the center of the scene.
 * 
 * @author Martin Pernollet
 */
public class Graph {
  protected List<Drawable> components;
  protected Scene scene;
  protected Transform transform;
  protected AbstractOrderingStrategy strategy;
  protected boolean sort = true;

  protected List<GraphListener> graphListener;

  protected BoundingBox3d clipBox;
  protected boolean clipIncludesLimits;
  protected static final float CLIP_MARGIN_RATIO = 1f/1000;
  
  
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
    this();
    this.scene = scene;
    this.strategy = strategy;
    this.sort = sort;
  }
  
  protected Graph() {
    this.components = new ArrayList<Drawable>();
    this.graphListener = new ArrayList<>();
  }


  public synchronized void dispose() {
    // synchronized(components){
    for (Drawable c : components)
      if (c != null)
        c.dispose();
    // }
    components.clear();
    scene = null;
  }

  /* */

  /**
   * Add a Drawable to the graph and call all views' so that they update their bounds according to
   * their mode (automatic or manual).
   * 
   * Addition to the graph is synchronized.
   * 
   * @param drawable : The drawable that must be added to the scene graph.
   * @param update : should be true if you wish to have all the views updated with old bounds
   *        including drawable bounds
   */
  public void add(Drawable drawable, boolean updateViews) {
    synchronized (this) {
      components.add(drawable);
    }

    if (updateViews)
      viewsUpdateBounds();
  }

  public void add(Drawable drawable) {
    add(drawable, true);
  }

  public void add(List<? extends Drawable> drawables, boolean updateViews) {
    for (Drawable d : drawables)
      add(d, false);
    if (updateViews)
      viewsUpdateBounds();
  }


  public void add(List<? extends Drawable> drawables) {
    add(drawables, true);
  }

  /**
   * Delete a Drawable from the SceneGraph and update all views' viewpoint in order to target the
   * center of the scene.
   * 
   * @param drawable The drawable that must be deleted from the scene graph.
   */
  public boolean remove(Drawable drawable, boolean updateViews) {
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

  public boolean remove(Drawable drawable) {
    return remove(drawable, true);
  }

  public List<Drawable> getAll() {
    return components;
  }

  public synchronized List<IGLBindedResource> getAllGLBindedResources() {
    List<IGLBindedResource> out = new ArrayList<>();
    for (Drawable c : components) {
      if (c instanceof IGLBindedResource) {
        out.add((IGLBindedResource) c);
      }
    }
    return out;
  }

  public void mountAllGLBindedResources(IPainter painter) {
    final List<IGLBindedResource> all = getAllGLBindedResources();
    for (IGLBindedResource r : all)
      if (!r.hasMountedOnce())
        r.mount(painter);
    fireMountAll();
  }

  public interface GraphListener {
    public void onMountAll();
  }

  protected void fireMountAll() {
    for (GraphListener listener : graphListener) {
      listener.onMountAll();
    }
  }

  /* */

  public List<GraphListener> getGraphListener() {
    return graphListener;
  }

  public void addGraphListener(GraphListener graphListener) {
    this.graphListener.add(graphListener);
  }

  /**
   * Decompose all {@link Composite} objects, and sort the extracted monotype (i.e.
   * non-{@link Composite} {@link Drawable}s) in order to render them according to the default -or
   * defined- {@link AbstractOrderingStrategy}.
   * 
   */
  public void draw(IPainter painter) {
    // activate clipping if defined
    if(clipBox!=null) {

      transform.execute(painter);
      
      
      if(clipIncludesLimits)
        painter.clip(clipBox.marginRatio(CLIP_MARGIN_RATIO)); // make the box a little bigger
      else
        painter.clip(clipBox);
      
      painter.clipOn();

    }
    
    // draw
    draw(painter, components, sort);
    
    // reset clipping if defined
    if(clipBox!=null) {
      painter.clipOff();
    }
  }
  
  
  


  public synchronized void draw(IPainter painter, List<Drawable> components, boolean sort) {
    painter.glMatrixMode_ModelView();
    if (!sort || strategy==null) {
      drawSimple(painter, components);
    } else {
      drawDecomposition(painter);
    }
  }

  /** render all items of the graph */
  public void drawSimple(IPainter painter, List<Drawable> components) {
    for (Drawable d : components)
      if (d.isDisplayed())
        d.draw(painter);
  }

  /** render all items of the graph after decomposing all composite item into primitive drawables */
  public void drawDecomposition(IPainter painter) {
    List<Drawable> monotypes = getDecomposition();
    strategy.sort(monotypes, painter.getCamera());

    for (Drawable d : monotypes) {
      if (d.isDisplayed())
        d.draw(painter);
    }
  }

  /**
   * Expand all {@link AbstractComposites} instance into a list of atomic {@link Drawable} types and
   * return all the current Graph primitives decomposition.
   */
  public List<Drawable> getDecomposition() {
    List<Drawable> monotypes;
    synchronized (components) {
      monotypes = Decomposition.getDecomposition(components);
    }
    return monotypes;
  }

  /** Update all interactive {@link Drawable} projections */
  public synchronized void project(IPainter painter, Camera camera) {
    for (Drawable d : components) {
      if (d instanceof Selectable)
        ((Selectable) d).project(painter, camera);
    }
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
   * Delegate transforming iteratively to all Drawable of this graph and stores the given transform
   * for keeping the ability of retrieving it.
   */
  public synchronized void setTransform(Transform transform) {
    this.transform = transform;

    synchronized (components) {
      for (Drawable c : components) {
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
   * Creates and return a BoundingBox3d that embed all Drawable bounds, among those that have a
   * defined bounding box.
   */
  public synchronized BoundingBox3d getBounds() {
    if (components.size() == 0) {
      // return a non initialized bound
      return new BoundingBox3d();
    } else {
      BoundingBox3d box = new BoundingBox3d();

      for (Drawable c : components) {
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
  

  public BoundingBox3d getClipBox() {
    return clipBox;
  }

  public void setClipBox(BoundingBox3d clipBox) {
    setClipBox(clipBox, true);
  }

  public void setClipBox(BoundingBox3d clipBox, boolean includeLimits) {
    this.clipBox = clipBox;
    this.clipIncludesLimits = includeLimits;
    
    viewsShoot();
  }

  /**
   * Return the list of available {@link Drawable}'s {@link ILegend} .
   */
  public synchronized List<ILegend> getLegends() {
    List<ILegend> list = new ArrayList<ILegend>();

    for (Drawable c : components) {
      if (c != null) {
        if (c.hasLegend() && c.isLegendDisplayed()) {
          list.add(c.getLegend());
        }
      }
    }
    return list;
  }

  /**
   * Return true if the {@link Graph} contains at least one {@link Drawable} that has
   * {@link AWTLegend} that must be displayed.
   */
  public synchronized int hasLegends() {
    int k = 0;

    // synchronized(components){
    for (Drawable c : components)
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
    synchronized (components) {
      for (Drawable c : components) {
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
   * This bypass ranking polygons w.r.t. camera. This will produce visual cue if the scene is
   * dynamic (changing the list of polygons or viewpoints).
   * 
   * @param sort
   */
  public void setSort(boolean sort) {
    this.sort = sort;
  }

  public Scene getScene() {
    return scene;
  }
  
  
  
  protected void viewsUpdateBounds() {
    for (View view : scene.views)
      view.updateBounds();
  }

  protected void viewsShoot() {
    if(scene!=null) {
      for (View view : scene.views)
        view.shoot();
    }
  }
}
