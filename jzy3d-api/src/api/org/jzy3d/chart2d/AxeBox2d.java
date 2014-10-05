package org.jzy3d.chart2d;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;

public class AxeBox2d extends AxeBox{
    public AxeBox2d(BoundingBox3d bbox, IAxeLayout layout) {
        super(bbox, layout);
    }
    public AxeBox2d(BoundingBox3d bbox) {
        super(bbox);
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
    
    /** Force given X axis to be used for tick placement*/
    protected int findClosestXaxe(Camera cam){
        return 0;
    }

    /** Force given Y axis to be used for tick placement*/
    protected int findClosestYaxe(Camera cam){
        return 3;
    }
    
    @Override
    public Halign layoutHorizontal(int direction, Camera cam, Halign hal, Coord3d tickPosition) {
        if(direction==AXE_X){
            return Halign.CENTER;
        }
        else if(direction==AXE_Y){
            return Halign.LEFT;
        }
        else{
            return Halign.LEFT;
        }
    }
    
    /** Draws Y axis label vertically. */
    @Override
    public void drawAxisLabel(GL gl, GLU glu, Camera cam, int direction, Color color, BoundingBox3d ticksTxtBounds, double xlab, double ylab, double zlab, String axeLabel) {
        Coord3d labelPosition = new Coord3d(xlab, ylab, zlab);
        BoundingBox3d labelBounds = null;
        
        if (isXDisplayed(direction)) {
            doTransform(gl);
            labelBounds = txt.drawText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);
        }
        else if (isYDisplayed(direction)) {
            doTransform(gl);
            vertical(gl, labelPosition, direction);
            labelBounds = txt.drawText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);
            vertical(gl, labelPosition, direction);          
//            gl.getGL2().glRotatef(90, 1, 1, 0);
        }
        if (labelBounds != null)
            ticksTxtBounds.add(labelBounds);
    }
    
    /* */
    
    public void vertical(GL gl, Coord3d currentPosition, int direction){
        translateTo(gl, currentPosition, false);
        rotateOf(gl, Math.PI, direction);
        translateTo(gl, currentPosition, true);
    }
    
    public void rotateOf(GL gl, double angle, int axis){
        gl.getGL2().glRotatef((float)angle, 1, 1, 1);
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

    
    /* */
}
