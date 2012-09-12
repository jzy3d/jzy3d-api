package org.jzy3d.chart;

import org.jzy3d.factories.JzyFactories;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ordering.BarycentreOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;


/** The {@link ChartScene} provides {@link ChartView} to its target
 * canvases and set a {@link BarycentreOrderingStrategy} as polygon
 * ordering method.
 * 
 * @author Martin Pernollet
 */
public class ChartScene extends Scene {
	public ChartScene(){
		nview = 0;
	}
	
	public ChartScene(boolean graphsort){
		super(graphsort);
		graph = new Graph(this, JzyFactories.ordering.getInstance(), graphsort);
		nview = 0;
	}
	
	public void clear(){
		view.setBoundManual(new BoundingBox3d(0,0,0,0,0,0));
	}
	
	public View newView(ICanvas canvas, Quality quality){
		if(nview>0)
			throw new RuntimeException("A view has already been defined for this scene. Can not use several views.");
		else
			nview++;
		
		view = initializeChartView(this, canvas, quality);
		views.add(view);
		return view;
	}
	
	public void clearView(View view){
		super.clearView(view);
		nview--;
	}
	
	protected View initializeChartView(Scene scene, ICanvas canvas, Quality quality){
		return JzyFactories.view.getInstance(scene, canvas, quality);//new ChartView(scene, canvas, quality);
	}
	
	/********************************************************/
	
	protected int nview;
	protected View view;
}
