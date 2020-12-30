package org.jzy3d.plot3d.primitives.symbols;

import java.awt.Shape;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.textured.NativeDrawableImage;
import org.jzy3d.plot3d.primitives.textured.TexturedCube;
import org.jzy3d.plot3d.primitives.volume.textured.AWTNativeDrawableImage;

/**
 * Create {@link NativeDrawableImage} symbols based on an {@link java.awt.Shape}
 * 
 * @author martin
 */
public class AWTShapeNativeSymbolHandler extends SymbolHandler{
    protected Shape awtShape;
    
    public AWTShapeNativeSymbolHandler(Shape awtShape) {
        super();
        this.awtShape = awtShape;
    }

    @Override
    public void addSymbolOn(Point point) {
        Color face = point.rgb;
        float size = 1;
        Coord3d position = point.xyz;

        List<Coord2d> zmapping = TexturedCube.makeZPlaneTextureMapping(position, size);
        
        // TODO : let the SAME buffered image instance be used by all DrawableTextures
        symbols.add(new AWTNativeDrawableImage(awtShape, PlaneAxis.Z, position.z, zmapping, face));
    }


}
