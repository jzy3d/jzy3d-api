package org.jzy3d.plot3d.primitives.contour;

import java.util.List;
import org.jzy3d.maths.Array;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.LineStrip;


/**
 * Tells if we should merge with any LineStrip according to a maximum distance threshold
 * 
 * @author Martin Pernollet
 */
public class DefaultLineStripMergePolicy implements ILineStripMergePolicy {
  public DefaultLineStripMergePolicy() {
    this.threshold = 2;
  }

  public DefaultLineStripMergePolicy(double threshold) {
    this.threshold = threshold;
  }

  @Override
  public LineStrip mostMergeableIfAny(LineStrip ls, List<LineStrip> lines) {
    if (lines.size() == 0)
      return null;

    double[] scores = new double[lines.size()];
    int k = 0;
    for (LineStrip line : lines) {
      scores[k++] = mergeScore(line, ls);
    }
    int[] ids = Array.sortAscending(scores);
    if (scores[0] < threshold)
      return lines.get(ids[0]);
    else
      return null;
  }

  @Override
  public boolean mergeable(LineStrip ls1, LineStrip ls2) {
    return mergeScore(ls1, ls2) < threshold;
  }

  @Override
  public double mergeScore(LineStrip ls1, LineStrip ls2) {
    return minDist(ls1, ls2);
  }

  protected double minDist(LineStrip strip1, LineStrip strip2) {
    Coord3d a = strip1.get(0).xyz;
    Coord3d b = strip1.get(strip1.size() - 1).xyz;
    Coord3d c = strip2.get(0).xyz;
    Coord3d d = strip2.get(strip2.size() - 1).xyz;
    double bc = b.distance(c);
    double da = d.distance(a);
    return Math.min(bc, da);
  }

  protected double threshold = 0;
}
