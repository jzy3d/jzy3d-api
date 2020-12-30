package org.jzy3d.plot3d.rendering.view;

import org.jzy3d.painters.Painter;

public interface IViewOverlay {
	public void render(View view, ViewportConfiguration viewport, Painter painter);
}
