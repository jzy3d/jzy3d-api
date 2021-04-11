package org.jzy3d.plot3d.rendering.tooltips;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;


public class InteractiveTooltip implements IInteractiveTooltipRenderer {
  public InteractiveTooltip(Coord3d target, String content, IntegerCoord2d screenLocation) {
    this(target, content, screenLocation, null);
  }

  public InteractiveTooltip(Coord3d target, String content, IntegerCoord2d screenLocation,
      IAction action) {
    this.target = target;
    this.action = action;
    this.content = content;
    this.screenLocation = screenLocation;
  }

  /** Return screen bounds of the tooltip rectangle used at last call to render() */
  @Override
  public Rectangle getLastBounds() {
    return lastBounds;
  }

  @Override
  public void render(Graphics2D g2d) {
    lastBounds =
        new Rectangle(screenLocation.x - 10, screenLocation.y - 13, 10 + content.length() * 6, 16);

    g2d.setColor(Color.WHITE);
    g2d.fillRect(lastBounds.x, lastBounds.y, lastBounds.width, lastBounds.height);
    g2d.setColor(Color.BLACK);
    g2d.drawRect(lastBounds.x, lastBounds.y, lastBounds.width, lastBounds.height);
    g2d.drawString(content, screenLocation.x, screenLocation.y);
  }

  @Override
  public void updateScreenPosition(IntegerCoord2d position) {
    screenLocation = position;
  }

  protected Coord3d target;
  protected String content;
  protected IAction action;
  protected IntegerCoord2d screenLocation;
  protected Rectangle lastBounds;

}
