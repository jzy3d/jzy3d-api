package org.jzy3d.plot3d.primitives;

import java.util.List;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Lists;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.StencilFunc;
import org.jzy3d.painters.StencilOp;
import org.jzy3d.plot3d.transform.Transform;

/**
 * This pseudo-drawable will handle drawing outlines (such as polygon border), contour line (lying
 * exactly ON a polygon), or text which position is ON a polygon) that may be coplanar or partially
 * hidden by a collection of polygons.
 * 
 * This allows rendering outlines properly and hence deals with Z-fighting artifacts, 2d text
 * crossing 3D contents, etc.
 * 
 * @author Martin Pernollet
 *
 */
public class CoplanarityManager extends Drawable implements IGLBindedResource {
  protected List<? extends Drawable> outlines;
  protected Drawable plane;

  public CoplanarityManager(Drawable outline, Drawable plane) {
    this(Lists.of(outline), plane);
  }

  public CoplanarityManager(List<? extends Drawable> outlines, Drawable plane) {
    this.outlines = outlines;
    this.plane = plane;

    computeBounds(outlines, plane);
  }

  /**
   * Will try to compute bounds if they are available. This should be invoked
   * <ul>
   * <li>While building the object
   * <li>While mounting the object, has it may contain resource that have their bounds known after
   * mounting.
   * </ul>
   * 
   * @param outlines
   * @param plane
   */
  protected void computeBounds(List<? extends Drawable> outlines, Drawable plane) {
    this.bbox = new BoundingBox3d();

    if (plane.getBounds() != null && !plane.getBounds().isReset()) {
      bbox.add(plane.getBounds());
    }

    for (Drawable outline : outlines) {
      if (outline.getBounds() != null && !outline.getBounds().isReset()) {
        bbox.add(outline.getBounds());
      }
    }
  }

  /**
   * Implements recipe given by
   * 
   * @see https://www.opengl.org/archives/resources/code/samples/sig99/advanced99/notes/node20.html
   */
  @Override
  public void draw(IPainter painter) {

    // Turn on stenciling
    painter.glEnable_Stencil();

    // Set stencil function to always pass
    painter.glStencilFunc(StencilFunc.GL_ALWAYS, 1, 1);

    // Set stencil op to set 1 if depth passes, 0 if it fails
    painter.glStencilOp(StencilOp.GL_KEEP, StencilOp.GL_ZERO, StencilOp.GL_REPLACE);

    // Draw the base polygons
    plane.draw(painter);

    // Set stencil function to pass when stencil is 1
    painter.glStencilFunc(StencilFunc.GL_EQUAL, 1, 1);

    // Disable writes to stencil buffer
    painter.glStencilMask_False();

    // Turn off depth buffering
    painter.glDisable_DepthTest();

    // Render the overlying drawables
    for (Drawable d : outlines) {
      d.draw(painter);
    }

    // Reset states
    if (!painter.getQuality().isAlphaActivated()) {
      if (painter.getQuality().isDepthActivated()) {
        painter.glEnable_DepthTest();
      }
    } else if (!painter.getQuality().isDisableDepthBufferWhenAlpha()) {
      painter.glEnable_DepthTest();
    }

    painter.glDisable_Stencil();

    // could also try
    // https://www.khronos.org/opengl/wiki/Drawing_Coplanar_Primitives_Widthout_Polygon_Offset
  }

  @Override
  public void applyGeometryTransform(Transform transform) {}

  @Override
  public void updateBounds() {
    for (Drawable outline : outlines) {
      outline.updateBounds();
    }
    plane.updateBounds();

    computeBounds(outlines, plane);
  }

  @Override
  public void mount(IPainter painter) {
    mount(painter, plane);

    for (Drawable d : outlines) {
      mount(painter, d);
    }

    // update bounds now that mounted elements have their bounds known
    computeBounds(outlines, plane);

    mounted = true;
  }

  boolean mounted = false;

  protected void mount(IPainter painter, Drawable d) {
    if (d instanceof IGLBindedResource) {
      ((IGLBindedResource) d).mount(painter);
    }
  }

  @Override
  public boolean hasMountedOnce() {
    return mounted;
  }

}
