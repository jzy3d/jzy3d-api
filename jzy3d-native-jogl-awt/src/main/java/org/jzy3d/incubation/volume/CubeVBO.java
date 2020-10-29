package org.jzy3d.incubation.volume;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.plot3d.rendering.view.Camera;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class CubeVBO extends DrawableVBO {
	
	boolean disposed = false;
	
	public CubeVBO(CubeVBOBuilder builder) {
		super(builder);
		this.setGeometry(GL2.GL_QUADS);
		this.setColor( new Color(1f, 0f, 1f, 1f));
		this.doSetBoundingBox(new BoundingBox3d(0, 1, 0, 1, 0, 1));
	}
	
	@Override
    public void draw(Painter painter, GL gl, GLU glu, Camera cam) {
		
		doTransform(painter, cam);
		
		if (!hasMountedOnce) {
			mount(gl);
		}
		
		super.draw(painter, gl, glu, cam);
		
		if (disposed) {
			
			gl.glDeleteBuffers(1, arrayName, 0);
			gl.glDeleteBuffers(1, elementName, 0);
			return;
		}

	}
	
	 @Override
	    public void mount(GL gl) {
	        try {
	            loader.load(gl, this);
	            hasMountedOnce = true;
	        } catch (Exception e) {
	            e.printStackTrace();
//	            Logger.getLogger(DrawableVBO.class).error(e, e);
	        }
	    }
	 
	
	@Override
	public void dispose() {
		disposed = true;
	}
}
