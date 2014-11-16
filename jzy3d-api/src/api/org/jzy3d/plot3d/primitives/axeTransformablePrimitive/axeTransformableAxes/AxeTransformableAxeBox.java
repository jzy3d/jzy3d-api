package org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformableAxes;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.GlVertexExecutor;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformer;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.layout.AxeBoxLayout;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;

/**
 * The AxeBox displays a box with front face invisible and ticks labels.
 * 
 * @author Martin Pernollet
 */
public class AxeTransformableAxeBox extends AxeBox {
    public AxeTransformableAxeBox(BoundingBox3d bbox,AxeTransformer transformerX, AxeTransformer transformerY, AxeTransformer transformerZ) {
        this(bbox, new AxeBoxLayout(),transformerX, transformerY, transformerZ);
    }

    public AxeTransformableAxeBox(BoundingBox3d bbox, IAxeLayout layout, AxeTransformer transformerX, AxeTransformer transformerY, AxeTransformer transformerZ) {
        super(bbox,layout);
        this.transformerX = transformerX;
        this.transformerY = transformerY;
        this.transformerZ = transformerZ;
    }

    /** reset to identity and apply scaling */
    public void doTransform(GL gl) {
        if (gl.isGL2()) {
            gl.getGL2().glLoadIdentity();
            //gl.getGL2().glScalef((float)Math.log(scale.x), scale.y, scale.z);
            gl.getGL2().glScalef(scale.x, scale.y, scale.z);
        } else {
            GLES2CompatUtils.glLoadIdentity();
            GLES2CompatUtils.glScalef(scale.x, scale.y, scale.z);
        }
    }
    
    /*protected boolean[] getHiddenQuads(GL gl, Camera cam) {
        boolean[] status = new boolean[6];
        return status;
    }*/
    
    /**
     * Make all GL2 calls allowing to build a cube with 6 separate quads. Each
     * quad is indexed from 0.0f to 5.0f using glPassThrough, and may be traced
     * in feedback mode when mode=GL2.GL_FEEDBACK
     */
    protected void drawCube(GL gl, int mode) {
        for (int q = 0; q < 6; q++) {
            if (gl.isGL2()) {
                if (mode == GL2.GL_FEEDBACK)
                    gl.getGL2().glPassThrough((float) q);
                gl.getGL2().glBegin(GL2.GL_QUADS);
                for (int v = 0; v < 4; v++) {
                    Coord3d c3d = new Coord3d(quadx[q][v], quady[q][v], quadz[q][v]); //era qua
                    
                    //System.out.println(c3d.x);
                    //System.out.println(Math.log(c3d.x));
                    
                    GlVertexExecutor.Vertex(gl, c3d, transformerX, transformerY, transformerZ);
                }
                gl.getGL2().glEnd();
            } else {
                if (mode == GL2.GL_FEEDBACK)
                    GLES2CompatUtils.glPassThrough((float) q);
                GLES2CompatUtils.glBegin(GL2.GL_QUADS);
                for (int v = 0; v < 4; v++) {
                    Coord3d c3d = new Coord3d(quadx[q][v], quady[q][v], quadz[q][v]);
                    GlVertexExecutor.Vertex(gl, c3d, transformerX, transformerY, transformerZ);
                }
                GLES2CompatUtils.glEnd();
            }

        }
    }

