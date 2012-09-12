package org.jzy3d.plot3d.primitives.graphs.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.maths.graphs.IGraph;
import org.jzy3d.picking.PickingSupport;
import org.jzy3d.plot3d.primitives.pickable.PickableTexture;
import org.jzy3d.plot3d.primitives.textured.DrawableTexture;
import org.jzy3d.plot3d.rendering.textures.SharedTexture;
import org.jzy3d.plot3d.rendering.textures.TextureFactory;
import org.jzy3d.plot3d.rendering.view.Camera;

public class TextureGraph2d<V,E>  extends DefaultDrawableGraph2d<V, E> {
	public static float TEX_MAPPING_WIDTH = 0.25f;
	public static float TEX_MAPPING_HEIGHT = 0.25f;
	
	public TextureGraph2d(String nodeMaskFile){
		super();
		resource = TextureFactory.get(nodeMaskFile);
		labelScreenOffset = new Coord2d(0, 0);
		labelSceneOffset = new Coord3d(0,-TEX_MAPPING_HEIGHT/2,0);
	}
	
	public void setGraphModel(IGraph<V,E> graph, PickingSupport picking){
		super.setGraphModel(graph, picking);
		
		for(V v: graph.getVertices()){
			PickableTexture t = newTexture(v);
			vertexTextures.put(v, t);
			picking.registerDrawableObject(t, v);
		}
	}
	
	protected PickableTexture newTexture(V vertex){
		List<Coord2d> mapping = DrawableTexture.getManualTextureMapping(TEX_MAPPING_WIDTH, TEX_MAPPING_HEIGHT);
		PickableTexture texture = new PickableTexture(resource, PlaneAxis.Z, Z, mapping, Color.BLACK);
		return texture;
	}
		
	/***********************/
	
	@Override
	protected void drawVertices(GL2 gl, GLU glu, Camera cam){
		for(V v: graph.getVertices()){
			if(highlights.get(v))
				drawVertexNode(gl, glu, cam, v, layout.get(v), formatter.getHighlightedVertexColor());
			else
				drawVertexNode(gl, glu, cam, v, layout.get(v), formatter.getVertexColor());
		}
	}
	
	@Override
	protected void drawVertexNode(GL2 gl, GLU glu, Camera cam, V v, Coord2d coord, Color color){
		PickableTexture t = vertexTextures.get(v);
		t.setColorFilter(color);
        t.setPlanePosition(coord);
        t.draw(gl, glu, cam);
	}
	
	/***********************/
	
	protected SharedTexture resource;	
	protected Map<V, PickableTexture> vertexTextures = new HashMap<V, PickableTexture>();
}
