package org.jzy3d.plot3d.rendering.legends.colorbars;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.Dimension;

public interface IColorbarLegend {

    public abstract void render(GL gl, GLU glu);

    public abstract void drawableChanged(DrawableChangedEvent e);

    public abstract Dimension getMinimumSize();

    public abstract void setMinimumSize(Dimension dimension);

}