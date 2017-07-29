package org.jzy3d.chart2d;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.plot2d.primitives.Serie2d;


public class Chart2dGroup {
	protected static Serie2d.Type DEFAULT_SERIE_TYPE = Serie2d.Type.LINE;

	protected Map<String,Chart2d> charts = new HashMap<String,Chart2d>();

	/**
	 * Create a chart group for TIME series.
	 */
	public Chart2dGroup(float timeMax, int ymin, int ymax, int chartNumber) {
		for (int i = 0; i < chartNumber; i++) {
			Chart2d pitchChart = new Chart2d();
			pitchChart.asTimeChart(timeMax, ymin, ymax, "Time", "Value");
			Serie2d seriePitch = pitchChart.getSerie(name(i), DEFAULT_SERIE_TYPE);
			seriePitch.setColor(Color.BLUE);
			charts.put(name(i), pitchChart);
		}
	}
	
	/**
	 * Create a regular line chart group.
	 */
	public Chart2dGroup(int ymin, int ymax, int chartNumber) {
		for (int i = 0; i < chartNumber; i++) {
			Chart2d pitchChart = new Chart2d();
			Serie2d seriePitch = pitchChart.getSerie(name(i), DEFAULT_SERIE_TYPE);
			seriePitch.setColor(Color.BLUE);
			charts.put(name(i), pitchChart);
		}
	}
	
	public void asTimeCharts(float timeMax, float ymin, float ymax, String xlabel, String ylabel){
		for(Chart2d chart : charts.values()){
			chart.asTimeChart(timeMax, ymin, ymax, xlabel, ylabel); 
		}
	}

	public Collection<Chart2d> getCharts() {
		return charts.values();
	}
	
	public Chart getChart(String key){
		return charts.get(key);
	}
	public Chart getChart(int key){
		return getChart(""+key);
	}
	
	/**
	 * Return the 2d serie of a chart.
	 * 
	 * @param chartKey
	 * @param serieKey serie name in chart
	 * @return
	 */
	public Serie2d getSerie(int chartKey, int serieKey){
		return getSerie(""+chartKey, ""+serieKey);
	}
	
	public Serie2d getSerie(String chartKey, String serieKey){
		return ((Chart2d)getChart(chartKey)).getSerie(serieKey, DEFAULT_SERIE_TYPE);
	}
	
	
	
	protected String name(int id){
		return "" + id;
	}
}
