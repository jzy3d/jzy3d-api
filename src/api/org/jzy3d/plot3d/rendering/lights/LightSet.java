package org.jzy3d.plot3d.rendering.lights;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.jzy3d.maths.Coord3d;


public class LightSet {	
	public LightSet(){
		this.lights = new ArrayList<Light>();
	}
	
	public LightSet(List<Light> lights){
		this.lights = lights;
	}
	
	public void init(GL2 gl){
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
	}
	
	public void apply(GL2 gl, Coord3d scale){
		if(lazyLightInit){
			initLight(gl);
			for(Light light: lights)
				LightSwitch.enable(gl, light.getId());
			lazyLightInit = false;
		}
		for(Light light: lights)
			light.apply(gl, scale);
	}
	
	public void enableLightIfThereAreLights(GL2 gl){
		enable(gl, true);
	}
	
	public void enable(GL2 gl, boolean onlyIfAtLeastOneLight){
		if(onlyIfAtLeastOneLight){
			if( lights.size() > 0 )
            	gl.glEnable(GL2.GL_LIGHTING);
		}
		else
			gl.glEnable(GL2.GL_LIGHTING);
	}
	
	public void disable(GL2 gl){
		gl.glDisable(GL2.GL_LIGHTING);
	}
	
	public Light get(int id){
		return lights.get(id);
	}
	
	public void add(Light light){
		if(lights.size()==0)
			queryLazyLightInit();
		lights.add(light);
	}
	
	public void remove(Light light){
		lights.remove(light);
	}
	
	/***********************************/
	
	protected void queryLazyLightInit(){
		lazyLightInit = true;
	}
	
	//http://www.sjbaker.org/steve/omniv/opengl_lighting.html
	protected void initLight(GL2 gl){
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glEnable(GL2.GL_LIGHTING);
		
		// Light model
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
        //gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL2.GL_TRUE);
		//gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL2.GL_FALSE);
	}
		
	/***********************************/
	
	protected List<Light> lights;
	protected boolean lazyLightInit = false;
}
