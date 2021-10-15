package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Normal;
import org.jzy3d.maths.Normal.NormalPer;
import org.jzy3d.maths.Utils;
import org.jzy3d.painters.DepthFunc;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;
import com.google.common.collect.Lists;

public abstract class Geometry extends Wireframeable implements ISingleColorable, IMultiColorable {

  /**
   * Default setting of {@link #setNormalProcessingAutomatic(boolean)}. Can be globally changed to
   * avoid setting it geometry-wise.
   */
  public static boolean NORMAL_AUTO_DEFAULT = true;

  /**
   * Default setting of {@link #setSplitInTriangles(boolean)}. Can be globally changed to avoid
   * setting it geometry-wise.
   */
  public static boolean SPLIT_TRIANGLE_DEFAULT = false;

  /**
   * Default setting of {@link #setNormalizeNormals(boolean)}. Can be globally changed to avoid
   * setting it geometry-wise.
   */
  public static boolean NORMALIZE_NORMAL_DEFAULT = true;

  /** A flag to show normals for debugging lighting */
  public static boolean SHOW_NORMALS = false;
  public static int NORMAL_LINE_WIDTH = 2;
  public static int NORMAL_POINT_WIDTH = 4;
  public static Color NORMAL_END_COLOR = Color.BLACK.clone();
  public static Color NORMAL_START_COLOR = Color.GRAY.clone();

  protected PolygonMode polygonMode;
  protected ColorMapper mapper;
  protected List<Point> points;
  protected Color color;
  protected Coord3d center;

  protected List<Coord3d> normals;

  protected boolean normalProcessingAutomatic = NORMAL_AUTO_DEFAULT;
  protected boolean splitInTriangles = SPLIT_TRIANGLE_DEFAULT;
  protected boolean normalizeNormals = NORMALIZE_NORMAL_DEFAULT;


  protected NormalPer normalPer = NormalPer.GEOMETRY;


  /**
   * Initializes an empty {@link Geometry} with face status defaulting to true, and wireframe status
   * defaulting to false.
   */
  public Geometry() {
    this(4);
  }

  public Geometry(int n) {
    super();
    points = new ArrayList<>(n);
    bbox = new BoundingBox3d();
    center = new Coord3d();
    polygonMode = PolygonMode.FRONT_AND_BACK;
  }

  public Geometry(Point... points) {
    this(points.length);
    add(points);
  }

  public Geometry(Color wireframeColor, Color faceColor, Coord3d... points) {
    this(points.length);
    add(faceColor, points);
    setWireframeColor(wireframeColor);
    setWireframeDisplayed(wireframeColor != null);
  }

  public Geometry(List<Point> points) {
    this(points.size());
    add(points);
  }

  public Geometry(Color wireframeColor, Point... points) {
    this(points);
    setWireframeColor(wireframeColor);
    setWireframeDisplayed(wireframeColor != null);
  }

  public Geometry(Color wireframeColor, boolean wireframeDisplayed, Point... points) {
    this(points);
    setWireframeColor(wireframeColor);
    setWireframeDisplayed(wireframeDisplayed);
  }

  public void setReflectLight(boolean reflectLight) {
    this.reflectLight = reflectLight;

    if (reflectLight && normals == null) {
      normals = new ArrayList<>(points.size());
    }
  }

  public List<Coord3d> getNormals() {
    return normals;
  }

