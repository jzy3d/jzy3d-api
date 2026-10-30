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
  
  protected Vector3d vector;
  
  protected static Vector3d createVector3d(Coord3d pos, Coord3d dir, float length) {
    Coord3d dirN = dir.getNormalizedTo(length / 2f);
    Coord3d end = pos.add(dirN);
    dirN = dirN.negative();
    Coord3d start = pos.add(dirN);
    return new Vector3d(start, end);
  }
  
  public Arrow() {}
  
  public Arrow(Coord3d pos, Coord3d dir, float length, float radius, int slices, int rings, Color color) {
    this();
    setData(createVector3d(pos, dir, length), radius, slices, rings, color);
  }

  public void setData(Vector3d vector, float radius, int slices, int rings, Color color) {
    this.vector = vector;
    
    Coord3d position = vector.getCenter();
    float length = vector.norm();
    float coneHeight = radius * 2.5f;
    float cylinderHeight = length - coneHeight;

    // Arrow body
    cylinder = new Cylinder();
    cylinder.setData(new Coord3d(0, 0, -length / 2f), cylinderHeight, radius, slices, rings, color);
    
    // Arrow extremity
    cone = new Cone();
    cone.setData(new Coord3d(0, 0, length / 2d - coneHeight), coneHeight, radius * 1.6f, slices,
        rings, color);
    
    add(cylinder);
    add(cone);
    
    // Apply the same wireframe settings to each element
    // of this composite
    setWireframeDisplayed(isWireframeDisplayed());

    
    Transform trans = new Transform();
    
    // Rotate shape
    Rotate rot = createRotateTo(new Coord3d(0d, 0d, 1d), vector.vector());
    
    if(Float.isFinite(rot.getAngle())){
      trans.add(rot);
    }

    // Shift shape
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

  public Vector3d getVector() {
    return vector;
  }
  
  
}
