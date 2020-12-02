package org.jzy3d.plot3d.primitives.axes;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.axis.AxeAnnotation;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;

import jgl.GL;

public class EmulGLAxisBox extends AxisBox {
	public EmulGLAxisBox(BoundingBox3d bbox, IAxisLayout layout) {
		super(bbox, layout);
	}

	public EmulGLAxisBox(BoundingBox3d bbox) {
		super(bbox);
	}

	@Override
	public void draw(Painter painter) {
		
		updateHiddenQuads(painter);

		doTransform(painter);

		if (getQuadIsHidden() == null)
			return;

		// Draw faces
		painter.glPolygonMode(GL.GL_BACK, GL.GL_LINE);
		painter.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);

		painter.glColor4f(0, 0, 0, 1);

		for (int q = 0; q < 6; q++) {
			if (!getQuadIsHidden()[q]) {
				painter.glBegin_LineLoop(); //weird : left the triangle

				for (int v = 0; v < 4; v++) {
					painter.glVertex3f(getQuadX()[q][v], getQuadY()[q][v], getQuadZ()[q][v]);
				}
				painter.glEnd();
			}
		}

		// Draw Grid on quad

		painter.glLineStipple(1, (short) 0xAAAA);
		painter.glEnable_LineStipple();
		
        /*for (int quad = 0; quad < 6; quad++)
            if (!quadIsHidden[quad])
                drawGridOnQuad(painter, gl, quad);*/


		for (int quad = 0; quad < 6; quad++) {
			if (!getQuadIsHidden()[quad]) {
				
				// Draw X grid along X axis
		        if ((quad != 0) && (quad != 1)) {
		            double[] xticks = layout.getXTicks();
		            for (int t = 0; t < xticks.length; t++) {
		                painter.glBegin(GL.GL_LINES); //JOGL CONSTANT VALUE 2 -> THIS REQUIRES 1, recognized by JGL
		                painter.vertex((float) xticks[t], quady[quad][0], quadz[quad][0], spaceTransformer);
		                painter.vertex((float) xticks[t], quady[quad][2], quadz[quad][2], spaceTransformer);
		                painter.glEnd();
		            }
		        }
		        // Draw Y grid along Y axis
		        if ((quad != 2) && (quad != 3)) {
		            double[] yticks = layout.getYTicks();
		            for (int t = 0; t < yticks.length; t++) {
		                painter.glBegin(GL.GL_LINES);
		                painter.vertex(quadx[quad][0], (float) yticks[t], quadz[quad][0], spaceTransformer);
		                painter.vertex(quadx[quad][2], (float) yticks[t], quadz[quad][2], spaceTransformer);
		                painter.glEnd();
		            }
		        }
		        // Draw Z grid along Z axis
		        if ((quad != 4) && (quad != 5)) {
		            double[] zticks = layout.getZTicks();
		            for (int t = 0; t < zticks.length; t++) {
		                painter.glBegin(GL.GL_LINES);
		                painter.vertex(quadx[quad][0], quady[quad][0], (float) zticks[t], spaceTransformer);
		                painter.vertex(quadx[quad][2], quady[quad][2], (float) zticks[t], spaceTransformer);
		                painter.glEnd();
		            }
		        }
			}
		}
		
		painter.glDisable_LineStipple();

		
        synchronized (annotations) {
            for (AxeAnnotation a : annotations) {
                a.draw(painter, this);
            }
        }

        doTransform(painter);
        
        // Execute this to collect text to draw
        drawTicksAndLabels(painter);

		
		// Text of axis
		synchronized (ticks) {
			for (Tick tick : ticks) {
				// Coord3d screen = modelToScreen(tick.position);
				// glut.glutBitmapString(axisFont, tick.label, (int) screen.x,
				// canvas.getHeight() - (int) screen.y);
				Color c = getLayout().getXTickColor();
				Coord3d p = tick.position;
				painter.glutBitmapString(axisFont, tick.label, p, c);
			}
		}
	}
	
	public class Tick{
	  public String label;
	  public Coord3d position;
	}


	/* ************************************************************* */
	
	/* ********************** TEXT MANAGEMENT ********************** */

	/* ************************************************************* */	
	
	
	
	Font axisFont = new Font("Arial", Font.PLAIN, 12);

	/** Override default behaviour to replace direct text rendering by tick labels collection for later rendering.*/
	@Override
	public void drawAxisTickNumericLabel(Painter painter, int direction, Color color,
			Halign hAlign, Valign vAlign, BoundingBox3d ticksTxtBounds, String tickLabel, Coord3d tickPosition) {

		//super.drawAxisTickNumericLabel(painter, direction, color, hAlign, vAlign, ticksTxtBounds, tickLabel, tickPosition);

		Tick t = new Tick();
		t.label = tickLabel;
		t.position = tickPosition;

		synchronized (ticks) {
			ticks.add(t);
		}
	}

	// invoke tick and axe label rendering
	@Override
	public void drawTicksAndLabels(Painter painter) {
		synchronized (ticks) {
			ticks.clear();
		}
		super.drawTicksAndLabels(painter);
	}
	
	// prevent invoking original text renderer
	public void drawAxisLabel(Painter painter, int direction, Color color, BoundingBox3d ticksTxtBounds, double xlab, double ylab, double zlab, String axeLabel) {
       // super.drawAxisLabel(painter, direction, color, ticksTxtBounds, xlab, ylab, zlab, axeLabel);
    }

	List<Tick> ticks = new ArrayList<>();

}