  /**
   * Provides normals for this geometry.
   * 
   * Manually setting normals will disable automatic normal processing.
   * 
   * Not providing normals will keep the default automatic normal processing active.
   * 
   * You may more simply set a single normal for this geometry by calling
   * {@link #setNormal(Coord3d)}
   * 
   * @param normals
   * @param normalPer indicate if the supplied normals are given per vertex or per geometry. The
   *        ability to provide a list is kept for the case where one wishes to
   *        {@link #setSplitInTriangles(true)}.
   */
  public void setNormals(List<Coord3d> normals, NormalPer normalPer) {
    if (NormalPer.POINT.equals(normalPer)) {
      if (normals.size() != points.size()) {
        throw new IllegalArgumentException("Must have has many normals than points. Points : "
            + points.size() + " - Normals : " + normals.size());
      }
    } else if (NormalPer.GEOMETRY.equals(normalPer)) {
      if (!splitInTriangles && normals.size() != 1) {
        throw new IllegalArgumentException(
            "Must have a single normal for the whole Geometry but got : " + normals.size());
      } else if (splitInTriangles && normals.size() != 2) {
        throw new IllegalArgumentException(
            "Must have two normals for the whole Geometry because it is configured to be splitted in triangle, but got : "
                + normals.size());
      }
    }

    this.normals = normals;
    this.normalPer = normalPer;
    this.normalProcessingAutomatic = false;
  }

  public void setNormal(Coord3d normals) {
    setNormals(Lists.newArrayList(normals), NormalPer.GEOMETRY);
  }

  /**
   * Normal processing is false by default, and becomes true as soon as one provide normals through
   * {@link #setNormals(List, NormalPer)}
   * 
   * @return
   */
  public boolean isNormalProcessingAutomatic() {
    return normalProcessingAutomatic;
  }

  /**
   * Set normal processing to be automatic or not.
   * 
   * @param normalProcessingAutomatic
   */
  public void setNormalProcessingAutomatic(boolean normalProcessingAutomatic) {
    this.normalProcessingAutomatic = normalProcessingAutomatic;
  }

  /* * */
  

  @Override
  public void draw(IPainter painter) {
    doTransform(painter);

    if (mapper != null)
      mapper.preDraw(this);


    if (isReflectLight()) {
      applyMaterial(painter);
    }

    
    // Draw content of polygon
    if(depthFunctionChangeForWireframe)
      painter.glDepthFunc(DepthFunc.GL_LESS);
    
    drawFace(painter);

    // drawing order is important for EmulGL to cleanly render polygon edges
    if(depthFunctionChangeForWireframe) {
      painter.glDepthFunc(DepthFunc.GL_LEQUAL);
    }
    
    // Draw edge of polygon
    drawWireframe(painter);

    if(depthFunctionChangeForWireframe)
      painter.glDepthFunc(DepthFunc.GL_LESS);

    if (mapper != null)
      mapper.postDraw(this);

    doDrawBoundsIfDisplayed(painter);
  }

  ///////////////////////////////////
  //
  // DRAW FACES
  //
  ///////////////////////////////////


  protected void drawFace(IPainter painter) {
    if (faceDisplayed) {
      painter.glPolygonMode(polygonMode, PolygonFill.FILL);

      if (wireframeDisplayed && polygonWireframeDepthTrick)
        applyDepthRangeForUnderlying(painter); // ENABLE RANGE FOR UNDER

      if (wireframeDisplayed && polygonOffsetFillEnable)
        polygonOffsetFillEnable(painter); // ENABLE OFFSET

      callPointsForFace(painter);

      if (wireframeDisplayed && polygonOffsetFillEnable)
        polygonOffsetFillDisable(painter); // DISABLE OFFSET

      if (wireframeDisplayed && polygonWireframeDepthTrick)
        applyDepthRangeDefault(painter); // DISAABLE RANGE FOR UNDER

    }
  }

  /**
   * Drawing the point list in face mode (polygon content)
   */
  protected void callPointsForFace(IPainter painter) {
    if (splitInTriangles) {
      callPointsForFace_SplitInTriangle(painter);
    } else {
      callPointsForFace_NoSplit(painter);
    }
  }

  protected void callPointsForFace_NoSplit(IPainter painter) {
    // if(reflectLight) {
    if (normalProcessingAutomatic) {
      callPointsForFace_NoSplit_NormalAuto(painter);
    } else {
      callPointsForFace_NoSplit_NormalSupplied(painter);
    }
    /*
     * } else { callPointsForFace_NoSplit_NoNormal(painter); }
     */
  }

