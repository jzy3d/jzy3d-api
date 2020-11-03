package org.jzy3d.plot3d.primitives.enlightables;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.Wireframeable;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;

public abstract class AbstractEnlightable extends Wireframeable {

	protected void applyMaterial(Painter painter) {
		painter.material(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT, materialAmbiantReflection);
		painter.material(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE, materialDiffuseReflection);
		painter.material(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, materialSpecularReflection);
		painter.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SHININESS, materialShininess, 0);
	}

	/******************** LIGHT CONFIG **************************/

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

	public float getMaterialShininess() {
		return materialShininess[0];
	}

	public void setMaterialShininess(float shininess) {
		materialShininess[0] = shininess;
	}

	protected Color materialAmbiantReflection = new Color(1, 1, 1, 1);
	protected Color materialDiffuseReflection = new Color(1, 1, 1, 1);
	protected Color materialSpecularReflection = new Color(1, 1, 1, 1);
	protected Color materialEmission = new Color(1, 1, 1, 1);
	protected float[] materialShininess = new float[1];
}
