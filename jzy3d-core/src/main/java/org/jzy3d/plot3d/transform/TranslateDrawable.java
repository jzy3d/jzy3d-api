package org.jzy3d.plot3d.transform;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.plot3d.primitives.AbstractDrawable;

import com.jogamp.opengl.GL;

/**
 * Translate drawable to (0,0,0) or back to its previous position according to
 * the input parameters.
 */
public class TranslateDrawable implements Transformer {
    public TranslateDrawable(AbstractDrawable drawable, boolean reverse) {
        super();
        this.reverse = reverse;
        this.drawable = drawable;
    }

    @Override
    public void execute(GL gl) {
        if (drawable != null) {
            BoundingBox3d bounds = drawable.getBounds();
            if (bounds != null) {
                Coord3d center = bounds.getCenter();
                translateTo(gl, center, reverse);
            }
        }
    }

    public void translateTo(GL gl, Coord3d center, boolean reverse) {
        if (gl.isGLES()) {
            float reverseCoef = (reverse ? -1.0f : 1.0f);
            GLES2CompatUtils.glTranslatef(reverseCoef * center.x / 2, reverseCoef * center.y / 2, reverseCoef * center.z / 2);
        } else {
            if (reverse)
                gl.getGL2().glTranslatef(-center.x / 2, -center.y / 2, -center.z / 2);
            else
                gl.getGL2().glTranslatef(center.x / 2, center.y / 2, center.z / 2);
        }
    }

    @Override
    public Coord3d compute(Coord3d input) {
        if (drawable != null) {
            BoundingBox3d bounds = drawable.getBounds();
            if (bounds != null) {
                Coord3d center = bounds.getCenter();
                if (reverse)
                    return input.sub(center.div(2));
                else
                    return input.add(center.div(2));
            }
        }
        return null;
    }

    public AbstractDrawable getDrawable() {
        return drawable;
    }

    public void setDrawable(AbstractDrawable drawable) {
        this.drawable = drawable;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public String toString() {
        return "(TranslateDrawable)" + drawable;
    }

    protected AbstractDrawable drawable;
    protected boolean reverse;
}
