package org.jzy3d.lwjgl;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.IAnimator;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.symbols.SymbolHandler;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.image.IImageWrapper;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.IViewOverlay;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;

public class LWJGLPainterFactory implements IPainterFactory{
  IChartFactory factory;
  
  @Override
  public IPainter newPainter() {
    return new LWJGLPainterAWT();
  }

  @Override
  public IViewOverlay newViewOverlay() {
    return null;
  }

  @Override
  public IViewportLayout newViewportLayout() {
    return new ViewAndColorbarsLayout();
  }

  @Override
  public SymbolHandler newSymbolHandler(IImageWrapper image) {
    return null;
  }

  @Override
  public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
    return new LWJGLCanvasAWT(factory, scene, quality);
  }

  @Override
  public IAnimator newAnimator(ICanvas canvas) {
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
    return newFrame(chart, new Rectangle(0, 0, 800, 600), "Jzy3d");
  }

  @Override
  public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
    return new FrameAWT(chart, bounds, title);
  }

  @Override
  public void setChartFactory(IChartFactory factory) {
    this.factory = factory;
  }

  @Override
  public IChartFactory getChartFactory() {
    return factory;
  }

  @Override
  public boolean isOffscreen() {
    return false;
  }

  @Override
  public void setOffscreenDisabled() {
  }

  @Override
  public void setOffscreen(int width, int height) {
  }

  @Override
  public void setOffscreen(Rectangle rectangle) {
  }

  @Override
  public Dimension getOffscreenDimension() {
    return null;
  }

  @Override
  public boolean isDebugGL() {
    return false;
  }

  @Override
  public void setDebugGL(boolean debugGL) {
  }
}
