package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.plot3d.rendering.view.View2DLayout;

public class ITTest_2D extends ITTest {
  
  // ----------------------------------------------------
  // TEST MULTIPLE MARGINS SETTINGS
  // ----------------------------------------------------

  static String caseMargin = "when2DLayoutConfig_ThenApplyMargins";

  // parameters to evaluate in the test
  protected static int[] margins = {0, 20};
  protected static int[] tickLabelDists = {0, 10};
  protected static int[] axisLabelDists = {0, 10};
  protected static boolean[] textAddMargin = {true};

  /** Toolkit and resolution iterator */
  @Test
  public void when2DLayoutConfig_ThenApplyMargins() {
    System.out.println("ITTest : " + caseMargin);

    forEach((toolkit, resolution) -> when2DLayoutConfig_ThenApplyMargins(toolkit, resolution));
  }

  /** Test parameters iterator */
  protected void when2DLayoutConfig_ThenApplyMargins(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = chart(wt, hidpi);
    chart.add(surface());


    // For each layout property
    forEach((margin, tickLabelDist, axisLabelDist, textAddMargin) -> {

      // When : no margin, no text
      View2DLayout layout = chart.view2d().getView().get2DLayout();
      layout.setMargin(margin);
      layout.setTickLabelDistance(tickLabelDist);
      layout.setAxisLabelDistance(axisLabelDist);
      layout.setTextAddMargin(textAddMargin);

      // Then
      assertChart(chart, name(ITTest_2D.this, caseMargin, wt, hidpi,
          properties(margin, tickLabelDist, axisLabelDist, textAddMargin)));
    });
  }

  public static String properties(int margin, int tickLabel, int axisLabel, boolean textAddMargin) {
    return "BorderMargin" + KV + margin + SEP_PROP + "TickLabel" + KV + tickLabel + SEP_PROP
        + "AxisLabel" + KV + axisLabel + SEP_PROP + "TextAddMargin" + KV + textAddMargin;
  }

  public static interface ITTest2D_Margin_Instance {
    public void run(int margin, int tickLabelDistance, int axisLabelDistance, boolean textVisible);
  }

  /** Iterate over all combination of test parameters to use */
  public static void forEach(ITTest2D_Margin_Instance runner) {
    for (int margin : margins) {
      for (int tickLabelDist : tickLabelDists) {
        for (int axisLabelDist : axisLabelDists) {
          for (boolean textVisible : textAddMargin) {
            runner.run(margin, tickLabelDist, axisLabelDist, textVisible);
          }
        }
      }
    }
  }

  
  // ----------------------------------------------------
  // TEST MULTIPLE AXIS ORIENTATION SETTINGS
  // ----------------------------------------------------

  static String caseAxisRotated = "whenAxisRotated_ThenApplyMargins";
  
  protected static LabelOrientation[] yOrientations =
      {LabelOrientation.VERTICAL, LabelOrientation.HORIZONTAL};
  
  
  /** Toolkit and resolution iterator */
  @Test
  public void whenAxisRotated_ThenApplyMargins() {
    System.out.println("ITTest : " + caseAxisRotated);

    forEach((toolkit, resolution) -> whenAxisRotated_ThenApplyMargins(toolkit, resolution));
  }


  protected void whenAxisRotated_ThenApplyMargins(WT wt, HiDPI hidpi) {
    // Given a chart with long Y axis name
    Chart chart = chart(wt, hidpi);
    chart.add(surface());
    chart.view2d();

    AxisLayout axisLayout = chart.getAxisLayout();
    axisLayout.setYAxisLabel("Y axis longer than usual");
    
    forEach((yOrientation)->{
      // When : vertical orientation
      axisLayout.setYAxisLabelOrientation(yOrientation);

      // Then
      assertChart(chart, name(ITTest_2D.this, caseAxisRotated, wt, hidpi, properties(yOrientation)));
      
    });
  }

  public static String properties(LabelOrientation yOrientation) {
    return "yAxisOrientation" + KV + yOrientation;
  }

  public static interface ITTest2D_Orientation_Instance {
    public void run(LabelOrientation yAxisOrientation);
  }

  /** Iterate over all combination of test parameters to use */
  public static void forEach(ITTest2D_Orientation_Instance runner) {
    for (LabelOrientation yAxisOrientation : yOrientations) {
      runner.run(yAxisOrientation);
    }
  }


}
