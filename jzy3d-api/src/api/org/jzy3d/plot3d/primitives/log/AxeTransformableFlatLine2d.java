package org.jzy3d.plot3d.primitives.log;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.FlatLine2d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.log.transformers.LogTransformer;

public class AxeTransformableFlatLine2d extends FlatLine2d {
	public AxeTransformableFlatLine2d(LogTransformer transformers){
		this.transformers = transformers;
	}
	
	public AxeTransformableFlatLine2d(float[] x, float[] y, float depth, LogTransformer transformers){
		this.transformers = transformers;
		setData(x, y, depth);
	}
	
	protected AxeTransformableQuad getLineElement(float x1, float x2, float y1, float y2, float depth){
		AxeTransformableQuad q = new AxeTransformableQuad(transformers);
		q.add( new Point( new Coord3d( 0f, x1, y1 ) ) );
		q.add( new Point( new Coord3d( 0f, x2, y2 ) ) );
		q.add( new Point( new Coord3d( depth, x2, y2 ) ) );
		q.add( new Point( new Coord3d( depth, x1, y1 ) ) );
		return q;
	}
	
	LogTransformer transformers;
}
