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
        doTransform(painter, cam);

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
            	polygonOffsetLineEnable(painter, gl);
            callPointForWireframe(painter, gl);
            if (polygonOffsetFillEnable)
            	polygonOffsetLineDisable(painter, gl);
        }

        if (mapper != null)
            mapper.postDraw(this);

        doDrawBoundsIfDisplayed(painter, gl, glu, cam);
    }
	
    protected void polygonOffsetLineEnable(Painter painter, GL gl) {
		painter.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
		painter.glPolygonOffset(polygonOffsetFactor, polygonOffsetUnit);
    }

    protected void polygonOffsetLineDisable(Painter painter, GL gl) {
    	painter.glDisable(GL2GL3.GL_POLYGON_OFFSET_LINE);
    }
    
    @Override
    public void callPointForWireframe(Painter painter, GL gl) {
        painter.glLineWidth(wfwidth);
        Color c = wfcolor;
        
        begin(painter, gl);
        for (Point p : points) {
        	if (mapper != null) {
        		c = mapper.getColor(p.getCoord().z);
        		painter.color(c);
        	}
            painter.vertex(p.xyz, spaceTransformer);
        }
        painter.glEnd();
    }
	
}
