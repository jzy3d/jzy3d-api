package org.jzy3d.plot2d.primitives;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.plot3d.primitives.Drawable;

public interface Serie2d {
  public void clear();

  public void add(float x, float y);

  public void add(double x, double y);

  public void add(float x, float y, Color color);

  public void add(double x, double y, Color color);

  public void add(Coord2d c);

  public void add(Coord2d c, Color color);

  public void add(List<Coord2d> c);

  public String getName();

  public void setWidth(int width);

  public void setColor(Color color);

  public Color getColor();

  public Drawable getDrawable();

  public enum Type {
    LINE, LINE_ON_OFF, SCATTER, SCATTER_POINTS
  }
}
