package org.jzy3d.plot3d.rendering.lights;

import javax.media.opengl.GL2;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;

import com.jogamp.opengl.util.gl2.GLUT;

public class Light {
    public Light(){
        this(lightCount++, true);
    }

    public Light(int id){
		this(id, true);
	}
	
	public Light(int id, boolean representationDisplayed){
		this(id, true, true);
	}
	
	public Light(int id, boolean enabled, boolean representationDisplayed){
		this.lightId = id;
		this.enabled = enabled;
		this.representationDisplayed = representationDisplayed;
		
		ambiantColor = Color.WHITE;
		diffuseColor = Color.WHITE;
		specularColor = Color.WHITE;
	}
	
	public void apply(GL2 gl, Coord3d scale){
		if(enabled){
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
					
			gl.glTranslatef(position.x * scale.x, position.y * scale.y, position.z * scale.z);
			
			// Light position representation (cube)
			if(representationDisplayed){
				gl.glDisable(GL2.GL_LIGHTING);
		        gl.glColor3f(0.0f, 1.0f, 1.0f);						        
		        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
                glut.glutSolidCube(representationRadius);
		        gl.glEnable(GL2.GL_LIGHTING);
			}
	        
	        // Actual light source setting	TODO: check we really need to define @ each rendering	
			LightSwitch.enable(gl, lightId);
	        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, positionZero, 0);
	        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambiantColor.toArray(), 0);
	        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseColor.toArray(), 0);
	        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specularColor.toArray(), 0);
		}
		else
			gl.glDisable(GL2.GL_LIGHTING);
	}
	
	/** Indicates if a square is drawn to show the light position. */
	public void setRepresentationDisplayed(boolean status){
		representationDisplayed = status;
	}
	
	public boolean getRepresentationDisplayed(){
		return representationDisplayed;
	}
	
	public int getRepresentationRadius() {
        return representationRadius;
    }

    public void setRepresentationRadius(int representationRadius) {
        this.representationRadius = representationRadius;
    }

    public int getId(){
		return lightId;
	}

	public void setPosition(Coord3d position){
		this.position = position;
	}
	
	public Coord3d getPosition(){
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
