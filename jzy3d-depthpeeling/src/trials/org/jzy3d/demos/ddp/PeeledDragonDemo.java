package org.jzy3d.demos.ddp;

import java.io.File;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.debugGL.tracers.DebugGLChart3d;
import org.jzy3d.events.IViewPointChangedListener;
import org.jzy3d.events.ViewPointChangedEvent;
import org.jzy3d.factories.DepthPeelingChartFactory;
import org.jzy3d.io.obj.OBJFileLoader;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.scene.Graph.GraphListener;

/**
 * Download https://download.jzy3d.org/objfiles/dragon.obj to src/library/models/dragon.obj if not yet available.
 * @author martin
 *
 */
public class PeeledDragonDemo {
  public static void main(String[] args) {
    System.err.println("May require vm argument -Xmx1024m");

    DepthPeelingChartFactory f = new DepthPeelingChartFactory(PeelingMethod.WEIGHTED_SUM_MODE);
    Chart chart = f.newChart();

    String objFilePath = "src/library/models/dragon.obj";
    File file = new File("./" + objFilePath);
    
    OBJFileLoader loader = new OBJFileLoader(file);
    DrawableVBO vbo = new DrawableVBO(loader);
    vbo.setColor(new Color(0f, 0f, 1f, 1f));
    
    chart.getScene().add(vbo);

    //chart.addLightOnCamera();
    //chart.addLight(new Coord3d(-0.01,0.2,0.04));
    
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
