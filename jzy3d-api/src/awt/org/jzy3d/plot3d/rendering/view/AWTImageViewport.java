package org.jzy3d.plot3d.rendering.view;

import java.awt.Image;
import java.nio.ByteBuffer;

import org.jzy3d.maths.Dimension;
import org.jzy3d.plot3d.rendering.image.GLImage;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;

/**
 * A {@link AWTImageViewport} allows displaying a 2d {@link Image} within an
 * OpenGL2 viewport.
 * 
 * @author Martin Pernollet
 */
public class AWTImageViewport extends AbstractViewportManager implements IImageViewport {

    public AWTImageViewport() {
        setViewportMode(ViewportMode.RECTANGLE_NO_STRETCH);
    }

    @Override
    public void render(GL gl, GLU glu) {
        // gl.glDisable(GL2.GL_LIGHTING);

        if (gl.isGL2()) {
            // Set viewport and projection
            gl.getGL2().glMatrixMode(GLMatrixFunc.GL_PROJECTION);
            gl.getGL2().glPushMatrix();
            gl.getGL2().glLoadIdentity();
            applyViewport(gl, glu);
            gl.getGL2().glOrtho(0, screenWidth, 0, screenHeight, -1, 1);

            // Zoom and layout
            gl.getGL2().glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
            gl.getGL2().glPushMatrix();
            gl.getGL2().glLoadIdentity();

            ImageRenderer.renderImage(gl, imageData, imageWidth, imageHeight, screenWidth, screenHeight, -0.75f);

            // Restore matrices state
            gl.getGL2().glPopMatrix();
            gl.getGL2().glMatrixMode(GLMatrixFunc.GL_PROJECTION);
            gl.getGL2().glPopMatrix();
        }
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
