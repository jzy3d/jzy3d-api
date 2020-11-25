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
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/**
 * A Composite gathers several Drawable and provides default methods for
 * rendering them all in one call. <br>
 *
 * @author Martin Pernollet
 *
 */
public abstract class Composite extends Wireframeable implements ISingleColorable, IMultiColorable {
    public Composite() {
        super();
        components = new ArrayList<Drawable>();
    }

    /****************************************************************/

    /** Append a list of Drawables to this composite. */
    public void add(List<? extends Drawable> drawables) {
        synchronized (components) {
            components.addAll(drawables);
        }
    }

    /** Clear the list of Drawables from this composite. */
    public void clear() {
        synchronized (components) {
            components.clear();
        }
    }

    /** Add a Drawable to this composite. */
    public void add(Drawable drawable) {
        synchronized(components){
            components.add(drawable);
        }
    }

    /** Remove a Drawable from this composite. */
    public void remove(Drawable drawable) {
        synchronized(components){
            components.remove(drawable);
        }
    }

    /** Get a Drawable stored by this composite. */
    public Drawable get(int p) {
        synchronized(components){
            return components.get(p);
        }
    }

    public List<Drawable> getDrawables() {
        return components;
    }

    /** Return the number of Drawable stored by this composite. */
    public int size() {
        return components.size();
    }

    /* */

    /** Delegate rendering iteratively to all Drawable of this composite. */
    @Override
    public void draw(Painter painter) {
        if (mapper != null)
            mapper.preDraw(this);

        synchronized (components) {
            for (Drawable c : components) {
                if (c != null)
                    c.draw(painter);
            }
        }

        if (mapper != null)
            mapper.postDraw(this);

        doDrawBoundsIfDisplayed(painter);
    }

    /**
     * Delegate transforming iteratively to all Drawable of this composite and
     * stores the given transform for keeping the ability of retrieving it.
     */
    @Override
    public void setTransform(Transform transform) {
        this.transform = transform;

        synchronized (components) {
            for (Drawable c : components) {
                if (c != null)
                    c.setTransform(transform);
            }
        }
    }

    @Override
    public void setTransformBefore(Transform transform) {
        this.transformBefore = transform;

        synchronized (components) {
            for (Drawable c : components) {
                if (c != null)
                    c.setTransformBefore(transform);
            }
        }
    }
    
    @Override
    public void setSpaceTransformer(SpaceTransformer spaceTransformer) {
        this.spaceTransformer = spaceTransformer;
        
        synchronized (components) {
            for (Drawable c : components) {
                if (c != null)
                    c.setSpaceTransformer(spaceTransformer);
            }
        }
    }

    /** Return the transform that was affected to this composite. */
    @Override
    public Transform getTransform() {
        return transform;
    }

    /**
     * Creates and return a BoundingBox3d that embed all available Drawable
     * bounds.
     */
    @Override
    public BoundingBox3d getBounds() {
        updateBounds();
        return bbox;
    }

    @Override
    public void updateBounds() {
        BoundingBox3d box = new BoundingBox3d();

        synchronized (components) {
            for (Drawable c : components) {
                if (c != null && c.getBounds() != null)
                    box.add(c.getBounds());
            }
        }
        bbox = box;
    }

    @Override
    public void applyGeometryTransform(Transform transform) {
        synchronized (components) {
            for (Drawable c : components) {
                c.applyGeometryTransform(transform);
            }
        }
        // updateBounds(); no need, as computed by getBounds()
    }

    /****************************************************************/

    @Override
    public void setWireframeColor(Color color) {
        super.setWireframeColor(color);

        if (components != null) {
            synchronized (components) {
                for (Drawable c : components) {
                    if (c != null && c instanceof Wireframeable)
                        ((Wireframeable) c).setWireframeColor(color);
                }
            }
        }
    }

    @Override
    public void setWireframeDisplayed(boolean status) {
        super.setWireframeDisplayed(status);

        if (components != null) {
            synchronized (components) {
                for (Drawable c : components) {
                    if (c != null && c instanceof Wireframeable)
                        ((Wireframeable) c).setWireframeDisplayed(status);
                }
            }
        }
    }

    @Override
    public void setWireframeWidth(float width) {
        super.setWireframeWidth(width);

        if (components != null) {
            synchronized (components) {
                for (Drawable c : components) {
                    if (c != null && c instanceof Wireframeable)
                        ((Wireframeable) c).setWireframeWidth(width);
                }
            }
        }
    }

    @Override
    public void setFaceDisplayed(boolean status) {
        super.setFaceDisplayed(status);

        if (components != null) {
            synchronized (components) {
                for (Drawable c : components) {
                    if (c != null && c instanceof Wireframeable)
                        ((Wireframeable) c).setFaceDisplayed(status);
                }
            }
        }
    }

    @Override
    public void setDisplayed(boolean status) {
        super.setDisplayed(status);

        if (components != null) {
            synchronized (components) {
                for (Drawable c : components) {
                    if (c != null && c instanceof Wireframeable)
                        ((Wireframeable) c).setDisplayed(status);
                }
            }
        }
    }
    
    /**
	 * A utility to change polygon offset fill status of a {@link Composite}
	 * containing {@link Geometry}s.
	 * 
	 * @param composite
	 * @param polygonOffsetFillEnable status
	 */
    @Override
	public void setPolygonOffsetFillEnable(boolean polygonOffsetFillEnable) {
        if (components != null) {
            synchronized (components) {

				for (Drawable d : components) {
					if (d instanceof Wireframeable) {
						((Wireframeable) d).setPolygonOffsetFillEnable(polygonOffsetFillEnable);
					} else if (d instanceof Composite) {
						((Composite) d).setPolygonOffsetFillEnable(polygonOffsetFillEnable);
					}
				}
            }
        }
	}


    /****************************************************************/

    @Override
    public void setColorMapper(ColorMapper mapper) {
        this.mapper = mapper;

        if (components != null) {

            synchronized (components) {
                for (Drawable d : components) {
                    if (d instanceof IMultiColorable)
                        ((IMultiColorable) d).setColorMapper(mapper);
                    else if (d instanceof ISingleColorable)
                        ((ISingleColorable) d).setColor(mapper.getColor(d.getBarycentre()));
                }
            }

            fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
        }

    }

    @Override
    public ColorMapper getColorMapper() {
        return mapper;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;

        if (components != null) {
            synchronized (components) {
                for (Drawable d : components)
                    if (d instanceof ISingleColorable)
                        ((ISingleColorable) d).setColor(color);
            }
            fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
        }
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
        String output = Utils.blanks(depth) + "(Composite3d) #elements:" + components.size() + " | isDisplayed=" + isDisplayed();

        if (detailedToString) {
            int k = 0;
            for (Drawable c : components) {
                if (c != null) {
                    if (c instanceof Composite)
                        output += "\n" + ((Composite) c).toString(depth + 1);
                    else
                        output += "\n" + Utils.blanks(depth + 1) + " Composite element[" + (k++) + "]:" + c.toString();
                } else
                    output += Utils.blanks(depth + 1) + "(null)\n";
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

    /****************************************************************/

    protected List<Drawable> components = null;
    protected Transform transform;

    protected ColorMapper mapper;
    protected Color color;
    protected boolean detailedToString = false;
}
