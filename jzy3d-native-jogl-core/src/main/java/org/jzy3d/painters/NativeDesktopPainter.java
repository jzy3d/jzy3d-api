package org.jzy3d.painters;

import java.awt.Font;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;

public class NativeDesktopPainter extends AbstractPainter implements Painter {
	protected GL gl;
	protected GLU glu = new GLU();
	protected GLUT glut = new GLUT();

	public GL getGL() {
		return gl;
	}

	public void setGL(GL gl) {
		this.gl = gl;
	}

	public GLU getGLU() {
		return glu;
	}

	public void setGLU(GLU glu) {
		this.glu = glu;
	}

	public GLUT getGLUT() {
		return glut;
	}

	public void setGLUT(GLUT glut) {
		this.glut = glut;
	}

	/**
	 * Get the current GL context of the canvas and make it current.
	 * 
	 * This is usefull when needing to get a GL instance outside of the context of
	 * the {@link GLEventListener}, e.g. when clicking the frame with mouse.
	 */
	public GL getCurrentGL(ICanvas canvas) {
		getCurrentContext(canvas).makeCurrent();
		return getCurrentContext(canvas).getGL();
	}

	public GLContext getCurrentContext(ICanvas canvas) {
		return ((INativeCanvas)canvas).getDrawable().getContext();
	}
	
