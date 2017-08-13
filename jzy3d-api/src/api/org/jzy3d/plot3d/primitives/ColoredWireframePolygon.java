package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.rendering.view.Camera;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.glu.GLU;

public class ColoredWireframePolygon extends Polygon {

	
	@Override
    public void draw(GL gl, GLU glu, Camera cam) {
        doTransform(gl, glu, cam);

        if (mapper != null)
            mapper.preDraw(this);

        // Draw content of polygon
        if (facestatus) {
            applyPolygonModeFill(gl);
            if (wfstatus && polygonOffsetFillEnable)
                polygonOffseFillEnable(gl);
            callPointsForFace(gl);
            if (wfstatus && polygonOffsetFillEnable)
                polygonOffsetFillDisable(gl);
        }

        // Draw edge of polygon
        if (wfstatus) {
            applyPolygonModeLine(gl);
            if (polygonOffsetFillEnable)
            	polygonOffsetLineEnable(gl);
            callPointForWireframe(gl);
            if (polygonOffsetFillEnable)
            	polygonOffsetLineDisable(gl);
        }

        if (mapper != null)
            mapper.postDraw(this);

        doDrawBounds(gl, glu, cam);
    }
	
    protected void polygonOffsetLineEnable(GL gl) {
    	if (gl.isGL2GL3()) {
    		gl.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
    		gl.glPolygonOffset(polygonOffsetFactor, polygonOffsetUnit);
    	}
    }

    protected void polygonOffsetLineDisable(GL gl) {
        if (gl.isGL2GL3()) {
        	gl.glDisable(GL2GL3.GL_POLYGON_OFFSET_LINE);
        }
    }
    
    @Override
    public void callPointsForWireframeGL2(GL gl) {
        gl.glLineWidth(wfwidth);
        Color c = wfcolor;
        
        begin(gl);
        for (Point p : points) {
        	if (mapper != null) c = mapper.getColor(p.getCoord().z);
        	colorGL2(gl, c);
            vertexGL2(gl, p.xyz);
        }
        end(gl);
    }
	
}
