package org.jzy3d.chart;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.colors.Color;
import org.jzy3d.factories.JzyFactories;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewt;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer2d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;


/** {@link Chart} is a convenient object that gather all components required to render 
 * a 3d scene for plotting.
 * 
 * The chart {@link Quality} enable the following functionalities:
 * 
 *				 
 * 
 * 
 * @author Martin Pernollet
 *
 */
public class Chart{
    public static Quality DEFAULT_QUALITY = Quality.Intermediate;
    public static String DEFAULT_WINDOWING_TOOLKIT = "awt";
	
    public Chart(){
		this(DEFAULT_QUALITY, DEFAULT_WINDOWING_TOOLKIT);
	}	
	public Chart(Quality quality){
		this(quality, DEFAULT_WINDOWING_TOOLKIT);
	}	
	public Chart(String windowingToolkit){
		this(DEFAULT_QUALITY, windowingToolkit);
	}
	
    public Chart(Quality quality, String windowingToolkit){
        this(quality, windowingToolkit, org.jzy3d.global.Settings.getInstance().getGLCapabilities());
    }
    
    public Chart(Quality quality, String windowingToolkit, GLCapabilities capabilities){
        this.capabilities = capabilities;
        
		// Set up controllers
		controllers = new ArrayList<AbstractCameraController>(1);
		
		// Set up the scene and 3d canvas
		scene  = initializeScene( quality.isAlphaActivated() );
		canvas = initializeCanvas(scene, quality, windowingToolkit);	
		
        // Set up the view
        view = canvas.getView();
		view.setBackgroundColor(Color.WHITE);
	}
	
	protected ICanvas initializeCanvas(Scene scene, Quality quality, String chartType){
		if("awt".compareTo(chartType)==0)
			return new CanvasAWT(scene, quality, capabilities);
		else if("swing".compareTo(chartType)==0)
			return new CanvasSwing(scene, quality, capabilities);
        else if("newt".compareTo(chartType)==0)
            return new CanvasNewt(scene, quality, capabilities);
		else if(chartType.startsWith("offscreen")){
            Pattern pattern = Pattern.compile("offscreen,(\\d+),(\\d+)");
            Matcher matcher = pattern.matcher(chartType);
            if(matcher.matches()){
                int width = Integer.parseInt(matcher.group(1));
                int height = Integer.parseInt(matcher.group(2));
                return new OffscreenCanvas(scene, quality, GLProfile.getDefault(), width, height);
            }
            else
                return new OffscreenCanvas(scene, quality, GLProfile.getDefault(), 500, 500);
        }
		else
			throw new RuntimeException("unknown chart type:" + chartType);
	}
	
	/**
	 * Provides a concrete scene. This method shoud be overriden to inject a custom scene,
	 * which may rely on several views, and could enhance manipulation of scene graph.
	 */
	protected ChartScene initializeScene(boolean graphsort){
		return JzyFactories.scene.getInstance(graphsort);
	}
	
	public void clear(){
		scene.clear();
		view.shoot();
	}
	
	public void stopAnimator(){
	    if(canvas!=null && canvas instanceof IScreenCanvas){
	        GLAnimatorControl control = ((IScreenCanvas)canvas).getAnimator();
	        if(control!=null)
	            control.stop();
	    }
	}
	
	public void dispose(){
		clearControllerList();
		canvas.dispose();
		scene.dispose(); // view is disposed by scene
		canvas   = null;
		scene    = null;
	}
	
	/**
	 * Trigger a chart rendering. Only usefull if chart Quality.is
	 */
	public void render(){
		view.shoot();
	}
	
	public BufferedImage screenshot(){
		return canvas.screenshot();
	}
	
	public void updateProjectionsAndRender(){
        getView().shoot();
        getView().project();
        render();
    }
	
	/**************************************************************/
		
	/** Add a {@link AbstractCameraController} to this {@link Chart}.
	 * Warning: the {@link Chart} is not the owner of the controller. Disposing
	 * the chart thus just unregisters the controllers, but does not handle
	 * stopping and disposing controllers.
	 */
	public void addController(AbstractCameraController controller){
		controller.register(this);
		controllers.add(controller);
	}
	
