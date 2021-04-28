package org.jzy3d.chart.controllers;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Wireframeable;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Graph;
import jgl.GL;

public class AdaptiveMouseController extends AWTCameraMouseController {
  protected Quality currentQuality;
  protected boolean currentHiDPI;
  protected ICanvas canvas;
  protected GL gl;
  protected EmulGLPainter painter;

  /**
   * An optimization policy
   */
  protected AdaptiveRenderingPolicy policy;

  /**
   * Keep track if optimization should be triggered or not. Defined at mouse pressed.
   */
  protected boolean mustOptimizeMouseDrag = false;

  /**
   * Keep track if this is the first mouse drag event since the last mouse released event. This is
   * used to trigger optimization only once when multiple successive mouse drag events are received.
   */
  protected boolean isFirstDrag = true;



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

  protected List<Wireframeable> droppedFaceToReset = new ArrayList<>();

  /**
   * Keep track of canvas performance.
   */
  // protected double lastRenderingTime = -1;


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
  }

  // **************** START/STOP OPTIMISATION ***************** //

  protected void startOptimizations() {
    if (policy.optimizeByDroppingWireframeOnly)
      startDroppingWireframeOnly(getChart());
    if (policy.optimizeByDroppingFace)
      startDroppingFace(getChart());
    if (policy.optimizeByDroppingFaceAndColoringWireframe)
      startDroppingFaceAndColoringWireframe(getChart());
    if (policy.optimizeByDroppingHiDPI)
      startNoHiDPI(getChart());
  }

  protected void stopOptimizations() {
    if (policy.optimizeByDroppingWireframeOnly)
      stopDroppingWireframeOnly();
    if (policy.optimizeByDroppingFace)
      stopDroppingFace();
    if (policy.optimizeByDroppingFaceAndColoringWireframe)
      stopDroppingFaceAndColoringWireframe();
    if (policy.optimizeByDroppingHiDPI)
      stopNoHiDPI(getChart()); // trigger a last rendering
  }


  // OptimizeWithHiDPI

  protected void startNoHiDPI(Chart chart) {
    Quality alternativeQuality = currentQuality.clone();
    alternativeQuality.setPreserveViewportSize(true);
    chart.setQuality(alternativeQuality);
    gl.setAutoAdaptToHiDPI(false);
  }

  protected void stopNoHiDPI(Chart chart) {
    chart.setQuality(currentQuality);
    gl.setAutoAdaptToHiDPI(currentHiDPI);
    // this force the GL image to apply the new HiDPI setting immediatly
    gl.updatePixelScale(((EmulGLCanvas) canvas).getGraphics());
    gl.applyViewport();
  }

  // KeepingWireframeOnly

  protected void startDroppingWireframeOnly(Chart chart) {
    Graph graph = chart.getScene().getGraph();
    for (Drawable d : graph.getAll()) {
      if (d instanceof Wireframeable) {
        Wireframeable w = (Wireframeable) d;
        if (w.getWireframeDisplayed()) {
          droppedWireframeToReset.add(w);
          w.setWireframeDisplayed(false);
        }
      }
    }
  }

  protected void stopDroppingWireframeOnly() {
    for (Wireframeable w : droppedWireframeToReset) {
      w.setWireframeDisplayed(true);
    }
  }

  // DroppingFace

  protected void startDroppingFace(Chart chart) {
    Graph graph = chart.getScene().getGraph();
    for (Drawable d : graph.getAll()) {
      if (d instanceof Wireframeable) {
        Wireframeable w = (Wireframeable) d;
        if (w.getFaceDisplayed()) {
          droppedFaceToReset.add(w);
          w.setFaceDisplayed(false);
        }
      }
    }
  }

  protected void stopDroppingFace() {
    for (Wireframeable w : droppedFaceToReset) {
      w.setFaceDisplayed(true);
      w.setWireframeColorFromPolygonPoints(false);
    }
  }

  // DroppingFaceAndColoringWireframe

  protected void startDroppingFaceAndColoringWireframe(Chart chart) {
    Graph graph = chart.getScene().getGraph();
    for (Drawable d : graph.getAll()) {
      if (d instanceof Wireframeable) {
        Wireframeable w = (Wireframeable) d;
        if (w.getFaceDisplayed()) {
          droppedFaceAndColoredWireframeToReset.add(w);
          w.setFaceDisplayed(false);
          w.setWireframeColorFromPolygonPoints(true);
        }
      }
    }
  }

  protected void stopDroppingFaceAndColoringWireframe() {
    for (Wireframeable w : droppedFaceAndColoredWireframeToReset) {
      w.setFaceDisplayed(true);
      w.setWireframeColorFromPolygonPoints(false);
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
