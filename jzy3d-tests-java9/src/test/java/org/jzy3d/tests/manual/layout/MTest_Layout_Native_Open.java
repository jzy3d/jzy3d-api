package org.jzy3d.tests.manual.layout;

import java.awt.image.BufferedImage;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.events.IViewLifecycleEventListener;
import org.jzy3d.events.ViewLifecycleEvent;
import org.jzy3d.painters.Font;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.HiDPIProportionalFontSizePolicy;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.View2DLayout_Debug;

/**
 * QTP
 * valider avec / sans HiDPI : 
 * - le layout du texte est le même
 * - les marges / offset sont appliquées correctement
 * - marche côté gauche / côté droit
 * - avec vertical / oblique / etc
 * 
 * - Le calcul d'offset de label ne prend pas en compte la rotation du texte dans la place qu'il prend
 * 
 * @author martin
 *
 */
public class MTest_Layout_Native_Open {

  static final float ALPHA_FACTOR = 0.55f;// .61f;

  public static void main(String[] args) throws InterruptedException {
    AWTChartFactory factory = new AWTChartFactory();
    //EmulGLChartFactory factory = new EmulGLChartFactory();

    new MTest_Layout_Native_Open().go(factory);
  
    
  }

  private  void go(ChartFactory factory) throws InterruptedException {
    Quality q = Quality.Advanced();
    q.setAnimated(false);
    q.setHiDPIEnabled(true);


    // --------------------------------------------------------
    // Configure chart content

    Shape surface = SampleGeom.surface();

    AWTChart chart = (AWTChart)factory.newChart(q);
    AWTView view = chart.getView();

//((ViewAndColorbarsLayout) chart.getView().getLayout()).setShrinkColorbar(true);

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

    
    //chart.getAxisLayout().setMainColor(Color.WHITE);
    //chart.getView().setBackgroundColor(Color.BLACK);

    // view.setDisplayAxisWholeBounds(true);
    // view.setMaintainAllObjectsInView(true);
    //view.setCameraRenderingSphereRadiusFactor(1.1f);

    // TODO : auto configure colorbar with back color?
    
    chart.add(surface);
    
    AWTColorbarLegend colorbar = chart.colorbar(surface);

    // --------------------------------------------------------
    // Open and enable controllers


    chart.getKeyboard();
    chart.getMouse();  
    
    
    
    
  
    
    view.addRenderer2d(new View2DLayout_Debug(Color.GREEN));
    
    /*view.getCamera().setScreenGridDisplayed(true);
    colorbar.setScreenGridDisplayed(true);*/
    
    
    view.addViewLifecycleChangedListener(new IViewLifecycleEventListener() {
      @Override
      public void viewWillRender(ViewLifecycleEvent e) {
      }
      @Override
      public void viewHasInit(ViewLifecycleEvent e) {
      }
      @Override
      public void viewHasRendered(ViewLifecycleEvent e) {
        info(chart, view, colorbar);
      }

      
    });
    
    
    chart.open(800, 600);
    chart.view2d();
    
    //Thread.sleep(100);
    //info(chart, view, colorbar);

  }
  
  private void info(AWTChart chart, AWTView view, AWTColorbarLegend colorbar) {
    //System.out.println("Canvas.bounds   : " + ((Component)chart.getCanvas()).getBounds());
    System.out.println("---------------------------------");
    System.out.println("Canvas.dims        : " + chart.getCanvas().getDimension());
    System.out.println("---");
    System.out.println("View.cam.viewport  : " + view.getCamera().getLastViewPort());
    System.out.println("View.scale         : " + view.getPixelScale());
    System.out.println("---");
    
    BufferedImage i = colorbar.getImage();

    System.out.println("Colorbar.askedDim  : " + colorbar.getWidth() + " x " + colorbar.getHeight());
    System.out.println("Colorbar.viewport  : " + colorbar.getLastViewPort());
    System.out.println("Colorbar.margins   : " + colorbar.getMargin());
    System.out.println("Colorbar.image     : " + i.getWidth(null) + " x " + i.getHeight(null));
    System.out.println("Colorbar.minDim    : " + colorbar.getMinimumDimension());
    System.out.println("---");

    
    AWTColorbarImageGenerator gen = colorbar.getImageGenerator();
    System.out.println("Colorbar.gen.scale       : " + gen.getPixelScale());
    System.out.println("Colorbar.gen.prefWidth   : " + gen.getPreferredWidth(chart.getPainter()));
    System.out.println("Colorbar.gen.bar         : " + gen.getBarWidth());
    System.out.println("Colorbar.gen.txt2bar     : " + gen.getTextToBarHorizontalMargin());
    System.out.println("Colorbar.gen.maxTxtWidth : " + gen.getMaxTextWidth());
  }



  /*
   * 
       if (factory instanceof EmulGLChartFactory) {
      EmulGLSkin skin = EmulGLSkin.on(chart);

      //skin.getCanvas().setProfileDisplayMethod(true);
      //skin.getCanvas().setDebugEvents(true);

      skin.getLayout().setShrinkColorbar(true);

      AdaptiveRenderingPolicy policy = new AdaptiveRenderingPolicy();
      policy.renderingRateLimiter = new RateLimiterAdaptsToRenderTime();
      policy.optimizeForRenderingTimeLargerThan = 130;// ms
      policy.optimizeByDroppingFaceAndKeepingWireframeWithColor = true;

      skin.getMouse().setPolicy(policy);

      skin.getThread().setSpeed(15);
    } else {
      ((ViewAndColorbarsLayout) ((AWTView) chart.getView()).getLayout()).setShrinkColorbar(true);
    }*/
   
}
