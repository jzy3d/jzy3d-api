package org.jzy3d.plot3d.primitives;

import java.util.Random;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.selectable.SelectableScatter;

public class SampleGeom {
  public static Shape surface() {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };
    Range range = new Range(-3, 3);
    int steps = 50;

    Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f));
    surface.setColorMapper(colorMapper);
    return surface;
  }
  
  public static SelectableScatter generateSelectableScatter(int npt) {
    Coord3d[] points = new Coord3d[npt];
    Color[] colors = new Color[npt];
    Random rng = new Random();
    rng.setSeed(0);
    for (int i = 0; i < npt; i++) {
      colors[i] = new Color(0, 64 / 255f, 84 / 255f);
      points[i] = new Coord3d(rng.nextFloat(), rng.nextFloat(), rng.nextFloat());
    }
    SelectableScatter dots = new SelectableScatter(points, colors);
    dots.setWidth(1);
    dots.setHighlightColor(Color.YELLOW);
    return dots;
  }


}
