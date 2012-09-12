package org.jzy3d.chart.graphs;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

public class GraphChart extends Chart{
	public GraphChart() {
		super();
	}

	public GraphChart(Quality quality, String chartType) {
		super(quality, chartType);
	}

	public GraphChart(Quality quality) {
		super(quality);
	}

	public GraphChart(String chartType) {
		super(chartType);
	}

	protected ChartScene initializeScene(boolean graphsort){
		return new ChartScene(graphsort){
			protected GraphView initializeChartView(Scene scene, ICanvas canvas, Quality quality){
				return new GraphView(scene, canvas, quality);
			}
		};
	}
}
