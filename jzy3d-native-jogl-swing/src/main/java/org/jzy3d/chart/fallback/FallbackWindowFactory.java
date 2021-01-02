package org.jzy3d.chart.fallback;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Date;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.screenshot.AWTScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController.IScreenshotEventListener;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.factories.AWTPainterFactory;
import org.jzy3d.maths.Utils;
import org.jzy3d.ui.views.ImagePanel;

public class FallbackWindowFactory extends AWTPainterFactory {
    @Override
    public ICameraMouseController newMouseCameraController(Chart chart) {
        //if (chart.getFactory() instanceof OffscreenChartFactory)
            return new AWTCameraMouseController(chart) {
                @Override
                public void register(Chart chart) {
                    // super.register(chart);
                    if (targets == null)
                        targets = new ArrayList<Chart>(1);
                    targets.add(chart);

                    // TODO : CREATE FallbackCanvas wrapping/extending
                    // ImagePanel, rather than injecting in FallbackChart
                    ImagePanel panel = ((FallbackChart) chart).getImagePanel();
                    panel.addMouseListener(this);
                    panel.addMouseMotionListener(this);
                    panel.addMouseWheelListener(this);
                    // was : chart.getCanvas().addMouseController(this);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    // System.out.println("mouse press");
                    chart.render(); // CE MODE DEVRAIT POUVOIR ETRE ACTIVE DANS
                                    // LA CLASSE MERE
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    super.mouseDragged(e);
                    // System.out.println("mouse drag");

                    chart.render(); 

                }
                
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    super.mouseWheelMoved(e);
                    chart.render();
                }
            };
        //else
        //    throw new IllegalArgumentException("Unxpected chart type");
        // return new NewtCameraMouseController(chart);
    }

    @Override
    public IScreenshotKeyController newKeyboardScreenshotController(Chart chart) {
        // trigger screenshot on 's' letter
        String file = SCREENSHOT_FOLDER + "capture-" + Utils.dat2str(new Date(), "yyyy-MM-dd-HH-mm-ss") + ".png";
        IScreenshotKeyController screenshot;

        //if (chart.getCanvas() instanceof CanvasAWT)
            screenshot = new AWTScreenshotKeyController(chart, file);
        //else
        //    screenshot = new NewtScreenshotKeyController(chart, file);

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
}
