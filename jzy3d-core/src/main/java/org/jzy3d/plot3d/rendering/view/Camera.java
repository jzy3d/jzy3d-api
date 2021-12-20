package org.jzy3d.plot3d.rendering.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PolygonArray;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.transform.Transform;

/**
 * The {@link Camera} allows projecting a 3d scene to a 2d screen based on an orthogonal or
 * perspective projection.
 * 
 * The 3d world part displayed on the 2d screen is defined by the following parameters which are
 * illustrated on the image below
 * <ul>
 * <li>{@link Camera#target} indicates the position of the point that the camera is currently
 * centering on.
 * <li>{@link Camera#eye} indicates the position of the lens of the camera.
 * <li>{@link Camera#up} indicates the direction of the top of the camera.
 * <li>{@link Camera#radius} indicates the width of the field of view when working with
 * {@link CameraMode#ORTHOGONAL} projections
 * <li>{@link Camera#near} defines the distance from which a 3d item is visible by camera.
 * <li>{@link Camera#far} defines the distance up to which a 3d item is visible by camera.
 * </ul>
 * 
 * <br>
 * <img src="doc-files/camera.png"/>
 * <a href="https://lucid.app/lucidchart/78ec260b-d2d1-430d-a363-a95089dae86d/edit?page=bKd5zgy4FZv5#">Schema source</a>
 * <br>
 * 
 * All camera settings are in cartesian coordinates.
 * 
 * @author Martin Pernollet
 */
public class Camera extends AbstractViewportManager {
  private static final Logger LOGGER = LogManager.getLogger(Camera.class);

  /** The polar default view point, i.e. Coord3d(Math.PI/3,Math.PI/5,500). */
  public static final Coord3d DEFAULT_VIEW = new Coord3d(Math.PI / 3, Math.PI / 5, 500);

  /**
   * Defines if camera distance is real, or only squared (squared distance avoid computing
   * Math.sqrt() and is thus faster). Second mode requires value true
   */
  public static final boolean DEFAULT_CAMERA_DISTANCE_MODE = true;

  /**
   * Defines if camera distance is real, or only squared (squared distance avoid computing
   * Math.sqrt() and is thus faster).
   */
  protected boolean useSquaredDistance = DEFAULT_CAMERA_DISTANCE_MODE;

  /** The camera viewpoint */
  protected Coord3d eye;
  /** The camera target */
  protected Coord3d target;
  /** The camera up vector */
  protected Coord3d up;
  /** The scale used to display elements */
  protected Coord3d scale;

  /** Predicate caching pre-computed data to quickly decide if a point is 'on the left'. */
  private Predicate<Coord3d> isOnLeftSide;

  /**
   * The rendering radius, used to automatically define with/height of scene and distance of
   * clipping planes.
   */
  protected float radius;
  /** The distance between the camera eye and the near clipping plane. */
  protected float near;
  /** The distance between the camera eye and the far clipping plane. */
  protected float far;

  /**
   * The configuration used to make orthogonal rendering.
   * 
   * @see glOrtho
   */
  protected Ortho ortho = new Ortho();

  /**
   * Set up a Camera looking at target, with a viewpoint standing at target+(0,0,100). The top of
   * the camera is set up toward the positive Z direction.
   */
  public Camera(Coord3d target) {
    initWithTarget(target);
  }

  public void initWithTarget(Coord3d initialTarget) {
    Coord3d initialEye = DEFAULT_VIEW.cartesian().add(initialTarget);
    Coord3d initialUp = new Coord3d(0, 0, 1);
    Coord3d initialScale = new Coord3d(1, 1, 1);
    setPosition(initialEye, initialTarget, initialUp, initialScale);

    setViewPort(1, 1, 0, 1);
    setRenderingDepth(0.5f, 100000f);
    setRenderingSphereRadius(1);
    setViewportMode(ViewportMode.RECTANGLE_NO_STRETCH);
  }

  public Camera() {
    this(Coord3d.ORIGIN.clone());
  }

  /******************************************************************/

