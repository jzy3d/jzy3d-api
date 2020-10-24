package org.jzy3d.plot3d.primitives;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.Painter;

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
    public void drawLine(Painter painter, GL gl) {
        painter.glBegin(GL.GL_LINE_STRIP);

        painter.glLineWidth(wfwidth);
        if (filter == null)
            doDrawAllLines(painter, gl);
        else
            doDrawLinesFiltered(painter, gl);
        painter.glEnd();
    }

    @Override
    public void drawPoints(Painter painter, GL gl) {
        painter.glBegin(GL.GL_POINTS);
        if (filter == null)
            doDrawAllPoints(painter, gl);
        else
            doDrawPointsFiltered(painter, gl);
        painter.glEnd();
    }

    private void doDrawAllLines(Painter painter, GL gl) {
        if (wfcolor == null) {
            for (Point p : points) {
                painter.color(p.rgb);
            	painter.vertex(p.xyz, spaceTransformer);
            }
        } else {
            for (Point p : points) {
            	painter.color(wfcolor);
            	painter.vertex(p.xyz, spaceTransformer);
            }
        }
    }

    private void doDrawLinesFiltered(Painter painter, GL gl) {
        for (int i = 0; i < filter.length; i++) {
            if (filter[i]) {
                Point p = points.get(i);
                if (wfcolor == null)
                    painter.color(p.rgb);
                else
                	painter.color(wfcolor);
            	painter.vertex(p.xyz, spaceTransformer);
            }
        }
    }

    private void doDrawAllPoints(Painter painter, GL gl) {
        for (Point p : points) {
            if (wfcolor == null)
                painter.color(p.rgb);
            else
            	painter.color(wfcolor);
        	painter.vertex(p.xyz, spaceTransformer);
        }
    }

    private void doDrawPointsFiltered(Painter painter, GL gl) {
        for (int i = 0; i < filter.length; i++) {
            if (filter[i]) {
                Point p = points.get(i);
                if (wfcolor == null)
                    painter.color(p.rgb);
                else
                	painter.color(wfcolor);
            	painter.vertex(p.xyz, spaceTransformer);
            }
        }
    }

}
