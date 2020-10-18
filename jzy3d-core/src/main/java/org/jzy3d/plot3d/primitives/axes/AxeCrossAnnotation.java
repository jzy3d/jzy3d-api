package org.jzy3d.plot3d.primitives.axes;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Range;

import com.jogamp.opengl.GL;

public class AxeCrossAnnotation extends AxeLineAnnotation implements AxeAnnotation {
    protected Coord2d value;

    @Override
    public void draw(GL gl, AxeBox axe) {
        Range xrange = axe.getBoxBounds().getXRange();
        Range yrange = axe.getBoxBounds().getYRange();
        if (gl.isGL2()) {
            drawHorizontalLineGL2(gl, xrange, value.y);
            drawVerticalLineGL2(gl, yrange, value.x);
        } else {
            drawHorizontalLineGLES2(xrange, value.y);
            drawVerticalLineGLES2(yrange, value.x);
        }
    }
   
    public synchronized Coord2d getValue() {
        return value;
    }

    public synchronized void setValue(Coord2d value) {
        this.value = value;
    }
}
