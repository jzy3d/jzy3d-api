package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Painter;

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
    public void drawLine(Painter painter, GL gl) {
        painter.glLineWidth(wfwidth);

        if (wfcolor == null) {
            drawLineSegmentsByPointColor(painter, gl);
        } else {
            drawLineSegmentsByWireColor(painter, gl);
        }
    }

    public void drawLineSegmentsByPointColor(Painter painter, GL gl) {
        int nPt = 0;
        int nOff = 0;
        int nextOff = idOff.size() != 0 ? idOff.get(nOff) : 0;

        painter.glBegin(GL.GL_LINE_STRIP);

        while (nPt <= points.size() - 1) {
            // point
            Point p = points.get(nPt);
            pointColorSelf(painter, p);

            // consume off
            if (nextOff == nPt) {
            	painter.glEnd();
                painter.glBegin(GL.GL_LINE_STRIP);

                nOff++;
                if (nOff <= idOff.size() - 1)
                    nextOff = idOff.get(nOff);
            }
            nPt++;
        }

        painter.glEnd();
    }

    public void drawLineSegmentsByWireColor(Painter painter, GL gl) {
        int nPt = 0;
        int nOff = 0;
        int nextOff = -1;
        if (idOff.size() > 0)
            nextOff = idOff.get(nOff);

        painter.glBegin(GL.GL_LINE_STRIP);

        while (nPt <= points.size() - 1) {
            // point
            Point p = points.get(nPt);
            pointColorWire(painter, p);

            // consume off
            if (nextOff == nPt) {
            	painter.glEnd();
                painter.glBegin(GL.GL_LINE_STRIP);

                nOff++;
                if (nOff != -1 && nOff <= idOff.size() - 1)
                    nextOff = idOff.get(nOff);
            }
            nPt++;
        }
        painter.glEnd();
    }

    public void pointColorWire(Painter painter, Point p) {
        painter.color(wfcolor);
        painter.vertex(p.xyz, spaceTransformer);
    }

    public void pointColorSelf(Painter painter, Point p) {
        painter.color(p.rgb);
        painter.vertex(p.xyz, spaceTransformer);
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
