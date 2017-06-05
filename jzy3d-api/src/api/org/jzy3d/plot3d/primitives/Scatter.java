package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Coord3ds;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

/**
 * Experimental 3d object.
 * 
 * @author Martin Pernollet
 * 
 */
public class Scatter extends AbstractDrawable implements ISingleColorable {

    public Scatter() {
        bbox = new BoundingBox3d();
        setWidth(1.0f);
        setColor(Color.BLACK);
    }

    public Scatter(Coord3d[] coordinates) {
        this(coordinates, Color.BLACK);
    }

    public Scatter(Coord3d[] coordinates, Color rgb) {
        this(coordinates, rgb, 1.0f);
    }

    public Scatter(Coord3d[] coordinates, Color rgb, float width) {
        bbox = new BoundingBox3d();
        setData(coordinates);
        setWidth(width);
        setColor(rgb);
    }

    public Scatter(Coord3ds coords) {
        this(coords.coordsArray(), coords.colorsArray());
    }
    
    public Scatter(Coord3d[] coordinates, Color[] colors) {
        this(coordinates, colors, 1.0f);
    }

    public Scatter(Coord3d[] coordinates, Color[] colors, float width) {
        bbox = new BoundingBox3d();
        setData(coordinates);
        setWidth(width);
        setColors(colors);
    }

    public void clear() {
        coordinates = null;
        bbox.reset();
    }

    /* */

    @Override
    public void draw(GL gl, GLU glu, Camera cam) {
        doTransform(gl, glu, cam);

        if (gl.isGL2()) {
            drawGL2(gl);
        } else {
            drawGLES2();
        }

        doDrawBounds(gl, glu, cam);
    }

    public void drawGLES2() {
        GLES2CompatUtils.glPointSize(width);

        GLES2CompatUtils.glBegin(GL.GL_POINTS);
        if (colors == null)
            GLES2CompatUtils.glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
        if (coordinates != null) {
            int k = 0;
            for (Coord3d c : coordinates) {
                if (colors != null) {
                    GLES2CompatUtils.glColor4f(colors[k].r, colors[k].g, colors[k].b, colors[k].a);
                    k++;
                }
                GLES2CompatUtils.glVertex3f(c.x, c.y, c.z);
            }
        }
        GLES2CompatUtils.glEnd();
    }

    public void drawGL2(GL gl) {
        gl.getGL2().glPointSize(width);

        gl.getGL2().glBegin(GL.GL_POINTS);
        if (colors == null)
            gl.getGL2().glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
        if (coordinates != null) {
            int k = 0;
            for (Coord3d c : coordinates) {
                if (colors != null) {
                    gl.getGL2().glColor4f(colors[k].r, colors[k].g, colors[k].b, colors[k].a);
                    k++;
                }
                gl.getGL2().glVertex3f(c.x, c.y, c.z);
            }
        }
        gl.getGL2().glEnd();
    }

    @Override
    public void applyGeometryTransform(Transform transform) {
        for (Coord3d c : coordinates) {
            c.set(transform.compute(c));
        }
        updateBounds();
    }

    /* */

    /**
     * Set the coordinates of the point.
     * 
     * @param xyz
     *            point's coordinates
     */
    public void setData(Coord3d[] coordinates) {
        this.coordinates = coordinates;

        updateBounds();
    }

    @Override
    public void updateBounds() {
        bbox.reset();
        for (Coord3d c : coordinates)
            bbox.add(c);
    }

    public Coord3d[] getData() {
        return coordinates;
    }

    public void setColors(Color[] colors) {
        this.colors = colors;

        fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
    }

    @Override
    public void setColor(Color color) {
        this.rgb = color;

        fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
    }

    @Override
    public Color getColor() {
        return rgb;
    }

    /**
     * Set the width of the point.
     * 
     * @param width
     *            point's width
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**********************************************************************/

    public Color[] colors;
    public Coord3d[] coordinates;
    public Color rgb;
    public float width;
}
