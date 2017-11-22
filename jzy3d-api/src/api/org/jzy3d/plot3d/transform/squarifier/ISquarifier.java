package org.jzy3d.plot3d.transform.squarifier;

import org.jzy3d.maths.Coord3d;

public interface ISquarifier {
	
	Coord3d scale(float xRange, float yRange, float zRange);

}
