package org.jzy3d.plot3d.rendering.lights;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;

public class LightSwitch {
	public static void enable(GL gl, int lightId){
		switch(lightId){
			case 0: gl.glEnable(GLLightingFunc.GL_LIGHT0); break;
			case 1: gl.glEnable(GLLightingFunc.GL_LIGHT1); break;
			case 2: gl.glEnable(GLLightingFunc.GL_LIGHT2); break;
			case 3: gl.glEnable(GLLightingFunc.GL_LIGHT3); break;
			case 4: gl.glEnable(GLLightingFunc.GL_LIGHT4); break;
			case 5: gl.glEnable(GLLightingFunc.GL_LIGHT5); break;
			case 6: gl.glEnable(GLLightingFunc.GL_LIGHT6); break;
			case 7: gl.glEnable(GLLightingFunc.GL_LIGHT7); break;
			default: throw new IllegalArgumentException("light id must belong to [0;7]. Having " + lightId);
		}
	}
	
	public static void disable(GL2 gl, int lightId){
		switch(lightId){
			case 0: gl.glDisable(GLLightingFunc.GL_LIGHT0); break;
			case 1: gl.glDisable(GLLightingFunc.GL_LIGHT1); break;
			case 2: gl.glDisable(GLLightingFunc.GL_LIGHT2); break;
			case 3: gl.glDisable(GLLightingFunc.GL_LIGHT3); break;
			case 4: gl.glDisable(GLLightingFunc.GL_LIGHT4); break;
			case 5: gl.glDisable(GLLightingFunc.GL_LIGHT5); break;
			case 6: gl.glDisable(GLLightingFunc.GL_LIGHT6); break;
			case 7: gl.glDisable(GLLightingFunc.GL_LIGHT7); break;
			default: throw new IllegalArgumentException("light id must belong to [0;7]. Having " + lightId);
		}
	}
}
