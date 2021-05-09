package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.EmulGLSkin;
import org.jzy3d.chart.controllers.RateLimiterAdaptsToRenderTime;
import org.jzy3d.chart.controllers.mouse.camera.AdaptiveRenderingPolicy;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.HiDPIProportionalFontSizePolicy;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.tests.integration.ITTest.WT;

public class ITTest_AxisLabelRotateLayout {

  @Test
  public void whenAxisLabelOrientationNotHorizontal() {

    whenAxisLabelOrientationNotHorizontal(WT.EmulGL_AWT, HiDPI.ON);
    whenAxisLabelOrientationNotHorizontal(WT.EmulGL_AWT, HiDPI.OFF);
    whenAxisLabelOrientationNotHorizontal(WT.Native_AWT, HiDPI.OFF);
  }

  public void whenAxisLabelOrientationNotHorizontal(WT wt, HiDPI hidpi) {
    // -------------
    // GIVEN
    
    Chart chart = ITTest.chart(wt, hidpi);
    Shape surface = ITTest.surface();

    // -------------
    // WHEN

    IAxisLayout layout = chart.getAxisLayout();
    //layout.setFont(new Font("Apple Chancery", 20));
    layout.setFont(new Font("Helvetica", 20));
    layout.setFontSizePolicy(new HiDPIProportionalFontSizePolicy(chart.getView()));

    layout.setXAxisLabel("My X axis label is a little long to draw");
    layout.setYAxisLabel("My Y axis label is a little long to draw");
    layout.setZAxisLabel("My Z axis label is a little long to draw");

    layout.setZAxisSide(ZAxisSide.LEFT);
    layout.setZAxisLabelOrientation(LabelOrientation.VERTICAL);
    layout.setYAxisLabelOrientation(LabelOrientation.PARALLEL_TO_AXIS);
    layout.setXAxisLabelOrientation(LabelOrientation.PARALLEL_TO_AXIS);
    
    layout.setAxisLabelOffsetAuto(true);
    layout.setAxisLabelOffsetMargin(20);
    
    layout.setXTickColor(Color.RED);
    layout.setYTickColor(Color.GREEN);
    layout.setZTickColor(Color.BLUE);

    View view = chart.getView();
    //view.setDisplayAxisWholeBounds(true);
    //view.setMaintainAllObjectsInView(true);
    view.setCameraRenderingSphereRadiusFactor(1.1f);

    AWTColorbarLegend colorbar = new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout());
    colorbar.setMinimumWidth(200);
    surface.setLegend(colorbar);
    chart.add(surface);

    // Open and enable controllers

    chart.getKeyboard();
    chart.getMouse();
    
    if (chart.getFactory() instanceof EmulGLChartFactory) {
      EmulGLSkin skin = EmulGLSkin.on(chart);
      //skin.getCanvas().setProfileDisplayMethod(true);
      
      AdaptiveRenderingPolicy policy = new AdaptiveRenderingPolicy();
      policy.renderingRateLimiter = new RateLimiterAdaptsToRenderTime();
      policy.optimizeForRenderingTimeLargerThan = 130;// ms
      policy.optimizeByDroppingFaceAndKeepingWireframeWithColor = true;

      skin.getMouse().setPolicy(policy);
      
      skin.getThread().setSpeed(15);
    }
    
    // -------------
    // THEN

    ITTest.assertChart(chart, ITTest.name(this, wt, chart.getQuality().getHiDPI()));
  }
}
