package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;

public class ColorMapRedAndGreen extends AbstractColorMap implements IColorMap {

  public ColorMapRedAndGreen() {
    super();
  }

  /** @inheritDoc */
  @Override
  public Color getColor(double x, double y, double z, double zMin, double zMax) {
    double rel_value = processRelativeZValue(z, zMin, zMax);
    float b = 0;
    float v = (float) colorComponentRelative(rel_value, 0.375f, 0.25f, 0.75f);
    float r = (float) colorComponentRelative(rel_value, 0.625f, 0.25f, 0.75f);
    return new Color(r, v, b);
  }
}
