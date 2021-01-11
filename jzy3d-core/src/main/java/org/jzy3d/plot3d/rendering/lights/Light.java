package org.jzy3d.plot3d.rendering.lights;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;

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

    public void apply(IPainter painter, Coord3d scale) {
        if (enabled) {
            painter.glMatrixMode_ModelView();
            painter.glLoadIdentity();
            painter.glTranslatef(position.x * scale.x, position.y * scale.y, position.z * scale.z);

            // Light position representation (cube)
            if (representationDisplayed) {
                painter.glDisable_Lighting();
                painter.glColor3f(0.0f, 1.0f, 1.0f);
                painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.LINE);
                painter.glutSolidCube(representationRadius);
                painter.glEnable_Lighting();
            }

            configureLight(painter);            
        } else {
            painter.glDisable_Lighting();
        }
    }

    protected void configureLight(IPainter painter) {
        // Actual light source setting TODO: check we really need to
        // define @ each rendering
        LightSwitch.enable(painter, lightId);
        
        configureLight(painter, lightId);
    }

    protected void configureLight(IPainter painter, int lightId) {
        painter.glLight_Position(lightId, positionZero);
        painter.glLight_Ambiant(lightId, ambiantColor);
        painter.glLight_Diffuse(lightId, diffuseColor);
        painter.glLight_Specular(lightId, specularColor);
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
