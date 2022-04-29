package org.jzy3d.junit;

import org.jzy3d.os.OperatingSystem;

public class Platform {
  protected OperatingSystem os = new OperatingSystem();
  protected String gpuName = "unknownGPU";
  
  public String getLabel() {
    String osName = clean(os.getName());
    String osVer = clean(os.getVersion());
    
    return osName + "_" + osVer + "_" + gpuName;
  }
  
  protected String clean(String s) {
    return s.toLowerCase().replace(" ", "");
  }

  public OperatingSystem getOs() {
    return os;
  }

  public String getGpuName() {
    return gpuName;
  }
}
