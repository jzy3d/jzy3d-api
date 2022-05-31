package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Vector3d;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.Translate;

/**
 * @author Dr. Oliver Rettig, DHBW-Karlsruhe, Germany, 2019
 */
public class Arrow extends Composite {

  protected Cylinder cylinder;
  protected Cone cone;

  public void setData(Vector3d vec, float radius, int slices, int rings, Color color) {
    Coord3d position = vec.getCenter();
    float length = vec.norm();
    float coneHeight = radius * 2.5f;
    float cylinderHeight = length - coneHeight;

    cylinder = new Cylinder();
    cylinder.setData(new Coord3d(0, 0, -length / 2f), cylinderHeight, radius, slices, rings, color);
    
    cone = new Cone();
    cone.setData(new Coord3d(0, 0, length / 2d - coneHeight), coneHeight, radius * 1.6f, slices,
        rings, color);
    
    add(cylinder);
    add(cone);
    
    setWireframeDisplayed(isWireframeDisplayed());

    Transform trans = new Transform();
    Rotate rot = createRotateTo(new Coord3d(0d, 0d, 1d), vec.vector());
    trans.add(rot);
    Translate translate = new Translate(position);
    trans.add(translate);
    applyGeometryTransform(trans);
  }

  private static Rotate createRotateTo(Coord3d from, Coord3d to) {
    double fromMag = (float) Math.sqrt(from.x * from.x + from.y * from.y + from.z * from.z);
    double toMag = (float) Math.sqrt(to.x * to.x + to.y * to.y + to.z * to.z);
    double angle = Math.acos(from.dot(to) / fromMag / toMag) * 180f / Math.PI;
    return new Rotate(angle, from.cross(to));
  }
}
