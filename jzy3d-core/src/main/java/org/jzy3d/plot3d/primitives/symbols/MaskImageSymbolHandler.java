package org.jzy3d.plot3d.primitives.symbols;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.textured.MaskPair;
import org.jzy3d.plot3d.primitives.textured.NativeDrawableImage;
import org.jzy3d.plot3d.primitives.textured.TexturedCube;
import org.jzy3d.plot3d.rendering.textures.TextureFactory;

/**
 * Create symbols based on an {@link MaskPair} defined {@link NativeDrawableImage}s
 * 
 * @author martin
 */
public class MaskImageSymbolHandler extends SymbolHandler {
  MaskPair mask;

  public MaskImageSymbolHandler(int n) {
    super(n);
    mask = new MaskPair(TextureFactory.get("data/textures/masks/sharp-bg-mask-100-100.png"),
        TextureFactory.get("data/textures/masks/sharp-symbol-mask-100-100.png"));

  }

  public void addSymbolOn(Point point) {
    addSymbolOn(point, mask);
  }

  public void addSymbolOn(Point point, MaskPair mask) {
    // p.setAlphaFactor(0.8f);
    Color face = point.rgb;
    float size = 1;
    Coord3d position = point.xyz;
    // symbols.add(new TexturedCube(position, face, face.negative(), mask, size));

    List<Coord2d> zmapping = TexturedCube.makeZPlaneTextureMapping(position, size);
    NativeDrawableImage northBg =
        new NativeDrawableImage(mask.bgMask, PlaneAxis.Z, position.z, zmapping, face.negative());
    NativeDrawableImage north =
        new NativeDrawableImage(mask.symbolMask, PlaneAxis.Z, position.z, zmapping, face);
    symbols.add(northBg);
    symbols.add(north);
  }

  public MaskPair getMask() {
    return mask;
  }

  public void setMask(MaskPair mask) {
    this.mask = mask;
  }
}
