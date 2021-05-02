package org.jzy3d.plot3d.primitives.axis;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;

/**
 * A helper class to process and draw axis ticks. Processing requires calling there the
 * {@link AxisLabelProcessor} methods.
 * 
 * @author Martin Pernollet
 */
public class AxisTickProcessor {
  protected AxisBox axis;
  protected IAxisLayout layout;
  protected AxisLabelProcessor labels;

  public AxisTickProcessor(AxisBox axis) {
    this.axis = axis;
    this.layout = axis.getLayout();
    this.labels = axis.labels;
  }

  public BoundingBox3d drawTicks(IPainter painter, int axis, int dimension, Color color) {
    return drawTicks(painter, axis, dimension, color, null, null);
  }

  /** Draws axis labels, tick lines and tick label */
  public BoundingBox3d drawTicks(IPainter painter, int axis, int dimension, Color color,
      Horizontal hal, Vertical val) {
    int quad_0;
    int quad_1;
    float tickLength = 20.0f; // with respect to range
    float axeLabelDist = 2.5f;
    BoundingBox3d ticksTxtBounds = new BoundingBox3d();

    // Retrieve the quads that intersect and create the selected axe
    if (this.axis.isX(dimension)) {
      quad_0 = this.axis.axeXquads[axis][0];
      quad_1 = this.axis.axeXquads[axis][1];
    } else if (this.axis.isY(dimension)) {
      quad_0 = this.axis.axeYquads[axis][0];
      quad_1 = this.axis.axeYquads[axis][1];
    } else { // (axis==AXE_Z)
      quad_0 = this.axis.axeZquads[axis][0];
      quad_1 = this.axis.axeZquads[axis][1];
    }

    // --------------------------------------------------------------
    // Computes POSition of ticks lying on the selected axe (i.e. 1st point of the tick line)

    Coord3d pos = tickPosition(quad_0, quad_1);

    // Computes the DIRection of the ticks assuming initial vector point is the center
    Coord3d dir = tickDirection(quad_0, quad_1);

    // Do draw ticks
    AxisRenderingInfo info = drawAxisTicks(painter, dimension, color, hal, val, tickLength,
        ticksTxtBounds, pos, dir, this.axis.getAxisTicks(dimension));

    // --------------------------------------------------------------
    // Variables for storing the position of the LABel position
    // (2nd point on the tick line)

    String axisLabel = labels.axisLabel(dimension);
    float rotation = labels.axisLabelRotation(painter, dimension, info.axisSegment);
    Coord3d labelPosition = labels.axisLabelPosition(dimension, tickLength, axeLabelDist, pos, dir);


    // --------------------------------------------------------------
    // Verify if needs a left/right offset to avoid covering tick labels

    Coord2d offset2D = null;

    if (layout.isAxisLabelOffsetAuto()) {
      if (this.axis.isZ(dimension)) {
        if (LabelOrientation.VERTICAL.equals(layout.getZAxisLabelOrientation())) {
          offset2D = labels.axisLabelOffsetVertical(painter, info, labelPosition,
              layout.getAxisLabelOffsetMargin());
        } else if (LabelOrientation.PARALLEL_TO_AXIS.equals(layout.getZAxisLabelOrientation())) {
          offset2D = labels.axisLabelOffset(painter, info, labelPosition,
              layout.getAxisLabelOffsetMargin());
        }
      } else if (this.axis.isX(dimension)
          && !LabelOrientation.HORIZONTAL.equals(layout.getXAxisLabelOrientation())) {
        offset2D =
            labels.axisLabelOffset(painter, info, labelPosition, layout.getAxisLabelOffsetMargin());
      } else if (this.axis.isY(dimension)
          && !LabelOrientation.HORIZONTAL.equals(layout.getYAxisLabelOrientation())) {
        offset2D =
            labels.axisLabelOffset(painter, info, labelPosition, layout.getAxisLabelOffsetMargin());
      }
    }
    if (offset2D == null) {
      offset2D = new Coord2d();
    }

    // --------------------------------------------------------------
    // Do draw axis label

    labels.drawAxisLabel(painter, dimension, color, ticksTxtBounds, labelPosition, axisLabel,
        rotation, offset2D);

    return ticksTxtBounds;
  }

  public AxisRenderingInfo drawAxisTicks(IPainter painter, int dimension, Color color,
      Horizontal hal, Vertical val, float tickLength, BoundingBox3d ticksTxtBounds, Coord3d pos,
      Coord3d dir, double[] ticks) {

    return drawAxisTicks(painter, dimension, color, hal, val, tickLength, ticksTxtBounds, pos.x,
        pos.y, pos.z, dir.x, dir.y, dir.z, ticks);

  }

