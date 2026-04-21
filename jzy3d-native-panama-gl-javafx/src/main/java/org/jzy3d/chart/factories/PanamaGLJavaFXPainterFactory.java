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
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.javafx.controllers.keyboard.JavaFXCameraKeyController;
import org.jzy3d.javafx.controllers.mouse.JavaFXCameraMouseController;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.PanamaGLPainter;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.PanamaGLJavaFXCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

public class PanamaGLJavaFXPainterFactory extends APanamaGLPainterFactory {

  @Override
  public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
    PanamaGLJavaFXCanvas canvas =
        new PanamaGLJavaFXCanvas(factory, scene, quality, panamaGLFactory);

    // Update painter with the GL context created by GLCanvasJFX.
    ((PanamaGLPainter) canvas.getView().getPainter()).setContext(canvas.getGLCanvas().getContext());
    return canvas;
  }

  @Override
  public ICameraMouseController newMouseCameraController(Chart chart) {
    PanamaGLJavaFXCanvas canvas = (PanamaGLJavaFXCanvas) chart.getCanvas();
    return new JavaFXCameraMouseController(chart, canvas.getNode());
  }

  @Override
  public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
    throw new NotImplementedException();
  }

  @Override
  public ICameraKeyController newKeyboardCameraController(Chart chart) {
    PanamaGLJavaFXCanvas canvas = (PanamaGLJavaFXCanvas) chart.getCanvas();
    return new JavaFXCameraKeyController(chart, canvas.getNode());
  }

  @Override
  public IScreenshotKeyController newKeyboardScreenshotController(Chart chart) {
    throw new NotImplementedException();
  }

  /**
   * A JavaFX backend has no toolkit-agnostic frame concept: a JavaFX application owns the
   * {@code Stage} lifecycle. Embed the chart canvas in a {@code javafx.scene.Scene} from your
   * {@code javafx.application.Application} subclass instead of calling {@code chart.open(...)}.
   */
  @Override
  public IFrame newFrame(Chart chart) {
    return newFrame(chart, new Rectangle(0, 0, 800, 600), "PanamaGL");
  }

  @Override
  public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
    throw new NotImplementedException(
        "JavaFX backend does not provide an IFrame implementation. Embed the chart canvas "
            + "into a javafx.scene.Scene from your Application subclass. See demos in "
            + "jzy3d-tutorials-panama-gl-javafx.");
  }
}
