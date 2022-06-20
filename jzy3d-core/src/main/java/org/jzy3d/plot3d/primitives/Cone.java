package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;

/**
 * @author Dr. Oliver Rettig, DHBW-Karlsruhe, Germany, 2019
 */
public class Cone extends Composite {

  protected float height;
  protected float radius;
  protected Polygon bottom;

  public void setData(Coord3d position, float height, float radius, int slices, int rings,
      Color color) {
    this.height = height;
    this.radius = radius;

    bottom = new Polygon();

    for (int i = 0; i < slices; i++) {
      float angleBorder1 = (float) i * 2 * (float) Math.PI / slices;
      float angleBorder2 = (float) (i + 1) * 2 * (float) Math.PI / slices;

      Coord2d border1 = new Coord2d(angleBorder1, radius).cartesian();
      Coord2d border2 = new Coord2d(angleBorder2, radius).cartesian();

      Polygon face = new Polygon();
      face.add(new Point(new Coord3d(position.x + border1.x, position.y + border1.y, position.z)));
      face.add(new Point(new Coord3d(position.x, position.y, position.z + height)));
      face.add(new Point(new Coord3d(position.x + border2.x, position.y + border2.y, position.z)));
      face.setColor(color);

      add(face);

      // compute bottom face
      bottom
          .add(new Point(new Coord3d(position.x + border1.x, position.y + border1.y, position.z)));
    }

    bottom.setColor(color);
    add(bottom);
    
    setWireframeDisplayed(isWireframeDisplayed());

  }
}
