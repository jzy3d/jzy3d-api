package org.jzy3d.plot3d.rendering.canvas;

import java.io.File;
import java.io.IOException;

import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;

/**
 * A {@link ICanvas} represent the target component for rendering OpenGL.
 * 
 * It might be displayed on the screen in a GUI ({@link IScreenCanvas}), or
 * simply be an offscreen component able to export an image
 * {@link OffscreenCanvas}.
 * 
 * @see {@link IScreenCanvas}
 * 
 * @author Martin Pernollet
 */
public interface ICanvas {
    /** Returns a reference to the held view. */
    public View getView();

    /** Returns the renderer's width, i.e. the display width. */
    public int getRendererWidth();

    /** Returns the renderer's height, i.e. the display height. */
    public int getRendererHeight();

    //public Renderer3d getRenderer();
    
    public void screenshot(File file) throws IOException;

    public Object screenshot();

    /**
     * Invoked when a user requires the Canvas to be repainted (e.g. a non 3d
     * layer has changed).
     */
    public void forceRepaint();


    /** Performs all required cleanup when destroying a Canvas. */
    public void dispose();

    /**
     * A generic interface for mouse listener to remain Windowing toolkit
     * independant.
     * 
     * Implementation of this method should simply cast the input assuming it
     * will correspond to canvas-compatible mouse listener.
     */
    public void addMouseController(Object o);

    /**
     * A generic interface for mouse listener to remain Windowing toolkit
     * independant.
     * 
     * Implementation of this method should simply cast the input assuming it
     * will correspond to canvas-compatible mouse listener.
     */
    public void addKeyController(Object o);

    /**
     * A generic interface for key listener to remain Windowing toolkit
     * independant.
     * 
     * Implementation of this method should simply cast the input assuming it
     * will correspond to canvas-compatible key listener.
     */
    public void removeMouseController(Object o);

    /**
     * A generic interface for key listener to remain Windowing toolkit
     * independant. * Implementation of this method should simply cast the input
     * assuming it will correspond to canvas-compatible key listener.
     */
    public void removeKeyController(Object o);

    /* */

    public String getDebugInfo();

    /**
     * Defines pixel scale.
     * 
     * On MacOS Retina displays, a X*Y chart uses a (X*2)*(Y*2) viewport. This
     * makes some calculation based on viewport (such as
     * {@link Camera#screenToModel(Painter, org.jzy3d.maths.Coord3d)}
     * output wrong result.
     * 
     * When running on Retina display, those two options fixed buggy mouse
     * selections on Retina:
     * <ul>
     * <li>setPixelScale(new float[]{0.5f,0.5f})
     * <li>setPixelScale(new float[] { ScalableSurface.IDENTITY_PIXELSCALE,
     * ScalableSurface.IDENTITY_PIXELSCALE })
     * <ul>
     * 
     * @see {@link ScalableSurface#setSurfaceScale(float[])} in JOGL javadoc for
     *      more informations
     * 
     */
    public void setPixelScale(float[] scale);

}
