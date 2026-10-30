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
 * Create {@link NativeDrawableImage} symbols to be displayed on line strip points.
 * 
 * Note that this symbol handler handle image like real 3d objects with an orientation. In other
 * word, the image is standing on a static plane that remain unchanged when camera rotate.
 * 
 * For a 2d sprite that will be always camera facing, try {@link AWTNativeSymbolHandler2d}.
 * 
 * @author martin
 */
public class AWTNativeSymbolHandler extends SymbolHandler {
  protected SharedTexture sharedTexture;
  protected AWTImageWrapper image;

  public AWTNativeSymbolHandler(IImageWrapper image) {
    this(new BufferedImageTexture(((AWTImageWrapper) image).getImage()));
    this.image = (AWTImageWrapper) image;
  }

  public AWTNativeSymbolHandler(SharedTexture sharedTexture) {
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

  public static AWTNativeSymbolHandler from(Shape shape) {
    return from(AWTImageWrapper.getImage(shape));
  }

  public static AWTNativeSymbolHandler from(BufferedImage image) {
    return new AWTNativeSymbolHandler(new BufferedImageTexture(image));
  }

  public static AWTNativeSymbolHandler from(IImageWrapper image) {
    return from(((AWTImageWrapper) image).getImage());
  }

}
