package org.jzy3d.analysis;

import java.awt.Component;
import java.awt.Rectangle;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jzy3d.bridge.swt.Bridge;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.global.Settings;
import org.jzy3d.utils.LoggerUtils;


public class AnalysisLauncher {	
    /** Opens a demo with mouse/key/thread controllers for viewpoint change. */
	public static void open(IAnalysis demo) throws Exception{
	    LoggerUtils.minimal();
		open(demo, DEFAULT_WINDOW);
	}
	
	public static void open(IAnalysis demo, Rectangle rectangle) throws Exception{
		Settings.getInstance().setHardwareAccelerated(true);
		demo.init();
		Chart chart = demo.getChart();
		
		ChartLauncher.instructions();
		ChartLauncher.openChart(chart, rectangle, demo.getName());
		//ChartLauncher.screenshot(demo.getChart(), "./data/screenshots/"+demo.getName()+".png");
	}
	
	/**
	 * Opens an analysis without pluging mouse & key listeners
	 * @param demo
	 * @throws Exception
	 */
	public static void openStatic(IAnalysis demo) throws Exception{
		openStatic(demo, DEFAULT_WINDOW);
	}
	
    public static void openStatic(IAnalysis demo, Rectangle rectangle) throws Exception{
		Settings.getInstance().setHardwareAccelerated(true);
		demo.init();
		Chart chart = demo.getChart();
		
		ChartLauncher.openStaticChart(chart, rectangle, demo.getName());
		ChartLauncher.screenshot(demo.getChart(), "./data/screenshots/"+demo.getName()+".png");
	}
	
	public static void openStaticSWT(IAnalysis demo) throws Exception{
		Settings.getInstance().setHardwareAccelerated(true);
		Chart chart = demo.getChart();
		
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Bridge.adapt(shell, (Component) chart.getCanvas());
		
		shell.setText(demo.getName());
		shell.setSize(800, 600);
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	protected static String DEFAULT_CANVAS_TYPE = "awt";
	protected static Rectangle DEFAULT_WINDOW = new Rectangle(0,0,600,600);
}

