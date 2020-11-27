package org.jzy3d.painters;

import java.awt.Font;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.primitives.axis.IAxis;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.LightModel;
import org.jzy3d.plot3d.rendering.lights.MaterialProperty;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/**
 * A {@link Painter} offers methods for drawing and viewing 3d objects.
 * 
 * It draws things by providing a generalization of the OpenGL interface to
 * allow any {@link Drawable} to be painted. The painter methods are named to
 * both match the OpenGL function naming and also match the Jzy3d primitives
 * (Coord3d, Color, etc)
 * 
 * Traditional OpenGL methods mapping
 * <ul>
 * <li>GL.glBegin() -> {@link Painter#glBegin()}
 * <li>GLU.gluLookAt() -> {@link Painter#gluLookAt()}
 * <li>GLUT.glutBitmapString() -> {@link Painter#glutBitmapString()}
 * </ul>
 * 
 * Traditional OpenGL constants mapping with "_"
 * <ul>
 * <li>GL.glEnable(GL.GL_BLEND) -> {@link Painter#glEnable_Blend()}
 * <li>GL.glBegin(GL.GL_POLYGON) -> {@link Painter#glBegin_Polygon()}
 * </ul>
 * 
 * Traditional OpenGL constants mapping with enums, e.g. 
 * <ul>
 * <li>GL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, float[], 0) -> {@link Painter#glMaterial(MaterialProperty, Color, boolean)}
 * </ul>
 * 
 * Jzy3d model mapping
 * <ul>
 * <li>GL.glNormal3f(x,y,z) -> {@link Painter#normal(Coord3d)}
 * <li>GL.glVertex3f(x,y,z) -> {@link Painter#vertex(Coord3d)}
 * <li>GL.glColor4f(r,g,b,a) -> {@link Painter#color(Color)}
 * </ul>
 * 
 * NB : note that jzy3d model mapping comes in addition to the existing OpenGL
 * methods : {@link Painter#color(Color)} and
 * {@link Painter#glColor4f(float, float, float, float)} are both available.
 * 
 * 
 * Implementations of this interface may use native rendering
 * ({@link NativeDeskopPainter}) or rely on pure java emulation of OpenGL1
 * ({@link EmulGLPainter}). They are instanciated by their factories
 * (respectively {@link AWTChartFactory} and {@link EmulGLChartFactory}).
 * 
 * The {@link Painter} is initialized by a {@link View} which provides a
 * {@link Camera}. The {@link Camera} allows drawing things based on the eye
 * position and orientation. of the painter.
 * 
 * Last, there are utility methods for Jzy3d for easily configuring multiple
 * OpenGL settings with {@link Painter#configureGL(Quality)}.
 * 
 */
public interface Painter {
	public Camera getCamera();

	public void setCamera(Camera camera);

	public View getView();

	public IScreenCanvas getCanvas();

	public Scene getScene();

	public IAxis getAxe();

	enum Geometry {
		POINT, LINE, POLYGON
	}

	/** Apply quality settings as OpenGL commands */
	public void configureGL(Quality quality);

	/** A convenient shortcut to glNormal3f */
	public void normal(Coord3d norm);

	/** A convenient shortcut to glVertex3f */
	public void vertex(Coord3d coord);

	/**
	 * A convenient shortcut to glVertex3f, able to apply a space transform in case
	 * it is not null
	 */
	public void vertex(Coord3d coord, SpaceTransformer transform);

	/**
	 * A convenient shortcut to glVertex3f, able to apply a space transform in case
	 * it is not null
	 */
	public void vertex(float x, float y, float z, SpaceTransformer transform);

	/** A convenient shortcut to glColor4f */
	public void color(Color color);

	/**
	 * A convenient shortcut to glColor4f which overrides the color's alpha channel
	 */
	public void colorAlphaOverride(Color color, float alpha);

	/**
	 * A convenient shortcut to glColor4f which multiplies the color's alpha channel
	 * by the given factor
	 */
	public void colorAlphaFactor(Color color, float alphaFactor);

	public void transform(Transform transform, boolean loadIdentity);

	public void raster(Coord3d coord, SpaceTransformer transform);

	public void clearColor(Color color);

	public void material(int face, int pname, Color color);

	public void glMaterial(MaterialProperty material, Color color, boolean b);

	public void glMaterial(MaterialProperty material, float[] value, boolean b);

	// public void begin(Geometry geometry);
	// public void end();
	// public void culling(boolean status);
	// public void lights(boolean status);
	// public void polygonOffset(boolean status);

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

	public void glPolygonMode(PolygonMode mode, PolygonFill fill);

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

	public void glNewList(int list, ListMode compile);

	public void glEndList();

	public void glCallList(int list);

	public boolean glIsList(int list);

	public void glDeleteLists(int list, int range);

