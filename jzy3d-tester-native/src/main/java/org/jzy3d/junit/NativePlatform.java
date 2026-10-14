package org.jzy3d.junit;

import org.jzy3d.plot3d.GPUInfo;
import org.jzy3d.plot3d.NativeGPUInfo;

public class NativePlatform extends Platform{
  protected GPUInfo info = NativeGPUInfo.load();

  public NativePlatform() {
    if (info != null && info.getRenderer() != null) {
      gpuName = info.getRenderer().replace(" ", "").replace("(R)", "").replace("(TM)", "");
    }
  }
  
  
}
