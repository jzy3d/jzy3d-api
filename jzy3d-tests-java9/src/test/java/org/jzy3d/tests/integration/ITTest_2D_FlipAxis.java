package org.jzy3d.tests.integration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Test;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Margin;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.Font;
import org.jzy3d.plot2d.rendering.AWTGraphicsUtils;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.HiDPIProportionalFontSizePolicy;
import org.jzy3d.plot3d.primitives.axis.layout.providers.RegularTickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.DefaultDecimalTickRenderer;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.legends.overlay.LegendLayout;
import org.jzy3d.plot3d.rendering.view.AWTImageRenderer;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.plot3d.rendering.view.View2D;
import org.jzy3d.plot3d.rendering.view.View2DLayout;
import org.jzy3d.plot3d.rendering.view.View2DLayout_Debug;

public class ITTest_2D_FlipAxis extends ITTest {
  public static void main(String[] args) {
    open(new ITTest_2D_FlipAxis().when2DChartWithFlip(WT.Native_AWT, HiDPI.ON, View2D.YZ,
        FlipAxis.FLIP_H));
    // open(new ITTest_AxisLabelRotateLayout().whenAxisLabelOrientationNotHorizontal(WT.Native_AWT,
    // HiDPI.ON));
  }


  // ----------------------------------------------------
  // TEST MULTIPLE AXIS ORIENTATION SETTINGS
  // ----------------------------------------------------

  //View2D[] views = {View2D.XY, View2D.XZ, View2D.YZ};

  enum FlipAxis {
    FLIP_NONE, FLIP_H, FLIP_V, FLIP_BOTH;
  }

  @Test
  public void when2DChartWithColorbarAndMargins() {
    System.out.println("ITTest : when2DChartWithColorbarAndMargins");

    forEach((toolkit, resolution) -> {
      for (View2D view2d : View2D.values()) {
        for (FlipAxis flip : FlipAxis.values()) {
          when2DChartWithFlip(toolkit, resolution, view2d, flip);
        }


      }
    }, toolkitsAWT);
  }



