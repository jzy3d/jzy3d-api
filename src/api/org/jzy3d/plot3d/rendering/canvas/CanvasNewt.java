package org.jzy3d.plot3d.rendering.canvas;
import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLCapabilitiesImmutable;

import org.jzy3d.bridge.newt.controllers.keyboard.AwtToNewtKeyListener;
import org.jzy3d.bridge.newt.controllers.mouse.AwtToNewtMouseListener;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import javax.media.opengl.GLDrawable;
/** Experimental Newt canvas. 
 */
public class CanvasNewt extends Panel implements IScreenCanvas {
    
    public CanvasNewt(IChartComponentFactory factory, Scene scene, Quality quality, GLCapabilitiesImmutable glci){
        this(factory, scene, quality, glci, false, false);
    }
    
    public CanvasNewt(IChartComponentFactory factory, Scene scene, Quality quality, GLCapabilitiesImmutable glci, boolean traceGL, boolean debugGL){
        window = GLWindow.create(glci);
        canvas = new NewtCanvasAWT(window);
        view     = scene.newView(this, quality);
        renderer = factory.newRenderer(view, traceGL, debugGL);
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
    public GLDrawable getDrawable() {
        return window;
    }
    
    @Override
    public void dispose() {
        new Thread(new Runnable() {
            public void run() {
                //System.err.println("stopping canvas animator");
                if(animator!=null && animator.isStarted()) {
                    animator.stop();
                }
                if (renderer != null) {
                    renderer.dispose(window);
                }
                canvas.destroy();
                window = null;
                renderer = null;
                view = null;
                animator = null;
            }
        }).start();
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
    @Override
    public synchronized void addKeyListener(KeyListener l) {
        window.addKeyListener(new AwtToNewtKeyListener(canvas, l));
    }
    @Override
    public synchronized void addMouseListener(MouseListener l) {
        window.addMouseListener(new AwtToNewtMouseListener(canvas, l, null, null));
    }
    @Override
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        window.addMouseListener(new AwtToNewtMouseListener(canvas, null, l, null));
    }
    @Override
    public synchronized void addMouseWheelListener(MouseWheelListener l) {
        window.addMouseListener(new AwtToNewtMouseListener(canvas, null, null, l));
    }
    protected View       view;
    protected Renderer3d renderer;
    protected Animator   animator;
    protected GLWindow window;
    protected NewtCanvasAWT canvas;
    private static final long serialVersionUID = 8578690050666237742L;
}
