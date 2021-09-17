package org.jzy3d.chart.controllers.mouse.camera.adaptive.handlers;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.AbstractAdativeRenderingHandler;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.AdaptiveRenderingHandler;
import org.jzy3d.plot3d.primitives.Wireframeable;
import org.jzy3d.plot3d.rendering.view.lod.LODPerf;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting;

public class AdaptByPerformanceKnowledge extends AbstractAdativeRenderingHandler implements AdaptiveRenderingHandler{
  LODPerf perf = new LODPerf();
  double maxRenderingTime = 100;
  
  LODSetting selectedLODSetting;
  
  public AdaptByPerformanceKnowledge(Chart chart) {
    super(chart);
  }
  
  protected void applyOptimisation(Wireframeable w) {
    if(perf!=null) {
      selectedLODSetting = perf.applyBestCandidateBelow(maxRenderingTime, w);
    }
    else {
      //System.err.println("MISSING PERF!");
    }
  }

  public LODPerf getPerf() {
    return perf;
  }

  public void setPerf(LODPerf perf) {
    this.perf = perf;
  }

  public double getMaxRenderingTime() {
    return maxRenderingTime;
  }

  public void setMaxRenderingTime(double maxRenderingTime) {
    this.maxRenderingTime = maxRenderingTime;
  }

  public LODSetting getSelectedLODSetting() {
    return selectedLODSetting;
  }
}
