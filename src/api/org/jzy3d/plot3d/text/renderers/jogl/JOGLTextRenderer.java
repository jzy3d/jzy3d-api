package org.jzy3d.plot3d.text.renderers.jogl;

import java.awt.Font;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.AbstractTextRenderer;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;

import com.jogamp.opengl.util.awt.TextRenderer;

public class JOGLTextRenderer extends AbstractTextRenderer implements ITextRenderer {
	public JOGLTextRenderer() {
		this(new DefaultTextStyle(java.awt.Color.BLUE));
	}
	
	public JOGLTextRenderer(ITextStyle style) {
		this.font = new Font("Serif", Font.PLAIN, 72);
		this.renderer = new TextRenderer(font, true, true, style);
		this.style = style;
	}
	
	@Override
	public void drawSimpleText(GL2 gl, GLU glu, Camera cam, String s, Coord3d position, Color color) {
		renderer.begin3DRendering();
		renderer.draw3D(s, position.x, position.y, position.z, 0.01f);
		renderer.flush();
		renderer.end3DRendering();
	}
	
	@Override
	public BoundingBox3d drawText(GL2 gl, GLU glu, Camera cam, String s, Coord3d position, Halign halign, Valign valign, Color color, Coord2d screenOffset, Coord3d sceneOffset) {
		renderer.begin3DRendering();
		
		if(LAYOUT){ // work in progress
			Rectangle2D d = style.getBounds(s, font, renderer.getFontRenderContext());
			//System.out.println(d);
			Coord3d left2d = cam.modelToScreen(gl, glu, position);
			Coord2d right2d = new Coord2d(left2d.x+(float)d.getWidth(), left2d.y+(float)d.getHeight());
			Coord3d right3d = cam.screenToModel(gl, glu, new Coord3d(right2d,0));
			Coord3d offset3d = right3d.sub(position).div(2);//.mul(0.1f);
			Coord3d real = position.add(sceneOffset).sub(offset3d);
			/*System.out.println("real=" + real);
			System.out.println("position=" + position);
			//System.out.println("left3d=" + left2d);
			System.out.println("right3d=" + right3d);
			System.out.println("offset3d=" + offset3d);*/
			renderer.draw3D(s, real.x, real.y, real.z, 0.005f);
		}
		else{
			Coord3d real = position.add(sceneOffset);
			renderer.draw3D(s, real.x, real.y, real.z, 0.005f);
		}
		renderer.flush();
		renderer.end3DRendering();
		
		return null;
	}
	
	protected boolean LAYOUT  = false;	
	
	protected Font font;
	protected TextRenderer.RenderDelegate style;
	protected TextRenderer renderer;
}
