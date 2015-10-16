package org.jzy3d.analysis;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public abstract class AbstractAnalysis implements IAnalysis{
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public String getPitch(){
		return "";
	}
	
	@Override
	public boolean isInitialized(){
	    return chart!=null;
	}
	
	@Override
	public Chart initializeChart(){
        AWTChartComponentFactory f = new AWTChartComponentFactory();
        return f.newChart(Chart.DEFAULT_QUALITY, getCanvasType());
	}

    @Override
    public Chart initializeChart(Quality quality){
        AWTChartComponentFactory f = new AWTChartComponentFactory();
        return f.newChart(quality, getCanvasType());
    }

	
	@Override
	public Chart getChart(){
        return chart;
    }
	
	@Override
	public String getCanvasType(){
	    return canvasType;
	}
	
	@Override
	public void setCanvasType(String type){
	    canvasType = type;
	}
	
	@Override
    public boolean hasOwnChartControllers(){
	    return false;
	}

    protected Chart chart;
    protected String canvasType="awt";
}
