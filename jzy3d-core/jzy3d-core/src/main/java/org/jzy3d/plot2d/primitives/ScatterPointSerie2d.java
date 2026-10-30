package org.jzy3d.plot2d.primitives;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.ConcurrentScatterPoint;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.LightPoint;

// TODO : create LightPoint that don't hold a bounding box
public class ScatterPointSerie2d implements Serie2d {
  protected ConcurrentScatterPoint scatter;
  protected String name;
  protected Color defaultColor = Color.YELLOW.clone();

  public ScatterPointSerie2d(String name) {
    this.name = name;
    this.scatter = makeDrawable();
  }

  public ConcurrentScatterPoint getScatter() {
    return scatter;
  }

  public void setScatter(ConcurrentScatterPoint scatter) {
    this.scatter = scatter;
  }

  public void setName(String name) {
    this.name = name;
  }

  protected ConcurrentScatterPoint makeDrawable() {
    ConcurrentScatterPoint s = new ConcurrentScatterPoint();
    s.setWidth(3);
    return s;
  }

  @Override
  public void add(float x, float y) {
    scatter.add(new LightPoint(new Coord3d(x, y, 0), defaultColor));
  }

  @Override
  public void add(double x, double y) {
    scatter.add(new LightPoint(new Coord3d(x, y, 0), defaultColor));
  }

  @Override
  public void add(Coord2d c) {
    scatter.add(new LightPoint(new Coord3d(c.x, c.y, 0), defaultColor));
  }

  @Override
  public void add(Coord2d c, Color color) {
    scatter.add(new LightPoint(new Coord3d(c.x, c.y, 0), color));
  }

  @Override
  public void add(float x, float y, Color color) {
    scatter.add(new LightPoint(new Coord3d(x, y, 0), color));
  }

  @Override
  public void add(double x, double y, Color color) {
    scatter.add(new LightPoint(new Coord3d(x, y, 0), color));
  }


  @Override
  public void add(List<Coord2d> c) {
    for (Coord2d c2 : c) {
      scatter.add(new LightPoint(new Coord3d(c2.x, c2.y, 0), defaultColor));
    }
  }

  @Override
  public void setColor(Color color) {
    defaultColor = color;
  }

  @Override
  public Color getColor() {
    return defaultColor;// line.getWireframeColor();
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

