package org.jzy3d.demos.teapot;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Transform;

public class TeapotDemo {

  public static void main(String[] args) {
    
    Transform transform = new Transform();
    transform.add(new Rotate(90, new Coord3d(1,0,0)));
    
    Teapot teapot = new Teapot();
    teapot.setTransformBefore(transform);
    
    teapot.setFaceDisplayed(true);
    teapot.setColor(new Color(0.3f, 0.3f, 0.9f));
    
    teapot.setWireframeColor(Color.BLACK);
    teapot.setWireframeDisplayed(true);
    teapot.setWireframeWidth(3);
    
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

    
    // ---------------------------------------------

    chart.add(teapot);
    
    Light light = chart.addLightOnCamera();
    
    
    
    // ---------------------------------------------

    chart.open();
    chart.addMouseCameraController();

  }
}
