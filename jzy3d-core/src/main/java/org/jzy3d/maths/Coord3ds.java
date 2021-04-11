package org.jzy3d.maths;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;

/**
 * A set of points.
 * 
 * @author Martin Pernollet
 */
public class Coord3ds {
  public float x[];
  public float y[];
  public float z[];

  public float r[];
  public float g[];
  public float b[];
  public float a[];

  public Coord3ds(int size) {
    x = new float[size];
    y = new float[size];
    z = new float[size];

    r = new float[size];
    g = new float[size];
    b = new float[size];
    a = new float[size];
  }

  public void set(int id, float x, float y, float z, float r, float g, float b, float a) {
    this.x[id] = x;
    this.y[id] = y;
    this.z[id] = z;

    this.r[id] = r;
    this.g[id] = g;
    this.b[id] = b;
    this.a[id] = a;
  }

  public List<Coord3d> coords() {
    List<Coord3d> coords = new ArrayList<>();
    for (int i = 0; i < x.length; i++) {
      coords.add(new Coord3d(x[i], y[i], z[i]));
    }
    return coords;
  }

  public Coord3d[] coordsArray() {
    Coord3d[] coords = new Coord3d[x.length];
    for (int i = 0; i < x.length; i++) {
      coords[i] = new Coord3d(x[i], y[i], z[i]);
    }
    return coords;
  }

  public Color[] colorsArray() {
    Color[] colors = new Color[r.length];
    for (int i = 0; i < r.length; i++) {
      colors[i] = new Color(r[i], g[i], b[i], a[i]);
    }
    return colors;
  }

}
