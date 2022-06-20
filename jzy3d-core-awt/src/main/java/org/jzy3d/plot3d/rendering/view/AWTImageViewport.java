package org.jzy3d.plot3d.rendering.view;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Margin;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.image.AWTImageConvert;

/**
 * A {@link AWTImageViewport} allows displaying a 2d {@link Image} within an OpenGL viewport.
 * 
 * @author Martin Pernollet
 */
public class AWTImageViewport extends AbstractViewportManager implements IImageViewport {

  protected ByteBuffer imageData = null;
  protected BufferedImage image;
  protected int imageHeight;
  protected int imageWidth;

  protected Margin margin = new Margin(0, 0, 0, 0); // no margin by default
  protected Coord2d pixelScale = new Coord2d(1, 1); // assume default pixel scale

  public AWTImageViewport() {
    setViewportMode(ViewportMode.RECTANGLE_NO_STRETCH);
  }

  @Override
  public void render(IPainter painter) {
    updatePixelScale(painter.getView().getPixelScale());

    // Set viewport and projection
    painter.glMatrixMode_Projection();
    painter.glPushMatrix();
    painter.glLoadIdentity();
    applyViewport(painter);
    painter.glOrtho(0, screenWidth, 0, screenHeight, -1, 1);

    // Zoom and layout
    painter.glMatrixMode_ModelView();
    painter.glPushMatrix();
    painter.glLoadIdentity();

    renderImage(painter);

    // Restore matrices state
    painter.glPopMatrix();
    painter.glMatrixMode_Projection();
    painter.glPopMatrix();
  }


  protected void renderImage(IPainter painter) {
    if (imageData == null)
      return;

    ImageLayout iLayout = computeLayout(painter);

    // Draw

    painter.drawImage(imageData, imageWidth, imageHeight, iLayout.zoom, iLayout.position);
  }

  /**
   * Compute the position of the image in this viewport based on the image dimensions, the viewport
   * dimensions and the margin.
   */
  protected ImageLayout computeLayout(IPainter painter) {
    Coord2d scale = painter.getCanvas().getPixelScale();
    ImageLayout iLayout = new ImageLayout();


    // If image is smaller than viewport, move it a bit to let it appear in the center
    if (imageWidth < screenWidth) {
      // inspired by View2DLayout_Debug which is accurate
      iLayout.position.x =
          Math.round((screenWidth - (imageWidth + (margin.getWidth() * scale.x))) / 2f);
      iLayout.position.x += (margin.getLeft() * scale.x);

    }
    // Else if image is bigger than viewport, unzoom it a bit to let it fit the dimensions
    else if (imageWidth > screenWidth) {
      iLayout.zoom.x = ((float) screenWidth) / ((float) imageWidth);
    }
    // If exact fit, keep default values

    // If image is smaller than viewport, move it a bit to let it appear in the center
    if (imageHeight < screenHeight) {
      iLayout.position.y =
          Math.round((screenHeight - (imageHeight + (margin.getHeight() * scale.y))) / 2f);
      iLayout.position.y += (margin.getBottom() * scale.y);
    }
    // If image is bigger than viewport, unzoom it a bit to let it fit the dimensions
    else if (imageWidth > screenWidth) {
      iLayout.zoom.y = ((float) screenHeight) / ((float) imageHeight);
    }
    // If exact fit, keep default value

    // System.out.println("AWTImageViewport posi.x:" + xPosition + " posi.y:" + xPosition);
    // System.out.println("AWTImageViewport zoom.x:" + xZoom + " zoom.y:" + yZoom);
    // System.out.println("AWTImageViewport size.x:" + imageWidth + " size.y:" + imageHeight);
    return iLayout;
  }

  public class ImageLayout {
    protected static final float IMAGE_Z = 0;

    public Coord2d zoom = new Coord2d(1, 1);
    public Coord3d position = new Coord3d(0, 0, IMAGE_Z);
  }


  /**
   * Update internal pixel scale knowledge. Called by render loop and provided by painter's view.
   * May be overrided to update the image.
   */
  @Override
  public void updatePixelScale(Coord2d pixelScale) {
    this.pixelScale = pixelScale;
  }
  
  @Override
  public Coord2d getPixelScale() {
    return this.pixelScale;
  }

  /**
   * Set the {@link Image} that will be displayed by the layer.
   * 
   * @param image
   */
  public void setImage(BufferedImage image, int width, int height) {
    if (image != null) {
      synchronized (image) {
        ByteBuffer b = AWTImageConvert.getImageAsByteBuffer(image, width, height);
        setImage(image, width, height, b);
      }
    }
  }

  public void setImage(BufferedImage image, int width, int height, ByteBuffer buffer) {
    this.image = image;
    this.imageHeight = height;
    this.imageWidth = width;
    this.imageData = buffer;
  }

  public void setImage(BufferedImage image) {
    if (image != null) {
      setImage(image, image.getWidth(null), image.getHeight(null));
    }
  }

  /**
   * Return the image rendered by the {@link AWTImageViewport}
   */
  public BufferedImage getImage() {
    return image;
  }

  /** Return the minimum size for this graphic. */
  @Override
  public Dimension getMinimumDimension() {
    return new Dimension(0, 0);
  }

  public Margin getMargin() {
    return margin;
  }

  public void setMargin(Margin margin) {
    this.margin = margin;
  }
}
