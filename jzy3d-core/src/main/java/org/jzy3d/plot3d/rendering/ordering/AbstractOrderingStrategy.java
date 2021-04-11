package org.jzy3d.plot3d.rendering.ordering;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.transform.Transform;



/**
 * An ordering strategy is a {@link Comparator} for {@link Drawable}s that may compute the priority
 * between {@link Drawable}s d1 and d2.
 *
 * @author Martin Pernollet
 *
 */
public abstract class AbstractOrderingStrategy implements Comparator<Drawable> {

  /** Returns a score for ranking this drawable among other drawables of the scenegraph. */
  public abstract double score(Drawable drawable);

  public abstract double score(Coord3d coord);

  /**
   * @throws java.lang.IllegalArgumentException: "Comparison method violates its general contract!"
   *         on some JVM. Fix with System.setProperty("java.util.Arrays.useLegacyMergeSort",
   *         "true");
   * @param monotypes
   * @param cam
   */
  public void sort(List<Drawable> monotypes, Camera cam) {
    setCamera(cam);
    Collections.sort(monotypes, this);
  }

  /**
   * Return the opposite of closest distance, so that closest distance is sorted after farest
   * distance.
   */
  protected int comparison(double dist1, double dist2) {
    if (dist1 == dist2)
      return 0;
    else if (dist1 < dist2)
      return 1;// *Math.max((int)Math.abs(dist1-dist2),1);
    else
      return -1;// *Math.max((int)Math.abs(dist1-dist2),1);
  }

  /* */

  public void setAll(Camera camera, Transform transform) {
    this.camera = camera;
    this.transform = transform;
  }

  public Camera getCamera() {
    return camera;
  }

  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  public Transform getTransform() {
    return transform;
  }

  public void setTransform(Transform transform) {
    this.transform = transform;
  }

  public View getView() {
    return view;
  }

  public void setView(View view) {
    this.view = view;
  }

  /* */


  protected View view;
  protected Camera camera;
  protected Transform transform;
}
