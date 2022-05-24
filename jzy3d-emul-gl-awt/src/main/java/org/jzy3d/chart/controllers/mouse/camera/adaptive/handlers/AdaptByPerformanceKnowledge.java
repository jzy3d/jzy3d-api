package org.jzy3d.chart.controllers.mouse.camera.adaptive.handlers;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.AbstractAdativeRenderingHandler;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.AdaptiveRenderingHandler;
import org.jzy3d.painters.ColorModel;
import org.jzy3d.plot3d.primitives.Wireframeable;
import org.jzy3d.plot3d.rendering.view.lod.LODPerf;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting;

/**
 * Apply object-wise drawing settings, and chart-wise drawing settings such as {@link ColorModel}.
 * 
 * @author martin
 *
 */
public class AdaptByPerformanceKnowledge extends AbstractAdativeRenderingHandler
    implements AdaptiveRenderingHandler {
  LODPerf perf = new LODPerf();
  double maxRenderingTime = 100;

  LODSetting selectedLODSetting;

  ColorModel previousColorModel = null;

  public AdaptByPerformanceKnowledge(Chart chart) {
    super(chart);
  }

  /** Apply a drawing setting per object */
  protected void applyOptimisation(Wireframeable w) {
    if (perf != null) {
      selectedLODSetting = perf.applyBestCandidateBelow(maxRenderingTime, w);
    }
  }

  /** Invoke all setting edition per object and then globally edit the chart. */
  @Override
  public void apply() {
    super.apply();

    if (selectedLODSetting != null) {
      ColorModel model = selectedLODSetting.getColorModel();

      if (model != null && !model.equals(chart.getQuality().getColorModel())) {
        previousColorModel = chart.getQuality().getColorModel();
        chart.getQuality().setColorModel(model);
      }
    }
  }

  @Override
  public void revert() {
    super.revert();

    if (previousColorModel != null) {
      chart.getQuality().setColorModel(previousColorModel);
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
