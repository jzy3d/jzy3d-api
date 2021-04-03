package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.transform.Transform;

/**
 * Allows rendering a sphere. 
 * 
 * The position and shape of a Sphere3d is defined through its {@link setData()} method. Moreover, a
 * Sphere3d is Wireframeable3d and support only one color that is defined trough its
 * {@link setColor()} method.
 * 
 * @author Martin Pernollet
 */
public class Sphere extends Wireframeable implements ISingleColorable {

  /**
   * Initialize a black sphere at the origin with a radius of 10, and slicing of 15.
   */
  public Sphere() {
    super();
    bbox = new BoundingBox3d();
    setPosition(Coord3d.ORIGIN);
    setVolume(10f);
    setSlicing(15, 15);
    setColor(Color.BLACK);
  }

  /** Initialize a sphere with the given parameters. */
  public Sphere(Coord3d position, float radius, int slicing, Color color) {
    super();
    bbox = new BoundingBox3d();
    setPosition(position);
    setVolume(radius);
    setSlicing(slicing, slicing);
    setColor(color);
  }

  /********************************************************/

  @Override
  public void draw(IPainter painter) {
    doTransform(painter);
    doDrawSphere(painter);
    doDrawBoundsIfDisplayed(painter);
  }

  protected void doDrawSphere(IPainter painter) {
    painter.glTranslatef(position.x, position.y, position.z);

    if (faceDisplayed) {
      painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.FILL);
      painter.glColor4f(color.r, color.g, color.b, color.a);
      // glu.gluSphere(qobj, radius, slices, stacks);
      painter.glutSolidSphere(radius, slices, stacks);
    }
    if (wireframeDisplayed) {
      painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.LINE);
      painter.glLineWidth(wireframeWidth);
      painter.glColor4f(wireframeColor.r, wireframeColor.g, wireframeColor.b, wireframeColor.a);
      // glu.gluSphere(qobj, radius, slices, stacks);
      painter.glutSolidSphere(radius, slices, stacks);
      // gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_LINE);
      // gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
    }
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    position.set(transform.compute(position));
    updateBounds();
  }

  /*
   * protected GLUquadric qobj; public void draw2(){ // qobj = glu.gluNewQuadric();
   * //glu.gluSphere(qobj, radius, slices, stacks); }
   */

  /**********************************************************************/

  /**
   * Set the sphere data.
   * 
   * @param position sphere position (may be handled differently in future version)
   * @param radius radius of the sphere
   * @param slices number of vertical slices (i.e. wireframes)
   * @param stacks number of horizontal stacks (i.e. wireframes)
   */
  public void setData(Coord3d position, float radius, float height, int slices, int stacks) {
    setPosition(position);
    setVolume(radius);
    setSlicing(slices, stacks);
  }

  /**
   * Set the position of the Sphere and the dimensions of its boundingbox. Note that this position
   * will be use to translate the object before drawing it (meaning a glTranslate(position) is
   * performed right after the Translate.execute).
   * 
   * @param position
   */
  public void setPosition(Coord3d position) {
    this.position = position;
    updateBounds();
  }

  public Coord3d getPosition() {
    return position;
  }

  @Override
  public void updateBounds() {
    bbox.reset();
    bbox.add(position.x + radius, position.y + radius, position.z + radius);
    bbox.add(position.x - radius, position.y - radius, position.z - radius);
  }

  /**
   * Set the radius of the sphere, and the dimensions of its boundingbox.
   * 
   * @param radius sphere radius
   */
  public void setVolume(float radius) {
    this.radius = radius;
    updateBounds();
  }

  /**
   * Set the sphere slicing parameters, i.e. the subtlety of the circle estimation.
   * 
   * @param verticalWires number of vertical slices
   * @param horizontalWires number of horizontal slices
   */
  public void setSlicing(int verticalWires, int horizontalWires) {
    this.slices = verticalWires;
    this.stacks = horizontalWires;
  }

  /********************************************************/

  @Override
  public void setColor(Color color) {
    this.color = color;

    fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
  }

  @Override
  public Color getColor() {
    return color;
  }

  /********************************************************/

  @Override
  public String toString(int depth) {
    return Utils.blanks(depth) + "(Sphere) radius= " + radius + " x=" + position.x + " y="
        + position.y + " z=" + position.z + " r=" + color.r + " g=" + color.g + " b=" + color.b
        + " a=" + color.a;
  }

  /********************************************************/

  protected Coord3d position;
  protected float radius;

  protected int slices;
  protected int stacks;

  protected Color color;
}
