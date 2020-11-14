package org.jzy3d.plot3d.rendering.view;

import java.awt.Image;
import java.nio.ByteBuffer;

import org.jzy3d.maths.Dimension;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.rendering.image.GLImage;

import com.jogamp.opengl.fixedfunc.GLMatrixFunc;

/**
 * A {@link AWTImageViewport} allows displaying a 2d {@link Image} within an
 * OpenGL2 viewport.
 * 
 * @author Martin Pernollet
 */
public class AWTImageViewport extends AbstractViewportManager implements IImageViewport {
	static final float IMAGE_Z = 0;//-0.75f;
	
    public AWTImageViewport() {
        setViewportMode(ViewportMode.RECTANGLE_NO_STRETCH);
    }

    @Override
    public void render(Painter painter) {
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

        ImageRenderer.renderImage(painter, imageData, imageWidth, imageHeight, screenWidth, screenHeight, IMAGE_Z);

        // Restore matrices state
        painter.glPopMatrix();
        painter.glMatrixMode_Projection();
        painter.glPopMatrix();
    }

    /**
     * Set the {@link Image} that will be displayed by the layer.
     * 
     * @param image
     */
    public void setImage(Image image, int width, int height) {
        if (image != null) {
            synchronized (image) {
                ByteBuffer b = GLImage.getImageAsGlByteBuffer(image, width, height);
                setImage(image, width, height, b);
            }
        }
    }

    public void setImage(Image image, int width, int height, ByteBuffer buffer) {
        imageObj = image;
        imageHeight = height;
        imageWidth = width;
        imageData = buffer;
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
        return imageObj;
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

    /*******************************************************************************/

    private ByteBuffer imageData = null;
    protected Image imageObj;
    protected int imageHeight;
    protected int imageWidth;
}
