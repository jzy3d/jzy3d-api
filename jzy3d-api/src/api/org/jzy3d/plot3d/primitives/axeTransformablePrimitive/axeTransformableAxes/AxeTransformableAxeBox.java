package org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformableAxes;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Vector3d;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.GlVertexExecutor;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformer;
import org.jzy3d.plot3d.primitives.axes.AxeAnnotation;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.layout.AxeBoxLayout;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

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
            gl.getGL2().glScalef(scale.x, scale.y, scale.z);
        } else {
            GLES2CompatUtils.glLoadIdentity();
            GLES2CompatUtils.glScalef(scale.x, scale.y, scale.z);
        }
    }


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
                    Coord3d c3d = new Coord3d(quadx[q][v], quady[q][v], quadz[q][v]);
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


    /** Draws axis labels, tick lines and tick label */
    protected BoundingBox3d drawTicks(GL gl, GLU glu, Camera cam, int axis, int direction, Color color, Halign hal, Valign val) {
        int quad_0;
        int quad_1;
        float tickLength = 20.0f; // with respect to range
        float axeLabelDist = 2.5f;
        BoundingBox3d ticksTxtBounds = new BoundingBox3d();

        // Retrieve the quads that intersect and create the selected axe
        if (isX(direction)) {
            quad_0 = axeXquads[axis][0];
            quad_1 = axeXquads[axis][1];
        } else if (isY(direction)) {
            quad_0 = axeYquads[axis][0];
            quad_1 = axeYquads[axis][1];
        } else { // (axis==AXE_Z)
            quad_0 = axeZquads[axis][0];
            quad_1 = axeZquads[axis][1];
        }

        // Computes POSition of ticks lying on the selected axe
        // (i.e. 1st point of the tick line)
        double xpos = normx[quad_0] + normx[quad_1];
        double ypos = normy[quad_0] + normy[quad_1];
        double zpos = normz[quad_0] + normz[quad_1];

        // Computes the DIRection of the ticks
        // assuming initial vector point is the center
        float xdir = (normx[quad_0] + normx[quad_1]) - center.x;
        float ydir = (normy[quad_0] + normy[quad_1]) - center.y;
        float zdir = (normz[quad_0] + normz[quad_1]) - center.z;
        xdir = xdir == 0 ? 0 : xdir / Math.abs(xdir); // so that direction as
                                                      // length 1
        ydir = ydir == 0 ? 0 : ydir / Math.abs(ydir);
        zdir = zdir == 0 ? 0 : zdir / Math.abs(zdir);

        // Variables for storing the position of the LABel position
        // (2nd point on the tick line)
        double xlab;
        double ylab;
        double zlab;

        // Draw the label for axis
        String axeLabel;
        int dist = 1;
        if (isX(direction)) {
            xlab = center.x;
            ylab = axeLabelDist * (yrange / tickLength) * dist * ydir + ypos;
            zlab = axeLabelDist * (zrange / tickLength) * dist * zdir + zpos;
            axeLabel = layout.getXAxeLabel();
        } else if (isY(direction)) {
            xlab = axeLabelDist * (xrange / tickLength) * dist * xdir + xpos;
            ylab = center.y;
            zlab = axeLabelDist * (zrange / tickLength) * dist * zdir + zpos;
            axeLabel = layout.getYAxeLabel();
        } else {
            xlab = axeLabelDist * (xrange / tickLength) * dist * xdir + xpos;
            ylab = axeLabelDist * (yrange / tickLength) * dist * ydir + ypos;
            zlab = center.z;
            axeLabel = layout.getZAxeLabel();
        }

        drawAxisLabel(gl, glu, cam, direction, color, ticksTxtBounds, xlab, ylab, zlab, axeLabel);
        drawAxisTicks(gl, glu, cam, direction, color, hal, val, tickLength, ticksTxtBounds, xpos, ypos, zpos, xdir, ydir, zdir, getAxisTicks(direction));
        return ticksTxtBounds;
    }

    public void drawAxisLabel(GL gl, GLU glu, Camera cam, int direction, Color color, BoundingBox3d ticksTxtBounds, double xlab, double ylab, double zlab, String axeLabel) {
        if (isXDisplayed(direction) || isYDisplayed(direction) || isZDisplayed(direction)) {
            Coord3d labelPosition = new Coord3d(xlab, ylab, zlab);
            BoundingBox3d labelBounds = txt.drawText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);
            if (labelBounds != null)
                ticksTxtBounds.add(labelBounds);
        }
    }


    public void drawAxisTicks(GL gl, GLU glu, Camera cam, int direction, Color color, Halign hal, Valign val, float tickLength, BoundingBox3d ticksTxtBounds, double xpos,
            double ypos, double zpos, float xdir, float ydir, float zdir, double[] ticks) {
        double xlab;
        double ylab;
        double zlab;
        String tickLabel = "";

        for (int t = 0; t < ticks.length; t++) {
            // Shift the tick vector along the selected axis
            // and set the tick length
            if (isX(direction)) {
                xpos = ticks[t];
                xlab = xpos;
                ylab = (yrange / tickLength) * ydir + ypos;
                zlab = (zrange / tickLength) * zdir + zpos;
                tickLabel = layout.getXTickRenderer().format(xpos);
            } else if (isY(direction)) {
                ypos = ticks[t];
                xlab = (xrange / tickLength) * xdir + xpos;
                ylab = ypos;
                zlab = (zrange / tickLength) * zdir + zpos;
                tickLabel = layout.getYTickRenderer().format(ypos);
            } else { // (axis==AXE_Z)
                zpos = ticks[t];
                xlab = (xrange / tickLength) * xdir + xpos;
                ylab = (yrange / tickLength) * ydir + ypos;
                zlab = zpos;
                tickLabel = layout.getZTickRenderer().format(zpos);
            }
            Coord3d tickPosition = new Coord3d(xlab, ylab, zlab);

            if (layout.isTickLineDisplayed()) {
                if (gl.isGL2()) {
                    drawTickLine(gl, color, xpos, ypos, zpos, xlab, ylab, zlab);
                } else {
                    // FIXME REWRITE ANDROID
                }
            }

            // Select the alignement of the tick label
            Halign hAlign = layoutHorizontal(direction, cam, hal, tickPosition);
            Valign vAlign = layoutVertical(direction, val, zdir);

            // Draw the text label of the current tick
            drawAxisTickNumericLabel(gl, glu, direction, cam, color, hAlign, vAlign, ticksTxtBounds, tickLabel, tickPosition);
        }
    }

    public void drawAxisTickNumericLabel(GL gl, GLU glu, int direction, Camera cam, Color color, Halign hAlign, Valign vAlign, BoundingBox3d ticksTxtBounds, String tickLabel,
            Coord3d tickPosition) {
        // doTransform(gl);
        if (gl.isGL2()) {
            gl.getGL2().glLoadIdentity();
            gl.getGL2().glScalef(scale.x, scale.y, scale.z);
            // gl.getGL2().glRotatef(90, 1, 0, 1);
        } else {
            GLES2CompatUtils.glLoadIdentity();
            // GLES2CompatUtils.glRotatef((float)Math.PI/2, 1, 0, 1);
            GLES2CompatUtils.glScalef(scale.x, scale.y, scale.z);
        }

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


    protected static final int PRECISION = 6;

    protected View view;

    // use this text renderer to get occupied volume by text
    protected ITextRenderer txt = new TextBitmapRenderer();

    protected IAxeLayout layout;

    protected BoundingBox3d boxBounds;
    protected BoundingBox3d wholeBounds;
    protected Coord3d center;
    protected Coord3d scale;

    protected float xrange;
    protected float yrange;
    protected float zrange;

    protected float quadx[][];
    protected float quady[][];
    protected float quadz[][];

    protected float normx[];
    protected float normy[];
    protected float normz[];

    protected float axeXx[][];
    protected float axeXy[][];
    protected float axeXz[][];
    protected float axeYx[][];
    protected float axeYy[][];
    protected float axeYz[][];
    protected float axeZx[][];
    protected float axeZy[][];
    protected float axeZz[][];

    protected int axeXquads[][];
    protected int axeYquads[][];
    protected int axeZquads[][];

    protected boolean quadIsHidden[];

    protected static final int AXE_X = 0;
    protected static final int AXE_Y = 1;
    protected static final int AXE_Z = 2;
    
    protected List<AxeAnnotation> annotations = new ArrayList<AxeAnnotation>();
    protected AxeTransformer transformerX;
    protected AxeTransformer transformerY;
    protected AxeTransformer transformerZ;
}
