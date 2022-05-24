package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.IColorMappable;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/**
 * This interface defines the set of methods that any concrete colormap should define in order to be
 * used by an object implementing the {@link IMultiColorable} interface.
 * 
 * The ColorMappable interface impose to an object to provide a Z-scaling, that is, a minimum and
 * maximum value on the Z axis. These values are used by concrete colormaps in order to set an
 * interval for the possible colors.
 * 
 * @author Martin Pernollet
 */
public interface IColorMap {
  public Color getColor(double x, double y, double z, double zMin, double zMax);

  /**
   * Computes the color of a {@link IColorMappable} object according to the Z value of each of its
   * components.
   * 
   * @param colorable A {@link ColorMappable} object.
   * @param x
   * @param y
   * @param z
   * @return a color for the given point.
   */
  Color getColor(IColorMappable colorable, double x, double y, double z);

  /**
   * 
   * @param colorable A {@link ColorMappable} object.
   * @param v the variable that is Color-dependent, and can be independent of the coordinates.
   * @return a color for the given point.
   */

  Color getColor(IColorMappable colorable, double v);

  /**
   * Indicates if the colormap use the standard or reverted color direction
   * 
   * @param isStandard
   */
  public void setDirection(boolean isStandard);

  /**
   * 
   * @return the colormap direction: true if standard, false if reverted.
   */
  public boolean getDirection();

  /**
   * Return the influence of a color, by comparing the input value to the color spectrum.
   * 
   * The design of a colormap implies defining the influence of each base color (red, green, and
   * blue) over the range of input data. For this reason, the value given to this function is a
   * number between 0 and 1, indicating the position of the input value in the colormap. Any value
   * standing outside of colormap boundaries should have the "maximal" or "minimal" color.
   * 
   * Exemple: A rainbow colormap is a progressive transition from blue, to green and red. The mix
   * between these 3 colors, may be expressed by the definition of 3 functions:
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
   * 
   * In order to get the color of an input value standing between 0 and 1, the user should call:
   * 
   * <pre>
   * <code>
   * float blue  = (float) colorComponentRelative( rel_value, 0.25, 0.25, 0.75 );
   * float green = (float) colorComponentRelative( rel_value, 0.50, 0.25, 0.75 );
   * float red   = (float) colorComponentRelative( rel_value, 0.75, 0.25, 0.75 );
   * 
   * return new Color4f( red, green, blue, 1.0f );
   * </code>
   * </pre>
   * 
   * @param value
   * @param center
   * @param topwidth
   * @param bottomwidth
   * @return color influence.
   */
  public double colorComponentRelative(double value, double center, double topwidth,
      double bottomwidth);


  /**
   * Defines a simple function for a color.
   * 
   * Between bLeft and tLeft, as well as tRight and bRight, the output value is a linear
   * interpolation between 0 and 1.
   * 
   * <pre>
   * <code>
   *                      tLeft       tRight
   *                        /-----------\
   *                       /             \ 
   *                      /               \
   *              -------|-----------------|--------
   *                    bLeft            bRight  
   * </code>
   * </pre>
   * 
   * @param value
   * @param bLeft under this value, the color is 0
   * @param bRight up to this value, the color is 0
   * @param tLeft from this value until @link tRight, the color is 1
   * @param tRight from @link tLeft until this value, the color is 1
   * @return color
   */
  public double colorComponentAbsolute(double value, double bLeft, double bRight, double tLeft,
      double tRight);

  public SpaceTransformer getSpaceTransformer();

  public void setSpaceTransformer(SpaceTransformer transformer);

}
