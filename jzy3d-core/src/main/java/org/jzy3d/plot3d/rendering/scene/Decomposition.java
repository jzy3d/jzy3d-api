package org.jzy3d.plot3d.rendering.scene;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Wireframeable;

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
          monotypes.add((Polygon) c);
      }
    }
    return monotypes;
  }

  /**
   * Decompose all input drawable that are of type {@link Composite} into a list of non decomposable
   * {@link Drawable}s.
   * 
   * Note that
   * <ul>
   * <li>A {@link Drawable#isDisplayed()}=false will be skipped and neither decomposed, neither in
   * output list
   * <li>A {@link Drawable#isFaceDisplayed()}=false will be kept but not decomposed as object with
   * wireframe only or boundary only do not need a decomposition for rendering properly with alpha.
   * </ul>
   */
  public static List<Drawable> getDecomposition(List<? extends Drawable> drawables) {
    List<Drawable> monotypes = new ArrayList<>();

    for (Drawable drawable : drawables) {
      // only work on displayed drawable
      if (drawable != null && drawable.isDisplayed()) {
        if (drawable instanceof Composite) {
          Composite composite = (Composite)drawable;
          monotypes.addAll(getDecomposition(composite));
        }
        else {
          monotypes.add(drawable);
        }
      }
      // skip hidden drawable
    }
    return monotypes;
  }

  /** Recursively expand all monotype {@link Drawable}s from the given {@link Composite}. */
  public static List<Drawable> getDecomposition(Composite input) {
    List<Drawable> selection = new ArrayList<>();
    
    // Do not decompose input if face not displayed
    if(!input.canDecompose()) {
      selection.add(input);
    }
    else {
      // composite internally make use of synchronisation on 
      // its list of child, so we do so
      synchronized (input.getDrawables()) {
        for (Drawable child : input.getDrawables()) {
          // only work on displayed drawable
          if (child != null && child.isDisplayed()) {
            if (child instanceof Composite) {
              Composite compositeChild = (Composite) child;
              
              // A non wireframeable composite is decomposed only 
              // if its faces are displayed
              if (compositeChild.canDecompose()) {
                selection.addAll(getDecomposition(compositeChild));
              } else {
                selection.add(compositeChild);
              }
            } else {
              selection.add(child);
            }
          }
          // skip hidden drawable
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
