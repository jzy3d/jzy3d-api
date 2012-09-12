package org.jzy3d.plot3d.primitives.textured;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.ConvexHull;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.plot3d.primitives.AbstractComposite;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.selectable.Selectable;
import org.jzy3d.plot3d.rendering.view.Camera;


public class TexturedCube extends AbstractComposite implements Selectable, ITranslucent{
	public TexturedCube(MaskPair masks){
		this(new Coord3d(), Color.CYAN, Color.RED, masks);
	}
	
	public TexturedCube(Coord3d position, MaskPair masks){
		this(position, Color.CYAN, Color.RED, masks);
	}
	
	public TexturedCube(Coord3d position, Color color, Color bgcolor, MaskPair masks){	
		float width = 0.5f;
		
		List<Coord2d> zmapping  = new ArrayList<Coord2d>(4);
		zmapping.add( new Coord2d(position.x-width, position.y-width) );
		zmapping.add( new Coord2d(position.x+width, position.y-width) );
		zmapping.add( new Coord2d(position.x+width, position.y+width) );
		zmapping.add( new Coord2d(position.x-width, position.y+width) );
		
		List<Coord2d> ymapping  = new ArrayList<Coord2d>(4);
		ymapping.add( new Coord2d(position.x-width, position.z-width) );
		ymapping.add( new Coord2d(position.x+width, position.z-width) );
		ymapping.add( new Coord2d(position.x+width, position.z+width) );
		ymapping.add( new Coord2d(position.x-width, position.z+width) );
		
		List<Coord2d> xmapping  = new ArrayList<Coord2d>(4);
		xmapping.add( new Coord2d(position.y-width, position.z-width) );
		xmapping.add( new Coord2d(position.y+width, position.z-width) );
		xmapping.add( new Coord2d(position.y+width, position.z+width) );
		xmapping.add( new Coord2d(position.y-width, position.z+width) );
		
		northBg  = new DrawableTexture(masks.bgMask, PlaneAxis.Z, position.z + width, zmapping, bgcolor);
		southBg  = new DrawableTexture(masks.bgMask, PlaneAxis.Z, position.z - width, zmapping, bgcolor);
		westBg  = new DrawableTexture(masks.bgMask, PlaneAxis.X, position.x - width, xmapping, bgcolor);
		eastBg  = new DrawableTexture(masks.bgMask, PlaneAxis.X, position.x + width, xmapping, bgcolor);
		nearBg  = new DrawableTexture(masks.bgMask, PlaneAxis.Y, position.y - width, ymapping, bgcolor);
		farBg  = new DrawableTexture(masks.bgMask, PlaneAxis.Y, position.y + width, ymapping, bgcolor);
		
		north  = new DrawableTexture(masks.symbolMask, PlaneAxis.Z, position.z + width, zmapping, color);
		south  = new DrawableTexture(masks.symbolMask, PlaneAxis.Z, position.z - width, zmapping, color);
		west  = new DrawableTexture(masks.symbolMask, PlaneAxis.X, position.x - width, xmapping, color);
		east  = new DrawableTexture(masks.symbolMask, PlaneAxis.X, position.x + width, xmapping, color);
		near  = new DrawableTexture(masks.symbolMask, PlaneAxis.Y, position.y - width, ymapping, color);
		far  = new DrawableTexture(masks.symbolMask, PlaneAxis.Y, position.y + width, ymapping, color);
				
		add(north);
		add(south);
		add(west);
		add(east);
		add(near);
		add(far);
		
		add(northBg);
		add(southBg);
		add(westBg);
		add(eastBg);
		add(nearBg);
		add(farBg);
		
		bbox = new BoundingBox3d(position, width*2);
	}
	
	public BoundingBox3d getBounds(){
		return bbox;
	}
	
	@Override
	public void project(GL2 gl, GLU glu, Camera cam) {
		lastProjection = cam.modelToScreen( gl, glu, getBounds().getVertices() );
		lastHull = ConvexHull.hull(lastProjection);
	}
	
	@Override
	public java.awt.Polygon getHull2d() {
		return lastHull;
	}
	
	@Override
	public List<Coord3d> getLastProjection(){
		return lastProjection;
	}
	
	@Override
	public void setAlphaFactor(float a) {
		alpha = a;
		
		((DrawableTexture)northBg).setAlphaFactor( alpha );
		((DrawableTexture)southBg).setAlphaFactor( alpha );
		((DrawableTexture)westBg).setAlphaFactor( alpha );
		((DrawableTexture)eastBg).setAlphaFactor( alpha );
		((DrawableTexture)nearBg).setAlphaFactor( alpha );
		((DrawableTexture)farBg).setAlphaFactor( alpha );
		
		((DrawableTexture)north).setAlphaFactor( alpha );
		((DrawableTexture)south).setAlphaFactor( alpha );
		((DrawableTexture)west).setAlphaFactor( alpha );
		((DrawableTexture)east).setAlphaFactor( alpha );
		((DrawableTexture)near).setAlphaFactor( alpha );
		((DrawableTexture)far).setAlphaFactor( alpha );
	}
	
	protected AbstractDrawable north;
	protected AbstractDrawable south;
	protected AbstractDrawable west;
	protected AbstractDrawable east;
	protected AbstractDrawable near;
	protected AbstractDrawable far;
	
	protected AbstractDrawable northBg;
	protected AbstractDrawable southBg;
	protected AbstractDrawable westBg;
	protected AbstractDrawable eastBg;
	protected AbstractDrawable nearBg;
	protected AbstractDrawable farBg;
	
	protected List<Coord3d> lastProjection;
	protected java.awt.Polygon lastHull;
	
	protected float alpha;
}
