package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;


public class ColorMapWhiteBlue extends AbstractColorMap implements IColorMap {
  public ColorMapWhiteBlue() {
    super();
  }

  /** @inheritDoc */
  @Override
  public Color getColor(double x, double y, double z, double zMin, double zMax) {
    double rel_value = processRelativeZValue(z, zMin, zMax);
    return new Color((float) rel_value, (float) rel_value, 1.0f);
  }
}
