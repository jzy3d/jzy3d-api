package org.jzy3d.plot3d.rendering.legends.colorbars;

import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.Dimension;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

public interface IColorbarLegend {
    public void render(GL gl, GLU glu);
    public void drawableChanged(DrawableChangedEvent e);
    public Dimension getMinimumSize();
    public void setMinimumSize(Dimension dimension);
}