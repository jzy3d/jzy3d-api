package org.jzy3d.plot3d.rendering.view;

import java.awt.Image;
import java.nio.ByteBuffer;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.image.AWTImageConvert;

/**
 * A {@link AWTImageViewport} allows displaying a 2d {@link Image} within an OpenGL viewport.
 * 
 * @author Martin Pernollet
 */
public class AWTImageViewport extends AbstractViewportManager implements IImageViewport {
  protected static final float IMAGE_Z = 0;// -0.75f;
  protected static final int MARGIN_HEIGHT = 25;
  protected static final int MARGIN_WIDTH = 25;
  

  protected ByteBuffer imageData = null;
  protected Image image;
  protected int imageHeight;
  protected int imageWidth;
  
  protected Dimension margin = new Dimension(MARGIN_WIDTH, MARGIN_HEIGHT);
  protected Coord2d pixelScale = new Coord2d(1,1); // assume default pixel scale

  public AWTImageViewport() {
    setViewportMode(ViewportMode.RECTANGLE_NO_STRETCH);
  }

  @Override
  public void render(IPainter painter) {
    updatePixelScale(painter.getView().getPixelScale());
    // gl.glDisable(GL2.GL_LIGHTING);

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

    renderImage(painter, imageData, imageWidth, imageHeight, screenWidth,
        screenHeight, IMAGE_Z);

    // Restore matrices state
    painter.glPopMatrix();
    painter.glMatrixMode_Projection();
    painter.glPopMatrix();
  }
  

  protected void renderImage(IPainter painter, ByteBuffer imageBuffer, int imageWidth, int imageHeight,
      int screenWidth, int screenHeight, float z) {
    if (imageBuffer == null)
      return;

    float xratio = 1;
    float yratio = 1;
    int xpict = 0;
    int ypict = 0;

    if (imageWidth < screenWidth)
      xpict = (int) ((float) screenWidth / 2 - (float) imageWidth / 2);
    else
      xratio = ((float) screenWidth) / ((float) imageWidth);

    if (imageHeight < screenHeight)
      ypict = (int) ((float) screenHeight / 2 - (float) imageHeight / 2);
    else
      yratio = ((float) screenHeight) / ((float) imageHeight);

    // Draw
    
    Coord2d pixelZoom = new Coord2d(xratio, yratio);
    Coord3d imagePosition = new Coord3d(xpict, ypict, z);
    
    painter.drawImage(imageBuffer, imageWidth, imageHeight, pixelZoom, imagePosition);
  }

  
  /** Update internal pixel scale knowledge. Called by render loop and provided by painter's view. May be overrided to update the image. */
  protected void updatePixelScale(Coord2d pixelScale) {
    this.pixelScale = pixelScale ;
  }

  /**
   * Set the {@link Image} that will be displayed by the layer.
   * 
   * @param image
   */
  public void setImage(Image image, int width, int height) {
    if (image != null) {
      synchronized (image) {
        ByteBuffer b = AWTImageConvert.getImageAsByteBuffer(image, width, height);
        setImage(image, width, height, b);
      }
    }
  }

  public void setImage(Image image, int width, int height, ByteBuffer buffer) {
    this.image = image;
    this.imageHeight = height;
    this.imageWidth = width;
    this.imageData = buffer;
  }

  public void setImage(Image image) {
    if (image != null) {
      setImage(image, image.getWidth(null), image.getHeight(null));
    }
  }

  /**
   * Return the image rendered by the {@link AWTImageViewport}
   */
  public Image getImage() {
    return image;
  }

  /** Return the minimum size for this graphic. */
  @Override
  public Dimension getMinimumSize() {
    return new Dimension(0, 0);
  }

  /** Return the prefered size for this graphic. */
  @Override
  public Dimension getPreferedSize() {
    return new Dimension(1, 1);
  }

}
