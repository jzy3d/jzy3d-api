package org.jzy3d.plot3d.primitives.axes;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.GLES2CompatUtils;

import com.jogamp.opengl.GL;

public class AxeYLineAnnotation implements AxeAnnotation {
    protected float value;
    protected Color color = Color.RED;
    protected float width = 3;

    @Override
    public void draw(GL gl, AxeBox axe) {
        Range xrange = axe.getBoxBounds().getXRange();
        Range zrange = axe.getBoxBounds().getYRange();
        if (gl.isGL2()) {
            drawLineGL2(gl, xrange, zrange);
        } else {
            
            drawLineGLES2(xrange, zrange);
        }

    }

    public void drawLineGLES2(Range yrange, Range zrange) {
        GLES2CompatUtils.glBegin(GL.GL_LINE_STRIP);
        GLES2CompatUtils.glLineWidth(width);
        GLES2CompatUtils.glColor4f(color.r, color.g, color.b, color.a);
        GLES2CompatUtils.glVertex3f(value, yrange.getMin(), 0);
        GLES2CompatUtils.glVertex3f(value, yrange.getMax(), 0);
        GLES2CompatUtils.glEnd();
    }

    public synchronized void drawLineGL2(GL gl, Range yrange, Range zrange) {
        gl.getGL2().glLineWidth(width);
        gl.getGL2().glBegin(GL.GL_LINE_STRIP);
        gl.getGL2().glColor4f(color.r, color.g, color.b, color.a);
        gl.getGL2().glVertex3f(value, yrange.getMin()-yrange.getRange()/30, zrange.getMin()-2);
        gl.getGL2().glVertex3f(value, yrange.getMax()+yrange.getRange()/30, zrange.getMin()-2);
        
        gl.getGL2().glEnd();
    }

    public synchronized float getValue() {
        return value;
    }

    public synchronized void setValue(float value) {
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

}