  /**
   * Set the position of this camera's lens.
   * <p>
   * All other parameters of this camera remain unchanged. To change multiple parameters atomically
   * use {@link #setPosition(Coord3d, Coord3d, Coord3d, Coord3d)}.
   * </p>
   * 
   * @param eye the new <em>scaled</em> position of this camera's lens.
   */
  public void setEye(Coord3d eye) {
    setPosition(eye, target);
  }

  /** Returns the eye's position. */
  public Coord3d getEye() {
    return eye;
  }

  /**
   * Set the position of the target at which this camera is centering.
   * <p>
   * All other parameters of this camera remain unchanged. To change multiple parameters atomically
   * use {@link #setPosition(Coord3d, Coord3d, Coord3d, Coord3d)}.
   * </p>
   * 
   * @param target the new <em>scaled</em> position of this camera's target
   */
  public void setTarget(Coord3d target) {
    setPosition(eye, target);
  }

  /** Returns the target's position that was set at the last call to lookAt(). */
  public Coord3d getTarget() {
    return target;
  }

  /**
   * Set the up-direction of this camera.
   * <p>
   * All other parameters of this camera remain unchanged. To change multiple parameters atomically
   * use {@link #setPosition(Coord3d, Coord3d, Coord3d, Coord3d)}.
   * </p>
   * 
   * @param up the new up-direction of this camera
   */
  public void setUp(Coord3d up) {
    setPosition(eye, target, up, scale);
  }

  /** Returns the top of the camera. */
  public Coord3d getUp() {
    return this.up;
  }

  /** Returns the scale used by this camera to display elements. */
  public Coord3d getScale() {
    return scale;
  }

  /**
   * Set the camera's eye- and target-location.
   * <p>
   * All other parameters of this camera remain unchanged. To change more parameters atomically use
   * {@link #setPosition(Coord3d, Coord3d, Coord3d, Coord3d)}.
   * </p>
   * 
   * @param eye
   * @param target
   */
  public void setPosition(Coord3d eye, Coord3d target) {
    setPosition(eye, target, up, scale);
  }

  /**
   * Atomically sets this camera's eye- and target-position, its up-direction and the scale with
   * which data are displayed.
   *
   * @param eye the scaled location of the camera eye (previously multiplied with this camera's
   *        scale)
   * @param target the scaled location at which this camera will look (previously multiplied with
   *        this camera's scale)
   * @param up the direction of the up-side of this camera (not scaled)
   * @param scale the scale used by this camera to display elements
   */
  public void setPosition(Coord3d eye, Coord3d target, Coord3d up, Coord3d scale) {
    this.eye = eye;
    this.target = target;
    this.up = up;
    this.scale = scale;
    this.isOnLeftSide = null; // trigger full re-computation of all data for side-computation
  }

  /**
   * Returns true if the camera is 'looking up', in other word if the eye's Z value is inferior to
   * the target's Z value.
   * 
   * @see {@link #side(Coord3d)}
   */
  public boolean isTiltUp() {
    return eye.z < target.z;
  }

  /**
   * Set the radius of the sphere that will be contained into the rendered view. The "far" and
   * "near" clipping planes are modified according to the eye-target distance.
   */
  public void setRenderingSphereRadius(float radius) {
    this.radius = radius;
    this.near = (float) eye.distance(target) - radius * 2;
    this.far = (float) eye.distance(target) + radius * 2;
  }

  /**
   * Return the radius of the sphere that will be contained into the rendered view.
   */
  public float getRenderingSphereRadius() {
    return radius;
  }

  /**
   * Manually set the rendering depth (near and far clipping planes). Note that
   * {@link Camera.setRenderingSphereRadius} modified the "far" clipping plane.
   */
  public void setRenderingDepth(float near, float far) {
    this.near = near;
    this.far = far;
  }

  /** Return the position of the "near" clipping plane */
  public float getNear() {
    return near;
  }

  /** Return the position of the "far" clipping plane */
  public float getFar() {
    return far;
  }

