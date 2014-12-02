package org.jzy3d.plot3d.builder.concrete.AxeTransformable;

import org.jzy3d.plot3d.builder.concrete.OrthonormalTessellator;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.AxeTransformablePolygon;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformer;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformerSet;

public class AxeTransformableOrthonromalTassellator extends
		OrthonormalTessellator {
	
	AxeTransformerSet transformers;
	
	
	
	public AxeTransformableOrthonromalTassellator(AxeTransformerSet transformers) {
		super();
		this.transformers = transformers;
	}



	@Override
	protected AbstractDrawable newQuad(Point p[]){
	    AxeTransformablePolygon quad = new AxeTransformablePolygon(transformers);
        for(int pi=0; pi<p.length; pi++)
            quad.add(p[pi]);
        return quad;
	}

}
