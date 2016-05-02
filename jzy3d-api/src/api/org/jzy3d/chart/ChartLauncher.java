package org.jzy3d.chart;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.maths.Rectangle;

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class ChartLauncher {
    static Logger logger = Logger.getLogger(ChartLauncher.class);
    
    public static String SCREENSHOT_FOLDER = "./data/screenshots/";
    
    public static ICameraMouseController openChart(Chart chart) {
        return openChart(chart, new Rectangle(0, 0, 800, 600), "Jzy3d", true);
    }

    public static ICameraMouseController openChart(Chart chart, Rectangle rectangle) {
        return openChart(chart, rectangle, "Jzy3d", true);
    }

    public static ICameraMouseController openChart(Chart chart, String title) {
        return openChart(chart, new Rectangle(0, 0, 800, 600), title, true);
    }

    public static ICameraMouseController openChart(Chart chart, Rectangle bounds, String title) {
        return openChart(chart, bounds, title, true);
    }

    public static ICameraMouseController openChart(Chart chart, Rectangle bounds, String title, boolean allowSlaveThreadOnDoubleClick) {
        return openChart(chart, bounds, title, allowSlaveThreadOnDoubleClick, false);
    }

    public static ICameraMouseController openChart(final Chart chart, Rectangle bounds, final String title, boolean allowSlaveThreadOnDoubleClick, boolean startThreadImmediatly) {
        ICameraMouseController mouse = configureControllers(chart, title, allowSlaveThreadOnDoubleClick, startThreadImmediatly);
        chart.render();
        frame(chart, bounds, title);
        return mouse;
    }

    public static ICameraMouseController configureControllers(final Chart chart, final String title, boolean allowSlaveThreadOnDoubleClick, boolean startThreadImmediatly) {
        chart.addKeyController();
        chart.addScreenshotKeyController();
        return chart.addMouseController();
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

    
    /* FRAMES */
    
    /*public static void frame(Chart chart){
    	Screen screen = Settings.getInstance().getScreen();
        frame(chart, new Rectangle(0,0,screen.getWidth(), screen.getHeight()), "Jzy3d");
    }*/
    
    public static void frame(Chart chart, Rectangle bounds, String title){
    	chart.getFactory().newFrame(chart, bounds, title);
    }
    
    /* SCREENSHOT */

    public static void screenshot(Chart chart, String filename) throws IOException {
        File output = new File(filename);
        if (!output.getParentFile().exists())
            output.mkdirs();
        TextureData screen = chart.screenshot();
        if(screen!=null){
        	TextureIO.write(screen, new File(filename));    
	        logger.info("Dumped screenshot in: " + filename);
        }
        else{
        	logger.error("screenshot not available");
        }
    }
}
