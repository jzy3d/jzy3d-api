package org.jzy3d.analysis;

import org.jzy3d.chart.Chart;

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
}
