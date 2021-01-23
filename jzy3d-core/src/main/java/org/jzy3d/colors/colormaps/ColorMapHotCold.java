package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;


public class ColorMapHotCold extends AbstractColorMap implements IColorMap {
  public ColorMapHotCold() {
    super();
  }

  /** @inheritDoc */
  @Override
  public Color getColor(double x, double y, double z, double zMin, double zMax) {
    double rel_value = processRelativeZValue(z, zMin, zMax);

    float b = (float) colorComponentAbsolute(rel_value, -0.250, +0.875, +0.250, +0.500);
    float v = (float) colorComponentAbsolute(rel_value, +0.125, +0.875, +0.500, +0.500);
    float r = (float) colorComponentAbsolute(rel_value, +0.125, +1.250, +0.500, +0.750);

    return new Color(r, v, b);
  }
}
