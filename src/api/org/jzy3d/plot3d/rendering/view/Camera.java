package org.jzy3d.plot3d.rendering.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Grid;
import org.jzy3d.maths.PolygonArray;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.transform.Transform;

/**
 * A {@link Camera} allow to define on the view and target points in a
 * cartesian coordinate system.
 * 
 * The {@link Camera} provides the following services:
 * <ul>
 * <li>allows setting perspective/orthogonal rendering mode through
 * {@link CameraMode}.
 * <li>selects the appropriate clipping planes according to a given target box.
 * <li>ensure the <i>modelview</i> matrix is always available for GL2 calls related to
 * anything else than projection.
 * <li>methods to convert screen coordinates into 3d coordinates and vice-versa
 * </ul>
 * 
 * @author Martin Pernollet
 */
public class Camera extends AbstractViewportManager {

    /** The polar default view point, i.e. Coord3d(Math.PI/3,Math.PI/5,500). */
    public static final Coord3d DEFAULT_VIEW = new Coord3d(Math.PI / 3, Math.PI / 5, 500);
    /** Defines if camera distance is real, or only squared (squared distance avoid computing Math.sqrt()
     * and is thus faster). 
     * Second mode requires value true */
    public static final boolean DEFAULT_CAMERA_DISTANCE_MODE = true;
    /**
     * Set up a Camera looking at target, with a viewpoint standing at
     * target+(0,0,100). The top of the camera is set up toward the positive Z
     * direction.
     */
    public Camera(Coord3d target) {
        setTarget(target);
        setEye(DEFAULT_VIEW.cartesian().add(target));
        setUp(new Coord3d(0, 0, 1));

        setViewPort(1, 1, 0, 1);
        setRenderingDepth(0.5f, 100000f);
        setRenderingSphereRadius(1);
        setViewportMode(ViewportMode.RECTANGLE_NO_STRETCH);
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

    /** Returns true if the camera is 'looking up', 
     * in other word if the eye's Z value is inferior 
     * to the target's Z value. */
    public boolean isTiltUp() {
        return eye.z < target.z;
    }

    /* */

    /**
     * Set the radius of the sphere that will be contained into the rendered
     * view. As a side effect, the "far" clipping plane is modified according to
     * the eye-target distance, and the position of the "near" clipping plane.
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
     * Manually set the rendering depth (near and far clipping planes). Note
     * that {@link Camera.setRenderingSphereRadius} modified the "far" clipping
     * plane.
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

    /************************** PROJECT / UNPROJECT ****************************/

    /**
     * Transform a 2d screen coordinate into a 3d coordinate. The z component of
     * the screen coordinate indicates a depth value between the near and far
     * clipping plane of the {@link Camera}.
     * 
     * @throws a
     *             RuntimeException if an error occured while trying to retrieve
     *             model coordinates
     */
    public Coord3d screenToModel(GL2 gl, GLU glu, Coord3d screen) {
        int viewport[] = getViewPortAsInt(gl);
        float worldcoord[] = new float[3];// wx, wy, wz;// returned xyz coords
        float realy = screen.y;// viewport[3] - (int)screen.y - 1;

        boolean s = glu.gluUnProject(screen.x, realy, screen.z, getModelViewAsFloat(gl), 0, getProjectionAsFloat(gl), 0, viewport, 0, worldcoord, 0);
        if (!s)
            failedProjection("Could not retrieve screen coordinates in model.");

        return new Coord3d(worldcoord[0], worldcoord[1], worldcoord[2]);
    }

    /**
     * Transform a 3d point coordinate into its screen position.
     * 
     * @throws a
     *             RuntimeException if an error occured while trying to retrieve
     *             model coordinates
     */
    public Coord3d modelToScreen(GL2 gl, GLU glu, Coord3d point) {
        int viewport[] = getViewPortAsInt(gl);

        float screencoord[] = new float[3];// wx, wy, wz;// returned xyz coords
        if (!glu.gluProject(point.x, point.y, point.z, getModelViewAsFloat(gl), 0, getProjectionAsFloat(gl), 0, viewport, 0, screencoord, 0))
            failedProjection("Could not retrieve model coordinates in screen for " + point);
        return new Coord3d(screencoord[0], screencoord[1], screencoord[2]);
    }

    public Coord3d[] modelToScreen(GL2 gl, GLU glu, Coord3d[] points) {
        int viewport[] = getViewPortAsInt(gl);
        float screencoord[] = new float[3];

        Coord3d[] projection = new Coord3d[points.length];
        for (int i = 0; i < points.length; i++) {
            if (!glu.gluProject(points[i].x, points[i].y, points[i].z, getModelViewAsFloat(gl), 0, getProjectionAsFloat(gl), 0, viewport, 0, screencoord, 0))
                failedProjection("Could not retrieve model coordinates in screen for " + points[i]);
            projection[i] = new Coord3d(screencoord[0], screencoord[1], screencoord[2]);
        }
        return projection;
    }

    public Coord3d[][] modelToScreen(GL2 gl, GLU glu, Coord3d[][] points) {
        int viewport[] = getViewPortAsInt(gl);
        float screencoord[] = new float[3];

        Coord3d[][] projection = new Coord3d[points.length][points[0].length];

        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                if (!glu.gluProject(points[i][j].x, points[i][j].y, points[i][j].z, getModelViewAsFloat(gl), 0, getProjectionAsFloat(gl), 0, viewport, 0, screencoord, 0))
                    failedProjection("Could not retrieve model coordinates in screen for " + points[i][j]);
                projection[i][j] = new Coord3d(screencoord[0], screencoord[1], screencoord[2]);
            }
        }
        return projection;
    }

    public List<Coord3d> modelToScreen(GL2 gl, GLU glu, List<Coord3d> points) {
        int viewport[] = getViewPortAsInt(gl);
        float screencoord[] = new float[3];

        List<Coord3d> projection = new Vector<Coord3d>();
        for (Coord3d point : points) {
            if (!glu.gluProject(point.x, point.y, point.z, getModelViewAsFloat(gl), 0, getProjectionAsFloat(gl), 0, viewport, 0, screencoord, 0))
                failedProjection("Could not retrieve model coordinates in screen for " + point);
            projection.add(new Coord3d(screencoord[0], screencoord[1], screencoord[2]));
        }
        return projection;
    }

    public ArrayList<ArrayList<Coord3d>> modelToScreen(GL2 gl, GLU glu, ArrayList<ArrayList<Coord3d>> polygons) {
        int viewport[] = getViewPortAsInt(gl);
        float screencoord[] = new float[3];

        ArrayList<ArrayList<Coord3d>> projections = new ArrayList<ArrayList<Coord3d>>(polygons.size());
        for (ArrayList<Coord3d> polygon : polygons) {
            ArrayList<Coord3d> projection = new ArrayList<Coord3d>(polygon.size());
            for (Coord3d point : polygon) {
                if (!glu.gluProject(point.x, point.y, point.z, getModelViewAsFloat(gl), 0, getProjectionAsFloat(gl), 0, viewport, 0, screencoord, 0))
                    failedProjection("Could not retrieve model coordinates in screen for " + point);
                projection.add(new Coord3d(screencoord[0], screencoord[1], screencoord[2]));
            }
            projections.add(projection);
        }
        return projections;
    }

    public PolygonArray modelToScreen(GL2 gl, GLU glu, PolygonArray polygon) {
        int viewport[] = getViewPortAsInt(gl);
        float screencoord[] = new float[3];

        int len = polygon.length();

        float[] x = new float[len];
        float[] y = new float[len];
        float[] z = new float[len];

        for (int i = 0; i < len; i++) {
            if (!glu.gluProject(polygon.x[i], polygon.y[i], polygon.z[i], getModelViewAsFloat(gl), 0, getProjectionAsFloat(gl), 0, viewport, 0, screencoord, 0))
                failedProjection("Could not retrieve model coordinates in screen for point " + i);
            x[i] = (float) screencoord[0];
            y[i] = (float) screencoord[1];
            z[i] = (float) screencoord[2];
        }
        return new PolygonArray(x, y, z);
    }

    public Grid modelToScreen(GL2 gl, GLU glu, Grid grid) {
        int viewport[] = getViewPortAsInt(gl);
        double screencoord[] = new double[3];

        int xlen = grid.getX().length;
        int ylen = grid.getY().length;
        float[] x = new float[xlen];
        float[] y = new float[ylen];
        float[][] z = new float[xlen][ylen];

        for (int i = 0; i < xlen; i++) {
            for (int j = 0; j < ylen; j++) {
                // if( ! glu.gluProject( grid.getX()[i], grid.getY()[i],
                // grid.getZ()[i][j], getModelViewAsFloat(gl), 0,
                // getProjectionAsFloat(gl), 0, viewport, 0, screencoord, 0 ) )
                failedProjection("Could not retrieve model coordinates in screen for point " + i);
                x[i] = (float) screencoord[0];
                y[j] = (float) screencoord[1]; // STUPID :)
                z[i][j] = (float) screencoord[2];
            }
        }
        return new Grid(x, y, z);
    }

    public PolygonArray[][] modelToScreen(GL2 gl, GLU glu, PolygonArray[][] polygons) {
        int viewport[] = getViewPortAsInt(gl);
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
                    if (!glu.gluProject(polygon.x[k], polygon.y[k], polygon.z[k], getModelViewAsFloat(gl), 0, getProjectionAsFloat(gl), 0, viewport, 0, screencoord, 0))
                        failedProjection("Could not retrieve model coordinates in screen for point " + k);
                    x[k] = (float) screencoord[0];
                    y[k] = (float) screencoord[1];
                    z[k] = (float) screencoord[2];
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
            System.err.println(message);
    }

    boolean failOnException = false;

    /*******************************************************************/

    protected int[] getViewPortAsInt(GL2 gl) {
        int viewport[] = new int[4];
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        return viewport;
    }

    protected double[] getProjectionAsDouble(GL2 gl) {
        double projection[] = new double[16];
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);
        return projection;
    }

    protected float[] getProjectionAsFloat(GL2 gl) {
        float projection[] = new float[16];
        gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projection, 0);
        return projection;
    }

    protected double[] getModelViewAsDouble(GL2 gl) {
        double modelview[] = new double[16];
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
        return modelview;
    }

    protected float[] getModelViewAsFloat(GL2 gl) {
        float modelview[] = new float[16];
        gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
        return modelview;
    }
    
    public void show(GL2 gl, Transform transform, Coord3d scaling){
        if(transform!=null)
            transform.execute(gl);
        
        
        Coord3d eye = getEye().mul(scaling);
        
        gl.glBegin(GL2.GL_POINTS);
        gl.glPointSize(camWidth);
        gl.glColor4f(camColor.r, camColor.g, camColor.b, camColor.a);
        gl.glVertex3f(eye.x, eye.y, eye.z);
        gl.glEnd();
    }
    
    Color camColor = Color.BLACK;
    int camWidth = 3;
    
    /* */

    /**
     * Sets the projection and the mapping of the 3d model to 2d screen. The
     * projection must be either Camera.PERSPECTIVE or Camera.ORTHOGONAL. <br>
     * shoot() finally calls the GL function glLookAt, according to the stored eye,
     * target, up and scale values. <br>
     * Note that the Camera set by itselft the MatrixMode to model view at the
     * end of a shoot(). <br>
     * 
     * @param gl
     *            GL2 context.
     * @param glu
     *            GLU context.
     * @param projection
     *            the projection mode.
     * @throws a
     *             Runtime Exception if the projection mode is neither
     *             Camera.PERSPECTIVE nor Camera.ORTHOGONAL.
     */
    public void shoot(GL2 gl, GLU glu, CameraMode projection) {
        shoot(gl, glu, projection, false);
    }

    public void shoot(GL2 gl, GLU glu, CameraMode projection, boolean doPushMatrixBeforeShooting) {
        gl.glMatrixMode(GL2.GL_PROJECTION);
        if (doPushMatrixBeforeShooting)
            gl.glPushMatrix();
        gl.glLoadIdentity();
        doShoot(gl, glu, projection);
    }

    public void doShoot(GL2 gl, GLU glu, CameraMode projection) {
        // Set viewport
        ViewportConfiguration viewport = applyViewport(gl, glu);
        
        // Set perspective
        if (projection == CameraMode.PERSPECTIVE) {
            boolean stretchToFill = ViewportMode.STRETCH_TO_FILL.equals(viewport.getMode());
            
            glu.gluPerspective(computeFieldOfView(radius * 2, eye.distance(target)), stretchToFill ? ((float) screenWidth) / ((float) screenHeight) : 1, near, far);
        } else if (projection == CameraMode.ORTHOGONAL) {
            if(ViewportMode.STRETCH_TO_FILL.equals(viewport.getMode()))
                gl.glOrtho(-radius, +radius , -radius, +radius, near, far);
            else if(ViewportMode.RECTANGLE_NO_STRETCH.equals(viewport.getMode()))
                gl.glOrtho(-radius * viewport.ratio(), +radius * viewport.ratio(), -radius, +radius, near, far);
            else if(ViewportMode.SQUARE.equals(viewport.getMode()))
                gl.glOrtho(-radius, +radius , -radius, +radius, near, far);
            
            //gl.glOrtho(-radius * viewport.ratio(), +radius * viewport.ratio(), -radius / viewport.ratio(), +radius/ viewport.ratio(), near, far);
            //gl.glOrtho(-radius / viewport.ratio(), +radius / viewport.ratio(), -radius, +radius, near, far);
            //gl.glOrtho(-radius, +radius, -radius * viewport.ratio(), +radius * viewport.ratio(), near, far);
            
            //gl.glOrtho(-radius, +radius, -radius, +radius, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        } else
            throw new RuntimeException("Camera.shoot(): unknown projection mode '" + projection + "'");

        // Set camera position
        glu.gluLookAt(eye.x, eye.y, eye.z, target.x, target.y, target.z, up.x, up.y, up.z);

        // System.out.println("eye:" + eye);
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
     * Return the distance between the camera eye and the given drawable's barycenter.
     */
    public double getDistance(AbstractDrawable drawable) {
        if(useSquaredDistance)
            return drawable.getBarycentre().distanceSq(getEye());
        else
            return drawable.getBarycentre().distance(getEye());
    }
    
    /**
     * Apply scaling before computing distance between the camera eye and the given drawable's barycenter.
     */
    public double getDistance(AbstractDrawable drawable, Coord3d viewScale) {
        if(useSquaredDistance)
            return drawable.getBarycentre().distanceSq(getEye().div(viewScale));
        else
            return drawable.getBarycentre().distance(getEye().div(viewScale));
    }

    /**
     * Return the distance between the camera eye and the given coordinate.
     */
    public double getDistance(Coord3d coord) {
        if(useSquaredDistance)
            return coord.distanceSq(getEye());
        else
            return coord.distance(getEye());
    }
    
    /**
     * Apply scaling before computing distance between the camera eye and the given coordinate.
     */
    public double getDistance(Coord3d coord, Coord3d viewScale) {
        if(useSquaredDistance)
            return coord.distanceSq(getEye().div(viewScale));
        else
            return coord.distance(getEye().div(viewScale));
    }
    
    public boolean isUseSquaredDistance() {
        return useSquaredDistance;
    }

    /** Defines what getDistance(...) will return, either:
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

    /* */

    protected Coord3d eye;
    protected Coord3d target;
    protected Coord3d up;

    protected float radius;
    protected float near;
    protected float far;
    
    protected boolean useSquaredDistance = DEFAULT_CAMERA_DISTANCE_MODE;
}
