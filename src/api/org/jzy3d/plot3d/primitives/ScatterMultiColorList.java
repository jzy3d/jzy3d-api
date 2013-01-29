package org.jzy3d.plot3d.primitives;

import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axes.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.legends.colorbars.ColorbarLegend;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;



/** 
 * A scatter plot supporting a List<Coord3d> as input.
 * 
 * @author Martin Pernollet
 *
 */
public class ScatterMultiColorList extends AbstractDrawable implements IMultiColorable{
    public ScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper){
        this(coordinates, mapper, 1.0f);
    }
    
    public ScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper, float width){
        bbox = new BoundingBox3d();
        setData(coordinates);
        setWidth(width);
        setColorMapper(mapper);
    }
    
    public void clear(){
        coordinates = null;
        bbox.reset();
    }
    
    public void enableColorBar(ITickProvider provider, ITickRenderer renderer){
        setLegend( new ColorbarLegend(this, provider, renderer) );
        setLegendDisplayed(true);
    }
        
    /* */
    
    public void draw(GL2 gl, GLU glu, Camera cam){
        doTransform(gl, glu, cam);
        
        gl.glPointSize(width);      
        gl.glBegin(GL2.GL_POINTS);

        if(coordinates!=null){
            for(Coord3d coord: coordinates){
                Color color = mapper.getColor(coord); // TODO: should store result in the point color
                gl.glColor4f(color.r, color.g, color.b, color.a);
                gl.glVertex3f(coord.x, coord.y, coord.z);
            }
        }
        gl.glEnd();
        
        doDrawBounds(gl, glu, cam);
    }


    @Override
    public void applyGeometryTransform(Transform transform) {
        for(Coord3d c: coordinates){
            c.set(transform.compute(c));
        }
        updateBounds();
    }
    
    @Override
    public void updateBounds() {
        bbox.reset();
        for(Coord3d c: coordinates)
            bbox.add(c);
    }
    
    /* */
    
    /** 
     * Set the coordinates of the point.
     * @param xyz point's coordinates
     */
    public void setData(List<Coord3d> coordinates){
        this.coordinates = coordinates;
        
        bbox.reset();
        for(Coord3d c: coordinates)
            bbox.add(c);
    }
    
    public List<Coord3d> getData(){
        return coordinates;
    }
    
    @Override
    public ColorMapper getColorMapper() {
        return mapper;
    }

    @Override
    public void setColorMapper(ColorMapper mapper) {
        this.mapper = mapper;       
    }
        
    /**
     * Set the width of the point.
     * @param width point's width
     */
    public void setWidth(float width){
        this.width = width;
    }
    
    /**********************************************************************/
    
    protected List<Coord3d> coordinates;
    protected float     width;
    protected ColorMapper mapper;
}