	public void removeController(AbstractCameraController controller){
		controller.unregister(this);
		controllers.remove(controller);
	}
	
	protected void clearControllerList(){
		for(AbstractCameraController controller: controllers)
			controller.unregister(this);
		controllers.clear();
	}
	
	public List<AbstractCameraController> getControllers(){
	    return controllers;
	}
	
	public void addDrawable(AbstractDrawable drawable){
		getScene().getGraph().add(drawable);
	}
	
	public void addDrawable(AbstractDrawable drawable, boolean updateViews){
		getScene().getGraph().add(drawable, updateViews);
	}
	
	public void addDrawable(List<? extends AbstractDrawable> drawables, boolean updateViews){
		getScene().getGraph().add(drawables, updateViews);
	}
	
	public void addDrawable(List<? extends AbstractDrawable> drawables){
		getScene().getGraph().add(drawables);
	}
	
	public void removeDrawable(AbstractDrawable drawable){
		getScene().getGraph().remove(drawable);
	}
	
	public void removeDrawable(AbstractDrawable drawable, boolean updateViews){
		getScene().getGraph().remove(drawable, updateViews);
	}

	public void addRenderer(Renderer2d renderer2d){
		view.addRenderer2d(renderer2d);
	}
	
	public void removeRenderer(Renderer2d renderer2d){
		view.removeRenderer2d(renderer2d);
	}
	
	public View getView(){
		return view;
	}

	public ChartScene getScene(){
		return scene;
	}
	
	public ICanvas getCanvas(){
		return canvas;
	}
	
	public IAxeLayout getAxeLayout(){
		return getView().getAxe().getLayout();
	}

	public void setAxeDisplayed(boolean status){
		view.setAxeBoxDisplayed(status);
		view.shoot(); 
	}

	/**************************************************************************************/
	
	public void setViewPoint(Coord3d viewPoint){
		view.setViewPoint(viewPoint);
		view.shoot();
	}

	public Coord3d getViewPoint(){
		return view.getViewPoint();
	}
	
	/* */
	
	public void setViewMode(ViewPositionMode mode){
		// Store current view mode and view point in memory
		ViewPositionMode previous = view.getViewMode();		
		if(previous==ViewPositionMode.FREE)
			previousViewPointFree = view.getViewPoint();
		else if(previous==ViewPositionMode.TOP)
			previousViewPointTop = view.getViewPoint();
		else if(previous==ViewPositionMode.PROFILE)
			previousViewPointProfile = view.getViewPoint();	
		
		// Set new view mode and former view point
		view.setViewPositionMode(mode);
		if(mode==ViewPositionMode.FREE)
			view.setViewPoint( previousViewPointFree==null ? View.DEFAULT_VIEW.clone() : previousViewPointFree );
		else if(mode==ViewPositionMode.TOP)
			view.setViewPoint( previousViewPointTop==null ? View.DEFAULT_VIEW.clone() : previousViewPointTop );
		else if(mode==ViewPositionMode.PROFILE)
			view.setViewPoint( previousViewPointProfile==null ? View.DEFAULT_VIEW.clone() : previousViewPointProfile );
		
		view.shoot();
	}
	
	public ViewPositionMode getViewMode(){
		return view.getViewMode();
	}
	
	/* */

	public void setScale(org.jzy3d.maths.Scale scale, boolean notify){
		view.setScale(scale, notify);
	}
	
	public void setScale(Scale scale){
		setScale(scale, true);
	}
	
	public Scale getScale() {
		return new Scale(view.getBounds().getZmin(), view.getBounds().getZmax());
	}
	
	public float flip(float y){
		return canvas.getRendererHeight() - y;
	}
	
	public GLCapabilities getCapabilities(){
	    return capabilities;
	}
	
	/* */
	
	protected ChartScene  scene;
	protected View        view;
	protected ICanvas 	  canvas;
	
	protected Coord3d	  previousViewPointFree;
	protected Coord3d	  previousViewPointTop;
	protected Coord3d	  previousViewPointProfile;	
	
	protected ArrayList<AbstractCameraController> controllers;	
	
	protected GLCapabilities capabilities;
}
