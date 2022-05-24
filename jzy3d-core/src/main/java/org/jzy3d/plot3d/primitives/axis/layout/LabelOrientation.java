package org.jzy3d.plot3d.primitives.axis.layout;

import org.jzy3d.plot3d.rendering.view.modes.CameraMode;

public enum LabelOrientation {
  /** Default orientation of a label */
  HORIZONTAL, 
  /** Default orientation of a label */
  VERTICAL, 
  /** 
   * Process the labelled axis orientation in order to let the text be drawn along the axis.
   * 
   * Do not use for Z axis as it is always vertical if {@link CameraMode#ORTHOGONAL} is enabled ({@link CameraMode#PERSPECTIVE} makes Z axis not vertical on the screen)
   * Setting Vertical for Z axis label is more efficient. */
  PARALLEL_TO_AXIS
}
