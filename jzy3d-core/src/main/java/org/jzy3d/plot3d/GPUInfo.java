package org.jzy3d.plot3d;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;

public class GPUInfo {
  protected String vendor;
  protected String renderer;
  protected String version;
  protected List<String> extensions = new ArrayList<>(); 
  
  /** Read GPU information through a painter. A GL context MUST be current. */
  public static GPUInfo load(IPainter painter) {
    GPUInfo gpu = new GPUInfo();
    gpu.vendor = painter.glGetString(GLConstants.GL_VENDOR);
    gpu.renderer = painter.glGetString(GLConstants.GL_RENDERER);
    gpu.version = painter.glGetString(GLConstants.GL_VERSION);

    String ext = painter.glGetString(GLConstants.GL_EXTENSIONS);

    if (ext != null) {
      for (String e : ext.split(" ")) {
        gpu.extensions.add(e);
      }
    }
    return gpu;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("GL_VENDOR     : " + vendor + "\n");
    sb.append("GL_RENDERER   : " + renderer + "\n");
    sb.append("GL_VERSION    : " + version + "\n");
    
    if(extensions!=null) {
      sb.append("GL_EXTENSIONS : (" + extensions.size() + ")\n");
      for(String e: extensions) {
        sb.append("\t" + e + "\n");
      }
    }
    else {
      sb.append("GL_EXTENSIONS : null\n");      
    }
    
    return sb.toString();
  }

  public String getVendor() {
    return vendor;
  }

  public String getRenderer() {
    return renderer;
  }

  public String getVersion() {
    return version;
  }

  public List<String> getExtensions() {
    return extensions;
  }
}
