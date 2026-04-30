package org.jzy3d.demos.surface;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import com.jogamp.opengl.awt.GLJPanel;

/**
 * Demo an Swing chart using JOGL {@link GLJPanel}.
 * 
 * @author martin
 */
public class SurfaceDemo_JOGL_Swing extends AbstractAnalysis {
  public SurfaceDemo_JOGL_Swing() {
    super(new SwingChartFactory());
  }

  public static void main(String[] args) throws Exception {
    SurfaceDemo_JOGL_Swing d = new SurfaceDemo_JOGL_Swing();
    AnalysisLauncher.open(d);
  }

  @Override
  public void init() {
    Shape surface = SampleGeom.surface();
    
    chart = new SwingChartFactory().newChart(Quality.Advanced());
    chart.add(surface);
  }
}
