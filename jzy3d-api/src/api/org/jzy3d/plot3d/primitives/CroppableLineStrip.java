package org.jzy3d.plot3d.primitives;

import org.jzy3d.maths.BoundingBox3d;

import com.jogamp.opengl.GL;

public class CroppableLineStrip extends LineStrip implements Croppable {
    boolean[] filter; // true = show, false = hide

    @Override
    public void filter(BoundingBox3d bounds) {
        filter = new boolean[points.size()];
        for (int i = 0; i < filter.length; i++)
            filter[i] = bounds.contains(points.get(i).xyz);
    }

    @Override
    public void resetFilter() {
        filter = null;
    }

    @Override
    public void drawLineGL2(GL gl) {
        gl.getGL2().glBegin(GL.GL_LINE_STRIP);

        gl.getGL2().glLineWidth(wfwidth);
        if (filter == null)
            doDrawAllLines(gl);
        else
            doDrawLinesFiltered(gl);
        gl.getGL2().glEnd();
    }

    @Override
    public void drawPointsGL2(GL gl) {
        gl.getGL2().glBegin(GL.GL_POINTS);
        if (filter == null)
            doDrawAllPoints(gl);
        else
            doDrawPointsFiltered(gl);
        gl.getGL2().glEnd();
    }

    private void doDrawAllLines(GL gl) {
        if (wfcolor == null) {
            for (Point p : points) {
                gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
            }
        } else {
            for (Point p : points) {
                gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
                gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
            }
        }
    }

    private void doDrawLinesFiltered(GL gl) {
        for (int i = 0; i < filter.length; i++) {
            if (filter[i]) {
                Point p = points.get(i);
                if (wfcolor == null)
                    gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                else
                    gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
                gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
            }
        }
    }

    private void doDrawAllPoints(GL gl) {
        for (Point p : points) {
            if (wfcolor == null)
                gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
            else
                gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
            gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
        }
    }

    private void doDrawPointsFiltered(GL gl) {
        for (int i = 0; i < filter.length; i++) {
            if (filter[i]) {
                Point p = points.get(i);
                if (wfcolor == null)
                    gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                else
                    gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
                gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
            }
        }
    }

}