	@Override
	public void configureGL(Quality quality) {
		// Activate Depth buffer
        if (quality.isDepthActivated()) {
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glDepthFunc(GL.GL_LEQUAL);
        } else{
        	gl.glDisable(GL.GL_DEPTH_TEST);
        	//gl.glDepthRangef(n, f);
        }
        
        // Blending
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        // on/off is handled by each viewport (camera or image)

        // Activate tranparency
        if (quality.isAlphaActivated()) {
            gl.glEnable(GL2.GL_ALPHA_TEST);
            
            if (quality.isDisableDepthBufferWhenAlpha()){
                // Disable depth test to keeping pixels of
            	// "what's behind a polygon" when drawing with
            	// alpha
                gl.glDisable(GL.GL_DEPTH_TEST); 
            }
        } else {
            gl.glDisable(GL2.GL_ALPHA_TEST);
        }

        // Make smooth colors for polygons (interpolate color between points)
        if (quality.isSmoothColor())
            gl.getGL2().glShadeModel(GLLightingFunc.GL_SMOOTH);
        else
        	gl.getGL2().glShadeModel(GLLightingFunc.GL_FLAT);

        // Make smoothing setting
        if (quality.isSmoothPolygon()) {
            gl.glEnable(GL2.GL_POLYGON_SMOOTH);
            gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
        } else
        	gl.glDisable(GL2.GL_POLYGON_SMOOTH);

        if (quality.isSmoothLine()) {
            gl.glEnable(GL.GL_LINE_SMOOTH);
            gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
        } else
        	gl.glDisable(GL.GL_LINE_SMOOTH);

        if (quality.isSmoothPoint()) {
        	gl.glEnable(GL2ES1.GL_POINT_SMOOTH);
        	gl.glHint(GL2ES1.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
        } else
        	gl.glDisable(GL2ES1.GL_POINT_SMOOTH);
	}

	/************ OPEN GL Interface **************/

	// GL MATRIX

	@Override
	public void glPushMatrix() {
		gl.getGL2().glPushMatrix();
	}

	@Override
	public void glPopMatrix() {
		gl.getGL2().glPopMatrix();
	}

	@Override
	public void glMatrixMode(int mode) {
		gl.getGL2().glMatrixMode(mode);
	}

	@Override
	public void glLoadIdentity() {
		gl.getGL2().glLoadIdentity();
	}

	@Override
	public void glScalef(float x, float y, float z) {
		gl.getGL2().glScalef(x, y, z);
	}

	@Override
	public void glTranslatef(float x, float y, float z) {
		gl.getGL2().glTranslatef(x, y, z);
	}

	@Override
	public void glRotatef(float angle, float x, float y, float z) {
		gl.getGL2().glRotatef(angle, x, y, z);	
	}

	@Override
	public void glEnable(int type) {
		gl.glEnable(type);
	}

	@Override
	public void glDisable(int type) {
		gl.glDisable(type);
	}

	// GL GEOMETRY

	@Override
	public void glPointSize(float width) {
		gl.getGL2().glPointSize(width);
	}

	@Override
	public void glLineWidth(float width) {
		gl.getGL2().glLineWidth(width);
	}

	@Override
	public void glBegin(int type) {
		gl.getGL2().glBegin(type);
	}

	@Override
	public void glColor3f(float r, float g, float b) {
		gl.getGL2().glColor3f(r, b, b);
	}

	@Override
	public void glColor4f(float r, float g, float b, float a) {
		gl.getGL2().glColor4f(r, g, b, a);
	}

	@Override
	public void glVertex3f(float x, float y, float z) {
		gl.getGL2().glVertex3f(x, y, z);
	}

	@Override
	public void glVertex3d(double x, double y, double z) {
		gl.getGL2().glVertex3d(x, y, z);
	}

	@Override
	public void glEnd() {
		gl.getGL2().glEnd();
	}

	@Override
	public void glFrontFace(int mode) {
		gl.glFrontFace(mode);
	}

	@Override
	public void glCullFace(int mode) {
		gl.glCullFace(mode);
	}

	@Override
	public void glPolygonMode(PolygonMode mode, PolygonFill fill) {
		int modeValue = polygonModeValue(mode);
		int fillValue = polygonFillValue(fill);
		
		glPolygonMode(modeValue, fillValue);
	}
	
	protected int polygonModeValue(PolygonMode mode) {
		switch (mode) {
		case FRONT:
			return GL.GL_FRONT;
		case BACK:
			return GL.GL_BACK;
		case FRONT_AND_BACK:
			return GL.GL_FRONT_AND_BACK;
		default:
			throw new IllegalArgumentException("Unsupported mode '" + mode + "'");
		}
	}
	
	protected int polygonFillValue(PolygonFill mode) {
		switch (mode) {
		case FILL:
			return GL2.GL_FILL;
		case LINE:
			return GL2.GL_LINE;
		default:
			throw new IllegalArgumentException("Unsupported mode '" + mode + "'");
		}
	}
	@Override
	public void glPolygonMode(int frontOrBack, int fill) {
		gl.getGL2().glPolygonMode(frontOrBack, fill);
	}

	@Override
	public void glPolygonOffset(float factor, float units) {
		gl.glPolygonOffset(factor, units); // handle stippling
	}

	@Override
	public void glLineStipple(int factor, short pattern) {
		gl.getGL2().glLineStipple(factor, pattern);
	}

	// GL TEXTURE

	@Override
	public void glTexCoord2f(float s, float t) {
		gl.getGL2().glTexCoord2f(s, t);
	}

	@Override
	public void glTexEnvf(int target, int pname, float param) {
		gl.getGL2().glTexEnvf(target, pname, param);
	}

	@Override
	public void glTexEnvi(int target, int pname, int param) {
		gl.getGL2().glTexEnvi(target, pname, param);
	}

	@Override
	public void glRasterPos3f(float x, float y, float z) {
		gl.getGL2().glRasterPos3f(x, y, z);
	}

	@Override
	public void glDrawPixels(int width, int height, int format, int type, Buffer pixels) {
		gl.getGL2().glDrawPixels(width, height, format, type, pixels);
	}

	@Override
	public void glPixelZoom(float xfactor, float yfactor) {
		gl.getGL2().glPixelZoom(xfactor, yfactor);
	}
	
	@Override
	public void glPixelStorei(int pname, int param) {
		gl.glPixelStorei(pname, param);		
	}

	@Override
	public void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove, byte[] bitmap, int bitmap_offset) {
		gl.getGL2().glBitmap(width, height, xorig, yorig, xmove, ymove, bitmap, bitmap_offset);
	}
	
	@Override
	public void glutBitmapString(int font, String string) {
		glut.glutBitmapString(font, string);
	}
	
	/** JGL only. Will throw exception */
	@Override
	public void glutBitmapString(Font axisFont, String label, Coord3d p, Color c) {
		throw new NotImplementedException();
	}

	@Override
	public int glutBitmapLength(int font, String string) {
		return glut.glutBitmapLength(font, string);
	}

	// GL LISTS

	

	

	@Override
	public int glGenLists(int range) {
		return gl.getGL2().glGenLists(range);
	}

