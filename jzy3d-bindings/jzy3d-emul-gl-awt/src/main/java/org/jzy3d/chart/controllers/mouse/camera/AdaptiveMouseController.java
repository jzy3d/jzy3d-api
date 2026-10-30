package org.jzy3d.chart.controllers.mouse.camera;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.RateLimiter;
import org.jzy3d.chart.controllers.RateLimiterAdaptsToRenderTime;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.AbstractAdativeRenderingHandler;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.handlers.AdaptByDroppingFaceAndKeepingWireframe;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.handlers.AdaptByDroppingFaceAndKeepingWireframeWithColor;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.handlers.AdaptByDroppingHiDPI;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.handlers.AdaptByDroppingSmoothColor;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.handlers.AdaptByDroppingWireframe;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.handlers.AdaptByKeepingBoundingBoxOnly;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.handlers.AdaptByPerformanceKnowledge;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot3d.primitives.Wireframeable;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import jgl.wt.awt.GL;

public class AdaptiveMouseController extends AWTCameraMouseController {
  protected Quality currentQuality;
  protected boolean currentHiDPI;
  protected ICanvas canvas;
  protected GL gl;
  protected EmulGLPainter painter;

  /**
   * An optimization policy
   */
  protected AdaptiveRenderingPolicy policy = new AdaptiveRenderingPolicy();

  /**
   * Keep track if optimization should be triggered or not. Defined at mouse pressed.
   */
  protected boolean mustOptimizeMouseDrag = false;

  /**
   * Keep track if this is the first mouse drag event since the last mouse released event. This is
   * used to trigger optimization only once when multiple successive mouse drag events are received.
   */
  protected boolean isFirstDrag = true;


  protected AbstractAdativeRenderingHandler adaptByDroppingFace;
  protected AdaptByDroppingFaceAndKeepingWireframeWithColor adaptByDroppingFaceAndColoringWire;
  protected AdaptByDroppingWireframe adaptByDroppingWireframe;
  protected AdaptByDroppingHiDPI adaptByDroppingHiDPI;
  protected AdaptByDroppingSmoothColor adaptByDroppingSmoothColor;
  protected AdaptByKeepingBoundingBoxOnly adaptByKeepingBoundingBoxOnly;
  protected AdaptByPerformanceKnowledge adaptByPerformanceKnowledge;

  /**
   * Keep track of drawable that have had their wireframe disabled for optimization in order to
   * re-activate at mouse release.
   */
  protected List<Wireframeable> droppedWireframeToReset = new ArrayList<>();

  /**
   * Keep track of drawable that have had their face disabled for optimization in order to
   * re-activate at mouse release.
   */
  protected List<Wireframeable> droppedFaceAndColoredWireframeToReset = new ArrayList<>();



  public AdaptiveMouseController() {
    super();
  }

  public AdaptiveMouseController(Chart chart, AdaptiveRenderingPolicy policy) {
    super(chart);
    setPolicy(policy);
  }

  public AdaptiveMouseController(Chart chart) {
    super(chart);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    loadChartFields(getChart());

    double lastRenderingTime = getLastRenderingTimeFromCanvas();

    // decide if an optimization should be applied
    if (detectIfRenderingIsSlow(lastRenderingTime)) {
      mustOptimizeMouseDrag = true;
      // never true if policy is null
    }

    // do usual work
    super.mousePressed(e);

    isFirstDrag = true;
  }


  @Override
  public void mouseDragged(MouseEvent e) {
    //System.out.println("DRAG");

    if (isFirstDrag) {
      isFirstDrag = false;

      // apply optimization
      if (mustOptimizeMouseDrag) {
        startOptimizations();
      }
    }
    super.mouseDragged(e);
    

  }

  @Override
  public void mouseReleased(MouseEvent e) {
    // do usual work
    super.mouseReleased(e);

    // If optimization did activate during mouse drag,
    // then rollback optimization as we are exiting from drag/rotation
    if (mustOptimizeMouseDrag) {
      stopOptimizations();

      // for display of this last image with the new HiDPI setting
      canvas.forceRepaint();

      mustOptimizeMouseDrag = false;
    }

  }

  // **************** PROCESS RENDERING PERF ***************** //

