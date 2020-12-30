package org.jzy3d.plot3d.rendering.shaders.mandelbrot;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.transform.Transform;

// equivalent de DrawableTexture
public class TexSurface extends Drawable{
    protected static float SIZE = 100f;
    public TexSurface(){
        bbox = new BoundingBox3d(0f, SIZE, 0f, SIZE, SIZE-1, SIZE+1);
    }
    
    @Override
    public void draw(IPainter painter) {
        doTransform(painter);
        // Reset the current matrix to the "identity"
        //GL2 gl2 = gl.getGL2();
        painter.glLoadIdentity();

        // Draw A Quad
        //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        
        painter.glBegin_Quad();
        {
        	painter.glTexCoord2f(0.0f, 0.0f);
        	painter.glVertex3f(0.0f, SIZE, SIZE);
            
        	painter.glTexCoord2f(SIZE, 0.0f);
        	painter.glVertex3f(SIZE, SIZE, SIZE);
            
        	painter.glTexCoord2f(SIZE, SIZE);
        	painter.glVertex3f(SIZE, 0.0f, SIZE);
            
        	painter.glTexCoord2f(0.0f, SIZE);
        	painter.glVertex3f(0.0f, 0.0f, SIZE);
        }
        // Done Drawing The Quad
        painter.glEnd();

        // Flush all drawing operations to the graphics card
        painter.glFlush();
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
