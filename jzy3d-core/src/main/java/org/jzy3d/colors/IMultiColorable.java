package org.jzy3d.colors;

/**
 * {@link IMultiColorable} objects may have several colors interpolated between each of their
 * individual points colors. <br>
 * A {@link IMultiColorable} object requires a {@link ColorMapper} that defines a strategy for
 * coloring points according to their position.
 *
 * @author Martin Pernollet
 */
public interface IMultiColorable {
  /** Set the colormapper that will be used by the Drawable, instead of using precomputed colors. */
  public void setColorMapper(ColorMapper mapper);

  /** Get the colormapper. */
  public ColorMapper getColorMapper();
}
