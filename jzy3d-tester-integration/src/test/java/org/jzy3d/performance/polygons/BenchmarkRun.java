package org.jzy3d.performance.polygons;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;
import java.util.Set;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.EmulGLSkin;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.io.xls.ExcelBuilder;
import org.jzy3d.io.xls.ExcelBuilder.Type;
import org.jzy3d.maths.Range;
import org.jzy3d.maths.Statistics;
import org.jzy3d.maths.TicToc;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.HiDPI;

public class BenchmarkRun implements BenchmarkXLS {

  public static void main(String[] args) throws IOException, InterruptedException {
    int width = 1024;
    int height = 768;
    HiDPI hidpi = HiDPI.ON;
    boolean alphaEnabled = false;
    String info = "native"; // for building file name

    boolean offscreen = false;
    int stepMax = 250; // max number of steps for surface
    int stepMin = 2; // min number of steps for surface
    int shoots = 30; // number of trials

    boolean saveImages = false;


    ExcelBuilder xls = new ExcelBuilder(Type.XLSX, SHEET_CONFIG);

    RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

    Map<String, String> systemProperties = runtimeBean.getSystemProperties();
    Set<String> keys = systemProperties.keySet();

    int line = 0;
    for (String key : keys) {
      String value = systemProperties.get(key);

      xls.setCell(line, 0, key);
      xls.setCell(line, 1, value);
      line++;
    }



    // --------------------------------------------------------
    // Configure base quality for standard case

    // ChartFactory factory = new EmulGLChartFactory();
    ChartFactory factory = new AWTChartFactory();

    if (offscreen)
      factory.getPainterFactory().setOffscreen(width, height);

    Quality q = Quality.Advanced();
    q.setAnimated(factory instanceof AWTChartFactory); // RESOLVE ME : CHART DOES NOT SHOW A NON
                                                       // ANIMATED AWT
    q.setAlphaActivated(alphaEnabled);
    q.setHiDPI(hidpi);

    Chart chart = factory.newChart(q);
    if (!offscreen)
      chart.open(width, height);

    // EmulGLSkin skin = EmulGLSkin.on(chart);
    // chart.addMouseCameraController();


    Shape surface = null;

    xls.setCurrentSheet(xls.newSheet(SHEET_BENCHMARK));


    line = 0;


    for (int i = stepMax; i >= stepMin; i--) {


      if (surface != null) {
        chart.remove(surface);
        chart.render();
      }

      surface = surface(i);
      chart.add(surface);

      int polygonNumber = surface.size();


      int minPoly = Integer.MAX_VALUE;
      int maxPoly = 0;

      // Measure perf
      double[] renderingTime = new double[shoots];

      TicToc t = new TicToc();

      for (int s = 0; s < shoots; s++) {

        t.tic();
        chart.render();
        t.toc();

        renderingTime[s] = t.elapsedMilisecond();

        if (renderingTime[s] < 40) {
          Thread.sleep(50);
        }

        xls.setCell(line, TIME, renderingTime[s]);
        xls.setCell(line, POLYGONS, polygonNumber);
        xls.setCell(line, WIDTH, width);
        xls.setCell(line, HEIGHT, height);
        line++;

        if (polygonNumber > maxPoly) {
          maxPoly = polygonNumber;
        }
        if (polygonNumber < minPoly) {
          minPoly = polygonNumber;
        }
      }


      // System.out.println("Median perf for " + n + " polygons : "
      // + Statistics.median(perf, false) + " after " + shoots + " shoots");
      // Array.print(perf);

      System.out.println(polygonNumber + ", " + (width * height) + ", "
          + Statistics.median(renderingTime, false) + ", " + shoots);

      if (saveImages)
        chart.screenshot(new File("target/bench-" + polygonNumber + ".png"));

    }

    // finish
    String file = BenchmarkUtils.getExcelFilename(outputFolder, stepMin, stepMax, info);
    xls.save(file);

    System.out.println("Wrote " + file + " with " + line + " lines");
  }




  private static Shape surface(int steps) {
    SurfaceBuilder builder = new SurfaceBuilder();

    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return 0;// x * Math.sin(x * y);
      }
    };

    Range range = new Range(-3, 3);

    Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps), mapper);

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface);
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(false);
    surface.setWireframeColor(Color.BLACK);
    surface.setWireframeWidth(1);
    return surface;
  }
}
