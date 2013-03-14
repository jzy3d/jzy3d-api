package org.jzy3d.plot3d.primitives.selectable;

import java.awt.Polygon;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.view.Camera;

/**
 * A Scatter that supports an "highlighted status" to change selected point
 * color
 * 
 * @author Martin Pernollet
 * 
 */
public class SelectableScatter extends Scatter implements ISingleColorable, Selectable {
    public SelectableScatter(Coord3d[] coordinates, Color[] colors) {
        super(coordinates, colors);
    }

    public void draw(GL2 gl, GLU glu, Camera cam) {
        doTransform(gl, glu, cam);

        gl.glPointSize(width);

        gl.glBegin(GL2.GL_POINTS);
        if (colors == null)
            gl.glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
        if (coordinates != null) {
            int k = 0;
            for (Coord3d c : coordinates) {
                if (colors != null) {
                    if (isHighlighted[k]) // Selection coloring goes here
                        gl.glColor4f(highlightColor.r, highlightColor.g, highlightColor.b, highlightColor.a);
                    else
                        gl.glColor4f(colors[k].r, colors[k].g, colors[k].b, colors[k].a);
                    k++;
                }

                gl.glVertex3f(c.x, c.y, c.z);
            }
        }
        gl.glEnd();
    }

    @Override
    public void project(GL2 gl, GLU glu, Camera cam) {
        projection = cam.modelToScreen(gl, glu, getData());
    }

    public Coord3d[] getProjection() {
        return projection;
    }

    public Color getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    public void setHighlighted(int id, boolean value) {
        isHighlighted[id] = value;
    }

    public boolean getHighlighted(int id) {
        return isHighlighted[id];
    }

    public void resetHighlighting() {
        this.isHighlighted = new boolean[coordinates.length];
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
        this.isHighlighted = new boolean[coordinates.length];

        bbox.reset();
        for (Coord3d c : coordinates)
            bbox.add(c);
    }

    public Coord3d[] getData() {
        return coordinates;
    }

    @Override
    public Polygon getHull2d() {
        throw new NotImplementedException();
    }

    @Override
    public List<Coord3d> getLastProjection() {
        throw new NotImplementedException();
    }

    /**********************************************************************/

    protected boolean[] isHighlighted;
    protected Color highlightColor = Color.RED.clone();

    protected Coord3d[] projection;

}
