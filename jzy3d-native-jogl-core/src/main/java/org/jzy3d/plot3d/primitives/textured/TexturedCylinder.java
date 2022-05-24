package org.jzy3d.plot3d.primitives.textured;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.ConvexHull;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.maths.Polygon2d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Quad;
import org.jzy3d.plot3d.primitives.selectable.Selectable;
import org.jzy3d.plot3d.rendering.view.Camera;


public class TexturedCylinder extends Composite implements Selectable, ITranslucent {
  public TexturedCylinder(MaskPair masks) {
    this(new Coord3d(), Color.CYAN, Color.RED, masks);
  }

  public TexturedCylinder(Coord3d position, MaskPair masks) {
    this(position, Color.CYAN, Color.RED, masks);
  }

  public TexturedCylinder(Coord3d position, Color color, Color bgcolor, MaskPair masks) {
    alpha = 1;
    List<Coord2d> mapping = new ArrayList<Coord2d>(4);
    mapping.add(new Coord2d(position.x - 0.5, position.y - 0.5));
    mapping.add(new Coord2d(position.x + 0.5, position.y - 0.5));
    mapping.add(new Coord2d(position.x + 0.5, position.y + 0.5));
    mapping.add(new Coord2d(position.x - 0.5, position.y + 0.5));

    dDiskDown =
        new NativeDrawableImage(masks.bgMask, PlaneAxis.Z, position.z - 0.5f, mapping, bgcolor);
    dDiskUp =
        new NativeDrawableImage(masks.bgMask, PlaneAxis.Z, position.z + 0.5f, mapping, bgcolor);
    dArrowDown =
        new NativeDrawableImage(masks.symbolMask, PlaneAxis.Z, position.z - 0.5f, mapping, color);
    dArrowUp =
        new NativeDrawableImage(masks.symbolMask, PlaneAxis.Z, position.z + 0.5f, mapping, color);

    int slices = 20;
    float radius = 0.5f;
    float height = 1f;
    Coord3d pos = new Coord3d(position.x, position.y, position.z - 0.5f);
    quads = new ArrayList<TranslucentQuad>(slices);
    for (int i = 0; i < slices; i++) {
      float angleBorder1 = (float) i * 2 * (float) Math.PI / slices;
      float angleBorder2 = (float) (i + 1) * 2 * (float) Math.PI / slices;

      Coord2d border1 = new Coord2d(angleBorder1, radius).cartesian();
      Coord2d border2 = new Coord2d(angleBorder2, radius).cartesian();

      TranslucentQuad face = new TranslucentQuad();
      face.add(new Point(new Coord3d(pos.x + border1.x, pos.y + border1.y, pos.z)));
      face.add(new Point(new Coord3d(pos.x + border1.x, pos.y + border1.y, pos.z + height)));
      face.add(new Point(new Coord3d(pos.x + border2.x, pos.y + border2.y, pos.z + height)));
      face.add(new Point(new Coord3d(pos.x + border2.x, pos.y + border2.y, pos.z)));
      face.setColor(bgcolor);
      face.setWireframeDisplayed(false);
      quads.add(face);
    }

    add(dDiskDown);
    add(dArrowDown);
    add(dDiskUp);
    add(dArrowUp);
    add(quads);

    bbox = new BoundingBox3d();

    for (Quad quad : quads) {
      bbox.add(quad);
    }
  }

  @Override
  public void setAlphaFactor(float a) {
    alpha = a;

    ((NativeDrawableImage) dDiskDown).setAlphaFactor(alpha);
    ((NativeDrawableImage) dArrowDown).setAlphaFactor(alpha);
    ((NativeDrawableImage) dDiskUp).setAlphaFactor(alpha);
    ((NativeDrawableImage) dArrowUp).setAlphaFactor(alpha);
    for (TranslucentQuad q : quads) {
      q.setAlphaFactor(alpha);
    }
  }

  @Override
  public BoundingBox3d getBounds() {
    return bbox;
  }

  @Override
  public void project(IPainter painter, Camera cam) {
    lastProjection = cam.modelToScreen(painter, getBounds().getVertices());
    lastHull = ConvexHull.hull(lastProjection);
  }

  @Override
  public Polygon2d getHull2d() {
    return lastHull;
  }

  @Override
  public List<Coord3d> getLastProjection() {
    return lastProjection;
  }

  protected Drawable dArrowUp;
  protected Drawable dArrowDown;
  protected Drawable dDiskUp;
  protected Drawable dDiskDown;
  protected List<TranslucentQuad> quads;

  protected List<Coord3d> lastProjection;
  protected Polygon2d lastHull;
  protected float alpha;
}
