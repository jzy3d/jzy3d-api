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

import java.util.Date;
import org.eclipse.swt.widgets.Composite;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.SWTCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController.IScreenshotEventListener;
import org.jzy3d.chart.controllers.keyboard.screenshot.SWTScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.SWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Utils;
import org.jzy3d.painters.PanamaGLPainter;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.PanamaGLSWTCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PanamaGLSWTPainterFactory extends APanamaGLPainterFactory {

  public static String SCREENSHOT_FOLDER = "./data/screenshots/";
  static Logger logger = LoggerFactory.getLogger(PanamaGLSWTPainterFactory.class);

  @Override
  public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
    Composite parent = ((ISWTChartFactory) factory).getComposite();
    PanamaGLSWTCanvas canvas =
        new PanamaGLSWTCanvas(factory, scene, quality, parent, panamaGLFactory);

    // Update painter with the GL context created by GLCanvasSWT.
    ((PanamaGLPainter) canvas.getView().getPainter()).setContext(canvas.getGLCanvas().getContext());
    return canvas;
  }

  @Override
  public ICameraMouseController newMouseCameraController(Chart chart) {
    return new SWTCameraMouseController(chart);
  }

  @Override
  public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
    throw new NotImplementedException(
        "SWT mouse picking controller is not yet implemented for PanamaGL.");
  }

  @Override
  public ICameraKeyController newKeyboardCameraController(Chart chart) {
    return new SWTCameraKeyController(chart);
  }

  /**
   * Output file of screenshot can be configured using
   * {@link IScreenshotKeyController#setFilename(String)}.
   */
  @Override
  public IScreenshotKeyController newKeyboardScreenshotController(Chart chart) {
    String file =
        SCREENSHOT_FOLDER + "capture-" + Utils.dat2str(new Date(), "yyyy-MM-dd-HH-mm-ss") + ".png";
    IScreenshotKeyController screenshot = new SWTScreenshotKeyController(chart, file);

    screenshot.addListener(new IScreenshotEventListener() {
      @Override
      public void failedScreenshot(String file, Exception e) {
        logger.error("Failed to save screenshot to '" + file + "'", e);
      }

      @Override
      public void doneScreenshot(String file) {
        logger.info("Screenshot save to '" + file + "'");
      }
    });
    return screenshot;
  }

  /**
   * The SWT backend has no toolkit-agnostic frame concept: an SWT application owns the
   * {@code Shell} lifecycle. Embed the chart canvas in a parent {@code Composite} from your own
   * SWT entry point instead of calling {@code chart.open(...)}.
   */
  @Override
  public IFrame newFrame(Chart chart) {
    return newFrame(chart, new Rectangle(0, 0, 800, 600), "PanamaGL");
  }

  @Override
  public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
    throw new NotImplementedException(
        "SWT backend does not provide an IFrame implementation. Embed the chart canvas into a "
            + "parent SWT Composite from your own entry point. See demos in "
            + "jzy3d-tutorials-panama-gl-swt.");
  }
}
