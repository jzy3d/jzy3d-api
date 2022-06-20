package org.jzy3d.tests.integration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import org.junit.Test;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.plot2d.primitives.LineSerie2d;
import org.jzy3d.plot3d.rendering.legends.overlay.Legend;
import org.jzy3d.plot3d.rendering.legends.overlay.LegendLayout.Corner;
import org.jzy3d.plot3d.rendering.legends.overlay.OverlayLegendRenderer;
import org.jzy3d.plot3d.rendering.view.AWTImageRenderer;
import org.jzy3d.plot3d.rendering.view.HiDPI;

public class ITTest_Overlay extends ITTest {
  public static void main(String[] args) {
    open(new ITTest_Overlay().whenOverlayAreMoved(WT.Native_Swing, HiDPI.ON));
  }

  protected static String caseOverlayMove = "OverlayMove";
  
  //protected static Corner[][] corners =
  //    {{Corner.TOP_RIGHT, Corner.BOTTOM_RIGHT}};

  protected static Corner[][] corners =
    {{Corner.TOP_LEFT, Corner.TOP_RIGHT}, {Corner.TOP_RIGHT, Corner.BOTTOM_RIGHT},
        {Corner.BOTTOM_RIGHT, Corner.BOTTOM_LEFT}, {Corner.BOTTOM_LEFT, Corner.TOP_LEFT}};

  @Test
  public void whenOverlayAreMoved() {
    System.out.println("ITTest : " + caseOverlayMove);

    forEach((toolkit, resolution) -> whenOverlayAreMoved(toolkit, resolution));

  }
  
  public Chart whenOverlayAreMoved(WT wt, HiDPI hidpi) {

    // Given a chart with content
    AWTChart chart = (AWTChart) chart(wt, hidpi);

    LineSerie2d line1 = new LineSerie2d("Line 1");
    LineSerie2d line2 = new LineSerie2d("Line 2");
    LineSerie2d line3 = new LineSerie2d("Line 3 with a longer name");

    line1.setColor(Color.RED);
    line2.setColor(Color.BLUE);
    line3.setColor(Color.GREEN);

    line1.setWidth(3);

    Random rng = new Random();
    rng.setSeed(0);

    for (int i = 0; i < 50; i++) {
      line1.add(i, rng.nextDouble());
      line2.add(i, rng.nextDouble());
      line3.add(i, rng.nextDouble());
    }

    chart.add(line1);
    chart.add(line2);
    chart.add(line3);


    // Given a first legend
    List<Legend> infos = new ArrayList<Legend>();
    infos.add(new Legend(line1.getName(), line1.getColor()));
    infos.add(new Legend(line2.getName(), Color.BLUE));
    infos.add(new Legend(line3.getName(), Color.GREEN));

    OverlayLegendRenderer legend = new OverlayLegendRenderer(infos);
    legend.getLayout().getMargin().setWidth(10);
    legend.getLayout().getMargin().setHeight(10);
    legend.getLayout().setBackgroundColor(Color.WHITE);
    legend.getLayout().setFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 11));
    chart.addRenderer(legend);
    
    // Given a second legend
    BufferedImage image = null;
    try {
      image = ImageIO.read(new File("src/test/resources/icons/martin.jpeg"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    AWTImageRenderer renderer = new AWTImageRenderer(image);
    
    chart.addRenderer(renderer);
    

    chart.view2d();

    // -------------------------------------
    // Evaluate each legend configuration
    
    forEach((cornerSet)->{

      // When
      legend.getLayout().setCorner(cornerSet[0]);
      renderer.getLayout().setCorner(cornerSet[1]);
      
      //chart.render();

      // Then
      assertChart(chart, name(this, wt, chart.getQuality().getHiDPI(), properties(cornerSet)));
      
    });
    

    return chart;
  }
  
  public static String properties(Corner[] cornerSet) {
    return "Corner1" + KV + cornerSet[0] + SEP_PROP + "Corner2" + KV + cornerSet[1];
  }


  public static interface ITTest_Overlay_Instance {
    public void run(Corner[] corners);
  }

  /** Iterate over all combination of test parameters to use */
  public static void forEach(ITTest_Overlay_Instance runner) {
    for (Corner[] cornerSet : corners) {
      runner.run(cornerSet);
    }
  }

}
