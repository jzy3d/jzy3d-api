package org.jzy3d.plot3d.primitives.axis;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.text.overlay.AWTTextOverlay;

/**
 * The AxeBox displays a box with front face invisible and ticks labels.
 * 
 * @author Martin Pernollet
 */
public class AxisBoxWithTxtRenderer extends AxisBox implements IAxis {
  public AxisBoxWithTxtRenderer(BoundingBox3d bbox, IAxisLayout layout) {
    super(bbox, layout);
  }

  public AxisBoxWithTxtRenderer(BoundingBox3d bbox) {
    super(bbox);
  }


  @Override
  public void dispose() {
    if (txtRenderer != null)
      txtRenderer.dispose();
  }


  public AWTTextOverlay getExperimentalTextRenderer() {
    return txtRenderer;
  }

  /**
   * Initialize a text renderer that will reference the target canvas for getting its dimensions (in
   * order to convert coordinates from OpenGL2 to Java2d).
   * 
   * @param canvas
   */
  public void setExperimentalTextOverlayRenderer(ICanvas canvas) {
    txtRenderer = new AWTTextOverlay(canvas);
  }
/*
  @Override
  protected BoundingBox3d drawTicks(IPainter painter, int axis, int direction, Color color,
      Horizontal hal, Vertical val) {
    int quad_0;
    int quad_1;
    Horizontal hAlign;
    Vertical vAlign;
    float tickLength = 20.0f; // with respect to range
    float axeLabelDist = 2.5f;
    BoundingBox3d ticksTxtBounds = new BoundingBox3d();

    // Retrieve the quads that intersect and create the selected axe
    if (direction == AXE_X) {
      quad_0 = axeXquads[axis][0];
      quad_1 = axeXquads[axis][1];
    } else if (direction == AXE_Y) {
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

    // Variables for storing the position of the LABel position
    // (2nd point on the tick line)
    double xlab;
    double ylab;
    double zlab;

    // Computes the DIRection of the ticks
    // assuming initial vector point is the center
    float xdir = (normx[quad_0] + normx[quad_1]) - center.x;
    float ydir = (normy[quad_0] + normy[quad_1]) - center.y;
    float zdir = (normz[quad_0] + normz[quad_1]) - center.z;
    xdir = xdir == 0 ? 0 : xdir / Math.abs(xdir); // so that direction as
                                                  // length 1
    ydir = ydir == 0 ? 0 : ydir / Math.abs(ydir);
    zdir = zdir == 0 ? 0 : zdir / Math.abs(zdir);

    // Draw the label for axis
    String axeLabel;
    int dist = 1;
    if (direction == AXE_X) {
      xlab = center.x;
      ylab = axeLabelDist * (yrange / tickLength) * dist * ydir + ypos;
      zlab = axeLabelDist * (zrange / tickLength) * dist * zdir + zpos;
      axeLabel = layout.getXAxisLabel();
    } else if (direction == AXE_Y) {
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

    if ((direction == AXE_X && layout.isXAxeLabelDisplayed())
        || (direction == AXE_Y && layout.isYAxeLabelDisplayed())
        || (direction == AXE_Z && layout.isZAxeLabelDisplayed())) {
      Coord3d labelPosition = new Coord3d(xlab, ylab, zlab);
      if (txtRenderer != null)
        txtRenderer.appendText(painter, axeLabel, labelPosition, Horizontal.CENTER, Vertical.CENTER,
            color);
      else {
        BoundingBox3d labelBounds = textRenderer.drawText(painter, getLayout().getFont(),
            axeLabel, labelPosition, Horizontal.CENTER, Vertical.CENTER, color);
        if (labelBounds != null)
          ticksTxtBounds.add(labelBounds);
      }
    }

    // Retrieve the selected tick positions
    double ticks[];
    if (direction == AXE_X)
      ticks = layout.getXTicks();
    else if (direction == AXE_Y)
      ticks = layout.getYTicks();
    else
      // (axis==AXE_Z)
      ticks = layout.getZTicks();

    // Draw the ticks, labels, and dotted lines iteratively
    String tickLabel = "";

    for (int t = 0; t < ticks.length; t++) {
      // Shift the tick vector along the selected axis
      // and set the tick length
      if (direction == AXE_X) {
        xpos = ticks[t];
        xlab = xpos;
        ylab = (yrange / tickLength) * ydir + ypos;
        zlab = (zrange / tickLength) * zdir + zpos;
        tickLabel = layout.getXTickRenderer().format(xpos);
      } else if (direction == AXE_Y) {
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


      painter.glColor3f(color.r, color.g, color.b);
      painter.glLineWidth(1);

      // Draw the tick line
      painter.glBegin_Line();
      painter.glVertex3d(xpos, ypos, zpos);
      painter.glVertex3d(xlab, ylab, zlab);
      painter.glEnd();

      // Select the alignement of the tick label
      if (hal == null)
        hAlign = painter.getCamera().side(tickPosition) ? Horizontal.LEFT : Horizontal.RIGHT;
      else
        hAlign = hal;

      if (val == null) {
        if (direction == AXE_Z)
          vAlign = Vertical.CENTER;
        else {
          if (zdir > 0)
            vAlign = Vertical.TOP;
          else
            vAlign = Vertical.BOTTOM;
        }
      } else
        vAlign = val;

      // Draw the text label of the current tick
      if (txtRenderer != null)
        txtRenderer.appendText(painter, tickLabel, tickPosition, hAlign, vAlign, color);
      else {
        BoundingBox3d tickBounds =
            textRenderer.drawText(painter, getLayout().getFont(), tickLabel, tickPosition, hAlign, vAlign, color);
        if (tickBounds != null)
          ticksTxtBounds.add(tickBounds);
      }
    }

    return ticksTxtBounds;
  }*/

  protected AWTTextOverlay txtRenderer; // keep it null in order to not use it
}
