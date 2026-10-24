package org.jzy3d.plot3d.primitives.volume.textured;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.plot3d.primitives.textured.NativeDrawableImage;
import org.jzy3d.plot3d.rendering.image.AWTImageWrapper;
import org.jzy3d.plot3d.rendering.textures.BufferedImageTexture;
import org.jzy3d.plot3d.rendering.textures.SharedTexture;

public class AWTNativeDrawableImage extends NativeDrawableImage {

  public AWTNativeDrawableImage(java.awt.Shape shape, PlaneAxis orientation, float axisValue,
      List<Coord2d> coords, Color filter) {
    this(new BufferedImageTexture(AWTImageWrapper.getImage(shape)), orientation, axisValue, coords,
        filter);
  }

  /* SUPER CLASS INVOKE */

  public AWTNativeDrawableImage(SharedTexture resource, PlaneAxis orientation, float axisValue,
      Color filter) {
    super(resource, orientation, axisValue, filter);
  }

  public AWTNativeDrawableImage(SharedTexture resource, PlaneAxis orientation, float axisValue,
      List<Coord2d> coords, Color filter) {
    super(resource, orientation, axisValue, coords, filter);
  }

  public AWTNativeDrawableImage(SharedTexture resource, PlaneAxis orientation, float axisValue,
      List<Coord2d> coords) {
    super(resource, orientation, axisValue, coords);
  }

  public AWTNativeDrawableImage(SharedTexture resource, PlaneAxis orientation, float axisValue) {
    super(resource, orientation, axisValue);
  }

  public AWTNativeDrawableImage(SharedTexture resource, PlaneAxis orientation) {
    super(resource, orientation);
  }

  public AWTNativeDrawableImage(SharedTexture resource) {
    super(resource);
  }

}
