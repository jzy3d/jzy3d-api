package org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;

public class AxeTransformerSet {
    private AxeTransformer x;
    private AxeTransformer y;
    private AxeTransformer z;

    public AxeTransformerSet(AxeTransformer x, AxeTransformer y, AxeTransformer z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public AxeTransformerSet() {
        super();
        this.x = new LinearAxeTransformer();
        this.y = new LinearAxeTransformer();
        this.z = new LinearAxeTransformer();
    }

    public AxeTransformer getX() {
        if (x != null)
            return x;
        else
            return new LinearAxeTransformer();
    }

    public void setX(AxeTransformer x) {
        this.x = x;
    }

    public AxeTransformer getY() {
        if (y != null)
            return y;
        else
            return new LinearAxeTransformer();
    }

    public void setY(AxeTransformer y) {
        this.y = y;
    }

    public AxeTransformer getZ() {
        if (z != null)
            return z;
        else
            return new LinearAxeTransformer();
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
