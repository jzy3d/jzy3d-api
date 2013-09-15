package org.jzy3d.plot3d.builder;

import java.util.List;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Coordinates;
import org.jzy3d.plot3d.primitives.AbstractComposite;


public abstract class Tessellator {
	public Tessellator() {
	}

	public AbstractComposite build(List<Coord3d> coordinates) {
		Coordinates coords = new Coordinates(coordinates);
		return build(coords.getX(), coords.getY(), coords.getZ());
	}

	public abstract AbstractComposite build(float[] x, float[] y, float[] z);
}