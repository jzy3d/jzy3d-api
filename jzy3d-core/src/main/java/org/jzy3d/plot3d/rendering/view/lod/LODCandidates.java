package org.jzy3d.plot3d.rendering.view.lod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jzy3d.painters.ColorModel;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting.Bounds;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting.FaceColor;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting.WireColor;

/**
 * List of Level-Of-Details settings, ranked from most good looking to less good looking. 
 * 
 * @see {@link LODPerf}
 * 
 * @author Martin Pernollet
 *
 */
public class LODCandidates {
  List<LODSetting> rank = new ArrayList<>();
  List<LODSetting> reverseRank;
  
  public static final LODSetting BOUNDS_ONLY = new LODSetting("BOUNDS_ONLY", Bounds.ON);

  public LODCandidates() {
    this(new LODSetting("0 [face:on, wire:off, color:smooth]", FaceColor.ON, WireColor.OFF, ColorModel.SMOOTH),
        new LODSetting("1 [face:on, wire:off, color:flat]", FaceColor.ON, WireColor.OFF, ColorModel.FLAT),
        new LODSetting("2 [face:off, wire:varying, color:flat]", FaceColor.OFF, WireColor.VARYING, ColorModel.FLAT),
        BOUNDS_ONLY);
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
