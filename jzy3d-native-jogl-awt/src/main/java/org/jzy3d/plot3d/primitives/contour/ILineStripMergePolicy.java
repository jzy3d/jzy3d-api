package org.jzy3d.plot3d.primitives.contour;

import java.util.List;
import org.jzy3d.plot3d.primitives.LineStrip;


public interface ILineStripMergePolicy {
  public LineStrip mostMergeableIfAny(LineStrip ls, List<LineStrip> lines);

  public boolean mergeable(LineStrip ls1, LineStrip ls2);

  public double mergeScore(LineStrip ls1, LineStrip ls2);
}
