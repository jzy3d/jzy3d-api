package org.jzy3d.plot3d.builder.concrete;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.builder.Tessellator;
import org.jzy3d.plot3d.primitives.ColoredWireframePolygon;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.TesselatedPolygon;
import org.jzy3d.plot3d.primitives.WaterfallComposite;

/**
 * Build a drawable Waterfall using <a href="">Matlab style</a>
 * 
 * @author Jacob Filik
 */
public class WaterfallTessellator extends Tessellator {

  @Override
  public Shape build(float[] x, float[] y, float[] z) {
    if (x.length * y.length != z.length)
      throw new IllegalArgumentException("length of y must equal x.length*z.length");

    // Calculate min and max to determine sensible vale
    float min = Float.POSITIVE_INFINITY;
    float max = Float.NEGATIVE_INFINITY;

    for (int i = 0; i < z.length; i++) {
      if (z[i] < min)
        min = z[i];
      if (z[i] > max)
        max = z[i];
    }

    // Apply small offset to min for drawing bottom line
    if (min == max) {
      min = min - 10E-3f;
    } else {
      min = min - ((max - min) / 10E3f);
    }

    WaterfallComposite output = new WaterfallComposite();

    for (int i = 0; i < y.length; i++) {
      ColoredWireframePolygon coloredPoly = new ColoredWireframePolygon();
      Point start = new Point(new Coord3d(x[0], y[i], min));
      coloredPoly.add(start);
      Shape fill = new Shape();
      for (int j = 0; j < x.length - 1; j++) {
        Coord3d c0 = new Coord3d(x[j], y[i], min);
        Point p0 = new Point(c0, Color.WHITE);
        Coord3d c1 = new Coord3d(x[j], y[i], z[j + (i * x.length)]);
        Point p1 = new Point(c1, Color.WHITE);
        Coord3d c2 = new Coord3d(x[j + 1], y[i], z[j + (i * x.length) + 1]);
        Point p2 = new Point(c2, Color.WHITE);
        Coord3d c3 = new Coord3d(x[j + 1], y[i], min);
        Point p3 = new Point(c3, Color.WHITE);
        TesselatedPolygon tess = new TesselatedPolygon(new Point[] {p0, p1, p2, p3});

        Coord3d coord3d = new Coord3d(x[j], y[i], z[j + (i * x.length)]);
        coloredPoly.add(new Point(coord3d));

        fill.add(tess);
      }
      int xEnd = x.length - 1;
      coloredPoly.add(new Point(new Coord3d(x[xEnd], y[i], z[xEnd + (x.length * i)])));
      coloredPoly.add(new Point(new Coord3d(x[xEnd], y[i], min)));
      coloredPoly.setWireframeWidth(1f);

      output.add(coloredPoly, fill);
    }

    return output;
  }

}
