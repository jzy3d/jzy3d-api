package org.jzy3d.emulgl.trials;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.EmulGLSkin;
import org.jzy3d.chart.controllers.RateLimiterAdaptsToRenderTime;
import org.jzy3d.chart.controllers.mouse.camera.AdaptiveRenderingPolicy;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot3d.primitives.Geometry;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.RandomGeom;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;
import jgl.GL;
import jgl.Util;
import jgl.context.gl_context;

public class DemoPolygonsWithWireframe {

  public static void main(String[] args) {
    Geometry.SHOW_NORMALS = true;
    
    RandomGeom geom = new RandomGeom();
    
    Quality q = Quality.Advanced();
    q.setAlphaActivated(false);
    
    ChartFactory f = new EmulGLChartFactory();
    Chart chart = f.newChart(q);

    List<Polygon> polys = new ArrayList<>();

    Color color = Color.ORANGE;
    boolean wireframe = true;
    Color wireColor = Color.BLUE;
    
    Polygon p;

    p = geom.poly(0, 1, 0, 10, 10, true, color);
    p.setWireframeDisplayed(wireframe);
    p.setWireframeColor(wireColor);
    polys.add(p);

    p = geom.poly(0, 1, 1, 10, 10, true, color);
    p.setWireframeDisplayed(wireframe);
    p.setWireframeColor(wireColor);
    polys.add(p);
    
    
    
    for(Polygon poly: polys) {
      poly.setReflectLight(true);
    }
    
    chart.add(polys);
    
    Light[] lights = null;
      lights = chart.addLightPairOnCamera(Color.GRAY);
      
      /*Attenuation attenuation = new Attenuation(1f,0,0);
      lights[0].setAttenuation(attenuation);
      lights[1].setAttenuation(attenuation);*/

      //lights[0].setType(Light.Type.DIRECTIONAL);
      //lights[1].setType(Light.Type.DIRECTIONAL);
    
    chart.getView().setAxisDisplayed(true);
    chart.getView().setSquared(false);
    chart.open("Wireframe debug | " + f.getClass().getSimpleName());
    chart.addMouseCameraController();

    // if(reflectLight)
    // SwingChartLauncher.openLightEditors(chart);

    // ----------------------------------
    // Perf

    if (f instanceof EmulGLChartFactory) {
      AdaptiveRenderingPolicy policy = new AdaptiveRenderingPolicy();
      policy.renderingRateLimiter = new RateLimiterAdaptsToRenderTime();
      policy.optimizeForRenderingTimeLargerThan = 40;// ms
      policy.optimizeByDroppingFaceAndKeepingWireframe = false;
      policy.optimizeByDroppingFaceAndKeepingWireframeWithColor = true;

      EmulGLSkin skin = EmulGLSkin.on(chart);
      skin.getMouse().setPolicy(policy);
      skin.getCanvas().setProfileDisplayMethod(true);
    }
    
    
    GL gl = ((EmulGLPainter)chart.getPainter()).getGL();
    gl_context context = gl.getContext();
    BufferedImage im = Util.debugDepthBufferToBufferedImage(gl, "target/depth.png");
    
    

    
    //Image jGLColorBuffer = canvas.createImage(producer);
    

    // ----------------------------------
    // Debug

    /*if(openDebugGL) {
      
      DebugGLChart3d debugChart = new DebugGLChart3d(chart, new AWTChartFactory());
      debugChart.open(new Rectangle(600, 0, 600, 600));
  
      DebugGLChart2d debugChart2d = new DebugGLChart2d(chart);
      
      if(lights!=null) {
        debugChart2d.watch("viewpoint.x", Color.GREEN, c -> c.getView().getViewPoint().y);
        debugChart2d.watch("light1.elev", Color.RED, c -> c.lightPointUpPolar.y);
        debugChart2d.watch("light2.elev", Color.BLUE, c -> c.lightPointDownPolar.y);
        
      }
      
      debugChart2d.open(new Rectangle(450, 450, 300, 300));
    
    }*/
  }

}
