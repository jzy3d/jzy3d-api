package org.jzy3d.demos.teapot;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Transform;

public class TeapotDemoEmulGL {

  public static void main(String[] args) {
    
    Transform transform = new Transform();
    transform.add(new Rotate(90, new Coord3d(1,0,0)));
    
    Teapot teapot = new Teapot();
    teapot.setTransformBefore(transform);
    
    teapot.setFaceDisplayed(true);
    teapot.setColor(Color.ORANGE);
    
    teapot.setWireframeColor(Color.BLACK);
    teapot.setWireframeDisplayed(true);
    teapot.setWireframeWidth(3);
    
    
    // ---------------------------------------------
    ChartFactory factory = new AWTChartFactory();
    //ChartFactory factory = new EmulGLChartFactory();
    
    // Emulgl will show limitations
    // 1-wireframe and face do not mix cleanly (polygon offset fill)
    // 2-wireframe color tend to saturate (here in green)
    
    Quality q = Quality.Intermediate(); 
    q.setDepthActivated(true);
    q.setAlphaActivated(false);
    q.setAnimated(false); 
    q.setHiDPIEnabled(true); 
    
    Chart chart = factory.newChart(q);
    
    chart.add(teapot);
    chart.addLightOnCamera();

    chart.getView().setSquared(false);
    
    chart.open();
    chart.addMouseCameraController();

  }
}
