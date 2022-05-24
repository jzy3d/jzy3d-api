package org.jzy3d.chart.controllers.mouse.camera.adaptive.handlers;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.AbstractAdativeRenderingHandler;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.AdaptiveRenderingHandler;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.Toggle;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot3d.primitives.Wireframeable;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import jgl.wt.awt.GL;

public class AdaptByDroppingSmoothColor extends AbstractAdativeRenderingHandler
    implements AdaptiveRenderingHandler {
  protected EmulGLPainter painter;
  protected EmulGLCanvas canvas;

  protected Quality currentQuality;

  public AdaptByDroppingSmoothColor(Chart chart) {
    super(chart);

    painter = (EmulGLPainter) chart.getPainter();
    canvas = (EmulGLCanvas) chart.getCanvas();
  }



  @Override
  public void apply() {
    // store current quality
    currentQuality = chart.getQuality();

    // edit quality to alternate setting
    Quality alternativeQuality = currentQuality.clone();
    alternativeQuality.setSmoothColor(false);

    // apply alternate settings
    chart.setQuality(alternativeQuality);
    
    applyShadeModelNow();
  }


  @Override
  public void revert() {
    chart.setQuality(currentQuality);
    
    applyShadeModelNow();
  }

  protected void applyShadeModelNow() {
    if(chart.getQuality().isSmoothColor())
      painter.glShadeModel_Smooth();
    else
      painter.glShadeModel_Flat();
  }


  @Override
  protected void applyOptimisation(Wireframeable w) {
    // nothing to do drawable-wise
  }
}
