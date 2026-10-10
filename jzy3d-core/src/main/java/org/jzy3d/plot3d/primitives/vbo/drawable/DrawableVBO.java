package org.jzy3d.plot3d.primitives.vbo.drawable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.LogManager;
import org.jzy3d.colors.Color;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.primitives.vbo.buffers.FloatVBO;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Rotator;
import org.jzy3d.plot3d.transform.Transform;
import org.slf4j.LoggerFactory;

/**
 * A {@link DrawableVBO} is able to efficiently draw a large collection of geometries.
 * 
 * The user must provide a loader that will be later called when the GL context requires loading the
 * VBO data in GPU memory.
 * 
 * The loader might freely make settings on the drawable as it is called with a reference to the
 * {@link DrawableVBO} is will be loading data into.
 * 
 * One can separate data and appearance by setting geometry settings out of loading process.
 * 
 * DrawableVBO shape1 = new DrawableVBO(new MemoryVBOLoader(getScatter(size)));
 * shape1.setGeometry(GLConstants.GL_POINTS); shape1.setColor(Color.WHITE);
 * 
 * @author Martin Pernollet
 */
public class DrawableVBO extends Drawable implements IGLBindedResource {
  protected int geometry = GLConstants.GL_TRIANGLES;
  protected float width = 1;
  protected Quality quality = Quality.Nicest();

  protected int colorChannelNumber = 3;

  protected PolygonMode polygonMode;
  
  protected int byteOffset;
  protected int normalOffset;
  protected int dimensions;
  protected int size;
  protected int pointer;

  protected int arrayName[] = new int[1];
  protected int elementName[] = new int[1];

  protected boolean hasMountedOnce = false;
  protected Color color = new Color(1f, 0f, 1f, 0.75f);

  protected boolean polygonOffsetFillEnable = true;
  protected float polygonOffsetFactor = 1.0f;
  protected float polygonOffsetUnit = 1.0f;



  public DrawableVBO(IGLLoader<DrawableVBO> loader) {
    this.loader = loader;
  }

  @Override
  public boolean hasMountedOnce() {
    return hasMountedOnce;
  }

  @Override
  public void mount(IPainter painter) {
    try {
      loader.load(painter, this);
      hasMountedOnce = true;
    } catch (Exception e) {
      e.printStackTrace();
      LoggerFactory.getLogger(DrawableVBO.class).error(e.getMessage());
    }
  }

  // element array buffer is an index:
  // @see
  // http://www.opengl-tutorial.org/intermediate-tutorials/tutorial-9-vbo-indexing/
  @Override
  public void draw(IPainter painter) {
    if (hasMountedOnce) {
      doTransform(painter);
      configure(painter);
      doDrawElements(painter);
      doDrawBoundsIfDisplayed(painter);
    }
  }

  public float getWidth() {
    return width;
  }

  public void setWidth(float width) {
    this.width = width;
  }

  public Quality getQuality() {
    return quality;
  }

  public void setQuality(Quality quality) {
    this.quality = quality;
  }

  protected void doDrawElements(IPainter painter) {
    doBindGL2(painter);
    pointers(painter);
    color(painter);
    enable(painter);
    applyWidth(painter);
    applyQuality(painter);

    applyPolygonModeFillGL2(painter);

    if (isPolygonOffsetFillEnable())
      polygonOffseFillEnable(painter);

    applyVertices(painter);
    disable(painter);
    disableColor(painter);
  }

  protected void disableColor(IPainter painter) {
    if (hasColorBuffer) {
      painter.glDisableClientState(GLConstants.GL_COLOR_ARRAY);
    }
  }

  protected void pointers(IPainter painter) {
    painter.glVertexPointer(dimensions, GLConstants.GL_FLOAT, byteOffset, pointer);
    painter.glNormalPointer(GLConstants.GL_FLOAT, byteOffset, normalOffset);
  }

  protected void color(IPainter painter) {
    if (hasColorBuffer) {
      // int bo = 6 * Float.BYTES;
      int p = 3 * Float.BYTES;
      painter.glEnableClientState(GLConstants.GL_COLOR_ARRAY);
      painter.glColorPointer(colorChannelNumber, GLConstants.GL_FLOAT, byteOffset, p);
    }
  }

  protected void enable(IPainter painter) {
    painter.glEnableClientState(GLConstants.GL_VERTEX_ARRAY);
    painter.glEnableClientState(GLConstants.GL_NORMAL_ARRAY);
  }

