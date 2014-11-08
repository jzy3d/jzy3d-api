package org.jzy3d.plot3d.builder.axeTransformable;

import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.builder.concrete.OrthonormalTessellator;
import org.jzy3d.plot3d.builder.concrete.AxeTransformable.AxeTransformableOrthonromalTassellator;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformer;

public class axeTransformableBuilder {
	
	public static Shape buildOrthonormal(OrthonormalGrid grid, Mapper mapper, AxeTransformer transformerX, AxeTransformer transformerY, AxeTransformer transformerZ) {
		AxeTransformableOrthonromalTassellator tesselator = new AxeTransformableOrthonromalTassellator(transformerX,transformerY,transformerZ);
		return (Shape) tesselator.build(grid.apply(mapper));
	}
	
}
