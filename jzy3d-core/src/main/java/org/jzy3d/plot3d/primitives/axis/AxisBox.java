package org.jzy3d.plot3d.primitives.axis;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Vector3d;
import org.jzy3d.painters.Painter;
import org.jzy3d.painters.RenderMode;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.primitives.axis.layout.AxisBoxLayout;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;


/**
 * The {@link AxisBox} displays a box with front face invisible and ticks labels.
 * 
 * @author Martin Pernollet
 */
public class AxisBox implements IAxis {
    static Logger LOGGER = Logger.getLogger(AxisBox.class);

    public AxisBox(BoundingBox3d bbox) {
        this(bbox, new AxisBoxLayout());
    }

    public AxisBox(BoundingBox3d bbox, IAxisLayout layout) {
        this.layout = layout;
        if (bbox.valid())
            setAxe(bbox);
        else
            setAxe(new BoundingBox3d(-1, 1, -1, 1, -1, 1));
        wholeBounds = new BoundingBox3d();
        init();
    }

    /**
     * Draws the AxeBox. The camera is used to determine which axis is closest to the ur point ov view, in order to decide for an axis on which to diplay the tick values.
     */
    @Override
    public void draw(Painter painter) {
        cullingEnable(painter);
        updateHiddenQuads(painter);

        doTransform(painter);
        drawFace(painter);

        doTransform(painter);
        drawGrid(painter);

        synchronized (annotations) {
            for (AxeAnnotation a : annotations) {
                a.draw(painter, this);
            }
        }

        doTransform(painter);
        drawTicksAndLabels(painter);

        cullingDisable(painter);

    }

    /** reset to identity and apply scaling 
     * @param painter TODO*/
    public void doTransform(Painter painter) {
        painter.glLoadIdentity();
        painter.glScalef(scale.x, scale.y, scale.z);
    }

    public void cullingDisable(Painter painter) {
    	painter.glDisable_CullFace();
    }

    public void cullingEnable(Painter painter) {
        painter.glEnable_CullFace();
        painter.glFrontFace_ClockWise();
        painter.glCullFace_Front();
    }

    /* */

    /* DRAW AXEBOX ELEMENTS */

    public void drawFace(Painter painter) {
        if (layout.isFaceDisplayed()) {
            Color quadcolor = layout.getQuadColor();
            painter.glPolygonMode(PolygonMode.BACK, PolygonFill.FILL);
            
            painter.glColor4f(quadcolor.r, quadcolor.g, quadcolor.b, quadcolor.a);
            painter.glLineWidth(1.0f);
            painter.glEnable_PolygonOffsetFill();
            painter.glPolygonOffset(1.0f, 1.0f); // handle stippling
            drawCube(painter, RenderMode.RENDER);
            painter.glDisable_PolygonOffsetFill();
        }
    }

    public void drawGrid(Painter painter) {
        Color gridcolor = layout.getGridColor();

        painter.glPolygonMode(PolygonMode.BACK, PolygonFill.LINE);
        painter.glColor4f(gridcolor.r, gridcolor.g, gridcolor.b, gridcolor.a);
        painter.glLineWidth(1);
        drawCube(painter, RenderMode.RENDER);

        // Draw grids on non hidden quads
        painter.glPolygonMode(PolygonMode.BACK, PolygonFill.LINE);
        painter.glColor4f(gridcolor.r, gridcolor.g, gridcolor.b, gridcolor.a);
        painter.glLineWidth(1);
        painter.glLineStipple(1, (short) 0xAAAA);
        
        painter.glEnable_LineStipple();
        for (int quad = 0; quad < 6; quad++)
            if (!quadIsHidden[quad])
                drawGridOnQuad(painter, quad);
        painter.glDisable_LineStipple();
    }

    public void drawTicksAndLabels(Painter painter) {
        wholeBounds.reset();
        wholeBounds.add(boxBounds);

        drawTicksAndLabelsX(painter);
        drawTicksAndLabelsY(painter);
        drawTicksAndLabelsZ(painter);
    }