  protected double getLastRenderingTimeFromCanvas() {
    return canvas.getLastRenderingTimeMs();
  }

  protected boolean detectIfRenderingIsSlow(double lastRenderingTime) {
    if (policy == null)
      return false;
    else
      return policy.optimizeForRenderingTimeLargerThan < lastRenderingTime;
  }
  
  protected void loadChartFields(Chart chart) {
    painter = (EmulGLPainter) chart.getPainter();
    currentQuality = chart.getQuality();
    currentHiDPI = painter.getGL().isAutoAdaptToHiDPI();
    canvas = chart.getCanvas();
    gl = ((EmulGLPainter) chart.getPainter()).getGL();

    RateLimiter r = getRateLimiter();
    if (r instanceof RateLimiterAdaptsToRenderTime) {
      ((RateLimiterAdaptsToRenderTime) r).setCanvas(canvas);
    }
    
    if(adaptByDroppingFace==null)
      adaptByDroppingFace = new AdaptByDroppingFaceAndKeepingWireframe(chart);
    if(adaptByDroppingFaceAndColoringWire==null)
      adaptByDroppingFaceAndColoringWire = new AdaptByDroppingFaceAndKeepingWireframeWithColor(chart);
    if(adaptByDroppingWireframe==null)
      adaptByDroppingWireframe = new AdaptByDroppingWireframe(chart);
    if(adaptByDroppingHiDPI==null)
      adaptByDroppingHiDPI = new AdaptByDroppingHiDPI(chart);
    if(adaptByDroppingSmoothColor==null)
      adaptByDroppingSmoothColor = new AdaptByDroppingSmoothColor(chart);
    if(adaptByKeepingBoundingBoxOnly==null) 
      adaptByKeepingBoundingBoxOnly = new AdaptByKeepingBoundingBoxOnly(chart);
    if(adaptByPerformanceKnowledge==null)
      adaptByPerformanceKnowledge = new AdaptByPerformanceKnowledge(chart);
    adaptByPerformanceKnowledge.setPerf(getLODPerf());
    adaptByPerformanceKnowledge.setMaxRenderingTime(policy.optimizeForRenderingTimeLargerThan);
    
  }

  // **************** START/STOP OPTIMISATION ***************** //

  protected void startOptimizations() {
    if (policy.optimizeByDroppingWireframeOnly)
      adaptByDroppingWireframe.apply();
    
    if (policy.optimizeByDroppingFaceAndKeepingWireframe)
      adaptByDroppingFace.apply();
    
    if (policy.optimizeByDroppingFaceAndKeepingWireframeWithColor)
      adaptByDroppingFaceAndColoringWire.apply();
    
    if (policy.optimizeByDroppingHiDPI)
      adaptByDroppingHiDPI.apply();
    
    if (policy.optimizeByDroppingSmoothColor)
      adaptByDroppingSmoothColor.apply();
    
    if(policy.optimizeByDrawingBoundingBoxOnly)
      adaptByKeepingBoundingBoxOnly.apply();
    
    if(policy.optimizeByPerformanceKnowledge){
      adaptByPerformanceKnowledge.apply();
    }

  }

  protected void stopOptimizations() {
    if (policy.optimizeByDroppingWireframeOnly)
      adaptByDroppingWireframe.revert();
    
    if (policy.optimizeByDroppingFaceAndKeepingWireframe)
      adaptByDroppingFace.revert();
    
    if (policy.optimizeByDroppingFaceAndKeepingWireframeWithColor)
      adaptByDroppingFaceAndColoringWire.revert();

    if (policy.optimizeByDroppingHiDPI)
      adaptByDroppingHiDPI.revert();
    
    if (policy.optimizeByDroppingSmoothColor)
      adaptByDroppingSmoothColor.revert();
    
    if(policy.optimizeByDrawingBoundingBoxOnly)
      adaptByKeepingBoundingBoxOnly.revert();
    
    if(policy.optimizeByPerformanceKnowledge){
      adaptByPerformanceKnowledge.revert();
    }

  }

  // GET / SET

  public AdaptiveRenderingPolicy getPolicy() {
    return policy;
  }

  public void setPolicy(AdaptiveRenderingPolicy policy) {
    this.policy = policy;
    this.setRateLimiter(policy.renderingRateLimiter);
  }

}
