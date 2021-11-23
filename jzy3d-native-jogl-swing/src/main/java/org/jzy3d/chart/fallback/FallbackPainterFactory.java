package org.jzy3d.chart.fallback;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.factories.AWTPainterFactory;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.rendering.view.AWTImageRenderer3d;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.ui.views.ImagePanel;

public class FallbackPainterFactory extends AWTPainterFactory {
  @Override
  public Renderer3d newRenderer3D(View view) {
    return new AWTImageRenderer3d(view, traceGL, debugGL);
  }

  @Override
  public ICameraMouseController newMouseCameraController(Chart chart) {
    return new AWTCameraMouseController(chart) {
      @Override
      public void register(Chart chart) {
        if (targets == null)
          targets = new ArrayList<Chart>(1);
        targets.add(chart);

        // TODO : CREATE FallbackCanvas wrapping/extending
        // ImagePanel, rather than injecting in FallbackChart
        ImagePanel panel = ((FallbackChart) chart).getImagePanel();
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);
        panel.addMouseWheelListener(this);
      }

      @Override
      public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        chart.render();
      }

      @Override
      public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        chart.render();
      }

      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);
        chart.render();
      }
    };
  }

  /*
   * public IFrame newFrame(Chart chart) { }
   */

  public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
    return new FallbackChartFrameSwing(chart);
  }


  public static String SCREENSHOT_FOLDER = "./data/screenshots/";
}
