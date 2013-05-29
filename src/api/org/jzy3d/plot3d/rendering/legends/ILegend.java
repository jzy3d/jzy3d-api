package org.jzy3d.plot3d.rendering.legends;

import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.plot3d.rendering.view.IImageViewport;

public interface ILegend  extends IImageViewport{

    public void drawableChanged(DrawableChangedEvent e);

    /** Recompute the picture, using last used dimensions. */
    public void updateImage();

}