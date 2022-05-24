package org.jzy3d.demos.debugGL;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.AWTPainterFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

/**
 * This shows how to switch OpenGL version with JOGL.
 * 
 * It requires to invoke the JVM with -Djogl.disable.openglcore=true to work.
 * 
 * @author martin
 */
public class GetOpenGLVersion_Jzy3D {
  public static void main(String[] args) {
    //GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
    //GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    //GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL3bc));
    //GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL4));
    GLCapabilities caps = new GLCapabilities(GLProfile.getMaximum(true));
    
    IPainterFactory p = new AWTPainterFactory(caps);
    Chart chart = new AWTChartFactory(p).newChart();
    
    chart.open();
    
    System.out.println("GL2    : " + GLProfile.isAvailable(GLProfile.GL2));
    System.out.println("GL2GL3 : " + GLProfile.isAvailable(GLProfile.GL2GL3));
    System.out.println("GL3    : " + GLProfile.isAvailable(GLProfile.GL3));
    System.out.println("GL3bc  : " + GLProfile.isAvailable(GLProfile.GL3bc));
    System.out.println("GL4    : " + GLProfile.isAvailable(GLProfile.GL4));
    System.out.println("GL4ES3 : " + GLProfile.isAvailable(GLProfile.GL4ES3));
    System.out.println("GL4bc  : " + GLProfile.isAvailable(GLProfile.GL4bc));
    
    // The debug info will show open GL version.
    // It will only follow the profile capabilities if the VM is
    // started with -Djogl.disable.openglcore=true
    System.out.println(chart.getCanvas().getDebugInfo());

  }

}
