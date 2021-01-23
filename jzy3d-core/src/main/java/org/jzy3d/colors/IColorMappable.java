package org.jzy3d.colors;

import org.jzy3d.colors.colormaps.IColorMap;

/**
 * ColorMappable interface.
 * <p>
 * This interface defines a set of methods that any colored object using a concrete
 * {@link IColorMap} must provide.
 * 
 * @author Martin Pernollet
 */
public interface IColorMappable {

  /**
   * Retrieve the lower value boundary for a {@link IColorMap}.
   * 
   * @return the minimum Z value
   */
  public double getMin();

  /**
   * Retrieve the upper value boundary for a {@link IColorMap}.
   * 
   * @return the maximum Z value
   */
  public double getMax();

  /**
   * Set the lower value boundary for a {@link IColorMap}.
   * 
   * @param value the minimum Z value
   */
  public void setMin(double value);

  /**
   * Set the upper value boundary for a {@link IColorMap}.
   * 
   * @param value the maximum Z value
   */
  public void setMax(double value);

}
