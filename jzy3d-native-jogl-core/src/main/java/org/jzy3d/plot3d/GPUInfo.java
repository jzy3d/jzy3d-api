package org.jzy3d.plot3d;

import java.util.ArrayList;
import java.util.List;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLProfile;

public class GPUInfo {
  protected String vendor;
  protected String renderer;
  protected String version;
  protected List<String> extensions = new ArrayList<>(); 
  
  public static void main(String[] args) {
    GPUInfo gpu = GPUInfo.load();

    System.out.println("GPU : " + gpu.renderer + "\n");
  }

  /** Initialize a GL context offscreen to query GPU information */
  public static GPUInfo load() {
    GLProfile glp = GLProfile.getMaxProgrammable(true);
    GLCapabilities caps = new GLCapabilities(glp);
    caps.setOnscreen(false);
    GLDrawableFactory factory = GLDrawableFactory.getFactory(glp);
    GLAutoDrawable drawable =
        factory.createOffscreenAutoDrawable(factory.getDefaultDevice(), caps, null, 1, 1);
    drawable.display();
    drawable.getContext().makeCurrent();

    GL gl = drawable.getContext().getGL();

    //System.out.println(drawable.getContext().getGLVendorVersionNumber());
    
    GPUInfo gpu = load(gl);
    
    drawable.getContext().release();
    
    return gpu;
  }

  /** Use an existing GL context to query GPU information */
  public static GPUInfo load(GL gl) {
    // Load Info
    GPUInfo gpu = new GPUInfo();
    gpu.vendor = gl.glGetString(GL.GL_VENDOR);
    gpu.renderer = gl.glGetString(GL.GL_RENDERER);
    gpu.version = gl.glGetString(GL.GL_VERSION);
    
    String ext = gl.glGetString(GL.GL_EXTENSIONS);

    if(ext!=null) {
      for(String e: ext.split(" ")) {
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
