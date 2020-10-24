package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.rendering.view.Camera;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.glu.GLU;

public class ColoredWireframePolygon extends Polygon {

	
	@Override
    public void draw(Painter painter, GL gl, GLU glu, Camera cam) {
        doTransform(painter, gl, glu, cam);

        if (mapper != null)
            mapper.preDraw(this);

        // Draw content of polygon
        if (facestatus) {
            applyPolygonModeFill(painter, gl);
            if (wfstatus && polygonOffsetFillEnable)
                polygonOffseFillEnable(painter, gl);
            callPointsForFace(painter, gl);
            if (wfstatus && polygonOffsetFillEnable)
                polygonOffsetFillDisable(painter, gl);
        }

        // Draw edge of polygon
        if (wfstatus) {
            applyPolygonModeLine(painter, gl);
            if (polygonOffsetFillEnable)
            	polygonOffsetLineEnable(gl);
            callPointForWireframe(painter, gl);
            if (polygonOffsetFillEnable)
            	polygonOffsetLineDisable(gl);
        }

        if (mapper != null)
            mapper.postDraw(this);

        doDrawBounds(painter, gl, glu, cam);
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
    public void callPointForWireframe(Painter painter, GL gl) {
        gl.glLineWidth(wfwidth);
        Color c = wfcolor;
        
        begin(gl);
        for (Point p : points) {
        	if (mapper != null) c = mapper.getColor(p.getCoord().z);
            painter.vertex(p.xyz, spaceTransformer);
        }
        painter.glEnd();
    }
	
}
