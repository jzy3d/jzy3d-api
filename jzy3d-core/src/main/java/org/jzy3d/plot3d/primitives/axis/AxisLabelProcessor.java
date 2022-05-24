package org.jzy3d.plot3d.primitives.axis;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.View2DLayout;
import org.jzy3d.plot3d.rendering.view.View2DProcessing;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.TextAlign;
import org.jzy3d.plot3d.text.align.Vertical;

/**
 * A helper class to process axis labels.
 * 
 * @author martin
 */
public class AxisLabelProcessor {
  protected AxisBox axis;
  protected AxisLayout layout;

  public AxisLabelProcessor(AxisBox axis) {
    this.axis = axis;
    this.layout = axis.getLayout();
  }

  protected void drawAxisLabel(IPainter painter, int direction, Color color,
      BoundingBox3d ticksTxtBounds, Coord3d position, String label, float rotation,
      Coord2d offset) {

    boolean shouldDisplayLabel = axis.isXAxeLabelDisplayed(direction)
        || axis.isYAxeLabelDisplayed(direction) || axis.isZAxeLabelDisplayed(direction);

    if (shouldDisplayLabel) {

      TextAlign align = getAxisLabelTextAlign(direction);

      BoundingBox3d labelBounds = axis.textRenderer.drawText(painter, layout.getFont(), label,
          position, rotation, align, color, offset);
      if (labelBounds != null)
        ticksTxtBounds.add(labelBounds);
    }
  }

  protected TextAlign getAxisLabelTextAlign(int direction) {

    // 2D case
    if (axis.getView().is2D()) {
      // X axis label
      if (axis.isX(direction)) {
        return new TextAlign(Horizontal.CENTER, Vertical.BOTTOM);
      }
      // Y axis label
      else if (axis.isY(direction)) {
        return new TextAlign(Horizontal.LEFT, Vertical.CENTER);
      }
      // Z axis label : should be hidden
      else {
        return new TextAlign(Horizontal.CENTER, Vertical.CENTER);
      }
    }

    // 3D case
    else {
      return new TextAlign(Horizontal.CENTER, Vertical.CENTER);
    }

  }


  /**
   * Compute the offset to apply to a vertical Z label to avoid covering the tick labels.
   * 
   * Retrieve pixel scale in view to adapt margin
   * 
   * @param painter
   * @param info
   * @param labelPosition
   * @param offset2D
   * @param margin
   */
  protected Coord2d axisLabelOffsetVertical(IPainter painter, AxisRenderingInfo info,
      Coord3d labelPosition, int margin) {

    Coord2d offset2D = new Coord2d();
    Coord2d pixelScale = painter.getView().getPixelScale();
    Coord3d lab2D = painter.modelToScreen(labelPosition);
    int fontHeight = (int) (layout.getFont().getHeight());// * pixelScale.y

    float labelBorderX = 0;

    for (int i = 0; i < info.tickValues.length; i++) {

      Coord3d t2d = painter.modelToScreen(info.tickLabelPositions[i]);
      int tickLabelWidth =
          (int) (painter.getTextLengthInPixels(layout.getFont(), info.tickLabels[i]));// *
                                                                                      // pixelScale.x

      // System.out.println(pixelScale);
      if (ZAxisSide.LEFT.equals(layout.getZAxisSide())) {
        labelBorderX = t2d.x - tickLabelWidth - fontHeight / 2 - margin * pixelScale.x;
        // (text is layout at center, so divide by half font height

      } else if (ZAxisSide.RIGHT.equals(layout.getZAxisSide())) {
        labelBorderX = t2d.x + tickLabelWidth + fontHeight / 2 - margin * pixelScale.x;
      }

    }
    if (ZAxisSide.LEFT.equals(layout.getZAxisSide())) {
      if (lab2D.x > labelBorderX) {
        offset2D.x = labelBorderX - lab2D.x;
      }
    } else if (ZAxisSide.RIGHT.equals(layout.getZAxisSide())) {
      if (lab2D.x < labelBorderX) {
        offset2D.x = labelBorderX - lab2D.x;
      }
    }



    return offset2D;
  }

