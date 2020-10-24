package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * A {@link CompileableComposite} allows storage and subsequent faster execution
 * of individual contained instances drawing routines in an OpenGL display list.
 * 
 * Compiling the object take the time needed to render it as a standard
 * {@link AbstractComposite}, and rendering it once it is compiled seems to take
 * roughly half the time up to now. Since compilation occurs during a {@link
 * draw()}, the first call to {@link draw()} is supposed to be 1.5x longer than
 * a standard {@link AbstractComposite}, while all next cycles would be 0.5x
 * longer.
 * 
 * Compilation occurs when the content or the display attributes of this
 * Composite changes (then all add(), remove(), setColor(),
 * setWireFrameDisplayed(), etc). One can also force rebuilding the object by
 * calling recompile();
 * 
 * IMPORTANT: for the moment, {@link CompileableComposite} should not be use in
 * a charts using a {@link Quality} superior to Intermediate, in other word, you
 * should not desire to have alpha enabled in your scene. Indeed, alpha requires
 * ordering of polygons each time the viewpoint changes, which would require to
 * recompile the object.
 * 
 * Unsupported in Android / OpenGL ES
 * 
 * @author Nils Hoffmann
 */
public class CompileableComposite extends AbstractWireframeable implements
		ISingleColorable, IMultiColorable {

	private int dlID = -1;
	private boolean resetDL = false;

	protected ColorMapper mapper;
	protected Color color;
	protected boolean detailedToString = false;
	protected List<AbstractDrawable> components = new ArrayList<AbstractDrawable>();

	public CompileableComposite() {
		super();
		components = new ArrayList<AbstractDrawable>();
	}

	/**
	 * Force the object to be rebuilt and stored as a display list at the next
	 * call to draw().
	 */
	public void recompile() {
		resetDL = true;
	}

	/**
	 * Reset the object if required, compile the object if it is not compiled,
	 * and execute actual rendering.
	 */
	@Override
	public void draw(Painter painter, GL gl, GLU glu, Camera cam) {
		if (resetDL)
			reset(gl);
		if (dlID == -1)
			compile(painter, gl, glu, cam);
		execute(painter, gl, glu, cam);
	}

	/****************************************************************/

	/** If you call compile, the display list will be regenerated. 
	 * @param painter TODO*/
	protected void compile(Painter painter, GL gl, GLU glu, Camera cam) {
		reset(gl); // clear old list

		nullifyChildrenTransforms();
		dlID = gl.getGL2().glGenLists(1);
		gl.getGL2().glNewList(dlID, GL2.GL_COMPILE);
		drawComponents(painter, gl, glu, cam);
		doDrawBounds(painter, gl, glu, cam);
		gl.getGL2().glEndList();
	}

	protected void execute(Painter painter, GL gl, GLU glu, Camera cam) {
		doTransform(painter, gl, glu, cam);
		gl.getGL2().glCallList(dlID);
	}

	protected void reset(GL gl) {
		if (!gl.isGL2()) {
			throw new UnsupportedOperationException(
					"CompileableComposite : Display lists not supported with OpenGL ES");
		}
		if (dlID != -1) {
			if (gl.getGL2().glIsList(dlID)) {
				gl.getGL2().glDeleteLists(dlID, 1);
			}
			dlID = -1;
		}
		resetDL = false;
	}

	/**
	 * When a drawable has a null transform, no transform is applied at
	 * draw(...).
	 */
	protected void nullifyChildrenTransforms() {
		for (AbstractDrawable c : components) {
			if (c != null) {
				c.setTransform(null);
			}
		}
	}

	protected void drawComponents(Painter painter, GL gl, GLU glu, Camera cam) {
		synchronized (components) {
			for (AbstractDrawable s : components) {
				s.draw(painter, gl, glu, cam);
			}
		}
	}

	@Override
    public void applyGeometryTransform(Transform transform) {
		for (AbstractDrawable c : components) {
			c.applyGeometryTransform(transform);
		}
		// updateBounds(); getBounds() do the work
	}

	@Override
    public BoundingBox3d getBounds() {
		updateBounds();
		return bbox;
	}

	@Override
    public void updateBounds() {
		BoundingBox3d box = new BoundingBox3d();

		for (AbstractDrawable c : components) {
			if (c != null && c.getBounds() != null)
				box.add(c.getBounds());
		}
		bbox = box;
	}

	/****************************************************************/

	/** Append a list of Drawables to this composite. */
	public void add(List<? extends AbstractDrawable> drawables) {
		components.addAll(drawables);
		recompile();
	}

	/** Clear the list of Drawables from this composite. */
	public void clear() {
		components.clear();
		recompile();
	}

	/** Add a Drawable to this composite. */
	public void add(AbstractDrawable drawable) {
		components.add(drawable);
		recompile();
	}

	/** Remove a Drawable from this composite. */
	public void remove(AbstractDrawable drawable) {
		components.remove(drawable);
		recompile();
	}

	/** Get a Drawable stored by this composite. */
	public AbstractDrawable get(int p) {
		return components.get(p);
	}

	public List<AbstractDrawable> getDrawables() {
		return components;
	}

	/** Return the number of Drawable stored by this composite. */
	public int size() {
		return components.size();
	}

	/* */

	@Override
    public void setWireframeColor(Color color) {
		super.setWireframeColor(color);

		if (components != null) {
			for (AbstractDrawable c : components) {
				if (c != null && c instanceof AbstractWireframeable) {
					((AbstractWireframeable) c).setWireframeColor(color);
				}
			}
		}
		recompile();
	}

	@Override
    public void setWireframeDisplayed(boolean status) {
		super.setWireframeDisplayed(status);

		if (components != null)
			for (AbstractDrawable c : components)
				if (c != null && c instanceof AbstractWireframeable)
					((AbstractWireframeable) c).setWireframeDisplayed(status);
		recompile();
	}

	@Override
    public void setWireframeWidth(float width) {
		super.setWireframeWidth(width);

		if (components != null)
			for (AbstractDrawable c : components)
				if (c != null && c instanceof AbstractWireframeable)
					((AbstractWireframeable) c).setWireframeWidth(width);
		recompile();
	}

	@Override
    public void setFaceDisplayed(boolean status) {
		super.setFaceDisplayed(status);

		if (components != null)
			for (AbstractDrawable c : components)
				if (c != null && c instanceof AbstractWireframeable)
					((AbstractWireframeable) c).setFaceDisplayed(status);
		recompile();
	}

	/****************************************************************/
	@Override
    public void setColorMapper(ColorMapper mapper) {
		this.mapper = mapper;

		if (components != null) {
			for (AbstractDrawable d : components) {
				if (d instanceof IMultiColorable) {
					((IMultiColorable) d).setColorMapper(mapper);
				} else if (d instanceof ISingleColorable) {
					((ISingleColorable) d).setColor(mapper.getColor(d
							.getBarycentre()));
				}
			}

			fireDrawableChanged(new DrawableChangedEvent(this,
					DrawableChangedEvent.FIELD_COLOR));
		}
		recompile();

	}

	@Override
    public ColorMapper getColorMapper() {
		return mapper;
	}

	@Override
    public void setColor(Color color) {
		this.color = color;

		if (components != null) {
			for (AbstractDrawable d : components) {
				if (d instanceof ISingleColorable) {
					((ISingleColorable) d).setColor(color);
				}
			}

			fireDrawableChanged(new DrawableChangedEvent(this,
					DrawableChangedEvent.FIELD_COLOR));
		}
		recompile();
	}

	@Override
    public Color getColor() {
		return color;
	}

	/****************************************************************/
	/** Print out information concerning all Drawable of this composite. */
	@Override
    public String toString() {
		return toString(0);
	}

	@Override
    public String toString(int depth) {
		String output = Utils.blanks(depth) + "(Composite3d) #elements:"
				+ components.size() + " | isDisplayed=" + isDisplayed();

		if (detailedToString) {
			int k = 0;
			for (AbstractDrawable c : components) {
				if (c != null) {
					if (c instanceof AbstractComposite) {
						output += "\n"
								+ ((AbstractComposite) c).toString(depth + 1);
					} else {
						output += "\n" + Utils.blanks(depth + 1)
								+ " Composite element[" + (k++) + "]:"
								+ c.toString();
					}
				} else {
					output += Utils.blanks(depth + 1) + "(null)\n";
				}
			}
		}

		return output;
	}

	public boolean isDetailedToString() {
		return detailedToString;
	}

	/**
	 * When to true, the {@link toString()} method will give the detail of each
	 * element of this composite object in a tree like layout.
	 */
	public void setDetailedToString(boolean detailedToString) {
		this.detailedToString = detailedToString;
	}
}
