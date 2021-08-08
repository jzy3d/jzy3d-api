package org.jzy3d.plot3d.transform;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;

/**
 * A {@link Transform} stores a sequence of {@link Transformer}s, that are of concrete type
 * {@link Rotate}, {@link Scale}, {@link Translate}.
 * 
 * When a Transform is executed by default, it first loads the identity matrix before executing the
 * sequence of Transformers.
 * 
 * @author Martin Pernollet
 */
public class Transform {

  /** Initialize a list of Transformer. */
  public Transform() {
    sequence = new ArrayList<Transformer>();
  }

  /**
   * Initialize a list of Transformer with a single Transformer as sequence.
   */
  public Transform(Transformer transformer) {
    sequence = new ArrayList<Transformer>();
    sequence.add(transformer);

  }
  
  public Transform(Transformer... transformer) {
    sequence = new ArrayList<Transformer>();
    for(Transformer t: transformer) {
      sequence.add(t);
    }
  }

  /**
   * Initialize a list of Transformer with the sequence hold by the given Transform.
   */
  public Transform(Transform transform) {
    sequence = new ArrayList<Transformer>();
    for (Transformer next : transform.sequence)
      sequence.add(next);
  }

  /* */

  /**
   * Appends a Transformer to the sequence that this Transform must performs.
   * 
   * @param next
   */
  public void add(Transformer next) {
    sequence.add(next);
  }

  /**
   * Appends a Transform to the current Transform.
   * 
   * @param next
   */
  public void add(Transform transform) {
    for (Transformer next : transform.sequence)
      sequence.add(next);
  }

  /* */

  /**
   * Load the identity matrix and executes the stored sequence of Transformer.
   * 
   * @param painter TODO
   */
  public void execute(IPainter painter) {
    execute(painter, true);
  }

  public void execute(IPainter painter, boolean loadIdentity) {
    if (loadIdentity) {
      painter.glLoadIdentity();
    }

    for (Transformer t : sequence)
      t.execute(painter);
  }

  /** Apply the transformations to the input coordinate */
  public Coord3d compute(Coord3d input) {
    Coord3d output = input.clone();
    for (Transformer t : sequence) {
      output = t.compute(output);
    }
    return output;
  }

  @Override
  public String toString() {
    String txt = "";
    for (Transformer t : sequence)
      txt += " * " + t.toString();
    return txt;
  }

  /***********************************************************/

  private List<Transformer> sequence;
}
