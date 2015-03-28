package org.jzy3d.javafx;

import java.awt.image.BufferedImage;
import java.util.Date;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.NewtCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.AWTScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController.IScreenshotEventListener;
import org.jzy3d.chart.controllers.keyboard.screenshot.NewtScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.javafx.JavaFXRenderer3d.DisplayListener;
import org.jzy3d.javafx.controllers.JavaFXCameraMouseController;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

public class JavaFXChartFactory extends AWTChartComponentFactory {
    
    public static Chart chart(Quality quality, String toolkit) {
        JavaFXChartFactory f = new JavaFXChartFactory();
        return f.newChart(quality, toolkit);
    }

    public Image getScreenshotAsJavaFXImage(AWTChart chart) {
        chart.screenshot();
        AWTRenderer3d renderer = (AWTRenderer3d) chart.getCanvas().getRenderer();
        BufferedImage i = renderer.getLastScreenshotImage();
        if (i != null) {
            Image image = SwingFXUtils.toFXImage(i, null);
            return image;
        } else {
            //System.err.println(this.getClass() + " SCREENSHOT NULL");
            return null;
        }
    }

    /**
     * Return an {@link ImageView} from an {@link AWTChart} expected to render offscreen 
     * and to use a {@link JavaFXRenderer3d} poping Images when the chart is redrawn.
     * @param chart
     * @return
     */
    public ImageView bindImageView(AWTChart chart) {
        ImageView imageView = new ImageView();
        imageView.fitHeightProperty();
        imageView.fitWidthProperty();

        bind(imageView, chart);

        // Initialize imageView
        Image image = getScreenshotAsJavaFXImage(chart);
        if (image != null) {
            System.out.println("setting image at init");
            imageView.setImage(image);
        } else{
            //System.err.println("image is null at init");
        }
            
        JavaFXCameraMouseController jfxMouse = (JavaFXCameraMouseController) chart.addMouseController();
        jfxMouse.setNode(imageView);
        // JavaFXNodeMouse.makeDraggable(stage, imgView);
        return imageView;
    }


    /* ########################################### */

    /**
     * Register for renderer notifications with a new JavaFX Image
     */
    public void bind(final ImageView imageView, AWTChart chart) {
        if (!(chart.getCanvas().getRenderer() instanceof JavaFXRenderer3d)) {
            System.err.println("NOT BINDING IMAGE VIEW TO CHART AS NOT A JAVAFX RENDERER");
            return;
        }

        // Set listener on renderer to update imageView
        JavaFXRenderer3d renderer = (JavaFXRenderer3d) chart.getCanvas().getRenderer();
        renderer.addDisplayListener(new DisplayListener() {
            @Override
            public void onDisplay(Image image) {
                if (image != null) {
                    imageView.setImage(image);
                } else {
                    System.err.println("image is null while listening to renderer");
                }
            }
        });
    }

    public void addSceneSizeChangedListener(Chart chart, Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                //System.out.println("scene Width: " + newSceneWidth);
                resetTo(chart, scene.widthProperty().get(), scene.heightProperty().get());
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                //System.out.println("scene Height: " + newSceneHeight);
                resetTo(chart, scene.widthProperty().get(), scene.heightProperty().get());
            }
        });
    }

    protected void resetTo(Chart chart, double width, double height) {
        if (chart.getCanvas() instanceof OffscreenCanvas) {
            OffscreenCanvas canvas = (OffscreenCanvas) chart.getCanvas();
            canvas.initGLPBuffer(canvas.getCapabilities(), (int) width, (int) height);
            chart.render();
        } else {
            System.err.println("NOT AN OFFSCREEN CANVAS!");
        }
    }

    /* ################################################# */

    @Override
    public Renderer3d newRenderer(View view, boolean traceGL, boolean debugGL) {
        return new JavaFXRenderer3d(view, traceGL, debugGL);
    }

    /* ################################################# */

    @Override
    public ICameraMouseController newMouseController(Chart chart) {
        ICameraMouseController mouse = new JavaFXCameraMouseController(chart, null);
        return mouse;
    }

    @Override
    public IScreenshotKeyController newScreenshotKeyController(Chart chart) {
        // trigger screenshot on 's' letter
        String file = SCREENSHOT_FOLDER + "capture-" + Utils.dat2str(new Date(), "yyyy-MM-dd-HH-mm-ss") + ".png";
        IScreenshotKeyController screenshot;

        if (!chart.getWindowingToolkit().equals("newt"))
            screenshot = new AWTScreenshotKeyController(chart, file);
        else
            screenshot = new NewtScreenshotKeyController(chart, file);

        screenshot.addListener(new IScreenshotEventListener() {
            @Override
            public void failedScreenshot(String file, Exception e) {
                System.out.println("Failed to save screenshot:");
                e.printStackTrace();
            }

            @Override
            public void doneScreenshot(String file) {
                System.out.println("Screenshot: " + file);
            }
        });
        return screenshot;
    }

    public static String SCREENSHOT_FOLDER = "./data/screenshots/";

    @Override
    public ICameraKeyController newKeyController(Chart chart) {
        ICameraKeyController key = null;
        if (!chart.getWindowingToolkit().equals("newt"))
            key = new AWTCameraKeyController(chart);
        else
            key = new NewtCameraKeyController(chart);
        return key;
    }
}
