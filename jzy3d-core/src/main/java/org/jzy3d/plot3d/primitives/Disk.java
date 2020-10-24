package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

public class Disk extends AbstractWireframeable implements ISingleColorable {

	/** Initialize a Cylinder at the origin. */
	public Disk() {
		super();
		bbox = new BoundingBox3d();
		setPosition(Coord3d.ORIGIN);
		setVolume(0f, 10f);
		setSlicing(15, 15);
		setColor(Color.BLACK);
	}

	/** Initialize a cylinder with the given parameters. */
	public Disk(Coord3d position, float radiusInner, float radiusOuter,
			int slices, int loops, Color color) {
		super();
		bbox = new BoundingBox3d();
		setPosition(position);
		setVolume(radiusInner, radiusOuter);
		setSlicing(slices, loops);
		setColor(color);
	}

	/* */

	@Override
    public void draw(Painter painter, GL gl, GLU glu, Camera cam) {
		doTransform(painter, gl, glu, cam);

		if (gl.isGL2()) {
			gl.getGL2().glTranslatef(x, y, z);

			gl.glLineWidth(wfwidth);

			// Draw
			GLUquadric qobj = glu.gluNewQuadric();

			if (facestatus) {
				if (wfstatus) {
					gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
					gl.glPolygonOffset(1.0f, 1.0f);
				}

				gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
				gl.getGL2().glColor4f(color.r, color.g, color.b, color.a);
				glu.gluDisk(qobj, radiusInner, radiusOuter, slices, loops);

				if (wfstatus)
					gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

			}
			if (wfstatus) {
				gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
				gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
				glu.gluDisk(qobj, radiusInner, radiusOuter, slices, loops);
			}
		} else {
			GLES2CompatUtils.glTranslatef(x, y, z);

			gl.glLineWidth(wfwidth);

			// Draw
			GLUquadric qobj = glu.gluNewQuadric();

			if (facestatus) {
				if (wfstatus) {
					gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
					gl.glPolygonOffset(1.0f, 1.0f);
				}

				GLES2CompatUtils.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
				GLES2CompatUtils.glColor4f(color.r, color.g, color.b, color.a);
				glu.gluDisk(qobj, radiusInner, radiusOuter, slices, loops);

				if (wfstatus)
					gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

			}
			if (wfstatus) {
				GLES2CompatUtils.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
				GLES2CompatUtils.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
				glu.gluDisk(qobj, radiusInner, radiusOuter, slices, loops);
			}
		}

		doDrawBounds(painter, gl, glu, cam);
	}

	/* */

	public void setData(Coord3d position, float radiusInner, float radiusOuter,
			int slices, int loops) {
		setPosition(position);
		setVolume(radiusInner, radiusOuter);
		setSlicing(slices, loops);
	}

	public void setPosition(Coord3d position) {
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;

		updateBounds();
	}

	public void setVolume(float radiusInner, float radiusOuter) {
		if (radiusOuter < radiusInner)
			throw new IllegalArgumentException(
					"inner radius must be smaller than outer radius");

		this.radiusInner = radiusInner;
		this.radiusOuter = radiusOuter;

		updateBounds();
	}

	@Override
    public void updateBounds() {
		bbox.reset();
		bbox.add(x + radiusOuter, y + radiusOuter, z);
		bbox.add(x - radiusOuter, y - radiusOuter, z);
	}

	public void setSlicing(int verticalWires, int horizontalWires) {
		this.slices = verticalWires;
		this.loops = horizontalWires;
	}

	/* */

	@Override
    public void setColor(Color color) {
		this.color = color;

		fireDrawableChanged(new DrawableChangedEvent(this,
				DrawableChangedEvent.FIELD_COLOR));
	}

	@Override
    public Color getColor() {
		return color;
	}

	@Override
    public void applyGeometryTransform(Transform transform) {
		Coord3d change = transform.compute(new Coord3d(x, y, z));
		x = change.x;
		y = change.y;
		z = change.z;
		updateBounds();
	}

	/* */

	private float x;
	private float y;
	private float z;

	private int slices;
	private int loops;
	private float radiusInner;
	private float radiusOuter;

	private Color color;
}
