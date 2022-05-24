package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.events.IDrawableListener;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.BoundingBox3d.Corners;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/**
 * A {@link Drawable} defines objects that may be rendered into an OpenGL context provided by a
 * {@link ICanvas}.
 * 
 * A {@link Drawable} must basically provide a rendering function called draw() that receives a
 * reference to a GL2 and a GLU context. It may also use a reference to a Camera in order to
 * implement specific behaviors according to the Camera position.
 * 
 * A {@link Drawable} provides services for setting the transformation factor that is used inside
 * the draw function, as well as a getter of the object's BoundingBox3d. Note that the BoundingBox
 * must be set by a concrete descendant of a {@link Drawable}.
 * 
 * A good practice is to define a setData function for initializing a {@link Drawable} and building
 * its polygons. Since each class may have its own inputs, setData is not part of the interface but
 * should be used as a convention. When not defining a setData function, a {@link Drawable} may have
 * its data loaded by an {@link add(Drawable)} function.
 * 
 * Note: A {@link Drawable} may last provide the information whether it is displayed or not,
 * according to a rendering into the FeedBack buffer. This is currently supported specifically for
 * the {@link AxisBox} object but could be extended with some few more algorithm for referencing all
 * GL2 polygons.
 * 
 * @author Martin Pernollet
 * 
 */
public abstract class Drawable implements IGLRenderer, ISortableDraw {
  protected Transform transform;
  protected Transform transformBefore;
  protected BoundingBox3d bbox;
  protected ILegend legend = null;
  protected List<IDrawableListener> listeners;
  protected boolean hasListeners = true;

  protected boolean displayed = true;
  protected boolean legendDisplayed = false;
  protected boolean boundingBoxDisplayed = false;
  protected Color boundingBoxColor = Color.BLACK.clone();

  protected SpaceTransformer spaceTransformer = null;

  /**
   * Performs all required operation to cleanup the Drawable.
   */
  public void dispose() {
    if (listeners != null)
      listeners.clear();
  }

  /**
   * Call OpenGL2 routines for rendering the object.
   */
  @Override
  public abstract void draw(IPainter painter);

  public abstract void applyGeometryTransform(Transform transform);

  public abstract void updateBounds();

  public void doTransform(IPainter painter) {
    if (transformBefore != null) {
      if (transformBefore != null)
        transformBefore.execute(painter, true);
      if (transform != null)
        transform.execute(painter, false);
    } else {
      if (transform != null)
        transform.execute(painter);
    }
  }

  protected void doDrawBoundsIfDisplayed(IPainter painter) {
    if (isBoundingBoxDisplayed()) {
      int width = 2; // getWireframeWidth()
      painter.box(bbox, getBoundingBoxColor(), 2, spaceTransformer);
    }
  }

  /**
   * Set object's transformation that is applied at the beginning of a call to
   * {@link #draw(IPainter)}.
   * 
   * @param transform
   */
  public void setTransform(Transform transform) {
    this.transform = transform;

    fireDrawableChanged(DrawableChangedEvent.FIELD_TRANSFORM);
  }

  /**
   * Get object's transformation that is applied at the beginning of a call to
   * {@link #draw(IPainter)}.
   * 
   * @return transform
   */
  public Transform getTransform() {
    return transform;
  }

  public Transform getTransformBefore() {
    return transformBefore;
  }

  public void setTransformBefore(Transform transformBefore) {
    getBounds().apply(transformBefore);
    
    this.transformBefore = transformBefore;
  }

  /**
   * Return the BoundingBox of this object.
   * 
   * @return a bounding box
   */
  public BoundingBox3d getBounds() {
    return bbox;
  }

  /**
   * Return the barycentre of this object, which is computed as the center of its bounding box. If
   * the bounding box is not available, the returned value is {@link Coord3d.INVALID}
   * 
   * @return the center of the bounding box, or {@link Coord3d.INVALID}.
   */
  public Coord3d getBarycentre() {
    if (getBounds() != null)
      return getBounds().getCenter();
    else
      return Coord3d.INVALID.clone();
  }

  /**
   * Set to true or false the displayed status of this object.
   * 
   * @param status
   */
  public void setDisplayed(boolean status) {
    displayed = status;

    fireDrawableChanged(DrawableChangedEvent.FIELD_DISPLAYED);
  }

  /** Return the display status of this object. */
  public boolean isDisplayed() {
    return displayed;
  }

  /** Return the distance of the object center to the {@link Camera}'s eye. */
  @Override
  public double getDistance(Camera camera) {
    return getBarycentre().distance(camera.getEye());
  }

  @Override
  public double getShortestDistance(Camera camera) {
    return getDistance(camera);
  }

  @Override
  public double getLongestDistance(Camera camera) {
    return getDistance(camera);
  }

  /* */

  public void setLegend(ILegend legend) {
    this.legend = legend;
    legendDisplayed = true;
    fireDrawableChanged(DrawableChangedEvent.FIELD_METADATA);
  }

  public ILegend getLegend() {
    return legend;
  }

  public boolean hasLegend() {
    return (legend != null);
  }

  public void setLegendDisplayed(boolean status) {
    legendDisplayed = status;
  }

  public boolean isLegendDisplayed() {
    return legendDisplayed;
  }

  public boolean isBoundingBoxDisplayed() {
    return boundingBoxDisplayed;
  }

  public void setBoundingBoxDisplayed(boolean boundingBoxDisplayed) {
    this.boundingBoxDisplayed = boundingBoxDisplayed;
  }

  public Color getBoundingBoxColor() {
    return boundingBoxColor;
  }

  public void setBoundingBoxColor(Color boundingBoxColor) {
    this.boundingBoxColor = boundingBoxColor;
  }

  public SpaceTransformer getSpaceTransformer() {
    return spaceTransformer;
  }

  public void setSpaceTransformer(SpaceTransformer spaceTransformer) {
    this.spaceTransformer = spaceTransformer;
  }

  /* */

  public void addDrawableListener(IDrawableListener listener) {
    if (listeners == null)
      listeners = new ArrayList<IDrawableListener>();
    listeners.add(listener);
    hasListeners = true;
  }

  public void removeDrawableListener(IDrawableListener listener) {
    listeners.remove(listener);
  }

  protected void fireDrawableChanged(int eventType) {
    if (listeners != null) {
      fireDrawableChanged(new DrawableChangedEvent(this, eventType));
    }
  }

  protected void fireDrawableChanged(DrawableChangedEvent e) {
    if (listeners != null) {
      for (IDrawableListener listener : listeners) {
        listener.drawableChanged(e);
      }
    }
  }
  
  public Wireframeable asWireframeable() {
    if(this instanceof Wireframeable) {
      return (Wireframeable)this;
    }
    else {
      return null;
    }
  }

  /* */

  @Override
  public String toString() {
    return toString(0);
  }

  public String toString(int depth) {
    return Utils.blanks(depth) + "(" + this.getClass().getSimpleName() + ")";
  }
}
