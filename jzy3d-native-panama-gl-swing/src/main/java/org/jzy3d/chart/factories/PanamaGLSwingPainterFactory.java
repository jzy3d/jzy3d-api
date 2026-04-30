/*******************************************************************************
 * Copyright (c) 2022, 2023 Martin Pernollet & contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 *******************************************************************************/
package org.jzy3d.chart.factories;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.AWTScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.PanamaGLPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.PanamaGLSwingCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import panamagl.canvas.GLCanvasSwing;
import panamagl.opengl.GLContext;

public class PanamaGLSwingPainterFactory extends APanamaGLPainterFactory {

  @Override
  public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
    GLCanvasSwing glCanvas = new GLCanvasSwing(panamaGLFactory);
    GLContext context = glCanvas.getContext();

    PanamaGLSwingCanvas icanvas = new PanamaGLSwingCanvas(factory, scene, quality, glCanvas);

    // Update painter with context
    ((PanamaGLPainter) icanvas.getView().getPainter()).setContext(context);
    return icanvas;
  }

  @Override
  public ICameraMouseController newMouseCameraController(Chart chart) {
    return new AWTCameraMouseController(chart);
  }

  @Override
  public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public ICameraKeyController newKeyboardCameraController(Chart chart) {
    return new AWTCameraKeyController(chart);
  }

  @Override
  public IScreenshotKeyController newKeyboardScreenshotController(Chart chart) {
    return new AWTScreenshotKeyController(chart, "target/out.png");
  }

  @Override
  public IFrame newFrame(Chart chart) {
    return newFrame(chart, new Rectangle(0, 0, 800, 600), "PanamaGL");
  }

  @Override
  public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
    FrameSwing f = new FrameSwing();
    f.initialize(chart, bounds, title);
    return f;
  }
}
