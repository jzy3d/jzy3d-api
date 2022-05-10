package org.jzy3d.performance.polygons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.io.xls.ExcelBuilder;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.IntegerTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.View2DLayout;

/*
 * // PB1 : avec 200 ou 500 samples, view2D calcule une camera qui n'affiche rien
 * 
 * 
 * DONE : changement 2D/3D applique la modif immédiatement, même si animation stop
 * 
 * ------------------------
 * DONE : axe en double sur emulGL, ajoute un fix sur le delete texte (should delete image also if ledend)
 * 
 * ------------------------
 * DONE : configurer la vue avec des paramètres 2D
 * 
 * TODO : considérer la taille des textes.
 * 
 * 
 * ------------------------
 * PB5 : native prend en compte la colorbar sur le côté, mais pas emulgl qui a un viewport qui s'étale sur toute la longueur.
 * 
 * >> soit on évite d'appliquer le stretch qui force la vue 2D à étaler jusqu'au bord de l'écran
 * >> soit on adapte NativeGL pour que la colorbar s'affiche au dessus comme EmulGL
 * >> soit on adapte EmulGL pour pouvoir composer le viewport avec la colorbar sur le côté, en s'appuyant sur la formule PIX x (bounds/canvas)
 *    c'est pratique de pouvoir stretch la 3D sans réfléchir à la position de la colorbar et avoir garantie de non débordement.
 * >> soit on adapte EmuLGL pour prendre des bounds plus grand qui vont permettre de créer un décallage à droite
 * 
 *
 */
public class BenchmarkPlot implements BenchmarkXLS {

  public static void main(String[] args) throws IOException, InterruptedException {

    // -------------------------------
    // Chart configuration for plotting
    // ChartFactory f = new AWTChartFactory() ;
    ChartFactory f = new EmulGLChartFactory();
    Quality q = Quality.Advanced();
    // q.setHiDPI(HiDPI.OFF);


    // -------------------------------
    // Collect benchmark data

    int stepMin = 2; // min number of steps for surface
    int stepMax = 100; // max number of steps for surface
    String info = "emulgl-hidpi"; // HiDPI setting during benchmark
    // String info = "native"; // HiDPI setting during benchmark

    int timeMax = 250;


    String file = BenchmarkUtils.getExcelFilename(outputFolder, stepMin, stepMax, info);
    ExcelBuilder xls = new ExcelBuilder(file);
    xls.setCurrentSheet(SHEET_BENCHMARK);

    int line = 0;


    List<Coord3d> data = new ArrayList<>();

    Cell cellTime = xls.getCell(line, TIME);
    Cell cellPoly = xls.getCell(line, POLYGONS);

    double maxX = 0;

    while (cellTime != null && cellPoly != null) {
      double x = cellPoly.getNumericCellValue();
      double y = cellTime.getNumericCellValue();

      if (x > maxX)
        maxX = x;

      data.add(new Coord3d(x, y, 0));

      cellTime = xls.getCell(line, TIME);
      cellPoly = xls.getCell(line, POLYGONS);

      line++;

      // System.out.println("x " + x + " y " + y +" max " + maxX );
      // System.out.println("c");
    }


    System.out.println("pouet");
    System.out.println("loaded " + line + " XLS lines");


    // -------------------------------
    // Plot benchmark data

    Scatter scatter = new Scatter(data, Color.BLUE, 2);

    Chart c = f.newChart(q);


    // Thread.sleep(1000);



    c.add(scatter);
    c.add(line(40, maxX, Color.GREEN, 2));
    c.add(line(60, maxX, Color.ORANGE, 2));



    c.add(line(80, maxX, Color.RED, 2));
    c.getAxisLayout()
        .setXAxisLabel("Number of polygons (polygons all together cover the same surface)");
    c.getAxisLayout().setYAxisLabel("Rendering time (ms)");
    c.getAxisLayout().setYAxisLabelOrientation(LabelOrientation.PARALLEL_TO_AXIS);
    c.getAxisLayout().setFont(Font.Helvetica_18);
    c.getAxisLayout().setXTickRenderer(new IntegerTickRenderer(true));

    c.getView().setBoundManual(new BoundingBox3d(0, (float) maxX, 0, timeMax, -10, 10));


    c.open(file, 1024, 768);

    // PB : MUST BE DONE BEFORE OPENING chart
    c.view2d();
    
    View2DLayout layout = c.getView().getLayout_2D();
    layout.setMarginHorizontal(20);
    layout.setMarginVertical(20);
    
    //layout.setMarginRight(30);
    //layout.setMarginTop(60);
    //layout.setKeepTextVisible(false);

    c.render();

    c.addMouse();

    // DebugGLChart3d debugChart = new DebugGLChart3d(c, new AWTChartFactory());
    // debugChart.open(new Rectangle(0, 0, 300, 300));

  }

  private static LineStrip line(int ms, double maxX, Color lineColor, int width) {
    LineStrip line = new LineStrip(lineColor, new Coord3d(0, ms, 0), new Coord3d(maxX, ms, 0));
    line.setWireframeWidth(width);
    return line;
  }
}
