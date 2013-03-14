package org.jzy3d.plot3d.primitives.graphs;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.graphs.IGraph;
import org.jzy3d.picking.PickingSupport;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.graphs.layout.DefaultGraphFormatter;
import org.jzy3d.plot3d.primitives.graphs.layout.IGraphFormatter;
import org.jzy3d.plot3d.primitives.graphs.layout.IGraphLayout2d;
import org.jzy3d.plot3d.primitives.pickable.Pickable;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

/** 
 * An implementation based on {@link Pickable} objects should register
 * mapping between vertex model and vertex representation through
 * {@link PickSupport.registerPickableObject(IPickable pickable, V v)}
 * 
 * Registration should be done by overriding setGraphModel.
 * 
 * One can later listen to picking events through:
 * {@link PickSupport.addVertexPickListener(IVertexPickListener<V> listener)}
 */
public abstract class AbstractDrawableGraph2d<V, E> extends AbstractDrawable implements IDrawableGraph2d<V, E>{
	public AbstractDrawableGraph2d() {
		super();
		formatter = new DefaultGraphFormatter<V, E>();
	}
	
	@Override
	public IGraphFormatter<V, E> getGraphFormatter() {
		return formatter;
	}

	@Override
	public IGraphLayout2d<V> getGraphLayout(){
		return layout;
	}
	
	@Override
	public IGraph<V,E> getGraphModel(){
		return graph;
	}

	@Override
	public void setGraphFormatter(IGraphFormatter<V, E> formatter) {
		this.formatter = formatter;
	}
	
	@Override
	public void setGraphLayout(IGraphLayout2d<V> layout) {
		this.layout = layout;		
		bbox.reset();		
		for(Coord2d c: layout.values())
			bbox.add(c.x, c.y, Z);
	}
	
	@Override
	public void setGraphModel(IGraph<V,E> graph, PickingSupport picking){
		setGraphModel(graph);
	}
	
	@Override
	public void setGraphModel(IGraph<V,E> graph){
		this.graph= graph; 
		for(V v: graph.getVertices())
			highlights.put(v, false);
	}
	
	/*******************************************************/
	
	@Override
	public void draw(GL2 gl, GLU glu, Camera cam) {
		if(layout==null)
			throw new RuntimeException("missing vertex mapping");
		
		doTransform(gl, glu, cam);
	    
		// TODO move to graph view init
	    gl.glEnable(GL2.GL_POINT_SMOOTH);
	    gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
	
	    if(formatter.areEdgesDisplayed())
	    	drawEdges(gl, glu, cam);
	    if(formatter.areVerticesDisplayed())
	    	drawVertices(gl, glu, cam);
	    if(formatter.areVertexLabelsDisplayed())
	    	drawVertexLabels(gl, glu, cam);
	}
	
	protected abstract void drawVertices(GL2 gl, GLU glu, Camera cam);
	protected abstract void drawVertexLabels(GL2 gl, GLU glu, Camera cam);
	protected abstract void drawEdges(GL2 gl, GLU glu, Camera cam);

	/*******************************************************/

	public Coord2d getLabelScreenOffset() {
		return labelScreenOffset;
	}

	public void setLabelScreenOffset(Coord2d labelOffset) {
		this.labelScreenOffset = labelOffset;
	}
	
	public Coord3d getLabelSceneOffset() {
		return labelSceneOffset;
	}

	public void setLabelSceneOffset(Coord3d labelSceneOffset) {
		this.labelSceneOffset = labelSceneOffset;
	}

	/*******************************************************/
	// HIGHLIGHTS
	
	@Override
	public boolean isEdgeHighlighted(E e){
		return false;
	}
	
	@Override
	public boolean isVertexHighlighted(V v){
		return highlights.get(v);
	}
	
	@Override
	public void setVertexHighlighted(V v, boolean nodeDisplayed){
		highlights.put(v, nodeDisplayed);
	}
	
	@Override
	public void clearHighlighted(){
		for(@SuppressWarnings("unused") Boolean h: highlights.values())
			h = true;
	}
	
	protected Map<V, Boolean> highlights = new HashMap<V, Boolean>();

	/*******************************************************/
	
	protected IGraph<V,E> graph;
	protected IGraphFormatter<V, E> formatter;
	protected IGraphLayout2d<V> layout;

	protected Coord2d labelScreenOffset;
	protected Coord3d labelSceneOffset;
	protected static float Z = 0;
	
	
	protected TextBitmapRenderer txt = new TextBitmapRenderer();
	//protected TextRenderer txtRenderer;


}