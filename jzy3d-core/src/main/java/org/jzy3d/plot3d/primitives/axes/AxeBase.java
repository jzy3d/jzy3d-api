package org.jzy3d.plot3d.primitives.axes;

import java.util.List;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

/**
 * An AxeBase provide a simple 3-segment object which is configured by a
 * BoundingBox.
 * 
 * @author Martin Pernollet
 */
public class AxeBase implements IAxe {

	/** Create a simple axe centered on (0,0,0), with a dimension of 1. */
	public AxeBase() {
		setAxe(new BoundingBox3d(0, 1, 0, 1, 0, 1));
		setScale(new Coord3d(1.0f, 1.0f, 1.0f));
	}

	/** Create a simple axe centered on (box.xmin, box.ymin, box.zmin) */
	public AxeBase(BoundingBox3d box) {
		setAxe(box);
		setScale(new Coord3d(1.0f, 1.0f, 1.0f));
	}

	@Override
	public void dispose() {
	}

	/******************************************************************/

	@Override
	public void setAxe(BoundingBox3d box) {
		bbox = box;
	}

	@Override
	public void draw(Painter painter, GL gl, GLU glu, Camera camera) {
		painter.glLoadIdentity();
		painter.glScalef(scale.x, scale.y, scale.z);
		painter.glLineWidth(2);

		painter.glBegin(GL.GL_LINES);
		painter.glColor3f(1.0f, 0.0f, 0.0f); // R
		painter.glVertex3f(bbox.getXmin(), bbox.getYmin(), bbox.getZmin());
		painter.glVertex3f(bbox.getXmax(), 0, 0);
		painter.glColor3f(0.0f, 1.0f, 0.0f); // G
		painter.glVertex3f(bbox.getXmin(), bbox.getYmin(), bbox.getZmin());
		painter.glVertex3f(0, bbox.getYmax(), 0);
		painter.glColor3f(0.0f, 0.0f, 1.0f); // B
		painter.glVertex3f(bbox.getXmin(), bbox.getYmin(), bbox.getZmin());
		painter.glVertex3f(0, 0, bbox.getZmax());
		painter.glEnd();
	}

	/**
	 * Set the scaling factor that are applied on this object before GL2 commands.
	 */
	@Override
	public void setScale(Coord3d scale) {
		this.scale = scale;
	}

	@Override
	public BoundingBox3d getBoxBounds() {
		return bbox;
	}

	/** Get the minimum values of the bounding box for each dimension. */
	@Override
	public Coord3d getCenter() {
		return new Coord3d(bbox.getXmin(), bbox.getYmin(), bbox.getZmin());
	}

	@Override
	public IAxeLayout getLayout() {
		return layout;
	}

	@Override
	public SpaceTransformer getSpaceTransformer() {
		return spaceTransformer;
	}

	@Override
	public void setSpaceTransformer(SpaceTransformer spaceTransformer) {
		this.spaceTransformer = spaceTransformer;
	}

	@Override
	public Coord3d getScale() {
		return scale;
	}

	protected Coord3d scale;
	protected BoundingBox3d bbox;
	protected IAxeLayout layout;

	protected SpaceTransformer spaceTransformer;

	@Override
	public List<AxeAnnotation> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAnnotations(List<AxeAnnotation> annotations) {
		// TODO Auto-generated method stub

	}

	@Override
	public BoundingBox3d getWholeBounds() {
		return bbox;
	}
}
