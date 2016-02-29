package org.jzy3d.plot3d.text;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

public abstract class AbstractTextRenderer implements ITextRenderer{
	public AbstractTextRenderer(){
		defScreenOffset = new Coord2d();
		defSceneOffset = new Coord3d();
	}
	
	@Override
	public BoundingBox3d drawText(GL gl, GLU glu, Camera cam, String s, Coord3d position, Halign halign, Valign valign, Color color){
		return drawText(gl, glu, cam, s, position, halign, valign, color, defScreenOffset, defSceneOffset);
	}	

	@Override
	public BoundingBox3d drawText(GL gl, GLU glu, Camera cam, String s, Coord3d position, Halign halign, Valign valign, Color color, Coord2d screenOffset){
		return drawText(gl, glu, cam, s, position, halign, valign, color, screenOffset, defSceneOffset);
	}	

	@Override
	public BoundingBox3d drawText(GL gl, GLU glu, Camera cam, String s, Coord3d position, Halign halign, Valign valign, Color color, Coord3d sceneOffset){
		return drawText(gl, glu, cam, s, position, halign, valign, color, defScreenOffset, sceneOffset);
	}	
	
	protected Coord2d defScreenOffset;
	protected Coord3d defSceneOffset;

}
