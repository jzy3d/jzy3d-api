package org.jzy3d.plot3d.text.overlay;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;


public class SwingTextOverlay extends TextOverlay{
	public SwingTextOverlay(ICanvas canvas){
		super(canvas);
	}
	
	protected void init(ICanvas canvas) {
	    if(canvas instanceof CanvasSwing)
            initComponent((Component)canvas);
        else
            super.init(canvas);
    }
}