  /**
   * Return true if the given (not scaled) point is on the left of the plane build by the
   * eye->target and up-direction vector, else false.
   *
   * @param point not scaled point (its values correspond to a point in the scene)
   * @return true if the given point is 'on the left side' of this camera
   */
  public boolean side(Coord3d point) {
    // Compute the sign of the triple product of eye-to-point-direction, up-direction and
    // eye-to-target-direction (all not scaled). The given point is left of this camera's view-plane
    // build by eye-to-target-vector and the up-direction, if the following inequation holds
    // <e-p> * (<up> x <e-t>) > 0
    if (isOnLeftSide == null) {
      // Pre-compute and capture the required data in a Predicate to quickly decide (without
      // creating objects) if a point is on the left of the current camera position.

      Coord3d e = eye.div(scale); // un-scaled eye vector
      Coord3d viewDirectionU = target.sub(eye).div(scale).normalizeTo(1f);
      // Left pointing normal vector of the view-plane build by the eye->target-vector and the
      // up-direction (effectivly the cross product of up-direction and eye-target-vector).
      // Not scaled but normalized to length one for numerical stability.
      Coord3d vpn = up.getNormalizedTo(1f).cross(viewDirectionU); // view-plane normal

      isOnLeftSide = p -> 0 < (p.x - e.x) * vpn.x + (p.y - e.y) * vpn.y + (p.z - e.z) * vpn.z;
      // equivalent to: 0 < p.sub(e).dot(vpn)
    }
    return this.isOnLeftSide.test(point);
  }

  /** Return last values used to make orthogonal scene rendering. Do not edit. */
  public Ortho getOrtho() {
    return ortho;
  }

  /************************** PROJECT / UNPROJECT ****************************/

