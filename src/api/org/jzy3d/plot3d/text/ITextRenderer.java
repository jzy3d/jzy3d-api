package org.jzy3d.plot3d.text;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;

public interface ITextRenderer {
	public BoundingBox3d drawText(GL2 gl, GLU glu, Camera cam, String s, Coord3d position, Halign halign, Valign valign, Color color);
	public BoundingBox3d drawText(GL2 gl, GLU glu, Camera cam, String s, Coord3d position, Halign halign, Valign valign, Color color, Coord2d screenOffset, Coord3d sceneOffset);	
	public BoundingBox3d drawText(GL2 gl, GLU glu, Camera cam, String s, Coord3d position, Halign halign, Valign valign, Color color, Coord2d screenOffset);
	public BoundingBox3d drawText(GL2 gl, GLU glu, Camera cam, String s, Coord3d position, Halign halign, Valign valign, Color color, Coord3d sceneOffset);
	
	public void drawSimpleText(GL2 gl, GLU glu, Camera cam, String s, Coord3d position, Color color);
}
