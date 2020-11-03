package org.jzy3d.plot3d.primitives.axes;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.Painter;

import com.jogamp.opengl.GL;

public class AxeCrossAnnotation extends AxeLineAnnotation implements AxeAnnotation {
    protected Coord2d value;

    @Override
    public void draw(Painter painter, GL gl, AxisBox axe) {
        Range xrange = axe.getBoxBounds().getXRange();
        Range yrange = axe.getBoxBounds().getYRange();

        drawHorizontalLineGL2(painter, gl, xrange, value.y);
        drawVerticalLine(painter, gl, yrange, value.x);
    }
   
    public synchronized Coord2d getValue() {
        return value;
    }

    public synchronized void setValue(Coord2d value) {
        this.value = value;
    }
}