  /**
   * Transform a 2d screen coordinate into a 3d coordinate. The z component of the screen coordinate
   * indicates a depth value between the near and far clipping plane of the {@link Camera}.
   * 
   * @throws a RuntimeException if an error occured while trying to retrieve model coordinates
   */
  public Coord3d screenToModel(IPainter painter, Coord3d screen) {
    int viewport[] = painter.getViewPortAsInt();
    float modelView[] = painter.getModelViewAsFloat();
    float projection[] = painter.getProjectionAsFloat();
    float worldcoord[] = new float[3];// wx, wy, wz;// returned xyz coords

    boolean s = painter.gluUnProject(screen.x, screen.y, screen.z, modelView, 0, projection, 0,
        viewport, 0, worldcoord, 0);
    if (!s)
      failedProjection("Could not retrieve screen coordinates in model.");

    return new Coord3d(worldcoord[0], worldcoord[1], worldcoord[2]);
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
   * NativeDesktopPainter p = (NativeDesktopPainter)chart.getPainter();
   * p.getCurrentContext(chart.getCanvas()).makeCurrent(); // make context current
   * 
   * Coord3d screen2dCoord = camera.modelToScreen(chart.getPainter(), world3dCoord);
   * 
   * p.getCurrentContext(chart.getCanvas()).release(); // release context to let other use it
   * </code>
   * </pre>
   * 
   * @throws a RuntimeException if an error occured while trying to retrieve model coordinates AND
   *         if {@link #failOnException} is set to true (default is false). In case
   *         {@link #failOnException} is false, a DEBUG log is sent to the {@link #LOGGER}.
   */
  public Coord3d modelToScreen(IPainter painter, Coord3d point) {
    int viewport[] = painter.getViewPortAsInt();

    float screenCoord[] = new float[3];// wx, wy, wz;// returned xyz coords

    if (!painter.gluProject(point.x, point.y, point.z, painter.getModelViewAsFloat(), 0,
        painter.getProjectionAsFloat(), 0, viewport, 0, screenCoord, 0))
      failedProjection("Could not retrieve model coordinates in screen for " + point);
    return new Coord3d(screenCoord[0], screenCoord[1], screenCoord[2]);
  }

  public Coord3d[] modelToScreen(IPainter painter, Coord3d[] points) {
    int viewport[] = painter.getViewPortAsInt();

    float screenCoord[] = new float[3];

    Coord3d[] projection = new Coord3d[points.length];

    for (int i = 0; i < points.length; i++) {
      if (!painter.gluProject(points[i].x, points[i].y, points[i].z, painter.getModelViewAsFloat(),
          0, painter.getProjectionAsFloat(), 0, viewport, 0, screenCoord, 0))
        failedProjection("Could not retrieve model coordinates in screen for " + points[i]);
      projection[i] = new Coord3d(screenCoord[0], screenCoord[1], screenCoord[2]);
    }
    return projection;
  }

  public Coord3d[][] modelToScreen(IPainter painter, Coord3d[][] points) {
    int viewport[] = painter.getViewPortAsInt();

    float screenCoord[] = new float[3];

    Coord3d[][] projection = new Coord3d[points.length][points[0].length];

    for (int i = 0; i < points.length; i++) {
      for (int j = 0; j < points[i].length; j++) {
        if (!painter.gluProject(points[i][j].x, points[i][j].y, points[i][j].z,
            painter.getModelViewAsFloat(), 0, painter.getProjectionAsFloat(), 0, viewport, 0,
            screenCoord, 0))
          failedProjection("Could not retrieve model coordinates in screen for " + points[i][j]);
        projection[i][j] = new Coord3d(screenCoord[0], screenCoord[1], screenCoord[2]);
      }
    }
    return projection;
  }

  public List<Coord3d> modelToScreen(IPainter painter, List<Coord3d> points) {
    int viewport[] = painter.getViewPortAsInt();

    float screenCoord[] = new float[3];

    List<Coord3d> projection = new Vector<Coord3d>();

    for (Coord3d point : points) {
      if (!painter.gluProject(point.x, point.y, point.z, painter.getModelViewAsFloat(), 0,
          painter.getProjectionAsFloat(), 0, viewport, 0, screenCoord, 0))
        failedProjection("Could not retrieve model coordinates in screen for " + point);
      projection.add(new Coord3d(screenCoord[0], screenCoord[1], screenCoord[2]));
    }
    return projection;
  }

  public ArrayList<ArrayList<Coord3d>> modelToScreen(IPainter painter,
      ArrayList<ArrayList<Coord3d>> polygons) {
    int viewport[] = painter.getViewPortAsInt();

    float screenCoord[] = new float[3];

    ArrayList<ArrayList<Coord3d>> projections = new ArrayList<ArrayList<Coord3d>>(polygons.size());

    for (ArrayList<Coord3d> polygon : polygons) {
      ArrayList<Coord3d> projection = new ArrayList<Coord3d>(polygon.size());
      for (Coord3d point : polygon) {
        if (!painter.gluProject(point.x, point.y, point.z, painter.getModelViewAsFloat(), 0,
            painter.getProjectionAsFloat(), 0, viewport, 0, screenCoord, 0))
          failedProjection("Could not retrieve model coordinates in screen for " + point);
        projection.add(new Coord3d(screenCoord[0], screenCoord[1], screenCoord[2]));
      }
      projections.add(projection);
    }
    return projections;
  }

  public PolygonArray modelToScreen(IPainter painter, PolygonArray polygon) {
    int viewport[] = painter.getViewPortAsInt();

    float screenCoord[] = new float[3];

    int len = polygon.length();

    float[] x = new float[len];
    float[] y = new float[len];
    float[] z = new float[len];

    for (int i = 0; i < len; i++) {
      if (!painter.gluProject(polygon.x[i], polygon.y[i], polygon.z[i],
          painter.getModelViewAsFloat(), 0, painter.getProjectionAsFloat(), 0, viewport, 0,
          screenCoord, 0))
        failedProjection("Could not retrieve model coordinates in screen for point " + i);
      x[i] = screenCoord[0];
      y[i] = screenCoord[1];
      z[i] = screenCoord[2];
    }
    return new PolygonArray(x, y, z);
  }

  public PolygonArray[][] modelToScreen(IPainter painter, PolygonArray[][] polygons) {
    int viewport[] = painter.getViewPortAsInt();
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
          if (!painter.gluProject(polygon.x[k], polygon.y[k], polygon.z[k],
              painter.getModelViewAsFloat(), 0, painter.getProjectionAsFloat(), 0, viewport, 0,
              screencoord, 0))
            failedProjection("Could not retrieve model coordinates in screen for point " + k);
          x[k] = screencoord[0];
          y[k] = screencoord[1];
          z[k] = screencoord[2];
        }
        projections[i][j] = new PolygonArray(x, y, z);
      }
    }
    return projections;
  }

  protected void failedProjection(String message) {
    if (failOnException)
      throw new RuntimeException(message);
    //else
    //  LOGGER.debug(message);
  }

