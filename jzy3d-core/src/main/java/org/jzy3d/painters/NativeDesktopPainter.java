package org.jzy3d.painters;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;
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

	@Override
	public void transform(Transform transform, boolean loadIdentity) {
		transform.execute(gl, loadIdentity);
	}

	/************ OPEN GL Interface **************/

	@Override
	public void glLoadIdentity() {
		gl.getGL2().glLoadIdentity();
	}

	@Override
	public void glScalef(float x, float y, float z) {
		gl.getGL2().glScalef(x, y, z);
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
	public void glPolygonMode(int frontOrBack, int fill) {
		gl.getGL2().glPolygonMode(frontOrBack, fill);
	}

	@Override
	public void glColor4f(float r, float g, float b, float a) {
		gl.getGL2().glColor4f(r, g, b, a);
	}

	@Override
	public void glEnable(int type) {
		gl.glEnable(type);
	}

	@Override
	public void glPolygonOffset(float factor, float units) {
		gl.glPolygonOffset(factor, units); // handle stippling

	}

	@Override
	public void glDisable(int type) {
		gl.glDisable(type);
	}

	@Override
	public void glLineStipple(int factor, short pattern) {
		gl.getGL2().glLineStipple(factor, pattern);
	}

	@Override
	public void glPointSize(float width) {
		gl.getGL2().glPointSize(width);
	}

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

	// GL MATRIX
	
	@Override
	public void glPushMatrix() {
		gl.getGL2().glPushMatrix();
	}

	@Override
	public void glPopMatrix() {
		gl.getGL2().glPopMatrix();
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

	
}
