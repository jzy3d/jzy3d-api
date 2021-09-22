package org.jzy3d.plot3d.rendering.view.lod;

import java.util.HashMap;
import java.util.Map;
import org.jzy3d.plot3d.primitives.Wireframeable;

/**
 * Hold performance results of multiple candidate {@link LODSetting} aiming at choosing an
 * appropriate level of details according to the rendering time expectations.
 * 
 * @author Martin Pernollet
 *
 */
public class LODPerf {
  LODCandidates candidates;
  Map<LODSetting, Double> score = new HashMap<>();
  boolean debug = false;


  public LODPerf() {
    this(new LODCandidates());
  }

  public LODPerf(LODCandidates candidates) {
    this.candidates = candidates;
  }

  public double getScore(LODSetting setting) {
    return score.get(setting);
  }

  public void setScore(LODSetting setting, double value) {
    score.put(setting, value);
  }

  boolean otherwiseLowest = true;
  boolean otherwiseBoundsOnlyIfCandidate = true;

  public LODSetting applyBestCandidateBelow(double maxMili, Wireframeable wireframeable) {
    for (LODSetting s : candidates.getRank()) {
      if (getScore(s) < maxMili) {
        if (debug) {
          System.out.println("Apply to " + wireframeable + " select " + s.getName() + " for "
              + maxMili + "ms for score " + getScore(s));
        }
        s.apply(wireframeable);
        return s;
      }
    }

    if (otherwiseLowest) {
      LODSetting s = getLowestScore();
      if (s != null) {
        if (debug) {
          System.out.println("Apply to " + wireframeable + " select " + s.getName() + " for "
              + maxMili + "ms for score " + getScore(s));
        }
        s.apply(wireframeable);
      }
      return s;
    }

    if (otherwiseBoundsOnlyIfCandidate) {
      // Apply BOUNDS only if BOUNDS only is in the list, regardless it reaches the max score
      LODSetting s = LODCandidates.BOUNDS_ONLY;

      if (candidates.getRank().contains(s)) {
        s.apply(wireframeable);
        if (debug) {
          System.out.println("Apply to " + wireframeable + " select " + s.getName() + " for "
              + maxMili + "ms for score " + getScore(s));
        }
        return s;
      }

    }
    return null;

  }

  public LODSetting getLowestScore() {
    LODSetting minLOD = null;
    double minScore = Double.MAX_VALUE;

    for (LODSetting s : getCandidates().getRank()) {
      double score = getScore(s);

      if (score < minScore) {
        minLOD = s;
        minScore = score;
      }
    }
    return minLOD;
  }

  public LODCandidates getCandidates() {
    return candidates;
  }

  public void print() {
    for (LODSetting lodSetting : getCandidates().getReverseRank()) {
      double value = getScore(lodSetting);
      System.out.println("LODPerf eval : " + lodSetting.getName() + " took " + value + "ms");
    }
  }
}
