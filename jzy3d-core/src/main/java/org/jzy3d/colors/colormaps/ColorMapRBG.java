package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;

public class ColorMapRBG extends AbstractColorMap implements IColorMap {
  public ColorMapRBG() {
    super();
  }

  /** @inheritDoc */
  @Override
  public Color getColor(double x, double y, double z, double zMin, double zMax) {
    double rel_value = processRelativeZValue(z, zMin, zMax);
    float v = (float) colorComponentRelative(rel_value, 0.25f, 0.25f, 0.75f);
    float b = (float) colorComponentRelative(rel_value, 0.50f, 0.25f, 0.75f);
    float r = (float) colorComponentRelative(rel_value, 0.75f, 0.25f, 0.75f);
    return new Color(r, v, b);
  }
}
