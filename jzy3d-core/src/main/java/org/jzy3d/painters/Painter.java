package org.jzy3d.painters;

import java.awt.Font;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

import com.jogamp.opengl.glu.GLUquadric;

/** 
 * 1.0 way of drawing : 
 * - isolation of actual engine
 * - no dependency off API on third party
 * - reverse dependency of delaunay
 * 
 * - enrichir IAxe avec toutes les méthodes de axebox
 * 
 * implementation embed
 * - GL
 * - GLU
 * - View
 * - Camera
*/
public interface Painter {
    public Camera getCamera();
    public void setCamera(Camera camera);

    public View getView();
    public IScreenCanvas getCanvas();
    public Scene getScene();
    public IAxe getAxe();

    enum Geometry{
        POINT, LINE, POLYGON
    }
    
	public void configureGL(Quality quality);

    
    public void begin(Geometry geometry);
    public void end();
    public void vertex(Coord3d coord);
    public void vertex(Coord3d coord, SpaceTransformer transform);
    public void vertex(float x, float y, float z, SpaceTransformer transform);

    public void color(Color color);
    public void colorAlphaOverride(Color color, float alpha);
    public void colorAlphaFactor(Color color, float alpha);
    
    public void transform(Transform transform, boolean loadIdentity);
    
    public void raster(Coord3d coord, SpaceTransformer transform);
    

	public void clearColor(Color color);

	public void normal(Coord3d norm);
    

	public void material(int face, int pname, Color color);
	
	public void culling(boolean status);
    public void lights(boolean status);
    public void polygonOffset(boolean status);
    
    
    public int[] getViewPortAsInt();
	public double[] getProjectionAsDouble();
	public float[] getProjectionAsFloat();
	public double[] getModelViewAsDouble();
	public float[] getModelViewAsFloat();
	
    
    
    // ----------------------------
    
    
    // GL INTERFACE
    
    public void glLoadIdentity();
    public void glPushMatrix();
    public void glPopMatrix();
    public void glMatrixMode(int mode);

    public void glScalef(float x, float y, float z);
	public void glTranslatef(float x, float y, float z);
	public void glRotatef(float angle, float x, float y, float z);
	
	// GL CONFIG
	
	public void glDepthFunc(int func);
	public void glBlendFunc(int sfactor, int dfactor);
	
    
	// GL GEOMETRIES
	
    public void glBegin(int type);
	public void glEnd();

	public void glColor3f(float r, float g, float b);
    public void glColor4f(float r, float g, float b, float a);
    public void glVertex3f(float x, float y, float z);
    public void glVertex3d(double x, double y, double z);
	
    public void glEnable(int type);
    public void glDisable(int type);
    
    public void glFrontFace(int mode);
    public void glCullFace(int mode);

    public void glPolygonMode(int frontOrBack, int fill);
    public void glPolygonOffset(float factor, float units);
    
    public void glLineStipple(int factor, short pattern);
    
    public void glLineWidth(float width);
	public void glPointSize(float width);
	
    public void glTexCoord2f(float s, float t);
    public void glTexEnvf(int target, int pname, float param);
    public void glTexEnvi(int target, int pname, int param);

    public int glGenLists(int range);
    public void glNewList(int list, int mode);
	public void glEndList();
	public void glCallList(int list);
	public boolean glIsList(int list);
	public void glDeleteLists(int list, int range);
	
	// GL DRAW IMAGES
	
    public void glDrawPixels(int width, int height, int format, int type, Buffer pixels);
    public void glPixelZoom(float xfactor, float yfactor);
    public void glPixelStorei(int pname, int param);
    public void glRasterPos3f(float x, float y, float z);
    public void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove, byte[] bitmap, int bitmap_offset);
	
    public void glutBitmapString(final int font, final String string);
    
    /** An interface for AWT user, jGL only @since 2.0.0 */
	public void glutBitmapString(Font axisFont, String label, Coord3d p, Color c);

	// GL VIEWPOINT
	
	public void glOrtho(double left, double right, double bottom, double top, double near_val, double far_val);
	public void gluPerspective(double fovy, double aspect, double zNear, double zFar);
	public void gluLookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ);
	public void glViewport(int x, int y, int width, int height);
	
	public boolean gluUnProject(float winX, float winY, float winZ, float[] model, int model_offset, float[] proj, int proj_offset, int[] view, int view_offset, float[] objPos, int objPos_offset);
	public boolean gluProject(float objX, float objY, float objZ, float[] model, int model_offset, float[] proj, int proj_offset, int[] view, int view_offset, float[] winPos, int winPos_offset);

    // GLU INTERFACE

	public void gluDisk(double inner, double outer, int slices, int loops);
	public void glutSolidSphere(double radius, int slices, int stacks);
	public void gluSphere(double radius, int slices, int stacks);
	public void gluCylinder(double base, double top, double height, int slices, int stacks);
	public void glutSolidCube(final float size);
	
	// GL FEEDBACK BUFFER
	
	public void glFeedbackBuffer(int size, int type, FloatBuffer buffer);
	public int glRenderMode(int mode);
	public void glPassThrough(float token);
	
	
	
	
	public void glGetIntegerv(int pname, int[] data, int data_offset);
	public void glGetDoublev(int pname, double[] params, int params_offset);
	public void glGetFloatv(int pname, float[] data, int data_offset);

	
	// GL LIGHTS
	
	public void glNormal3f(float nx, float ny, float nz);
	public void glShadeModel(int mode);
	public void glLightfv(int light, int pname, float[] params, int params_offset);
	public void glLightModeli(int mode, int value);
	public void glMaterialfv(int face, int pname, float[] params, int params_offset);

	// OTHER
	
	public void glHint(int target, int mode);
	public void glClearColor(float red, float green, float blue, float alpha);
	public void glClearDepth(double d);
	public void glClear(int mask);
	
	// GL PICKING
	
	public void glInitNames();
	public void glLoadName(int name);
	public void glPushName(int name);
	public void glPopName();

	public void glSelectBuffer(int size, IntBuffer buffer);
	public void gluPickMatrix(double x, double y, double delX, double delY, int[] viewport, int viewport_offset);
	public void glFlush();
}