	@Override
	public void glNewList(int list, int mode) {
		gl.getGL2().glNewList(list, mode);
	}

	@Override
	public void glEndList() {
		gl.getGL2().glEndList();
	}

	@Override
	public void glCallList(int list) {
		gl.getGL2().glCallList(list);
	}

	@Override
	public boolean glIsList(int list) {
		return gl.getGL2().glIsList(list);
	}

	@Override
	public void glDeleteLists(int list, int range) {
		gl.getGL2().glDeleteLists(list, range);
	}

	// GLU

	@Override
	public void gluDisk(double inner, double outer, int slices, int loops) {
		GLUquadric qobj = glu.gluNewQuadric();
		glu.gluDisk(qobj, inner, outer, slices, loops);
	}

	@Override
	public void glutSolidSphere(double radius, int slices, int stacks) {
		glut.glutSolidSphere(radius, slices, stacks);
	}
	
	@Override
	public void gluSphere(double radius, int slices, int stacks) {
		GLUquadric qobj = glu.gluNewQuadric();
		glu.gluSphere(qobj, radius, slices, stacks);	
	}

	@Override
	public void gluCylinder(double base, double top, double height, int slices, int stacks) {
		GLUquadric qobj = glu.gluNewQuadric();
		glu.gluCylinder(qobj, base, top, height, slices, stacks);
	}
	
	@Override
	public void glutSolidCube(float size) {
        glut.glutSolidCube(size);
	}


	// GL FEEDBACK BUFER

	
	@Override
	public void glFeedbackBuffer(int size, int type, FloatBuffer buffer) {
		gl.getGL2().glFeedbackBuffer(size, type, buffer);
	}

	@Override
	public int glRenderMode(int mode) {
		return gl.getGL2().glRenderMode(mode);
	}

	@Override
	public void glPassThrough(float token) {
		gl.getGL2().glPassThrough(token);
	}

	// GL VIEWPOINT

	@Override
	public void glOrtho(double left, double right, double bottom, double top, double near_val, double far_val) {
		gl.getGL2().glOrtho(left, right, bottom, top, near_val, far_val);
	}

	@Override
	public void gluPerspective(double fovy, double aspect, double zNear, double zFar) {
		glu.gluPerspective(fovy, aspect, zNear, zFar);		
	}

