package org.jzy3d.demos.surface;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.EmulGLSkin;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.rendering.canvas.Quality;


/**
 * Demo an AWT chart using jGL for CPU rendering (instead of GPU rendering).
 * 
 * @author martin
 *
 */
public class SurfaceDemoEmulGL {

  static final float ALPHA_FACTOR = 0.55f;// .61f;

  public static void main(String[] args) {
    EmulGLChartFactory factory = new EmulGLChartFactory();

    Quality q = Quality.Advanced(); 
    q.setAnimated(false); // leave CPU quiet if no need to re-render
    q.setHiDPIEnabled(true); // need java 9+ to enable HiDPI & Retina displays 
    // (tutorials built with Java 8 for backward compatibility, update your runtime to get HiDPI)
    
    Chart chart = factory.newChart(q);
    chart.add(SampleGeom.surface());
    
    EmulGLSkin skin = EmulGLSkin.on(chart);
    skin.getCanvas().setProfileDisplayMethod(true);

    chart.open();
    chart.addMouse();
  }
}
