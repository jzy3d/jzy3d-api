package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;

/**
 * A cube defined by a {@link BoundingBox3d}
 * 
 * @author Martin Pernollet
 */
public class CubeComposite extends Composite {
  protected Polygon north;
  protected Polygon south;
  protected Polygon west;
  protected Polygon east;
  protected Polygon near;
  protected Polygon far;


  public CubeComposite(Coord3d c, float radius) {
    this(new BoundingBox3d(c, radius*2));
  }

  public CubeComposite(BoundingBox3d box) {
    this(box, Color.BLACK, Color.BLUE);
  }

  public CubeComposite(BoundingBox3d box, Color wireframe, Color face) {
    //setWireframeColor(wireframe);
    setColor(face);

    north = new Quad();
    south = new Quad();
    west = new Quad();
    east = new Quad();
    near = new Quad();
    far = new Quad();

    north.add(new Point(new Coord3d(box.getXmin(), box.getYmin(), box.getZmax()), getColor()));
    north.add(new Point(new Coord3d(box.getXmin(), box.getYmax(), box.getZmax()), getColor()));
    north.add(new Point(new Coord3d(box.getXmax(), box.getYmax(), box.getZmax()), getColor()));
    north.add(new Point(new Coord3d(box.getXmax(), box.getYmin(), box.getZmax()), getColor()));

    south.add(new Point(new Coord3d(box.getXmin(), box.getYmin(), box.getZmin()), getColor()));
    south.add(new Point(new Coord3d(box.getXmin(), box.getYmax(), box.getZmin()), getColor()));
    south.add(new Point(new Coord3d(box.getXmax(), box.getYmax(), box.getZmin()), getColor()));
    south.add(new Point(new Coord3d(box.getXmax(), box.getYmin(), box.getZmin()), getColor()));

    west.add(new Point(new Coord3d(box.getXmin(), box.getYmin(), box.getZmin()), getColor()));
    west.add(new Point(new Coord3d(box.getXmin(), box.getYmax(), box.getZmin()), getColor()));
    west.add(new Point(new Coord3d(box.getXmin(), box.getYmax(), box.getZmax()), getColor()));
    west.add(new Point(new Coord3d(box.getXmin(), box.getYmin(), box.getZmax()), getColor()));

    east.add(new Point(new Coord3d(box.getXmax(), box.getYmin(), box.getZmin()), getColor()));
    east.add(new Point(new Coord3d(box.getXmax(), box.getYmax(), box.getZmin()), getColor()));
    east.add(new Point(new Coord3d(box.getXmax(), box.getYmax(), box.getZmax()), getColor()));
    east.add(new Point(new Coord3d(box.getXmax(), box.getYmin(), box.getZmax()), getColor()));


    near.add(new Point(new Coord3d(box.getXmin(), box.getYmin(), box.getZmin()), getColor()));
    near.add(new Point(new Coord3d(box.getXmax(), box.getYmin(), box.getZmin()), getColor()));
    near.add(new Point(new Coord3d(box.getXmax(), box.getYmin(), box.getZmax()), getColor()));
    near.add(new Point(new Coord3d(box.getXmin(), box.getYmin(), box.getZmax()), getColor()));

    far.add(new Point(new Coord3d(box.getXmin(), box.getYmax(), box.getZmin()), getColor()));
    far.add(new Point(new Coord3d(box.getXmax(), box.getYmax(), box.getZmin()), getColor()));
    far.add(new Point(new Coord3d(box.getXmax(), box.getYmax(), box.getZmax()), getColor()));
    far.add(new Point(new Coord3d(box.getXmin(), box.getYmax(), box.getZmax()), getColor()));

    // System.out.println(box.getXmin());
    add(north);
    add(south);
    add(west);
    add(east);
    add(near);
    add(far);

    bbox = box;

    setWireframeColor(wireframe);
  }

  public Polygon getNorth() {
    return north;
  }

  public Polygon getSouth() {
    return south;
  }

  public Polygon getWest() {
    return west;
  }

  public Polygon getEast() {
    return east;
  }

  public Polygon getNear() {
    return near;
  }

  public Polygon getFar() {
    return far;
  }
}
