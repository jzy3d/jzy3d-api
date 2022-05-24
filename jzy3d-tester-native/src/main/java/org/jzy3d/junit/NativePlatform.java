package org.jzy3d.junit;

import org.jzy3d.plot3d.GPUInfo;

public class NativePlatform extends Platform{
  protected GPUInfo info = GPUInfo.load();
  
  public NativePlatform() {
    gpuName = info.getRenderer().replace(" ", "").replace("(R)", "").replace("(TM)", "");
  }
  
  
}
