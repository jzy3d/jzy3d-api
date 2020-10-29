package org.jzy3d.plot3d.rendering.lights;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Painter;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;

public class LightSet {
	public LightSet() {
		this.lights = new ArrayList<Light>();
	}

	public LightSet(List<Light> lights) {
		this.lights = lights;
	}

	public void init(Painter painter) {
		painter.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
	}

	public void apply(Painter painter, Coord3d scale) {
		if (lazyLightInit) {
			initLight(painter);
			for (Light light : lights)
				LightSwitch.enable(painter, light.getId());
			lazyLightInit = false;
		}
		for (Light light : lights){
		    light.apply(painter, scale);
		}
	}

	public void enableLightIfThereAreLights(Painter painter) {
		enable(painter, true);
	}

	public void enable(Painter painter, boolean onlyIfAtLeastOneLight) {
		if (onlyIfAtLeastOneLight) {
			if (lights.size() > 0)
				painter.glEnable(GLLightingFunc.GL_LIGHTING);
		} else
			painter.glEnable(GLLightingFunc.GL_LIGHTING);
	}

	public void disable(Painter painter) {
		painter.glDisable(GLLightingFunc.GL_LIGHTING);
	}

	public Light get(int id) {
		return lights.get(id);
	}

	public void add(Light light) {
		if (lights.size() == 0)
			queryLazyLightInit();
		lights.add(light);
	}

	public void remove(Light light) {
		lights.remove(light);
	}

	/***********************************/

	protected void queryLazyLightInit() {
		lazyLightInit = true;
	}

	// http://www.sjbaker.org/steve/omniv/opengl_lighting.html
	protected void initLight(Painter painter) {
		painter.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
		painter.glEnable(GLLightingFunc.GL_LIGHTING);

		// Light model
		painter.glLightModeli(GL2ES1.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
		// gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL2.GL_TRUE);
		// gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL2.GL_FALSE);
	}

	/***********************************/

	protected List<Light> lights;
	protected boolean lazyLightInit = false;
}
