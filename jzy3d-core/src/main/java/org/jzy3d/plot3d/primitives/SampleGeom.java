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
    Range range = new Range(-3, 3);

    return surface(range, range);
  }

  public static Shape surface(Range xRange, Range yRange) {
    return surface(xRange, yRange, 0.5f);
  }
  
  public static Shape surface(Range xRange, Range yRange, float alpha) {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };
    int steps = 50;

    Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(xRange, steps, yRange, steps), mapper);
    
    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, alpha));

    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    surface.setWireframeWidth(1);
    
    return surface;
  }

  public static Scatter scatter(int size, int width) {
    return scatter(size, width, 0.75f);
  }
  
  public static Scatter scatter(int size, int width, float alpha) {
    float x;
    float y;
    float z;

    Coord3d[] points = new Coord3d[size];
    Color[] colors = new Color[size];

    Random r = new Random();
    r.setSeed(0);

    for (int i = 0; i < size; i++) {
      x = r.nextFloat() - 0.5f;
      y = r.nextFloat() - 0.5f;
      z = r.nextFloat() - 0.5f;
      points[i] = new Coord3d(x, y, z);
      colors[i] = new Color(x, y, z, alpha);
    }

    Scatter scatter = new Scatter(points, colors);
    scatter.setWidth(width);
    return scatter;
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
