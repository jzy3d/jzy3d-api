package org.jzy3d.painters;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import org.jzy3d.plot3d.rendering.canvas.ICanvas;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
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
		return canvas.getDrawable().getContext();
	}

	@Override
	public void begin(Geometry geometry) {
		// TODO Auto-generated method stub
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
	}

	@Override
	public void culling(boolean status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void lights(boolean status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void polygonOffset(boolean status) {
		// TODO Auto-generated method stub

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
	public void gluDisk(GLUquadric quad, double inner, double outer, int slices, int loops) {
		glu.gluDisk(quad, inner, outer, slices, loops);
	}

	@Override
	public void glutSolidSphere(double radius, int slices, int stacks) {
		glut.glutSolidSphere(radius, slices, stacks);
	}

	@Override
	public void gluCylinder(GLUquadric quad, double base, double top, double height, int slices, int stacks) {
		glu.gluCylinder(quad, base, top, height, slices, stacks);
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
	
	

}
