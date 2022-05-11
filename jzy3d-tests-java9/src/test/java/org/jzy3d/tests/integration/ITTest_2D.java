package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.plot3d.rendering.view.View2DLayout;

public class ITTest_2D extends ITTest{
  // parameters to evaluate in the test
  protected static int[] margins = {0, 20};
  protected static int[] tickLabelDists = {0, 10};
  protected static boolean[] textAddMargin = {false, true};
  

  
  @Test
  public void when2DChart_ThenApplyMargins() {
    System.out.println("ITTest : when2DChart_ThenApplyMargins");

    forEach((toolkit, resolution)-> when2DChart_ThenApplyMargins(toolkit, resolution));
  }

  private void when2DChart_ThenApplyMargins(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = chart(wt, hidpi);
    chart.add(surface());


    // For each layout property
    forEach((margin, tickLabelDist, textAddMargin)-> {
        
        // When : no margin, no text
        View2DLayout layout = chart.view2d().getView().getLayout_2D();
        layout.setMargin(margin);
        layout.setTickLabelDistance(tickLabelDist);
        layout.setTextAddMargin(textAddMargin);
        layout.setAxisLabelDistance(0);
        
        // Then
        assertChart(chart, name(ITTest_2D.this, null, wt, chart.getQuality().getHiDPI(), properties(margin, tickLabelDist, textAddMargin)));
    });    
  }
  
  

  public static String properties(int margin, int tickLabel, boolean textAddMargin) {
    return "BorderMargin" + KV + margin + SEP_PROP + "TickLabel" + KV + tickLabel + SEP_PROP + "TextAddMargin" + KV + textAddMargin;
  }


  
  public static interface ITTest2DInstance{
    public void run(int margin, int tickLabel, boolean textVisible);
  }

  public static void forEach(ITTest2DInstance runner) {
    for(int margin: margins) {
      for(int tickLabelDist: tickLabelDists) {
        for(boolean textVisible: textAddMargin) {
          runner.run(margin, tickLabelDist, textVisible);
        }
      }
    }
  }

}