  /**
   * Offset for oblique labels
   * 
   * @param painter
   * @param info
   * @param labelPosition
   * @param margin
   * @return
   */
  protected Coord2d axisLabelOffset(IPainter painter, AxisRenderingInfo info, Coord3d labelPosition,
      int margin) {

    Coord2d pixelScale = painter.getView().getPixelScale();

    // 2D label position
    Coord3d label2D = painter.modelToScreen(labelPosition);
    int fontHeight = layout.getFont().getHeight();

    // Compute longest tick label in pixels
    int maxTickLabelWidth = 0;
    for (int i = 0; i < info.tickValues.length; i++) {
      int tickLabelWidth = painter.getTextLengthInPixels(layout.getFont(), info.tickLabels[i]);
      if (tickLabelWidth > maxTickLabelWidth) {
        maxTickLabelWidth = tickLabelWidth;
      }
    }

    // Compute position of the middle tick
    int middle = info.tickValues.length / 2;
    Coord3d tick2D = painter.modelToScreen(info.tickLabelPositions[middle]);

    // Finally compute offset
    Coord2d offset2D = new Coord2d();
    float labelBorderX = 0;

    if (painter.getCamera().side(labelPosition)) { // left side offset
      labelBorderX = tick2D.x - maxTickLabelWidth - fontHeight / 2 - margin * pixelScale.x;
      // (text is layout at center, so divide by half font height

      // TODO : consider TEXT ROTATION to guess correct max width

      if (label2D.x > labelBorderX) {
        offset2D.x = labelBorderX - label2D.x;
      }
    } else { // right side offset
      labelBorderX = tick2D.x + maxTickLabelWidth + fontHeight / 2 + margin * pixelScale.x;

      // TODO : consider TEXT ROTATION to guess correct max width

      if (label2D.x < labelBorderX) {
        offset2D.x = labelBorderX - label2D.x;
      }
    }

    return offset2D;
  }


  /**
   * Compute axis label rotation according to settings and direction of the axis segment.
   */
  protected float axisLabelRotation(IPainter painter, int direction, Coord3d[] axisSegment) {
    float rotation = 0;

    LabelOrientation orientation = null;
    if (axis.isX(direction)) {
      orientation = layout.getXAxisLabelOrientation();
    } else if (axis.isY(direction)) {
      orientation = layout.getYAxisLabelOrientation();
    } else if (axis.isZ(direction)) {
      orientation = layout.getZAxisLabelOrientation();
    }
    if (LabelOrientation.VERTICAL.equals(orientation)) {
      rotation = -(float) Math.PI / 2;

      // In case Z label on right, flip text to have it written
      if (axis.isZ(direction) && ZAxisSide.RIGHT.equals(layout.getZAxisSide())) {
        rotation *= -1;
      }
    } else if (LabelOrientation.PARALLEL_TO_AXIS.equals(orientation)) {
      rotation = axis.rotateLabel.computeSegmentRotation2D(painter, axisSegment);
    }
    return rotation;
  }

  /**
   * Return the axis label for the given direction.
   */
  protected String axisLabel(int dimension) {
    String axeLabel;
    if (axis.isX(dimension)) {
      axeLabel = layout.getXAxisLabel();
    } else if (axis.isY(dimension)) {
      axeLabel = layout.getYAxisLabel();
    } else {
      axeLabel = layout.getZAxisLabel();
    }
    return axeLabel;
  }

  /**
   * Compute the axis label position according to the tick position (on the axis segment) and a
   * direction for the tick.
   * 
   * This is quite intricated with {@link View#computeCamera3D_RenderingSphere} and
   * {@link View#computeCamera2D_RenderingSquare} which will perform the actual camera configuration
   * to ensure enough white space around the scene to draw the axis and tick labels
   * 
   */
  protected Coord3d axisLabelPosition(int direction, float tickLength, Coord3d pos, Coord3d dir) {


    if (axis.getView().is3D()) {

      return axisLabelPosition_3D(direction, tickLength, pos, dir);
    } else {
      return axisLabelPosition_2D(direction, pos);

    }
  }

