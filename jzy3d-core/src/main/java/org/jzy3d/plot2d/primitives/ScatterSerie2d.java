package org.jzy3d.plot2d.primitives;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRedAndGreen;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.primitives.ConcurrentScatterMultiColorList;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.ScatterMultiColorList;

public class ScatterSerie2d implements Serie2d {
  protected ScatterMultiColorList scatter;
  protected String name;

  public ScatterSerie2d(String name) {
    this.name = name;
    this.scatter = makeDrawable();
  }

  protected ScatterMultiColorList makeDrawable() {
    ColorMapRedAndGreen g = colormap();
    ColorMapper m = colormapper(g);
    ScatterMultiColorList s = new ConcurrentScatterMultiColorList(m);
    s.setWidth(3);
    return s;
  }

  public ColorMapper colormapper(ColorMapRedAndGreen g) {
    ColorMapper m = new ColorMapper(g, 0, 1) {
      @Override
      public Color getColor(Coord3d coord) {
        Color out = colormap.getColor(this, coord.x, coord.z, coord.y);

        if (factor != null)
          out.mul(factor);
        return out;
      }
    };
    return m;
  }

  public ColorMapRedAndGreen colormap() {
    ColorMapRedAndGreen g = new ColorMapRedAndGreen() {
      @Override
      public Color getColor(double x, double y, double z, double zMin, double zMax) {
        double rel_value = processRelativeZValue(z, zMin, zMax);

        float bCenter = 0.20f;
        float rCenter = 0.80f;
        float topWidth = 0.40f;
        float botWidth = 0.80f;

        float b = 0;
        float v = (float) colorComponentRelative(rel_value, bCenter, topWidth, botWidth);
        float r = (float) colorComponentRelative(rel_value, rCenter, topWidth, botWidth);
        return new Color(r, v, b);
      }
    };
    g.setDirection(false);
    return g;
  }

  @Override
  public void add(float x, float y) {
    scatter.add(new Coord3d(x, y, 0));
  }

  @Override
  public void add(double x, double y) {
    scatter.add(new Coord3d(x, y, 0));
  }

  @Override
  public void add(Coord2d c) {
    scatter.add(new Coord3d(c.x, c.y, 0));
  }

  @Override
  public void add(Coord2d c, Color color) {
    scatter.add(new Coord3d(c.x, c.y, 0));
  }

  @Override
  public void add(float x, float y, Color color) {
    throw new NotImplementedException();
  }

  @Override
  public void add(double x, double y, Color color) {
    throw new NotImplementedException();
  }

  @Override
  public void add(List<Coord2d> c) {
    for (Coord2d c2 : c) {
      scatter.add(new Coord3d(c2.x, c2.y, 0));
    }
  }

  @Override
  public void setColor(Color color) {
    // line.setColor(color);
  }

  @Override
  public Color getColor() {
    return null;// line.getWireframeColor();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Drawable getDrawable() {
    return scatter;
  }

  @Override
  public void clear() {
    scatter.clear();
  }

  @Override
  public void setWidth(int width) {
    scatter.setWidth(width);
  }
}
