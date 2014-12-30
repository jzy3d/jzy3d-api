package org.jzy3d.plot3d.builder.concrete;

import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.log.AxeTransformablePolygon;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public class OrthonormalTessellatorLog extends OrthonormalTessellator {
    SpaceTransformer transformers;

    public OrthonormalTessellatorLog(SpaceTransformer transformers) {
        super();
        this.transformers = transformers;
    }

    @Override
    protected AbstractDrawable newQuad(Point p[]) {
        AxeTransformablePolygon quad = new AxeTransformablePolygon(transformers);
        for (int pi = 0; pi < p.length; pi++)
            quad.add(p[pi]);
        return quad;
    }
}
