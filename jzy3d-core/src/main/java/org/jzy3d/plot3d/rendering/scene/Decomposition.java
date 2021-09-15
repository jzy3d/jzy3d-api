package org.jzy3d.plot3d.rendering.scene;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Polygon;

public class Decomposition {
  /**
   * Decompose all input drawable that are of type {@link Composite} into a list of non decomposable
   * {@link Polygon}s.
   * 
   * Any {@link Drawable} not of {@link Polygon} type will be ignored.
   */
  public static List<Polygon> getPolygonDecomposition(List<? extends Drawable> drawables) {
    List<Polygon> monotypes = new ArrayList<>();

    for (Drawable c : drawables) {
      if (c != null && c.isDisplayed()) {
        if (c instanceof Composite)
          monotypes.addAll(getPolygonDecomposition((Composite) c));
        else if (c instanceof Polygon)
          monotypes.add((Polygon)c);
      }
    }
    return monotypes;
  }

  /**
   * Decompose all input drawable that are of type {@link Composite} into a list of non decomposable
   * {@link Drawable}s
   */
  public static List<Drawable> getDecomposition(List<? extends Drawable> drawables) {
    List<Drawable> monotypes = new ArrayList<>();

    for (Drawable c : drawables) {
      if (c != null && c.isDisplayed()) {
        if (c instanceof Composite)
          monotypes.addAll(getDecomposition((Composite) c));
        else if (c instanceof Drawable)
          monotypes.add(c);
      }
    }
    return monotypes;
  }

  /** Recursively expand all monotype {@link Drawable}s from the given {@link Composite}. */
  public static List<Drawable> getDecomposition(Composite input) {
    List<Drawable> selection = new ArrayList<>();

    // composite internally make use of synchronisation on its list of child, so we do so
    synchronized (input.getDrawables()) {
      for (Drawable c : input.getDrawables()) {
        if (c != null && c.isDisplayed()) {
          if (c instanceof Composite)
            selection.addAll(getDecomposition((Composite) c));
          else if (c instanceof Drawable)
            selection.add(c);
        }
      }
    }
    return selection;
  }

  /**
   * Recursively expand all monotype {@link Polygon}s from the given {@link Composite}.
   * 
   * Any {@link Drawable} not of {@link Polygon} type will be ignored.
   */
  public static List<Polygon> getPolygonDecomposition(Composite input) {
    List<Polygon> selection = new ArrayList<>();

    // composite internally make use of synchronisation on its list of child, so we do so
    synchronized (input.getDrawables()) {
      for (Drawable c : input.getDrawables()) {
        if (c != null && c.isDisplayed()) {
          if (c instanceof Composite)
            selection.addAll(getPolygonDecomposition((Composite) c));
          else if (c instanceof Polygon)
            selection.add((Polygon) c);
        }
      }
    }
    return selection;
  }

  private Decomposition() {}
}
