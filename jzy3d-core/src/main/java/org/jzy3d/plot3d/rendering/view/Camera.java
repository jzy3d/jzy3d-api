package org.jzy3d.plot3d.rendering.view;

import java.util.function.Predicate;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.rendering.view.modes.ProjectionMode;
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
 * <li>{@link Camera#setRenderingSphereRadius(float)} allows defining the volume to capture with the
 * camera. Alternatively, a 2D view will use {@link Camera#setRenderingSquare(BoundingBox2d)} to
 * define the volume to capture with the camera. They are used to define the width of the field of
 * view.
 * <li>{@link Camera#near} defines the distance from which a 3d item is visible by camera.
 * <li>{@link Camera#far} defines the distance up to which a 3d item is visible by camera.
 * </ul>
 * 
 * <br>
 * <img src="doc-files/camera.png"/> <a href=
 * "https://lucid.app/lucidchart/78ec260b-d2d1-430d-a363-a95089dae86d/edit?page=bKd5zgy4FZv5#">Schema
 * source</a> <br>
 * 
 * All camera settings are in cartesian coordinates.
 * 
 * @see http://www.songho.ca/opengl/gl_transform.html for explanations on the maths being 3D to 2D
 *      projection.
 * 
 * @author Martin Pernollet
 */
public class Camera extends AbstractViewportManager {
  // private static final Logger LOGGER = LogManager.getLogger(Camera.class);

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
   * Indicates if we are processing visible volume for 3D or 2D charts. 3D chart will lead to
   * processing a {@link #setRenderingSphereRadius(float)}, while 2D chart will lead to processing a
   * {@link #setRenderingSquare(float, float, float, float, float, float)}
   */
  protected ProjectionMode projectionMode = ProjectionMode.Projection3D;

  /**
   * The rendering radius, used to automatically define with/height of scene and distance of
   * clipping planes.
   */
  protected float renderingSphereRadius;
  /** The distance between the camera eye and the near clipping plane. */
  protected float near;
  /** The distance between the camera eye and the far clipping plane. */
  protected float far;

  /**
   * 
   */
  protected BoundingBox2d renderingSquare;

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
   * Return the projection mode (for 3D or 2D charts), which was defined while calling
   * {@link #setRenderingSphereRadius(float)} for 3D charts, or
   * {@link #setRenderingSquare(BoundingBox2d, float, float)} for 2D charts.
   */
  public ProjectionMode getProjectionMode() {
    return projectionMode;
  }

  /**
   * Set the radius of the sphere that will be visible by the camera (i.e. contained into the
   * rendered view), for a 3D chart. The "far" and "near" clipping planes are modified according to
   * the eye-target distance.
   * 
   * After calling this method, {@link #getProjectionMode()} returns
   * {@link ProjectionMode.Projection3D}.
   */
  public void setRenderingSphereRadius(float radius) {
    this.renderingSphereRadius = radius;
    this.projectionMode = ProjectionMode.Projection3D;

    setNearFarClippingPlanesWithRadius(radius);
  }

  /**
   * Set the boundaries of the model space that should be visible by the camera, for a 2D chart
   * having only X and Y boundaries.
   * 
   * The values describe an area relative to the camera settings (eye, target, up), the actual visible region 
   * of space is then made of the rendering square centered at the eye/target axis.
   * 
   * @see {@link #projectionOrtho(IPainter, ViewportConfiguration)}
   * 
   * After calling this method, {@link #getProjectionMode()} returns
   * {@link ProjectionMode.Projection2D}.
   */
  public void setRenderingSquare(BoundingBox2d renderingSquare, float zNear, float zFar) {
    this.renderingSquare = renderingSquare;
    this.projectionMode = ProjectionMode.Projection2D;
    this.near = zNear;
    this.far = zFar;
  }

  /**
   * Set the boundaries of the model space that should be visible by the camera, for a 2D chart
   * having only X and Y boundaries.
   * 
   * The values describe an area relative to the camera settings (eye, target, up), the actual visible region 
   * of space is then made of the rendering square centered at the eye/target axis.
   * 
   * @see {@link #projectionOrtho(IPainter, ViewportConfiguration)}
   * 
   * After calling this method, {@link #getProjectionMode()} returns
   * {@link ProjectionMode.Projection2D}.
   */
  public void setRenderingSquare(BoundingBox2d renderingSquare) {
    this.renderingSquare = renderingSquare;
    this.projectionMode = ProjectionMode.Projection2D;

    // derive general 3D case
    float radius = Math.max(renderingSquare.xrange(), renderingSquare.yrange()) / 2;

    setNearFarClippingPlanesWithRadius(radius);
  }

  protected void setNearFarClippingPlanesWithRadius(float radius) {
    this.near = (float) eye.distance(target) - radius * 2;
    this.far = (float) eye.distance(target) + radius * 2;
  }


  /**
   * Return the radius of the sphere that will be contained into the rendered 3D view.
   */
  public float getRenderingSphereRadius() {
    return renderingSphereRadius;
  }

  /**
   * Return the rendering (X,Y) square that will be contained into the rendered 2D view.
   */
  public BoundingBox2d getRenderingSquare() {
    return renderingSquare;
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
   * A null coordinate can be returned if the projection could not be performed for some reasons.
   * This may occur if projection or modelview matrices are not invertible or if these matrices
   * where unavailable (hence resulting to zero matrices) while invoking this method. Zero matrices
   * can be avoided by ensuring the GL context is current using {@link IPainter#acquireGL()}
   * 
   * @see {@link IPainter#gluUnProject(float, float, float, float[], int, float[], int, int[], int, float[], int)}
   * @see
   */
  public Coord3d screenToModel(IPainter painter, Coord3d screen) {
    return painter.screenToModel(screen);
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
   * A null coordinate can be returned if the projection could not be performed for some reasons.
   */
  public Coord3d modelToScreen(IPainter painter, Coord3d point) {
    return painter.modelToScreen(point);
  }


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

  /**
   * Apply camera position and orientation and performs projection of the visible volume either in
   * perspective or orthogonal mode.
   * 
   * The orthogonal mode support 2D/3D.
   */
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
   * Perform a perspective projection by processing the field of view based on the
   * {@link #renderingSphereRadius}, {@link #target} and {@link #eye}.
   * 
   * <img src="doc-files/perspective.png"/>
   * 
   * @param painter
   * @param viewport
   * @see {@link #projectionOrtho(IPainter, ViewportConfiguration)}
   */
  public void projectionPerspective(IPainter painter, ViewportConfiguration viewport) {

    // easier perspective processing
    if (perspectiveProjectionUseFrustrum) {
      float r = renderingSphereRadius / (painter.getView().getFactorViewPointDistance());

      painter.glFrustum(-r, r, -r, r, near, far);
    }

    // former perspective processing
    else {
      boolean stretchToFill = ViewportMode.STRETCH_TO_FILL.equals(viewport.getMode());
      double fov = computeFieldOfView(renderingSphereRadius * 4, eye.distance(target));
      float aspect = stretchToFill ? ((float) screenWidth) / ((float) screenHeight) : 1;
      float nearCorrected = near <= 0 ? Float.MIN_VALUE : near;

      painter.gluPerspective(fov / 1, aspect * 0.55, nearCorrected, far);
    }
  }

  protected boolean perspectiveProjectionUseFrustrum = true;



  public void doLookAt(IPainter painter) {
    // System.out.println("Camera.LookAt : " + target + " FROM " + eye);

    painter.gluLookAt(eye.x, eye.y, eye.z, target.x, target.y, target.z, up.x, up.y, up.z);
  }

  /**
   * Perform a orthogonal projection.
   * 
   * The viewable part of the 3d scene is defined by parameters {left, right, bottom, top, near,
   * far} which are processed according to the {@link ViewportMode} and the values of the camera
   * settings ({@link #renderingSphereRadius}, {@link #target} and {@link #eye}, {@link #near} and
   * {@link #far} clipping planes).
   * 
   * <br>
   * <img src="doc-files/orthogonal.png"/>
   * 
   * @param painter
   * @param viewport
   * @see {@link #projectionPerspective(IPainter, ViewportConfiguration)}
   */
  public void projectionOrtho(IPainter painter, ViewportConfiguration viewport) {

    // Case of 3D charts
    if (ProjectionMode.Projection3D.equals(projectionMode)) {
      projectionOrtho3D(viewport);
    }
    // Case of 2D charts
    else if (ProjectionMode.Projection2D.equals(projectionMode)) {
      projectionOrtho2D();
    }
    // Undefined
    else {
      throw new IllegalArgumentException("Unexpected value : " + projectionMode);
    }

    // Apply
    ortho.apply(painter);
  }

  protected void projectionOrtho2D() {
    ortho.update(renderingSquare.xmin(), renderingSquare.xmax(), renderingSquare.ymin(),
        renderingSquare.ymax(), near, far);

    // System.out.println("Camera:" + ortho.toString());
    // System.out.println("Camera:" + up);
    // painter.glOrtho(left, right, bottom, top, near, far);


    // BoundingBox2d b2 = renderingSquare.shift(new Coord2d(target.x, target.y));
    // System.out.println("Camera : 2D capturing at : " + eye);
    // System.out.println("Camera : 2D capturing sq : " + b2);


  }

  protected void projectionOrtho3D(ViewportConfiguration viewport) {
    // Case of a viewport stretched to fill the canvas or of a square viewport
    if (ViewportMode.STRETCH_TO_FILL.equals(viewport.getMode())
        || ViewportMode.SQUARE.equals(viewport.getMode())) {
      ortho.update(-renderingSphereRadius, +renderingSphereRadius, -renderingSphereRadius,
          +renderingSphereRadius, near, far);
    }

    // Case of a rectangle viewport not stretched
    else if (ViewportMode.RECTANGLE_NO_STRETCH.equals(viewport.getMode())) {
      ortho.update(-renderingSphereRadius * viewport.ratio(),
          +renderingSphereRadius * viewport.ratio(), -renderingSphereRadius, +renderingSphereRadius,
          near, far);
    }
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
    output += ("  lookTo = {" + target.x + ", " + target.y + ", " + target.z + "}");
    output += ("  topToward = {" + up.x + ", " + up.y + ", " + up.z + "}");
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
         //System.out.println("Camera.glOrtho("+left+","+ right+","+ bottom+","+ top +","+ near+","+
         //far + ")");
      }

    }

    @Override
    public String toString() {
      return "left:" + left + " right:" + right + " bottom:" + bottom + " top:" + top + " near:"
          + near + " far:" + far;
    }
  }
}
