package org.jzy3d.plot3d.rendering.ordering;

import org.jzy3d.plot3d.primitives.AbstractDrawable;

/** The default ordering strategy let drawables be displayed in their original order.*/
public class DefaultOrderingStrategy extends AbstractOrderingStrategy{
	public int compare(AbstractDrawable o1, AbstractDrawable o2) {
		return 0;
	}
}