  /**
   * Draw an array of ticks on the given axis indicated by direction field.
   * 
   * Return the segment of the axis.
   */
  public AxisRenderingInfo drawAxisTicks(IPainter painter, int dimension, Color color,
      Horizontal hal, Vertical val, float tickLength, BoundingBox3d ticksTxtBounds, double xpos,
      double ypos, double zpos, float xdir, float ydir, float zdir, double[] ticks) {
    double xlab;
    double ylab;
    double zlab;
    String tickLabel = "";

    AxisRenderingInfo info = new AxisRenderingInfo();
    info.axisSegment = new Coord3d[2];
    info.tickValues = ticks;
    info.tickLabels = new String[ticks.length];
    info.tickLabelPositions = new Coord3d[ticks.length];

    // Coord3d[] axisSegment = new Coord3d[2];

    for (int t = 0; t < ticks.length; t++) {
      // Shift the tick vector along the selected axis
      // and set the tick length
      if (axis.spaceTransformer == null) {
        if (axis.isX(dimension)) {

          // Tick position
          xpos = ticks[t];
          xlab = xpos;
          ylab = (axis.yrange / tickLength) * ydir + ypos;
          zlab = (axis.zrange / tickLength) * zdir + zpos;

          // Tick label
          tickLabel = layout.getXTickRenderer().format(ticks[t]);

        } else if (axis.isY(dimension)) {

          // Tick position
          ypos = ticks[t];
          xlab = (axis.xrange / tickLength) * xdir + xpos;
          ylab = ypos;
          zlab = (axis.zrange / tickLength) * zdir + zpos;

          // Tick label
          tickLabel = layout.getYTickRenderer().format(ticks[t]);

        } else { // (axis==AXE_Z)

          // Tick position
          zpos = ticks[t];
          xlab = (axis.xrange / tickLength) * xdir + xpos;
          ylab = (axis.yrange / tickLength) * ydir + ypos;
          zlab = zpos;

          // Tick label
          tickLabel = layout.getZTickRenderer().format(ticks[t]);
        }
      } else {
        // use space transform shift if we have a space transformer
        if (axis.isX(dimension)) {
          xpos = axis.spaceTransformer.getX().compute((float) ticks[t]);
          xlab = xpos;
          ylab = Math.signum(tickLength * ydir)
              * (axis.yrange / axis.spaceTransformer.getY().compute(Math.abs(tickLength)))
              * axis.spaceTransformer.getY().compute(Math.abs(ydir)) + ypos;
          zlab = Math.signum(tickLength * ydir)
              * (axis.zrange / axis.spaceTransformer.getZ().compute(Math.abs(tickLength)))
              * axis.spaceTransformer.getZ().compute(Math.abs(zdir)) + zpos;
          tickLabel = layout.getXTickRenderer().format(ticks[t]);
        } else if (axis.isY(dimension)) {
          ypos = axis.spaceTransformer.getY().compute((float) ticks[t]);
          xlab = Math.signum(tickLength * xdir)
              * (axis.xrange / axis.spaceTransformer.getX().compute(Math.abs(tickLength)))
              * axis.spaceTransformer.getX().compute(Math.abs(xdir)) + xpos;
          ylab = ypos;
          zlab = Math.signum(tickLength * zdir)
              * (axis.zrange / axis.spaceTransformer.getZ().compute(Math.abs(tickLength)))
              * axis.spaceTransformer.getZ().compute(Math.abs(zdir)) + zpos;
          tickLabel = layout.getYTickRenderer().format(ticks[t]);
        } else { // (axis==AXE_Z)
          zpos = axis.spaceTransformer.getZ().compute((float) ticks[t]);
          xlab = Math.signum(tickLength * xdir)
              * (axis.xrange / axis.spaceTransformer.getX().compute(Math.abs(tickLength)))
              * axis.spaceTransformer.getX().compute(Math.abs(xdir)) + xpos;
          ylab = Math.signum(tickLength * ydir)
              * (axis.yrange / axis.spaceTransformer.getY().compute(Math.abs(tickLength)))
              * axis.spaceTransformer.getY().compute(Math.abs(ydir)) + ypos;
          zlab = zpos;
          tickLabel = layout.getZTickRenderer().format(ticks[t]);
        }
      }
      Coord3d tickLabelPosition = new Coord3d(xlab, ylab, zlab);
      Coord3d tickStartPosition = new Coord3d(xpos, ypos, zpos);

      // Select the alignement of the tick label
      Horizontal hAlign = align(hal, dimension, painter.getCamera(), tickLabelPosition);
      Vertical vAlign = align(val, dimension, zdir);

      // Draw the text label of the current tick
      drawAxisTickNumericLabel(painter, dimension, color, hAlign, vAlign, ticksTxtBounds, tickLabel,
          tickLabelPosition);

      // Draw the tick line
      if (layout.isTickLineDisplayed()) {
        drawTickLine(painter, color, tickStartPosition, tickLabelPosition);
      }


      // ------------------------
      // Remember the axis info
      if (t == 0) {
        info.axisSegment[0] = tickStartPosition;
      } else if (t == (ticks.length - 1)) {
        info.axisSegment[1] = tickStartPosition;
      }
      info.tickLabels[t] = tickLabel;
      info.tickLabelPositions[t] = tickLabelPosition;
    }

    return info;
  }