  boolean failOnException = false;

  /*******************************************************************/

  public void show(IPainter painter, Transform transform, Coord3d scaling) {
    if (transform != null)
      transform.execute(painter);

    Coord3d eye = getEye().mul(scaling);

    painter.glBegin_Point();
    painter.glPointSize(camWidth);
    painter.glColor4f(camColor.r, camColor.g, camColor.b, camColor.a);
    painter.glVertex3f(eye.x, eye.y, eye.z);
    painter.glEnd();

  }

  Color camColor = Color.BLACK;
  int camWidth = 3;

  /* */

  /**
   * Sets the projection and the mapping of the 3d model to 2d screen. The projection must be either
   * Camera.PERSPECTIVE or Camera.ORTHOGONAL. <br>
   * shoot() finally calls the GL function glLookAt, according to the stored eye, target, up and
   * scale values. <br>
   * Note that the Camera set by itselft the MatrixMode to model view at the end of a shoot(). <br>
   * 
   * @param painter TODO
   * @param projection the projection mode.
   * @throws a Runtime Exception if the projection mode is neither Camera.PERSPECTIVE nor
   *         Camera.ORTHOGONAL.
   */
  public void shoot(IPainter painter, CameraMode projection) {
    shoot(painter, projection, false);
  }

  public void shoot(IPainter painter, CameraMode projection, boolean doPushMatrixBeforeShooting) {
    painter.glMatrixMode_Projection();

    if (doPushMatrixBeforeShooting)
      painter.glPushMatrix();

    painter.glLoadIdentity();

    doShoot(painter, projection);
  }

  public void doShoot(IPainter painter, CameraMode projection) {
    // Set viewport
    ViewportConfiguration viewport = applyViewport(painter);

    // Set projection
    if (projection == CameraMode.PERSPECTIVE) {
      projectionPerspective(painter, viewport);
    } else if (projection == CameraMode.ORTHOGONAL) {
      projectionOrtho(painter, viewport);
    } else
      throw new RuntimeException("Camera.shoot(): unknown projection mode '" + projection + "'");

    // Set camera position
    doLookAt(painter);
  }

  /**
   * Perform a perspective projection by processing the field of view based on the {@link #radius},
   * {@link #target} and {@link #eye}.
   * 
   * <img src="doc-files/perspective.png"/>
   * 
   * @param painter
   * @param viewport
   * @see {@link #projectionOrtho(IPainter, ViewportConfiguration)}
   */
  public void projectionPerspective(IPainter painter, ViewportConfiguration viewport) {
    boolean stretchToFill = ViewportMode.STRETCH_TO_FILL.equals(viewport.getMode());
    double fov = computeFieldOfView(radius * 2, eye.distance(target));
    float aspect = stretchToFill ? ((float) screenWidth) / ((float) screenHeight) : 1;
    float nearCorrected = near <= 0 ? 0.000000000000000000000000000000000000001f : near;

    painter.gluPerspective(fov / 1, aspect * 0.55, nearCorrected, far);

    // painter.glFrustum(-radius*3, radius*3, -radius*3, radius*3, near, far);
  }

  public void doLookAt(IPainter painter) {
    painter.gluLookAt(eye.x, eye.y, eye.z, target.x, target.y, target.z, up.x, up.y, up.z);
  }

  /**
   * Perform a orthogonal projection.
   * 
   * The viewable part of the 3d scene is defined by parameters {left, right, bottom, top, near,
   * far} which are processed according to the {@link ViewportMode} and the values of the camera
   * settings ({@link #radius}, {@link #target} and {@link #eye}, {@link #near} and {@link #far}
   * clipping planes).
   * 
   * <br>
   * <img src="doc-files/orthogonal.png"/>
   * 
   * @param painter
   * @param viewport
   * @see {@link #projectionPerspective(IPainter, ViewportConfiguration)}
   */
  public void projectionOrtho(IPainter painter, ViewportConfiguration viewport) {
    if (ViewportMode.STRETCH_TO_FILL.equals(viewport.getMode())) {
      ortho.update(-radius, +radius, -radius, +radius, near, far);
    } else if (ViewportMode.RECTANGLE_NO_STRETCH.equals(viewport.getMode())) {
      ortho.update(-radius * viewport.ratio(), +radius * viewport.ratio(), -radius, +radius, near,
          far);
    } else if (ViewportMode.SQUARE.equals(viewport.getMode())) {
      ortho.update(-radius, +radius, -radius, +radius, near, far);
    }

    ortho.apply(painter);
  }

