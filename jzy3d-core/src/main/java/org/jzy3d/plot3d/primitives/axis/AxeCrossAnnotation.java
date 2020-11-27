package org.jzy3d.plot3d.primitives.axis;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.Painter;

public class AxeCrossAnnotation extends AxeLineAnnotation implements AxeAnnotation {
    protected Coord2d value;

    @Override
    public void draw(Painter painter, AxisBox axe) {
        Range xrange = axe.getBoxBounds().getXRange();
        Range yrange = axe.getBoxBounds().getYRange();

        drawHorizontalLineGL2(painter, xrange, value.y);
        drawVerticalLine(painter, yrange, value.x);
    }
   
    public synchronized Coord2d getValue() {
        return value;
    }

    public synchronized void setValue(Coord2d value) {
        this.value = value;
    }
}