  protected void callPointsForFace_NoSplit_NormalSupplied(IPainter painter) {
    begin(painter);

    // invoke points for vertex and color, with 1 normal per vertex
    for (int i = 0; i < points.size(); i++) {
      Point p = points.get(i);
      if (mapper != null) {
        applyPointOrMapperColor(painter, p);
      } else {
        painter.color(p.rgb);
      }
      if (isReflectLight()) {
        if (NormalPer.POINT.equals(normalPer)) {
          painter.normal(normals.get(i));
        } else /* if(NormalPer.GEOMETRY) && */ if (i == 0) {
          painter.normal(normals.get(0));
        }
      }
      painter.vertex(p.xyz, spaceTransformer);
    }

    painter.glEnd();

    if (SHOW_NORMALS & normals != null) {
      if (NormalPer.POINT.equals(normalPer)) {
        drawPolygonNormal(painter, points, normals);
      } else /* if(NormalPer.GEOMETRY) && */ {
        drawPolygonNormal(painter, points, normals.get(0));
      }

    }
  }

  protected void callPointsForFace_NoSplit_NormalAuto(IPainter painter) {
    Coord3d normal = null;
    if (isReflectLight()) {
      normal = computeNormalAutomatic(points);
    }

    // Draw geometry
    begin(painter);

    // process a single normal for lights
    if (isReflectLight()) {
      painter.normal(normal);
    }

    // invoke points for vertex and color
    for (Point p : points) {
      if (mapper != null) {
        applyPointOrMapperColor(painter, p);
      } else {
        painter.color(p.rgb);
      }
      painter.vertex(p.xyz, spaceTransformer);
    }

    painter.glEnd();

    if (SHOW_NORMALS && normal != null) {
      drawPolygonNormal(painter, points, normal);
    }
  }

  protected void callPointsForFace_NoSplit_NoNormal(IPainter painter) {
    begin(painter);

    // invoke points for vertex and color, with 1 normal per vertex
    for (int i = 0; i < points.size(); i++) {
      Point p = points.get(i);
      if (mapper != null) {
        applyPointOrMapperColor(painter, p);
      } else {
        painter.color(p.rgb);
      }
      painter.vertex(p.xyz, spaceTransformer);
    }

    painter.glEnd();
  }

  protected void callPointsForFace_SplitInTriangle(IPainter painter) {
    int triangles = points.size() - 2;

    Point p1 = points.get(0);

    for (int triangleId = 0; triangleId < triangles; triangleId++) {
      Point p2 = points.get(triangleId + 1);
      Point p3 = points.get(triangleId + 2);

      if (normalProcessingAutomatic) {
        callPointsForFace_SplitInTriangle_NormalAuto(painter, p1, p2, p3);
      } else {
        callPointsForFace_SplitInTriangle_NormalSupplied(painter, p1, p2, p3, triangleId);
      }
    }
  }

  protected void callPointsForFace_SplitInTriangle_NormalSupplied(IPainter painter, Point p1,
      Point p2, Point p3, int t) {
    painter.glBegin_Triangle();

    // case of supplied normal per vertex
    if (normals.size() == points.size()) {
      if (isReflectLight()) {
        painter.normal(normals.get(0));
      }
      applyPointOrMapperColor(painter, p1);
      painter.vertex(p1.xyz, spaceTransformer);

      if (isReflectLight()) {
        painter.normal(normals.get(t + 1));
      }
      applyPointOrMapperColor(painter, p2);
      painter.vertex(p2.xyz, spaceTransformer);

      if (isReflectLight()) {
        painter.normal(normals.get(t + 2));
      }
      applyPointOrMapperColor(painter, p3);
      painter.vertex(p3.xyz, spaceTransformer);
    }
    // case of supplied normal per triangle
    else if (normals.size() == 1) {
      if (isReflectLight()) {
        painter.normal(normals.get(t));
      }
      applyPointOrMapperColor(painter, p1);
      painter.vertex(p1.xyz, spaceTransformer);

      applyPointOrMapperColor(painter, p2);
      painter.vertex(p2.xyz, spaceTransformer);

      applyPointOrMapperColor(painter, p3);
      painter.vertex(p3.xyz, spaceTransformer);
    }

    painter.glEnd();

    if (SHOW_NORMALS && normals != null && normals.size() >= 3) {
      drawTriangleNormal(painter, p1, p2, p3, normals.get(0), normals.get(1), normals.get(2));
    }
  }

