package org.jzy3d.plot3d.primitives.axes;

import org.jzy3d.maths.Range;
import org.jzy3d.painters.Painter;

public class AxeXLineAnnotation extends AxeLineAnnotation implements AxeAnnotation {
    protected float value;

    @Override
    public void draw(Painter painter, AxisBox axe) {
        Range yrange = axe.getBoxBounds().getYRange();

        drawVerticalLine(painter, yrange, value);
    }
   
    public synchronized float getValue() {
        return value;
    }

    public synchronized void setValue(float value) {
        this.value = value;
    }
}
