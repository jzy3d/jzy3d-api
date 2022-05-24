package org.jzy3d.demos.teapot;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.Teapot;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;

public class TeapotDemo {

  public static void main(String[] args) {
    
    Teapot teapot = new Teapot();
    teapot.setFaceDisplayed(true);
    teapot.setColor(Color.WHITE);
    teapot.setWireframeColor(Color.CYAN);
    teapot.setWireframeDisplayed(false);
    teapot.setWireframeWidth(2);
    teapot.setReflectLight(true);
    
    
    
    // ---------------------------------------------
    ChartFactory factory = new AWTChartFactory();
    //ChartFactory factory = new EmulGLChartFactory();
    
    // Emulgl will show limitations
    // 1-wireframe and face do not mix cleanly (polygon offset fill)
    // 2-wireframe color tend to saturate (here in green)
    
    Quality q = Quality.Advanced(); 
    q.setDepthActivated(true);
    q.setAlphaActivated(false);
    q.setAnimated(false); 
    q.setHiDPIEnabled(true); 
    
    Chart chart = factory.newChart(q);
    chart.getView().setSquared(false);
    chart.getView().setBackgroundColor(Color.BLACK);
    chart.getView().getAxis().getLayout().setMainColor(Color.WHITE);
    // ---------------------------------------------

    chart.add(teapot);
    
    //Light light = chart.addLightOnCamera();
    Light light = chart.addLight(chart.getView().getBounds().getCorners().getXmaxYmaxZmax());
    //light.setRepresentationDisplayed(true);
    
    // ---------------------------------------------

    chart.open();
    chart.addMouseCameraController();

  }
}
