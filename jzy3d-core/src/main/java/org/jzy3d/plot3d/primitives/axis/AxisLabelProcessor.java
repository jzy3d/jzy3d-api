package org.jzy3d.plot3d.primitives.axis;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.rendering.view.View2DLayout;
import org.jzy3d.plot3d.text.align.Horizontal;
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
      BoundingBox3d ticksTxtBounds, Coord3d labelPosition, String axeLabel, float rotation, Coord2d offset) {
    if (axis.isXAxeLabelDisplayed(direction) || axis.isYAxeLabelDisplayed(direction) || axis.isZAxeLabelDisplayed(direction)) {
      // not fully relevant
      /*
       * if(spaceTransformer!=null){ labelPosition = spaceTransformer.compute(labelPosition); }
       */

      Horizontal hal = null;
      Vertical val = null;

      if(axis.getView().is2D()) {
        if(axis.isX(direction)) {
          hal = Horizontal.CENTER;
          val = Vertical.BOTTOM;
        }
        else if(axis.isY(direction)) {
          hal = Horizontal.LEFT;
          val = Vertical.CENTER;
        }
      }
      else {
        hal = Horizontal.CENTER;
        val = Vertical.CENTER;
      }

      BoundingBox3d labelBounds = axis.textRenderer.drawText(painter, layout.getFont(), axeLabel,
          labelPosition, rotation, hal, val, color, offset);
      if (labelBounds != null)
        ticksTxtBounds.add(labelBounds);
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
    int fontHeight = (int)(layout.getFont().getHeight());// * pixelScale.y
    
    float labelBorderX = 0;
    
    for (int i = 0; i < info.tickValues.length; i++) {
      
      Coord3d t2d = painter.modelToScreen(info.tickLabelPositions[i]);
      int tickLabelWidth = (int)(painter.getTextLengthInPixels(layout.getFont(), info.tickLabels[i]));//* pixelScale.x
      
      //System.out.println(pixelScale);
      if(ZAxisSide.LEFT.equals(layout.getZAxisSide())) {
        labelBorderX = t2d.x - tickLabelWidth - fontHeight/2 - margin * pixelScale.x; 
        // (text is layout at center, so divide by half font height
        
      }
      else if(ZAxisSide.RIGHT.equals(layout.getZAxisSide())) {
        labelBorderX = t2d.x + tickLabelWidth + fontHeight/2 - margin * pixelScale.x;
      }

    }
    if(ZAxisSide.LEFT.equals(layout.getZAxisSide())) {
      if(lab2D.x > labelBorderX) {
        offset2D.x = labelBorderX - lab2D.x;          
      }
    }
    else if(ZAxisSide.RIGHT.equals(layout.getZAxisSide())) {
      if(lab2D.x < labelBorderX) {
        offset2D.x = labelBorderX - lab2D.x;          
      }      
    }
    
    
    
    return offset2D;
  }
  
  /**
   * Offset for oblique labels
   * @param painter
   * @param info
   * @param labelPosition
   * @param margin
   * @return
   */
  protected Coord2d axisLabelOffset(IPainter painter, AxisRenderingInfo info,
      Coord3d labelPosition, int margin) {
    
    Coord2d pixelScale = painter.getView().getPixelScale();

    // 2D label position
    Coord3d label2D = painter.modelToScreen(labelPosition);
    int fontHeight = layout.getFont().getHeight();
    
    // Compute longest tick label in pixels
    int maxTickLabelWidth = 0;
    for (int i = 0; i < info.tickValues.length; i++) {
      int tickLabelWidth = painter.getTextLengthInPixels(layout.getFont(), info.tickLabels[i]);
      if(tickLabelWidth>maxTickLabelWidth) {
        maxTickLabelWidth = tickLabelWidth;
      }
    }

    // Compute position of the middle tick
    int middle = info.tickValues.length/2;
    Coord3d tick2D = painter.modelToScreen(info.tickLabelPositions[middle]);

    // Finally compute offset
    Coord2d offset2D = new Coord2d();
    float labelBorderX = 0;

    if(painter.getCamera().side(labelPosition)) { // left side offset
      labelBorderX = tick2D.x - maxTickLabelWidth - fontHeight/2 - margin  * pixelScale.x; 
      // (text is layout at center, so divide by half font height
      
      // TODO : consider TEXT ROTATION to guess correct max width
      
      if(label2D.x > labelBorderX) {
        offset2D.x = labelBorderX - label2D.x;          
      }
    }
    else  { // right side offset
      labelBorderX = tick2D.x + maxTickLabelWidth + fontHeight/2 + margin  * pixelScale.x;

      // TODO : consider TEXT ROTATION to guess correct max width

      if(label2D.x < labelBorderX) {
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
   */
  protected Coord3d axisLabelPosition(int direction, float tickLength, Coord3d pos, Coord3d dir) {
    double xlab = 0;
    double ylab = 0;
    double zlab = 0;

    // 3D case : process a shift from the center of the axis to annotate
    // based on the tick length (hence distance between tick text and axis)
    // and a distance factor of the axis label w.r.t. the tick length
    if(axis.getView().is3D()) {
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
    }
    
    // 2D case : processed in 2D according to pixel margins, font size, and a 
    // pixel to world dimension ratio returned by the view
    else {
      // X : position axe + margin + tickLabelHeight + axisLabelMargin
      // Y : position axe + margin + tickLabelHeight OR Width (rotation) + axisLabelMargin
      
      // Compute space in pixel of the tick labels for X and Y axis
      int txtHorizontal = axis.getLayout().getMaxYTickLabelWidth(axis.getView().getPainter());
      int txtVertical = axis.getLayout().getFont().getHeight();
      
      // Get total shift to axis for the label, given in pixel distance
      View2DLayout layout2D = axis.getView().getLayout_2D();
      
      // for Y axis label
      float xShiftPx = layout2D.getyTickLabelsDistance() + txtHorizontal + layout2D.getyAxisLabelsDistance();
      
      // for X axis label
      float yShiftPx = layout2D.getxTickLabelsDistance() + txtVertical + layout2D.getxAxisLabelsDistance();
      
      // Add self dimension of the axis label
      //xShiftPx+=axis.getView().getPainter().getTextLengthInPixels(axis.getLayout().getFont(), axis.getLayout().getYAxisLabel());
      
      // Convert pixel/2D shift into 3D world distance
      Coord2d modelToScreenRatio = axis.getView().getModelToScreenRatio();
      float xShift = xShiftPx * modelToScreenRatio.x;
      float yShift = yShiftPx * modelToScreenRatio.y;
      
      if (axis.isX(direction)) {
        xlab = axis.center.x;
        ylab = pos.y - yShift;
        zlab = pos.z;
      } else if (axis.isY(direction)) {
        xlab = pos.x - xShift;
        ylab = axis.center.y;
        zlab = pos.z;
      } /*else {
        xlab = axeLabelDist * (axis.xrange / tickLength) * dir.x + pos.x;
        ylab = axeLabelDist * (axis.yrange / tickLength) * dir.y + pos.y;
      } */ 
      
    }
    return new Coord3d(xlab, ylab, zlab); 
  }
}
