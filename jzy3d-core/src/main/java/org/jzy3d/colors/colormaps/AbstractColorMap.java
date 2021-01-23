package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.IColorMappable;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public abstract class AbstractColorMap implements IColorMap {
  public abstract Color getColor(double x, double y, double z, double zMin, double zMax);

  public AbstractColorMap() {
    direction = true;
  }

  @Override
  public void setDirection(boolean isStandard) {
    direction = isStandard;
  }

  @Override
  public boolean getDirection() {
    return direction;
  }

  @Override
  public Color getColor(IColorMappable colorable, double x, double y, double z) {
    return getColor(x, y, z, colorable.getMin(), colorable.getMax());
  }

  /**
   * Compute a value in [0;1] to pick a color in a [0;1] color range.
   * 
   * Basically normalize the z value in its original range to the color range.
   * 
   * @return (z - zMin) / (zMax - zMin) OR (zMax - z) / (zMax - zMin) OR 1 OR 0
   */
  protected double processRelativeZValue(double z, double zMin, double zMax) {
    double rel_value = 0;

    if (z < zMin) {
      if (direction)
        rel_value = 0;
      else
        rel_value = 1;
    } else if (z > zMax) {
      if (direction)
        rel_value = 1;
      else
        rel_value = 0;
    } else {
      if (direction)
        rel_value = (z - zMin) / (zMax - zMin);
      else
        rel_value = (zMax - z) / (zMax - zMin);
    }

    return rel_value;
  }

  @Override
  public Color getColor(IColorMappable colorable, double z) {
    return getColor(0.0f, 0.0f, z, colorable.getMin(), colorable.getMax());
  }

  /** @inheritDoc */
  @Override
  public double colorComponentRelative(double value, double center, double topwidth,
      double bottomwidth) {
    return colorComponentAbsolute(value, center - (bottomwidth / 2), center + (bottomwidth / 2),
        center - (topwidth / 2), center + (topwidth / 2));
  }

  public double colorComponentRelativeNoLeftBorder(double value, double center, double topwidth,
      double bottomwidth) {
    return colorComponentAbsolute(value, center - (bottomwidth / 2), center + (bottomwidth / 2),
        center - (bottomwidth / 2), center + (topwidth / 2));
  }

  public double colorComponentRelativeNoRightBorder(double value, double center, double topwidth,
      double bottomwidth) {
    return colorComponentAbsolute(value, center - (bottomwidth / 2), center + (bottomwidth / 2),
        center - (topwidth / 2), center + (bottomwidth / 2));
  }


  /** @inheritDoc */
  @Override
  public double colorComponentAbsolute(double value, double bLeft, double bRight, double tLeft,
      double tRight) {
    double output = 0;
    // a gauche ou a droite du creneau
    if ((value < bLeft) || (value >= bRight)) {
      output = 0;
    }
    // sur le plateau haut
    else if ((value >= tLeft) && (value < tRight)) {
      output = 1;
    }
    // sur la pente gauche du creneau
    else if ((value >= bLeft) && (value < tLeft)) {
      output = (value - bLeft) / (tLeft - bLeft);
    }
    // sur la pente droite du creneau
    else if ((value >= tRight) && (value < bRight)) {
      output = (value - bRight) / (tRight - bRight);
    }
    return output;
  }

  @Override
  public SpaceTransformer getSpaceTransformer() {
    return spaceTransformer;
  }

  @Override
  public void setSpaceTransformer(SpaceTransformer transformer) {
    this.spaceTransformer = transformer;
  }

  protected boolean direction;
  protected SpaceTransformer spaceTransformer;

}
