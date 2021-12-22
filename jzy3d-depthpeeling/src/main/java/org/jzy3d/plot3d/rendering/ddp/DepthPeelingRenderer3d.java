package org.jzy3d.plot3d.rendering.ddp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.IGLRenderer;
import org.jzy3d.plot3d.rendering.ddp.algorithms.DualDepthPeelingAlgorithm;
import org.jzy3d.plot3d.rendering.ddp.algorithms.FrontToBackPeelingAlgorithm;
import org.jzy3d.plot3d.rendering.ddp.algorithms.IDepthPeelingAlgorithm;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import org.jzy3d.plot3d.rendering.ddp.algorithms.WeightedAveragePeelingAlgorithm;
import org.jzy3d.plot3d.rendering.ddp.algorithms.WeightedSumPeelingAlgorithm;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

/**
 * Execute depth peeling methods in a Jzy3d {@link Renderer3d}
 * 
 * This feature is based on Order Independent Transparency algorithms published by Louis Bavoil
 * (NVIDIA Corporation).
 * 
 * The renderer support 4 methods:
 * <ul>
 * <li>Dual depth peeling (accurate)
 * <li>Front to back peeling (accurate)
 * <li>Weighted average peeling (fast)
 * <li>Weighted sum peeling (fast)
 * </ul>
 * 
 * Depth peeling is traditionally used to perform order independent transparency (OIT) with N
 * geometry passes for N transparency layers. Dual depth peeling enables peeling N transparency
 * layers in N/2+1 passes, by peeling from the front and the back simultaneously using a min-max
 * depth buffer. This sample performs either normal or dual depth peeling and blends on the fly.
 * 
 * @author Louis Bavoil - original paper and C++ code
 * @author Martin Pernollet - port to Jzy3d
 */
public class DepthPeelingRenderer3d extends AWTRenderer3d {
  protected Logger LOGGER = LogManager.getLogger(DepthPeelingRenderer3d.class);

  protected IDepthPeelingAlgorithm dualPeelingAlgorithm;
  protected GLU glu = new GLU();

  public DepthPeelingRenderer3d(final View view, boolean traceGL, boolean debugGL) {
    this(PeelingMethod.WEIGHTED_AVERAGE_MODE, view, traceGL, debugGL);
  }

  public DepthPeelingRenderer3d(PeelingMethod algorithm, final View view,
      boolean traceGL, boolean debugGL) {
    super(view, traceGL, debugGL);
    dualPeelingAlgorithm = getDepthPeelingAlgorithm(algorithm);
    dualPeelingAlgorithm.setTasksToRender(getDepthPeelingContentRenderer(view));
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    updatePainterWithGL(drawable);

    if (drawable != null && view !=null) {
      dualPeelingAlgorithm.init(view.getPainter(), width, height);
    }
    super.init(drawable);
  }


  @Override
  public void display(GLAutoDrawable drawable) {
    updatePainterWithGL(drawable);

    GL2 gl = drawable.getGL().getGL2();

    
    if(view!=null) {
      view.clear();
      
      float[] background = new float[3];
      
      background[0] = view.getBackgroundColor().r;
      background[1] = view.getBackgroundColor().g;
      background[2] = view.getBackgroundColor().b;
      
      dualPeelingAlgorithm.setBackground(background);

      //dualPeelingAlgorithm.setOpacity(0.95f);
      
      // Following line will call taskToRender, which will trigger :
      // algo.resetNumPass()
      // algo.doRender()
      // - do pre rendering
      // - view.render()
      // - do post rendering
      
      dualPeelingAlgorithm.display(view.getPainter()); 
      
      view.renderOverlay();
      
      renderScreenshotIfRequired(gl);
      
      if (!drawable.getAutoSwapBufferMode())
        drawable.swapBuffers();
    }

  }


  public static IGLRenderer getDepthPeelingContentRenderer(final View view) {
    return new IGLRenderer() {
      @Override
      public void draw(IPainter painter) {
        view.render();
      }
    };
  }

  /**
   * Rebuild all depth peeling buffers for the new screen size.
   */
  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    updatePainterWithGL(drawable);

    if (this.width != width || this.height != height) {
      this.width = width;
      this.height = height;
      
      dualPeelingAlgorithm.reshape(view.getPainter(), width, height);
    }
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    dualPeelingAlgorithm.dispose(view.getPainter());
  }
  
  

  public static IDepthPeelingAlgorithm getDepthPeelingAlgorithm(PeelingMethod method) {
    if (method == PeelingMethod.DUAL_PEELING_MODE)
      return new DualDepthPeelingAlgorithm();
    else if (method == PeelingMethod.F2B_PEELING_MODE)
      return new FrontToBackPeelingAlgorithm();
    else if (method == PeelingMethod.WEIGHTED_AVERAGE_MODE)
      return new WeightedAveragePeelingAlgorithm();
    else if (method == PeelingMethod.WEIGHTED_SUM_MODE)
      return new WeightedSumPeelingAlgorithm();
    else
      throw new RuntimeException("Unknown method:" + method);
  }
}
