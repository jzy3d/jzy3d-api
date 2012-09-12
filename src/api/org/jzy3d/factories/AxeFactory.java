package org.jzy3d.factories;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.view.View;


public class AxeFactory{
	public IAxe getInstance(BoundingBox3d box, View view) {
	    AxeBox axe = new AxeBox(box);
	    axe.setView(view);
		return axe;
	}
}
