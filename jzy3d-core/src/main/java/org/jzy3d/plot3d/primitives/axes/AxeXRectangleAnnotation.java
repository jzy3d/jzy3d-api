package org.jzy3d.plot3d.primitives.axes;

import org.jzy3d.maths.Range;
import org.jzy3d.painters.Painter;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;

public class AxeXRectangleAnnotation extends AxeXLineAnnotation implements AxeAnnotation {
    public synchronized void drawLine(Painter painter, GL gl, Range yrange, Range zrange) {
        //painter.glLineWidth(width);
        painter.glBegin(GL2.GL_POLYGON);
        painter.glColor4f(color.r, color.g, color.b, color.a);
        
        applyPolygonModeLine(painter, gl);
        applyPolygonModeFill(painter, gl);
        
        float ymin = yrange.getMin()-yrange.getRange()/30;
        float ymax = yrange.getMax()+yrange.getRange()/30;
        float z = zrange.getMin()-2;
        
        painter.glVertex3f(value-width, ymin, z);
        painter.glVertex3f(value-width, ymax, z);
        painter.glVertex3f(value, ymax, z);
        painter.glVertex3f(value, ymin, z);
        
        painter.glEnd();
    }
    
    public enum PolygonMode {
        FRONT, BACK, FRONT_AND_BACK
    }
    PolygonMode polygonMode = PolygonMode.FRONT_AND_BACK;

    protected void applyPolygonModeLine(Painter painter, GL gl) {
        switch (polygonMode) {
        case FRONT:
            painter.glPolygonMode(GL.GL_FRONT, GL2GL3.GL_LINE);
            break;
        case BACK:
            painter.glPolygonMode(GL.GL_BACK, GL2GL3.GL_LINE);
            break;
        case FRONT_AND_BACK:
            painter.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
            break;
        default:
            break;
        }
    }

    protected void applyPolygonModeFill(Painter painter, GL gl) {
        switch (polygonMode) {
        case FRONT:
            painter.glPolygonMode(GL.GL_FRONT, GL2GL3.GL_FILL);
            break;
        case BACK:
            painter.glPolygonMode(GL.GL_BACK, GL2GL3.GL_FILL);
            break;
        case FRONT_AND_BACK:
            painter.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
            break;
        default:
            break;
        }
    }
}
