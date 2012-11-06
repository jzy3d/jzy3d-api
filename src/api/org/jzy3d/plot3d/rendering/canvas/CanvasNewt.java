package org.jzy3d.plot3d.rendering.canvas;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLCapabilitiesImmutable;

import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;

/** Experimental Newt canvas. 
 */
public class CanvasNewt extends Panel implements IScreenCanvas{
    public CanvasNewt(IChartComponentFactory factory, Scene scene, Quality quality, GLCapabilitiesImmutable glci){
        window = GLWindow.create(glci);
        canvas = new NewtCanvasAWT(window);
        
        view     = scene.newView(this, quality);
        renderer = factory.newRenderer(view, false, false);
        
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
        if(animator!=null)
            animator.stop();
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
    
    @Override
    public BufferedImage screenshot(){
        renderer.nextDisplayUpdateScreenshot();
        display();
        return renderer.getLastScreenshot();
    }
    
    @Override
	public Renderer3d getRenderer(){
		return renderer;
	}
    
    public String getDebugInfo(){
		GL gl = getView().getCurrentGL();
		
		StringBuffer sb = new StringBuffer();
		sb.append("Chosen GLCapabilities: " + window.getChosenGLCapabilities() + "\n");
		sb.append("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR) + "\n");
		sb.append("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER) + "\n");
		sb.append("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION) + "\n");
		//sb.append("INIT GL IS: " + gl.getClass().getName() + "\n");
		return sb.toString();
	}

    
    /* */
    
    

    /** Provide the actual renderer width for the open gl camera settings, 
     * which is obtained after a resize event.*/
    @Override
    public int getRendererWidth(){
        return (renderer!=null?renderer.getWidth():0);
    }
    
    /** Provide the actual renderer height for the open gl camera settings, 
     * which is obtained after a resize event.*/
    @Override
    public int getRendererHeight(){
        return (renderer!=null?renderer.getHeight():0);
    }
    
    /** Provide a reference to the View that renders into this canvas.*/
    @Override
    public View getView(){
        return view;
    }
    
    public GLWindow getWindow() {
		return window;
	}

	public NewtCanvasAWT getCanvas() {
		return canvas;
	}



	protected View       view;
    protected Renderer3d renderer;
    protected Animator   animator;
    
    protected GLWindow window;
    protected NewtCanvasAWT canvas;
    
    private static final long serialVersionUID = 8578690050666237742L;

}
