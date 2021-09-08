package org.jzy3d.plot3d.rendering.view.annotation;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Geometry;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.rendering.scene.Decomposition;

/**
 * Draws the barycenter of an {@link Geometry} and a line each point and the barycenter.
 * 
 * @author Martin
 */
public class BarycenterAnnotation extends Composite {
  public BarycenterAnnotation(Geometry annotated) {
    Color c = Color.BLACK;

    this.annotated = annotated;
    bary = new Point();
    bary.setWidth(5);
    lines = new ArrayList<>();

    for (Point pt : annotated.getPoints()) {
      Point b2 = bary.clone();
      Point pt2 = pt.clone();

      LineStrip line = new LineStrip(b2, pt2);
      line.setWireframeColor(c);
      lines.add(line);
    }

    add(bary);
    add(lines);

    setColor(c);
    setWireframeColor(c);
  }

  @Override
  public void draw(IPainter painter) {
    bary.xyz = annotated.getBarycentre();
    int k = 0;
    for (LineStrip line : lines) {
      line.get(0).xyz = bary.xyz.clone();
      line.get(1).xyz = annotated.get(k).xyz.clone();
      k++;
    }
    super.draw(painter);
  }

  public static List<BarycenterAnnotation> annotate(Composite composite) {
    List<BarycenterAnnotation> annotations = new ArrayList<>();

    List<Drawable> items = Decomposition.getDecomposition(composite);
    for (Drawable item : items) {
      if (item instanceof Geometry)
        annotations.add(new BarycenterAnnotation((Geometry) item));
    }
    return annotations;
  }

  protected Geometry annotated;
  protected Point bary;
  protected List<LineStrip> lines;
}
