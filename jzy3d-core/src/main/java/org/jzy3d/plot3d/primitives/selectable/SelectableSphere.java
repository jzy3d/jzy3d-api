package org.jzy3d.plot3d.primitives.selectable;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Polygon2d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.builder.concrete.SphereScatterGenerator;
import org.jzy3d.plot3d.primitives.Sphere;
import org.jzy3d.plot3d.rendering.view.Camera;

public class SelectableSphere extends Sphere implements Selectable {
  public SelectableSphere() {
    this(Coord3d.ORIGIN, 10f, 15, Color.BLACK);
  }

  public SelectableSphere(Coord3d position, float radius, int slicing, Color color) {
    super(position, radius, slicing, color);
    buildAnchors();
  }

  @Override
  public void draw(IPainter painter) {
    super.draw(painter);

    // Draws selection anchors
    painter.glBegin_Point();
    painter.color(Color.RED);
    for (Coord3d a : anchors)
      painter.vertex(a);
    painter.glEnd();
  }

  @Override
  public void project(IPainter painter, Camera cam) {
    projection = cam.modelToScreen(painter, anchors);
  }

  public List<Coord3d> getProjection() {
    return projection;
  }

  @Override
  public void setPosition(Coord3d position) {
    super.setPosition(position);
    buildAnchors();
  }

  @Override
  public void setVolume(float radius) {
    super.setVolume(radius);
    buildAnchors();
  }

  protected void buildAnchors() {
    anchors = buildAnchors(position, radius);
  }

  protected List<Coord3d> buildAnchors(Coord3d position, float radius) {
    return SphereScatterGenerator.generate(position, radius, PRECISION, false);
  }

  @Override
  public Polygon2d getHull2d() {
    throw new RuntimeException("not implemented");
  }

  @Override
  public List<Coord3d> getLastProjection() {
    throw new RuntimeException("not implemented");
  }

  /*********************************************/

  public void setHighlighted(boolean value) {
    isHighlighted = value;
  }

  public boolean isHighlighted() {
    return isHighlighted;
  }

  public void resetHighlighting() {
    this.isHighlighted = false;
  }

  protected List<Coord3d> anchors;
  protected int PRECISION = 10;
  protected boolean isHighlighted = false;

  protected List<Coord3d> projection;
}
