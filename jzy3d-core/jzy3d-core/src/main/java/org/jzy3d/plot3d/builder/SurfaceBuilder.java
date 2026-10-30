package org.jzy3d.plot3d.builder;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.builder.concrete.OrthonormalTessellator;
import org.jzy3d.plot3d.builder.concrete.RingTessellator;
import org.jzy3d.plot3d.builder.delaunay.DelaunayTessellator;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.primitives.Shape;

public class SurfaceBuilder {
  /**
   * Apply a function to an orthonormal grid and return a drawable surface object.
   */
  public Shape orthonormal(Mapper mapper, Range range, int steps) {
    return orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
  }

  /**
   * Apply a function to an orthonormal grid and return a drawable surface object.
   */
  public Shape orthonormal(OrthonormalGrid grid, Mapper mapper) {
    OrthonormalTessellator tesselator = new OrthonormalTessellator();
    return (Shape) tesselator.build(grid.apply(mapper));
  }

  /**
   * Apply a function to an orthonormal grid and then slice a ring from it between a min and max
   * radius
   */
  public Shape ring(OrthonormalGrid grid, Mapper mapper, float ringMin, float ringMax) {
    RingTessellator tesselator = new RingTessellator(ringMin, ringMax,
        new ColorMapper(new ColorMapRainbow(), 0, 1), Color.BLACK);
    return (Shape) tesselator.build(grid.apply(mapper));
  }

  /**
   * Apply a function to an orthonormal grid and then slice a ring from it between a min and max
   * radius
   */
  public Shape ring(OrthonormalGrid grid, Mapper mapper, float ringMin, float ringMax,
      ColorMapper cmap, Color factor) {
    RingTessellator tesselator = new RingTessellator(ringMin, ringMax, cmap, factor);
    return (Shape) tesselator.build(grid.apply(mapper));
  }

  /**
   * Build a surface out of an disordered point mesh.
   * 
   * The tesselation is built with a 3d Delaunay algorithm.
   * 
   * Note that this algorithm works better for horizontal distribution than vertical distribution of
   * points. The algorithm is based on a terrain library and hence assume a set of point than lie as
   * a floor rather than a wall.
   * 
   * Beside this limitation, Delaunay remains a fast and efficient solution when sampling points can
   * not be acquired based on a known structure (such as orthonormal grid).
   */
  public Shape delaunay(List<Coord3d> coordinates) {
    DelaunayTessellator tesselator = new DelaunayTessellator();
    return (Shape) tesselator.build(coordinates);
  }

  /* BIG SURFACE */

  /**
   * Apply a function to an orthonormal grid and return a drawable surface object optimized for
   * large number of polygons through OpenGL Display Lists.
   */
  public CompileableComposite orthonormalBig(OrthonormalGrid grid, Mapper mapper) {
    Tessellator tesselator = new OrthonormalTessellator();
    Shape s1 = (Shape) tesselator.build(grid.apply(mapper));
    return buildComposite(applyStyling(s1));
  }

  public Shape applyStyling(Shape s) {
    s.setColorMapper(
        new ColorMapper(DEFAULT_COLORMAP, s.getBounds().getZmin(), s.getBounds().getZmax()));
    s.setFaceDisplayed(DEFAULT_FACE_DISPLAYED);
    s.setWireframeDisplayed(DEFAULT_WIREFRAME_DISPLAYED);
    s.setWireframeColor(DEFAULT_WIREFRAME_COLOR);
    return s;
  }

  /**
   * Build a {@link CompileableComposite} out of a {@link Shape}, which allows faster rendering.
   * 
   * @param s
   * @return
   */
  public CompileableComposite buildComposite(Shape s) {
    CompileableComposite sls = new CompileableComposite();
    sls.add(s.getDrawables());
    sls.setColorMapper(new ColorMapper(DEFAULT_COLORMAP, sls.getBounds().getZmin(),
        sls.getBounds().getZmax(), DEAFAULT_COLORFACTOR));
    sls.setFaceDisplayed(s.isFaceDisplayed());
    sls.setWireframeDisplayed(s.isWireframeDisplayed());
    sls.setWireframeColor(s.getWireframeColor());
    sls.setSpaceTransformer(s.getSpaceTransformer());
    return sls;
  }

  protected static IColorMap DEFAULT_COLORMAP = new ColorMapRainbow();
  protected static Color DEAFAULT_COLORFACTOR = new Color(1, 1, 1, 1f);
  protected static Color DEFAULT_WIREFRAME_COLOR = Color.BLACK;

  protected static boolean DEFAULT_FACE_DISPLAYED = true;
  protected static boolean DEFAULT_WIREFRAME_DISPLAYED = false;
}
