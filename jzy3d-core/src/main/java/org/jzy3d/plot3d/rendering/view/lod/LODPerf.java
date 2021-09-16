package org.jzy3d.plot3d.rendering.view.lod;

import java.util.HashMap;
import java.util.Map;
import org.jzy3d.plot3d.primitives.Wireframeable;

public class LODPerf {
  LODCandidates candidates;
  Map<LODSetting, Double> score = new HashMap<>(); 
  
  

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

  public LODSetting applyBestCandidateBelow(double maxMili, Wireframeable wireframeable) {
    for(LODSetting s: candidates.getRank()) {
      if(getScore(s)<maxMili) {
        System.out.println("Apply to " + wireframeable + " select " + s.getName()  +" for " + maxMili + "ms for score " +getScore(s));

        s.apply(wireframeable);
        return s;
      }
    }
    LODSetting s=LODCandidates.BOUNDS_ONLY;
    s.apply(wireframeable);
    System.out.println("Apply to " + wireframeable + " select " + s.getName()  +" for " + maxMili + "ms for score " +getScore(s));
    return s;
  }

  public LODCandidates getCandidates() {
    return candidates;
  }
}
