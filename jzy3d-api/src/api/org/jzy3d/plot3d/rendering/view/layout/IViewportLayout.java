package org.jzy3d.plot3d.rendering.view.layout;

import org.jzy3d.chart.Chart;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

public interface IViewportLayout {
    public void update(Chart chart);
    public void render(GL gl, GLU glu, Chart chart);
}
