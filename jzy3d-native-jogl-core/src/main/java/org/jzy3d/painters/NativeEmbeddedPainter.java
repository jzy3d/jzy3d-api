package org.jzy3d.painters;

import java.awt.Font;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.primitives.axes.IAxis;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;

public class NativeEmbeddedPainter extends AbstractPainter implements Painter{
    protected GL gl;
    protected GLU glu = new GLU();
    protected GLUT glut = new GLUT();
    protected Camera camera;

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
    
    @Override
    public View getView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public IScreenCanvas getCanvas() {
        return null;
    }

    @Override
    public Scene getScene() {
        return null;
    }

    @Override
    public IAxis getAxe() {
        return null;
    }

    
    /************ OPEN GL Interface **************/
    
    @Override
    public void glLoadIdentity() {
    	GLES2CompatUtils.glLoadIdentity();
    }
    
	@Override
	public void glMatrixMode(int mode) {
		GLES2CompatUtils.glMatrixMode(mode);	
	}
	
	@Override
	public void glPushMatrix() {
		GLES2CompatUtils.glPushMatrix();
	}
	
	@Override
	public void glPopMatrix() {
		GLES2CompatUtils.glPopMatrix();
	}

	@Override
	public void glScalef(float x, float y, float z) {
		GLES2CompatUtils.glScalef(x, y, z);   
	}
	
	@Override
	public void glTranslatef(float x, float y, float z) {
		GLES2CompatUtils.glTranslatef(x, y, z);
	}
	
	@Override
	public void glRotatef(float angle, float x, float y, float z) {
		GLES2CompatUtils.glRotatef(angle, x, y, z);	
	}

	@Override
	public void glLineWidth(float width) {
		GLES2CompatUtils.glLineWidth(width);
	}

	@Override
	public void glBegin(int type) {
		GLES2CompatUtils.glBegin(type);
	}

	@Override
	public void glColor3f(float r, float g, float b) {
		GLES2CompatUtils.glColor3f(r, g, b); 
	}

	@Override
	public void glVertex3f(float x, float y, float z) {
		GLES2CompatUtils.glVertex3f(x, y, z);
	}
	
	@Override
	public void glVertex3d(double x, double y, double z) {
		throw new NotImplementedException();
	}


	@Override
	public void glEnd() {
		GLES2CompatUtils.glEnd();
	}
	
	@Override
	public void glPolygonMode(int frontOrBack, int fill) {
        GLES2CompatUtils.glPolygonMode(frontOrBack, fill);
	}

	@Override
	public void glColor4f(float r, float g, float b, float a) {
		GLES2CompatUtils.glColor4f(r, g, b, a);
	}

	@Override
	public void glEnable(int type) {
		GLES2CompatUtils.glEnable(type);
	}

	@Override
	public void glPolygonOffset(float factor, float units) {
		GLES2CompatUtils.glPolygonOffset(factor, units); // handle stippling
	}

	@Override
	public void glFrontFace(int mode) {
		throw new NotImplementedException();
		//gl.glFrontFace(mode);		
	}
	
	@Override
	public void glCullFace(int mode) {
		throw new NotImplementedException();
		//GLES2CompatUtils.glCullFace(mode);
	}
	
	@Override
	public void glDisable(int type) {
		GLES2CompatUtils.glDisable(type);		
	}
	
	@Override
	public void glLineStipple(int factor, short pattern) {
		GLES2CompatUtils.glLineStipple(factor, pattern);		
	}
	
	@Override
	public void glPointSize(float width) {
		GLES2CompatUtils.glPointSize(width);
	}
	
	@Override
	public void glTexCoord2f(float s, float t) {
		GLES2CompatUtils.glTexCoord2f(s, t);
	}
	
	@Override
	public void glTexEnvf(int target, int pname, float param) {
		GLES2CompatUtils.glTexEnvi(target, pname, (int)param);
	}

	@Override
	public void glTexEnvi(int target, int pname, int param) {
		GLES2CompatUtils.glTexEnvi(target, pname, param);
	}
	
	@Override
	public void glRasterPos3f(float x, float y, float z) {
		GLES2CompatUtils.glRasterPos3f(x, y, z);
	}
	
	@Override
	public void glDrawPixels(int width, int height, int format, int type, Buffer pixels) {
		GLES2CompatUtils.glDrawPixels(width, height, format, type, pixels);
	}

	@Override
	public void glPixelZoom(float xfactor, float yfactor) {
		GLES2CompatUtils.glPixelZoom(xfactor, yfactor);
	}

	@Override
	public void glPixelStorei(int pname, int param) {
		throw new NotImplementedException();
		//GLES2CompatUtils.glPixelStorei(pname, param);		
	}
	
