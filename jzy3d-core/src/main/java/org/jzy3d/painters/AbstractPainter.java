package org.jzy3d.painters;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PolygonArray;
import org.jzy3d.os.OperatingSystem;
import org.jzy3d.os.WindowingToolkit;
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
  protected OperatingSystem os = new OperatingSystem();

  public AbstractPainter() {
  }

  @Override
  public OperatingSystem getOS() {
    return os;
  }

  @Override
  public WindowingToolkit getWindowingToolkit() {
    return WindowingToolkit.UNKOWN;
  }
  
  @Override
  public void clearCache() {
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
    // if(box==null)
    // return;

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
  
  @Override
  public boolean[] clipStatus() {
    boolean[] status = new boolean[6];
    
    int[] v = {-1};
    
    for(int i=0; i<6; i++) {
      glGetIntegerv(clipPlaneId(i), v, 0);
      status[i] = (v[0]==1);
    }
    return status;
  }


  /**
   * A convenient shortcut to invoke a clipping plane using an ID in [0;5] instead of the original
   * OpenGL ID value.
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
   * A good explanation on vector form of a plane equation : 
   * @see https://www.youtube.com/watch?v=4GJiz6jxOac&list=PLkZjai-2JcxnYmkg6fpzz4WFumGVl7MOa&index=7
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


  /**
   * Transform a 2d screen coordinate into a 3d coordinate. The z component of the screen coordinate
   * indicates a depth value between the near and far clipping plane of the {@link Camera}.
   * 
   * A null coordinate can be returned if the projection could not be performed for some reasons.
   * This may occur if projection or modelview matrices are not invertible or if these matrices
   * where unavailable (hence resulting to zero matrices) while invoking this method. Zero matrices
   * can be avoided by ensuring the GL context is current using {@link IPainter#acquireGL()}
   * 
   * @see {@link IPainter#gluUnProject(float, float, float, float[], int, float[], int, int[], int, float[], int)}
   */
  @Override
  public Coord3d screenToModel(Coord3d screen) {
    int viewport[] = getViewPortAsInt();
    float modelView[] = getModelViewAsFloat();
    float projection[] = getProjectionAsFloat();

    return screenToModel(screen, viewport, modelView, projection);
  }

  /**
   * Transform a 2d screen coordinate into a 3d coordinate.
   * 
   * Allow to pass custom viewport, modelview matrix and projection matrix. To use current one,
   * invoke {@link #screenToModel(Coord3d)}
   */
  @Override
  public Coord3d screenToModel(Coord3d screen, int[] viewport, float[] modelView,
      float[] projection) {
    // Array.print("Painter.screenToModel : viewport : ", viewport);
    // Array.print("Painter.screenToModel : modelView : ", modelView);
    // Array.print("Painter.screenToModel : projection : ", projection);

    // double modelView[] = painter.getModelViewAsDouble();
    // double projection[] = painter.getProjectionAsDouble();
    float worldcoord[] = new float[3];// wx, wy, wz;// returned xyz coords

    boolean s =
        gluUnProject(screen.x, screen.y, screen.z, modelView, projection, viewport, worldcoord);


    if (s) {
      return new Coord3d(worldcoord[0], worldcoord[1], worldcoord[2]);
    } else {
      // System.out.println("NULL Coord");
      // Array.print("viewport : ", viewport);
      // Array.print("modelview : ", modelView);
      // Array.print("projection : ", projection);
      return null;
    }
  }

  /**
   * Transform a 3d point coordinate into its screen position.
   * 
   * This method requires the GL context to be current. If not called inside a rendering loop, that
   * method may not apply correctly and output {0,0,0}. In that case and if the chart is based on
   * JOGL (native), one may force the context to be current using
   * 
   * <pre>
   * <code>
   * painter.acquireGL(); // make context current
   * Coord3d screen2dCoord = painter.modelToScreen(world3dCoord);
   * painter.releaseGL(); // release context to let other use it
   * </code>
   * </pre>
   * 
   * A null coordinate can be returned if the projection could not be performed for some reasons.
   */
  @Override
  public Coord3d modelToScreen(Coord3d point) {
    int viewport[] = getViewPortAsInt();
    float modelView[] = getModelViewAsFloat();
    float projection[] = getProjectionAsFloat();
    
    //Array.print("Painter.modelToScreen : viewport : ", viewport);
    //Array.print("Painter.modelToScreen : modelView : ", modelView);
    //Array.print("Painter.modelToScreen : projection : ", projection);


    return modelToScreen(point, viewport, modelView, projection);
  }

  /**
   * Transform a 3d coordinate to 2D screen coordinate.
   * 
   * Allow to pass custom viewport, modelview matrix and projection matrix. To use current one,
   * invoke {@link #modelToScreen(Coord3d)}
   */
  @Override
  public Coord3d modelToScreen(Coord3d point, int[] viewport, float[] modelView, float[] projection) {
    float screenCoord[] = new float[3];// wx, wy, wz;// returned xyz coords

    boolean s = gluProject(point.x, point.y, point.z, modelView, projection, viewport, screenCoord);

    if (s) {
      return new Coord3d(screenCoord[0], screenCoord[1], screenCoord[2]);
    } else {
      return null;
    }
  }

  /**
   * Project an array of coordinates.
   * 
   * @see {@link #modelToScreen(Coord3d)}
   */
  @Override
  public Coord3d[] modelToScreen(Coord3d[] points) {
    int viewport[] = getViewPortAsInt();

    float screenCoord[] = new float[3];

    Coord3d[] projection = new Coord3d[points.length];

    for (int i = 0; i < points.length; i++) {
      boolean s = gluProject(points[i].x, points[i].y, points[i].z, getModelViewAsFloat(),
          getProjectionAsFloat(), viewport, screenCoord);
      if (s)
        projection[i] = new Coord3d(screenCoord[0], screenCoord[1], screenCoord[2]);
    }
    return projection;
  }

  /**
   * Project an array of coordinates.
   * 
   * @see {@link #modelToScreen(Coord3d)}
   */
  @Override
  public Coord3d[][] modelToScreen(Coord3d[][] points) {
    int viewport[] = getViewPortAsInt();

    float screenCoord[] = new float[3];

    Coord3d[][] projection = new Coord3d[points.length][points[0].length];

    for (int i = 0; i < points.length; i++) {
      for (int j = 0; j < points[i].length; j++) {
        boolean s = gluProject(points[i][j].x, points[i][j].y, points[i][j].z,
            getModelViewAsFloat(), getProjectionAsFloat(), viewport, screenCoord);
        if (s)
          projection[i][j] = new Coord3d(screenCoord[0], screenCoord[1], screenCoord[2]);
      }
    }
    return projection;
  }

  /**
   * Project a list of coordinates.
   * 
   * @see {@link #modelToScreen(Coord3d)}
   */
  @Override
  public List<Coord3d> modelToScreen(List<Coord3d> points) {
    int viewport[] = getViewPortAsInt();

    float screenCoord[] = new float[3];

    List<Coord3d> projection = new Vector<Coord3d>();

    for (Coord3d point : points) {
      boolean s = gluProject(point.x, point.y, point.z, getModelViewAsFloat(), getProjectionAsFloat(), viewport, screenCoord);
      if (s)
        projection.add(new Coord3d(screenCoord[0], screenCoord[1], screenCoord[2]));
    }
    return projection;
  }

  /**
   * Project a list of lists of coordinates.
   * 
   * @see {@link #modelToScreen(Coord3d)}
   */
  @Override
  public List<ArrayList<Coord3d>> modelToScreen(ArrayList<ArrayList<Coord3d>> polygons) {
    int viewport[] = getViewPortAsInt();

    float screenCoord[] = new float[3];

    ArrayList<ArrayList<Coord3d>> projections = new ArrayList<ArrayList<Coord3d>>(polygons.size());

    for (ArrayList<Coord3d> polygon : polygons) {
      ArrayList<Coord3d> projection = new ArrayList<Coord3d>(polygon.size());
      for (Coord3d point : polygon) {
        boolean s = gluProject(point.x, point.y, point.z, getModelViewAsFloat(), 
            getProjectionAsFloat(), viewport, screenCoord);
        if (s)
          projection.add(new Coord3d(screenCoord[0], screenCoord[1], screenCoord[2]));
      }
      projections.add(projection);
    }
    return projections;
  }

  @Override
  public PolygonArray modelToScreen(PolygonArray polygon) {
    int viewport[] = getViewPortAsInt();

    float screenCoord[] = new float[3];

    int len = polygon.length();

    float[] x = new float[len];
    float[] y = new float[len];
    float[] z = new float[len];

    for (int i = 0; i < len; i++) {
      boolean s = gluProject(polygon.x[i], polygon.y[i], polygon.z[i],
          getModelViewAsFloat(), getProjectionAsFloat(), viewport, screenCoord);
      if (s) {
        x[i] = screenCoord[0];
        y[i] = screenCoord[1];
        z[i] = screenCoord[2];
      }
    }
    return new PolygonArray(x, y, z);
  }

  @Override
  public PolygonArray[][] modelToScreen(PolygonArray[][] polygons) {
    int viewport[] = getViewPortAsInt();
    float screencoord[] = new float[3];

    PolygonArray[][] projections = new PolygonArray[polygons.length][polygons[0].length];
    for (int i = 0; i < polygons.length; i++) {
      for (int j = 0; j < polygons[i].length; j++) {
        PolygonArray polygon = polygons[i][j];
        int len = polygon.length();
        float[] x = new float[len];
        float[] y = new float[len];
        float[] z = new float[len];

        for (int k = 0; k < len; k++) {
          boolean s = gluProject(polygon.x[k], polygon.y[k], polygon.z[k],
              getModelViewAsFloat(), getProjectionAsFloat(), viewport, screencoord);
          if (s) {
            x[k] = screencoord[0];
            y[k] = screencoord[1];
            z[k] = screencoord[2];
          }
        }
        projections[i][j] = new PolygonArray(x, y, z);
      }
    }
    return projections;
  }

  @Override
  public boolean gluUnProject(float winX, float winY, float winZ, float[] model, float[] proj,
      int[] view, float[] objPos) {
    return gluUnProject(winX, winY, winZ, model, 0, proj, 0, view, 0, objPos, 0);

  }

  @Override
  public boolean gluProject(float objX, float objY, float objZ, float[] model, float[] proj,
      int[] view, float[] winPos) {
    return gluProject(objX, objY, objZ, model, 0, proj, 0, view, 0, winPos, 0);
  }

  @Override
  public boolean isJVMScaleLargerThanNativeScale(Coord2d scaleHardware, Coord2d scaleJVM) {
    return scaleJVM.x > scaleHardware.x || scaleJVM.y > scaleHardware.y;
  }

  @Override
  public boolean isJVMScaleLargerThanNativeScale() {
    return isJVMScaleLargerThanNativeScale(getCanvas().getPixelScale(),
        getCanvas().getPixelScaleJVM());
  }

}
