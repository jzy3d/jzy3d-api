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
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Quad;
import org.jzy3d.plot3d.primitives.selectable.Selectable;
import org.jzy3d.plot3d.rendering.view.Camera;


public class TexturedCylinder extends AbstractComposite implements Selectable, ITranslucent{
	public TexturedCylinder(MaskPair masks){
		this(new Coord3d(), Color.CYAN, Color.RED, masks);
	}
	
	public TexturedCylinder(Coord3d position, MaskPair masks){
		this(position, Color.CYAN, Color.RED, masks);
	}
	
	public TexturedCylinder(Coord3d position, Color color, Color bgcolor, MaskPair masks){
		alpha = 1;
		List<Coord2d> mapping  = new ArrayList<Coord2d>(4);
		mapping.add( new Coord2d(position.x-0.5, position.y-0.5) );
		mapping.add( new Coord2d(position.x+0.5, position.y-0.5) );
		mapping.add( new Coord2d(position.x+0.5, position.y+0.5) );
		mapping.add( new Coord2d(position.x-0.5, position.y+0.5) );
		
		dDiskDown  = new DrawableTexture(masks.bgMask, PlaneAxis.Z, position.z - 0.5f, mapping, bgcolor);
		dDiskUp    = new DrawableTexture(masks.bgMask, PlaneAxis.Z, position.z + 0.5f, mapping, bgcolor);
		dArrowDown = new DrawableTexture(masks.symbolMask, PlaneAxis.Z, position.z - 0.5f, mapping, color);
		dArrowUp   = new DrawableTexture(masks.symbolMask, PlaneAxis.Z, position.z + 0.5f, mapping, color);
		
		int slices = 20;
		float radius = 0.5f;
		float height = 1f;
		Coord3d pos = new Coord3d(position.x, position.y, position.z-0.5f);
		quads = new ArrayList<TranslucentQuad>(slices);
		for(int i=0; i<slices; i++){
			float angleBorder1 = (float)i*2*(float)Math.PI/(float)slices;
			float angleBorder2 = (float)(i+1)*2*(float)Math.PI/(float)slices;
			
			Coord2d border1 = new Coord2d(angleBorder1, radius).cartesian();
			Coord2d border2 = new Coord2d(angleBorder2, radius).cartesian();
			
			TranslucentQuad face = new TranslucentQuad();
			face.add(new Point(new Coord3d(pos.x+border1.x, pos.y+border1.y, pos.z)));
			face.add(new Point(new Coord3d(pos.x+border1.x, pos.y+border1.y, pos.z+height)));
			face.add(new Point(new Coord3d(pos.x+border2.x, pos.y+border2.y, pos.z+height)));
			face.add(new Point(new Coord3d(pos.x+border2.x, pos.y+border2.y, pos.z)));
			face.setColor(bgcolor);
			face.setWireframeDisplayed(false);	
			quads.add(face);
		}
		
		add(dDiskDown);
		add(dArrowDown);
		add(dDiskUp);
		add(dArrowUp);
		add(quads);
		
		bbox = new BoundingBox3d();
		
		for (Quad quad : quads) {
			bbox.add(quad);
		}
	}
	
	@Override
	public void setAlphaFactor(float a) {
		alpha = a;
		
		((DrawableTexture)dDiskDown).setAlphaFactor( alpha );
		((DrawableTexture)dArrowDown).setAlphaFactor( alpha );
		((DrawableTexture)dDiskUp).setAlphaFactor( alpha );
		((DrawableTexture)dArrowUp).setAlphaFactor( alpha );
		for(TranslucentQuad q: quads){
			q.setAlphaFactor( alpha );
		}
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
	
	protected AbstractDrawable dArrowUp;
	protected AbstractDrawable dArrowDown;
	protected AbstractDrawable dDiskUp;
	protected AbstractDrawable dDiskDown;
	protected List<TranslucentQuad> quads;
	
	protected List<Coord3d> lastProjection;
	protected java.awt.Polygon lastHull;
	protected float alpha;
}
