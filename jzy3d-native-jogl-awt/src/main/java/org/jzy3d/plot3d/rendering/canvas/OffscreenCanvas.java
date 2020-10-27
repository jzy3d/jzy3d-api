package org.jzy3d.plot3d.rendering.canvas;

import java.io.File;
import java.io.IOException;

import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLFBODrawable;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.GLPixelBuffer;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * An {@link ICanvas} implementation able to render the chart in an offscreen canvas,
 * meaning no frame or GUI is needed to get a chart.
 * 
 * Subsequently, one will wish to generate chart images by calling:
 * <pre>
 * <code>
 * chart.screenshot();
 * </code>
 * </pre>
 * 
 * Note that the {@link GLCapabilities} are modified while an instance of {@link OffscreenCanvas} 
 * is modified.
 * 
 * @author Nils Hoffman
 * @author Martin Pernollet
 */
public class OffscreenCanvas implements ICanvas {
    public OffscreenCanvas(IChartComponentFactory factory, Scene scene, Quality quality, GLCapabilities capabilities, int width, int height) {
        this(factory, scene, quality, capabilities, width, height, false, false);
    }

    public OffscreenCanvas(IChartComponentFactory factory, Scene scene, Quality quality, GLCapabilities capabilities, int width, int height, boolean traceGL, boolean debugGL) {
        
        this.view = scene.newView(this, quality);
        this.renderer = factory.newRenderer(view, traceGL, debugGL);
        this.capabilities = capabilities;
        
        initBuffer(capabilities, width, height);
    }

    /**
     * Initialize a {@link GLOffscreenAutoDrawable} to the desired dimensions 
     * 
     * Might be a {@link GLPixelBuffer} or {@link GLFBODrawable} depending on the provided
     * {@link GLCapabilities} ({@link GLCapabilities#setPBuffer(true)} to enable PBuffer).
     * 
     * Can be called several time to reset buffer dimensions.
     * 
     * @param capabilities
     * @param width
     * @param height
     */
    public void initBuffer(GLCapabilities capabilities, int width, int height) {
        //capabilities.setDoubleBuffered(false);
        //capabilities/setPBuffer(true);
        
        /*capabilities.setHardwareAccelerated(true); 
        capabilities.setDoubleBuffered(false); 
        capabilities.setAlphaBits(8); 
        capabilities.setRedBits(8); 
        capabilities.setBlueBits(8); 
        capabilities.setGreenBits(8); 
        capabilities.setOnscreen(false); */

        
        GLProfile profile = capabilities.getGLProfile();
        GLDrawableFactory factory = GLDrawableFactory.getFactory(profile);
        
        //if (!factory.canCreateGLPbuffer(null, profile))
        //    throw new RuntimeException("No pbuffer support");
        
        if(offscreenDrawable!=null){
            offscreenDrawable.removeGLEventListener(renderer);
            offscreenDrawable.destroy();
            //glpBuffer.setSurfaceSize(width, height);
        }
        offscreenDrawable = factory.createOffscreenAutoDrawable(factory.getDefaultDevice(), capabilities, null /*new DefaultGLCapabilitiesChooser()*/, width, height);
        offscreenDrawable.addGLEventListener(renderer);            
        
    }
    
    /* NOT IMPLEMENTED */
    @Override
    public void setPixelScale(float[] scale){
        throw new NotImplementedException();
        //glpBuffer.setSurfaceScale(scale);
    }

    @Override
    public GLOffscreenAutoDrawable getDrawable() {
        return offscreenDrawable;
    }

    @Override
    public void dispose() {
        offscreenDrawable.destroy();
        renderer = null;
        view = null;
    }

    @Override
    public void forceRepaint() {
        offscreenDrawable.display();
    }

    @Override
    public TextureData screenshot() {
        renderer.nextDisplayUpdateScreenshot();
        offscreenDrawable.display();
        return renderer.getLastScreenshot();
    }
    
    @Override
    public TextureData screenshot(File file) throws IOException {
        TextureData screen = screenshot();
        TextureIO.write(screen, file);
        return screen;
    }

    /** Provide a reference to the View that renders into this canvas. */
    @Override
    public View getView() {
        return view;
    }

    @Override
    public int getRendererWidth() {
        return (renderer != null ? renderer.getWidth() : 0);
    }

    @Override
    public int getRendererHeight() {
        return (renderer != null ? renderer.getHeight() : 0);
    }

    @Override
    public Renderer3d getRenderer() {
        return renderer;
    }

    @Override
    public String getDebugInfo() {
    	GL gl = ((NativeDesktopPainter)getView().getPainter()).getCurrentGL(this);
		
        StringBuffer sb = new StringBuffer();
        sb.append("Chosen GLCapabilities: " + offscreenDrawable.getChosenGLCapabilities() + "\n");
        sb.append("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR) + "\n");
        sb.append("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER) + "\n");
        sb.append("GL_VERSION: " + gl.glGetString(GL.GL_VERSION) + "\n");
        // sb.append("INIT GL IS: " + gl.getClass().getName() + "\n");
        return sb.toString();
    }

    @Override
    public void addMouseController(Object o) {}
    @Override
    public void addKeyController(Object o) {}
    @Override
    public void removeMouseController(Object o) {}
    @Override
    public void removeKeyController(Object o) {}
    
    public GLCapabilities getCapabilities() {
        return capabilities;
    }

    protected View view;
    protected Renderer3d renderer;
    protected GLOffscreenAutoDrawable offscreenDrawable;
    protected GLCapabilities capabilities;
}
