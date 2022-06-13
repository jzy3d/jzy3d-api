package org.jzy3d.tests.manual.layout;

import org.jzy3d.analysis.AWTAbstractAnalysis;
import org.jzy3d.chart.EmulGLSkin;
import org.jzy3d.chart.controllers.RateLimiterAdaptsToRenderTime;
import org.jzy3d.chart.controllers.mouse.camera.AdaptiveRenderingPolicy;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.builder.Func3D;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.HiDPIProportionalFontSizePolicy;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;

/**
 * QTP valider avec / sans HiDPI : - le layout du texte est le même - les marges / offset sont
 * appliquées correctement - marche côté gauche / côté droit - avec vertical / oblique / etc
 * 
 * - Le calcul d'offset de label ne prend pas en compte la rotation du texte dans la place qu'il
 * prend
 * 
 * @author martin
 *
 */
public class MTest_Layout extends AWTAbstractAnalysis {

  static final float ALPHA_FACTOR = 0.55f;// .61f;

  public MTest_Layout() {
    factory = new EmulGLChartFactory();
  }
  
  Shape surface;
  AWTColorbarLegend colorbar;

  @Override
  public void init() {

    Quality q = Quality.Advanced();
    q.setAnimated(false);
    q.setHiDPIEnabled(true);


    // --------------------------------------------------------
    // Configure chart content

    Shape surface = surface();

    chart = factory.newChart(q);


    if (factory instanceof EmulGLChartFactory) {
      EmulGLSkin skin = EmulGLSkin.on(chart);

      //skin.getCanvas().setProfileDisplayMethod(true);
      //skin.getCanvas().setDebugEvents(true);

      AdaptiveRenderingPolicy policy = new AdaptiveRenderingPolicy();
      policy.renderingRateLimiter = new RateLimiterAdaptsToRenderTime();
      policy.optimizeForRenderingTimeLargerThan = 130;// ms
      policy.optimizeByDroppingFaceAndKeepingWireframeWithColor = true;

      skin.getMouse().setPolicy(policy);

      skin.getThread().setSpeed(15);
    } 
    
    AxisLayout layout = chart.getAxisLayout();
    // layout.setFont(new Font("Apple Chancery", 20));
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
    // view.setDisplayAxisWholeBounds(true);
    // view.setMaintainAllObjectsInView(true);
    view.setCameraRenderingSphereRadiusFactor(1.1f);

    colorbar =
        new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout());
    surface.setLegend(colorbar);
    chart.add(surface);

    // --------------------------------------------------------
    // Open and enable controllers


    chart.getKeyboard();
    chart.getMouse();

  }


  private static Shape surface() {
    Func3D func = new Func3D((x, y) -> x * Math.sin(x * y));
    Range range = new Range(-3, 3);
    int steps = 50;

    Shape surface = new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps), func);

    ColorMapper colorMapper =
        new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, ALPHA_FACTOR));// 0.65f));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    surface.setWireframeWidth(1);
    return surface;
  }

}
