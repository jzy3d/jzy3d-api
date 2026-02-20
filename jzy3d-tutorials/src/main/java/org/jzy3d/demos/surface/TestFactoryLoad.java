package org.jzy3d.demos.surface;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.maths.TicToc;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.slf4j.LoggerFactory;


/**
 * Demo an AWT chart using jGL for CPU rendering (instead of GPU rendering).
 * 
 * @author martin
 *
 */
public class TestFactoryLoad {

  static final float ALPHA_FACTOR = 0.55f;// .61f;

  public static void main(String[] args) {
    TicToc.tick();    
    
    LoggerFactory.getLogger(TestFactoryLoad.class);
    
    java.awt.Toolkit.getDefaultToolkit();
    TicToc.tockShow("Toolkit");

    
    TicToc.tick();
    EmulGLChartFactory factory = new EmulGLChartFactory();
    
    Quality q = Quality.Advanced(); 
    q.setAnimated(false); // leave CPU quiet if no need to re-render
    q.setHiDPIEnabled(true); // need java 9+ to enable HiDPI & Retina displays 

    Chart chart = factory.newChart(q);
    

    
    TicToc.tockShow("chart");
    
    System.exit(0);
  }
}
