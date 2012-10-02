package org.jzy3d.chart.factories;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.axes.ContourAxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.view.View;

public class ContourChartComponentFactory extends ChartComponentFactory{
    @Override
	public IAxe newAxe(BoundingBox3d box, View view) {
    	ContourAxeBox axe = new ContourAxeBox(box);
        axe.setView(view);
		return axe;
	}
}
