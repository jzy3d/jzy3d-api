package org.jzy3d.plot3d.primitives.textured;

import org.jzy3d.plot3d.rendering.textures.SharedTexture;

public class MaskPair {
  public MaskPair(SharedTexture bgMask, SharedTexture symbolMask) {
    this.bgMask = bgMask;
    this.symbolMask = symbolMask;
  }

  public SharedTexture bgMask;
  public SharedTexture symbolMask;
}