  /**
   * 2D case : processed in 2D according to pixel margins, font size, and a pixel to world dimension
   * ratio returned by the view
   * 
   * @see {@link View#computeCamera2D_RenderingSquare}
   * @see {@link #getAxisLabelTextAlign(int)}
   */
  protected Coord3d axisLabelPosition_2D(int direction, Coord3d pos) {
    // X : position axis + margin + tick Label Height + axis Label Margin
    // Y : position axis + margin + tick Label Height OR Width (rotation) + axis Label Margin

    View view = axis.getView();
    AxisLayout axisLayout = axis.getLayout();
    View2DLayout layout2D = view.get2DLayout();
    View2DProcessing processing2D = view.get2DProcessing();

    // for Y axis label, we do a shift along X dimension
    float xShiftPx = layout2D.getyTickLabelsDistance();
    xShiftPx += processing2D.getTickTextHorizontal();
    xShiftPx += layout2D.getyAxisLabelsDistance();
    
    if(!LabelOrientation.HORIZONTAL.equals(axisLayout.getYAxisLabelOrientation())) {
      
      // consider the rotation & offset due to vertical text 
      int textLength = view.getPainter().getTextLengthInPixels(axisLayout.getFont(), axisLayout.getYAxisLabel());
      
      // hack the emulgl vertical Y axis case 
      if(!view.getCanvas().isNative())
      textLength /= view.getPixelScale().y;
      
      xShiftPx -= textLength/2;
      
      int textHeight = axisLayout.getFont().getHeight();
      
      // hack the emulgl vertical Y axis case 
      if(!view.getCanvas().isNative())
        textHeight /= view.getPixelScale().x;

      xShiftPx += textHeight/2;
    }
    

    // for X axis label, we do a shift along Y dimension
    float yShiftPx = layout2D.getxTickLabelsDistance();
    yShiftPx += processing2D.getTickTextVertical();
    yShiftPx += layout2D.getxAxisLabelsDistance();
    
    
    
    Coord2d modelToScreenRatio = view.get2DProcessing().getModelToScreen();

    float xShift = xShiftPx * modelToScreenRatio.x;
    float yShift = yShiftPx * modelToScreenRatio.y;

    double xlab = 0;
    double ylab = 0;
    double zlab = 0;

    if (axis.isX(direction)) {
      xlab = axis.center.x;
      ylab = pos.y - yShift;
      zlab = pos.z;
    } else if (axis.isY(direction)) {
      xlab = pos.x - xShift;
      ylab = axis.center.y;
      zlab = pos.z;
    }
    return new Coord3d(xlab, ylab, zlab);
  }

  /**
   * 3D case : process a shift from the center of the axis to annotate based on the tick length
   * (hence distance between tick text and axis) and a distance factor of the axis label w.r.t. the
   * tick length
   * 
   * @see {@link View#computeCamera3D_RenderingSphere}
   */
  protected Coord3d axisLabelPosition_3D(int direction, float tickLength, Coord3d pos,
      Coord3d dir) {
    double xlab = 0;
    double ylab = 0;
    double zlab = 0;

    float axisLabelDistanceFactor = this.axis.getLayout().getAxisLabelDistance();

    if (axis.isX(direction)) {
      xlab = axis.center.x;
      ylab = axisLabelDistanceFactor * (axis.yrange / tickLength) * dir.y + pos.y;
      zlab = axisLabelDistanceFactor * (axis.zrange / tickLength) * dir.z + pos.z;
    } else if (axis.isY(direction)) {
      xlab = axisLabelDistanceFactor * (axis.xrange / tickLength) * dir.x + pos.x;
      ylab = axis.center.y;
      zlab = axisLabelDistanceFactor * (axis.zrange / tickLength) * dir.z + pos.z;
    } else {
      xlab = axisLabelDistanceFactor * (axis.xrange / tickLength) * dir.x + pos.x;
      ylab = axisLabelDistanceFactor * (axis.yrange / tickLength) * dir.y + pos.y;
      zlab = axis.center.z;
    }

    return new Coord3d(xlab, ylab, zlab);
  }
}
