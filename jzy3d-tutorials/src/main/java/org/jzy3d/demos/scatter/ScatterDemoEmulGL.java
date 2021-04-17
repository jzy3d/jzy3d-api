package org.jzy3d.demos.scatter;

import java.util.Random;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class ScatterDemoEmulGL {
  public static void main(String[] args) throws Exception {
    Quality q = Quality.Advanced;
    q.setAnimated(false);
    q.setPreserveViewportSize(false); // need java 9+ to enable HiDPI & Retina displays

    Chart chart = new EmulGLChartFactory().newChart(q);
    chart.getScene().add(scatter());
    chart.open();
    chart.addMouseCameraController();
    
    ((EmulGLCanvas) chart.getCanvas()).setProfileDisplayMethod(true); // to print frame rate
  }

  private static Scatter scatter() {
    int size = 50000;
    float x;
    float y;
    float z;
    float a;

    Coord3d[] points = new Coord3d[size];
    Color[] colors = new Color[size];

    Random r = new Random();
    r.setSeed(0);

    for (int i = 0; i < size; i++) {
      x = r.nextFloat() - 0.5f;
      y = r.nextFloat() - 0.5f;
      z = r.nextFloat() - 0.5f;
      points[i] = new Coord3d(x, y, z);
      a = 0.75f;
      colors[i] = new Color(x, y, z, a);
    }

    Scatter scatter = new Scatter(points, colors);
    scatter.setWidth(3);
    return scatter;
  }
}
