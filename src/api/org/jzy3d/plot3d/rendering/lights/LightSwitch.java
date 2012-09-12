package org.jzy3d.plot3d.rendering.lights;

import javax.media.opengl.GL2;

public class LightSwitch {
	public static void enable(GL2 gl, int lightId){
		switch(lightId){
			case 0: gl.glEnable(GL2.GL_LIGHT0); break;
			case 1: gl.glEnable(GL2.GL_LIGHT1); break;
			case 2: gl.glEnable(GL2.GL_LIGHT2); break;
			case 3: gl.glEnable(GL2.GL_LIGHT3); break;
			case 4: gl.glEnable(GL2.GL_LIGHT4); break;
			case 5: gl.glEnable(GL2.GL_LIGHT5); break;
			case 6: gl.glEnable(GL2.GL_LIGHT6); break;
			case 7: gl.glEnable(GL2.GL_LIGHT7); break;
			default: throw new IllegalArgumentException("light id must belong to [0;7]");
		}
	}
	
	public static void disable(GL2 gl, int lightId){
		switch(lightId){
			case 0: gl.glDisable(GL2.GL_LIGHT0); break;
			case 1: gl.glDisable(GL2.GL_LIGHT1); break;
			case 2: gl.glDisable(GL2.GL_LIGHT2); break;
			case 3: gl.glDisable(GL2.GL_LIGHT3); break;
			case 4: gl.glDisable(GL2.GL_LIGHT4); break;
			case 5: gl.glDisable(GL2.GL_LIGHT5); break;
			case 6: gl.glDisable(GL2.GL_LIGHT6); break;
			case 7: gl.glDisable(GL2.GL_LIGHT7); break;
			default: throw new IllegalArgumentException("light id must belong to [0;7]");
		}
	}
}
