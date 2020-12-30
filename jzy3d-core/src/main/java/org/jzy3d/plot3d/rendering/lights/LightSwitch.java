package org.jzy3d.plot3d.rendering.lights;

import org.jzy3d.painters.IPainter;

public class LightSwitch {
	public static void enable(IPainter painter, int lightId){
		painter.glEnable_Light(lightId);
	}
	
	public static void disable(IPainter painter, int lightId){
		painter.glDisable_Light(lightId);
	}
}
