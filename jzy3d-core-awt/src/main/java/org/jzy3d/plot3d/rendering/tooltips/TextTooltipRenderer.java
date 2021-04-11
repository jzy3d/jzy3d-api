package org.jzy3d.plot3d.rendering.tooltips;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;


public class TextTooltipRenderer implements ITooltipRenderer {
  public TextTooltipRenderer(String text, IntegerCoord2d screen, Coord3d target) {
    if (screen != null)
      this.text = text;
    if (screen != null)
      screenLocation = screen;
    if (target != null)
      this.target = target;
  }

  @Override
  public void render(Graphics2D g2d) {
    lastBounds =
        new Rectangle(screenLocation.x - 10, screenLocation.y - 13, 10 + text.length() * 6, 16);

    g2d.setColor(Color.WHITE);
    g2d.fillRect(lastBounds.x, lastBounds.y, lastBounds.width, lastBounds.height);
    g2d.setColor(Color.BLACK);
    g2d.drawRect(lastBounds.x, lastBounds.y, lastBounds.width, lastBounds.height);
    g2d.drawString(text, screenLocation.x, screenLocation.y);

  }

  @Override
  public void updateScreenPosition(IntegerCoord2d position) {
    screenLocation = position;
  }

  public void updateTargetCoordinate(Coord3d target) {
    this.target = target;
  }


  protected Coord3d target = Coord3d.INVALID;
  protected IntegerCoord2d screenLocation = new IntegerCoord2d();
  protected Rectangle lastBounds;
  protected String text = "";

}
