package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.Painter;

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
