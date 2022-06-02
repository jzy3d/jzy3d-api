package org.jzy3d.demos.chart2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.plot2d.primitives.LineSerie2d;
import org.jzy3d.plot3d.rendering.legends.overlay.Legend;
import org.jzy3d.plot3d.rendering.legends.overlay.LineLegendLayout;
import org.jzy3d.plot3d.rendering.legends.overlay.OverlayLegendRenderer;

public class Line2D_DemoAWT {

  public static void main(String[] args) {
    AWTChartFactory f = new AWTChartFactory();
    AWTChart chart = (AWTChart) f.newChart();

    // Add 2D series
    LineSerie2d line1 = new LineSerie2d("Line 1");
    LineSerie2d line2 = new LineSerie2d("Line 2");
    LineSerie2d line3 = new LineSerie2d("Line 3 with a longer name");

    line1.setColor(Color.RED);
    line2.setColor(Color.BLUE);
    line3.setColor(Color.GREEN);

    Random rng = new Random();
    rng.setSeed(0);

    for (int i = 0; i < 30; i++) {
      line1.add(i, rng.nextDouble());
      line2.add(i, rng.nextDouble());
      line3.add(i, rng.nextDouble());
    }

    chart.add(line1);
    chart.add(line2);
    chart.add(line3);


    // Legend
    List<Legend> infos = new ArrayList<Legend>();
    infos.add(new Legend(line1.getName(), line1.getColor()));
    infos.add(new Legend(line2.getName(), line2.getColor()));
    infos.add(new Legend(line3.getName(), line3.getColor()));

    OverlayLegendRenderer legend = new OverlayLegendRenderer(infos);
    LineLegendLayout layout = legend.getLayout();
    
    layout.getMargin().setWidth(10);
    layout.getMargin().setHeight(10);
    layout.setBackgroundColor(Color.WHITE);
    layout.setFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 11));
    
    chart.addRenderer(legend);
    
    // Open as 2D chart
    chart.view2d();
    chart.open();
  }

}
