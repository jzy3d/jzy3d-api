package org.jzy3d.plot3d.primitives.axes;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Vector3d;
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
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.glu.GLU;

/**
 * The AxeBox displays a box with front face invisible and ticks labels.
 * 
 * @author Martin Pernollet
 */
public class AxeBox implements IAxe {
    static Logger LOGGER = Logger.getLogger(AxeBox.class);

    public AxeBox(BoundingBox3d bbox) {
        this(bbox, new AxeBoxLayout());
    }

    public AxeBox(BoundingBox3d bbox, IAxeLayout layout) {
        this.layout = layout;
        if (bbox.valid())
            setAxe(bbox);
        else
            setAxe(new BoundingBox3d(-1, 1, -1, 1, -1, 1));
        wholeBounds = new BoundingBox3d();
        init();
    }

    @Override
    public List<AxeAnnotation> getAnnotations() {
        return annotations;
    }

    @Override
    public void setAnnotations(List<AxeAnnotation> annotations) {
        this.annotations = annotations;
    }

    public void addAnnotation(AxeAnnotation annotation) {
        synchronized (annotations) {
            annotations.add(annotation);
        }
    }

    /**
     * Draws the AxeBox. The camera is used to determine which axis is closest to the ur point ov view, in order to decide for an axis on which to diplay the tick values.
     */
    @Override
    public void draw(GL gl, GLU glu, Camera camera) {
        cullingEnable(gl);
        updateHiddenQuads(gl, camera);

        doTransform(gl);
        drawFace(gl);

        doTransform(gl);
        drawGrid(gl);

        synchronized (annotations) {
            for (AxeAnnotation a : annotations) {
                a.draw(gl, this);
            }
        }

        doTransform(gl);
        drawTicksAndLabels(gl, glu, camera);

        cullingDisable(gl);

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

    public void cullingDisable(GL gl) {
        gl.glDisable(GL.GL_CULL_FACE);
    }

    public void cullingEnable(GL gl) {
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glFrontFace(GL.GL_CCW);
        gl.glCullFace(GL.GL_FRONT);
    }

    /* */

    /* DRAW AXEBOX ELEMENTS */

    public void drawFace(GL gl) {
        if (layout.isFaceDisplayed()) {
            Color quadcolor = layout.getQuadColor();
            if (gl.isGL2()) {
                gl.getGL2().glPolygonMode(GL.GL_BACK, GL2GL3.GL_FILL);
                gl.getGL2().glColor4f(quadcolor.r, quadcolor.g, quadcolor.b, quadcolor.a);
            } else {
                GLES2CompatUtils.glPolygonMode(GL.GL_BACK, GL2GL3.GL_FILL);
                GLES2CompatUtils.glColor4f(quadcolor.r, quadcolor.g, quadcolor.b, quadcolor.a);
            }
            gl.glLineWidth(1.0f);
            gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
            gl.glPolygonOffset(1.0f, 1.0f); // handle stippling
            drawCube(gl, GL2.GL_RENDER);
            gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
        }
    }

    public void drawGrid(GL gl) {
        Color gridcolor = layout.getGridColor();

        if (gl.isGL2()) {
            gl.getGL2().glPolygonMode(GL.GL_BACK, GL2GL3.GL_LINE);
            gl.getGL2().glColor4f(gridcolor.r, gridcolor.g, gridcolor.b, gridcolor.a);
            gl.getGL2().glLineWidth(1);
            drawCube(gl, GL2.GL_RENDER);

            // Draw grids on non hidden quads
            gl.getGL2().glPolygonMode(GL.GL_BACK, GL2GL3.GL_LINE);
            gl.getGL2().glColor4f(gridcolor.r, gridcolor.g, gridcolor.b, gridcolor.a);
            gl.getGL2().glLineWidth(1);
            gl.getGL2().glLineStipple(1, (short) 0xAAAA);
        } else {
            GLES2CompatUtils.glPolygonMode(GL.GL_BACK, GL2GL3.GL_LINE);
            GLES2CompatUtils.glColor4f(gridcolor.r, gridcolor.g, gridcolor.b, gridcolor.a);
            gl.glLineWidth(1);
            drawCube(gl, GL2.GL_RENDER);

            // Draw grids on non hidden quads
            GLES2CompatUtils.glPolygonMode(GL.GL_BACK, GL2GL3.GL_LINE);
            GLES2CompatUtils.glColor4f(gridcolor.r, gridcolor.g, gridcolor.b, gridcolor.a);
            GLES2CompatUtils.glLineWidth(1);
            GLES2CompatUtils.glLineStipple(1, (short) 0xAAAA);
        }

        gl.glEnable(GL2.GL_LINE_STIPPLE);
        for (int quad = 0; quad < 6; quad++)
            if (!quadIsHidden[quad])
                drawGridOnQuad(gl, quad);
        gl.glDisable(GL2.GL_LINE_STIPPLE);
    }

    public void drawTicksAndLabels(GL gl, GLU glu, Camera camera) {
        wholeBounds.reset();
        wholeBounds.add(boxBounds);

        drawTicksAndLabelsX(gl, glu, camera);
        drawTicksAndLabelsY(gl, glu, camera);
        drawTicksAndLabelsZ(gl, glu, camera);
    }

    public void drawTicksAndLabelsX(GL gl, GLU glu, Camera camera) {
        if (xrange > 0 && layout.isXTickLabelDisplayed()) {

            // If we are on top, we make direct axe placement
            if (view != null && view.getViewMode().equals(ViewPositionMode.TOP)) {
                BoundingBox3d bbox = drawTicks(gl, glu, camera, 1, AXE_X, layout.getXTickColor(), Halign.LEFT, Valign.TOP);
                wholeBounds.add(bbox);
            }
            // otherwise computed placement
            else {
                int xselect = findClosestXaxe(camera);
                if (xselect >= 0) {
                    BoundingBox3d bbox = drawTicks(gl, glu, camera, xselect, AXE_X, layout.getXTickColor());
                    wholeBounds.add(bbox);
                } else {
                    // HACK: handles "on top" view, when all face of cube are
                    // drawn, which forbid to select an axe automatically
                    BoundingBox3d bbox = drawTicks(gl, glu, camera, 2, AXE_X, layout.getXTickColor(), Halign.CENTER, Valign.TOP);
                    wholeBounds.add(bbox);
                }
            }
        }
    }

    public void drawTicksAndLabelsY(GL gl, GLU glu, Camera camera) {
        if (yrange > 0 && layout.isYTickLabelDisplayed()) {
            if (view != null && view.getViewMode().equals(ViewPositionMode.TOP)) {
                BoundingBox3d bbox = drawTicks(gl, glu, camera, 2, AXE_Y, layout.getYTickColor(), Halign.LEFT, Valign.GROUND);
                wholeBounds.add(bbox);
            } else {
                int yselect = findClosestYaxe(camera);
                if (yselect >= 0) {
                    BoundingBox3d bbox = drawTicks(gl, glu, camera, yselect, AXE_Y, layout.getYTickColor());
                    wholeBounds.add(bbox);
                } else {
                    // HACK: handles "on top" view, when all face of cube are
                    // drawn, which forbid to select an axe automatically
                    BoundingBox3d bbox = drawTicks(gl, glu, camera, 1, AXE_Y, layout.getYTickColor(), Halign.RIGHT, Valign.GROUND);
                    wholeBounds.add(bbox);
                }
            }
        }
    }

    public void drawTicksAndLabelsZ(GL gl, GLU glu, Camera camera) {
        if (zrange > 0 && layout.isZTickLabelDisplayed()) {
            if (view != null && view.getViewMode().equals(ViewPositionMode.TOP)) {

            } else {
                int zselect = findClosestZaxe(camera);
                if (zselect >= 0) {
                    BoundingBox3d bbox = drawTicks(gl, glu, camera, zselect, AXE_Z, layout.getZTickColor());
                    wholeBounds.add(bbox);
                }
            }
        }
    }

    /**
     * Make all GL2 calls allowing to build a cube with 6 separate quads. Each quad is indexed from 0.0f to 5.0f using glPassThrough, and may be traced in feedback mode when mode=GL2.GL_FEEDBACK
     */
    protected void drawCube(GL gl, int mode) {
        for (int q = 0; q < 6; q++) {
            if (gl.isGL2()) {
                if (mode == GL2.GL_FEEDBACK)
                    gl.getGL2().glPassThrough(q);
                gl.getGL2().glBegin(GL2GL3.GL_QUADS);
                for (int v = 0; v < 4; v++) {
                    vertexGL2(gl, quadx[q][v], quady[q][v], quadz[q][v]);
                }
                gl.getGL2().glEnd();
            } else {
                if (mode == GL2.GL_FEEDBACK)
                    GLES2CompatUtils.glPassThrough(q);
                GLES2CompatUtils.glBegin(GL2GL3.GL_QUADS);
                for (int v = 0; v < 4; v++) {
                    vertexGLES2(quadx[q][v], quady[q][v], quadz[q][v]);
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
            if (gl.isGL2()) {
                for (int t = 0; t < xticks.length; t++) {
                    gl.getGL2().glBegin(GL.GL_LINES);
                    vertexGL2(gl, (float) xticks[t], quady[quad][0], quadz[quad][0]);
                    vertexGL2(gl, (float) xticks[t], quady[quad][2], quadz[quad][2]);
                    gl.getGL2().glEnd();
                }
            } else {
                // FIXME TO BE REWRITTEN ANDROID
            }
        }
        // Draw Y grid along Y axis
        if ((quad != 2) && (quad != 3)) {
            double[] yticks = layout.getYTicks();
            if (gl.isGL2()) {
                for (int t = 0; t < yticks.length; t++) {
                    gl.getGL2().glBegin(GL.GL_LINES);
                    vertexGL2(gl, quadx[quad][0], (float) yticks[t], quadz[quad][0]);
                    vertexGL2(gl, quadx[quad][2], (float) yticks[t], quadz[quad][2]);
                    gl.getGL2().glEnd();
                }
            } else {
                // FIXME TO BE REWRITTEN ANDROID
            }
        }
        // Draw Z grid along Z axis
        if ((quad != 4) && (quad != 5)) {
            double[] zticks = layout.getZTicks();
            if (gl.isGL2()) {
                for (int t = 0; t < zticks.length; t++) {
                    gl.getGL2().glBegin(GL.GL_LINES);
                    vertexGL2(gl, quadx[quad][0], quady[quad][0], (float) zticks[t]);
                    vertexGL2(gl, quadx[quad][2], quady[quad][2], (float) zticks[t]);
                    gl.getGL2().glEnd();
                }
            } else {
                // FIXME TO BE REWRITTEN ANDROID
            }
        }
    }

    protected BoundingBox3d drawTicks(GL gl, GLU glu, Camera cam, int axis, int direction, Color color) {
        return drawTicks(gl, glu, cam, axis, direction, color, null, null);
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
        double xpos = 0;
        double ypos = 0;
        double zpos = 0;

        if (spaceTransformer == null) {
            xpos = normx[quad_0] + normx[quad_1];
            ypos = normy[quad_0] + normy[quad_1];
            zpos = normz[quad_0] + normz[quad_1];
        } else {
            xpos = spaceTransformer.getX().compute(normx[quad_0]) + spaceTransformer.getX().compute(normx[quad_1]);
            ypos = spaceTransformer.getY().compute(normy[quad_0]) + spaceTransformer.getY().compute(normy[quad_1]);
            zpos = spaceTransformer.getZ().compute(normz[quad_0]) + spaceTransformer.getZ().compute(normz[quad_1]);
        }

        // TODO : HERE SHOULD OVERRIDE IF TRANSFORM

        // Computes the DIRection of the ticks
        // assuming initial vector point is the center
        float xdir = (normx[quad_0] + normx[quad_1]) - center.x;
        float ydir = (normy[quad_0] + normy[quad_1]) - center.y;
        float zdir = (normz[quad_0] + normz[quad_1]) - center.z;
        xdir = xdir == 0 ? 0 : xdir / Math.abs(xdir); // so that direction has length 1
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
            
            // not fully relevant
            /*if(spaceTransformer!=null){
                labelPosition = spaceTransformer.compute(labelPosition);
            }*/
            
            BoundingBox3d labelBounds = txt.drawText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);
            if (labelBounds != null)
                ticksTxtBounds.add(labelBounds);
        }
    }

    protected boolean isZDisplayed(int direction) {
        return isZ(direction) && layout.isZAxeLabelDisplayed();
    }

    protected boolean isYDisplayed(int direction) {
        return isY(direction) && layout.isYAxeLabelDisplayed();
    }

    protected boolean isXDisplayed(int direction) {
        return isX(direction) && layout.isXAxeLabelDisplayed();
    }

    protected boolean isZ(int direction) {
        return direction == AXE_Z;
    }

    protected boolean isY(int direction) {
        return direction == AXE_Y;
    }

    protected boolean isX(int direction) {
        return direction == AXE_X;
    }

    protected double[] getAxisTicks(int direction) {
        double ticks[];
        if (isX(direction))
            ticks = layout.getXTicks();
        else if (isY(direction))
            ticks = layout.getYTicks();
        else
            // (axis==AXE_Z)
            ticks = layout.getZTicks();
        return ticks;
    }

    /** 
     * Draw an array of ticks on the given axis indicated by direction field. 
     */
    public void drawAxisTicks(GL gl, GLU glu, Camera cam, int direction, Color color, Halign hal, Valign val, float tickLength, BoundingBox3d ticksTxtBounds, double xpos, double ypos, double zpos, float xdir, float ydir, float zdir, double[] ticks) {
        double xlab;
        double ylab;
        double zlab;
        String tickLabel = "";

        for (int t = 0; t < ticks.length; t++) {
            // Shift the tick vector along the selected axis
            // and set the tick length
            if (spaceTransformer == null) {
                if (isX(direction)) {

                    // Tick position
                    xpos = ticks[t];
                    xlab = xpos;
                    ylab = (yrange / tickLength) * ydir + ypos;
                    zlab = (zrange / tickLength) * zdir + zpos;

                    // Tick label
                    tickLabel = layout.getXTickRenderer().format(ticks[t]);

                } else if (isY(direction)) {

                    // Tick position
                    ypos = ticks[t];
                    xlab = (xrange / tickLength) * xdir + xpos;
                    ylab = ypos;
                    zlab = (zrange / tickLength) * zdir + zpos;

                    // Tick label
                    tickLabel = layout.getYTickRenderer().format(ticks[t]);

                } else { // (axis==AXE_Z)

                    // Tick position
                    zpos = ticks[t];
                    xlab = (xrange / tickLength) * xdir + xpos;
                    ylab = (yrange / tickLength) * ydir + ypos;
                    zlab = zpos;

                    // Tick label
                    tickLabel = layout.getZTickRenderer().format(ticks[t]);
                }
            } else {
                // use space transform shift if we have a space transformer
                if (isX(direction)) {
                    xpos = spaceTransformer.getX().compute((float) ticks[t]);
                    xlab = xpos;
                    ylab = Math.signum(tickLength * ydir) * (yrange / spaceTransformer.getY().compute(Math.abs(tickLength))) * spaceTransformer.getY().compute(Math.abs(ydir)) + ypos;
                    zlab = Math.signum(tickLength * ydir) * (zrange / spaceTransformer.getZ().compute(Math.abs(tickLength))) * spaceTransformer.getZ().compute(Math.abs(zdir)) + zpos;
                    tickLabel = layout.getXTickRenderer().format(ticks[t]);
                } else if (isY(direction)) {
                    ypos = spaceTransformer.getY().compute((float) ticks[t]);
                    xlab = Math.signum(tickLength * xdir) * (xrange / spaceTransformer.getX().compute(Math.abs(tickLength))) * spaceTransformer.getX().compute(Math.abs(xdir)) + xpos;
                    ylab = ypos;
                    zlab = Math.signum(tickLength * zdir) * (zrange / spaceTransformer.getZ().compute(Math.abs(tickLength))) * spaceTransformer.getZ().compute(Math.abs(zdir)) + zpos;
                    tickLabel = layout.getYTickRenderer().format(ticks[t]);
                } else { // (axis==AXE_Z)
                    zpos = spaceTransformer.getZ().compute((float) ticks[t]);
                    xlab = Math.signum(tickLength * xdir) * (xrange / spaceTransformer.getX().compute(Math.abs(tickLength))) * spaceTransformer.getX().compute(Math.abs(xdir)) + xpos;
                    ylab = Math.signum(tickLength * ydir) * (yrange / spaceTransformer.getY().compute(Math.abs(tickLength))) * spaceTransformer.getY().compute(Math.abs(ydir)) + ypos;
                    zlab = zpos;
                    tickLabel = layout.getZTickRenderer().format(ticks[t]);
                }
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

    public void drawAxisTickNumericLabel(GL gl, GLU glu, int direction, Camera cam, Color color, Halign hAlign, Valign vAlign, BoundingBox3d ticksTxtBounds, String tickLabel, Coord3d tickPosition) {
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

    public Valign layoutVertical(int direction, Valign val, float zdir) {
        Valign vAlign;
        if (val == null) {
            if (isZ(direction))
                vAlign = Valign.CENTER;
            else {
                if (zdir > 0)
                    vAlign = Valign.TOP;
                else
                    vAlign = Valign.BOTTOM;
            }
        } else
            vAlign = val;
        return vAlign;
    }

    public Halign layoutHorizontal(int direction, Camera cam, Halign hal, Coord3d tickPosition) {
        Halign hAlign;
        if (hal == null)
            hAlign = cam.side(tickPosition) ? Halign.LEFT : Halign.RIGHT;
        else
            hAlign = hal;
        return hAlign;
    }

    public void drawTickLine(GL gl, Color color, double xpos, double ypos, double zpos, double xlab, double ylab, double zlab) {
        gl.getGL2().glColor3f(color.r, color.g, color.b);
        gl.getGL2().glLineWidth(1);

        // Draw the tick line
        gl.getGL2().glBegin(GL.GL_LINES);
        gl.getGL2().glVertex3d(xpos, ypos, zpos);
        gl.getGL2().glVertex3d(xlab, ylab, zlab);
        gl.getGL2().glEnd();
    }

    /**
     * A helper to call glVerted3f on the input coordinate. For GL2 profile only. If logTransform is non null, then each dimension transform is processed before calling glVertex3d.
     */
    protected void vertexGL2(GL gl, Coord3d c) {
        if (spaceTransformer == null) {
            gl.getGL2().glVertex3f(c.x, c.y, c.z);
        } else {
            gl.getGL2().glVertex3f(spaceTransformer.getX().compute(c.x), spaceTransformer.getY().compute(c.y), spaceTransformer.getZ().compute(c.z));
        }
    }

    protected void vertexGL2(GL gl, float x, float y, float z) {
        if (spaceTransformer == null) {
            gl.getGL2().glVertex3f(x, y, z);
        } else {
            gl.getGL2().glVertex3f(spaceTransformer.getX().compute(x), spaceTransformer.getY().compute(y), spaceTransformer.getZ().compute(z));
        }
    }

    /**
     * A helper to call glVerted3f on the input coordinate. For GLES2 profile only. If logTransform is non null, then each dimension transform is processed before calling glVertex3d.
     */
    protected void vertexGLES2(Coord3d c) {
        if (spaceTransformer == null) {
            GLES2CompatUtils.glVertex3f(c.x, c.y, c.z);
        } else {
            GLES2CompatUtils.glVertex3f(spaceTransformer.getX().compute(c.x), spaceTransformer.getY().compute(c.y), spaceTransformer.getZ().compute(c.z));
        }
    }

    protected void vertexGLES2(float x, float y, float z) {
        if (spaceTransformer == null) {
            GLES2CompatUtils.glVertex3f(x, y, z);
        } else {
            GLES2CompatUtils.glVertex3f(spaceTransformer.getX().compute(x), spaceTransformer.getY().compute(y), spaceTransformer.getZ().compute(z));
        }
    }

    /* */

    /* AXIS SELECTION */

    /**
     * Selects the closest displayable X axe from camera
     */
    protected int findClosestXaxe(Camera cam) {
        int na = 4;
        double[] distAxeX = new double[na];

        // keeps axes that are not at intersection of 2 quads
        for (int a = 0; a < na; a++) {
            if (quadIsHidden[axeXquads[a][0]] ^ quadIsHidden[axeXquads[a][1]])
                distAxeX[a] = new Vector3d(axeXx[a][0], axeXy[a][0], axeXz[a][0], axeXx[a][1], axeXy[a][1], axeXz[a][1]).distance(cam.getEye());
            else
                distAxeX[a] = Double.MAX_VALUE;
        }

        // prefers the lower one
        for (int a = 0; a < na; a++) {
            if (distAxeX[a] < Double.MAX_VALUE) {
                if (center.z > (axeXz[a][0] + axeXz[a][1]) / 2)
                    distAxeX[a] *= -1;
            }
        }

        return min(distAxeX);
    }

    /**
     * Selects the closest displayable Y axe from camera
     */
    protected int findClosestYaxe(Camera cam) {
        int na = 4;
        double[] distAxeY = new double[na];

        // keeps axes that are not at intersection of 2 quads
        for (int a = 0; a < na; a++) {
            if (quadIsHidden[axeYquads[a][0]] ^ quadIsHidden[axeYquads[a][1]])
                distAxeY[a] = new Vector3d(axeYx[a][0], axeYy[a][0], axeYz[a][0], axeYx[a][1], axeYy[a][1], axeYz[a][1]).distance(cam.getEye());
            else
                distAxeY[a] = Double.MAX_VALUE;
        }

        // prefers the lower one
        for (int a = 0; a < na; a++) {
            if (distAxeY[a] < Double.MAX_VALUE) {
                if (center.z > (axeYz[a][0] + axeYz[a][1]) / 2)
                    distAxeY[a] *= -1;
            }
        }

        return min(distAxeY);
    }

    /**
     * Selects the closest displayable Z axe from camera
     */
    protected int findClosestZaxe(Camera cam) {
        int na = 4;
        double[] distAxeZ = new double[na];

        // keeps axes that are not at intersection of 2 quads
        for (int a = 0; a < na; a++) {
            if (quadIsHidden[axeZquads[a][0]] ^ quadIsHidden[axeZquads[a][1]])
                distAxeZ[a] = new Vector3d(axeZx[a][0], axeZy[a][0], axeZz[a][0], axeZx[a][1], axeZy[a][1], axeZz[a][1]).distance(cam.getEye());
            else
                distAxeZ[a] = Double.MAX_VALUE;
        }

        // prefers the right one
        for (int a = 0; a < na; a++) {
            if (distAxeZ[a] < Double.MAX_VALUE) {
                Coord3d axeCenter = new Coord3d((axeZx[a][0] + axeZx[a][1]) / 2, (axeZy[a][0] + axeZy[a][1]) / 2, (axeZz[a][0] + axeZz[a][1]) / 2);
                if (!cam.side(axeCenter))
                    distAxeZ[a] *= -1;
            }
        }

        return min(distAxeZ);
    }

    /**
     * Return the index of the minimum value contained in the input array of doubles. If no value is smaller than Double.MAX_VALUE, the returned index is -1.
     */
    protected int min(double[] values) {
        double minv = Double.MAX_VALUE;
        int index = -1;

        for (int i = 0; i < values.length; i++)
            if (values[i] < minv) {
                minv = values[i];
                index = i;
            }
        return index;
    }

    /* */

    /* COMPUTATION OF HIDDEN QUADS */

    protected void updateHiddenQuads(GL gl, Camera camera) {
        quadIsHidden = getHiddenQuads(gl, camera);
    }

    /** Computes the visibility of each cube face. */
    protected boolean[] getHiddenQuads(GL gl, Camera cam) {
        Coord3d scaledEye = cam.getEye().div(scale);
        return getHiddenQuads(scaledEye, center);
    }

    public boolean[] getHiddenQuads(Coord3d scaledEye, Coord3d center) {
        boolean[] status = new boolean[6];
        
        if (scaledEye.x <= center.x) {
            status[0] = false;
            status[1] = true;
        } else {
            status[0] = true;
            status[1] = false;
        }
        if (scaledEye.y <= center.y) {
            status[2] = false;
            status[3] = true;
        } else {
            status[2] = true;
            status[3] = false;
        }
        if (scaledEye.z <= center.z) {
            status[4] = false;
            status[5] = true;
        } else {
            status[4] = true;
            status[5] = false;
        }
        return status;
    }

    /* */

    /**
     * Set the parameters and data of the AxeBox.
     */
    protected void setAxeBox(float xmin, float xmax, float ymin, float ymax, float zmin, float zmax) {
        // Compute center
        center = new Coord3d((xmax + xmin) / 2, (ymax + ymin) / 2, (zmax + zmin) / 2);
        xrange = xmax - xmin;
        yrange = ymax - ymin;
        zrange = zmax - zmin;

        // Define configuration of 6 quads (faces of the box)
        quadx = new float[6][4];
        quady = new float[6][4];
        quadz = new float[6][4];

        // x near
        quadx[0][0] = xmax;
        quady[0][0] = ymin;
        quadz[0][0] = zmax;
        quadx[0][1] = xmax;
        quady[0][1] = ymin;
        quadz[0][1] = zmin;
        quadx[0][2] = xmax;
        quady[0][2] = ymax;
        quadz[0][2] = zmin;
        quadx[0][3] = xmax;
        quady[0][3] = ymax;
        quadz[0][3] = zmax;
        // x far
        quadx[1][0] = xmin;
        quady[1][0] = ymax;
        quadz[1][0] = zmax;
        quadx[1][1] = xmin;
        quady[1][1] = ymax;
        quadz[1][1] = zmin;
        quadx[1][2] = xmin;
        quady[1][2] = ymin;
        quadz[1][2] = zmin;
        quadx[1][3] = xmin;
        quady[1][3] = ymin;
        quadz[1][3] = zmax;
        // y near
        quadx[2][0] = xmax;
        quady[2][0] = ymax;
        quadz[2][0] = zmax;
        quadx[2][1] = xmax;
        quady[2][1] = ymax;
        quadz[2][1] = zmin;
        quadx[2][2] = xmin;
        quady[2][2] = ymax;
        quadz[2][2] = zmin;
        quadx[2][3] = xmin;
        quady[2][3] = ymax;
        quadz[2][3] = zmax;
        // y far
        quadx[3][0] = xmin;
        quady[3][0] = ymin;
        quadz[3][0] = zmax;
        quadx[3][1] = xmin;
        quady[3][1] = ymin;
        quadz[3][1] = zmin;
        quadx[3][2] = xmax;
        quady[3][2] = ymin;
        quadz[3][2] = zmin;
        quadx[3][3] = xmax;
        quady[3][3] = ymin;
        quadz[3][3] = zmax;
        // z top
        quadx[4][0] = xmin;
        quady[4][0] = ymin;
        quadz[4][0] = zmax;
        quadx[4][1] = xmax;
        quady[4][1] = ymin;
        quadz[4][1] = zmax;
        quadx[4][2] = xmax;
        quady[4][2] = ymax;
        quadz[4][2] = zmax;
        quadx[4][3] = xmin;
        quady[4][3] = ymax;
        quadz[4][3] = zmax;
        // z down
        quadx[5][0] = xmax;
        quady[5][0] = ymin;
        quadz[5][0] = zmin;
        quadx[5][1] = xmin;
        quady[5][1] = ymin;
        quadz[5][1] = zmin;
        quadx[5][2] = xmin;
        quady[5][2] = ymax;
        quadz[5][2] = zmin;
        quadx[5][3] = xmax;
        quady[5][3] = ymax;
        quadz[5][3] = zmin;

        // Define configuration of each quad's normal
        normx = new float[6];
        normy = new float[6];
        normz = new float[6];

        normx[0] = xmax;
        normy[0] = 0;
        normz[0] = 0;
        normx[1] = xmin;
        normy[1] = 0;
        normz[1] = 0;
        normx[2] = 0;
        normy[2] = ymax;
        normz[2] = 0;
        normx[3] = 0;
        normy[3] = ymin;
        normz[3] = 0;
        normx[4] = 0;
        normy[4] = 0;
        normz[4] = zmax;
        normx[5] = 0;
        normy[5] = 0;
        normz[5] = zmin;

        // Define quad intersections that generate an axe
        // axe{A}quads[i][q]
        // A = axe direction (X, Y, or Z)
        // i = axe id (0 to 4)
        // q = quad id (0 to 1: an intersection is made of two quads)
        int na = 4; // n axes per dimension
        int np = 2; // n points for an axe
        int nq = 2;
        int i; // axe id

        axeXquads = new int[na][nq];
        axeYquads = new int[na][nq];
        axeZquads = new int[na][nq];

        i = 0;
        axeXquads[i][0] = 4;
        axeXquads[i][1] = 3; // quads making axe x0
        i = 1;
        axeXquads[i][0] = 3;
        axeXquads[i][1] = 5; // quads making axe x1
        i = 2;
        axeXquads[i][0] = 5;
        axeXquads[i][1] = 2; // quads making axe x2
        i = 3;
        axeXquads[i][0] = 2;
        axeXquads[i][1] = 4; // quads making axe x3
        i = 0;
        axeYquads[i][0] = 4;
        axeYquads[i][1] = 0; // quads making axe y0
        i = 1;
        axeYquads[i][0] = 0;
        axeYquads[i][1] = 5; // quads making axe y1
        i = 2;
        axeYquads[i][0] = 5;
        axeYquads[i][1] = 1; // quads making axe y2
        i = 3;
        axeYquads[i][0] = 1;
        axeYquads[i][1] = 4; // quads making axe y3
        i = 0;
        axeZquads[i][0] = 3;
        axeZquads[i][1] = 0; // quads making axe z0
        i = 1;
        axeZquads[i][0] = 0;
        axeZquads[i][1] = 2; // quads making axe z1
        i = 2;
        axeZquads[i][0] = 2;
        axeZquads[i][1] = 1; // quads making axe z2
        i = 3;
        axeZquads[i][0] = 1;
        axeZquads[i][1] = 3; // quads making axe z3

        // Define configuration of 4 axe per dimension:
        // axe{A}d[i][p], where
        //
        // A = axe direction (X, Y, or Z)
        // d = dimension (x coordinate, y coordinate or z coordinate)
        // i = axe id (0 to 4)
        // p = point id (0 to 1)
        //
        // Note: the points making an axe are from - to +
        // (i.e. direction is given by p0->p1)

        axeXx = new float[na][np];
        axeXy = new float[na][np];
        axeXz = new float[na][np];
        axeYx = new float[na][np];
        axeYy = new float[na][np];
        axeYz = new float[na][np];
        axeZx = new float[na][np];
        axeZy = new float[na][np];
        axeZz = new float[na][np];

        i = 0; // axe x0
        axeXx[i][0] = xmin;
        axeXy[i][0] = ymin;
        axeXz[i][0] = zmax;
        axeXx[i][1] = xmax;
        axeXy[i][1] = ymin;
        axeXz[i][1] = zmax;
        i = 1; // axe x1
        axeXx[i][0] = xmin;
        axeXy[i][0] = ymin;
        axeXz[i][0] = zmin;
        axeXx[i][1] = xmax;
        axeXy[i][1] = ymin;
        axeXz[i][1] = zmin;
        i = 2; // axe x2
        axeXx[i][0] = xmin;
        axeXy[i][0] = ymax;
        axeXz[i][0] = zmin;
        axeXx[i][1] = xmax;
        axeXy[i][1] = ymax;
        axeXz[i][1] = zmin;
        i = 3; // axe x3
        axeXx[i][0] = xmin;
        axeXy[i][0] = ymax;
        axeXz[i][0] = zmax;
        axeXx[i][1] = xmax;
        axeXy[i][1] = ymax;
        axeXz[i][1] = zmax;
        i = 0; // axe y0
        axeYx[i][0] = xmax;
        axeYy[i][0] = ymin;
        axeYz[i][0] = zmax;
        axeYx[i][1] = xmax;
        axeYy[i][1] = ymax;
        axeYz[i][1] = zmax;
        i = 1; // axe y1
        axeYx[i][0] = xmax;
        axeYy[i][0] = ymin;
        axeYz[i][0] = zmin;
        axeYx[i][1] = xmax;
        axeYy[i][1] = ymax;
        axeYz[i][1] = zmin;
        i = 2; // axe y2
        axeYx[i][0] = xmin;
        axeYy[i][0] = ymin;
        axeYz[i][0] = zmin;
        axeYx[i][1] = xmin;
        axeYy[i][1] = ymax;
        axeYz[i][1] = zmin;
        i = 3; // axe y3
        axeYx[i][0] = xmin;
        axeYy[i][0] = ymin;
        axeYz[i][0] = zmax;
        axeYx[i][1] = xmin;
        axeYy[i][1] = ymax;
        axeYz[i][1] = zmax;
        i = 0; // axe z0
        axeZx[i][0] = xmax;
        axeZy[i][0] = ymin;
        axeZz[i][0] = zmin;
        axeZx[i][1] = xmax;
        axeZy[i][1] = ymin;
        axeZz[i][1] = zmax;
        i = 1; // axe z1
        axeZx[i][0] = xmax;
        axeZy[i][0] = ymax;
        axeZz[i][0] = zmin;
        axeZx[i][1] = xmax;
        axeZy[i][1] = ymax;
        axeZz[i][1] = zmax;
        i = 2; // axe z2
        axeZx[i][0] = xmin;
        axeZy[i][0] = ymax;
        axeZz[i][0] = zmin;
        axeZx[i][1] = xmin;
        axeZy[i][1] = ymax;
        axeZz[i][1] = zmax;
        i = 3; // axe z3
        axeZx[i][0] = xmin;
        axeZy[i][0] = ymin;
        axeZz[i][0] = zmin;
        axeZx[i][1] = xmin;
        axeZy[i][1] = ymin;
        axeZz[i][1] = zmax;

        layout.getXTicks(xmin, xmax); // prepare ticks to display in the layout
                                      // tick buffer
        layout.getYTicks(ymin, ymax);
        layout.getZTicks(zmin, zmax);
        /*
         * setXTickMode(TICK_REGULAR, 3);5 setYTickMode(TICK_REGULAR, 3);5 setZTickMode(TICK_REGULAR, 5);6
         */
    }

    protected void init() {
        setScale(new Coord3d(1.0f, 1.0f, 1.0f));
    }

    @Override
    public void dispose() {
    }

    public ITextRenderer getTextRenderer() {
        return txt;
    }

    public void setTextRenderer(ITextRenderer renderer) {
        txt = renderer;
    }

    public View getView() {
        return view;
    }

    @Override
    public BoundingBox3d getBoxBounds() {
        return boxBounds;
    }

    @Override
    public IAxeLayout getLayout() {
        return layout;
    }

    @Override
    public SpaceTransformer getSpaceTransformer() {
        return spaceTransformer;
    }

    @Override
    public void setSpaceTransformer(SpaceTransformer spaceTransformer) {
        this.spaceTransformer = spaceTransformer;
    }

    /**
     * When setting a current view, the AxeBox can know the view is on mode CameraMode.TOP, and optimize some axis placement.
     */
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void setAxe(BoundingBox3d bbox) {
        this.boxBounds = bbox;
        // LOGGER.info(bbox);
        setAxeBox(bbox.getXmin(), bbox.getXmax(), bbox.getYmin(), bbox.getYmax(), bbox.getZmin(), bbox.getZmax());
    }

    /**
     * Return the boundingBox of this axis, including the volume occupied by the texts. This requires calling {@link draw()} before, which computes actual ticks position in 3d, and updates the bounds.
     */
    @Override
    public BoundingBox3d getWholeBounds() {
        return wholeBounds;
    }

    @Override
    public Coord3d getCenter() {
        return center;
    }

    /**
     * Set the scaling factor that are applyed on this object before GL2 commands.
     */
    @Override
    public void setScale(Coord3d scale) {
        this.scale = scale;
    }

    @Override
    public Coord3d getScale() {
        return scale;
    }

    /* */

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

    public static final int AXE_X = 0;
    public static final int AXE_Y = 1;
    public static final int AXE_Z = 2;

    protected List<AxeAnnotation> annotations = new ArrayList<AxeAnnotation>();

    protected SpaceTransformer spaceTransformer;
    
    public float[][] getQuadX(){
      return quadx;
    }
    public float[][] getQuadY(){
      return quady;
    }
    public float[][] getQuadZ(){
      return quadz;
    }
    public boolean[] getQuadIsHidden() {
      return quadIsHidden;
    }
}