	@Override
	public void gluLookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX,
			float upY, float upZ) {
		glu.gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);		
	}

	@Override
	public void glViewport(int x, int y, int width, int height) {
		gl.glViewport(x, y, width, height);	
	}

	@Override
	public boolean gluUnProject(float winX, float winY, float winZ, float[] model, int model_offset, float[] proj,
			int proj_offset, int[] view, int view_offset, float[] objPos, int objPos_offset) {
		return glu.gluUnProject(winX, winY, winZ, model, model_offset, proj, proj_offset, view, view_offset, objPos, objPos_offset);
	}

	@Override
	public boolean gluProject(float objX, float objY, float objZ, float[] model, int model_offset, float[] proj,
			int proj_offset, int[] view, int view_offset, float[] winPos, int winPos_offset) {
		return glu.gluProject(objX, objY, objZ, model, model_offset, proj, proj_offset, view, view_offset, winPos, winPos_offset);
	}
		
	// GL GET
	
	@Override
	public void glGetIntegerv(int pname, int[] data, int data_offset) {
		gl.glGetIntegerv(pname, data, data_offset);
	}

	@Override
	public void glGetDoublev(int pname, double[] params, int params_offset) {
		gl.getGL2().glGetDoublev(pname, params, params_offset);
	}

	@Override
	public void glGetFloatv(int pname, float[] data, int data_offset) {
		gl.glGetFloatv(pname, data, data_offset);
	}

	@Override
	public void glDepthFunc(int func) {
		gl.glDepthFunc(func);
	}

	@Override
	public void glBlendFunc(int sfactor, int dfactor) {
		gl.glBlendFunc(sfactor, dfactor);		
	}


	@Override
	public void glHint(int target, int mode) {
		gl.glHint(target, mode);
	}

	// GL LIGHTS
	
	
	
	@Override
	public void glShadeModel(int mode) {
		gl.getGL2().glShadeModel(mode);
	}

	@Override
	public void glMaterialfv(int face, int pname, float[] params, int params_offset) {
		gl.getGL2().glMaterialfv(face, pname, params, 0);
	}

	@Override
	public void glNormal3f(float nx, float ny, float nz) {
		gl.getGL2().glNormal3f(nx, ny, nz);		
	}

	@Override
	public void glLightfv(int light, int pname, float[] params, int params_offset) {
        gl.getGL2().glLightfv(light, pname, params, params_offset);
	}

	@Override
	public void glLightModeli(int mode, int value) {
		gl.getGL2().glLightModeli(mode, value);
	}

	// GL OTHER
	
	@Override
	public void glClearColor(float red, float green, float blue, float alpha) {
        gl.glClearColor(red, green, blue, alpha);
	}

	@Override
	public void glClearDepth(double d) {
        gl.glClearDepth(d);
	}

	@Override
	public void glClear(int mask) {
		gl.glClear(mask);
	}

	@Override
	public void glClearColorAndDepthBuffers() {
        glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); 
	}

	
	// GL PICKING
	
	
	@Override
	public void glInitNames() {
		gl.getGL2().glInitNames();
	}

	@Override
	public void glLoadName(int name) {
		gl.getGL2().glLoadName(name);
	}

	@Override
	public void glPushName(int name) {
		gl.getGL2().glPushName(name);
	}

	@Override
	public void glPopName() {
		gl.getGL2().glPopName();
	}

	@Override
	public void glSelectBuffer(int size, IntBuffer buffer) {
		gl.getGL2().glSelectBuffer(size, buffer);
	}

	@Override
	public void gluPickMatrix(double x, double y, double delX, double delY, int[] viewport, int viewport_offset) {
		glu.gluPickMatrix(x, y, delX, delY, viewport, viewport_offset);
	}

	@Override
	public void glFlush() {
		gl.glFlush();
	}
	
	
	
	

	@Override
	public void glEvalCoord2f(float u, float v) {
		gl.getGL2().glEvalCoord2f(u, v);
	}

	@Override
	public void glMap2f(int target, float u1, float u2, int ustride, int uorder, float v1, float v2, int vstride,
			int vorder, FloatBuffer points) {
		gl.getGL2().glMap2f(target, u1, u2, ustride, uorder, v1, v2, vstride, vorder, points);
	}

	
	/* *********************************************************************** */
	
	/* ******************** SHORTCUTS TO GL CONSTANTS ************************ */
	
	/* *********************************************************************** */

	@Override
	public void glEnable_PolygonOffsetFill() {
		glEnable(GL.GL_POLYGON_OFFSET_FILL);
	}

	@Override
	public void glDisable_PolygonOffsetFill() {
		glDisable(GL.GL_POLYGON_OFFSET_FILL);
	}
	
	@Override
	public void glEnable_Blend() {
        glEnable(GL.GL_BLEND);
	}

	@Override
	public void glDisable_Blend() {
        glDisable(GL.GL_BLEND);
	}

	@Override
	public void glMatrixMode_ModelView() {
		glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	}
	
	@Override
	public void glMatrixMode_Projection() {
		glMatrixMode(GLMatrixFunc.GL_PROJECTION);
	}

	@Override
	public void glBegin_Polygon() {
		glBegin(GL2.GL_POLYGON);
	}

	@Override
	public void glBegin_Quad() {
		glBegin(GL2.GL_QUADS);
	}

	@Override
	public void glBegin_Triangle() {
		glBegin(GL.GL_TRIANGLES);
	}

	@Override
	public void glBegin_Point() {
		glBegin(GL.GL_POINTS);	
	}

	@Override
	public void glBegin_LineStrip() {
		glBegin(GL.GL_LINE_STRIP);	
	}

	@Override
	public void glBegin_LineLoop() {
		glBegin(GL.GL_LINE_LOOP);	
	}

	@Override
	public void glBegin_Line() {
		glBegin(GL.GL_LINES);		
	}

	@Override
	public void glEnable_LineStipple() {
		glEnable(GL2.GL_LINE_STIPPLE);	
	}
	
	@Override
	public void glDisable_LineStipple() {
		glDisable(GL2.GL_LINE_STIPPLE);	
	}
	
	

}
