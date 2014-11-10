package org.jzy3d.chart.factories;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartComponentFactory.Toolkit;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformableAxes.AxeTransformableAxeBox;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformer;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.View;

public class AxeTransformableAWTChartComponentFactory  extends AWTChartComponentFactory{
	
	AxeTransformer transformerX;
	AxeTransformer transformerY;
	AxeTransformer transformerZ;
	
	
	
	public AxeTransformableAWTChartComponentFactory(
			AxeTransformer transformerX, AxeTransformer transformerY,
			AxeTransformer transformerZ) {
		this.transformerX = transformerX;
		this.transformerY = transformerY;
		this.transformerZ = transformerZ;
	}

	public static Chart chart() {
        return chart(Quality.Intermediate);
    }
    
    public static Chart chart(Quality quality, AxeTransformer transformerX, AxeTransformer transformerY, AxeTransformer transformerZ) {
    	AxeTransformableAWTChartComponentFactory f = new AxeTransformableAWTChartComponentFactory(transformerX, transformerY, transformerZ);
        return f.newChart(quality, Toolkit.newt);
    }

    public static Chart chart(String toolkit, AxeTransformer transformerX, AxeTransformer transformerY, AxeTransformer transformerZ) {
    	AxeTransformableAWTChartComponentFactory f = new AxeTransformableAWTChartComponentFactory(transformerX, transformerY, transformerZ);
        return f.newChart(Chart.DEFAULT_QUALITY, toolkit);
    }

    public static Chart chart(Quality quality, Toolkit toolkit, AxeTransformer transformerX, AxeTransformer transformerY, AxeTransformer transformerZ) {
    	AxeTransformableAWTChartComponentFactory f = new AxeTransformableAWTChartComponentFactory(transformerX, transformerY, transformerZ);
        return f.newChart(quality, toolkit);
    }

    public static Chart chart(Quality quality, String toolkit, AxeTransformer transformerX, AxeTransformer transformerY, AxeTransformer transformerZ) {
    	AxeTransformableAWTChartComponentFactory f = new AxeTransformableAWTChartComponentFactory(transformerX, transformerY, transformerZ);
        return f.newChart(quality, toolkit);
    }

    @Override
	public IAxe newAxe(BoundingBox3d box, View view) {
		AxeTransformableAxeBox axe = new AxeTransformableAxeBox(box,transformerX,transformerY,transformerZ);
		axe.setScale(new Coord3d(10.0,1.0,1.0));
		axe.setView(view);
		return axe;
	}

}
