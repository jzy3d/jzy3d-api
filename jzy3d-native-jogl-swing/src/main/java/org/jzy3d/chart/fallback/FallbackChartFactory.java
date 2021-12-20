package org.jzy3d.chart.fallback;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.events.IViewPointChangedListener;
import org.jzy3d.events.ViewPointChangedEvent;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.AWTImageRenderer3d;
import org.jzy3d.plot3d.rendering.view.AWTImageRenderer3d.DisplayListener;
import org.jzy3d.ui.views.ImagePanel;

public class FallbackChartFactory extends AWTChartFactory {
  static Logger LOGGER = LogManager.getLogger(FallbackChartFactory.class);

  @Override
  public FallbackChart newChart(IChartFactory factory, Quality quality) {
    return new FallbackChart(factory, quality);
  }

  public FallbackChartFactory() {
    this(new FallbackPainterFactory());
  }

  public FallbackChartFactory(IPainterFactory factory) {
    super(factory);
    getPainterFactory().setOffscreen(600, 600);
  }

  /* ########################################### */

  /**
   * Register for renderer notifications so that new {@link BufferedImage} are sent to
   * {@link ImagePanel}
   */
  public static void bind(final ImagePanel imageView, AWTChart chart) {
    if (!(((INativeCanvas) chart.getCanvas()).getRenderer() instanceof AWTImageRenderer3d)) {
      LOGGER.error("NOT BINDING IMAGE VIEW TO CHART AS NOT A AWTImageRenderer3d RENDERER");
      return;
    }

    // Set listener on renderer to update imageView
    AWTImageRenderer3d renderer =
        (AWTImageRenderer3d) ((INativeCanvas) chart.getCanvas()).getRenderer();
    renderer.addDisplayListener(new DisplayListener() {
      @Override
      public void onDisplay(Object image) {
        if (image != null) {
          imageView.setImage((java.awt.Image) image);

          /*
           * try { ImageIO.write((BufferedImage)image, "png", new
           * File("data/screenshots/fallback.png")); } catch (IOException e) { e.printStackTrace();
           * }
           */

          // Obligatoire pour que l'image soit rafraichie.
          ((IFallbackChart) chart).getImagePanel().repaint();
        } else {
          LOGGER.error("image is null while listening to renderer");
        }
      }
    });

    // Set listener on view to update imageView
    chart.getView().addViewPointChangedListener(new IViewPointChangedListener() {

      @Override
      public void viewPointChanged(ViewPointChangedEvent e) {

      }
    });

    // imageView.setFocusable(true);
    // imageView.setF

    // imageView.setImage((java.awt.Image)renderer.getLastScreenshotImage());
  }

  public static void addPanelSizeChangedListener(ImagePanel panel, Chart chart) {
    panel.addComponentListener(new ComponentListener() {
      @Override
      public void componentResized(ComponentEvent e) {
        Component c = e.getComponent();
        resetTo(chart, c);
      }

      @Override
      public void componentMoved(ComponentEvent e) {}

      @Override
      public void componentShown(ComponentEvent e) {}

      @Override
      public void componentHidden(ComponentEvent e) {}
    });
  }

  protected static void resetTo(Chart chart, Component c) {
    int height = c.getHeight();
    int width = c.getWidth();
    resetTo(chart, width, height);
  }

  protected static void resetTo(Chart chart, double width, double height) {
    if (height < 1 || width < 1) {
      // LOGGER.error("resetTo : width=" + width + "height=" + height);
      return;
    } else {
      // LOGGER.info("resetTo : width=" + width + "height=" + height);
    }

    if (chart.getCanvas() instanceof OffscreenCanvas) {
      OffscreenCanvas canvas = (OffscreenCanvas) chart.getCanvas();
      canvas.initBuffer(canvas.getCapabilities(), (int) width, (int) height);
      chart.render();
    } else {
      LOGGER.error("NOT AN OFFSCREEN CANVAS!");
    }
  }
}
