package org.jzy3d.plot3d.rendering.lights;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLES2CompatUtils;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.util.gl2.GLUT;

public class Light {
    public static void resetCounter() {
        lightCount = 0;
    }

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

                gl.getGL2().glTranslatef(position.x * scale.x, position.y * scale.y, position.z * scale.z);

                // Light position representation (cube)
                if (representationDisplayed) {
                    gl.glDisable(GLLightingFunc.GL_LIGHTING);
                    gl.getGL2().glColor3f(0.0f, 1.0f, 1.0f);
                    gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
                    glut.glutSolidCube(representationRadius);
                    gl.glEnable(GLLightingFunc.GL_LIGHTING);
                }

                setGLLight(gl);
            } else {
                // OpenGL ES 2

                GLES2CompatUtils.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
                GLES2CompatUtils.glLoadIdentity();

                GLES2CompatUtils.glTranslatef(position.x * scale.x, position.y * scale.y, position.z * scale.z);

                // // Light position representation (cube)
                if (representationDisplayed) {
                    gl.glDisable(GLLightingFunc.GL_LIGHTING);
                    GLES2CompatUtils.glColor3f(0.0f, 1.0f, 1.0f);
                    GLES2CompatUtils.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
                    glut.glutSolidCube(representationRadius);
                    gl.glEnable(GLLightingFunc.GL_LIGHTING);
                }
                
                setGLESLight(GLLightingFunc.GL_LIGHT0);

            }
        } else
            gl.glDisable(GLLightingFunc.GL_LIGHTING);
    }

    protected void setGLLight(GL gl) {
        // Actual light source setting TODO: check we really need to
        // define @ each rendering
        LightSwitch.enable(gl, lightId);
        switch (lightId) {
        case 0:
            setGLLight(gl, GLLightingFunc.GL_LIGHT0);
            break;
        case 1:
            setGLLight(gl, GLLightingFunc.GL_LIGHT1);
            break;
        case (2):
            setGLLight(gl, GLLightingFunc.GL_LIGHT2);
            break;
        case 3:
            setGLLight(gl, GLLightingFunc.GL_LIGHT3);
            break;
        case 4:
            setGLLight(gl, GLLightingFunc.GL_LIGHT4);
            break;
        case 5:
            setGLLight(gl, GLLightingFunc.GL_LIGHT5);
            break;
        case 6:
            setGLLight(gl, GLLightingFunc.GL_LIGHT6);
            break;
        case 7:
            setGLLight(gl, GLLightingFunc.GL_LIGHT7);
            break;
        }
    }
    
    /**
     * Warning : not tested. LighSwitch to be ported
     */
    protected void setGLESLight() {
        // Actual light source setting TODO: check we really need to
        // define @ each rendering
    //LightSwitch.enable(gl, lightId);
        
        
        switch (lightId) {
        case 0:
            setGLESLight(GLLightingFunc.GL_LIGHT0);
            break;
        case 1:
            setGLESLight(GLLightingFunc.GL_LIGHT1);
            break;
        case (2):
            setGLESLight(GLLightingFunc.GL_LIGHT2);
            break;
        case 3:
            setGLESLight(GLLightingFunc.GL_LIGHT3);
            break;
        case 4:
            setGLESLight(GLLightingFunc.GL_LIGHT4);
            break;
        case 5:
            setGLESLight(GLLightingFunc.GL_LIGHT5);
            break;
        case 6:
            setGLESLight(GLLightingFunc.GL_LIGHT6);
            break;
        case 7:
            setGLESLight(GLLightingFunc.GL_LIGHT7);
            break;
        }
    }

    protected void setGLESLight(int func) {
        GLES2CompatUtils.glLightfv(func, GLLightingFunc.GL_POSITION, positionZero, 0);
        GLES2CompatUtils.glLightfv(func, GLLightingFunc.GL_AMBIENT, ambiantColor.toArray(), 0);
        GLES2CompatUtils.glLightfv(func, GLLightingFunc.GL_DIFFUSE, diffuseColor.toArray(), 0);
        GLES2CompatUtils.glLightfv(func, GLLightingFunc.GL_SPECULAR, specularColor.toArray(), 0);
    }

    protected void setGLLight(GL gl, int func) {
        gl.getGL2().glLightfv(func, GLLightingFunc.GL_POSITION, positionZero, 0);
        gl.getGL2().glLightfv(func, GLLightingFunc.GL_AMBIENT, ambiantColor.toArray(), 0);
        gl.getGL2().glLightfv(func, GLLightingFunc.GL_DIFFUSE, diffuseColor.toArray(), 0);
        gl.getGL2().glLightfv(func, GLLightingFunc.GL_SPECULAR, specularColor.toArray(), 0);
    }

    /** Indicates if a square is drawn to show the light position. */
    public void setRepresentationDisplayed(boolean status) {
        representationDisplayed = status;
    }

    public boolean getRepresentationDisplayed() {
        return representationDisplayed;
    }

    public float getRepresentationRadius() {
        return representationRadius;
    }

    public void setRepresentationRadius(float representationRadius) {
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
    protected float representationRadius = 10;

    protected static GLUT glut = new GLUT();

    protected static int lightCount;
}
