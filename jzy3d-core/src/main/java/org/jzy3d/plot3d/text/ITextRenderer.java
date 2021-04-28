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

public interface ITextRenderer {
  /** 
   * The main text renderer method to implement. Other are shortcuts implemented by {@link AbstractTextRenderer}. 
   * 
   */
  public BoundingBox3d drawText(IPainter painter, Font font, String s, Coord3d position, float rotation, Horizontal halign, Vertical valign, Color color, Coord2d screenOffset, Coord3d sceneOffset);

  public BoundingBox3d drawText(IPainter painter, Font font, String s, Coord3d position, float rotation, Horizontal halign, Vertical valign, Color color);
  public BoundingBox3d drawText(IPainter painter, Font font, String s, Coord3d position, float rotation, Horizontal halign, Vertical valign, Color color, Coord2d screenOffset);

  public BoundingBox3d drawText(IPainter painter, Font font, String s, Coord3d position, Horizontal halign, Vertical valign, Color color);
  public BoundingBox3d drawText(IPainter painter, Font font, String s, Coord3d position, Horizontal halign, Vertical valign, Color color, Coord2d screenOffset);
  public BoundingBox3d drawText(IPainter painter, Font font, String s, Coord3d position, Horizontal halign, Vertical valign, Color color, Coord3d sceneOffset);

  public SpaceTransformer getSpaceTransformer();
  public void setSpaceTransformer(SpaceTransformer transformer);
}