  protected void callPointsForFace_SplitInTriangle_NormalAuto(IPainter painter, Point p1, Point p2,
      Point p3) {
    Coord3d normal = null;
    if (isReflectLight()) {
      normal = computeNormalAutomatic(p1, p2, p3);
    }

    painter.glBegin_Triangle();


    // process normal for lights
    if (isReflectLight()) {
      painter.normal(normal);
    }

    applyPointOrMapperColor(painter, p1);
    painter.vertex(p1.xyz, spaceTransformer);
    applyPointOrMapperColor(painter, p2);
    painter.vertex(p2.xyz, spaceTransformer);
    applyPointOrMapperColor(painter, p3);
    painter.vertex(p3.xyz, spaceTransformer);

    painter.glEnd();

    if (SHOW_NORMALS && normal != null) {
      drawTriangleNormal(painter, p1, p2, p3, normal);
    }
  }

  protected Coord3d computeNormalAutomatic(List<Point> points) {
    return Normal.compute(points, normalizeNormals, false);
  }

  protected Coord3d computeNormalAutomatic(Point p1, Point p2, Point p3) {
    return Normal.compute(p1.xyz, p2.xyz, p3.xyz, normalizeNormals);
  }

  /**
   * Apply mapper color if a mapper is defined and store the result in the point color, or use point
   * color if mapper is undefined.
   */
  protected void applyPointOrMapperColor(IPainter painter, Point p) {
    if (mapper != null) {
      Color c = mapper.getColor(p.xyz);
      painter.color(c);

      // store this color in case it should be used for drawing
      // the wireframe as stated by setWireframeColorFrom...
      p.rgb = c;
    } else {
      painter.color(p.rgb);
    }
  }


  //////////// DRAW NORMALS FOR DEBUGGING //////////////

  protected void drawPolygonNormal(IPainter painter, List<Point> points, Coord3d normal) {
    Coord3d start = new Coord3d();

    for (Point p : points) {
      start.addSelf(p.xyz);
    }
    start.divSelf(points.size());

    Coord3d end = start.add(normal);

    drawNormal(painter, start, end);
  }

  protected void drawPolygonNormal(IPainter painter, List<Point> points, List<Coord3d> normal) {
    for (int i = 0; i < points.size(); i++) {
      Coord3d start = points.get(i).xyz;
      Coord3d end = start.add(normal.get(i));

      drawNormal(painter, start, end);
    }
  }

  protected void drawTriangleNormal(IPainter painter, Point p1, Point p2, Point p3,
      Coord3d normal) {
    Coord3d start = new Coord3d();
    start.addSelf(p1.xyz);
    start.addSelf(p2.xyz);
    start.addSelf(p3.xyz);
    start.divSelf(3);

    Coord3d end = start.add(normal);

    drawNormal(painter, start, end);
  }

  protected void drawTriangleNormal(IPainter painter, Point p1, Point p2, Point p3, Coord3d n1,
      Coord3d n2, Coord3d n3) {
    drawNormal(painter, p1.xyz, p1.xyz.add(n1));
    drawNormal(painter, p2.xyz, p2.xyz.add(n2));
    drawNormal(painter, p3.xyz, p3.xyz.add(n3));
  }

  protected void drawNormal(IPainter painter, Coord3d start, Coord3d end) {
    // normal line
    painter.glLineWidth(NORMAL_LINE_WIDTH);
    painter.glBegin_Line();
    painter.color(NORMAL_START_COLOR);
    painter.vertex(start, spaceTransformer);
    painter.color(NORMAL_END_COLOR);
    painter.vertex(end, spaceTransformer);
    painter.glEnd();

    // normal arrow
    painter.glPointSize(NORMAL_POINT_WIDTH);
    painter.glBegin_Point();
    painter.color(NORMAL_START_COLOR);
    painter.vertex(start, spaceTransformer);
    painter.color(NORMAL_END_COLOR);
    painter.vertex(end, spaceTransformer);
    painter.glEnd();
  }


