package org.jzy3d.plot3d.primitives.graphs.impl;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.graphs.AbstractDrawableGraph2d;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

public class DefaultDrawableGraph2d<V, E> extends AbstractDrawableGraph2d<V, E> {
	public DefaultDrawableGraph2d() {
		super();
		bbox = new BoundingBox3d();
		labelScreenOffset = new Coord2d(0, 0);
		labelSceneOffset = new Coord3d(0, 0, 0);
	}

	/*******************************************************/

	@Override
    protected void drawVertices(Painter painter, GL gl, GLU glu, Camera cam) {
		if (gl.isGL2()) {
			painter.glPointSize(formatter.getVertexWidth());
			painter.glBegin(GL.GL_POINTS);
			for (V v : graph.getVertices()) {
				if (highlights.get(v))
					drawVertexNode(painter, gl, glu, cam, v,
							layout.get(v), formatter.getHighlightedVertexColor());
				else
					drawVertexNode(painter, gl, glu, cam, v,
							layout.get(v), formatter.getVertexColor());
			}
			painter.glEnd();
		} else {
			GLES2CompatUtils.glPointSize(formatter.getVertexWidth());
			GLES2CompatUtils.glBegin(GL.GL_POINTS);
			for (V v : graph.getVertices()) {
				if (highlights.get(v))
					drawVertexNode(painter, gl, glu, cam, v,
							layout.get(v), formatter.getHighlightedVertexColor());
				else
					drawVertexNode(painter, gl, glu, cam, v,
							layout.get(v), formatter.getVertexColor());
			}
			GLES2CompatUtils.glEnd();
		}
	}

	@Override
    protected void drawVertexLabels(Painter painter, GL gl, GLU glu, Camera cam) {
		for (V v : graph.getVertices()) {
			if (highlights.get(v))
				drawVertexLabel(painter, gl, glu, cam, v,
						layout.get(v), formatter.getHighlightedVertexColor());
			else
				drawVertexLabel(painter, gl, glu, cam, v,
						layout.get(v), formatter.getVertexLabelColor());
		}
	}

	@Override
    protected void drawEdges(Painter painter, GL gl, GLU glu, Camera cam) {
		for (E e : graph.getEdges()) {
			V v1 = graph.getEdgeStartVertex(e);
			V v2 = graph.getEdgeStopVertex(e);
			drawEdge(painter, gl, glu, cam, e, layout.get(v1),
					layout.get(v2), formatter.getEdgeColor());
		}
	}

	/*******************************************************/

	protected void drawVertexNode(Painter painter, GL gl, GLU glu, Camera cam,
			V v, Coord2d coord, Color color) {
		painter.color(color);
		painter.glVertex3f(coord.x, coord.y, Z);
	}

	protected void drawVertexLabel(Painter painter, GL gl, GLU glu, Camera cam,
			V v, Coord2d coord, Color color) {
		Coord3d textPosition = new Coord3d(coord, Z);
		txt.drawText(painter, gl, glu, cam, v.toString(), textPosition,
				Halign.CENTER, Valign.BOTTOM, color, labelScreenOffset, labelSceneOffset);
	}

	protected void drawEdge(Painter painter, GL gl, GLU glu, Camera cam, E e,
			Coord2d c1, Coord2d c2, Color color) {
		painter.glBegin(GL.GL_LINE_STRIP);
		painter.color(color);
		painter.glVertex3f(c1.x, c1.y, Z);
		painter.glVertex3f(c2.x, c2.y, Z);
		painter.glEnd();
	}

	@Override
	public void applyGeometryTransform(Transform transform) {
		Logger.getLogger(DefaultDrawableGraph2d.class).warn("not implemented");
	}

	@Override
	public void updateBounds() {
		Logger.getLogger(DefaultDrawableGraph2d.class).warn("not implemented");
	}

}
