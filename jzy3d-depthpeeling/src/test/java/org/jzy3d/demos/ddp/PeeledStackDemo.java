package org.jzy3d.demos.ddp;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.factories.DepthPeelingPainterFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Cylinder;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class PeeledStackDemo {
  static Color STACK_FACE = new Color(.5f, .5f, .5f, .5f);
  static Color STACK_WIRE = Color.BLACK;
  static int STACK_WIDTH = 100;
  static int STACK_HEIGHT = 10;
  static int CYLINDER_STEPS = 30;


  public static void main(String[] args) {
    AWTChartFactory f = new AWTChartFactory(new DepthPeelingPainterFactory());

    Chart chart = f.newChart(Quality.Nicest());


    Coord3d c1 = new Coord3d(0, 0, -2.5);
    Coord3d c2 = new Coord3d(10, 10, 19);

    cylinder(chart, c1, 5, 15, Color.CYAN);
    cylinder(chart, c2, 5, 15, Color.CYAN);
    line(chart, c1, c2, 3);
    stack(chart, STACK_WIDTH, STACK_HEIGHT, 0, STACK_FACE, STACK_WIRE);
    stack(chart, STACK_WIDTH, STACK_HEIGHT, 20, STACK_FACE, STACK_WIRE);

    chart.getView().setAxisDisplayed(false);
    chart.getView().setSquared(true);
    chart.open(600, 600);
    chart.getMouse();
  }

  /* STACK PRIMITIVES */

  public static void line(Chart chart, Coord3d c1, Coord3d c2, int width) {
    LineStrip ls1 = new LineStrip();
    ls1.add(new Point(c1, Color.CYAN));
    ls1.add(new Point(c2, Color.CYAN));
    ls1.setWidth(width);
    chart.getScene().add(ls1);
  }

  public static void cylinder(Chart chart, Coord3d c1Position, float height, float radius,
      Color color) {
    Cylinder c1 = new Cylinder();
    c1.setData(c1Position, height, radius, CYLINDER_STEPS, 1, color);
    chart.getScene().add(c1);
  }

  public static void stack(Chart chart, int width, int height, int position, Color face,
      Color wireframe) {
    BoundingBox3d bounds = new BoundingBox3d(-width / 2, width / 2, -width / 2, width / 2,
        position - height / 2, position + height / 2);
    ParallelepipedComposite p1 = new ParallelepipedComposite(bounds);
    p1.setColor(face);
    p1.setWireframeColor(wireframe);
    chart.getScene().add(p1);
  }
}
