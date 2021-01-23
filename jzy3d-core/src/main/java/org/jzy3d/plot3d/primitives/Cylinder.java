package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;


public class Cylinder extends Composite {
  public void setData(Coord3d position, float height, float radius, int slices, int rings,
      Color color) {
    this.height = height;
    this.radius = radius;

    // Create sides
    top = new Polygon();
    low = new Polygon();

    for (int i = 0; i < slices; i++) {
      float angleBorder1 = (float) i * 2 * (float) Math.PI / slices;
      float angleBorder2 = (float) (i + 1) * 2 * (float) Math.PI / slices;

      Coord2d border1 = new Coord2d(angleBorder1, radius).cartesian();
      Coord2d border2 = new Coord2d(angleBorder2, radius).cartesian();

      Quad face = new Quad();
      face.add(new Point(new Coord3d(position.x + border1.x, position.y + border1.y, position.z)));
      face.add(new Point(
          new Coord3d(position.x + border1.x, position.y + border1.y, position.z + height)));
      face.add(new Point(
          new Coord3d(position.x + border2.x, position.y + border2.y, position.z + height)));
      face.add(new Point(new Coord3d(position.x + border2.x, position.y + border2.y, position.z)));
      face.setColor(color);
      face.setWireframeDisplayed(false);

      // add the polygon to the cylinder
      add(face);

      // compute top and low faces
      low.add(new Point(new Coord3d(position.x + border1.x, position.y + border1.y, position.z)));
      top.add(new Point(
          new Coord3d(position.x + border1.x, position.y + border1.y, position.z + height)));
    }
    low.setColor(color);
    top.setColor(color);
    add(top);
    add(low);
  }

  protected float height;
  protected float radius;
  protected Polygon top;
  protected Polygon low;
}
