package org.jzy3d.plot3d.rendering.view.lod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting.Bounds;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting.FaceColor;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting.WireColor;

public class LODCandidates {
  List<LODSetting> rank = new ArrayList<>();
  List<LODSetting> reverseRank;
  
  public static final LODSetting BOUNDS_ONLY = new LODSetting("BOUNDS_ONLY", Bounds.ON);

  public LODCandidates() {
    rank.add(new LODSetting("0", FaceColor.SMOOTH, WireColor.OFF));
    rank.add(new LODSetting("1", FaceColor.FLAT, WireColor.OFF));
    rank.add(new LODSetting("2", FaceColor.OFF, WireColor.SMOOTH));
    rank.add(new LODSetting("3", FaceColor.OFF, WireColor.FLAT));
    rank.add(new LODSetting("4", FaceColor.OFF, WireColor.UNIFORM));
    rank.add(BOUNDS_ONLY);
    
    reverseRank = new ArrayList<LODSetting>(rank);
    Collections.reverse(reverseRank);
  }
  
  public LODCandidates(LODSetting... candidates) {
    for (int i = 0; i < candidates.length; i++) {
      rank.add(candidates[i]);
    }
    reverseRank = new ArrayList<LODSetting>(rank);
    Collections.reverse(reverseRank);
  }

  public List<LODSetting> getRank() {
    return rank;
  }

  public void setRank(List<LODSetting> rank) {
    this.rank = rank;
  }

  public List<LODSetting> getReverseRank() {
    return reverseRank;
  }

  public void setReverseRank(List<LODSetting> reverseRank) {
    this.reverseRank = reverseRank;
  }
}
