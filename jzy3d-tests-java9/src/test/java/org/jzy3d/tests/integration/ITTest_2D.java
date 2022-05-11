package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.plot3d.rendering.view.View2DLayout;

public class ITTest_2D extends ITTest{
  // parameters to evaluate in the test
  static int[] margins = {0, 20};
  static int[] tickLabelDists = {0, 10};
  static boolean[] textVisibles = {false, true};
  
  public static interface ITTest2DInstance{
    public void run(int margin, int tickLabel, boolean textVisible);
  }


  
  @Test
  public void when2DChart_ThenApplyMargins() {
    System.out.println("ITTest : when2DChart_ThenApplyMargins");

    applyToAllTookitAndResolutions(new ITTestInstance(){
      public void run(WT toolkit, HiDPI resolution) {
        when2DChart_ThenApplyMargins(toolkit, resolution);
      }
    });
  }

  private void when2DChart_ThenApplyMargins(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = chart(wt, hidpi);
    chart.add(surface());


    // For each layout property
    
    forAll_2D_Parameters(new ITTest2DInstance() {
      @Override
      public void run(int margin, int tickLabelDist, boolean textVisible) {
        // When : no margin, no text
        View2DLayout layout = chart.view2d().getView().getLayout_2D();
        layout.setMargin(margin);
        layout.setTickLabelDistance(tickLabelDist);
        layout.setKeepTextVisible(textVisible);
        
        // Then
        assertChart(chart, name(ITTest_2D.this, null, wt, chart.getQuality().getHiDPI(), properties(margin, tickLabelDist, textVisible)));

      }
    });
    
    /*for(int margin: margins) {
      for(int tickLabelDist: tickLabelDists) {
        for(boolean textVisible: textVisibles) {


        }
      }
    }*/
  }
  
  public static void forAll_2D_Parameters(ITTest2DInstance runner) {
    for(int margin: margins) {
      for(int tickLabelDist: tickLabelDists) {
        for(boolean textVisible: textVisibles) {
          runner.run(margin, tickLabelDist, textVisible);
        }
      }
    }
    
  }
  

  public static String properties(int margin, int tickLabel, boolean textVisible) {
    return "BorderMargin" + KV + margin + SEP_PROP + "TickLabel" + KV + tickLabel + SEP_PROP + "TextVisible" + KV + textVisible;
  }


}
