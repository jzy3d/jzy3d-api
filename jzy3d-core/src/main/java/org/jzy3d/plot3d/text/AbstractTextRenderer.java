package org.jzy3d.plot3d.text;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public abstract class AbstractTextRenderer implements ITextRenderer {
  protected static final int NO_ROTATION = 0;
  protected static final Coord2d NO_SCREEN_OFFSET = new Coord2d();
  protected static final Coord3d NO_SCENE_OFFSET = new Coord3d();

  protected SpaceTransformer spaceTransformer;

  @Override
  public SpaceTransformer getSpaceTransformer() {
    return spaceTransformer;
  }

  @Override
  public void setSpaceTransformer(SpaceTransformer transformer) {
    this.spaceTransformer = transformer;
  }


  @Override
  public BoundingBox3d drawText(IPainter painter, Font font, String s, Coord3d position,
      Horizontal halign, Vertical valign, Color color) {
    return drawText(painter, font, s, position, NO_ROTATION, halign, valign, color, NO_SCREEN_OFFSET, NO_SCENE_OFFSET);
  }
  
  @Override
  public BoundingBox3d drawText(IPainter painter, Font font, String s, Coord3d position,
      float rotation, Horizontal halign, Vertical valign, Color color) {
    return drawText(painter, font, s, position, rotation, halign, valign, color, NO_SCREEN_OFFSET, NO_SCENE_OFFSET);
  }
  
  @Override
  public BoundingBox3d drawText(IPainter painter, Font font, String s, Coord3d position,
      float rotation, Horizontal halign, Vertical valign, Color color, Coord2d screenOffset) {
    return drawText(painter, font, s, position, rotation, halign, valign, color, screenOffset, NO_SCENE_OFFSET);
  }



  @Override
  public BoundingBox3d drawText(IPainter painter, Font font, String s, Coord3d position,
      Horizontal halign, Vertical valign, Color color, Coord2d screenOffset) {
    return drawText(painter, font, s, position, NO_ROTATION, halign, valign, color, screenOffset, NO_SCENE_OFFSET);
  }

  @Override
  public BoundingBox3d drawText(IPainter painter, Font font, String s, Coord3d position,
      Horizontal halign, Vertical valign, Color color, Coord3d sceneOffset) {
    return drawText(painter, font, s, position, NO_ROTATION, halign, valign, color, NO_SCREEN_OFFSET, sceneOffset);
  }
}
