package org.jzy3d.plot3d.primitives.axes;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.painters.Painter;

import com.jogamp.opengl.GL;

public class AxeLineAnnotation {
    protected Color color = Color.RED;
    protected float width = 3;

    public void drawVerticalLineGLES2(Painter painter, Range yrange, float value) {
        GLES2CompatUtils.glBegin(GL.GL_LINE_STRIP);
        GLES2CompatUtils.glLineWidth(width);
        GLES2CompatUtils.glColor4f(color.r, color.g, color.b, color.a);
        GLES2CompatUtils.glVertex3f(value, yrange.getMin(), 0);
        GLES2CompatUtils.glVertex3f(value, yrange.getMax(), 0);
        GLES2CompatUtils.glEnd();
    }

    public synchronized void drawVerticalLine(Painter painter, GL gl, Range yrange, float value) {
        painter.glLineWidth(width);
        painter.glBegin(GL.GL_LINE_STRIP);
        painter.glColor4f(color.r, color.g, color.b, color.a);
        painter.glVertex3f(value, yrange.getMin()-yrange.getRange()/30, 0);
        painter.glVertex3f(value, yrange.getMax()+yrange.getRange()/30, 0);
        painter.glEnd();
    }
    
    public void drawHorizontalLineGLES2(Range xrange, float value) {
        GLES2CompatUtils.glBegin(GL.GL_LINE_STRIP);
        GLES2CompatUtils.glLineWidth(width);
        GLES2CompatUtils.glColor4f(color.r, color.g, color.b, color.a);
        GLES2CompatUtils.glVertex3f(xrange.getMin(), value, 0);
        GLES2CompatUtils.glVertex3f(xrange.getMax(), value, 0);
        GLES2CompatUtils.glEnd();
    }

    public synchronized void drawHorizontalLineGL2(Painter painter, GL gl, Range xrange, float value) {
        painter.glLineWidth(width);
        painter.glBegin(GL.GL_LINE_STRIP);
        painter.glColor4f(color.r, color.g, color.b, color.a);
        painter.glVertex3f(xrange.getMin()-xrange.getRange()/30, value, 0);
        painter.glVertex3f(xrange.getMax()+xrange.getRange()/30, value, 0);
        painter.glEnd();
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