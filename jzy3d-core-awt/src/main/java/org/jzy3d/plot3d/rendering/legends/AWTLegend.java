package org.jzy3d.plot3d.rendering.legends;

import java.awt.image.BufferedImage;
import java.io.IOException;
import org.jzy3d.chart.ChartView;
import org.jzy3d.colors.Color;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.events.IDrawableListener;
import org.jzy3d.io.AWTImageFile;
import org.jzy3d.maths.Dimension;
import org.jzy3d.plot2d.primitive.AWTImageGenerator;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.view.AWTImageViewport;

/**
 * A {@link AWTLegend} represent information concerning a {@link Drawable} that may be displayed as
 * a metadata in the {@link ChartView}.
 * 
 * The constructor of a {@link AWTLegend} registers itself as listener of its parent
 * {@link Drawable}, and unregister itself when it is disposed.
 * 
 * When defining a concrete {@link AWTLegend}, one should:
 * <ul>
 * <li>override the {@link toImage(int width, int height)} method, that defines the picture
 * representation.
 * <li>override the {@link drawableChanged(DrawableChangedEvent e)} method, that must select events
 * that actually triggers an image update.
 * </ul>
 * 
 * Last, a {@link AWTLegend} optimizes rendering by :
 * <ul>
 * <li>storing current image dimension,
 * <li>computing a new image only if the required {@link AWTLegend} dimensions changed.
 * </ul>
 * 
 * @author Martin Pernollet
 */
public abstract class AWTLegend extends AWTImageViewport implements IDrawableListener, ILegend {
  protected Drawable drawable;
  protected Color foreground;
  protected Color background;
  protected Dimension minimumDimension;
  protected AWTImageGenerator imageGenerator;
  
  public AWTLegend(Drawable drawable) {
    this.drawable = drawable;
    if (drawable != null) {
      drawable.addDrawableListener(this);
    }
  }

  public AWTLegend(Drawable drawable, Color foreground, Color background) {
    this(drawable);
    this.foreground = foreground;
    this.background = background;
  }

  public void dispose() {
    if (drawable != null)
      drawable.removeDrawableListener(this);
  }

  public abstract BufferedImage toImage(int width, int height);

  /**
   * Defines viewport dimensions, and precompute an image if required (i.e. if the viewport
   * dimension have changed
   */
  @Override
  public void setViewPort(int width, int height, float left, float right) {
    super.setViewPort(width, height, left, right);

    int imgWidth = getSliceWidth(width, left, right);

    if (imageWidth != imgWidth || imageHeight != height) {
      //imageWidth = imgWidth;
      //imageHeight = height;
      //updateImage();
      setImage(toImage(imgWidth, height));
    }
  }

  @Override
  public void drawableChanged(DrawableChangedEvent e) {
    if (e.what() == DrawableChangedEvent.FIELD_COLOR)
      updateImage();
  }


  @Override
  public void updateImage() {
    setImage(toImage(imageWidth, imageHeight));
  }

  public void saveImage(String filename) throws IOException {
    AWTImageFile.savePNG(image, filename);
  }


  @Override
  public Dimension getMinimumSize() {
    return minimumDimension;
  }

  public void setMinimumSize(Dimension dimension) {
    minimumDimension = dimension;
  }

  public void setGeneratorColors() {
    if (foreground != null)
      imageGenerator.setForegroundColor(foreground);
    else
      imageGenerator.setForegroundColor(Color.BLACK);
    if (background != null) {
      imageGenerator.setBackgroundColor(background);
      imageGenerator.setHasBackground(true);
    } else
      imageGenerator.setHasBackground(false);
  }

  public Color getForeground() {
    return foreground;
  }

  public void setForeground(Color foreground) {
    this.foreground = foreground;
  }

  public Color getBackground() {
    return background;
  }

  public void setBackground(Color background) {
    this.background = background;
  }

  public Dimension getMinimumDimension() {
    return minimumDimension;
  }

  public void setMinimumDimension(Dimension minimumDimension) {
    this.minimumDimension = minimumDimension;
  }

  public void setMinimumWidth(int minimumWidth) {
    this.minimumDimension.width = minimumWidth;
  }
}
