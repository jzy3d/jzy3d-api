package org.jzy3d.plot3d.primitives.axes;

import org.jzy3d.painters.Painter;

import com.jogamp.opengl.GL;

public interface AxeAnnotation {
    public void draw(Painter painter, GL gl, AxisBox axe);
}
