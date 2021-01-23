package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;

/**
 * A rainbow colormap is a progressive transition from blue, to green and then red. The input is
 * normalized according to the colormap boundaries (through setZmin()/setZmax()). The mix between
 * these 3 colors, may be expressed by the definition of 3 functions:
 * 
 * <pre>
* <code>
*       blue     green     red
*     /-------\/-------\/-------\
*    /        /\       /\        \
*   /        /  \     /  \        \
*  /        /    \   /    \        \
* |----------------|----------------|
* 0               0.5               1
* </code>
 * </pre>
 */
public class ColorMapRainbow extends AbstractColorMap implements IColorMap {
  public ColorMapRainbow() {
    super();
  }

  /** @inheritDoc */
  @Override
  public Color getColor(double x, double y, double z, double zMin, double zMax) {
    double rel_value = processRelativeZValue(z, zMin, zMax);

    float b = (float) colorComponentRelative(rel_value, 0.25f, 0.25f, 0.75f);
    float v = (float) colorComponentRelative(rel_value, 0.50f, 0.25f, 0.75f);
    float r = (float) colorComponentRelative(rel_value, 0.75f, 0.25f, 0.75f);

    return new Color(r, v, b);
  }
}
