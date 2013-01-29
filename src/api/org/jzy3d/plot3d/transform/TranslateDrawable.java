package org.jzy3d.plot3d.transform;

import javax.media.opengl.GL2;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractDrawable;

/** Translate drawable to (0,0,0) or back to its previous position. */

public class TranslateDrawable implements Transformer {
    public TranslateDrawable(AbstractDrawable drawable, boolean reverse) {
        super();
        this.reverse = reverse;
        this.drawable = drawable;
    }

    public void execute(GL2 gl) {
        if (drawable != null) {
            BoundingBox3d bounds = drawable.getBounds();
            if (bounds != null) {
                Coord3d center = bounds.getCenter();
                if (reverse)
                    gl.glTranslatef(-center.x/2, -center.y/2, -center.z/2);
                else
                    gl.glTranslatef(center.x/2, center.y/2, center.z/2);

            }
        }
    }

    public Coord3d compute(Coord3d input) {
        if (drawable != null) {
            BoundingBox3d bounds = drawable.getBounds();
            if (bounds != null) {
                Coord3d center = bounds.getCenter();
                if(reverse)
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

    public String toString() {
        return "(TranslateDrawable)" + drawable;
    }

    protected AbstractDrawable drawable;
    protected boolean reverse;
}
