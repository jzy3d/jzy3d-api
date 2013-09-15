package org.jzy3d.plot3d.rendering.legends;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.jzy3d.chart.ChartView;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.events.IDrawableListener;
import org.jzy3d.io.FileImage;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.rendering.view.AWTImageViewport;

/**
 * A {@link AWTLegend} represent information concerning a
 * {@link AbstractDrawable} that may be displayed as a metadata in the
 * {@link ChartView}.
 * 
 * The constructor of a {@link AWTLegend} registers itself as listener of its
 * parent {@link AbstractDrawable}, and unregister itself when it is disposed.
 * 
 * When defining a concrete {@link AWTLegend}, one should:
 * <ul>
 * <li>override the {@link toImage(int width, int height)} method, that defines
 * the picture representation.
 * <li>override the {@link drawableChanged(DrawableChangedEvent e)} method, that
 * must select events that actually triggers an image update.
 * </ul>
 * 
 * Last, a {@link AWTLegend} optimizes rendering by :
 * <ul>
 * <li>storing current image dimension,
 * <li>computing a new image only if the required {@link AWTLegend} dimensions
 * changed.
 * </ul>
 * 
 * @author Martin Pernollet
 */
public abstract class AWTLegend extends AWTImageViewport implements IDrawableListener, ILegend {

    public AWTLegend(AbstractDrawable parent) {
        this.parent = parent;
        if (parent != null)
            parent.addDrawableListener(this);
    }

    public void dispose() {
        if (parent != null)
            parent.removeDrawableListener(this);
    }

    public abstract BufferedImage toImage(int width, int height);

    @Override
    public abstract void drawableChanged(DrawableChangedEvent e);

    /**
     * Defines viewport dimensions, and precompute an image if required (i.e. if
     * the viewport dimension have changed
     */
    @Override
    public void setViewPort(int width, int height, float left, float right) {
        super.setViewPort(width, height, left, right);

        int imgWidth = (int) (width * (right - left));

        if (imageWidth != imgWidth || imageHeight != height)
            setImage(toImage(imgWidth, height));
    }

    @Override
    public void updateImage() {
        setImage(toImage(imageWidth, imageHeight));
    }

    public void saveImage(String filename) throws IOException {
        FileImage.savePNG(imageObj, filename);
    }

    protected AbstractDrawable parent;
}
