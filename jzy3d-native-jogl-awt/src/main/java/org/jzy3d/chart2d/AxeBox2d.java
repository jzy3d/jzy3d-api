package org.jzy3d.chart2d;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;

public class AxeBox2d extends AxeBox {
    public AxeBox2d(BoundingBox3d bbox, IAxeLayout layout) {
        super(bbox, layout);
    }

    public AxeBox2d(BoundingBox3d bbox) {
        super(bbox);
        txt = new TextBitmapRenderer();
        //txt = new JOGLTextRenderer(new DefaultTextStyle(java.awt.Color.BLACK));// ATTENTION AWT!!
    }

    /* */

    /**
     * Renders only X and Y ticks.
     */
    @Override
    public void drawTicksAndLabels(GL gl, GLU glu, Camera camera) {
        wholeBounds.reset();
        wholeBounds.add(boxBounds);

        drawTicksAndLabelsX(gl, glu, camera);
        drawTicksAndLabelsY(gl, glu, camera);
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
    public void drawAxisLabel(GL gl, GLU glu, Camera cam, int direction, Color color, BoundingBox3d ticksTxtBounds, double xlab, double ylab, double zlab, String axeLabel) {
        Coord3d labelPosition = new Coord3d(xlab, ylab, zlab);
        BoundingBox3d labelBounds = null;

        if (isXDisplayed(direction)) {
//            doTransform(gl);
            labelBounds = txt.drawText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);
        } else if (isYDisplayed(direction)) {
            labelBounds = txt.drawText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);
            //labelBounds = txtRotation.drawText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);
        }
        if (labelBounds != null)
            ticksTxtBounds.add(labelBounds);
        doTransform(gl);

    }

    /* VERTICAL ROTATION */
    
    protected RotatedTextBitmapRenderer txtRotation = new RotatedTextBitmapRenderer();

    /* ROTATED TEXT BITMAP RENDERER NOT WORKING PROPERLY */
    
    public class RotatedTextBitmapRenderer extends TextBitmapRenderer {
        @Override
        public BoundingBox3d drawText(GL gl, GLU glu, Camera cam, String text, Coord3d position, Halign halign, Valign valign, Color color, Coord2d screenOffset,
                Coord3d sceneOffset) {
            color(gl, color);

            // compute a corrected position according to layout
            Coord3d posScreen = cam.modelToScreen(gl, glu, position);
            float strlen = glut.glutBitmapLength(font, text);
            float x = computeXWithAlign(halign, posScreen, strlen, 0.0f);
            float y = computeYWithAlign(valign, posScreen, 0.0f);
            Coord3d posScreenShifted = new Coord3d(x + screenOffset.x, y + screenOffset.y, posScreen.z);

            Coord3d posReal;
            try {
                posReal = cam.screenToModel(gl, glu, posScreenShifted);
            } catch (RuntimeException e) {
                Logger.getLogger(TextBitmapRenderer.class).error("TextBitmap.drawText(): could not process text position: " + posScreen + " " + posScreenShifted);
                return new BoundingBox3d();
            }

            // Draws actual string <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            rotateText(gl, posReal); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<
            // CETTE ROTATION NE MARCHE PAS ET AFFECTE LE BON RENDU QUAND ON UTILISE BOUNDING POLICY!!
            
            glRasterPos(gl, sceneOffset, Coord3d.ORIGIN);
            glut.glutBitmapString(font, text);
            
            return computeTextBounds(gl, glu, cam, posScreenShifted, strlen);
        }

        // CUSTOM ROTATION
        
        public void rotateText(GL gl, Coord3d posReal) {
            gl.getGL2().glPushMatrix();
            
            gl.getGL2().glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
            loadIdentity(gl);
            rotateOf(gl, 90, AXE_Z);
            translateTo(gl, posReal, false);
            gl.getGL2().glScalef(scale.x, scale.y, scale.z);
            
            gl.getGL2().glPopMatrix();
        }

        public void loadIdentity(GL gl) {
            gl.getGL2().glLoadIdentity();
        }
        
        public void rotateOf(GL gl, double angle, int axis) {
            gl.getGL2().glRotatef((float) angle, 0, 0, 1);
        }

        public void translateTo(GL gl, Coord3d position, boolean reverse) {
            if (gl.isGLES()) {
                float reverseCoef = (reverse ? -1.0f : 1.0f);
                GLES2CompatUtils.glTranslatef(reverseCoef * position.x, reverseCoef * position.y, reverseCoef * position.z);
            } else {
                if (reverse)
                    gl.getGL2().glTranslatef(-position.x, -position.y, -position.z);
                else
                    gl.getGL2().glTranslatef(position.x, position.y / 2, position.z);
            }
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
