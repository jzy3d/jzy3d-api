package org.jzy3d.plot3d.rendering.scene;

import java.util.List;
import java.util.Vector;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.lights.LightSet;
import org.jzy3d.plot3d.rendering.view.View;



/**
 * A Scene holds a {@link Graph} to be rendered by a list {@link View}s.
 * 
 * The creation of Views is not of user concern, since it is handled during the registration of the
 * Scene by a {@link ICanvas}. The newView() is thus protected because it is supposed to be called
 * by a Canvas3d or a View only.
 * 
 * The Scene is called by the {@link Renderer3d} to provide the effective (protected) GL2 calls for
 * initialization (List and Texture loading), clearing of window, and current view rendering.
 *
 * @author Martin Pernollet
 */
public class Scene {
  protected Vector<View> views;
  protected Graph graph;
  protected LightSet lightSet;
  protected IChartFactory factory;

  public Scene(IChartFactory factory, boolean graphsort) {
    this.graph = factory.newGraph(this, factory.newOrderingStrategy(), graphsort);
    this.lightSet = new LightSet();
    this.views = new Vector<View>();
    this.factory = factory;
  }
  
  protected Scene(Graph graph) {
    this.graph = graph;
    this.lightSet = new LightSet();
    this.views = new Vector<View>();
  }


  /** Handles disposing of the Graph as well as all views pointing to this Graph. */
  public void dispose() {
    graph.dispose();
    for (View v : views)
      v.dispose();
    views.clear();
  }

  /***************************************************************/

  /** Attach a scene graph to this scene. */
  public void setGraph(Graph graph) {
    this.graph = graph;
  }

  /** Get the scene graph attached to this scene. */
  public Graph getGraph() {
    return this.graph;
  }

  /** Attach a light set to this scene. */
  public void setLightSet(LightSet lightSet) {
    this.lightSet = lightSet;
  }

  /** Get the light set attached to this scene. */
  public LightSet getLightSet() {
    return this.lightSet;
  }

  /***************************************************************/

  /** Add a list of drawable to the scene. */
  public void add(List<? extends Drawable> drawables) {
    this.graph.add(drawables);
  }

  /** Add a drawable to the scene. */
  public void add(Drawable drawable) {
    this.graph.add(drawable);
  }

  /** Add a drawable to the scene and refresh on demand. */
  public void add(Drawable drawable, boolean updateViews) {
    this.graph.add(drawable, updateViews);
  }

  /** Remove a drawable from the scene and refresh on demand. */
  public void remove(Drawable drawable, boolean updateViews) {
    this.graph.remove(drawable, updateViews);
  }

  /** Remove a drawable from the scene. */
  public void remove(Drawable drawable) {
    this.graph.remove(drawable);
  }

  /** Add a light to the scene. */
  public void add(Light drawable) {
    this.lightSet.add(drawable);
  }

  /** Remove a drawable from the scene. */
  public void remove(Light drawable) {
    this.lightSet.remove(drawable);
  }

  /**
   * Instantiate a View attached to the given Canvas, and return its reference.
   */
  public View newView(ICanvas canvas, Quality quality) {
    View view = factory.newView(this, canvas, quality);
    views.add(view);
    return view;
  }

  public void clearView(View view) {
    views.remove(view);
    view.dispose();
  }

  /** Return the scene {@link Graph} string representation. */
  @Override
  public String toString() {
    return graph.toString();
  }

}
