package org.jzy3d.demos.ddp;

import java.io.File;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.factories.DepthPeelingChartFactory;
import org.jzy3d.io.obj.OBJFileLoader;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ddp.DepthPeelingChart;
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

    String objFilePath = "data/dragon.obj";
    File file = new File("./" + objFilePath);
    
    OBJFileLoader loader = new OBJFileLoader(file);
    
    
    chart.getScene().add(new DrawableVBO(loader));
    //chart.getScene().add(new DrawableOBJFile("data/bun_zipper.ply"));

    // createStack(chart, 0.01f, 0.01f, Coord3d.ORIGIN, Color.BLUE /*no alpha*/, Color.BLACK);

    
    ChartLauncher.openChart(chart, new Rectangle(0, 0, 600, 600));
  }


}
