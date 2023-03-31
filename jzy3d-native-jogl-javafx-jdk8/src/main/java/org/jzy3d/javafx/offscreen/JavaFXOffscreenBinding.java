package org.jzy3d.javafx.offscreen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.javafx.controllers.keyboard.JavaFXCameraKeyController;
import org.jzy3d.javafx.controllers.mouse.JavaFXCameraMouseController;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.view.AWTImageRenderer3d.DisplayListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;

public class JavaFXOffscreenBinding {
  protected static Logger LOGGER = LogManager.getLogger(JavaFXOffscreenChartFactory.class);

  
  /* ********************************************************** */
  /* GET A JAVAFX IMAGE VIEW BINDED TO CHART
  /* ********************************************************** */

  /**
   * Return an {@link ImageView} from an {@link AWTChart} expected to render offscreen and to use a
   * {@link JavaFXOffscreenRenderer3d} poping Images when the chart is redrawn.
   * 
   * Also attaches a {@link JavaFXCameraMouseController} and {@link JavaFXCameraKeyController} to the 
   * returned {@link ImageView}.
   * 
   * @param chart
   * @return an {@link ImageView} displaying the chart content interactively
   */
  public ImageView bindImageView(AWTNativeChart chart) {
    ImageView imageView = new ImageView();
    //imageView.fitHeightProperty();
    //imageView.fitWidthProperty();

    bind(imageView, chart);

    // Initialize imageView
    /*Image image = getScreenshotAsJavaFXImage(chart);
    if (image != null) {
      System.out.println("setting image at init");
      imageView.setImage(image);
    } else {
      // LOGGER.error("image is null at init");
    }*/

    JavaFXCameraMouseController jfxMouse = (JavaFXCameraMouseController) chart.addMouse();
    jfxMouse.setNode(imageView);
    // JavaFXNodeMouse.makeDraggable(stage, imgView);

    JavaFXCameraKeyController jfxKey = (JavaFXCameraKeyController) chart.addKeyboard();
    jfxKey.setNode(imageView);
    imageView.setFocusTraversable(true);
    return imageView;
  }


  /**
   * Register for renderer notifications with a new JavaFX Image
   *
   * @param imageView
   * @param chart
   */
  protected void bind(final ImageView imageView, AWTChart chart) {

    // Downcast renderer
    JavaFXOffscreenRenderer3d renderer = getRenderer(chart);

    // Set listener on renderer to update imageView
    renderer.addDisplayListener(new DisplayListener() {
      @Override
      public void onDisplay(Object image) {
        if (image != null) {
          javafx.scene.image.Image img = (javafx.scene.image.Image) image;

          //System.out.println("JavaFXOffscreenBinding.bind receives an image of size "
          //    + img.getWidth() + " x " + img.getHeight());

          //imageView.setStyle("-fx-background-color: WHITE");
          imageView.setImage(img);

        } else {
          LOGGER.error("image is null while listening to renderer");
        }
      }
    });
  }

  protected JavaFXOffscreenRenderer3d getRenderer(AWTChart chart) {
    return (JavaFXOffscreenRenderer3d) ((INativeCanvas) chart.getCanvas()).getRenderer();
  }
  
  /* ********************************************************** */
  /* A RESIZE LISTENER
  /* ********************************************************** */


  /**
   * Listen to scene size change in order to reset offscreen chart dimensions.
   * 
   * @param chart
   * @param scene
   */
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

      //System.out.println("JavaFXOffscreenChartFactory.resetTo " + width + " x " + height);
      //canvas.initBuffer(canvas.getCapabilities(), (int) width, (int) height);
      canvas.resize((int) width, (int) height);
      
    } else {
      throw new RuntimeException("Expect the chart to have an offscreen canvas. Here we have a " + chart.getCanvas());
    }
  }
}
