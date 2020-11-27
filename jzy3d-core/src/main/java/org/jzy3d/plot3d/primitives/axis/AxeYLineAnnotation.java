package org.jzy3d.plot3d.primitives.axis;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.Painter;

public class AxeYLineAnnotation implements AxeAnnotation {
    protected float value;
    protected Color color = Color.RED;
    protected float width = 3;

    @Override
    public void draw(Painter painter, AxisBox axe) {
        Range xrange = axe.getBoxBounds().getXRange();
        Range zrange = axe.getBoxBounds().getYRange();
        
        drawLine(painter, xrange, zrange);
    }

    public synchronized void drawLine(Painter painter, Range yrange, Range zrange) {
        painter.glLineWidth(width);
        painter.glBegin_LineStrip();
        painter.glColor4f(color.r, color.g, color.b, color.a);
        painter.glVertex3f(value, yrange.getMin()-yrange.getRange()/30, zrange.getMin()-2);
        painter.glVertex3f(value, yrange.getMax()+yrange.getRange()/30, zrange.getMin()-2);
        painter.glEnd();
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
