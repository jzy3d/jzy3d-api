package org.jzy3d.contour;

import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.maths.Coord3d;


public class DefaultContourColoringPolicy implements IContourColoringPolicy {
  public DefaultContourColoringPolicy(ColorMapper cmapper) {
    this.cmapper = cmapper;
  }

  @Override
  public int getRGB(double z) {
    if (z != AbstractContourGenerator.NON_CONTOUR) {
      Coord3d coord = new Coord3d(0.0f, 0.0f, z);
      Color color = cmapper.getColor(coord);
      return AWTColor.toAWT(color).getRGB();
    } else {
      return WHITE_ALPHA_RGB;
    }
  }

  public ColorMapper getColorMapper() {
    return cmapper;
  }

  public void setColorMmapper(ColorMapper cmapper) {
    this.cmapper = cmapper;
  }



  protected ColorMapper cmapper;
  protected static int WHITE_ALPHA_RGB = AWTColor.toAWT(Color.WHITE).getRGB();
}
