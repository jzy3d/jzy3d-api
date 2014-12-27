package org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;

public class AxeTransformerSet {
    protected AxeTransformer x;
    protected AxeTransformer y;
    protected AxeTransformer z;

    public AxeTransformerSet(AxeTransformer x, AxeTransformer y, AxeTransformer z) {
        this.x = x != null ? x : new LinearAxeTransformer();
        this.y = y != null ? y : new LinearAxeTransformer();
        this.z = z != null ? z : new LinearAxeTransformer();
    }

    public AxeTransformerSet() {
        this.x = new LinearAxeTransformer();
        this.y = new LinearAxeTransformer();
        this.z = new LinearAxeTransformer();
    }

    public AxeTransformer getX() {
        return x;
    }

    public void setX(AxeTransformer x) {
        this.x = x;
    }

    public AxeTransformer getY() {
        return y;
    }

    public void setY(AxeTransformer y) {
        this.y = y;
    }

    public AxeTransformer getZ() {
        return z;
    }

    public void setZ(AxeTransformer z) {
        this.z = z;
    }

    public Coord3d computePoint(Coord3d point) {
        return new Coord3d(getX().compute(point.x), getY().compute(point.y), getZ().compute(point.z));
    }

    public Coord2d computePoint(Coord2d point) {
        return new Coord2d(getX().compute(point.x), getY().compute(point.y));
    }

}
