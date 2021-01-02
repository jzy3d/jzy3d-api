package org.jzy3d.plot3d.primitives.symbols;

import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.textured.NativeDrawableImage;
import org.jzy3d.plot3d.primitives.textured.TexturedCube;
import org.jzy3d.plot3d.primitives.volume.textured.AWTNativeDrawableImage;
import org.jzy3d.plot3d.rendering.image.AWTImageWrapper;
import org.jzy3d.plot3d.rendering.image.IImageWrapper;
import org.jzy3d.plot3d.rendering.textures.BufferedImageTexture;
import org.jzy3d.plot3d.rendering.textures.SharedTexture;

/**
 * Create {@link NativeDrawableImage} symbols based on an {@link java.awt.Shape}
 * 
 * @author martin
 */
public class AWTShapeNativeSymbolHandler extends SymbolHandler{
    protected SharedTexture sharedTexture;
    protected AWTImageWrapper image;

    public AWTShapeNativeSymbolHandler(IImageWrapper image) {
        this(new BufferedImageTexture(((AWTImageWrapper) image).getImage()));
        this.image = (AWTImageWrapper)image;
    }
    
    public AWTShapeNativeSymbolHandler(SharedTexture sharedTexture) {
        super();
        this.sharedTexture = sharedTexture;
    }

    @Override
    public void addSymbolOn(Point point) {
        Color face = point.rgb;
        float size = 1;
        Coord3d position = point.xyz;

        List<Coord2d> zmapping = TexturedCube.makeZPlaneTextureMapping(position, size);
        
        // TODO : let the SAME buffered image instance be used by all DrawableTextures
        symbols.add(new AWTNativeDrawableImage(sharedTexture, PlaneAxis.Z, position.z, zmapping, face));
    }

    public static AWTShapeNativeSymbolHandler from(Shape shape) {
    	return from(AWTImageWrapper.getImage(shape));
    }

	public static AWTShapeNativeSymbolHandler from(BufferedImage image) {
		return new AWTShapeNativeSymbolHandler(new BufferedImageTexture(image));
	}
    
	public static AWTShapeNativeSymbolHandler from(IImageWrapper image) {
		return from(((AWTImageWrapper)image).getImage());
	}
    

}
