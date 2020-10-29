package org.jzy3d.plot3d.rendering.shaders.mandelbrot;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

// equivalent de DrawableTexture
public class TexSurface extends AbstractDrawable{
    protected static float SIZE = 100f;
    public TexSurface(){
        bbox = new BoundingBox3d(0f, SIZE, 0f, SIZE, SIZE-1, SIZE+1);
    }
    
    @Override
    public void draw(Painter painter, GL gl, GLU glu, Camera cam) {
        doTransform(painter, cam);
        // Reset the current matrix to the "identity"
        GL2 gl2 = gl.getGL2();
        gl2.glLoadIdentity();

        // Draw A Quad
        //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        
        gl2.glBegin(GL2.GL_QUADS);
        {
            gl2.glTexCoord2f(0.0f, 0.0f);
            gl2.glVertex3f(0.0f, SIZE, SIZE);
            
            gl2.glTexCoord2f(SIZE, 0.0f);
            gl2.glVertex3f(SIZE, SIZE, SIZE);
            
            gl2.glTexCoord2f(SIZE, SIZE);
            gl2.glVertex3f(SIZE, 0.0f, SIZE);
            
            gl2.glTexCoord2f(0.0f, SIZE);
            gl2.glVertex3f(0.0f, 0.0f, SIZE);
        }
        // Done Drawing The Quad
        gl2.glEnd();

        // Flush all drawing operations to the graphics card
        gl2.glFlush();
    }

    @Override
    public void applyGeometryTransform(Transform transform) {
        //throw new RuntimeException("not implemented");
    }

    @Override
    public void updateBounds() {
        //throw new RuntimeException("not implemented");
    }
}
