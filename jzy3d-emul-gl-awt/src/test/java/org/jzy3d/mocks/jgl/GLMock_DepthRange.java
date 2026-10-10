package org.jzy3d.mocks.jgl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jgl.wt.awt.GL;

/**
 * Record calls to glDepthRange.
 * 
 * Only calls made by the thread that last invoked {@link #clear_glDepthRange()} are recorded, since
 * the canvas may repaint in the background (e.g. when it detects a screen change) and add calls
 * that are not made by the test.
 */
public class GLMock_DepthRange extends GL{
  List<double[]> verify_glDepthRange = Collections.synchronizedList(new ArrayList<>());
  volatile Thread recordedThread;
  
  @Override
  public void glDepthRange(double near_val, double far_val) {
    super.glDepthRange(near_val, far_val);
    
    if (recordedThread != null && recordedThread != Thread.currentThread()) {
      return;
    }
    
    double[] args = {near_val, far_val};
    verify_glDepthRange.add(args);
    
    //Array.print("GLMock_DepthRange : ", args);
  }

  public List<double[]> verify_glDepthRange() {
    return verify_glDepthRange;
  }
  
  public void clear_glDepthRange() {
    recordedThread = Thread.currentThread();
    verify_glDepthRange.clear();
  }
}
