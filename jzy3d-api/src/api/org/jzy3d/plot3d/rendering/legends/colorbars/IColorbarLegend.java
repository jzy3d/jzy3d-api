package org.jzy3d.plot3d.rendering.legends.colorbars;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.Dimension;

public interface IColorbarLegend {
    public void render(GL gl, GLU glu);
    public void drawableChanged(DrawableChangedEvent e);
    public Dimension getMinimumSize();
    public void setMinimumSize(Dimension dimension);
}