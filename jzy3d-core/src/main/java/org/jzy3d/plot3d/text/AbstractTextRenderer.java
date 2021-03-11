package org.jzy3d.plot3d.text;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public abstract class AbstractTextRenderer implements ITextRenderer {
  protected SpaceTransformer spaceTransformer;
  protected Coord2d defScreenOffset;
  protected Coord3d defSceneOffset;

  public AbstractTextRenderer() {
    defScreenOffset = new Coord2d();
    defSceneOffset = new Coord3d();
  }

  @Override
  public BoundingBox3d drawText(IPainter painter, String s, Coord3d position, Horizontal halign,
      Vertical valign, Color color) {
    return drawText(painter, s, position, halign, valign, color, defScreenOffset, defSceneOffset);
  }

  @Override
  public BoundingBox3d drawText(IPainter painter, String s, Coord3d position, Horizontal halign,
      Vertical valign, Color color, Coord2d screenOffset) {
    return drawText(painter, s, position, halign, valign, color, screenOffset, defSceneOffset);
  }

  @Override
  public BoundingBox3d drawText(IPainter painter, String s, Coord3d position, Horizontal halign,
      Vertical valign, Color color, Coord3d sceneOffset) {
    return drawText(painter, s, position, halign, valign, color, defScreenOffset, sceneOffset);
  }

  @Override
  public SpaceTransformer getSpaceTransformer() {
    return spaceTransformer;
  }

  @Override
  public void setSpaceTransformer(SpaceTransformer transformer) {
    this.spaceTransformer = transformer;
  }
}
