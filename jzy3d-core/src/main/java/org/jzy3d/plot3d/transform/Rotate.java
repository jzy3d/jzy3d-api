package org.jzy3d.plot3d.transform;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;

/**
 * Rotate is a {@link Transformer} that stores the angle and rotate values required to perform the
 * effective OpenGL rotation in the ModelView matrix.
 * 
 * @see https://docs.gl/gl3/glRotate
 * 
 * @author Martin Pernollet
 */
public class Rotate implements Transformer {

  /**
   * Initialize a Rotation of angle degrees around the vector (x,y,z)
   * 
   * @param angle
   * @param rotate
   */
  public Rotate(float angle, Coord3d rotate) {
    this.angle = angle;
    this.rotate = rotate;
  }

  public Rotate(double angle, Coord3d rotate) {
    this.angle = (float) angle;
    this.rotate = rotate;
  }

  /**
   * produces a rotation of angle degrees around the vector (𝑥,𝑦,𝑧)
   */
  @Override
  public void execute(IPainter painter) {
    painter.glRotatef(angle, rotate.x, rotate.y, rotate.z);
  }

  @Override
  public Coord3d compute(Coord3d input) {
    return input.rotate(angle, rotate);
  }

  @Override
  public String toString() {
    return "(Rotate)a=" + angle + " " + rotate;
  }

  public float getAngle() {
    return angle;
  }

  public void setAngle(float angle) {
    this.angle = angle;
  }

  public Coord3d getRotate() {
    return rotate;
  }

  public void setRotate(Coord3d rotate) {
    this.rotate = rotate;
  }

  private float angle;
  private Coord3d rotate;
}
