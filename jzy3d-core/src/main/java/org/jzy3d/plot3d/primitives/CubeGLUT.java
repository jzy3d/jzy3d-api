package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.transform.Transform;

/**
 * A cube rendered with OpenGL GLUT.
 * 
 * GLUT helps fully defining cube with normals, which makes this cube suitable for usage with a
 * {@link Light}.
 * 
 * @author Martin Pernollet
 */
public class CubeGLUT extends Wireframeable implements ISingleColorable {
  protected float radius;
  protected Color color;
  protected Coord3d position;

  public CubeGLUT(Coord3d c, float radius) {
    this(new BoundingBox3d(c, radius * 2));
  }

  public CubeGLUT(BoundingBox3d box) {
    this(box, Color.BLACK, Color.BLUE);
  }
  
  public CubeGLUT(BoundingBox3d box, Color wireframe, Color face) {
    this.bbox = box;
    this.radius = box.getXRange().getRange() / 2;
    this.position = box.getCenter();

    setWireframeColor(wireframe);
    setColor(face);
  }


  @Override
  public void setColor(Color color) {
    this.color = color;
  }

  @Override
  public Color getColor() {
    return color;
  }
  
  public float getRadius() {
    return radius;
  }

  public void setRadius(float radius) {
    this.radius = radius;
  }

  public Coord3d getPosition() {
    return position;
  }

  public void setPosition(Coord3d position) {
    this.position = position;
  }

  @Override
  public void draw(IPainter painter) {
    doTransform(painter);
    doDrawCube(painter);
    doDrawBoundsIfDisplayed(painter);
  }

  protected void doDrawCube(IPainter painter) {
    painter.glTranslatef(position.x, position.y, position.z);

    if (faceDisplayed) {
      painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.FILL);
      painter.glColor4f(color.r, color.g, color.b, color.a);
      painter.glutSolidCube(radius * 2);
    }
    if (wireframeDisplayed) {
      painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.LINE);
      painter.glLineWidth(wireframeWidth);
      painter.glColor4f(wireframeColor.r, wireframeColor.g, wireframeColor.b, wireframeColor.a);
      painter.glutSolidCube(radius * 2);
    }
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateBounds() {

  }

}
