package org.jzy3d.javafx;

import java.awt.image.BufferedImage;
import org.apache.log4j.Logger;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.javafx.controllers.keyboard.JavaFXCameraKeyController;
import org.jzy3d.javafx.controllers.mouse.JavaFXCameraMouseController;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.AWTImageRenderer3d.DisplayListener;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@SuppressWarnings("restriction")
/* Disable JavaFX access restriction warnings */
public class JavaFXChartFactory extends AWTChartFactory {
  static Logger LOGGER = Logger.getLogger(JavaFXChartFactory.class);

  public static Chart chart(Quality quality, String toolkit) {
    JavaFXChartFactory f = new JavaFXChartFactory();
    return f.newChart(quality);
  }

  /* ******************************************** */

  public JavaFXChartFactory() {
    super(new JavaFXWindowFactory());
  }

  public JavaFXChartFactory(IPainterFactory windowFactory) {
    super(windowFactory);
  }


  public Image getScreenshotAsJavaFXImage(AWTNativeChart chart) {
    chart.screenshot();
    AWTRenderer3d renderer = (AWTRenderer3d) ((INativeCanvas) chart.getCanvas()).getRenderer();
    BufferedImage i = renderer.getLastScreenshotImage();
    if (i != null) {
      Image image = SwingFXUtils.toFXImage(i, null);
      return image;
    } else {
      // LOGGER.error(this.getClass() + " SCREENSHOT NULL");
      return null;
    }
  }

  /**
   * Return an {@link ImageView} from an {@link AWTChart} expected to render offscreen and to use a
   * {@link JavaFXRenderer3d} poping Images when the chart is redrawn.
   * 
   * @param chart
   * @return
   */
  public ImageView bindImageView(AWTNativeChart chart) {
    ImageView imageView = new ImageView();
    imageView.fitHeightProperty();
    imageView.fitWidthProperty();

    bind(imageView, chart);

    // Initialize imageView
    Image image = getScreenshotAsJavaFXImage(chart);
    if (image != null) {
      System.out.println("setting image at init");
      imageView.setImage(image);
    } else {
      // LOGGER.error("image is null at init");
    }

    JavaFXCameraMouseController jfxMouse =
        (JavaFXCameraMouseController) chart.addMouseCameraController();
    jfxMouse.setNode(imageView);
    // JavaFXNodeMouse.makeDraggable(stage, imgView);

    JavaFXCameraKeyController jfxKey =
        (JavaFXCameraKeyController) chart.addKeyboardCameraController();
    jfxKey.setNode(imageView);
    imageView.setFocusTraversable(true);
    return imageView;
  }

  /* ########################################### */

  /**
   * Register for renderer notifications with a new JavaFX Image
   */
  public void bind(final ImageView imageView, AWTChart chart) {
    if (!(((INativeCanvas) chart.getCanvas()).getRenderer() instanceof JavaFXRenderer3d)) {
      LOGGER.error("NOT BINDING IMAGE VIEW TO CHART AS NOT A JAVAFX RENDERER");
      return;
    }

    // Set listener on renderer to update imageView
    JavaFXRenderer3d renderer =
        (JavaFXRenderer3d) ((INativeCanvas) chart.getCanvas()).getRenderer();
    renderer.addDisplayListener(new DisplayListener() {
      @Override
      public void onDisplay(Object image) {
        if (image != null) {
          imageView.setImage((javafx.scene.image.Image) image);
        } else {
          LOGGER.error("image is null while listening to renderer");
        }
      }
    });
  }

  public void addSceneSizeChangedListener(Chart chart, Scene scene) {
    scene.widthProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth,
          Number newSceneWidth) {
        // System.out.println("scene Width: " + newSceneWidth);
        resetTo(chart, scene.widthProperty().get(), scene.heightProperty().get());
        // System.out.println("resize ok");
      }
    });
    scene.heightProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight,
          Number newSceneHeight) {
        // System.out.println("scene Height: " + newSceneHeight);
        resetTo(chart, scene.widthProperty().get(), scene.heightProperty().get());
        // System.out.println("resize ok");
      }
    });
  }

  protected void resetTo(Chart chart, double width, double height) {
    if (chart.getCanvas() instanceof OffscreenCanvas) {
      OffscreenCanvas canvas = (OffscreenCanvas) chart.getCanvas();

      // System.out.println("will init");
      canvas.initBuffer(canvas.getCapabilities(), (int) width, (int) height);
      // LOGGER.error("done initBuffer");
      chart.render();
      // LOGGER.error("done render");
    } else {
      LOGGER.error("NOT AN OFFSCREEN CANVAS!");
    }
  }
}
