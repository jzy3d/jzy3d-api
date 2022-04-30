package org.jzy3d.mocks.jgl;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.chart.factories.EmulGLPainterFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.Array;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import jgl.wt.awt.GL;

public class GLMock_DepthRange extends GL{
  List<double[]> verify_glDepthRange = new ArrayList<>();
  
  @Override
  public void glDepthRange(double near_val, double far_val) {
    super.glDepthRange(near_val, far_val);
    
    double[] args = {near_val, far_val};
    verify_glDepthRange.add(args);
    
    //Array.print("GLMock_DepthRange : ", args);
  }

  public List<double[]> verify_glDepthRange() {
    return verify_glDepthRange;
  }
  
  public void clear_glDepthRange() {
    verify_glDepthRange.clear();
  }
  
  public static EmulGLChartFactory inFactory(GLMock_DepthRange glMock) {

    // Given a surface chart with a mock GL injected for spying calls to glDepthRange
    EmulGLPainterFactory painterF = new EmulGLPainterFactory() {
      protected EmulGLCanvas newEmulGLCanvas(IChartFactory factory, Scene scene, Quality quality) {
        EmulGLCanvas c = new EmulGLCanvas(factory, scene, quality);
        c.setGL(glMock);
        return c;
      }
    };
    
    EmulGLChartFactory factory = new EmulGLChartFactory(painterF);
    Chart chart = factory.newChart(Quality.Advanced());
    EmulGLPainter painter = (EmulGLPainter)chart.getPainter();
    painter.setGL(glMock); // << spy
    
    return factory;
  }
  
}
