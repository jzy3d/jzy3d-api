package org.jzy3d.chart;

import org.jzy3d.chart.factories.ContourChartComponentFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class ContourChart extends Chart{
	public ContourChart(){
		this(DEFAULT_QUALITY, DEFAULT_WINDOWING_TOOLKIT);
	}	
	public ContourChart(Quality quality){
		this(quality, DEFAULT_WINDOWING_TOOLKIT);
	}	
	public ContourChart(String windowingToolkit){
		this(DEFAULT_QUALITY, windowingToolkit);
	}
	public ContourChart(Quality quality, String windowingToolkit){
        super(new ContourChartComponentFactory(), quality, windowingToolkit, org.jzy3d.global.Settings.getInstance().getGLCapabilities());
    }
}
