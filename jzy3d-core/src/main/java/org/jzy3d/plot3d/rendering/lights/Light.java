package org.jzy3d.plot3d.rendering.lights;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Painter;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;

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

    public void apply(Painter painter, Coord3d scale) {
        if (enabled) {
            painter.glMatrixMode_ModelView();
            painter.glLoadIdentity();
            painter.glTranslatef(position.x * scale.x, position.y * scale.y, position.z * scale.z);

            // Light position representation (cube)
            if (representationDisplayed) {
                painter.glDisable(GLLightingFunc.GL_LIGHTING);
                painter.glColor3f(0.0f, 1.0f, 1.0f);
                painter.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
                painter.glutSolidCube(representationRadius);
                painter.glEnable(GLLightingFunc.GL_LIGHTING);
            }

            configureLight(painter);            
        } else {
            painter.glDisable(GLLightingFunc.GL_LIGHTING);
        }
    }

    protected void configureLight(Painter painter) {
        // Actual light source setting TODO: check we really need to
        // define @ each rendering
        LightSwitch.enable(painter, lightId);
        switch (lightId) {
        case 0:
            configureLight(painter, GLLightingFunc.GL_LIGHT0);
            break;
        case 1:
            configureLight(painter, GLLightingFunc.GL_LIGHT1);
            break;
        case (2):
            configureLight(painter, GLLightingFunc.GL_LIGHT2);
            break;
        case 3:
            configureLight(painter, GLLightingFunc.GL_LIGHT3);
            break;
        case 4:
            configureLight(painter, GLLightingFunc.GL_LIGHT4);
            break;
        case 5:
            configureLight(painter, GLLightingFunc.GL_LIGHT5);
            break;
        case 6:
            configureLight(painter, GLLightingFunc.GL_LIGHT6);
            break;
        case 7:
            configureLight(painter, GLLightingFunc.GL_LIGHT7);
            break;
        }
    }

    protected void configureLight(Painter painter, int lightId) {
        painter.glLightfv(lightId, GLLightingFunc.GL_POSITION, positionZero, 0);
        painter.glLightfv(lightId, GLLightingFunc.GL_AMBIENT, ambiantColor.toArray(), 0);
        painter.glLightfv(lightId, GLLightingFunc.GL_DIFFUSE, diffuseColor.toArray(), 0);
        painter.glLightfv(lightId, GLLightingFunc.GL_SPECULAR, specularColor.toArray(), 0);
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

    protected static int lightCount;
}
