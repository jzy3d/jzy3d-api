package org.jzy3d.analysis;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public abstract class AbstractAnalysis  implements IAnalysis{

	protected Chart chart;
	protected IChartComponentFactory factory;
	
    public AbstractAnalysis(IChartComponentFactory factory){
        this.factory = factory;
    }

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getPitch() {
		return "";
	}

	@Override
	public boolean isInitialized() {
	    return chart!=null;
	}

	@Override
	public Chart initializeChart() {
	    return factory.newChart(Chart.DEFAULT_QUALITY);
	}

	@Override
	public Chart initializeChart(Quality quality) {
	    return factory.newChart(quality);
	}

	@Override
	public Chart getChart() {
	    return chart;
	}

	@Override
	public boolean hasOwnChartControllers() {
	    return false;
	}

	@Override
	public IChartComponentFactory getFactory() {
	    return factory;
	}

	@Override
	public void setFactory(IChartComponentFactory factory) {
	    this.factory = factory;
	}

}