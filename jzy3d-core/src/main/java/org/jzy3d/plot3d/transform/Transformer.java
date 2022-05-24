package org.jzy3d.plot3d.transform;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;


/**
 * A {@link Transformer} is able to execute a transformation into the OpenGL ModelView Matrix. A
 * {@link Transformer} is basically a Functor object that stores a transformation, and performs the
 * effective GL2 call to perform the transformation.
 * 
 * @author Martin Pernollet
 */
public interface Transformer {
  /**
   * Execute the transformation to the current GL context.
   * 
   * As {@link Transformer} are usually help by an {@ AbstractDrawable}, the transformation will
   * apply to this {@ AbstractDrawable}.
   * 
   * Computation is performed by GPU.
   * 
   * @param painter TODO
   */
  public void execute(IPainter painter);

  /**
   * Apply the transformations to the input coordinates.
   * 
   * Computation is performed by CPU.
   */
  public Coord3d compute(Coord3d input);
}
