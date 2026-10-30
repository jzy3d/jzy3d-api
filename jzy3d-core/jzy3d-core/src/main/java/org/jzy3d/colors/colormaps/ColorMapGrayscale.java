package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;


public class ColorMapGrayscale extends AbstractColorMap implements IColorMap {
  public ColorMapGrayscale() {
    super();
  }

  /** @inheritDoc */
  @Override
  public Color getColor(double x, double y, double z, double zMin, double zMax) {
    double rel_value = processRelativeZValue(z, zMin, zMax);

    float b = (float) rel_value;
    float v = (float) rel_value;
    float r = (float) rel_value;

    return new Color(r, v, b);
  }
}
