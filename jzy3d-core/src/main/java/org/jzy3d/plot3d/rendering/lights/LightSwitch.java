package org.jzy3d.plot3d.rendering.lights;

import org.jzy3d.painters.Painter;

import com.jogamp.opengl.fixedfunc.GLLightingFunc;

public class LightSwitch {
	public static void enable(Painter painter, int lightId){
		switch(lightId){
			case 0: painter.glEnable(GLLightingFunc.GL_LIGHT0); break;
			case 1: painter.glEnable(GLLightingFunc.GL_LIGHT1); break;
			case 2: painter.glEnable(GLLightingFunc.GL_LIGHT2); break;
			case 3: painter.glEnable(GLLightingFunc.GL_LIGHT3); break;
			case 4: painter.glEnable(GLLightingFunc.GL_LIGHT4); break;
			case 5: painter.glEnable(GLLightingFunc.GL_LIGHT5); break;
			case 6: painter.glEnable(GLLightingFunc.GL_LIGHT6); break;
			case 7: painter.glEnable(GLLightingFunc.GL_LIGHT7); break;
			default: throw new IllegalArgumentException("light id must belong to [0;7]. Having " + lightId);
		}
	}
	
	public static void disable(Painter painter, int lightId){
		switch(lightId){
			case 0: painter.glDisable(GLLightingFunc.GL_LIGHT0); break;
			case 1: painter.glDisable(GLLightingFunc.GL_LIGHT1); break;
			case 2: painter.glDisable(GLLightingFunc.GL_LIGHT2); break;
			case 3: painter.glDisable(GLLightingFunc.GL_LIGHT3); break;
			case 4: painter.glDisable(GLLightingFunc.GL_LIGHT4); break;
			case 5: painter.glDisable(GLLightingFunc.GL_LIGHT5); break;
			case 6: painter.glDisable(GLLightingFunc.GL_LIGHT6); break;
			case 7: painter.glDisable(GLLightingFunc.GL_LIGHT7); break;
			default: throw new IllegalArgumentException("light id must belong to [0;7]. Having " + lightId);
		}
	}
}
