package org.jzy3d.plot3d.primitives.axes;

import org.jzy3d.maths.Range;

import com.jogamp.opengl.GL;

public class AxeXLineAnnotation extends AxeLineAnnotation implements AxeAnnotation {
    protected float value;

    @Override
    public void draw(GL gl, AxeBox axe) {
        Range yrange = axe.getBoxBounds().getYRange();
        if (gl.isGL2()) {
            drawVerticalLineGL2(gl, yrange, value);
        } else {
            
            drawVerticalLineGLES2(yrange, value);
        }
    }
   
    public synchronized float getValue() {
        return value;
    }

    public synchronized void setValue(float value) {
        this.value = value;
    }
}
