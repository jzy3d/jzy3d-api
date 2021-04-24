package org.jzy3d.plot3d.rendering.view.annotation;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Geometry;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

/**
 * Draws the distance of every scene graph drawable object to camera eye.
 * 
 * The distance label is plotted at the barycenter of the object.
 * 
 * The camera is represented as a red point.
 * 
 * @author Martin
 */
public class CameraDistanceAnnotation extends Point {
  public CameraDistanceAnnotation(View view, Color color) {
    super();
    this.view = view;
    setColor(color);
    setWidth(5);
  }

  @Override
  public void draw(IPainter painter) {
    computeCameraPosition();
    doTransform(painter);

    doDrawCamera(painter, painter.getCamera());

    Horizontal h = Horizontal.CENTER;
    Vertical v = Vertical.CENTER;
    Coord2d screenOffset = new Coord2d(10, 0);
    Color colorBary = Color.BLACK;
    Color colorPt = Color.GRAY.clone();
    colorPt.alphaSelf(0.5f);

    Graph graph = view.getScene().getGraph();
    
    AbstractOrderingStrategy strat = graph.getStrategy();
    
    for (Drawable drawable : graph.getDecomposition()) {
      drawBarycenterDistanceToCamera(painter, h, v, screenOffset, colorBary, strat, drawable);

      //drawPolygonPointsDistanceToCamera(painter, h, v, screenOffset, colorPt, strat, drawable);
    }
  }

  private void drawBarycenterDistanceToCamera(IPainter painter, Horizontal h, Vertical v,
      Coord2d screenOffset, Color colorBary, AbstractOrderingStrategy strat, Drawable drawable) {
    double d = strat.score(drawable);

    // System.out.println(drawable.getBarycentre() );

    txt.setSpaceTransformer(drawable.getSpaceTransformer());
    txt.drawText(painter, Font.Helvetica_12, Utils.num2str(d, 4), drawable.getBarycentre(), h, v,
        colorBary, screenOffset);
  }

  private void drawPolygonPointsDistanceToCamera(IPainter painter, Horizontal h, Vertical v,
      Coord2d screenOffset, Color colorPt, AbstractOrderingStrategy strat, Drawable drawable) {
    double d;
    if (drawable instanceof Geometry) {
      Polygon p = (Polygon) drawable;
      for (Point pt : p.getPoints()) {
        // Point pt2 = pt.clone();
        d = strat.score(pt);

        // System.out.println(pt.xyz);

        txt.setSpaceTransformer(pt.getSpaceTransformer());
        txt.drawText(painter, Font.Helvetica_12, Utils.num2str(d, 4), pt.getCoord(), h, v, colorPt, screenOffset);
      }
    }
  }

  public void computeCameraPosition() {
    Coord3d scaling = view.getLastViewScaling().clone();
    xyz = view.getCamera().getEye().clone();
    xyz = xyz.div(scaling);
  }

  public void doDrawCamera(IPainter painter, Camera cam) {
    painter.glPointSize(width);
    painter.glBegin_Point();
    painter.glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
    painter.glVertex3f(xyz.x, xyz.y, xyz.z);
    painter.glEnd();
  }

  protected View view;
  protected ITextRenderer txt = new TextBitmapRenderer();
}
