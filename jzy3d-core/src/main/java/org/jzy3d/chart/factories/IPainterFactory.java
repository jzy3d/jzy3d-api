package org.jzy3d.chart.factories;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.IAnimator;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
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

/**
 * An {@link IPainterFactory} provides all Windowing toolkit dependent objects. It is a sub factory
 * of {@link IChartFactory} and hence allows porting charts to any windowing toolkit by setting
 * {@link IChartFactory#setPainterFactory(IPainterFactory)}.
 * 
 * The two main {@link IPainterFactory} are {@link NativePainterFactory} and
 * {@link EmulGLPainterFactory} which enable a {@link IChartFactory} for native or software
 * rendering. Each of these factories may be further overriden to cover a specific windowind toolkit
 * (AWT, NEWT, etc).
 * 
 * The {@link IPainterFactory} provides the following objects :
 * 
 * The {@link IPainter} itself allows flipping between native rendering
 * ({@link NativeDesktopPainter}) and software rendering ({@link EmulGLPainter}).
 * 
 * Following interfaces allows flipping between native or software AWT, NEWT, SWT, Swing, JavaFX:
 * <ul>
 * <li>{@link IViewOverlay}
 * <li>{@link ICanvas}
 * <li>{@link IFrame}
 * <li>Controllers : {@link IMousePickingController}, {@link ICameraKeyController},
 * {@link IScreenshotKeyController}.
 * </ul>
 * 
 * The {@link IAnimator} allows flipping between native or software continuous rendering.
 * 
 * @author Martin Pernollet
 */
public interface IPainterFactory {

  public IPainter newPainter();

  public IViewOverlay newViewOverlay();

  public IViewportLayout newViewportLayout();

  public SymbolHandler newSymbolHandler(IImageWrapper image);

  public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality);

  public IAnimator newAnimator(ICanvas canvas);

  public ICameraMouseController newMouseCameraController(Chart chart);

  public IMousePickingController newMousePickingController(Chart chart, int clickWidth);

  public ICameraKeyController newKeyboardCameraController(Chart chart);

  public IScreenshotKeyController newKeyboardScreenshotController(Chart chart);

  public IFrame newFrame(Chart chart);

  public IFrame newFrame(Chart chart, Rectangle bounds, String title);

  public void setChartFactory(IChartFactory factory);

  public IChartFactory getChartFactory();


  public boolean isOffscreen();

  public void setOffscreenDisabled();

  /**
   * The size of the offscreen chart dimension.
   * 
   * Native implementation will really make the chart offscreen. EmulGL will configure the chart for
   * offscreen rendering but won't prevent to open the chart. Note that if the chart is opened on an
   * HiDPI screen, further screenshot may be bigger than expected due to pixel scale capabilities
   * discovered only once the component is rendered on a screen.
   * 
   * @param width
   * @param height
   */
  public void setOffscreen(int width, int height);

  public void setOffscreen(Rectangle rectangle);

  public Dimension getOffscreenDimension();

  public boolean isDebugGL();

  /**
   * If true, will let GL trigger {@link GLException} if an error occur in OpenGL which ease
   * debugging. Default is false.
   */
  public void setDebugGL(boolean debugGL);

}
