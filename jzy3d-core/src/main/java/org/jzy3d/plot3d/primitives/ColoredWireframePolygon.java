package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.Painter;

import com.jogamp.opengl.GL2GL3;

public class ColoredWireframePolygon extends Polygon {

	
	@Override
    public void draw(Painter painter) {
        doTransform(painter);

        if (mapper != null)
            mapper.preDraw(this);

        // Draw content of polygon
        if (facestatus) {
			painter.glPolygonMode(polygonMode, PolygonFill.FILL);

            if (wfstatus && polygonOffsetFillEnable)
                polygonOffseFillEnable(painter);
            callPointsForFace(painter);
            if (wfstatus && polygonOffsetFillEnable)
                polygonOffsetFillDisable(painter);
        }

        // Draw edge of polygon
        if (wfstatus) {
			painter.glPolygonMode(polygonMode, PolygonFill.LINE);

			if (polygonOffsetFillEnable)
            	polygonOffsetLineEnable(painter);
            callPointForWireframe(painter);
            if (polygonOffsetFillEnable)
            	polygonOffsetLineDisable(painter);
        }

        if (mapper != null)
            mapper.postDraw(this);

        doDrawBoundsIfDisplayed(painter);
    }
	
    protected void polygonOffsetLineEnable(Painter painter) {
		painter.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
		painter.glPolygonOffset(polygonOffsetFactor, polygonOffsetUnit);
    }

    protected void polygonOffsetLineDisable(Painter painter) {
    	painter.glDisable(GL2GL3.GL_POLYGON_OFFSET_LINE);
    }
    
    @Override
    public void callPointForWireframe(Painter painter) {
        painter.glLineWidth(wfwidth);
        Color c = wfcolor;
        
        begin(painter);
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
