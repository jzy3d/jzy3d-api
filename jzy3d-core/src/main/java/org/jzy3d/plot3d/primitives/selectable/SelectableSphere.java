package org.jzy3d.plot3d.primitives.selectable;

import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Polygon2d;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.builder.concrete.SphereScatterGenerator;
import org.jzy3d.plot3d.primitives.Sphere;
import org.jzy3d.plot3d.rendering.view.Camera;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

public class SelectableSphere extends Sphere implements Selectable {
	public SelectableSphere() {
		this(Coord3d.ORIGIN, 10f, 15, Color.BLACK);
	}

	public SelectableSphere(Coord3d position, float radius, int slicing,
			Color color) {
		super(position, radius, slicing, color);
		buildAnchors();
	}

	@Override
    public void draw(Painter painter, GL gl, GLU glu, Camera cam) {
		super.draw(painter, gl, glu, cam);

		if (gl.isGL2()) {
			gl.getGL2().glBegin(GL.GL_POINTS);
			gl.getGL2().glColor4f(Color.RED.r, Color.RED.g, Color.RED.b,
					Color.RED.a);
			for (Coord3d a : anchors)
				gl.getGL2().glVertex3f(a.x, a.y, a.z);
			gl.getGL2().glEnd();
		} else {
			GLES2CompatUtils.glBegin(GL.GL_POINTS);
			GLES2CompatUtils.glColor4f(Color.RED.r, Color.RED.g, Color.RED.b,
					Color.RED.a);
			for (Coord3d a : anchors)
				GLES2CompatUtils.glVertex3f(a.x, a.y, a.z);
			GLES2CompatUtils.glEnd();
		}

	}

	@Override
	public void project(Painter painter, Camera cam) {
		projection = cam.modelToScreen(painter, anchors);
	}

	public List<Coord3d> getProjection() {
		return projection;
	}

	@Override
    public void setPosition(Coord3d position) {
		super.setPosition(position);
		buildAnchors();
	}

	@Override
    public void setVolume(float radius) {
		super.setVolume(radius);
		buildAnchors();
	}

	protected void buildAnchors() {
		anchors = buildAnchors(position, radius);
	}

	protected List<Coord3d> buildAnchors(Coord3d position, float radius) {
		return SphereScatterGenerator.generate(position, radius, PRECISION,
				false);
	}

	@Override
	public Polygon2d getHull2d() {
        throw new RuntimeException("not implemented");
	}

	@Override
	public List<Coord3d> getLastProjection() {
		throw new RuntimeException("not implemented");
	}

	/*********************************************/

	public void setHighlighted(boolean value) {
		isHighlighted = value;
	}

	public boolean isHighlighted() {
		return isHighlighted;
	}

	public void resetHighlighting() {
		this.isHighlighted = false;
	}

	protected List<Coord3d> anchors;
	protected int PRECISION = 10;
	protected boolean isHighlighted = false;

	protected List<Coord3d> projection;
}
