package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.events.IDrawableListener;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

/**
 * A {@link AbstractDrawable} defines objects that may be rendered into an
 * OpenGL context provided by a {@link ICanvas}. <br>
 * A {@link AbstractDrawable} must basically provide a rendering function called
 * draw() that receives a reference to a GL2 and a GLU context. It may also use
 * a reference to a Camera in order to implement specific behaviors according to
 * the Camera position. <br>
 * A {@link AbstractDrawable} provides services for setting the transformation
 * factor that is used inside the draw function, as well as a getter of the
 * object's BoundingBox3d. Note that the BoundingBox must be set by a concrete
 * descendant of a {@link AbstractDrawable}. <br>
 * A good practice is to define a setData function for initializing a
 * {@link AbstractDrawable} and building its polygons. Since each class may have
 * its own inputs, setData is not part of the interface but should be used as a
 * convention. When not defining a setData function, a {@link AbstractDrawable}
 * may have its data loaded by an {@link add(Drawable)} function.
 * <p>
 * Note: A {@link AbstractDrawable} may last provide the information whether it
 * is displayed or not, according to a rendering into the FeedBack buffer. This
 * is currently supported specifically for the {@link AxeBox} object but could
 * be extended with some few more algorithm for referencing all GL2 polygons.
 * 
 * @author Martin Pernollet
 * 
 */
public abstract class AbstractDrawable implements IGLRenderer, ISortableDraw {

    /**
     * Performs all required operation to cleanup the Drawable.
     */
    public void dispose() {
        if (listeners != null)
            listeners.clear();
    }

    /**
     * Call OpenGL2 routines for rendering the object.
     * @param gl
     *            GL2 context
     * @param glu
     *            GLU context
     * @param cam
     *            a reference to a shooting Camera.
     */
    @Override
    public abstract void draw(Painter painter, GL gl, GLU glu, Camera cam);

    public abstract void applyGeometryTransform(Transform transform);

    public abstract void updateBounds();

    public void doTransform(Painter painter, GL gl, GLU glu, Camera cam) {
        if (transformBefore != null) {
            if (transformBefore != null)
                transformBefore.execute(painter, true);
            if (transform != null)
                transform.execute(painter, false);
        } else {
            if (transform != null)
                transform.execute(painter);
        }
    }

    protected void doDrawBounds(Painter painter, GL gl, GLU glu, Camera cam) {
        if (isBoundingBoxDisplayed()) {
            Parallelepiped p = new Parallelepiped(getBounds());
            p.setFaceDisplayed(false);
            p.setWireframeColor(getBoundingBoxColor());
            p.draw(painter, gl, glu, cam);
        }
    }

    protected void negative(Color c) {
        c.r = 1 - c.r;
        c.g = 1 - c.g;
        c.b = 1 - c.b;
    }

    /**
     * Set object's transformation that is applied at the beginning of a call to
     * {@link #draw(Painter,GL,GLU, Camera)}.
     * 
     * @param transform
     */
    public void setTransform(Transform transform) {
        this.transform = transform;

        fireDrawableChanged(DrawableChangedEvent.FIELD_TRANSFORM);
    }

    /**
     * Get object's transformation that is applied at the beginning of a call to
     * {@link #draw(Painter,GL,GLU, Camera)}.
     * 
     * @return transform
     */
    public Transform getTransform() {
        return transform;
    }

    public Transform getTransformBefore() {
        return transformBefore;
    }

    public void setTransformBefore(Transform transformBefore) {
        this.transformBefore = transformBefore;
    }

    /**
     * Return the BoundingBox of this object.
     * 
     * @return a bounding box
     */
    public BoundingBox3d getBounds() {
        return bbox;
    }

    /**
     * Return the barycentre of this object, which is computed as the center of
     * its bounding box. If the bounding box is not available, the returned
     * value is {@link Coord3d.INVALID}
     * 
     * @return the center of the bounding box, or {@link Coord3d.INVALID}.
     */
    public Coord3d getBarycentre() {
        if (bbox != null)
            return bbox.getCenter();
        else
            return Coord3d.INVALID.clone();
    }

    /**
     * Set to true or false the displayed status of this object.
     * 
     * @param status
     */
    public void setDisplayed(boolean status) {
        displayed = status;

        fireDrawableChanged(DrawableChangedEvent.FIELD_DISPLAYED);
    }

    /** Return the display status of this object. */
    public boolean isDisplayed() {
        return displayed;
    }

    /** Return the distance of the object center to the {@link Camera}'s eye. */
    @Override
    public double getDistance(Camera camera) {
        return getBarycentre().distance(camera.getEye());
    }

    @Override
    public double getShortestDistance(Camera camera) {
        return getDistance(camera);
    }

    @Override
    public double getLongestDistance(Camera camera) {
        return getDistance(camera);
    }

    /* */

    public void setLegend(ILegend face) {
        this.legend = face;
        legendDisplayed = true;
        fireDrawableChanged(DrawableChangedEvent.FIELD_METADATA);
    }

    public ILegend getLegend() {
        return legend;
    }

    public boolean hasLegend() {
        return (legend != null);
    }

    public void setLegendDisplayed(boolean status) {
        legendDisplayed = status;
    }

    public boolean isLegendDisplayed() {
        return legendDisplayed;
    }

    public boolean isBoundingBoxDisplayed() {
        return boundingBoxDisplayed;
    }

    public void setBoundingBoxDisplayed(boolean boundingBoxDisplayed) {
        this.boundingBoxDisplayed = boundingBoxDisplayed;
    }

    public Color getBoundingBoxColor() {
        return boundingBoxColor;
    }

    public void setBoundingBoxColor(Color boundingBoxColor) {
        this.boundingBoxColor = boundingBoxColor;
    }
    
    public SpaceTransformer getSpaceTransformer() {
        return spaceTransformer;
    }

    public void setSpaceTransformer(SpaceTransformer spaceTransformer) {
        this.spaceTransformer = spaceTransformer;
    }

    /* */

    public void addDrawableListener(IDrawableListener listener) {
        if (listeners == null)
            listeners = new ArrayList<IDrawableListener>();
        listeners.add(listener);
        hasListeners = true;
    }

    public void removeDrawableListener(IDrawableListener listener) {
        listeners.remove(listener);
    }

    protected void fireDrawableChanged(int eventType) {
        if (listeners != null) {
            fireDrawableChanged(new DrawableChangedEvent(this, eventType));
        }
    }

    protected void fireDrawableChanged(DrawableChangedEvent e) {
        if (listeners != null) {
            for (IDrawableListener listener : listeners) {
                listener.drawableChanged(e);
            }
        }
    }

    /* */

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int depth) {
        return Utils.blanks(depth) + "(" + this.getClass().getSimpleName() + ")";
    }

    /* */

    protected Transform transform;
    protected Transform transformBefore;
    protected BoundingBox3d bbox;
    protected ILegend legend = null;
    protected List<IDrawableListener> listeners;
    protected boolean hasListeners = true;

    protected boolean displayed = true;
    protected boolean legendDisplayed = false;
    protected boolean boundingBoxDisplayed = false;
    protected Color boundingBoxColor = Color.BLACK.clone();

    protected SpaceTransformer spaceTransformer = null;

}
