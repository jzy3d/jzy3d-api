package org.jzy3d.plot3d.rendering.ddp;

import org.jzy3d.chart.factories.AWTPainterFactory;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

public class PeelingPainterFactory extends AWTPainterFactory{
	public PeelingPainterFactory() {
	}

	@Override
	public Renderer3d newRenderer3D(View view, boolean traceGL, boolean debugGL){
		PeelingMethod method = ((PeelingChartFactory)getChartFactory()).method;
        DepthPeelingRenderer3d r = new DepthPeelingRenderer3d(method, (DepthPeelingView)view, traceGL, debugGL);
        return r;
    }
}