  public void drawAxisTickNumericLabel(IPainter painter, int direction, Color color,
      Horizontal hAlign, Vertical vAlign, BoundingBox3d ticksTxtBounds, String tickLabel,
      Coord3d tickPosition) {
    painter.glLoadIdentity();
    painter.glScalef(axis.scale.x, axis.scale.y, axis.scale.z);

    BoundingBox3d tickBounds = axis.textRenderer.drawText(painter, layout.getFont(), tickLabel,
        tickPosition, hAlign, vAlign, color);
    if (tickBounds != null)
      ticksTxtBounds.add(tickBounds);
  }

  /** Compute the vertical alignment of a tick label based on direction in Z space. */
  public Vertical align(Vertical alignDefault, int dimension, float zdir) {
    Vertical align;
    if (alignDefault == null) {
      if (axis.isZ(dimension))
        align = Vertical.CENTER;
      else {
        if (zdir > 0)
          align = Vertical.TOP;
        else
          align = Vertical.BOTTOM;
      }
    } else
      align = alignDefault;
    return align;
  }

  /**
   * Compute horizontal alignement of a tick label based on tick position relative to camera, if the
   * input default is null
   */
  public Horizontal align(Horizontal alignDefault, int dimension, Camera cam,
      Coord3d tickPosition) {
    Horizontal align;
    if (alignDefault == null)
      align = cam.side(tickPosition) ? Horizontal.LEFT : Horizontal.RIGHT;
    else
      align = alignDefault;
    return align;
  }

  /** Actually draws the line. */
  public void drawTickLine(IPainter painter, Color color, Coord3d pos, Coord3d lab) {
    painter.glLoadIdentity();
    painter.glScalef(axis.scale.x, axis.scale.y, axis.scale.z);

    painter.color(color);
    painter.glLineWidth(1);

    // Draw the tick line
    painter.glBegin_Line();
    painter.glVertex3d(pos.x, pos.y, pos.z);
    painter.glVertex3d(lab.x, lab.y, lab.z);
    painter.glEnd();
  }

  // ********************* INTERNAL *********************** //

  protected Coord3d tickPosition(int quad_0, int quad_1) {
    double xpos = 0;
    double ypos = 0;
    double zpos = 0;

    if (axis.spaceTransformer == null) {
      xpos = axis.normx[quad_0] + axis.normx[quad_1];
      ypos = axis.normy[quad_0] + axis.normy[quad_1];
      zpos = axis.normz[quad_0] + axis.normz[quad_1];
    } else {
      xpos = axis.spaceTransformer.getX().compute(axis.normx[quad_0])
          + axis.spaceTransformer.getX().compute(axis.normx[quad_1]);
      ypos = axis.spaceTransformer.getY().compute(axis.normy[quad_0])
          + axis.spaceTransformer.getY().compute(axis.normy[quad_1]);
      zpos = axis.spaceTransformer.getZ().compute(axis.normz[quad_0])
          + axis.spaceTransformer.getZ().compute(axis.normz[quad_1]);
    }

    Coord3d pos = new Coord3d(xpos, ypos, zpos);
    return pos;
  }

  protected Coord3d tickDirection(int quad_0, int quad_1) {
    float xdir = (axis.normx[quad_0] + axis.normx[quad_1]) - axis.center.x;
    float ydir = (axis.normy[quad_0] + axis.normy[quad_1]) - axis.center.y;
    float zdir = (axis.normz[quad_0] + axis.normz[quad_1]) - axis.center.z;

    xdir = xdir == 0 ? 0 : xdir / Math.abs(xdir); // so that direction has length 1
    ydir = ydir == 0 ? 0 : ydir / Math.abs(ydir);
    zdir = zdir == 0 ? 0 : zdir / Math.abs(zdir);

    return new Coord3d(xdir, ydir, zdir);
  }
}
