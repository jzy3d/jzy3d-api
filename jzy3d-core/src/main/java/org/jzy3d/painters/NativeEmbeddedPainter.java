package org.jzy3d.painters;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;

public class NativeEmbeddedPainter extends AbstractPainter implements Painter{
    protected GL gl;
    protected GLU glu = new GLU();
    protected GLUT glut = new GLUT();
    protected Camera camera;

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
    public IAxe getAxe() {
        return null;
    }

    
    /* */
    

    @Override
    public void transform(Transform transform, boolean loadIdentity) {
        transform.execute(gl, loadIdentity);        
    }
    
    /************ OPEN GL Interface **************/
    
    @Override
    public void glLoadIdentity() {
    	GLES2CompatUtils.glLoadIdentity();
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
	public void glPushMatrix() {
		GLES2CompatUtils.glPushMatrix();
	}
	
	@Override
	public void glPopMatrix() {
		GLES2CompatUtils.glPopMatrix();
	}
	
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
}
