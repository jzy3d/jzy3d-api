package org.jzy3d.plot3d.builder.concrete.AxeTransformable;

import org.jzy3d.plot3d.builder.concrete.OrthonormalTessellator;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.AxeTransformablePolygon;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformer;

public class AxeTransformableOrthonromalTassellator extends
		OrthonormalTessellator {
	
	AxeTransformer transformerX;
	AxeTransformer transformerY;
	AxeTransformer transformerZ;
	
	
	
	public AxeTransformableOrthonromalTassellator(AxeTransformer transformerX,
			AxeTransformer transformerY, AxeTransformer transformerZ) {
		super();
		this.transformerX = transformerX;
		this.transformerY = transformerY;
		this.transformerZ = transformerZ;
	}



	@Override
	protected AbstractDrawable newQuad(Point p[]){
	    AxeTransformablePolygon quad = new AxeTransformablePolygon(transformerX,transformerY,transformerZ);
        for(int pi=0; pi<p.length; pi++)
            quad.add(p[pi]);
        return quad;
	}

}
