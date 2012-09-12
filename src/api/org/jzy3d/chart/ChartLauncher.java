package org.jzy3d.chart;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.bridge.swing.FrameSwing;
import org.jzy3d.chart.controllers.keyboard.camera.ScreenshotKeyListener;
import org.jzy3d.chart.controllers.keyboard.camera.ScreenshotKeyListener.IScreenshotEventListener;
import org.jzy3d.chart.controllers.mouse.camera.CameraMouseController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.plot3d.primitives.enlightables.AbstractEnlightable;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewt;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.ui.editors.LightEditor;
import org.jzy3d.ui.editors.MaterialEditor;
import org.jzy3d.ui.views.ImagePanel;

public class ChartLauncher {
    public static String SCREENSHOT_FOLDER = "./data/screenshots/";
    
    public static CameraMouseController openChart(Chart chart) {
        return openChart(chart, new Rectangle(0, 0, 800, 600), "Jzy3d", true);
    }

    public static CameraMouseController openChart(Chart chart, Rectangle rectangle) {
        return openChart(chart, rectangle, "Jzy3d", true);
    }

    public static CameraMouseController openChart(Chart chart, String title) {
        return openChart(chart, new Rectangle(0, 0, 800, 600), title, true);
    }

    public static CameraMouseController openChart(Chart chart, Rectangle bounds, String title) {
        return openChart(chart, bounds, title, true);
    }

    public static CameraMouseController openChart(Chart chart, Rectangle bounds, String title, boolean allowSlaveThreadOnDoubleClick) {
        return openChart(chart, bounds, title, allowSlaveThreadOnDoubleClick, false);
    }

    public static CameraMouseController openChart(final Chart chart, Rectangle bounds, final String title, boolean allowSlaveThreadOnDoubleClick, boolean startThreadImmediatly) {
        CameraMouseController mouse = configureControllers(chart, title, allowSlaveThreadOnDoubleClick, startThreadImmediatly);
        chart.render();
        frame(chart, bounds, title);
        return mouse;
    }

    public static CameraMouseController configureControllers(final Chart chart, final String title, boolean allowSlaveThreadOnDoubleClick, boolean startThreadImmediatly) {
        // Setup chart controllers and listeners
        CameraMouseController mouse = new CameraMouseController();
        /*
         * mouse.addControllerEventListener(new ControllerEventListener(){
         * public void controllerEventFired(ControllerEvent e) {
         * if(e.getType()==ControllerType.ROTATE){
         * //System.out.println("Mouse[ROTATE]:" + (Coord3d)e.getValue()); } }
         * });
         */
        chart.addController(mouse);

        if (allowSlaveThreadOnDoubleClick) {
            CameraThreadController thread = new CameraThreadController();
            mouse.addSlaveThreadController(thread);
            chart.addController(thread);
            if (startThreadImmediatly)
                thread.start();
        }

        // trigger screenshot on 's' letter
        ScreenshotKeyListener listener = new ScreenshotKeyListener(chart, SCREENSHOT_FOLDER + title + ".png");
        listener.addListener(new IScreenshotEventListener() {
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
        chart.getCanvas().addKeyListener(listener);
        return mouse;
    }

    public static void openStaticChart(Chart chart) {
        openStaticChart(chart, new Rectangle(0, 0, 800, 600), "Jzy3d");
    }

    public static void openStaticChart(Chart chart, Rectangle bounds, String title) {
        chart.render();
        frame(chart, bounds, title);
    }

    public static void instructions() {
        System.out.println(makeInstruction());
        System.out.println("------------------------------------");
    }

    public static String makeInstruction() {
        StringBuffer sb = new StringBuffer();
        sb.append("Rotate     : Left click and drag mouse\n");
        sb.append("Scale      : Roll mouse wheel\n");
        sb.append("Z Shift    : Right click and drag mouse\n");
        sb.append("Animate    : Double left click\n");
        sb.append("Screenshot : Press 's'\n");
        return sb.toString();
    }

    /*******************************************************/
    
    public static void openLightEditors(Chart chart) {
        // Material editor
        MaterialEditor enlightableEditor = new MaterialEditor(chart);
        if(chart.getScene().getGraph().getAll().get(0) instanceof AbstractEnlightable)
            enlightableEditor.setTarget((AbstractEnlightable)chart.getScene().getGraph().getAll().get(0));
        LightEditor lightEditor = new LightEditor(chart);
        lightEditor.setTarget(chart.getScene().getLightSet().get(0));
        
        // Windows
        ChartLauncher.openPanel(lightEditor, new Rectangle(0,0,200,900), "Light");
        ChartLauncher.openPanel(enlightableEditor, new Rectangle(200,0,200,675), "Material");
    }
    
    /*******************************************************/

    public static void openImagePanel(Image image) {
        openImagePanel(image, new Rectangle(0, 800, 600, 600));
    }

    public static void openImagePanel(Image image, Rectangle bounds) {
        ImagePanel panel = new ImagePanel(image);
        JFrame frame = new JFrame();
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setBounds(bounds);
        frame.setVisible(true);
    }

    public static void openPanel(JPanel panel, Rectangle bounds, String title) {
        JFrame frame = new JFrame(title);
        Container content = frame.getContentPane();
        // content.setBackground(Color.white);
        content.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setBounds(bounds);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });
    }
    
    /* FRAMES */
    
    public static void frame(Chart chart){
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame(chart, new Rectangle(0,0,screen.width, screen.height), "Jzy3d");
    }
    
    public static void frame(Chart chart, Rectangle bounds, String title){
        Object canvas = chart.getCanvas();
        
        if(canvas instanceof CanvasAWT)
            new FrameAWT(chart, bounds, title); // FrameSWT works as well
        else if(canvas instanceof CanvasNewt)
            new FrameAWT(chart, bounds, title); // FrameSWT works as well
        else if(canvas instanceof CanvasSwing)
            new FrameSwing(chart, bounds, title);
        else
            throw new RuntimeException("No default frame could be found for the given Chart canvas: " + canvas.getClass());
    }
    
    /* SCREENSHOT */

    public static void screenshot(Chart chart, String filename) throws IOException {
        File output = new File(filename);
        if (!output.getParentFile().exists())
            output.mkdirs();
        ImageIO.write(chart.screenshot(), "png", output);
        System.out.println("Dumped screenshot in: " + filename);
    }
}
