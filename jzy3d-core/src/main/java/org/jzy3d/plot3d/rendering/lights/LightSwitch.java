package org.jzy3d.plot3d.rendering.lights;

import org.jzy3d.painters.Painter;

public class LightSwitch {
	public static void enable(Painter painter, int lightId){
		painter.glEnable_Light(lightId);
	}
	
	public static void disable(Painter painter, int lightId){
		painter.glDisable_Light(lightId);
	}
}
