package org.jzy3d.plot3d.transform;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;

/**
 * Translate is a {@link Transformer} that stores the offset required to perform the effective
 * OpenGL2 translation in the ModelView Matrix.
 * 
 * @author Martin Pernollet
 */
public class Translate implements Transformer {
  protected Translate() {

  }

  /**
   * Initialize a Translate.
   * 
   * @param shift translation offset.
   */
  public Translate(Coord3d shift) {
    this.shift = shift;
  }

  @Override
  public void execute(IPainter painter) {
    painter.glTranslatef(shift.x, shift.y, shift.z);
  }

  @Override
  public Coord3d compute(Coord3d input) {
    return input.add(shift);
  }

  @Override
  public String toString() {
    return "(Translate)" + shift;
  }

  /**************************************************/

  private Coord3d shift;
}
