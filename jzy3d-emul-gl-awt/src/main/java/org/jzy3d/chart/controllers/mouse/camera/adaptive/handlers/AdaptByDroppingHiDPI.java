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

public class AdaptByDroppingHiDPI extends AbstractAdativeRenderingHandler implements AdaptiveRenderingHandler{
  protected EmulGLPainter painter;
  protected EmulGLCanvas canvas;

  protected Quality currentQuality;
  protected boolean currentHiDPI;
  protected GL gl;

  public AdaptByDroppingHiDPI(Chart chart) {
    super(chart);
    
    painter = (EmulGLPainter) chart.getPainter();
    canvas = (EmulGLCanvas)chart.getCanvas();
    gl =painter.getGL();

  }
  

  
  @Override
  public void apply() {
    // store current quality
    currentQuality = chart.getQuality();
    currentHiDPI = painter.getGL().isAutoAdaptToHiDPI();

    // edit quality to alternate setting
    Quality alternativeQuality = currentQuality.clone();
    alternativeQuality.setHiDPIEnabled(false);

    // apply alternate settings
    chart.setQuality(alternativeQuality);
    gl.setAutoAdaptToHiDPI(false);
  }


  @Override
  public void revert() {
    chart.setQuality(currentQuality);
    gl.setAutoAdaptToHiDPI(currentHiDPI);
    // this force the GL image to apply the new HiDPI setting immediatly
    gl.updatePixelScale(canvas.getGraphics());
    gl.applyViewport();
  }
  
  
  @Override
  protected void applyOptimisation(Wireframeable w) {
    // nothing to do drawable-wise
  }
}
