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
import org.jzy3d.plot3d.primitives.selectable.Selectable;
import org.jzy3d.plot3d.rendering.view.Camera;


public class TexturedCube extends Composite implements Selectable, ITranslucent {
  protected Drawable north;
  protected Drawable south;
  protected Drawable west;
  protected Drawable east;
  protected Drawable near;
  protected Drawable far;

  protected Drawable northBg;
  protected Drawable southBg;
  protected Drawable westBg;
  protected Drawable eastBg;
  protected Drawable nearBg;
  protected Drawable farBg;

  protected List<Coord3d> lastProjection;
  protected Polygon2d lastHull;

  protected float alpha;
  
  public TexturedCube(MaskPair masks) {
    this(new Coord3d(), Color.CYAN, Color.RED, masks);
  }

  public TexturedCube(Coord3d position, MaskPair masks) {
    this(position, Color.CYAN, Color.RED, masks);
  }

  public TexturedCube(Coord3d position, Color color, Color bgcolor, MaskPair masks) {
    this(position, color, bgcolor, masks, 0.5f);
  }

  public TexturedCube(Coord3d position, Color color, Color bgcolor, MaskPair masks, float width) {
    List<Coord2d> zmapping = makeZPlaneTextureMapping(position, width);
    List<Coord2d> ymapping = makeYPlaneTextureMapping(position, width);
    List<Coord2d> xmapping = makeXPlaneTextureMapping(position, width);

    northBg =
        new NativeDrawableImage(masks.bgMask, PlaneAxis.Z, position.z + width, zmapping, bgcolor);
    southBg =
        new NativeDrawableImage(masks.bgMask, PlaneAxis.Z, position.z - width, zmapping, bgcolor);
    westBg =
        new NativeDrawableImage(masks.bgMask, PlaneAxis.X, position.x - width, xmapping, bgcolor);
    eastBg =
        new NativeDrawableImage(masks.bgMask, PlaneAxis.X, position.x + width, xmapping, bgcolor);
    nearBg =
        new NativeDrawableImage(masks.bgMask, PlaneAxis.Y, position.y - width, ymapping, bgcolor);
    farBg =
        new NativeDrawableImage(masks.bgMask, PlaneAxis.Y, position.y + width, ymapping, bgcolor);

    north =
        new NativeDrawableImage(masks.symbolMask, PlaneAxis.Z, position.z + width, zmapping, color);
    south =
        new NativeDrawableImage(masks.symbolMask, PlaneAxis.Z, position.z - width, zmapping, color);
    west =
        new NativeDrawableImage(masks.symbolMask, PlaneAxis.X, position.x - width, xmapping, color);
    east =
        new NativeDrawableImage(masks.symbolMask, PlaneAxis.X, position.x + width, xmapping, color);
    near =
        new NativeDrawableImage(masks.symbolMask, PlaneAxis.Y, position.y - width, ymapping, color);
    far =
        new NativeDrawableImage(masks.symbolMask, PlaneAxis.Y, position.y + width, ymapping, color);

    add(north);
    add(south);
    add(west);
    add(east);
    add(near);
    add(far);

    add(northBg);
    add(southBg);
    add(westBg);
    add(eastBg);
    add(nearBg);
    add(farBg);

    bbox = new BoundingBox3d(position, width * 2);
  }

  public static List<Coord2d> makeXPlaneTextureMapping(Coord3d position, float width) {
    List<Coord2d> xmapping = new ArrayList<Coord2d>(4);
    xmapping.add(new Coord2d(position.y - width, position.z - width));
    xmapping.add(new Coord2d(position.y + width, position.z - width));
    xmapping.add(new Coord2d(position.y + width, position.z + width));
    xmapping.add(new Coord2d(position.y - width, position.z + width));
    return xmapping;
  }

  public static List<Coord2d> makeYPlaneTextureMapping(Coord3d position, float width) {
    List<Coord2d> ymapping = new ArrayList<Coord2d>(4);
    ymapping.add(new Coord2d(position.x - width, position.z - width));
    ymapping.add(new Coord2d(position.x + width, position.z - width));
    ymapping.add(new Coord2d(position.x + width, position.z + width));
    ymapping.add(new Coord2d(position.x - width, position.z + width));
    return ymapping;
  }

  public static List<Coord2d> makeZPlaneTextureMapping(Coord3d position, float width) {
    List<Coord2d> zmapping = new ArrayList<Coord2d>(4);
    zmapping.add(new Coord2d(position.x - width, position.y - width));
    zmapping.add(new Coord2d(position.x + width, position.y - width));
    zmapping.add(new Coord2d(position.x + width, position.y + width));
    zmapping.add(new Coord2d(position.x - width, position.y + width));
    return zmapping;
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

  @Override
  public void setAlphaFactor(float a) {
    alpha = a;

    ((NativeDrawableImage) northBg).setAlphaFactor(alpha);
    ((NativeDrawableImage) southBg).setAlphaFactor(alpha);
    ((NativeDrawableImage) westBg).setAlphaFactor(alpha);
    ((NativeDrawableImage) eastBg).setAlphaFactor(alpha);
    ((NativeDrawableImage) nearBg).setAlphaFactor(alpha);
    ((NativeDrawableImage) farBg).setAlphaFactor(alpha);

    ((NativeDrawableImage) north).setAlphaFactor(alpha);
    ((NativeDrawableImage) south).setAlphaFactor(alpha);
    ((NativeDrawableImage) west).setAlphaFactor(alpha);
    ((NativeDrawableImage) east).setAlphaFactor(alpha);
    ((NativeDrawableImage) near).setAlphaFactor(alpha);
    ((NativeDrawableImage) far).setAlphaFactor(alpha);
  }


}
