package org.jzy3d.plot3d.text.drawable.cells;


import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.pickable.PickableTexture;
import org.jzy3d.plot3d.primitives.textured.NativeDrawableImage;
import org.jzy3d.plot3d.rendering.textures.BufferedImageTexture;
import org.jzy3d.plot3d.rendering.textures.SharedTexture;

public class DrawableTextCell extends PickableTexture {
  public DrawableTextCell(int n, String text, Coord2d position, Coord2d dim) {
    super(makeImage(n, text), PlaneAxis.Z, 0, makeMapping(dim), Color.WHITE);
    setPlanePosition(position);
  }

  public DrawableTextCell(TextCellRenderer renderer, Coord2d position, Coord2d dim) {
    super(renderer.getImage(), PlaneAxis.Z, 0, makeMapping(dim), Color.WHITE);
    setPlanePosition(position);
  }

  public DrawableTextCell(BufferedImageTexture image, Coord2d position, Coord2d dim) {
    this(image, PlaneAxis.Z, 0, makeMapping(dim), Color.WHITE);
    setPlanePosition(position);
  }

  public DrawableTextCell(SharedTexture resource, PlaneAxis orientation, float axisValue,
      List<Coord2d> coords, Color filter) {
    super(resource, orientation, axisValue, coords, filter);
  }

  public void setCellRenderer(TextCellRenderer renderer) {
    setResource(renderer.getImage());
  }

  /******************************/
  // default image generation @ construction

  protected static Font DEFAULT_FONT = new Font("Serif", 16);

  protected static BufferedImageTexture makeImage(int n, String text) {
    return new TextCellRenderer(n, text, DEFAULT_FONT).getImage();
  }

  protected static List<Coord2d> makeMapping(Coord2d dim) {
    return NativeDrawableImage.getManualTextureMapping(dim.x, dim.y, dim.x / 2, dim.y / 2);
  }
}
