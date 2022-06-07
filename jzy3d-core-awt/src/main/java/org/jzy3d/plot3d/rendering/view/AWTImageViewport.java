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
  protected static final float IMAGE_Z = 0;

  protected ByteBuffer imageData = null;
  protected BufferedImage image;
  protected int imageHeight;
  protected int imageWidth;
  
  protected Margin margin = new Margin(0,0,0,0); // no margin by default
  protected Coord2d pixelScale = new Coord2d(1,1); // assume default pixel scale

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

    float xZoom = 1;
    float yZoom = 1;
    int xPosition = 0;
    int yPosition = 0;

    if (imageWidth < screenWidth) {
      if(margin.getLeft()!=margin.getRight()) {
        xPosition = Math.round(screenWidth / 2f - (imageWidth / 2f + margin.getWidth()));

      }
      else {
      xPosition = Math.round((float) screenWidth / 2 - (float) imageWidth / 2);
      }
    }
    else
      xZoom = ((float) screenWidth) / ((float) imageWidth);

    if (imageHeight < screenHeight) {
      //int shiftForMargin = 0;
      
      // TODO : clarify this
      
      if(margin.getTop()!=margin.getBottom()) {
        //shiftForMargin = Math.round(margin.getHeight()/2);
        yPosition = Math.round(screenHeight / 2f - (imageHeight / 2f - margin.getHeight()/2f) /*margin.getTop())*/);
      }
      else {
        yPosition = Math.round((float) screenHeight / 2 - ((float) imageHeight / 2));        
      }
    }
    else
      yZoom = ((float) screenHeight) / ((float) imageHeight);

    //System.out.println("AWTImageViewport posi.x:" + xPosition + " posi.y:" + xPosition);
    //System.out.println("AWTImageViewport zoom.x:" + xZoom + " zoom.y:" + yZoom);
    //System.out.println("AWTImageViewport size.x:" + imageWidth + " size.y:" + imageHeight);
    
    // Draw
    
    Coord2d zoom = new Coord2d(xZoom, yZoom);
    Coord3d position = new Coord3d(xPosition, yPosition, z);
    
    painter.drawImage(imageBuffer, imageWidth, imageHeight, zoom, position);
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
