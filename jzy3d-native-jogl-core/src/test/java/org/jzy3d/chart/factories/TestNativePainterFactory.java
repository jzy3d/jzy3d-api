package org.jzy3d.chart.factories;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.IViewOverlay;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;
import com.jogamp.opengl.GLCapabilities;


public class TestNativePainterFactory {
  @Test
  public void hasGoodSettingsToMakeOffscreenRenderingWithColor() {
    NativePainterFactory f = newAbstractNativePainterFactory();
    f.setOffscreen(800, 800);

    GLCapabilities caps = f.getCapabilities();

    Assert.assertFalse(caps.isOnscreen());

    Assert.assertEquals(8, caps.getAlphaBits());
    Assert.assertEquals(8, caps.getRedBits());
    Assert.assertEquals(8, caps.getBlueBits());
    Assert.assertEquals(8, caps.getGreenBits());
  }

  @Test
  public void hasGoodSettingsToMakeOnscreenRenderingWithColor() {
    NativePainterFactory f = newAbstractNativePainterFactory();

    GLCapabilities caps = f.getCapabilities();

    Assert.assertTrue(caps.getHardwareAccelerated());
    Assert.assertTrue(caps.isOnscreen());

    Assert.assertEquals(8, caps.getAlphaBits());
    Assert.assertEquals(8, caps.getRedBits());
    Assert.assertEquals(8, caps.getBlueBits());
    Assert.assertEquals(8, caps.getGreenBits());
  }

  private NativePainterFactory newAbstractNativePainterFactory() {
    return new NativePainterFactory() {
      @Override
      public IViewOverlay newViewOverlay() {
        return null;
      }

      @Override
      public IViewportLayout newViewportLayout() {
        return null;
      }

      @Override
      public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
        return null;
      }

      @Override
      public ICameraMouseController newMouseCameraController(Chart chart) {
        return null;
      }

      @Override
      public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
        return null;
      }

      @Override
      public ICameraKeyController newKeyboardCameraController(Chart chart) {
        return null;
      }

      @Override
      public IScreenshotKeyController newKeyboardScreenshotController(Chart chart) {
        return null;
      }

      @Override
      public IFrame newFrame(Chart chart) {
        return null;
      }

      @Override
      public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
        return null;
      }
    };
  }

}
