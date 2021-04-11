package org.jzy3d.plot3d.text.drawable;

import java.awt.Font;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.plot3d.primitives.pickable.PickableTexture;
import org.jzy3d.plot3d.primitives.textured.NativeDrawableImage;
import org.jzy3d.plot3d.rendering.textures.BufferedImageTexture;
import org.jzy3d.plot3d.rendering.textures.SharedTexture;

public class DrawableTextTexture extends PickableTexture {
  public DrawableTextTexture(String text, Coord2d position, Coord2d dim) {
    super(makeImage(text, DEFAULT_FONT), PlaneAxis.Z, 0, makeMapping(dim), Color.BLACK);
    setPlanePosition(position);
  }

  public DrawableTextTexture(String text, int fontSize, Coord2d position, Coord2d dim) {
    super(makeImage(text, new Font("Serif", DEFAULT_FONT_STYLE, fontSize)), PlaneAxis.Z, 0,
        makeMapping(dim), Color.BLACK);
    setPlanePosition(position);
  }

  /*************/

  public DrawableTextTexture(SharedTexture resource) {
    super(resource);
  }

  public DrawableTextTexture(SharedTexture resource, PlaneAxis orientation, float axisValue,
      Color color) {
    super(resource, orientation, axisValue, color);
  }

  public DrawableTextTexture(SharedTexture resource, PlaneAxis orientation, float axisValue,
      List<Coord2d> coords) {
    super(resource, orientation, axisValue, coords);
  }

  public DrawableTextTexture(SharedTexture resource, PlaneAxis orientation, float axisValue) {
    super(resource, orientation, axisValue);
  }

  public DrawableTextTexture(SharedTexture resource, PlaneAxis orientation) {
    super(resource, orientation);
  }

  public DrawableTextTexture(SharedTexture resource, PlaneAxis orientation, float axisValue,
      List<Coord2d> coords, Color filter) {
    super(resource, orientation, axisValue, coords, filter);
  }

  /*****************/
  protected static String DEFAULT_FONT_NAME = "Serif";
  protected static int DEFAULT_FONT_SIZE = 16;
  protected static int DEFAULT_FONT_STYLE = Font.PLAIN;
  protected static Font DEFAULT_FONT = new Font("Serif", DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE);

  // image generation @ construction
  protected static BufferedImageTexture makeImage(String text, Font font) {
    return new TextImageRenderer(text, font).getImage();
  }

  protected static List<Coord2d> makeMapping(Coord2d dim) {
    return NativeDrawableImage.getManualTextureMapping(dim.x, dim.y, dim.x / 2, dim.y / 2);
  }
}
