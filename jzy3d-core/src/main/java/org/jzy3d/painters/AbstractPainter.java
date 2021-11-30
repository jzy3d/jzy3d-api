package org.jzy3d.painters;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PolygonArray;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.ClipEq;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public abstract class AbstractPainter implements IPainter {

  protected Camera camera;
  protected View view;
  protected ICanvas canvas;

  public AbstractPainter() {
    super();
  }

  @Override
  public View getView() {
    return view;
  }

  @Override
  public void setView(View view) {
    this.view = view;
  }

  @Override
  public ICanvas getCanvas() {
    return canvas;
  }

  @Override
  public void setCanvas(ICanvas canvas) {
    this.canvas = canvas;
  }

  @Override
  public Camera getCamera() {
    return camera;
  }

  @Override
  public void setCamera(Camera camera) {
    this.camera = camera;
  }
  
  @Override
  public Quality getQuality() {
    return getView().getChart().getQuality();
  }

  @Override
  public void transform(Transform transform, boolean loadIdentity) {
    transform.execute(this, loadIdentity);
  }

  @Override
  public void color(Color color) {
    glColor4f(color.r, color.g, color.b, color.a);
  }

  @Override
  public void colorAlphaOverride(Color color, float alpha) {
    glColor4f(color.r, color.g, color.b, alpha);

  }

  @Override
  public void colorAlphaFactor(Color color, float alpha) {
    glColor4f(color.r, color.g, color.b, color.a * alpha);
  }

  @Override
  public void clearColor(Color color) {
    glClearColor(color.r, color.g, color.b, color.a);
  }

  @Override
  public void vertex(Coord3d coord, SpaceTransformer transform) {
    if (transform == null) {
      vertex(coord);
    } else {
      glVertex3f(transform.getX().compute(coord.x), transform.getY().compute(coord.y),
          transform.getZ().compute(coord.z));
    }
  }

  @Override
  public void vertex(float x, float y, float z, SpaceTransformer transform) {
    if (transform == null) {
      glVertex3f(x, y, z);
    } else {
      glVertex3f(transform.getX().compute(x), transform.getY().compute(y),
          transform.getZ().compute(z));
    }
  }
  
  @Override
  public void box(BoundingBox3d box, Color color, float width, SpaceTransformer spaceTransformer) {
    //if(box==null)
    //  return;
    
    color(color);
    glLineWidth(width);
      
    // top
    glBegin_LineLoop();
    vertex(box.getXmin(), box.getYmin(), box.getZmax(), spaceTransformer);
    vertex(box.getXmax(), box.getYmin(), box.getZmax(), spaceTransformer);
    vertex(box.getXmax(), box.getYmax(), box.getZmax(), spaceTransformer);
    vertex(box.getXmin(), box.getYmax(), box.getZmax(), spaceTransformer);
    glEnd();

    // bottom
    glBegin_LineLoop();
    vertex(box.getXmin(), box.getYmin(), box.getZmin(), spaceTransformer);
    vertex(box.getXmax(), box.getYmin(), box.getZmin(), spaceTransformer);
    vertex(box.getXmax(), box.getYmax(), box.getZmin(), spaceTransformer);
    vertex(box.getXmin(), box.getYmax(), box.getZmin(), spaceTransformer);
    glEnd();

    // left
    glBegin_LineLoop();
    vertex(box.getXmin(), box.getYmin(), box.getZmin(), spaceTransformer);
    vertex(box.getXmin(), box.getYmin(), box.getZmax(), spaceTransformer);
    vertex(box.getXmin(), box.getYmax(), box.getZmax(), spaceTransformer);
    vertex(box.getXmin(), box.getYmax(), box.getZmin(), spaceTransformer);
    glEnd();

    // right
    glBegin_LineLoop();
    vertex(box.getXmax(), box.getYmin(), box.getZmin(), spaceTransformer);
    vertex(box.getXmax(), box.getYmin(), box.getZmax(), spaceTransformer);
    vertex(box.getXmax(), box.getYmax(), box.getZmax(), spaceTransformer);
    vertex(box.getXmax(), box.getYmax(), box.getZmin(), spaceTransformer);
    glEnd();
    
    // near
    glBegin_LineLoop();
    vertex(box.getXmin(), box.getYmin(), box.getZmin(), spaceTransformer);
    vertex(box.getXmin(), box.getYmin(), box.getZmax(), spaceTransformer);
    vertex(box.getXmin(), box.getYmax(), box.getZmax(), spaceTransformer);
    vertex(box.getXmin(), box.getYmax(), box.getZmin(), spaceTransformer);
    glEnd();

    // far
    glBegin_LineLoop();
    vertex(box.getXmax(), box.getYmin(), box.getZmin(), spaceTransformer);
    vertex(box.getXmax(), box.getYmin(), box.getZmax(), spaceTransformer);
    vertex(box.getXmax(), box.getYmax(), box.getZmax(), spaceTransformer);
    vertex(box.getXmax(), box.getYmax(), box.getZmin(), spaceTransformer);
    glEnd();
  }

  @Override
  public void vertex(Coord3d coord) {
    glVertex3f(coord.x, coord.y, coord.z);
  }

  @Override
  public void normal(Coord3d norm) {
    glNormal3f(norm.x, norm.y, norm.z);
  }

  @Override
  public void clip(BoundingBox3d box) {
    clip(0, ClipEq.X_INFERIOR_TO, box.getXmax());
    clip(1, ClipEq.X_SUPERIOR_TO, box.getXmin());
    
    clip(2, ClipEq.Y_INFERIOR_TO, box.getYmax());
    clip(3, ClipEq.Y_SUPERIOR_TO, box.getYmin());
    
    clip(4, ClipEq.Z_INFERIOR_TO, box.getZmax());
    clip(5, ClipEq.Z_SUPERIOR_TO, box.getZmin());

  }
  
  @Override
  public void clipOn() {
    glEnable_ClipPlane(0);
    glEnable_ClipPlane(1);
    glEnable_ClipPlane(2);
    glEnable_ClipPlane(3);
    glEnable_ClipPlane(4);
    glEnable_ClipPlane(5);
  }

  @Override
  public void clipOff() {
    glDisable_ClipPlane(0);
    glDisable_ClipPlane(1);
    glDisable_ClipPlane(2);
    glDisable_ClipPlane(3);
    glDisable_ClipPlane(4);
    glDisable_ClipPlane(5);
  }
  
  /**
   * A convenient shortcut to invoke a clipping plane using an ID in [0;5] instead of the original OpenGL ID value.
   */
  @Override
  public void clip(int plane, ClipEq equation, double value) {
    glClipPlane(clipPlaneId(plane), equation(equation, value));
  }


  /**
   * The four coefs of the plane equation that are returned by this method are : Nx, Ny, Nz, D
   * 
   * The solve the formula : Nx*x + Ny*y + Nz*z + D = 0
   * 
   * where Nx, Ny and Nz are the 3 components of the normal to the plane. The x, y and z in the
   * equation are the coordinates of any point on the plane. The variable D is the distance of the
   * plane from the origin. A point that is being tested can give three results based on where it is
   * with respect to the plane :
   * 
   * <ul>
   * <li>The point is in front of the plane - In this case, the result obtained will be positive.
   * The value obtained is the distance of the point from the plane being tested.
   * <li>The point is behind the plane - In this case, the result will be negative. The value
   * obtained is the distance of the point from the plane being tested.
   * <li>The point is on the plane - The result will, quite obviously, be zero.
   * </ul>
   * 
   * @param eq
   * @param value
   * @return
   */
  protected double[] equation(ClipEq eq, double value) {
    
    switch (eq) {
      case X_SUPERIOR_TO:
        return new double[] {+1, 0, 0, -value};
      case X_INFERIOR_TO:
        return new double[] {-1, 0, 0, value};
      case Y_SUPERIOR_TO:
        return new double[] {0, +1, 0, -value};
      case Y_INFERIOR_TO:
        return new double[] {0, -1, 0, value};
      case Z_SUPERIOR_TO:
        return new double[] {0, 0, +1, -value};
      case Z_INFERIOR_TO:
        return new double[] {0, 0, -1, value};
      default:
        throw new IllegalArgumentException("This equation is not supported : " + eq);
    }
  }
  
  @Override
  public void raster(Coord3d coord, SpaceTransformer transform) {
    if (transform == null) {
      glRasterPos3f(coord.x, coord.y, coord.z);
    } else {
      glRasterPos3f(transform.getX().compute(coord.x), transform.getY().compute(coord.y),
          transform.getZ().compute(coord.z));
    }
  }

  @Override
  public void material(int face, int pname, Color color) {
    glMaterialfv(face, pname, color.toArray(), 0);
  }



  public Coord3d screenToModel(Coord3d screen) {
    return getCamera().screenToModel(this, screen);
  }

  /**
   * Transform a 3d point coordinate into its screen position.
   * 
   * @see {@link Camera#modelToScreen(IPainter, Coord3d)}
   */
  public Coord3d modelToScreen(Coord3d point) {
    return getCamera().modelToScreen(this, point);
  }

  public Coord3d[] modelToScreen(Coord3d[] points) {
    return getCamera().modelToScreen(this, points);
  }

  public Coord3d[][] modelToScreen(Coord3d[][] points) {
    return getCamera().modelToScreen(this, points);
  }

  public List<Coord3d> modelToScreen(List<Coord3d> points) {
    return getCamera().modelToScreen(this, points);
  }

  public ArrayList<ArrayList<Coord3d>> modelToScreen(ArrayList<ArrayList<Coord3d>> polygons) {
    return getCamera().modelToScreen(this, polygons);
  }

  public PolygonArray modelToScreen(PolygonArray polygon) {
    return getCamera().modelToScreen(this, polygon);
  }

  public PolygonArray[][] modelToScreen(PolygonArray[][] polygons) {
    return getCamera().modelToScreen(this, polygons);
  }
  
  
}
