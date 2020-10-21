package org.jzy3d.plot3d.primitives.graphs.impl;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.plot3d.primitives.graphs.AbstractDrawableGraph2d;
import org.jzy3d.plot3d.primitives.textured.DrawableTexture;
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
    protected void drawVertices(GL gl, GLU glu, Camera cam) {
		if (gl.isGL2()) {
			gl.getGL2().glPointSize(formatter.getVertexWidth());
			gl.getGL2().glBegin(GL.GL_POINTS);
			for (V v : graph.getVertices()) {
				if (highlights.get(v))
					drawVertexNode(gl, glu, cam, v, layout.get(v),
							formatter.getHighlightedVertexColor());
				else
					drawVertexNode(gl, glu, cam, v, layout.get(v),
							formatter.getVertexColor());
			}
			gl.getGL2().glEnd();
		} else {
			GLES2CompatUtils.glPointSize(formatter.getVertexWidth());
			GLES2CompatUtils.glBegin(GL.GL_POINTS);
			for (V v : graph.getVertices()) {
				if (highlights.get(v))
					drawVertexNode(gl, glu, cam, v, layout.get(v),
							formatter.getHighlightedVertexColor());
				else
					drawVertexNode(gl, glu, cam, v, layout.get(v),
							formatter.getVertexColor());
			}
			GLES2CompatUtils.glEnd();
		}
	}

	@Override
    protected void drawVertexLabels(GL gl, GLU glu, Camera cam) {
		for (V v : graph.getVertices()) {
			if (highlights.get(v))
				drawVertexLabel(gl, glu, cam, v, layout.get(v),
						formatter.getHighlightedVertexColor());
			else
				drawVertexLabel(gl, glu, cam, v, layout.get(v),
						formatter.getVertexLabelColor());
		}
	}

	@Override
    protected void drawEdges(GL gl, GLU glu, Camera cam) {
		for (E e : graph.getEdges()) {
			V v1 = graph.getEdgeStartVertex(e);
			V v2 = graph.getEdgeStopVertex(e);
			drawEdge(gl, glu, cam, e, layout.get(v1), layout.get(v2),
					formatter.getEdgeColor());
		}
	}

	/*******************************************************/

	protected void drawVertexNode(GL gl, GLU glu, Camera cam, V v,
			Coord2d coord, Color color) {
		if (gl.isGL2()) {
			gl.getGL2().glColor4f(color.r, color.g, color.b, color.a);
			gl.getGL2().glVertex3f(coord.x, coord.y, Z);
		} else {
			GLES2CompatUtils.glColor4f(color.r, color.g, color.b, color.a);
			GLES2CompatUtils.glVertex3f(coord.x, coord.y, Z);
		}
	}

	protected void drawVertexLabel(GL gl, GLU glu, Camera cam, V v,
			Coord2d coord, Color color) {
		Coord3d textPosition = new Coord3d(coord, Z);
		txt.drawText(gl, glu, cam, v.toString(), textPosition, Halign.CENTER,
				Valign.BOTTOM, color, labelScreenOffset, labelSceneOffset);
	}

	protected void drawEdge(GL gl, GLU glu, Camera cam, E e, Coord2d c1,
			Coord2d c2, Color color) {
		if (gl.isGL2()) {
			gl.getGL2().glBegin(GL.GL_LINE_STRIP);
			gl.getGL2().glColor4f(color.r, color.g, color.b, color.a);
			gl.getGL2().glVertex3f(c1.x, c1.y, Z);
			gl.getGL2().glVertex3f(c2.x, c2.y, Z);
			gl.getGL2().glEnd();
		} else {
			GLES2CompatUtils.glBegin(GL.GL_LINE_STRIP);
			GLES2CompatUtils.glColor4f(color.r, color.g, color.b, color.a);
			GLES2CompatUtils.glVertex3f(c1.x, c1.y, Z);
			GLES2CompatUtils.glVertex3f(c2.x, c2.y, Z);
			GLES2CompatUtils.glEnd();
		}
	}

	@Override
	public void applyGeometryTransform(Transform transform) {
		Logger.getLogger(DrawableTexture.class).warn("not implemented");
	}

	@Override
	public void updateBounds() {
		Logger.getLogger(DrawableTexture.class).warn("not implemented");
	}

}
