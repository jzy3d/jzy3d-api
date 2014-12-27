package org.jzy3d.plot3d.builder.axeTransformable;

import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.Tessellator;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.builder.concrete.OrthonormalTessellator;
import org.jzy3d.plot3d.builder.concrete.AxeTransformable.AxeTransformableOrthonormalTessellator;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformer;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformerSet;

public class axeTransformableBuilder extends Builder {
	
	public static Shape buildOrthonormal(OrthonormalGrid grid, Mapper mapper, AxeTransformerSet transformers) {
		AxeTransformableOrthonormalTessellator tesselator = new AxeTransformableOrthonormalTessellator(transformers);
		return (Shape) tesselator.build(grid.apply(mapper));
	}
	
	public static CompileableComposite buildOrthonormalBig(OrthonormalGrid grid, Mapper mapper, AxeTransformerSet transformers) {
        Tessellator tesselator = new AxeTransformableOrthonormalTessellator(transformers);
        Shape s1 = (Shape) tesselator.build(grid.apply(mapper));
        return buildComposite(applyStyling(s1));
    }
	
}
