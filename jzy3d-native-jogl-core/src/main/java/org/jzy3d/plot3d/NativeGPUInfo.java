package org.jzy3d.plot3d;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;

/**
 * Read {@link GPUInfo} with JOGL, either from an existing GL context or from a hidden offscreen
 * context.
 */
public class NativeGPUInfo extends GPUInfo {
  public static void main(String[] args) {
    GPUInfo gpu = NativeGPUInfo.load();

    System.out.println("GPU : " + gpu.renderer + "\n");
  }

  /** Read GPU information from a hidden offscreen context. */
  public static GPUInfo load() {
    GLProfile glp = GLProfile.getMaxProgrammable(true);
    GLCapabilities caps = new GLCapabilities(glp);
    caps.setOnscreen(false);
    GLDrawableFactory factory = GLDrawableFactory.getFactory(glp);
    GLAutoDrawable drawable =
        factory.createOffscreenAutoDrawable(factory.getDefaultDevice(), caps, null, 1, 1);

    final GPUInfo[] gpuHolder = new GPUInfo[1];

    drawable.addGLEventListener(new GLEventListener() {
      @Override
      public void init(GLAutoDrawable d) {
        gpuHolder[0] = load(d.getGL());
      }
      @Override public void display(GLAutoDrawable d) {}
      @Override public void reshape(GLAutoDrawable d, int x, int y, int w, int h) {}
      @Override public void dispose(GLAutoDrawable d) {}
    });

    drawable.display();
    drawable.destroy();

    return gpuHolder[0];
  }

  /** Read GPU information from a GL context, which MUST be current. */
  public static GPUInfo load(GL gl) {
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
}