  protected Chart when2DChartWithFlip(WT wt, HiDPI hidpi, View2D viewMode, FlipAxis flip) {
    // Given a chart with long Y axis name
    AWTChart chart = (AWTChart) chart(wt, hidpi);

    // ---------------------------------------
    // CHART CONTENT
    // ---------------------------------------

    // Create a chart
    AWTView view = (AWTView) chart.getView();

    // Surface
    Shape surface = SampleGeom.surface(new Range(-3, 1), new Range(-1, 3), 1f);
    surface.setWireframeDisplayed(false);
    chart.add(surface);


    DefaultDecimalTickRenderer tickRenderer = new DefaultDecimalTickRenderer(12);

    // Colorbar
    AWTColorbarLegend colorbar = chart.colorbar(surface);
    // TODO https://github.com/jzy3d/jzy3d-api/issues/273
    colorbar.getImageGenerator().setTickRenderer(tickRenderer);



    // Logo
    BufferedImage logo = null;
    try {
      logo = ImageIO.read(new File("src/test/resources/icons/martin.jpeg"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    logo = AWTGraphicsUtils.scale(logo, .25f, .25f);

    AWTImageRenderer imageRenderer = new AWTImageRenderer(logo);
    view.addRenderer2d(imageRenderer);


    // ---------------------------------------
    // LAYOUT CONFIGURATION
    // ---------------------------------------

    int minMargin = 10;
    int defaultDistances = 10;
    boolean spaceForRightMostTick = false;
    boolean spaceForLogo = false;
    // TODO : fix AWTImageViewport.renderImage to properly work for any x axis/tick label distance
    boolean colorbarBottomAlignWithChartView = false;


    // Axis layout configuration
    AxisLayout axisLayout = view.getAxisLayout();
    axisLayout.setTickLineDisplayed(true);
    axisLayout.setYAxisLabel("Y axis is a bit longer than usual");
    axisLayout.setZTickProvider(new RegularTickProvider(5));
    axisLayout.setZTickRenderer(tickRenderer);

    // axisLayout.setXAxisLabelDisplayed(false);


    axisLayout.setFont(new Font("Arial", 14));
    axisLayout.setFontSizePolicy(new HiDPIProportionalFontSizePolicy(chart.getView()));
    axisLayout.setAxisLabelOffsetAuto(true);

    // Logo layout configuration
    LegendLayout legendLayout = imageRenderer.getLayout();
    legendLayout.setCorner(LegendLayout.Corner.BOTTOM_LEFT);

    // View layout configuration
    View2DLayout viewLayout = view.get2DLayout();
    viewLayout.setMargin(new Margin(minMargin, minMargin * 2, minMargin * 3, minMargin * 4));
    viewLayout.setTickLabelDistance(defaultDistances);
    viewLayout.setAxisLabelDistance(defaultDistances * 2);
    viewLayout.setSymetricVerticalMargin(false);

    // View layout with a bit more space on the left for the logo
    if (spaceForLogo) {
      int marginL = Math.round(logo.getWidth(null) + legendLayout.getMargin().getLeft()) / 2;
      viewLayout.getMargin().setLeft(marginL);
    }

    // View layout avoid covering tick label with colorbar, or configure space for symetry
    if (spaceForRightMostTick) {
      if (surface.hasLegend()) {
        // viewLayout.getMargin().setRight(minMargin + 15);

        // NOT EXACT WHEN GROWING FONT!
        int xWidth = axisLayout.getRightMostXTickLabelWidth(view.getPainter());

        // distance between axis right border, and view main viewport border (distance to colorbar
        // border)
        viewLayout.getMargin().setRight(xWidth / 2);
      } else {
        viewLayout.setSymetricHorizontalMargin(true);
      }
    }


    // Colorbar Layout
    // ViewAndColorbarsLayout viewportLayout = (ViewAndColorbarsLayout) view.getLayout();


    Margin viewMargin = viewLayout.getMargin();
    int bottom = (int) viewMargin.getBottom();

    if (colorbarBottomAlignWithChartView) {

      bottom += viewLayout.getHorizontalTickLabelsDistance();
      bottom += axisLayout.getFont().getHeight();
      bottom += viewLayout.getHorizontalAxisLabelsDistance();
      bottom += axisLayout.getFont().getHeight();
    }

    // SI RIGHT MARGIN NON NUL ET LEFT NULL, IMAGE MAL CENTREE
    Margin colorbarMargin = new Margin(minMargin * 1, minMargin * 1, viewMargin.getTop(), bottom);
    colorbar.setMargin(colorbarMargin);


    View2DLayout_Debug debug = new View2DLayout_Debug();
    debug.setEnableColorMarginDebug(false);
    view.addRenderer2d(debug);


    // Enable 2D mode
    chart.view2d(viewMode);


    if (FlipAxis.FLIP_H.equals(flip)) {
      chart.getView().get2DLayout().setHorizontalAxisFlip(true);

      // Then
      assertChart(chart, name(ITTest_2D_FlipAxis.this,
          "View" + KV + viewMode + SEP_PROP + "Flip" + KV + "X", wt, hidpi));

    } else if (FlipAxis.FLIP_V.equals(flip)) {
      chart.getView().get2DLayout().setVerticalAxisFlip(true);

      // Then
      assertChart(chart, name(ITTest_2D_FlipAxis.this,
          "View" + KV + viewMode + SEP_PROP + "Flip" + KV + "Y", wt, hidpi));

    } else if (FlipAxis.FLIP_BOTH.equals(flip)) {
      chart.getView().get2DLayout().setBothAxisFlip(true);

      // Then
      assertChart(chart, name(ITTest_2D_FlipAxis.this,
          "View" + KV + viewMode + SEP_PROP + "Flip" + KV + "Both", wt, hidpi));

    } else {
      // Then
      assertChart(chart, name(ITTest_2D_FlipAxis.this,
          "View" + KV + viewMode + SEP_PROP + "Flip" + KV + "None", wt, hidpi));

    }



    return chart;
  }

  /*
   * public static interface ITTest2D_Orientation_Instance { public void run(LabelOrientation
   * yAxisOrientation); }
   * 
   * public static void forEach(ITTest2D_Orientation_Instance runner) { for (LabelOrientation
   * yAxisOrientation : yOrientations) { runner.run(yAxisOrientation); } }
   */

}
