package org.jzy3d.plot3d.rendering.lights;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;

import com.jogamp.opengl.util.gl2.GLUT;

public class Light {
	public Light() {
		this(lightCount++, true);
	}

	public Light(int id) {
		this(id, true);
	}

	public Light(int id, boolean representationDisplayed) {
		this(id, true, true);
	}

	public Light(int id, boolean enabled, boolean representationDisplayed) {
		this.lightId = id;
		this.enabled = enabled;
		this.representationDisplayed = representationDisplayed;

		ambiantColor = Color.WHITE;
		diffuseColor = Color.WHITE;
		specularColor = Color.WHITE;
	}

	public void apply(GL gl, Coord3d scale) {
		if (enabled) {

			if (gl.isGL2()) {
				gl.getGL2().glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
				gl.getGL2().glLoadIdentity();

				gl.getGL2().glTranslatef(position.x * scale.x,
						position.y * scale.y, position.z * scale.z);

				// Light position representation (cube)
				if (representationDisplayed) {
					gl.glDisable(GLLightingFunc.GL_LIGHTING);
					gl.getGL2().glColor3f(0.0f, 1.0f, 1.0f);
					gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK,
							GL2GL3.GL_LINE);
					glut.glutSolidCube(representationRadius);
					gl.glEnable(GLLightingFunc.GL_LIGHTING);
				}

				// Actual light source setting TODO: check we really need to
				// define @ each rendering
				LightSwitch.enable(gl, lightId);
				gl.getGL2().glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION,
						positionZero, 0);
				gl.getGL2().glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT,
						ambiantColor.toArray(), 0);
				gl.getGL2().glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE,
						diffuseColor.toArray(), 0);
				gl.getGL2().glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR,
						specularColor.toArray(), 0);
			} else {
				// OpenGL ES 2
				
				GLES2CompatUtils.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
				GLES2CompatUtils.glLoadIdentity();

				GLES2CompatUtils.glTranslatef(position.x * scale.x,
						position.y * scale.y, position.z * scale.z);

//				// Light position representation (cube)
				if (representationDisplayed) {
					gl.glDisable(GLLightingFunc.GL_LIGHTING);
					GLES2CompatUtils.glColor3f(0.0f, 1.0f, 1.0f);
					GLES2CompatUtils.glPolygonMode(GL.GL_FRONT_AND_BACK,
							GL2GL3.GL_LINE);
					glut.glutSolidCube(representationRadius);
					gl.glEnable(GLLightingFunc.GL_LIGHTING);
				}
//
//				// Actual light source setting TODO: check we really need to
//				// define @ each rendering
				LightSwitch.enable(gl, lightId);
			
				GLES2CompatUtils.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION,
						positionZero, 0);
				GLES2CompatUtils.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT,
						ambiantColor.toArray(), 0);
				GLES2CompatUtils.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE,
						diffuseColor.toArray(), 0);
				GLES2CompatUtils.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR,
						specularColor.toArray(), 0);
				
				
			}
		} else
			gl.glDisable(GLLightingFunc.GL_LIGHTING);
	}

	/** Indicates if a square is drawn to show the light position. */
	public void setRepresentationDisplayed(boolean status) {
		representationDisplayed = status;
	}

	public boolean getRepresentationDisplayed() {
		return representationDisplayed;
	}

	public int getRepresentationRadius() {
		return representationRadius;
	}

	public void setRepresentationRadius(int representationRadius) {
		this.representationRadius = representationRadius;
	}

	public int getId() {
		return lightId;
	}

	public void setPosition(Coord3d position) {
		this.position = position;
	}

	public Coord3d getPosition() {
		return position;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Color getAmbiantColor() {
		return ambiantColor;
	}

	public void setAmbiantColor(Color ambiantColor) {
		this.ambiantColor = ambiantColor;
	}

	public Color getDiffuseColor() {
		return diffuseColor;
	}

	public void setDiffuseColor(Color diffuseColor) {
		this.diffuseColor = diffuseColor;
	}

	public Color getSpecularColor() {
		return specularColor;
	}

	public void setSpecularColor(Color specularColor) {
		this.specularColor = specularColor;
	}

	/* */

	protected int lightId;
	protected boolean enabled;
	protected Color ambiantColor;
	protected Color diffuseColor;
	protected Color specularColor;
	protected Coord3d position;
	protected float positionZero[] = { 0.0f, 0.0f, 0.0f, 1.0f };

	protected boolean representationDisplayed;
	protected int representationRadius = 10;

	protected static GLUT glut = new GLUT();

	protected static int lightCount;
}
