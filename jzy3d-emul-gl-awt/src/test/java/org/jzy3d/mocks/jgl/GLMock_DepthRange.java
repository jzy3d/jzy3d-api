package org.jzy3d.mocks.jgl;

import java.util.ArrayList;
import java.util.List;
import jgl.wt.awt.GL;

public class GLMock_DepthRange extends GL{
  List<double[]> verify_glDepthRange = new ArrayList<>();
  
  @Override
  public void glDepthRange(double near_val, double far_val) {
    super.glDepthRange(near_val, far_val);
    
    double[] args = {near_val, far_val};
    verify_glDepthRange.add(args);
    
    //Array.print("GLMock_DepthRange : ", args);
  }

  public List<double[]> verify_glDepthRange() {
    return verify_glDepthRange;
  }
  
  public void clear_glDepthRange() {
    verify_glDepthRange.clear();
  }
}
