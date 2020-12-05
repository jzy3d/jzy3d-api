package org.jzy3d.plot3d.rendering.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PolygonArray;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.transform.Transform;

/**
 * A {@link Camera} allow to define on the view and target points in a cartesian
 * coordinate system.
 * 
 * The {@link Camera} provides the following services:
 * <ul>
 * <li>allows setting perspective/orthogonal rendering mode through
 * {@link CameraMode}.
 * <li>selects the appropriate clipping planes according to a given target box.
 * <li>ensure the <i>modelview</i> matrix is always available for GL2 calls
 * related to anything else than projection.
 * <li>methods to convert screen coordinates into 3d coordinates and vice-versa
 * </ul>
 * 
 * @author Martin Pernollet
 */
public class Camera extends AbstractViewportManager {
	static Logger LOGGER = Logger.getLogger(Camera.class);

	/** The polar default view point, i.e. Coord3d(Math.PI/3,Math.PI/5,500). */
	public static final Coord3d DEFAULT_VIEW = new Coord3d(Math.PI / 3, Math.PI / 5, 500);

	/**
	 * Defines if camera distance is real, or only squared (squared distance avoid
	 * computing Math.sqrt() and is thus faster). Second mode requires value true
	 */
	public static final boolean DEFAULT_CAMERA_DISTANCE_MODE = true;

	/**
	 * Defines if camera distance is real, or only squared (squared distance avoid
	 * computing Math.sqrt() and is thus faster).
	 */
	protected boolean useSquaredDistance = DEFAULT_CAMERA_DISTANCE_MODE;

	/** The camera viewpoint */
	protected Coord3d eye;
	/** The camera target */
	protected Coord3d target;
	/** The camera up vector */
	protected Coord3d up;

	/**
	 * The rendering radius, used to automatically define with/height of scene and
	 * distance of clipping planes.
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
	 * Set up a Camera looking at target, with a viewpoint standing at
	 * target+(0,0,100). The top of the camera is set up toward the positive Z
	 * direction.
	 */
	public Camera(Coord3d target) {
		initWithTarget(target);
	}

	public void initWithTarget(Coord3d target) {
		setTarget(target);
		setEye(DEFAULT_VIEW.cartesian().add(target));
		setUp(new Coord3d(0, 0, 1));

		setViewPort(1, 1, 0, 1);
		setRenderingDepth(0.5f, 100000f);
		setRenderingSphereRadius(1);
		setViewportMode(ViewportMode.RECTANGLE_NO_STRETCH);
	}

	public Camera() {
		this(Coord3d.ORIGIN.clone());
	}

	/******************************************************************/

	/** Set the eye's position. */
	public void setEye(Coord3d eye) {
		this.eye = eye;
	}

	/** Returns the eye's position. */
	public Coord3d getEye() {
		return eye;
	}

	/** Set the target point of the camera. */
	public void setTarget(Coord3d target) {
		this.target = target;
	}

	/** Returns the target's position that was set at the last call to lookAt(). */
	public Coord3d getTarget() {
		return target;
	}

	/** Set the top of the camera. */
	public void setUp(Coord3d up) {
		this.up = up;
	}

	/** Returns the top of the camera. */
	public Coord3d getUp() {
		return this.up;
	}

	/**
	 * Returns true if the camera is 'looking up', in other word if the eye's Z
	 * value is inferior to the target's Z value.
	 */
	public boolean isTiltUp() {
		return eye.z < target.z;
	}

	/* */

	/**
	 * Set the radius of the sphere that will be contained into the rendered view.
	 * The "far" and "near" clipping planes are modified according to the eye-target
	 * distance.
	 */
	public void setRenderingSphereRadius(float radius) {
		this.radius = radius;
		this.near = (float) eye.distance(target) - radius * 2;
		this.far = (float) eye.distance(target) + radius * 2;
	}

	/**
	 * Return the radius of the sphere that will be contained into the rendered
	 * view.
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

	/** Return true if the given point is on the left of the vector eye->target. */
	public boolean side(Coord3d point) {
		return 0 < ((point.x - target.x) * (eye.y - target.y) - (point.y - target.y) * (eye.x - target.x));
	}

	/** Return last values used to make orthogonal scene rendering. Do not edit. */
	public Ortho getOrtho() {
		return ortho;
	}

	/************************** PROJECT / UNPROJECT ****************************/

