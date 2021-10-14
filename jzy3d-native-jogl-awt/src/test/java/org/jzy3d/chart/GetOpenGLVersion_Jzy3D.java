package org.jzy3d.chart;

import org.junit.Test;
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
  @Test
  public void version() {
    System.out.println("Profile : GL2    : " + GLProfile.isAvailable(GLProfile.GL2));
    System.out.println("Profile : GL2GL3 : " + GLProfile.isAvailable(GLProfile.GL2GL3));
    System.out.println("Profile : GL3    : " + GLProfile.isAvailable(GLProfile.GL3));
    System.out.println("Profile : GL3bc  : " + GLProfile.isAvailable(GLProfile.GL3bc));
    System.out.println("Profile : GL4    : " + GLProfile.isAvailable(GLProfile.GL4));
    System.out.println("Profile : GL4ES3 : " + GLProfile.isAvailable(GLProfile.GL4ES3));
    System.out.println("Profile : GL4bc  : " + GLProfile.isAvailable(GLProfile.GL4bc));

    
    IPainterFactory p = new AWTPainterFactory();
    p.setOffscreen(500, 500);

    Chart chart = new AWTChartFactory(p).newChart();
    
    //chart.open();
    
    // The debug info will show open GL version.
    // It will only follow the profile capabilities if the VM is
    // started with -Djogl.disable.openglcore=true
    System.out.println("NB : Properly retrieving the profile capabilities requires -Djogl.disable.openglcore=true");
    System.out.println(chart.getCanvas().getDebugInfo());

  }

}
