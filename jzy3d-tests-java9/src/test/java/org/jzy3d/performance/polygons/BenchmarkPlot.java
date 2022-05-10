package org.jzy3d.performance.polygons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart2d.View2d;
import org.jzy3d.colors.Color;
import org.jzy3d.io.xls.ExcelBuilder;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.IntegerTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import org.jzy3d.plot3d.rendering.view.ViewportMode;

/*
 * // PB1 : avec 200 ou 500 samples, view2D calcule une camera qui n'affiche rien
 * 
 * 
 * PB2 : si view2D est lancé APRES open, le layout est de l'axe est merdique. (axe label en haut et
 * à droite, il semble qu'on regarde par en dessous). Se remet dans le bon sens au prochain render.
 * Semble du au fait que le TOP vector n'est pas mis à jour du premier coup, seulement au render
 * d'apres.
 * 
 *
 *
 * ------------------------
 * DONE : axe en double sur emulGL, ajoute un fix sur le delete texte (should delete image also if ledend)
 * 
 * ------------------------
 * PB4 : la fonction View.correct est très radicale. Si on la bypass, le chart est bien positionné
 * avec margin d'axe à 0. Pouvoir configurer le correctCameraPosition pour inclure texte mais avec
 * une marge
 * 
 * Le radius de la rendering sphere est très utilié pour appeler ORTHO2D
 * 
 * glOrtho(-radius, +radius, -radius, +radius, near, far);
 * 
 * computeCameraRenderingSphereRadius doit donc s'adapter pour ne plus configurer la Camera par
 * rapport à un radius impréci, mais plutôt configurer avec précision glOrtho en fonction 
 * - des bounds des données
 * - d'une marge de blanc exprimée en % du panneau complet. Pouvoir exprimer en pixel et calculer le ratio dynamiquement
 * - d'une taille de texte qu'il faut pouvoir prendre en compte à la demande.
 * 
 * Pouvoir configurer en 2D l'espacement du label de texte 
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
    int stepMax = 500; // max number of steps for surface
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