	/**
	 * Transform a 2d screen coordinate into a 3d coordinate. The z component of the
	 * screen coordinate indicates a depth value between the near and far clipping
	 * plane of the {@link Camera}.
	 * 
	 * @throws a RuntimeException if an error occured while trying to retrieve model
	 *           coordinates
	 */
	public Coord3d screenToModel(Painter painter, Coord3d screen) {
		int viewport[] = painter.getViewPortAsInt();
		float modelView[] = painter.getModelViewAsFloat();
		float projection[] = painter.getProjectionAsFloat();
		float worldcoord[] = new float[3];// wx, wy, wz;// returned xyz coords

		boolean s = painter.gluUnProject(screen.x, screen.y, screen.z, modelView, 0, projection, 0, viewport, 0,
				worldcoord, 0);
		if (!s)
			failedProjection("Could not retrieve screen coordinates in model.");

		return new Coord3d(worldcoord[0], worldcoord[1], worldcoord[2]);
	}

	/**
	 * Transform a 3d point coordinate into its screen position.
	 * 
	 * This method requires the GL context to be current. If not called inside a
	 * rendering loop, that method may not apply correctly and output {0,0,0}. In
	 * that case and if the chart is based on JOGL (native), one may force the
	 * context to be current using
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
	 * @throws a RuntimeException if an error occured while trying to retrieve model
	 *           coordinates AND if {@link #failOnException} is set to true (default
	 *           is false). In case {@link #failOnException} is false, a DEBUG log
	 *           is sent to the {@link #LOGGER}.
	 */
	public Coord3d modelToScreen(Painter painter, Coord3d point) {
		int viewport[] = painter.getViewPortAsInt();

		float screenCoord[] = new float[3];// wx, wy, wz;// returned xyz coords

		if (!painter.gluProject(point.x, point.y, point.z, painter.getModelViewAsFloat(), 0,
				painter.getProjectionAsFloat(), 0, viewport, 0, screenCoord, 0))
			failedProjection("Could not retrieve model coordinates in screen for " + point);
		return new Coord3d(screenCoord[0], screenCoord[1], screenCoord[2]);
	}

	public Coord3d[] modelToScreen(Painter painter, Coord3d[] points) {
		int viewport[] = painter.getViewPortAsInt();

		float screenCoord[] = new float[3];

		Coord3d[] projection = new Coord3d[points.length];

		for (int i = 0; i < points.length; i++) {
			if (!painter.gluProject(points[i].x, points[i].y, points[i].z, painter.getModelViewAsFloat(), 0,
					painter.getProjectionAsFloat(), 0, viewport, 0, screenCoord, 0))
				failedProjection("Could not retrieve model coordinates in screen for " + points[i]);
			projection[i] = new Coord3d(screenCoord[0], screenCoord[1], screenCoord[2]);
		}
		return projection;
	}