  ///////////////////////////////////
  //
  // WIREFRAME
  //
  ///////////////////////////////////

  protected void drawWireframe(IPainter painter) {
    if (wireframeDisplayed) {
      painter.glPolygonMode(polygonMode, PolygonFill.LINE);

      if (polygonWireframeDepthTrick)
        applyDepthRangeForOverlying(painter); // OVER - enable range

      if (polygonOffsetFillEnable)
        polygonOffsetFillEnable(painter);

      callPointForWireframe(painter);

      if (polygonOffsetFillEnable)
        polygonOffsetFillDisable(painter);

      if (polygonWireframeDepthTrick)
        applyDepthRangeDefault(painter); // OVER - disable range

    }
  }

  /**
   * Drawing the point list in wireframe mode
   */
  protected void callPointForWireframe(IPainter painter) {
    if (!isWireframeColorFromPolygonPoints()) {
      painter.color(wireframeColor);
    }

    painter.glLineWidth(getWireframeWidth());


    if (wireframeWithLineLoop) {
      // changed for JGL as wireframe polygon are transformed to pair of
      // triangles
      painter.glBegin_LineLoop(); 

      for (Point p : points) {
        if (isWireframeColorFromPolygonPoints()) {
          painter.color(p.rgb);
        }

        painter.vertex(p.xyz, spaceTransformer);
      }
      painter.glEnd();
    }
    else {
      
      // default Draw geometry
      begin(painter);

      // invoke points for vertex and color
      for (Point p : points) {
        if (isWireframeColorFromPolygonPoints()) {
          painter.color(p.rgb);
        }
        painter.vertex(p.xyz, spaceTransformer);
      }

      painter.glEnd();
    }
  }



  /**
   * Invoke GL begin with the actual geometry type {@link GL#GL_POINTS}, {@link GL#GL_LINES},
   * {@link GL#GL_TRIANGLES}, {@link GL2#GL_POLYGON} ...
   */
  protected abstract void begin(IPainter painter);

  /* DATA */

  public void add(float x, float y, float z) {
    add(new Coord3d(x, y, z));
  }

  public void add(Coord3d coord) {
    add(new Point(coord, wireframeColor), true);
  }

  public void add(Coord3d coord, Color color, boolean updateBounds) {
    add(new Point(coord, color), updateBounds);
  }

  public void add(Color faceColor, Coord3d... coords) {
    for (Coord3d coord : coords) {
      add(coord, faceColor, false);
    }
    updateBounds();
  }

  public void add(Point point) {
    add(point, true);
  }

  /** Add a point to the polygon. */
  public void add(Point point, boolean updateBounds) {
    points.add(point);
    if (updateBounds) {
      updateBounds();
    }
  }

  public void add(Point... points) {
    for (Point p : points) {
      add(p, false);
    }
    updateBounds();
  }

  public void add(List<Point> points) {
    for (Point p : points) {
      add(p, false);
    }
    updateBounds();
  }


  @Override
  public void applyGeometryTransform(Transform transform) {
    for (Point p : points) {
      p.xyz = transform.compute(p.xyz);
    }
    updateBounds();
  }

  @Override
  public void updateBounds() {
    bbox.reset();
    bbox.add(getPoints());

    // recompute center
    center = new Coord3d();
    for (Point p : points)
      center = center.add(p.xyz);
    center = center.div(points.size());
  }

  @Override
  public Coord3d getBarycentre() {
    return center;
  }

  public Point get(int p) {
    return points.get(p);
  }

  /** Returns the list of the mutable points held by this polygon. */
  public List<Point> getPoints() {
    return points;
  }

  /** Returns a set of the mutable points held by this polygon. */
  public Set<Point> getPointSet() {
    Set<Point> set = new HashSet<>();
    for (Point p : points) {
      set.add(p);
    }
    return set;
  }

