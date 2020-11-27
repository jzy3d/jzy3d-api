package org.jzy3d.chart.factories;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.axes.AxisBox;
import org.jzy3d.plot3d.primitives.axes.ContourAxisBox;
import org.jzy3d.plot3d.rendering.view.View;

public class ContourChartFactory extends AWTChartFactory{
    @Override
	public AxisBox newAxe(BoundingBox3d box, View view) {
    	ContourAxisBox axe = new ContourAxisBox(box);
        axe.setView(view);
		return axe;
	}
}
