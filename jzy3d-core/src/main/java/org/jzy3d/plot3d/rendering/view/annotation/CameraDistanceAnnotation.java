package org.jzy3d.plot3d.rendering.view.annotation;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.AbstractGeometry;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

/**
 * Draws all point distance to camera eye.
 * 
 * The camera is represented as a red point.
 * 
 * @author Martin
 */
public class CameraDistanceAnnotation extends Point {
	public CameraDistanceAnnotation(View view, Color color) {
		super();
		this.view = view;
		setColor(color);
		setWidth(5);
	}

	@Override
	public void draw(GL gl, GLU glu, Camera cam) {
		computeCameraPosition();
		doTransform(gl, glu, cam);

		doDrawCamera(gl, glu, cam);

		Halign h = Halign.RIGHT;
		Valign v = Valign.CENTER;
		Coord2d screenOffset = new Coord2d(10, 0);
		Color colorBary = Color.BLACK;
		Color colorPt = Color.GRAY.clone();
		colorPt.alphaSelf(0.5f);

		Graph graph = view.getScene().getGraph();
		AbstractOrderingStrategy strat = graph.getStrategy();
		for (AbstractDrawable drawables : graph.getDecomposition()) {
			double d = strat.score(drawables);
			txt.drawText(gl, glu, view.getCamera(), Utils.num2str(d, 4),
					drawables.getBarycentre(), h, v, colorBary, screenOffset);

			if (drawables instanceof AbstractGeometry) {
				Polygon p = (Polygon) drawables;
				for (Point pt : p.getPoints()) {
					// Point pt2 = pt.clone();
					d = strat.score(pt);
					txt.drawText(gl, glu, view.getCamera(),
							Utils.num2str(d, 4), pt.getCoord(), h, v, colorPt,
							screenOffset);
				}
			}
		}
	}

	public void computeCameraPosition() {
		Coord3d scaling = view.getLastViewScaling().clone();
		xyz = view.getCamera().getEye().clone();
		xyz = xyz.div(scaling);
	}

	public void doDrawCamera(GL gl, GLU glu, Camera cam) {
		if (gl.isGL2()) {
			gl.getGL2().glPointSize(width);
			gl.getGL2().glBegin(GL.GL_POINTS);
			gl.getGL2().glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
			gl.getGL2().glVertex3f(xyz.x, xyz.y, xyz.z);
			gl.getGL2().glEnd();
		} else {
			GLES2CompatUtils.glPointSize(width);
			GLES2CompatUtils.glBegin(GL.GL_POINTS);
			GLES2CompatUtils.glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
			GLES2CompatUtils.glVertex3f(xyz.x, xyz.y, xyz.z);
			GLES2CompatUtils.glEnd();
		}
	}

	protected View view;
	protected ITextRenderer txt = new TextBitmapRenderer();
}
