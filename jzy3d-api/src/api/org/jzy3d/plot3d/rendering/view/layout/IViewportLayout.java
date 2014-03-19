package org.jzy3d.plot3d.rendering.view.layout;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.jzy3d.chart.Chart;

public interface IViewportLayout {
    public void update(Chart chart);
    public void render(GL gl, GLU glu, Chart chart);
}
