package org.jzy3d.plot3d.rendering.canvas;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.image.BufferedImage;

import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLCapabilitiesImmutable;

import org.jzy3d.factories.JzyFactories;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;

/** Experimental Newt canvas. 
 * Mouse & key listener wont work with it.
 */
public class CanvasNewt extends Panel implements IScreenCanvas{
    public CanvasNewt(Scene scene, Quality quality, GLCapabilitiesImmutable glci){
        window = GLWindow.create(glci);
        canvas = new NewtCanvasAWT(window);
        
        view     = scene.newView(this, quality);
        renderer = JzyFactories.renderer3d.getInstance(view, false, false);
        
        window.addGLEventListener(renderer);
        
        // swing specific
        setFocusable(true);
        requestFocusInWindow();
        
        window.setAutoSwapBufferMode(quality.isAutoSwapBuffer());

        if(quality.isAnimated()){
            animator = new Animator(window);
            getAnimator().start();
        }
        
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
    }
    
    @Override
    public void dispose(){
        window.destroy();
        canvas.destroy();
        
        if(renderer!=null)
            renderer.dispose(window);
        renderer = null;
        view = null; 
    }
    
    @Override
    public void display() {
        window.display();
    }
    
    @Override
    public void forceRepaint(){
        display();
    }

    @Override
    public GLAnimatorControl getAnimator() {
        return window.getAnimator();
    }
    
    public BufferedImage screenshot(){
        renderer.nextDisplayUpdateScreenshot();
        display();
        return renderer.getLastScreenshot();
    }
    
    /* */
    
    /** Provide a reference to the View that renders into this canvas.*/
    public View getView(){
        return view;
    }

    /** Provide the actual renderer width for the open gl camera settings, 
     * which is obtained after a resize event.*/
    public int getRendererWidth(){
        return (renderer!=null?renderer.getWidth():0);
    }
    
    /** Provide the actual renderer height for the open gl camera settings, 
     * which is obtained after a resize event.*/
    public int getRendererHeight(){
        return (renderer!=null?renderer.getHeight():0);
    }
        
    /* */
    
    protected View       view;
    protected Renderer3d renderer;
    protected Animator   animator;
    
    protected GLWindow window;
    protected NewtCanvasAWT canvas;
    
    private static final long serialVersionUID = 8578690050666237742L;

}