  protected void disable(IPainter painter) {
    painter.glDisableClientState(GLConstants.GL_VERTEX_ARRAY);
    painter.glDisableClientState(GLConstants.GL_NORMAL_ARRAY);
  }

  protected void applyVertices(IPainter painter) {
    painter.glDrawElements(getGeometry(), size, GLConstants.GL_UNSIGNED_INT, pointer);
    doBindGL2(painter);
  }

  protected void applyWidth(IPainter painter) {
    if (geometry == GLConstants.GL_POINTS) {
      painter.glPointSize(width);
    } else if (geometry == GLConstants.GL_LINES) {
      painter.glLineWidth(width);
    }
  }

  protected void applyQuality(IPainter painter) {
    if (quality.isSmoothPolygon()) {
      painter.glEnable(GLConstants.GL_POLYGON_SMOOTH);
      painter.glHint(GLConstants.GL_POLYGON_SMOOTH_HINT, GLConstants.GL_NICEST);
    } else
      painter.glDisable(GLConstants.GL_POLYGON_SMOOTH);

    if (quality.isSmoothLine()) {
      painter.glEnable(GLConstants.GL_LINE_SMOOTH);
      painter.glHint(GLConstants.GL_LINE_SMOOTH_HINT, GLConstants.GL_NICEST);
    } else
      painter.glDisable(GLConstants.GL_LINE_SMOOTH);

    if (quality.isSmoothPoint()) {
      painter.glEnable(GLConstants.GL_POINT_SMOOTH);
      painter.glHint(GLConstants.GL_POINT_SMOOTH_HINT, GLConstants.GL_NICEST);
      // painter.glDisable(GLConstants.GL_BLEND);
      // painter.glHint(GLConstants.GL_POINT_SMOOTH_HINT, GLConstants.GL_NICEST);
    } else
      painter.glDisable(GLConstants.GL_POINT_SMOOTH);
  }

  protected void applyPolygonModeFillGL2(IPainter painter) {
    if (polygonMode == null)
      return;

    switch (polygonMode) {
      case FRONT:
        painter.glPolygonMode(GLConstants.GL_FRONT, GLConstants.GL_FILL);
        break;
      case BACK:
        painter.glPolygonMode(GLConstants.GL_BACK, GLConstants.GL_FILL);
        break;
      case FRONT_AND_BACK:
        painter.glPolygonMode(GLConstants.GL_FRONT_AND_BACK, GLConstants.GL_FILL);
        break;
      default:
        break;
    }
  }

  protected void polygonOffseFillEnable(IPainter painter) {
    painter.glEnable(GLConstants.GL_POLYGON_OFFSET_FILL);
    painter.glPolygonOffset(polygonOffsetFactor, polygonOffsetUnit);
  }

  protected void polygonOffsetFillDisable(IPainter painter) {
    painter.glDisable(GLConstants.GL_POLYGON_OFFSET_FILL);
  }


  protected void doBindGL2(IPainter painter) {
    painter.glBindBuffer(GLConstants.GL_ARRAY_BUFFER, arrayName[0]);
    painter.glBindBuffer(GLConstants.GL_ELEMENT_ARRAY_BUFFER, elementName[0]);
  }

  /**
   * Returns a {@link Rotator} that can let the VBO turn around Z axis centered at X=0,Y=0. Must be
   * started by {@link Rotator.start()} if not started explicitely.
   */
  public Rotator rotator(boolean start) {
    final Rotate r = new Rotate(25, new Coord3d(0, 0, 1));
    return rotator(start, r, 10);
  }

  public Rotator rotator(boolean start, final Rotate r, int sleep) {
    Transform t = new Transform();
    t.add(r);
    setTransformBefore(t);
    Rotator rotator = new Rotator(sleep, r);
    if (start)
      rotator.start();
    return rotator;
  }

  public Rotator rotator() {
    return rotator(false);
  }


  /*
   * An OBJ file appears to be really really slow to render without a FRONT_AND_BACK spec, probably
   * because such a big polygon set has huge cost to have culling status computed (culling enabled
   * by depth peeling).
   */
  protected void configure(IPainter painter) {
    painter.glPolygonMode(GLConstants.GL_FRONT_AND_BACK, GLConstants.GL_FILL);
    painter.color(color);
  }

  boolean hasColorBuffer = false;



  public boolean isHasColorBuffer() {
    return hasColorBuffer;
  }

  public void setHasColorBuffer(boolean hasColorBuffer) {
    this.hasColorBuffer = hasColorBuffer;
  }



  public int getGeometry() {
    return geometry;
  }

