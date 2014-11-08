package org.jzy3d.chart.factories;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformableAxes.AxeTransformableAxeBox;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformer;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.view.View;

public class AxeTransformableAWTChartComponentFactory  extends AWTChartComponentFactory{
	
	AxeTransformer transformerX; //transformation on x axis
	AxeTransformer transformerY; //transformation on y axis
	AxeTransformer transformerZ; //transformation on z axis
	
	
	@Override
	public IAxe newAxe(BoundingBox3d box, View view) {
		AxeTransformableAxeBox axe = new AxeTransformableAxeBox(box,transformerX,transformerY,transformerZ);
		axe.setView(view);
		return axe;
	}

}