  /**
   * Compute the field of View, in order to occupy the entire screen in PERSPECTIVE mode.
   */
  protected double computeFieldOfView(double size, double distance) {
    double radianTheta = 2.0 * Math.atan2(size / 2.0, distance);
    return (180.0 * radianTheta) / Math.PI;
  }

  /**
   * Return the distance between the camera eye and the given drawable's barycenter.
   */
  public double getDistance(Drawable drawable) {
    if (useSquaredDistance)
      return drawable.getBarycentre().distanceSq(getEye());
    else
      return drawable.getBarycentre().distance(getEye());
  }

  /**
   * Apply scaling before computing distance between the camera eye and the given drawable's
   * barycenter.
   */
  public double getDistance(Drawable drawable, Coord3d viewScale) {
    if (useSquaredDistance)
      return drawable.getBarycentre().distanceSq(getEye().div(viewScale));
    else
      return drawable.getBarycentre().distance(getEye().div(viewScale));
  }

  /**
   * Return the distance between the camera eye and the given coordinate.
   */
  public double getDistance(Coord3d coord) {
    if (useSquaredDistance)
      return coord.distanceSq(getEye());
    else
      return coord.distance(getEye());
  }

  /**
   * Apply scaling before computing distance between the camera eye and the given coordinate.
   */
  public double getDistance(Coord3d coord, Coord3d viewScale) {
    if (useSquaredDistance)
      return coord.distanceSq(getEye().div(viewScale));
    else
      return coord.distance(getEye().div(viewScale));
  }

  public boolean isUseSquaredDistance() {
    return useSquaredDistance;
  }

  /**
   * Defines what getDistance(...) will return, either:
   * <ul>
   * <li>Squared distance (faster to compute since no Math.sqrt(...) at the end)
   * <li>Real distance
   * </ul>
   * 
   * Default value is set to true, meaning it use the faster squared distance.
   */
  public void setUseSquaredDistance(boolean useSquaredDistance) {
    this.useSquaredDistance = useSquaredDistance;
  }

  /* */

  /**
   * Print out in console information concerning the camera.
   */
  @Override
  public String toString() {
    return toString(eye, target, up);
  }

  protected String toString(Coord3d eye, Coord3d target, Coord3d up) {
    String output = "(Camera)";
    output += (" lookFrom  = {" + eye.x + ", " + eye.y + ", " + eye.z + "}");
    output += (" lookTo    = {" + target.x + ", " + target.y + ", " + target.z + "}");
    output += (" topToward = {" + up.x + ", " + up.y + ", " + up.z + "}");
    return output;
  }

  /**
   * The configuration used to call glOrtho
   * 
   */
  public static class Ortho {
    /** the latest value used to invoke glOrtho */
    public double left;
    /** the latest value used to invoke glOrtho */
    public double right;
    /** the latest value used to invoke glOrtho */
    public double bottom;
    /** the latest value used to invoke glOrtho */
    public double top;
    /** the latest value used to invoke glOrtho */
    public double near;
    /** the latest value used to invoke glOrtho */
    public double far;

    public Ortho() {}

    public void update(double left, double right, double bottom, double top, double near,
        double far) {
      this.left = left;
      this.right = right;
      this.bottom = bottom;
      this.top = top;
      this.near = near;
      this.far = far;
    }

    /**
     * Applies orthogonal projection only if parameters are valid (i.e. not zero)
     */
    public void apply(IPainter painter) {
      if (left != 0 && right != 0 && bottom != 0 && top != 0 && near != 0 && far != 0) {
        painter.glOrtho(left, right, bottom, top, near, far);
      }
    }

    @Override
    public String toString() {
      return "left:" + left + " right:" + right + " bottom:" + bottom + " top:" + top + " near:"
          + near + " far:" + far;
    }
  }
}
