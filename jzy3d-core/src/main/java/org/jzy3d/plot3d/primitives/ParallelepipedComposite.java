package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;

/**
 * A composite implementation of a parallelepiped, meaning it can be decomposed into distinct
 * primitive for polygon ordering.
 * 
 * Supports two kind of sub polygon: culled or simple.
 */
public class ParallelepipedComposite extends Composite
    implements ISingleColorable, IMultiColorable {
  public enum PolygonType {
    SIMPLE, CULLED
  }

  /** Initialize a parallelepiped. */
  public ParallelepipedComposite() {
    this(PolygonType.CULLED);
  }

  public ParallelepipedComposite(PolygonType type) {
    super();
    this.type = type;
  }

  /** Initialize a parallelepiped. */
  public ParallelepipedComposite(BoundingBox3d b) {
    this(b, PolygonType.CULLED);
  }

  public ParallelepipedComposite(BoundingBox3d b, PolygonType type) {
    super();
    setData(b);
  }

  /** Set the parallelepiped data. */
  public void setData(BoundingBox3d box) {
    if (bbox == null)
      bbox = box;
    else {
      bbox.reset();
      bbox.add(box);
    }

    quads = new Polygon[6];

    quads[0] = createPolygon();
    quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmax())));
    quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmin())));
    quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmin())));
    quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmax())));

    quads[1] = createPolygon();
    quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmax())));
    quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmin())));
    quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmin())));
    quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmax())));

    quads[2] = createPolygon();
    quads[2].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmax())));
    quads[2].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmin())));
    quads[2].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmin())));
    quads[2].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmax())));

    quads[3] = createPolygon();
    quads[3].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmax())));
    quads[3].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmin())));
    quads[3].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmin())));
    quads[3].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmax())));

    quads[4] = createPolygon();
    quads[4].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmax())));
    quads[4].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmax())));
    quads[4].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmax())));
    quads[4].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmax())));

    quads[5] = createPolygon();
    quads[5].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmin())));
    quads[5].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmin())));
    quads[5].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmin())));
    quads[5].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmin())));

    for (Polygon p : quads)
      add(p);
  }

  protected Polygon createPolygon() {
    if (type == PolygonType.CULLED)
      return new Polygon();
    else if (type == PolygonType.SIMPLE)
      return new SimplePolygon();
    else
      throw new RuntimeException("unknown polygon type: " + type);
  }

  public void setPolygonOffsetFill(boolean value) {
    for (Polygon polygon : quads)
      polygon.setPolygonOffsetFillEnable(value);
  }

  public void setPolygonMode(PolygonMode mode) {
    for (Polygon polygon : quads)
      polygon.setPolygonMode(mode);
  }

  protected Polygon quads[];
  protected PolygonType type = PolygonType.CULLED;
}
