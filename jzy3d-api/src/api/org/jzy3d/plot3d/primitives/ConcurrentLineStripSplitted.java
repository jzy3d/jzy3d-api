package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.maths.Coord3d;

import com.jogamp.opengl.GL;

public class ConcurrentLineStripSplitted extends ConcurrentLineStrip {
    // List<Integer> idOn = new ArrayList<Integer>();
    List<Integer> idOff = new ArrayList<Integer>();

    public ConcurrentLineStripSplitted() {
        super(100);
    }

    public ConcurrentLineStripSplitted(int n) {
        super(n);
    }

    public ConcurrentLineStripSplitted(List<Coord3d> coords) {
        super(coords);
    }

    public ConcurrentLineStripSplitted(Point c1, Point c2) {
        super(c1, c2);
    }

    @Override
    public void drawLineGL2(GL gl) {
        gl.getGL2().glLineWidth(wfwidth);

        if (wfcolor == null) {
            drawLineSegmentsGL2ByPointColor(gl);
        } else {
            drawLineSegmentsGL2ByWireColor(gl);
        }
    }

    public void drawLineSegmentsGL2ByPointColor(GL gl) {
        int nPt = 0;
        int nOff = 0;
        int nextOff = idOff.size() != 0 ? idOff.get(nOff) : 0;

        begin(gl);

        while (nPt <= points.size() - 1) {
            // point
            Point p = points.get(nPt);
            pointColorSelf(gl, p);

            // consume off
            if (nextOff == nPt) {
                end(gl);
                begin(gl);

                nOff++;
                if (nOff <= idOff.size() - 1)
                    nextOff = idOff.get(nOff);
            }
            nPt++;
        }

        end(gl);
    }

    public void drawLineSegmentsGL2ByWireColor(GL gl) {
        int nPt = 0;
        int nOff = 0;
        int nextOff = -1;
        if (idOff.size() > 0)
            nextOff = idOff.get(nOff);

        begin(gl);

        while (nPt <= points.size() - 1) {
            // point
            Point p = points.get(nPt);
            pointColorWire(gl, p);

            // consume off
            if (nextOff == nPt) {
                end(gl);
                begin(gl);

                nOff++;
                if (nOff != -1 && nOff <= idOff.size() - 1)
                    nextOff = idOff.get(nOff);
            }
            nPt++;
        }

        end(gl);
    }

    public void pointColorWire(GL gl, Point p) {
        gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
        gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
    }

    public void pointColorSelf(GL gl, Point p) {
        gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
        gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
    }

    public void begin(GL gl) {
        gl.getGL2().glBegin(GL.GL_LINE_STRIP);
    }

    public void end(GL gl) {
        gl.getGL2().glEnd();
    }

    /* */

    public void addAndSplit(Point point) {
        synchronized (points) {
            idOff.add(points.size());
            points.add(point);

        }
        bbox.add(point);
    }

    @Override
    public void add(Point point) {
        synchronized (points) {
            points.add(point);
        }
        bbox.add(point);
    }
}
