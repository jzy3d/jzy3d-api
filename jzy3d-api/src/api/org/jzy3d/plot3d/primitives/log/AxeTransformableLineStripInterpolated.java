package org.jzy3d.plot3d.primitives.log;

import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.algorithms.interpolation.IInterpolator;
import org.jzy3d.plot3d.primitives.LineStripInterpolated;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public class AxeTransformableLineStripInterpolated extends LineStripInterpolated{

	public AxeTransformableLineStripInterpolated(IInterpolator interpolator, List<Coord3d> controlPoints, int resolution, SpaceTransformer transformers){
	    this.transformers = transformers;
		this.controlCoords = controlPoints;
		this.resolution = resolution;
		this.interpolatedCoords = interpolator.interpolate(controlPoints, resolution);
		
        this.interpolatedPoints = toPoints(interpolatedCoords, Color.BLUE, 3);
        this.controlPoints = toPoints(controlCoords, Color.RED, 5);
        this.line = new AxeTransformableLineStrip(interpolatedCoords, transformers);
        this.line.setWireframeColor(Color.BLACK);
        
		add( this.line );
        add( this.controlPoints );
        add( this.interpolatedPoints );
	}
	
	protected Point toPoint(Coord3d coord, Color color, float width){
        return new AxeTransformablePoint(coord, color, width, transformers);
    }
	
	SpaceTransformer transformers;
}
