package org.jzy3d.chart;

import org.jzy3d.chart.controllers.AdaptiveMouseController;
import org.jzy3d.chart.controllers.CameraThreadControllerWithTime;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.AWTScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;

/**
 * {@link EmulGLSkin} is a chart facade that returns known subtypes of chart components already downcasted.
 * 
 * 
 * @author Martin Pernollet
 *
 */
public class EmulGLSkin {
  /* EXNTENDING AND OVERIDE SUBTYPES CAN`T BUILD SO WON T DEFINE THIS AS A TYPING FACADE
   * 
   * instead, provide a, EmulGLTypeSkin returning downcast of generic factory instances
   * 
   * 
  @Override
  public AdaptiveMouseController addMousePickingController(int clickWidth) {
    return (AdaptiveMouseController)getFactory().getPainterFactory().newMousePickingController(this, clickWidth);
  }
  */
  
  public static EmulGLSkin on(Chart chart) {
    return new EmulGLSkin(chart);
  }

  protected Chart chart;

  public EmulGLSkin(Chart chart) {
    super();
    this.chart = chart;
  }

  public EmulGLCanvas getCanvas() {
    return (EmulGLCanvas)chart.getCanvas();
  }

  
  public AdaptiveMouseController addMouseCameraController() {
    return (AdaptiveMouseController)chart.addMouseCameraController();
  }

  public AWTCameraKeyController addKeyboardCameraController() {
    return (AWTCameraKeyController)chart.addKeyboardCameraController();
  }

  public AdaptiveMouseController getMouse() {
    return (AdaptiveMouseController)chart.getMouse();
  }

  // can't import AWTMousePick yet because in JOGL package
  public IMousePickingController getMousePicking() {
    return chart.getMousePicking();
  }

  public AWTCameraKeyController getKeyboard() {
    return (AWTCameraKeyController)chart.getKeyboard();
  }

  public AWTScreenshotKeyController getScreenshotKey() {
    return (AWTScreenshotKeyController)chart.getScreenshotKey();
  }

  public CameraThreadControllerWithTime getSlaveThreadController(AdaptiveMouseController mouse) {
    return (CameraThreadControllerWithTime)mouse.getSlaveThreadController();
  }

  
  public CameraThreadControllerWithTime getSlaveThreadController() {
    return getSlaveThreadController(getMouse());
  }
}
