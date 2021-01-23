package org.jzy3d.plot2d.rendering;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.jzy3d.colors.Color;

public class CanvasSWT implements Canvas {
  private final LocalResourceManager resourceManager =
      new LocalResourceManager(JFaceResources.getResources());

  /**
   * Creates a new instance of Pencil2dAWT. A Pencil2dAWT provides an implementation for drawing
   * wafer sites on an SWT GC (Graphic Context).
   */
  public CanvasSWT(GC graphic) {
    target = graphic;
  }

  public void dispose() {
    resourceManager.dispose();
  }

  @Override
  public void drawString(int x, int y, String text) {
    target.setBackground(bgColor);
    target.setForeground(fgColor);
    target.setFont(resourceManager.createFont(FontDescriptor.createFrom("Arial", 8, SWT.NONE)));
    target.drawString(text, x, y);
  }

  @Override
  public void drawRect(Color color, int x, int y, int width, int height, boolean border) {
    if (color != null) {
      target.setBackground(toSWTColor(color));
      target.fillRectangle(x, y, width, height);
    }

    if (border) {
      target.setForeground(fgColor);
      target.drawRectangle(x, y, width, height);
    }
  }

  @Override
  public void drawRect(Color color, int x, int y, int width, int height) {
    drawRect(color, x, y, width, height, true);
  }

  @Override
  public void drawDot(Color color, int x, int y) {
    org.eclipse.swt.graphics.Color current = toSWTColor(color);
    target.setBackground(current);
    target.setForeground(current);
    target.drawRectangle(x - PIXEL_WITH / 2, y - PIXEL_WITH / 2, PIXEL_WITH, PIXEL_WITH);
  }

  @Override
  public void drawOval(Color color, int x, int y, int width, int height) {
    target.setBackground(toSWTColor(color));
    target.fillOval(x, y, width, height);
    target.setForeground(fgColor);
    target.drawOval(x, y, width, height);
  }

  @Override
  public void drawBackground(Color color, int width, int heigth) {

    bgColor = toSWTColor(color);

    target.setBackground(bgColor);
    target.setForeground(fgColor);
    target.fillRectangle(0, 0, width, heigth);
  }

  /**********************************************************************************/

  /**
   * Converts a {@link org.jzy3d.colors.Color Imaging Color} into a
   * {@link org.eclipse.swt.graphics.Color SWT Color}. Note that SWT colors do not have an alpha
   * channel.
   */
  public org.eclipse.swt.graphics.Color toSWTColor(Color color) {
    return resourceManager
        .createColor(new RGB((int) (255 * color.r), (int) (255 * color.g), (int) (255 * color.b)));
  }

  /**********************************************************************************/

  private final GC target;
  private org.eclipse.swt.graphics.Color bgColor = WHITE; // default bg
  private final org.eclipse.swt.graphics.Color fgColor = BLACK; // default fg

  // system colors are allocated by the system and need not to be dispose manually
  private static final org.eclipse.swt.graphics.Color WHITE =
      Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
  private static final org.eclipse.swt.graphics.Color BLACK =
      Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

  private static final int PIXEL_WITH = 1;
}
