package org.jzy3d.plot3d.primitives;

import java.util.List;
import org.jzy3d.maths.Lists;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.StencilFunc;
import org.jzy3d.painters.StencilOp;
import org.jzy3d.plot3d.transform.Transform;

/**
 * This pseudo drawable will handle drawing outline, contour line, or text that may be coplanar or partially hidden by a collection of polygons. 
 * 
 * This allows rendering outlines properly and hence deals with Z-fighting artifacts, 2d text crossing 3D contents, etc.
 * 
 * @author Martin Pernollet
 *
 */
public class CoplanarityManager extends Drawable{
  protected Drawable outline;
  protected List<Drawable> outlines;
  protected Drawable plane;
  
  public CoplanarityManager(Drawable outline, Drawable plane) {
    this(Lists.of(outline), plane);
  }

  public CoplanarityManager(List<Drawable> outlines, Drawable plane) {
    this.outlines = outlines;
    this.plane = plane;
  }

  /**
   * Implements recipe given by
   * 
   * @see https://www.opengl.org/archives/resources/code/samples/sig99/advanced99/notes/node20.html
   */
  @Override
  public void draw(IPainter painter) {
    
    painter.glEnable_Stencil();
    
    painter.glStencilFunc(StencilFunc.GL_ALWAYS, 1, 1);
    painter.glStencilOp(StencilOp.GL_KEEP, StencilOp.GL_ZERO, StencilOp.GL_REPLACE);
    
    plane.draw(painter);
    
    painter.glStencilFunc(StencilFunc.GL_EQUAL, 1, 1);
    
    painter.glStencilMask_False();
    painter.glDisable_DepthTest();
    
    //outline.draw(painter);
    
    for(Drawable d: outlines) {
      d.draw(painter);
    }
    
    // Reset states
    if(!painter.getQuality().isAlphaActivated()) {
      if(painter.getQuality().isDepthActivated()) {
        painter.glEnable_DepthTest();
      }
    }
    else if(!painter.getQuality().isDisableDepthBufferWhenAlpha()){
        painter.glEnable_DepthTest();
    }
    
    painter.glDisable_Stencil();
    
    // could also try https://www.khronos.org/opengl/wiki/Drawing_Coplanar_Primitives_Widthout_Polygon_Offset
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
  }

  @Override
  public void updateBounds() {
  }

}