  /** Returns a set of the mutable coordinates held by this polygon points. */
  public Set<Coord3d> getCoordSet() {
    Set<Coord3d> set = new HashSet<>();
    for (Point p : points) {
      set.add(p.xyz);
    }
    return set;
  }

  /** Returns a list of the mutable coordinates held by this polygon points. */
  public List<Coord3d> getCoordList() {
    List<Coord3d> list = new ArrayList<>();
    for (Point p : points) {
      list.add(p.xyz);
    }
    return list;
  }

  /** Returns an array of the mutable coordinates held by this polygon points. */
  public Coord3d[] getCoordArray() {
    Coord3d[] array = new Coord3d[size()];
    int k = 0;
    for (Point p : getPoints()) {
      array[k++] = p.xyz;
    }
    return array;
  }


  public int size() {
    return points.size();
  }

  /* DISTANCES */

  @Override
  public double getDistance(Camera camera) {
    return getBarycentre().distance(camera.getEye());
  }

  @Override
  public double getShortestDistance(Camera camera) {
    double min = Float.MAX_VALUE;
    double dist = 0;
    for (Point point : points) {
      dist = point.getDistance(camera);
      if (dist < min)
        min = dist;
    }

    dist = getBarycentre().distance(camera.getEye());
    if (dist < min)
      min = dist;
    return min;
  }

  @Override
  public double getLongestDistance(Camera camera) {
    double max = 0;
    double dist = 0;
    for (Point point : points) {
      dist = point.getDistance(camera);
      if (dist > max)
        max = dist;
    }
    return max;
  }

  /* SETTINGS */

  public PolygonMode getPolygonMode() {
    return polygonMode;
  }

  /**
   * A null polygonMode imply no any call to gl.glPolygonMode(...) at rendering
   */
  public void setPolygonMode(PolygonMode polygonMode) {
    this.polygonMode = polygonMode;
  }

  /* COLOR */

  @Override
  public void setColorMapper(ColorMapper mapper) {
    this.mapper = mapper;

    fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
  }

  @Override
  public ColorMapper getColorMapper() {
    return mapper;
  }

  @Override
  public void setColor(Color color) {
    this.color = color;

    for (Point p : points)
      p.setColor(color);

    fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
  }

  @Override
  public Color getColor() {
    return color;
  }

  public boolean isSplitInTriangles() {
    return splitInTriangles;
  }

  /**
   * Splitting a polygon in multiple triangles is said to be better to adress complex rendering
   * issues in OpenGL, but has a (very small) overhead.
   */
  public void setSplitInTriangles(boolean splitInTriangles) {
    this.splitInTriangles = splitInTriangles;
  }



  public boolean isNormalizeNormals() {
    return normalizeNormals;
  }

  /**
   * Normalizing normals is usefull to ensure that polygons of different sizes will have a
   * consistent light reflection. Indeed, a large polygon will have a high normal value compared to
   * a small polygon.
   * 
   * Normalizing requires a bit more computation though.
   * 
   * This setting will only change something as long as
   * <ul>
   * <li>The object is configured to {@link #setReflectLight(true)} and if a {@link Light} is added
   * to the chart.
   * <li>The object is configured to {@link #setNormalProcessingAutomatic(true)}. Normals processed
   * externally should be normalized externally.
   */
  public void setNormalizeNormals(boolean normalizeNormals) {
    this.normalizeNormals = normalizeNormals;
  }

  @Override
  public String toString(int depth) {
    return (Utils.blanks(depth) + "(" + this.getClass().getSimpleName() + ") #points:"
        + points.size());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((normals == null) ? 0 : normals.hashCode());
    result = prime * result + ((points == null) ? 0 : points.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Geometry other = (Geometry) obj;
    if (normals == null) {
      if (other.normals != null)
        return false;
    } else if (!normals.equals(other.normals))
      return false;
    if (points == null) {
      if (other.points != null)
        return false;
    } else if (!points.equals(other.points))
      return false;
    return true;
  }


}
