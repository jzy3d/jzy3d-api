package org.jzy3d.chart.factories;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartComponentFactory.Toolkit;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.primitives.axes.LogAxeBox;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.transform.log.AxeTransform;
import org.jzy3d.plot3d.transform.log.LogTransformer;

public class AxeTransformableAWTChartComponentFactory  extends AWTChartComponentFactory{
	
	LogTransformer transformers;
	
	
	
	public AxeTransformableAWTChartComponentFactory(
			LogTransformer transformers) {
		this.transformers = transformers;
	}

	public static Chart chart() {
        return chart(Quality.Intermediate);
    }
    
    public static Chart chart(Quality quality, LogTransformer transformers) {
    	AxeTransformableAWTChartComponentFactory f = new AxeTransformableAWTChartComponentFactory(transformers);
        return f.newChart(quality, Toolkit.newt);
    }

    public static Chart chart(String toolkit, LogTransformer transformers) {
    	AxeTransformableAWTChartComponentFactory f = new AxeTransformableAWTChartComponentFactory(transformers);
        return f.newChart(Chart.DEFAULT_QUALITY, toolkit);
    }

    public static Chart chart(Quality quality, Toolkit toolkit, LogTransformer transformers) {
    	AxeTransformableAWTChartComponentFactory f = new AxeTransformableAWTChartComponentFactory(transformers);
        return f.newChart(quality, toolkit);
    }

    public static Chart chart(Quality quality, String toolkit, LogTransformer transformers) {
    	AxeTransformableAWTChartComponentFactory f = new AxeTransformableAWTChartComponentFactory(transformers);
        return f.newChart(quality, toolkit);
    }

    @Override
	public IAxe newAxe(BoundingBox3d box, View view) {
		LogAxeBox axe = new LogAxeBox(box,transformers);
		axe.setScale(new Coord3d(10.0,1.0,1.0));
		axe.setView(view);
		return axe;
	}

}
