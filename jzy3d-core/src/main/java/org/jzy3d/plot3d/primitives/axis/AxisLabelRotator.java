package org.jzy3d.plot3d.primitives.axis;

import org.jzy3d.maths.Angle2d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;

/**
 * An helper class to process the orientation of text according to a driving axis defined by 2 3D or
 * 2D points (already resulting from a screen projection).
 * 
 * @author Martin Pernollet
 *
 */
public class AxisLabelRotator {

  /**
   * Compute the 2D orientation (rotation) of an axis made of 2 3D points. The 2D orientation is
   * processed according to the viewpoint on this segment.
   * 
   * <img src="doc-files/AxisBox-Label.png">
   * 
   * <ul>
   * <li>segment[0] : 3D world coordinates of the starting point of the axis segment.
   * <li>segment[1] : 3D world coordinates of the ending point of the axis segment.
   * </ul>
   * 
   * @return
   */
  public float computeSegmentRotation2D(IPainter painter, Coord3d[] axisSegment) {
    Coord3d[] axs = painter.getCamera().modelToScreen(painter, axisSegment);
    return computeSegmentRotation(axs[0], axs[1]);
  }

  /**
   * Compute the 2D orientation (rotation) of an axis as it is currently displayed to screen (which
   * may change according to camera viewpoint)
   * 
   * <img src="doc-files/AxisBox-Label.png">
   * 
   * @param p1 2D screen coordinates of the starting point of the axis segment.
   * @param p2 2D screen coordinates of the ending point of the axis segment.
   * 
   *        NB : the third dimension of the screen coordinates is the depth of the point compared to
   *        camera. This may be usefull for guessing the direction of the axis : going toward or
   *        away from the viewer according to point order.
   * 
   * @return rotation in radian
   */
  public float computeSegmentRotation(Coord3d p1, Coord3d p2) {
    float rotation = 0;

    boolean sameX = p1.x == p2.x;
    boolean sameY = p1.y == p2.y;

    // case of oblique 2D segment
    if (!sameX && !sameY) {
      Coord2d squareAngle = new Coord2d(p2.x, p1.y);
      Angle2d angle = new Angle2d(squareAngle.x, squareAngle.y, p1.x, p1.y, p2.x, p2.y);

      // points are from left to right
      if (p1.x < p2.x) {
        // points are from bottom to top
        // like the x/y axis when it is on the right and view on top
        if (p1.y < p2.y) {
          rotation = -angle.angle();
        }
        // points are from top to bottom
        // like the x/y axis when it is on the left and view on top
        else {
          rotation = angle.angle();
        }
      }
      // point are from right to left
      else {
        // points are from bottom to top
        if (p1.y < p2.y) {
          rotation = angle.angle(); // flip this
        }
        // points are from top to bottom
        else {
          rotation = -angle.angle(); // flip this
        }
      }
    }
    // case of 2D vertical segment
    else if (sameX && !sameY) {
      // do nothing
      rotation = (float) -Math.PI / 2;
    }
    // case of 2D horizontal segment
    else if (!sameX && sameY) {
      rotation = 0;
    }
    return rotation;
  }
}
