package org.jzy3d.plot3d.rendering.tooltips;

import java.awt.Color;
import java.awt.Graphics2D;
import org.jzy3d.maths.Coord3d;


public class TooltipRenderer {
  public TooltipRenderer(String xUnit, String yUnit, String zUnit) {
    this(xUnit, yUnit, zUnit, xUnit != null, yUnit != null, zUnit != null);
  }

  public TooltipRenderer(String xUnit, String yUnit, String zUnit, boolean renderX, boolean renderY,
      boolean renderZ) {
    this.xUnit = xUnit;
    this.yUnit = yUnit;
    this.zUnit = zUnit;
    this.renderX = renderX;
    this.renderY = renderY;
    this.renderZ = renderZ;
  }

  public void render(Graphics2D g2d, Tooltip t) {
    render(g2d, t.x, t.y, t.target);
  }

  public void render(Graphics2D g2d, Tooltip t, int height) {
    render(g2d, t.x, height - t.y, t.target);
  }

  public void render(Graphics2D g2d, int x, int y, Coord3d coord) {
    String label = format(coord);
    g2d.setColor(Color.WHITE);
    g2d.fillRect(x - 10, y - 13, 10 + label.length() * 6, 16);
    g2d.setColor(Color.BLACK);
    g2d.drawRect(x - 10, y - 13, 10 + label.length() * 6, 16);
    g2d.drawString(label, x, y);
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
}
