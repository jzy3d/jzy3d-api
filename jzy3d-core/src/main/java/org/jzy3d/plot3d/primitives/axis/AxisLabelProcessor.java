package org.jzy3d.plot3d.primitives.axis;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.AxisBox.AxisRenderingInfo;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;

/**
 * A helper class to process axis labels.
 * 
 * @author martin
 */
public class AxisLabelProcessor {
  protected AxisBox axis;
  protected IAxisLayout layout;

  public AxisLabelProcessor(AxisBox axis) {
    this.axis = axis;
    this.layout = axis.getLayout();
  }

  /**
   * Compute the offset to apply to a vertical Z label to avoid covering the tick labels
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
      int stringWidth = (int)(painter.getTextLengthInPixels(layout.getFont(), info.tickLabels[i]));//* pixelScale.x
      
      
      if(ZAxisSide.LEFT.equals(layout.getZAxisSide())) {
        labelBorderX = t2d.x - stringWidth - fontHeight/2 - margin* pixelScale.x; 
        // (text is layout at center, so divide by half font height
      }
      else if(ZAxisSide.RIGHT.equals(layout.getZAxisSide())) {
        labelBorderX = t2d.x + stringWidth + fontHeight/2 - margin* pixelScale.x;
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
    
    // 2D label position
    Coord3d label2D = painter.modelToScreen(labelPosition);
    int fontHeight = layout.getFont().getHeight();
    
    // Compute longest tick label in pixels
    int maxStringWidth = 0;
    for (int i = 0; i < info.tickValues.length; i++) {
      int stringWidth = painter.getTextLengthInPixels(layout.getFont(), info.tickLabels[i]);
      if(stringWidth>maxStringWidth) {
        maxStringWidth = stringWidth;
      }
    }

    // Compute position of the middle tick
    int middle = info.tickValues.length/2;
    Coord3d tick2D = painter.modelToScreen(info.tickLabelPositions[middle]);

    // Finally compute offset
    Coord2d offset2D = new Coord2d();
    float labelBorderX = 0;

    if(painter.getCamera().side(labelPosition)) { // left side offset
      labelBorderX = tick2D.x - maxStringWidth - fontHeight/2 - margin; 
      // (text is layout at center, so divide by half font height
      if(label2D.x > labelBorderX) {
        offset2D.x = labelBorderX - label2D.x;          
      }
    }
    else  { // right side offset
      labelBorderX = tick2D.x + maxStringWidth + fontHeight/2 - margin;
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
  protected Coord3d axisLabelPosition(int direction, float tickLength, float axeLabelDist,
      Coord3d pos, Coord3d dir) {
    double xlab;
    double ylab;
    double zlab;

    // Draw the label for axis
    int dist = 1;
    if (axis.isX(direction)) {
      xlab = axis.center.x;
      ylab = axeLabelDist * (axis.yrange / tickLength) * dist * dir.y + pos.y;
      zlab = axeLabelDist * (axis.zrange / tickLength) * dist * dir.z + pos.z;
    } else if (axis.isY(direction)) {
      xlab = axeLabelDist * (axis.xrange / tickLength) * dist * dir.x + pos.x;
      ylab = axis.center.y;
      zlab = axeLabelDist * (axis.zrange / tickLength) * dist * dir.z + pos.z;
    } else {
      xlab = axeLabelDist * (axis.xrange / tickLength) * dist * dir.x + pos.x;
      ylab = axeLabelDist * (axis.yrange / tickLength) * dist * dir.y + pos.y;
      zlab = axis.center.z;
    }

    return new Coord3d(xlab, ylab, zlab);
  }

  
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
