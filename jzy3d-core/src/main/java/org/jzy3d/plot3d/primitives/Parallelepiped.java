package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.transform.Transform;



/**
 * A Parallelepiped is a parallelepiped rectangle that is Drawable and Wireframeable. A future
 * version of Rectangle3d should consider it as a Composite3d.
 * 
 * This class has been implemented for debugging purpose and inconsistency of its input w.r.t other
 * primitives should not be considered (no setData function).
 * 
 * @author Martin Pernollet
 */
public class Parallelepiped extends Wireframeable implements ISingleColorable, IMultiColorable {

  /** Initialize a parallelepiped. */
  public Parallelepiped() {
    super();
    bbox = new BoundingBox3d();
  }

  /** Initialize a parallelepiped. */
  public Parallelepiped(BoundingBox3d b) {
    super();
    bbox = new BoundingBox3d();
    setData(b);
  }

  /* */

  @Override
  public void draw(IPainter painter) {
    for (Polygon quad : quads)
      quad.draw(painter);
  }

  @Override
  public void setTransform(Transform transform) {
    this.transform = transform;
    for (Polygon quad : quads)
      quad.setTransform(transform);
  }

  /* */

  @Override
  public void setWireframeColor(Color color) {
    if (quads != null)
      for (Polygon quad : quads)
        quad.setWireframeColor(color);
  }

  @Override
  public void setWireframeDisplayed(boolean status) {
    if (quads != null)
      for (Polygon quad : quads)
        quad.setWireframeDisplayed(status);
  }

  @Override
  public void setWireframeWidth(float width) {
    if (quads != null)
      for (Polygon quad : quads)
        quad.setWireframeWidth(width);
  }

  @Override
  public void setFaceDisplayed(boolean status) {
    if (quads != null)
      for (Polygon quad : quads)
        quad.setFaceDisplayed(status);
  }

  @Override
  public Color getWireframeColor() {
    return quads[0].getWireframeColor();
  }

  @Override
  public boolean isWireframeDisplayed() {
    return quads[0].isWireframeDisplayed();
  }

  @Override
  public float getWireframeWidth() {
    return quads[0].getWireframeWidth();
  }

  @Override
  public boolean isFaceDisplayed() {
    return quads[0].isFaceDisplayed();
  }

  /**********************************************************************/

  /** Set the parallelepiped data. */
  public void setData(BoundingBox3d box) {
    bbox.reset();
    bbox.add(box);

    quads = new Polygon[6];

    quads[0] = new Polygon();
    quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmax())), false);
    quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmin())), false);
    quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmin())), false);
    quads[0].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmax())), false);
    quads[0].updateBounds();

    quads[1] = new Polygon();
    quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmax())), false);
    quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmin())), false);
    quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmin())), false);
    quads[1].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmax())), false);
    quads[1].updateBounds();

    quads[2] = new Polygon();
    quads[2].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmax())), false);
    quads[2].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmin())), false);
    quads[2].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmin())), false);
    quads[2].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmax())), false);
    quads[2].updateBounds();


    quads[3] = new Polygon();
    quads[3].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmax())), false);
    quads[3].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmin())), false);
    quads[3].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmin())), false);
    quads[3].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmax())), false);
    quads[3].updateBounds();

    quads[4] = new Polygon();
    quads[4].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmax())), false);
    quads[4].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmax())), false);
    quads[4].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmax())), false);
    quads[4].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmax())), false);
    quads[4].updateBounds();

    quads[5] = new Polygon();
    quads[5].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmin(), bbox.getZmin())), false);
    quads[5].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmin())), false);
    quads[5].add(new Point(new Coord3d(bbox.getXmin(), bbox.getYmax(), bbox.getZmin())), false);
    quads[5].add(new Point(new Coord3d(bbox.getXmax(), bbox.getYmax(), bbox.getZmin())), false);
    quads[5].updateBounds();
  }

  /* */

  @Override
  public void setColorMapper(ColorMapper mapper) {
    this.mapper = mapper;

    for (Polygon quad : quads) {
      quad.setColorMapper(mapper);
    }
  }

  @Override
  public ColorMapper getColorMapper() {
    return mapper;
  }

  @Override
  public void setColor(Color color) {
    this.color = color;

    for (Polygon quad : quads)
      quad.setColor(color);
  }

  @Override
  public Color getColor() {
    return color;
  }


  @Override
  public void applyGeometryTransform(Transform transform) {
    for (Polygon quad : quads) {
      quad.applyGeometryTransform(transform);
    }
  }

  /* */

  private ColorMapper mapper;
  private Polygon quads[];
  private Color color;

  @Override
  public void updateBounds() {
    throw new RuntimeException("not implemented");
  }
}
