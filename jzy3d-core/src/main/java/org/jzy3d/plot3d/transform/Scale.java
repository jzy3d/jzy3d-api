package org.jzy3d.plot3d.transform;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;

/**
 * Scale is a {@link Transformer} that stores the scaling factor required to perform the effective
 * OpenGL2 scaling in the ModelView Matrix.
 * 
 * @author Martin Pernollet
 */
public class Scale implements Transformer {

  /**
   * Initialize a Scale.
   * 
   * @param scale scaling factor.
   */
  public Scale(Coord3d scale) {
    this.scale = scale;
  }

  @Override
  public void execute(IPainter painter) {
    painter.glScalef(scale.x, scale.y, scale.z);
  }

  @Override
  public Coord3d compute(Coord3d input) {
    return input.mul(scale);
  }

  @Override
  public String toString() {
    return "(Scale)" + scale;
  }

  /**************************************************/

  private Coord3d scale;
}
