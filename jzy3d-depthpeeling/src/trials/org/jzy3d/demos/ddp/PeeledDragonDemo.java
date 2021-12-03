package org.jzy3d.demos.ddp;

import java.io.File;
import org.jzy3d.chart.Chart;
import org.jzy3d.factories.DepthPeelingChartFactory;
import org.jzy3d.io.obj.OBJFileLoader;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;

/**
 * Download https://download.jzy3d.org/objfiles/dragon.obj to data/dragon.obj
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
    
    chart.getScene().add(vbo);

    chart.addLightOnCamera();
    chart.open(600, 600);
    chart.getMouse();
  }


}