    /**
     * Draw a grid on the desired quad.
     */
    protected void drawGridOnQuad(GL gl, int quad) {
        // Draw X grid along X axis
        if ((quad != 0) && (quad != 1)) {
            double[] xticks = layout.getXTicks();
            for (int t = 0; t < xticks.length; t++) {
                if (gl.isGL2()) {
                    gl.getGL2().glBegin(GL2.GL_LINES);
                    Coord3d c3d = new Coord3d(xticks[t], quady[quad][0], quadz[quad][0]);
                    GlVertexExecutor.Vertex(gl, c3d, transformerX, transformerY, transformerZ);
                    c3d = new Coord3d(xticks[t], quady[quad][2], quadz[quad][2]);
                    GlVertexExecutor.Vertex(gl, c3d, transformerX, transformerY, transformerZ);
                    gl.getGL2().glEnd();
                } else {
                    // FIXME TO BE REWRITTEN ANDROID
                }
            }
        }
        // Draw Y grid along Y axis
        if ((quad != 2) && (quad != 3)) {
            double[] yticks = layout.getYTicks();
            for (int t = 0; t < yticks.length; t++) {
                if (gl.isGL2()) {
                    gl.getGL2().glBegin(GL2.GL_LINES);
                    Coord3d c3d = new Coord3d(quadx[quad][0], yticks[t], quadz[quad][0]);
                    GlVertexExecutor.Vertex(gl, c3d, transformerX, transformerY, transformerZ);
                    c3d = new Coord3d(quadx[quad][2], yticks[t], quadz[quad][2]);
                    GlVertexExecutor.Vertex(gl, c3d, transformerX, transformerY, transformerZ);
                    gl.getGL2().glEnd();
                } else {
                    // FIXME TO BE REWRITTEN ANDROID
                }
            }
        }
        // Draw Z grid along Z axis
        if ((quad != 4) && (quad != 5)) {
            double[] zticks = layout.getZTicks();
            for (int t = 0; t < zticks.length; t++) {
                if (gl.isGL2()) {
                    gl.getGL2().glBegin(GL2.GL_LINES);
                    Coord3d c3d = new Coord3d(quadx[quad][0], quady[quad][0], zticks[t]);
                    GlVertexExecutor.Vertex(gl, c3d, transformerX, transformerY, transformerZ);
                    c3d = new Coord3d(quadx[quad][2], quady[quad][2], zticks[t]);
                    GlVertexExecutor.Vertex(gl, c3d, transformerX, transformerY, transformerZ);
                    gl.getGL2().glEnd();
                } else {
                    // FIXME TO BE REWRITTEN ANDROID
                }
            }
        }
    }

    public void drawAxisLabel(GL gl, GLU glu, Camera cam, int direction, Color color, BoundingBox3d ticksTxtBounds, double xlab, double ylab, double zlab, String axeLabel) {
        if (isXDisplayed(direction) || isYDisplayed(direction) || isZDisplayed(direction)) {
            Coord3d labelPosition = new Coord3d(transformerX.compute((float)xlab), transformerY.compute((float)ylab), transformerZ.compute((float)zlab));
            BoundingBox3d labelBounds = txt.drawText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);
            if (labelBounds != null)
                ticksTxtBounds.add(labelBounds);
        }
    }

    public void drawAxisTickNumericLabel(GL gl, GLU glu, int direction, Camera cam, Color color, Halign hAlign, Valign vAlign, BoundingBox3d ticksTxtBounds, String tickLabel,
            Coord3d tickPosition) {
        //doTransform(gl);
        tickPosition = new Coord3d(transformerX.compute(tickPosition.x), transformerY.compute(tickPosition.y), transformerZ.compute(tickPosition.z));
        BoundingBox3d tickBounds = txt.drawText(gl, glu, cam, tickLabel, tickPosition, hAlign, vAlign, color);
        if (tickBounds != null)
            ticksTxtBounds.add(tickBounds);
    }

    public void drawTickLine(GL gl, Color color, double xpos, double ypos, double zpos, double xlab, double ylab, double zlab) {
        gl.getGL2().glColor3f(color.r, color.g, color.b);
        gl.getGL2().glLineWidth(1);

        // Draw the tick line
        gl.getGL2().glBegin(GL2.GL_LINES);
        Coord3d c3d = new Coord3d(xpos, ypos, zpos);
        GlVertexExecutor.Vertex(gl, c3d, transformerX, transformerY, transformerZ);
        c3d = new Coord3d(xlab, ylab, zlab);
        GlVertexExecutor.Vertex(gl, c3d, transformerX, transformerY, transformerZ);
        gl.getGL2().glEnd();
    }

    protected AxeTransformer transformerX;
    protected AxeTransformer transformerY;
    protected AxeTransformer transformerZ;
}