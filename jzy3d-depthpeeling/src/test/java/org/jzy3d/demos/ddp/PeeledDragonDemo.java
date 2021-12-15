package org.jzy3d.demos.ddp;

import java.io.File;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.debugGL.tracers.DebugGLChart3d;
import org.jzy3d.factories.DepthPeelingPainterFactory;
import org.jzy3d.io.obj.OBJFileLoader;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.plot3d.rendering.lights.Light;

/**
 * Download https://download.jzy3d.org/objfiles/dragon.obj to src/library/models/dragon.obj if not yet available.
 * 
 * Light & shadow not working here. See ObjFileVBODemo in the developer guide.
 * 
 * 
 * @author martin
 *
 */
public class PeeledDragonDemo {
  public static void main(String[] args) {
    AWTChartFactory f = new AWTChartFactory(new DepthPeelingPainterFactory());

    Chart chart = f.newChart();

    String objFilePath = "data/models/dragon.obj";
    File file = new File("./" + objFilePath);
    
    OBJFileLoader loader = new OBJFileLoader(file);
    DrawableVBO vbo = new DrawableVBO(loader);
    vbo.setColor(new Color(0f, 0f, 1f, 1f));
    
    chart.getScene().add(vbo);
    
    Light light = chart.addLight(new Coord3d(-0.2, 0.2, 0.3), Light.DEFAULT_COLOR,
        Light.DEFAULT_COLOR, Light.DEFAULT_COLOR, 0.01f);

    
    chart.addLightPairOnCamera();
    chart.open(600, 600);
    chart.getMouse();
    
    /*chart.getScene().getGraph().addGraphListener(new GraphListener() {
      
      @Override
      public void onMountAll() {
        System.out.println(vbo.getBounds());
      }
    });
    
    chart.getView().addViewPointChangedListener(new IViewPointChangedListener() {
      
      @Override
      public void viewPointChanged(ViewPointChangedEvent e) {
        for(Light light : chart.getScene().getLightSet().getLights()) {
          System.out.println(light.getId() + " " + light.getPosition());
        }
      }
    });*/
    
    
    DebugGLChart3d debugChart = new DebugGLChart3d(chart, new AWTChartFactory());
    debugChart.open(new Rectangle(600, 0, 600, 600));

  }


}