	public Coord3d[][] modelToScreen(Painter painter, Coord3d[][] points) {
		int viewport[] = painter.getViewPortAsInt();

		float screenCoord[] = new float[3];

		Coord3d[][] projection = new Coord3d[points.length][points[0].length];

		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points[i].length; j++) {
				if (!painter.gluProject(points[i][j].x, points[i][j].y, points[i][j].z, painter.getModelViewAsFloat(),
						0, painter.getProjectionAsFloat(), 0, viewport, 0, screenCoord, 0))
					failedProjection("Could not retrieve model coordinates in screen for " + points[i][j]);
				projection[i][j] = new Coord3d(screenCoord[0], screenCoord[1], screenCoord[2]);
			}
		}
		return projection;
	}

	public List<Coord3d> modelToScreen(Painter painter, List<Coord3d> points) {
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

	public ArrayList<ArrayList<Coord3d>> modelToScreen(Painter painter, ArrayList<ArrayList<Coord3d>> polygons) {
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

	public PolygonArray modelToScreen(Painter painter, PolygonArray polygon) {
		int viewport[] = painter.getViewPortAsInt();

		float screenCoord[] = new float[3];

		int len = polygon.length();

		float[] x = new float[len];
		float[] y = new float[len];
		float[] z = new float[len];

		for (int i = 0; i < len; i++) {
			if (!painter.gluProject(polygon.x[i], polygon.y[i], polygon.z[i], painter.getModelViewAsFloat(), 0,
					painter.getProjectionAsFloat(), 0, viewport, 0, screenCoord, 0))
				failedProjection("Could not retrieve model coordinates in screen for point " + i);
			x[i] = screenCoord[0];
			y[i] = screenCoord[1];
			z[i] = screenCoord[2];
		}
		return new PolygonArray(x, y, z);
	}

	public PolygonArray[][] modelToScreen(Painter painter, PolygonArray[][] polygons) {
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
					if (!painter.gluProject(polygon.x[k], polygon.y[k], polygon.z[k], painter.getModelViewAsFloat(), 0,
							painter.getProjectionAsFloat(), 0, viewport, 0, screencoord, 0))
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
		else
			// System.err.println(message);
			Logger.getLogger(Camera.class).debug(message);
	}

	boolean failOnException = false;

	/*******************************************************************/

	public void show(Painter painter, Transform transform, Coord3d scaling) {
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
	 * Sets the projection and the mapping of the 3d model to 2d screen. The
	 * projection must be either Camera.PERSPECTIVE or Camera.ORTHOGONAL. <br>
	 * shoot() finally calls the GL function glLookAt, according to the stored eye,
	 * target, up and scale values. <br>
	 * Note that the Camera set by itselft the MatrixMode to model view at the end
	 * of a shoot(). <br>
	 * 
	 * @param painter    TODO
	 * @param projection the projection mode.
	 * @throws a Runtime Exception if the projection mode is neither
	 *           Camera.PERSPECTIVE nor Camera.ORTHOGONAL.
	 */
	public void shoot(Painter painter, CameraMode projection) {
		shoot(painter, projection, false);
	}

	public void shoot(Painter painter, CameraMode projection, boolean doPushMatrixBeforeShooting) {
		painter.glMatrixMode_Projection();
		if (doPushMatrixBeforeShooting)
			painter.glPushMatrix();
		painter.glLoadIdentity();

		doShoot(painter, projection);
	}

	public void doShoot(Painter painter, CameraMode projection) {
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

	protected void projectionPerspective(Painter painter, ViewportConfiguration viewport) {
		boolean stretchToFill = ViewportMode.STRETCH_TO_FILL.equals(viewport.getMode());
		double fov = computeFieldOfView(radius * 2, eye.distance(target));
		float aspect = stretchToFill ? ((float) screenWidth) / ((float) screenHeight) : 1;
		float nearCorrected = near <= 0 ? 0.000000000000000000000000000000000000001f : near;

		painter.gluPerspective(fov, aspect, nearCorrected, far);
	}

	protected void doLookAt(Painter painter) {
		painter.gluLookAt(eye.x, eye.y, eye.z, target.x, target.y, target.z, up.x, up.y, up.z);
	}

	protected void projectionOrtho(Painter painter, ViewportConfiguration viewport) {
		if (ViewportMode.STRETCH_TO_FILL.equals(viewport.getMode())) {
			ortho.update(-radius, +radius, -radius, +radius, near, far);
		} else if (ViewportMode.RECTANGLE_NO_STRETCH.equals(viewport.getMode())) {
			ortho.update(-radius * viewport.ratio(), +radius * viewport.ratio(), -radius, +radius, near, far);
		} else if (ViewportMode.SQUARE.equals(viewport.getMode())) {
			ortho.update(-radius, +radius, -radius, +radius, near, far);
		}

		ortho.apply(painter);
	}

	/**
	 * Compute the field of View, in order to occupy the entire screen in
	 * PERSPECTIVE mode.
	 */
	protected double computeFieldOfView(double size, double distance) {
		double radtheta;
		double degtheta;
		radtheta = 2.0 * Math.atan2(size / 2.0, distance);
		degtheta = (180.0 * radtheta) / Math.PI;
		return degtheta;
	}

	/**
	 * Return the distance between the camera eye and the given drawable's
	 * barycenter.
	 */
	public double getDistance(Drawable drawable) {
		if (useSquaredDistance)
			return drawable.getBarycentre().distanceSq(getEye());
		else
			return drawable.getBarycentre().distance(getEye());
	}

	/**
	 * Apply scaling before computing distance between the camera eye and the given
	 * drawable's barycenter.
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
	 * Apply scaling before computing distance between the camera eye and the given
	 * coordinate.
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

		public Ortho() {
		}

		public void update(double left, double right, double bottom, double top, double near, double far) {
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
		public void apply(Painter painter) {
			if (left != 0 && right != 0 && bottom != 0 && top != 0 && near != 0 && far != 0) {
				painter.glOrtho(left, right, bottom, top, near, far);
			}
		}

		public String toString() {
			return "left:" + left + " right:" + right + " bottom:" + bottom + " top:" + top + " near:" + near + " far:"
					+ far;
		}
	}
}
