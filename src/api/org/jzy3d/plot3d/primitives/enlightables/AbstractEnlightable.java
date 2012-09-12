package org.jzy3d.plot3d.primitives.enlightables;

import javax.media.opengl.GL2;

import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.AbstractWireframeable;



public abstract class AbstractEnlightable extends AbstractWireframeable {
	
	
	protected void applyMaterial(GL2 gl){
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, materialAmbiantReflection.toArray(), 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, materialDiffuseReflection.toArray(), 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, materialSpecularReflection.toArray(), 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, materialShininess, 0);
        //gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, materialEmission.toArray(), 0);
	}

	/******************** LIGHT CONFIG **************************/
	
	/*public void setEnlightedBy(Light light, boolean status){
		
	}
	
	public boolean isEnlightedBy(Light light){
		return true;
	}*/
	
	public Color getMaterialAmbiantReflection() {
		return materialAmbiantReflection;
	}

	public void setMaterialAmbiantReflection(Color materialAmbiantReflection) {
		this.materialAmbiantReflection = materialAmbiantReflection;
	}

	public Color getMaterialDiffuseReflection() {
		return materialDiffuseReflection;
	}

	public void setMaterialDiffuseReflection(Color materialDiffuseReflection) {
		this.materialDiffuseReflection = materialDiffuseReflection;
	}

	public Color getMaterialSpecularReflection() {
		return materialSpecularReflection;
	}

	public void setMaterialSpecularReflection(Color materialSpecularReflection) {
		this.materialSpecularReflection = materialSpecularReflection;
	}

	public Color getMaterialEmission() {
		return materialEmission;
	}

	public void setMaterialEmission(Color materialEmission) {
		this.materialEmission = materialEmission;
	}
		
	public float getMaterialShininess(){
		return materialShininess[0];
	}	

	public void setMaterialShininess(float shininess){
		materialShininess[0] = shininess;
	}
	
	protected Color materialAmbiantReflection = new Color(1,1,1,1);
	protected Color materialDiffuseReflection = new Color(1,1,1,1);
	protected Color materialSpecularReflection = new Color(1,1,1,1);
	protected Color materialEmission = new Color(1,1,1,1);
	protected float[] materialShininess = new float[1];
}
