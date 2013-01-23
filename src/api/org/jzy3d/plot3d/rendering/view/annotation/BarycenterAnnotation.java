package org.jzy3d.plot3d.rendering.view.annotation;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.AbstractComposite;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.AbstractGeometry;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.rendering.scene.Decomposition;
import org.jzy3d.plot3d.rendering.view.Camera;

/**
 * Draws the barycenter of an {@link AbstractGeometry}
 * and a line each point and the barycenter.
 * 
 * @author Martin
 */
public class BarycenterAnnotation extends AbstractComposite{
    public BarycenterAnnotation(AbstractGeometry annotated) {
        Color c = Color.BLACK;
        
        this.annotated = annotated;
        bary = new Point();
        bary.setWidth(5);
        lines = new ArrayList<LineStrip>();
        
        for(Point pt: annotated.getPoints()){
            Point b2 = bary.clone();
            Point pt2 = pt.clone();
            
            LineStrip line = new LineStrip(b2, pt2);
            line.setWireframeColor(c);
            lines.add(line);
        }
    
        add(bary);
        add(lines);

        setColor(c);
        setWireframeColor(c);
    }

    @Override
    public void draw(GL2 gl, GLU glu, Camera camera) {
        bary.xyz = annotated.getBarycentre();
        int k = 0;
        for(LineStrip line: lines){
            line.get(0).xyz = bary.xyz.clone();
                line.get(1).xyz = annotated.get(k).xyz.clone();
            k++;
        }
        super.draw(gl, glu, camera);
    }

    public static List<BarycenterAnnotation> annotate(AbstractComposite composite){
        List<BarycenterAnnotation> annotations = new ArrayList<BarycenterAnnotation>();
        
        ArrayList<AbstractDrawable> items= Decomposition.getDecomposition(composite);
        for(AbstractDrawable item: items){
            if(item instanceof AbstractGeometry)
                annotations.add(new BarycenterAnnotation((AbstractGeometry)item));
        }
        return annotations;
    }
    
    protected AbstractGeometry annotated;
    protected Point bary;
    protected List<LineStrip> lines;
}
