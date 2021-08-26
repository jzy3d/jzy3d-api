package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public class MultiDrawable extends Drawable{
  List<Drawable> drawables;
  int currentDrawable;

  public MultiDrawable() {
    this(new ArrayList<>());
  }

  public MultiDrawable(List<Drawable> drawables) {
    super();
    this.drawables = drawables;
  }

  public List<Drawable> getDrawables() {
    return drawables;
  }

  public void setDrawables(List<Drawable> drawables) {
    this.drawables = drawables;
  }

  public int getCurrentDrawable() {
    return currentDrawable;
  }

  public void setCurrentDrawable(int currentDrawable) {
    this.currentDrawable = currentDrawable;
  }
  
  public Drawable current() {
    return drawables.get(currentDrawable);
  }

  //
  
  @Override
  public void draw(IPainter painter) {
    current().draw(painter);
  }

  public void applyGeometryTransform(Transform transform) {
    current().applyGeometryTransform(transform);
  }

  public void updateBounds() {
    current().updateBounds();
  }

  public void doTransform(IPainter painter) {
    current().doTransform(painter);
  }

  protected void doDrawBoundsIfDisplayed(IPainter painter) {
    //current().doDr
  }


  /**
   * Set object's transformation that is applied at the beginning of a call to
   * {@link #draw(IPainter)}.
   * 
   * @param transform
   */
  public void setTransform(Transform transform) {
    current().setTransform(transform);
  }

  /**
   * Get object's transformation that is applied at the beginning of a call to
   * {@link #draw(IPainter)}.
   * 
   * @return transform
   */
  public Transform getTransform() {
    return current().getTransform();
  }

  public Transform getTransformBefore() {
    return current().getTransformBefore();
  }

  public void setTransformBefore(Transform transformBefore) {
    current().setTransformBefore(transformBefore);
  }

  /**
   * Return the BoundingBox of this object.
   * 
   * @return a bounding box
   */
  public BoundingBox3d getBounds() {
    return current().getBounds();
  }

  /**
   * Return the barycentre of this object, which is computed as the center of its bounding box. If
   * the bounding box is not available, the returned value is {@link Coord3d.INVALID}
   * 
   * @return the center of the bounding box, or {@link Coord3d.INVALID}.
   */
  public Coord3d getBarycentre() {
    return current().getBarycentre();
  }

  /**
   * Set to true or false the displayed status of this object.
   * 
   * @param status
   */
  public void setDisplayed(boolean status) {
    current().setDisplayed(status);
  }

  /** Return the display status of this object. */
  public boolean isDisplayed() {
    return current().isDisplayed();
  }

  /** Return the distance of the object center to the {@link Camera}'s eye. */
  @Override
  public double getDistance(Camera camera) {
    return current().getDistance(camera);
  }

  @Override
  public double getShortestDistance(Camera camera) {
    return current().getShortestDistance(camera);
  }

  @Override
  public double getLongestDistance(Camera camera) {
    return current().getLongestDistance(camera);
  }

  /* */

  public void setLegend(ILegend legend) {
    current().setLegend(legend);
  }

  public ILegend getLegend() {
    return current().getLegend();
  }

  public boolean hasLegend() {
    return current().hasLegend();
  }

  public void setLegendDisplayed(boolean status) {
    current().setLegendDisplayed(status);
  }

  public boolean isLegendDisplayed() {
    return current().isLegendDisplayed();
  }

  public boolean isBoundingBoxDisplayed() {
    return current().isBoundingBoxDisplayed();
  }

  public void setBoundingBoxDisplayed(boolean boundingBoxDisplayed) {
    current().setBoundingBoxDisplayed(boundingBoxDisplayed);
  }

  public Color getBoundingBoxColor() {
    return current().getBoundingBoxColor();
  }

  public void setBoundingBoxColor(Color boundingBoxColor) {
    this.current().setBoundingBoxColor(boundingBoxColor);
  }

  public SpaceTransformer getSpaceTransformer() {
    return current().getSpaceTransformer();
  }

  public void setSpaceTransformer(SpaceTransformer spaceTransformer) {
    current().setSpaceTransformer(spaceTransformer);
  }

  /* 

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
  }*/

  /* */

  @Override
  public String toString() {
    return toString(0);
  }

  public String toString(int depth) {
    return Utils.blanks(depth) + "(" + this.getClass().getSimpleName() + ")";
  }
  
  
}
