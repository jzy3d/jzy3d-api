package org.jzy3d.analysis;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public interface IAnalysis {
	public String getName();
	public String getPitch();
	public Chart getChart() throws Exception;
	public void init() throws Exception;
    public boolean isInitialized();
    public boolean hasOwnChartControllers();
    public String getCanvasType();
    /** Determine the canvas, "awt", "swing", "newt", "offscreen".*/
    public void setCanvasType(String type);
    /** Use a factory to initialize a chart instance*/
    public Chart initializeChart();
    public Chart initializeChart(Quality quality);
    
    
    public IChartComponentFactory getFactory();
    public void setFactory(IChartComponentFactory factory);
}