    public void drawTicksAndLabelsX(Painter painter) {
        if (xrange > 0 && layout.isXTickLabelDisplayed()) {

            // If we are on top, we make direct axe placement
            if (view != null && view.getViewMode().equals(ViewPositionMode.TOP)) {
                BoundingBox3d bbox = drawTicks(painter, 1, AXE_X, layout.getXTickColor(), Halign.LEFT, Valign.TOP);
                wholeBounds.add(bbox);
            }
            // otherwise computed placement
            else {
                int xselect = findClosestXaxe(painter.getCamera());
                if (xselect >= 0) {
                    BoundingBox3d bbox = drawTicks(painter, xselect, AXE_X, layout.getXTickColor());
                    wholeBounds.add(bbox);
                } else {
                    // HACK: handles "on top" view, when all face of cube are
                    // drawn, which forbid to select an axe automatically
                    BoundingBox3d bbox = drawTicks(painter, 2, AXE_X, layout.getXTickColor(), Halign.CENTER, Valign.TOP);
                    wholeBounds.add(bbox);
                }
            }
        }
    }

    public void drawTicksAndLabelsY(Painter painter) {
        if (yrange > 0 && layout.isYTickLabelDisplayed()) {
            if (view != null && view.getViewMode().equals(ViewPositionMode.TOP)) {
                BoundingBox3d bbox = drawTicks(painter, 2, AXE_Y, layout.getYTickColor(), Halign.LEFT, Valign.GROUND);
                wholeBounds.add(bbox);
            } else {
                int yselect = findClosestYaxe(painter.getCamera());
                if (yselect >= 0) {
                    BoundingBox3d bbox = drawTicks(painter, yselect, AXE_Y, layout.getYTickColor());
                    wholeBounds.add(bbox);
                } else {
                    // HACK: handles "on top" view, when all face of cube are
                    // drawn, which forbid to select an axe automatically
                    BoundingBox3d bbox = drawTicks(painter, 1, AXE_Y, layout.getYTickColor(), Halign.RIGHT, Valign.GROUND);
                    wholeBounds.add(bbox);
                }
            }
        }
    }

    public void drawTicksAndLabelsZ(Painter painter) {
        if (zrange > 0 && layout.isZTickLabelDisplayed()) {
            if (view != null && view.getViewMode().equals(ViewPositionMode.TOP)) {

            } else {
                int zselect = findClosestZaxe(painter.getCamera());
                if (zselect >= 0) {
                    BoundingBox3d bbox = drawTicks(painter, zselect, AXE_Z, layout.getZTickColor());
                    wholeBounds.add(bbox);
                }
            }
        }
    }

    /**
     * Make all GL2 calls allowing to build a cube with 6 separate quads. Each quad is indexed from 0.0f to 5.0f using glPassThrough, and may be traced in feedback mode when mode=GL2.GL_FEEDBACK
     */
    protected void drawCube(Painter painter, RenderMode mode) {
        for (int q = 0; q < 6; q++) {
            if (mode == RenderMode.FEEDBACK)
                painter.glPassThrough(q);
            painter.glBegin_Quad();
            for (int v = 0; v < 4; v++) {
            	painter.vertex(quadx[q][v], quady[q][v], quadz[q][v], spaceTransformer);
            }
            painter.glEnd();
        }
    }

    /**
     * Draw a grid on the desired quad.
     */
    protected void drawGridOnQuad(Painter painter, int quad) {
        // Draw X grid along X axis
        if ((quad != 0) && (quad != 1)) {
            double[] xticks = layout.getXTicks();
            for (int t = 0; t < xticks.length; t++) {
                painter.glBegin_Line();
                painter.vertex((float) xticks[t], quady[quad][0], quadz[quad][0], spaceTransformer);
                painter.vertex((float) xticks[t], quady[quad][2], quadz[quad][2], spaceTransformer);
                painter.glEnd();
            }
        }
        // Draw Y grid along Y axis
        if ((quad != 2) && (quad != 3)) {
            double[] yticks = layout.getYTicks();
            for (int t = 0; t < yticks.length; t++) {
                painter.glBegin_Line();
                painter.vertex(quadx[quad][0], (float) yticks[t], quadz[quad][0], spaceTransformer);
                painter.vertex(quadx[quad][2], (float) yticks[t], quadz[quad][2], spaceTransformer);
                painter.glEnd();
            }
        }
        // Draw Z grid along Z axis
        if ((quad != 4) && (quad != 5)) {
            double[] zticks = layout.getZTicks();
            for (int t = 0; t < zticks.length; t++) {
                painter.glBegin_Line();
                painter.vertex(quadx[quad][0], quady[quad][0], (float) zticks[t], spaceTransformer);
                painter.vertex(quadx[quad][2], quady[quad][2], (float) zticks[t], spaceTransformer);
                painter.glEnd();
            }
        }
    }