  /**
   * Set geometry, use: GLConstants.GL_TRIANGLES (default) ...
   * 
   * @param geometry
   */
  public void setGeometry(int geometry) {
    this.geometry = geometry;
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    /*
     * Coord3d c = transform.compute(new Coord3d(x,y, z)); x = c.x; y = c.y; z = c.z;
     */
    LoggerFactory.getLogger(DrawableVBO.class).warn("not implemented");
  }

  @Override
  public void updateBounds() { // requires smart reload
    LoggerFactory.getLogger(DrawableVBO.class).warn("not implemented");
  }

  /** To be called by the VBOBuilder */
  public void setData(IPainter painter, FloatVBO vbo) {
    setData(painter, vbo.getIndices(), vbo.getVertices(), vbo.getBounds(), 0);
  }


  public void setData(IPainter painter, IntBuffer indices, FloatBuffer vertices,
      BoundingBox3d bounds) {
    setData(painter, indices, vertices, bounds, 0);
  }

  public void setData(IPainter painter, IntBuffer indices, FloatBuffer vertices, BoundingBox3d bounds,
      int pointer) {
    doConfigure(pointer, indices.capacity());
    doLoadArrayFloatBuffer(painter, vertices);
    doLoadElementIntBuffer(painter, indices);
    doSetBoundingBox(bounds);
  }

  public void doConfigure(int pointer, int size) {
    int dimensions = 3;
    int byteOffset = (dimensions * 2) * Float.BYTES; // (coord+normal)
    int normalOffset = dimensions * Float.BYTES;
    doConfigure(pointer, size, byteOffset, normalOffset, dimensions);
  }

  public void doConfigure(int pointer, int size, int byteOffset, int normalOffset, int dimensions) {
    this.byteOffset = byteOffset;
    this.normalOffset = normalOffset;
    this.dimensions = dimensions;
    this.size = size;
    this.pointer = pointer;
  }

  public void doLoadArrayFloatBuffer(IPainter painter, FloatBuffer vertices) {
    doLoadArrayFloatBuffer(painter, vertices.capacity() * Float.BYTES, vertices);
  }

  public void doLoadArrayFloatBuffer(IPainter painter, int vertexSize, FloatBuffer vertices) {
    painter.glGenBuffers(1, arrayName, 0);
    painter.glBindBuffer(GLConstants.GL_ARRAY_BUFFER, arrayName[0]);
    painter.glBufferData(GLConstants.GL_ARRAY_BUFFER, vertexSize, vertices, GLConstants.GL_STATIC_DRAW);
    painter.glBindBuffer(GLConstants.GL_ARRAY_BUFFER, pointer);
  }

  public void doLoadElementIntBuffer(IPainter painter, IntBuffer indices) {
    doLoadElementIntBuffer(painter, indices.capacity() * Integer.BYTES, indices);
  }

  public void doLoadElementIntBuffer(IPainter painter, int indexSize, IntBuffer indices) {
    painter.glGenBuffers(1, elementName, 0);
    painter.glBindBuffer(GLConstants.GL_ELEMENT_ARRAY_BUFFER, elementName[0]);
    painter.glBufferData(GLConstants.GL_ELEMENT_ARRAY_BUFFER, indexSize, indices, GLConstants.GL_STATIC_DRAW);
    painter.glBindBuffer(GLConstants.GL_ELEMENT_ARRAY_BUFFER, pointer);
  }

  public void doSetBoundingBox(BoundingBox3d bounds) {
    bbox = bounds;
  }

  /* */

  public PolygonMode getPolygonMode() {
    return polygonMode;
  }

  /**
   * A null polygonMode imply no any call to painter.glPolygonMode(...) at rendering
   */
  public void setPolygonMode(PolygonMode polygonMode) {
    this.polygonMode = polygonMode;
  }

  public boolean isPolygonOffsetFillEnable() {
    return polygonOffsetFillEnable;
  }

  public void setPolygonOffsetFillEnable(boolean polygonOffsetFillEnable) {
    this.polygonOffsetFillEnable = polygonOffsetFillEnable;
  }

  public float getPolygonOffsetFactor() {
    return polygonOffsetFactor;
  }

  public void setPolygonOffsetFactor(float polygonOffsetFactor) {
    this.polygonOffsetFactor = polygonOffsetFactor;
  }

  public float getPolygonOffsetUnit() {
    return polygonOffsetUnit;
  }

  public void setPolygonOffsetUnit(float polygonOffsetUnit) {
    this.polygonOffsetUnit = polygonOffsetUnit;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  protected IGLLoader<DrawableVBO> loader;


}