	// GL DRAW IMAGES

	public void glDrawPixels(int width, int height, int format, int type, Buffer pixels);

	public void glPixelZoom(float xfactor, float yfactor);

	public void glPixelStorei(int pname, int param);

	public void glPixelStore(PixelStore unpackAlignment, int param);

	public void glRasterPos3f(float x, float y, float z);

	public void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove, byte[] bitmap,
			int bitmap_offset);

	public void glutBitmapString(int font, String string);

	public int glutBitmapLength(int font, String string);

	/** An interface for AWT user, jGL only @since 2.0.0 */
	public void glutBitmapString(Font axisFont, String label, Coord3d p, Color c);

	// GL VIEWPOINT

	public void glOrtho(double left, double right, double bottom, double top, double near_val, double far_val);

	public void gluPerspective(double fovy, double aspect, double zNear, double zFar);

	public void gluLookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX,
			float upY, float upZ);

	public void glViewport(int x, int y, int width, int height);

	public boolean gluUnProject(float winX, float winY, float winZ, float[] model, int model_offset, float[] proj,
			int proj_offset, int[] view, int view_offset, float[] objPos, int objPos_offset);

	public boolean gluProject(float objX, float objY, float objZ, float[] model, int model_offset, float[] proj,
			int proj_offset, int[] view, int view_offset, float[] winPos, int winPos_offset);

	// GLU INTERFACE

	public void gluDisk(double inner, double outer, int slices, int loops);

	public void glutSolidSphere(double radius, int slices, int stacks);

	public void gluSphere(double radius, int slices, int stacks);

	public void gluCylinder(double base, double top, double height, int slices, int stacks);

	public void glutSolidCube(final float size);

	// GL FEEDBACK BUFFER

	public void glFeedbackBuffer(int size, int type, FloatBuffer buffer);

	public int glRenderMode(int mode);

	public int glRenderMode(RenderMode mode);

	public void glPassThrough(float token);

	public void glGetIntegerv(int pname, int[] data, int data_offset);

	public void glGetDoublev(int pname, double[] params, int params_offset);

	public void glGetFloatv(int pname, float[] data, int data_offset);

	// GL LIGHTS

	public void glNormal3f(float nx, float ny, float nz);

	public void glShadeModel(int mode);

	public void glLightfv(int light, int pname, float[] params, int params_offset);

	public void glLightModeli(int mode, int value);

	public void glLightModel(LightModel model, boolean value);

	public void glMaterialfv(int face, int pname, float[] params, int params_offset);

	// OTHER

	public void glHint(int target, int mode);

	public void glClearColor(float red, float green, float blue, float alpha);

	public void glClearDepth(double d);

	public void glClear(int mask);

	public void glClearColorAndDepthBuffers();

	// GL PICKING

	public void glInitNames();

	public void glLoadName(int name);

	public void glPushName(int name);

	public void glPopName();

	public void glSelectBuffer(int size, IntBuffer buffer);

	public void gluPickMatrix(double x, double y, double delX, double delY, int[] viewport, int viewport_offset);

	public void glFlush();

	public void glEvalCoord2f(float u, float v);

	public void glMap2f(int target, float u1, float u2, int ustride, int uorder, float v1, float v2, int vstride,
			int vorder, FloatBuffer points);

	/* *********************************************************************** */

	/* ******************** SHORTCUTS TO GL CONSTANTS ************************ */

	/* *********************************************************************** */

	public void glEnable_Blend();

	public void glDisable_Blend();

	public void glMatrixMode_ModelView();

	public void glMatrixMode_Projection();

	public void glBegin_Polygon();

	public void glBegin_Quad();

	public void glBegin_Triangle();

	public void glBegin_Point();

	public void glBegin_LineStrip();

	public void glBegin_LineLoop();

	public void glBegin_Line();

	public void glEnable_LineStipple();

	public void glDisable_LineStipple();

	public void glEnable_PolygonOffsetFill();

	public void glDisable_PolygonOffsetFill();

	public void glEnable_PolygonOffsetLine();

	public void glDisable_PolygonOffsetLine();

	public void glEnable_CullFace();

	public void glDisable_CullFace();

	public void glFrontFace_ClockWise();

	public void glCullFace_Front();

	public void glDisable_Lighting();

	public void glEnable_Lighting();

	public void glEnable_Light(int light);

	public void glDisable_Light(int light);

	public void glLight_Position(int lightId, float[] positionZero);

	public void glLight_Ambiant(int lightId, Color ambiantColor);

	public void glLight_Diffuse(int lightId, Color diffuseColor);

	public void glLight_Specular(int lightId, Color specularColor);

	public void glEnable_ColorMaterial();

	public void glEnable_PointSmooth();

	public void glHint_PointSmooth_Nicest();

}
