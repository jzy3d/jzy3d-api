package org.jzy3d.plot3d.primitives.axes;

import java.util.List;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;


/**Specify services that a concrete Axe must provide.*/
public interface IAxe {
	public void dispose();
	
	public void setAxe(BoundingBox3d box);
	public void draw(Painter painter, GL gl, GLU glu, Camera camera);
	
	public void setScale(Coord3d scale);
	public Coord3d getScale();
	public BoundingBox3d getBoxBounds();
	public Coord3d getCenter();
	
	public IAxeLayout getLayout();
	
	public List<AxeAnnotation> getAnnotations();
    public void setAnnotations(List<AxeAnnotation> annotations);

    public SpaceTransformer getSpaceTransformer();
    public void setSpaceTransformer(SpaceTransformer spaceTransformer) ;

    public BoundingBox3d getWholeBounds();
}
