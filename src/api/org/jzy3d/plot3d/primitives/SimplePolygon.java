package org.jzy3d.plot3d.primitives;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.plot3d.rendering.view.Camera;



/** 
 * A {@link SimplePolygon} makes the simplest possible GL rendering with especially no:
 * <ul>
 * <li>gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
 * <li>gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
 * </ul>
 * 
 * @author Martin Pernollet
 */
public class SimplePolygon extends Polygon implements ISingleColorable, IMultiColorable{
	public SimplePolygon(){
		super();
	}
	
	/**********************************************************************/
		
	public void draw(GL2 gl, GLU glu, Camera cam){
	    doTransform(gl, glu, cam);
				
		// Draw content of polygon
		if(facestatus){
		    //if(wfstatus)
            //    polygonOffseFillEnable(gl);
            
			gl.glBegin(GL2.GL_POLYGON);
			for(Point p: points){
				if(mapper!=null){
					Color c = mapper.getColor(p.xyz); // TODO: should store result in the point color
					gl.glColor4f(c.r, c.g, c.b, c.a);
					//System.out.println(c);
				}
				else{
					gl.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
				}				
				gl.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
			}
			gl.glEnd();
			//if(wfstatus)
            //    polygonOffsetFillDisable(gl);
		}
		
		// Draw edge of polygon
		if(wfstatus){
			gl.glBegin(GL2.GL_POLYGON);
            gl.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
            gl.glLineWidth(wfwidth);
			for(Point p: points){
				gl.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
			}
			gl.glEnd();
		}	
		
		doDrawBounds(gl, glu, cam);
	}
}
