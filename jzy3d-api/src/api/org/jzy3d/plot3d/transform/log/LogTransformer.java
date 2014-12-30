package org.jzy3d.plot3d.transform.log;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;

/**
 * A helper to apply 3 {@link AxeTransform} on each dimension of a {@link Coord3d}.
 * 
 * @author 
 *
 */
public class LogTransformer {
    protected AxeTransform x;
    protected AxeTransform y;
    protected AxeTransform z;

    public LogTransformer(AxeTransform x, AxeTransform y, AxeTransform z) {
        this.x = x != null ? x : new AxeTransformLinear();
        this.y = y != null ? y : new AxeTransformLinear();
        this.z = z != null ? z : new AxeTransformLinear();
    }

    public LogTransformer() {
        this.x = new AxeTransformLinear();
        this.y = new AxeTransformLinear();
        this.z = new AxeTransformLinear();
    }

    public AxeTransform getX() {
        return x;
    }

    public void setX(AxeTransform x) {
        this.x = x;
    }

    public AxeTransform getY() {
        return y;
    }

    public void setY(AxeTransform y) {
        this.y = y;
    }

    public AxeTransform getZ() {
        return z;
    }

    public void setZ(AxeTransform z) {
        this.z = z;
    }

    public Coord3d compute(Coord3d point) {
        return new Coord3d(getX().compute(point.x), getY().compute(point.y), getZ().compute(point.z));
    }

    public Coord2d compute(Coord2d point) {
        return new Coord2d(getX().compute(point.x), getY().compute(point.y));
    }
}
