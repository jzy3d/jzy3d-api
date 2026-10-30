package org.jzy3d.plot3d.primitives;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;

/**
 * A helper to generate drawable surfaces.
 * 
 * @author Martin Pernollet
 */
public class Surface {
  public static boolean DEFAULT_FACE_DISPLAYED = true;
  public static boolean DEFAULT_WIREFRAME_DISPLAYED = false;

  public static Shape shape(Mapper f1, Range xyRange, int steps, IColorMap colormap, float alpha) {
    final Shape surface = new SurfaceBuilder().orthonormal(f1, xyRange, steps);
    surface.setColorMapper(new ColorMapper(colormap, surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, alpha)));
    surface.setFaceDisplayed(DEFAULT_FACE_DISPLAYED);
    surface.setWireframeDisplayed(DEFAULT_WIREFRAME_DISPLAYED);
    return surface;
  }

  public static Shape shape(Mapper f1, Range xRange, Range yRange, int steps, IColorMap colormap,
      float alpha) {
    final Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(xRange, steps, yRange, steps), f1);
    surface.setColorMapper(new ColorMapper(colormap, surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, alpha)));
    surface.setFaceDisplayed(DEFAULT_FACE_DISPLAYED);
    surface.setWireframeDisplayed(DEFAULT_WIREFRAME_DISPLAYED);
    return surface;
  }

  public static Shape shape(List<Coord3d> coordinates, IColorMap colormap, float alpha) {
    final Shape surface = new SurfaceBuilder().delaunay(coordinates);
    surface.setColorMapper(new ColorMapper(colormap, surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, alpha)));
    surface.setFaceDisplayed(DEFAULT_FACE_DISPLAYED);
    surface.setWireframeDisplayed(DEFAULT_WIREFRAME_DISPLAYED);
    return surface;
  }

}