    protected BoundingBox3d drawTicks(Painter painter, int axis, int direction, Color color) {
        return drawTicks(painter, axis, direction, color, null, null);
    }

    /** Draws axis labels, tick lines and tick label*/
    protected BoundingBox3d drawTicks(Painter painter, int axis, int direction, Color color, Halign hal, Valign val) {
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
            axeLabel = layout.getXAxisLabel();
        } else if (isY(direction)) {
            xlab = axeLabelDist * (xrange / tickLength) * dist * xdir + xpos;
            ylab = center.y;
            zlab = axeLabelDist * (zrange / tickLength) * dist * zdir + zpos;
            axeLabel = layout.getYAxisLabel();
        } else {
            xlab = axeLabelDist * (xrange / tickLength) * dist * xdir + xpos;
            ylab = axeLabelDist * (yrange / tickLength) * dist * ydir + ypos;
            zlab = center.z;
            axeLabel = layout.getZAxisLabel();
        }

        drawAxisLabel(painter, direction, color, ticksTxtBounds, xlab, ylab, zlab, axeLabel);
        drawAxisTicks(painter, direction, color, hal, val, tickLength, ticksTxtBounds, xpos, ypos, zpos, xdir, ydir, zdir, getAxisTicks(direction));
        return ticksTxtBounds;
    }

    public void drawAxisLabel(Painter painter, int direction, Color color, BoundingBox3d ticksTxtBounds, double xlab, double ylab, double zlab, String axeLabel) {
        if (isXDisplayed(direction) || isYDisplayed(direction) || isZDisplayed(direction)) {
            Coord3d labelPosition = new Coord3d(xlab, ylab, zlab);
            
            // not fully relevant
            /*if(spaceTransformer!=null){
                labelPosition = spaceTransformer.compute(labelPosition);
            }*/
            
            BoundingBox3d labelBounds = txt.drawText(painter, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);
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
    public void drawAxisTicks(Painter painter, int direction, Color color, Halign hal, Valign val, float tickLength, BoundingBox3d ticksTxtBounds, double xpos, double ypos, double zpos, float xdir, float ydir, float zdir, double[] ticks) {
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
                    drawTickLine(painter, color, xpos, ypos, zpos, xlab, ylab, zlab);
            }

            // Select the alignement of the tick label
            Halign hAlign = layoutHorizontal(direction, painter.getCamera(), hal, tickPosition);
            Valign vAlign = layoutVertical(direction, val, zdir);

            // Draw the text label of the current tick
            drawAxisTickNumericLabel(painter, direction, color, hAlign, vAlign, ticksTxtBounds, tickLabel, tickPosition);
        }
    }

    public void drawAxisTickNumericLabel(Painter painter, int direction, Color color, Halign hAlign, Valign vAlign, BoundingBox3d ticksTxtBounds, String tickLabel, Coord3d tickPosition) {
        // doTransform(gl);
        painter.glLoadIdentity();
        painter.glScalef(scale.x, scale.y, scale.z);

        BoundingBox3d tickBounds = txt.drawText(painter, tickLabel, tickPosition, hAlign, vAlign, color);
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

    public void drawTickLine(Painter painter, Color color, double xpos, double ypos, double zpos, double xlab, double ylab, double zlab) {
        painter.glColor3f(color.r, color.g, color.b);
        painter.glLineWidth(1);

        // Draw the tick line
        painter.glBegin_Line();
        painter.glVertex3d(xpos, ypos, zpos);
        painter.glVertex3d(xlab, ylab, zlab);
        painter.glEnd();
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

    protected void updateHiddenQuads(Painter painter) {
        quadIsHidden = getHiddenQuads(painter);
    }

    /** Computes the visibility of each cube face. 
     * @param painter TODO*/
    protected boolean[] getHiddenQuads(Painter painter) {
        Coord3d scaledEye = painter.getCamera().getEye().div(scale);
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
    public IAxisLayout getLayout() {
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

    /* */

    protected static final int PRECISION = 6;

    protected View view;

    // use this text renderer to get occupied volume by text
    protected ITextRenderer txt = new TextBitmapRenderer();

    protected IAxisLayout layout;

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
