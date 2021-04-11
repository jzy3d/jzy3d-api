package org.jzy3d.plot3d.rendering.tooltips;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;


public class CoordinateTooltipRenderer implements ITooltipRenderer {
  public CoordinateTooltipRenderer() {
    this(null);
  }

  public CoordinateTooltipRenderer(IntegerCoord2d screen) {
    this("X", "Y", "Z", screen);
  }

  public CoordinateTooltipRenderer(IntegerCoord2d screen, Coord3d target) {
    this("X", "Y", "Z", screen);
    this.target = target;
  }

  public CoordinateTooltipRenderer(String xUnit, String yUnit, String zUnit) {
    this(xUnit, yUnit, zUnit, null);
  }

  public CoordinateTooltipRenderer(String xUnit, String yUnit, String zUnit,
      IntegerCoord2d screen) {
    this(xUnit, yUnit, zUnit, xUnit != null, yUnit != null, zUnit != null, screen, null);
  }

  public CoordinateTooltipRenderer(String xUnit, String yUnit, String zUnit, IntegerCoord2d screen,
      Coord3d target) {
    this(xUnit, yUnit, zUnit, xUnit != null, yUnit != null, zUnit != null, screen, target, false);
  }

  public CoordinateTooltipRenderer(String xUnit, String yUnit, String zUnit, IntegerCoord2d screen,
      Coord3d target, boolean newLineAfterEachDim) {
    this(xUnit, yUnit, zUnit, xUnit != null, yUnit != null, zUnit != null, screen, target,
        newLineAfterEachDim);
  }

  public CoordinateTooltipRenderer(String xUnit, String yUnit, String zUnit, boolean renderX,
      boolean renderY, boolean renderZ, IntegerCoord2d screen, Coord3d target) {
    this(xUnit, yUnit, zUnit, renderX, renderY, renderZ, screen, target, false);
  }

  public CoordinateTooltipRenderer(String xUnit, String yUnit, String zUnit, boolean renderX,
      boolean renderY, boolean renderZ, IntegerCoord2d screen, Coord3d target,
      boolean newLineAfterEachDim) {
    this.xUnit = xUnit;
    this.yUnit = yUnit;
    this.zUnit = zUnit;
    this.renderX = renderX;
    this.renderY = renderY;
    this.renderZ = renderZ;
    this.newLineAfterEachDim = newLineAfterEachDim;

    if (screen != null)
      screenLocation = screen;
    if (target != null)
      this.target = target;
  }

  @Override
  public void render(Graphics2D g2d) {
    if (newLineAfterEachDim) {
      String xcontent = xUnit + " = " + target.x;
      String ycontent = yUnit + " = " + target.y;
      String zcontent = zUnit + " = " + target.z;
      int maxlength = Math.max(Math.max(xcontent.length(), ycontent.length()), zcontent.length());
      lastBounds =
          new Rectangle(screenLocation.x - 10, screenLocation.y - 13, 10 + maxlength * 8, 16 * 3);

      g2d.setColor(Color.WHITE);
      g2d.fillRect(lastBounds.x, lastBounds.y, lastBounds.width, lastBounds.height);
      g2d.setColor(Color.BLACK);
      g2d.drawRect(lastBounds.x, lastBounds.y, lastBounds.width, lastBounds.height);
      g2d.drawString(xcontent, screenLocation.x, screenLocation.y);
      g2d.drawString(ycontent, screenLocation.x, screenLocation.y + 16);
      g2d.drawString(zcontent, screenLocation.x, screenLocation.y + 32);
    } else {
      String content = format(target);
      lastBounds = new Rectangle(screenLocation.x - 10, screenLocation.y - 13,
          10 + content.length() * 6, 16);

      g2d.setColor(Color.WHITE);
      g2d.fillRect(lastBounds.x, lastBounds.y, lastBounds.width, lastBounds.height);
      g2d.setColor(Color.BLACK);
      g2d.drawRect(lastBounds.x, lastBounds.y, lastBounds.width, lastBounds.height);
      g2d.drawString(content, screenLocation.x, screenLocation.y);
    }
  }

  @Override
  public void updateScreenPosition(IntegerCoord2d position) {
    screenLocation = position;
  }

  public void updateTargetCoordinate(Coord3d target) {
    this.target = target;
  }

  public String format(Coord3d c) {
    String out = "";
    if (renderX)
      out += xUnit + " = " + c.x + "  ";
    if (renderY)
      out += yUnit + " = " + c.y + "  ";
    if (renderZ)
      out += zUnit + " = " + c.z + "  ";
    return out;
  }

  protected String xUnit;
  protected String yUnit;
  protected String zUnit;
  protected boolean renderX;
  protected boolean renderY;
  protected boolean renderZ;

  protected Coord3d target = Coord3d.INVALID;
  protected IntegerCoord2d screenLocation = new IntegerCoord2d();
  protected Rectangle lastBounds;

  protected boolean newLineAfterEachDim;
}
