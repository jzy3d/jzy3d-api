package org.jzy3d.plot3d.primitives;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

public class Disk extends AbstractWireframeable implements ISingleColorable {

    /** Initialize a Cylinder at the origin. */
    public Disk() {
        super();
        bbox = new BoundingBox3d();
        setPosition(Coord3d.ORIGIN);
        setVolume(0f, 10f);
        setSlicing(15, 15);
        setColor(Color.BLACK);
    }

    /** Initialize a cylinder with the given parameters. */
    public Disk(Coord3d position, float radiusInner, float radiusOuter, int slices, int loops, Color color) {
        super();
        bbox = new BoundingBox3d();
        setPosition(position);
        setVolume(radiusInner, radiusOuter);
        setSlicing(slices, loops);
        setColor(color);
    }

    /* */

    public void draw(GL2 gl, GLU glu, Camera cam) {
        doTransform(gl, glu, cam);
        gl.glTranslatef(x, y, z);

        gl.glLineWidth(wfwidth);

        // Draw
        GLUquadric qobj = glu.gluNewQuadric();

        if (facestatus) {
            if (wfstatus) {
                gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
                gl.glPolygonOffset(1.0f, 1.0f);
            }

            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
            gl.glColor4f(color.r, color.g, color.b, color.a);
            glu.gluDisk(qobj, radiusInner, radiusOuter, slices, loops);

            if (wfstatus)
                gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);

        }
        if (wfstatus) {
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
            gl.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
            glu.gluDisk(qobj, radiusInner, radiusOuter, slices, loops);
        }

        doDrawBounds(gl, glu, cam);
    }

    /* */

    public void setData(Coord3d position, float radiusInner, float radiusOuter, int slices, int loops) {
        setPosition(position);
        setVolume(radiusInner, radiusOuter);
        setSlicing(slices, loops);
    }

    public void setPosition(Coord3d position) {
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;

        updateBounds();
    }

    public void setVolume(float radiusInner, float radiusOuter) {
        if (radiusOuter < radiusInner)
            throw new IllegalArgumentException("inner radius must be smaller than outer radius");

        this.radiusInner = radiusInner;
        this.radiusOuter = radiusOuter;

        updateBounds();
    }

    public void updateBounds() {
        bbox.reset();
        bbox.add(x + radiusOuter, y + radiusOuter, z);
        bbox.add(x - radiusOuter, y - radiusOuter, z);
    }

    public void setSlicing(int verticalWires, int horizontalWires) {
        this.slices = verticalWires;
        this.loops = horizontalWires;
    }

    /* */

    public void setColor(Color color) {
        this.color = color;

        fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
    }

    public Color getColor() {
        return color;
    }
    
    public void applyGeometryTransform(Transform transform){
       Coord3d change = transform.compute(new Coord3d(x,y,z));
       x = change.x;
       y = change.y;
       z = change.z;
       updateBounds();
    }

    /* */

    private float x;
    private float y;
    private float z;

    private int slices;
    private int loops;
    private float radiusInner;
    private float radiusOuter;

    private Color color;
}
