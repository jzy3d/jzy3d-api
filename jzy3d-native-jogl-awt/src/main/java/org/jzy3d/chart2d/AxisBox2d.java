package org.jzy3d.chart2d;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

public class AxisBox2d extends AxisBox {
    public AxisBox2d(BoundingBox3d bbox, IAxisLayout layout) {
        super(bbox, layout);
    }

    public AxisBox2d(BoundingBox3d bbox) {
        super(bbox);
        textRenderer = new TextBitmapRenderer();
        //txt = new JOGLTextRenderer(new DefaultTextStyle(java.awt.Color.BLACK));// ATTENTION AWT!!
    }

    /* */

    /**
     * Renders only X and Y ticks.
     */
    @Override
    public void drawTicksAndLabels(Painter painter) {
        wholeBounds.reset();
        wholeBounds.add(boxBounds);

        drawTicksAndLabelsX(painter);
        drawTicksAndLabelsY(painter);
    }

    /** Force given X axis to be used for tick placement */
    @Override
    protected int findClosestXaxe(Camera cam) {
        return 0;
    }

    /** Force given Y axis to be used for tick placement */
    @Override
    protected int findClosestYaxe(Camera cam) {
        return 3;
    }

    @Override
    public Halign layoutHorizontal(int direction, Camera cam, Halign hal, Coord3d tickPosition) {
        if (direction == AXE_X) {
            return Halign.CENTER;
        } else if (direction == AXE_Y) {
            return Halign.LEFT;
        } else {
            return Halign.LEFT;
        }
    }

    /** Draws Y axis label vertically. */
    @Override
    public void drawAxisLabel(Painter painter, int direction, Color color, BoundingBox3d ticksTxtBounds, double xlab, double ylab, double zlab, String axeLabel) {
        Coord3d labelPosition = new Coord3d(xlab, ylab, zlab);
        BoundingBox3d labelBounds = null;

        if (isXDisplayed(direction)) {
//            doTransform(gl);
            labelBounds = textRenderer.drawText(painter, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);
        } else if (isYDisplayed(direction)) {
            labelBounds = textRenderer.drawText(painter, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);
            //labelBounds = txtRotation.drawText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);
        }
        if (labelBounds != null)
            ticksTxtBounds.add(labelBounds);
        doTransform(painter);
    }

    /* VERTICAL ROTATION */
    
    protected RotatedTextBitmapRenderer txtRotation = new RotatedTextBitmapRenderer();

    /* ROTATED TEXT BITMAP RENDERER NOT WORKING PROPERLY */
    
    public class RotatedTextBitmapRenderer extends TextBitmapRenderer {
        @Override
        public BoundingBox3d drawText(Painter painter, String text, Coord3d position, Halign halign, Valign valign, Color color, Coord2d screenOffset, Coord3d sceneOffset) {
            painter.color(color);

            // compute a corrected position according to layout
            Coord3d posScreen = painter.getCamera().modelToScreen(painter, position);
            float strlen = painter.glutBitmapLength(font.getCode(), text);
            float x = computeXAlign(halign, posScreen, strlen, 0.0f);
            float y = computeYAlign(valign, posScreen, 0.0f);
            Coord3d posScreenShifted = new Coord3d(x + screenOffset.x, y + screenOffset.y, posScreen.z);

            Coord3d posReal;
            try {
                posReal = painter.getCamera().screenToModel(painter, posScreenShifted);
            } catch (RuntimeException e) {
                Logger.getLogger(TextBitmapRenderer.class).error("TextBitmap.drawText(): could not process text position: " + posScreen + " " + posScreenShifted);
                return new BoundingBox3d();
            }

            // Draws actual string <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            rotateText(painter, posReal); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<
            // CETTE ROTATION NE MARCHE PAS ET AFFECTE LE BON RENDU QUAND ON UTILISE BOUNDING POLICY!!
            
            glRasterPos(painter, sceneOffset, Coord3d.ORIGIN);
            painter.glutBitmapString(font.getCode(), text);
            
            return computeTextBounds(painter, posScreenShifted, strlen);
        }

        // CUSTOM ROTATION
        
        public void rotateText(Painter painter, Coord3d posReal) {
            painter.glPushMatrix();
            
            painter.glMatrixMode_ModelView();
            painter.glLoadIdentity();
            painter.glRotatef(90, 0, 0, 1);
            //rotateOf(gl, 90, AXE_Z);
            //translateTo(gl, posReal, false);
            
            if (false)
            	painter.glTranslatef(-posReal.x, -posReal.y, -posReal.z);
            else
            	painter.glTranslatef(posReal.x, posReal.y / 2, posReal.z);
            
            painter.glScalef(scale.x, scale.y, scale.z);
            painter.glPopMatrix();
        }
    }
    

    /*public void vertical(GL gl, Coord3d currentPosition, int direction) {
        translateTo(gl, currentPosition, false);
        rotateOf(gl, 90, direction);
        translateTo(gl, currentPosition, true);
    }

    public void verticalSimple(GL gl) {
        gl.getGL2().glRotatef(90, 0, 0, 1);
    }*/
}
