package org.jzy3d.painters;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PolygonArray;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.view.Camera;
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
  public void vertex(Coord3d coord) {
    glVertex3f(coord.x, coord.y, coord.z);
  }

  @Override
  public void normal(Coord3d norm) {
    glNormal3f(norm.x, norm.y, norm.z);
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

  public PolygonArray[][] modelToScreen(PolygonArray[][] polygons){
    return getCamera().modelToScreen(this, polygons);    
  }
}
