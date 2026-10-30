package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.algorithms.interpolation.IInterpolator;
import org.jzy3d.maths.algorithms.interpolation.algorithms.BernsteinInterpolator;

/**
 * Creates an interpolated line to make a smooth 3d curve based on control points.
 * 
 * @author martin
 */
public class LineStripInterpolated extends Composite {
  protected LineStripInterpolated() {}

  public LineStripInterpolated(List<Coord3d> controlPoints, int resolution) {
    this(new BernsteinInterpolator(), controlPoints, resolution, true);
  }

  public LineStripInterpolated(IInterpolator interpolator, List<Coord3d> controlPoints,
      int resolution) {
    this(interpolator, controlPoints, resolution, true);
  }

  public LineStripInterpolated(IInterpolator interpolator, List<Coord3d> controlPoints,
      int resolution, boolean drawPoints) {
    this.controlCoords = controlPoints;
    this.resolution = resolution;
    this.interpolatedCoords = interpolator.interpolate(controlPoints, resolution);

    this.interpolatedPoints = toPoints(interpolatedCoords, Color.BLUE, 3);
    this.controlPoints = toPoints(controlCoords, Color.RED, 5);
    this.line = new LineStrip(interpolatedCoords);
    this.line.setWireframeColor(Color.BLACK);

    add(this.line);

    if (drawPoints) {
      add(this.controlPoints);
      add(this.interpolatedPoints);
    }

    // this.line.setShowPoints(drawPoints);
  }

  protected Point toPoint(Coord3d coord, Color color, float width) {
    return new Point(coord, color, width);
  }

  protected List<Point> toPoints(List<Coord3d> coords, Color color, float width) {
    List<Point> points = new ArrayList<Point>();
    for (Coord3d coord : coords)
      points.add(toPoint(coord, color, width));
    return points;
  }

  /**********/

  public LineStrip getLine() {
    return line;
  }

  public List<Point> getControlPoints() {
    return controlPoints;
  }

  public List<Point> getInterpolatedPoints() {
    return interpolatedPoints;
  }

  public List<Coord3d> getControlCoords() {
    return controlCoords;
  }

  public List<Coord3d> getInterpolatedCoords() {
    return interpolatedCoords;
  }

  public int getResolution() {
    return resolution;
  }

  protected LineStrip line;

  protected List<Point> controlPoints;
  protected List<Point> interpolatedPoints;

  protected List<Coord3d> controlCoords;
  protected List<Coord3d> interpolatedCoords;
  protected int resolution;
}
