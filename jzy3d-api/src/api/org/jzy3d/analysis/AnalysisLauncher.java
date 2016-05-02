package org.jzy3d.analysis;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.Settings;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.utils.LoggerUtils;

public class AnalysisLauncher {
    /** Opens a demo with mouse/key/thread controllers for viewpoint change. */
    public static void open(IAnalysis demo) throws Exception {
        LoggerUtils.minimal();
        open(demo, DEFAULT_WINDOW);
    }

    public static void open(IAnalysis demo, Rectangle rectangle) throws Exception {
        Settings.getInstance().setHardwareAccelerated(true);
        demo.init();
        Chart chart = demo.getChart();

        System.out.println(demo.getPitch());
        System.out.println("------------------------------------");

        ChartLauncher.instructions();
        ChartLauncher.openChart(chart, rectangle, demo.getName());
        // ChartLauncher.screenshot(demo.getChart(),
        // "./data/screenshots/"+demo.getName()+".png");
    }

    /**
     * Opens an analysis without pluging mouse & key listeners
     * 
     * @param demo
     * @throws Exception
     */
    public static void openStatic(IAnalysis demo) throws Exception {
        openStatic(demo, DEFAULT_WINDOW);
    }

    public static void openStatic(IAnalysis demo, Rectangle rectangle) throws Exception {
        Settings.getInstance().setHardwareAccelerated(true);
        demo.init();
        Chart chart = demo.getChart();

        ChartLauncher.openStaticChart(chart, rectangle, demo.getName());
        ChartLauncher.screenshot(demo.getChart(), "./data/screenshots/" + demo.getName() + ".png");
    }

    protected static String DEFAULT_CANVAS_TYPE = "awt";
    protected static Rectangle DEFAULT_WINDOW = new Rectangle(200, 200, 600, 600);
}