	@Override
	public void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove, byte[] bitmap,
			int bitmap_offset) {
		GLES2CompatUtils.glBitmap(width, height, xorig, yorig, xmove, ymove, bitmap, bitmap_offset);
	}

	@Override
	public void glutBitmapString(int font, String string) {
		glut.glutBitmapString(font, string);
	}
	
	/** JGL only */
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
		throw new NotImplementedException();
		//GLES2CompatUtils.glGenLists(range);
	}

	@Override
	public void glNewList(int list, int mode) {
		throw new NotImplementedException();
		//GLES2CompatUtils.glNewList(list, mode);
	}

	@Override
	public void glEndList() {
		throw new NotImplementedException();
		//GLES2CompatUtils.glEndList();
	}

	@Override
	public void glCallList(int list) {
		throw new NotImplementedException();
		//gl.getGL2().glCallList(list);
	}
	
	@Override
	public boolean glIsList(int list) {
		throw new NotImplementedException();
		//return gl.getGL2().glIsList(list);
	}

	@Override
	public void glDeleteLists(int list, int range) {
		throw new NotImplementedException();
		//gl.getGL2().glDeleteLists(list, range);		
	}
	
	// GLU / GLUT
	
	@Override
	public void gluDisk(double inner, double outer, int slices, int loops) {
		GLUquadric qobj = glu.gluNewQuadric();
		glu.gluDisk(qobj, inner, outer, slices, loops);		
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
	public void glutSolidSphere(double radius, int slices, int stacks) {
		glut.glutSolidSphere(radius, slices, stacks);
	}
	
	@Override
	public void glutSolidCube(float size) {
        glut.glutSolidCube(size);
	}
	// GL FEEDBACK BUFFER
	
	@Override
	public void glFeedbackBuffer(int size, int type, FloatBuffer buffer) {
		throw new NotImplementedException();
		//GLES2CompatUtils.glFeedbackBuffer(size, type, buffer);
	}

	@Override
	public int glRenderMode(int mode) {
		throw new NotImplementedException();
		//return GLES2CompatUtils.glRenderMode(mode);
	}

	@Override
	public void glPassThrough(float token) {
		GLES2CompatUtils.glPassThrough(token);		
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
		gl.getGL2().glViewport(x, y, width, height);	
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
		throw new NotImplementedException();
		//GLES2CompatUtils.glGetIntegerv(pname, data, data_offset);
	}

	@Override
	public void glGetDoublev(int pname, double[] params, int params_offset) {
		GLES2CompatUtils.glGetDoublev(pname, params, params_offset);
	}

	@Override
	public void glGetFloatv(int pname, float[] data, int data_offset) {
		throw new NotImplementedException();
		//GLES2CompatUtils.glGetFloatv(pname, data, data_offset);
	}
	
	
	@Override
	public void glDepthFunc(int func) {
		throw new NotImplementedException();
		//GLES2CompatUtils.glDepthFunc(func);
	}

	@Override
	public void glBlendFunc(int sfactor, int dfactor) {
		throw new NotImplementedException();
		//GLES2CompatUtils.glBlendFunc(sfactor, dfactor);		
	}

	@Override
	public void glShadeModel(int mode) {
		throw new NotImplementedException();
		//GLES2CompatUtils..glShadeModel(mode);
	}

	@Override
	public void glHint(int target, int mode) {
		throw new NotImplementedException();
		//GLES2CompatUtils.glHint(target, mode);
	}
	
	// GL LIGHTS
	
	@Override
	public void glMaterialfv(int face, int pname, float[] params, int params_offset) {
		GLES2CompatUtils.glMaterialfv(face, pname, params, 0);
	}
	
	@Override
	public void glNormal3f(float nx, float ny, float nz) {
		GLES2CompatUtils.glNormal3f(nx, ny, nz);		
	}

	@Override
	public void glLightfv(int light, int pname, float[] params, int params_offset) {
		GLES2CompatUtils.glLightfv(light, pname, params, params_offset);
	}
	
	@Override
	public void glLightModeli(int mode, int value) {
		throw new NotImplementedException();
		//GLES2CompatUtils.glLightModeli(mode, value);
	}
	
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
		throw new NotImplementedException();
		//gl.getGL2().glInitNames();
	}

	@Override
	public void glLoadName(int name) {
		throw new NotImplementedException();
		//gl.getGL2().glLoadName(name);
	}

	@Override
	public void glPushName(int name) {
		throw new NotImplementedException();
		//gl.getGL2().glPushName(name);
	}

	@Override
	public void glPopName() {
		throw new NotImplementedException();
		//gl.getGL2().glPopName();
	}

	@Override
	public void glSelectBuffer(int size, IntBuffer buffer) {
		throw new NotImplementedException();
		//gl.getGL2().glSelectBuffer(size, buffer);
	}

	@Override
	public void gluPickMatrix(double x, double y, double delX, double delY, int[] viewport, int viewport_offset) {
		throw new NotImplementedException();
		//glu.gluPickMatrix(x, y, delX, delY, viewport, viewport_offset);
	}

	@Override
	public void glFlush() {
		throw new NotImplementedException();
		//gl.glFlush();
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
	public void glEnable_LineStipple() {
		glEnable(GL2.GL_LINE_STIPPLE);	
	}
	
	@Override
	public void glDisable_LineStipple() {
		glDisable(GL2.GL_LINE_STIPPLE);	
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
}
