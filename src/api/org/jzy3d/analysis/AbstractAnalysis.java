package org.jzy3d.analysis;

import org.jzy3d.chart.Chart;

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
