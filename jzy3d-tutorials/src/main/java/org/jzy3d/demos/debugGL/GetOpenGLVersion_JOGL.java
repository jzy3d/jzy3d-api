package org.jzy3d.demos.debugGL;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLProfile;

/**
 * This shows how to switch OpenGL version with JOGL.
 * 
 * It requires to invoke the JVM with -Djogl.disable.openglcore=true to work.
 * 
 * @see https://forum.jogamp.org/Selecting-the-highest-possible-GL-profile-at-runtime-td4041302.html
 */
public class GetOpenGLVersion_JOGL {

  public static void main(String[] args) throws Exception {
    
    // ------------------------------------------------------
    // Profile & capabilities
    
    //GLProfile glp = GLProfile.getDefault();
    //GLProfile glp = GLProfile.get(GLProfile.GL4bc);
    GLProfile glp = GLProfile.get(GLProfile.GL2);
    //GLProfile glp = GLProfile.getMaximum(true);

    GLCapabilities caps = new GLCapabilities(glp);
    caps.setOnscreen(false);

    // ------------------------------------------------------
    // Drawable to get a GL context

    GLDrawableFactory factory = GLDrawableFactory.getFactory(glp);
    GLAutoDrawable drawable =
        factory.createOffscreenAutoDrawable(factory.getDefaultDevice(), caps, null, 100, 100);
    drawable.display();
    drawable.getContext().makeCurrent();

    GL gl = drawable.getContext().getGL();


    // ------------------------------------------------------
    // Report
    
    System.out.println("PROFILE       : " + glp);
    System.out.println("CAPS (query)  : " + caps);
    System.out.println("CAPS (found)  : " + drawable.getChosenGLCapabilities());
    
    System.out.println(getDebugInfo(gl));
    
    System.out.println("GL2    : " + GLProfile.isAvailable(GLProfile.GL2));
    System.out.println("GL2GL3 : " + GLProfile.isAvailable(GLProfile.GL2GL3));
    System.out.println("GL3    : " + GLProfile.isAvailable(GLProfile.GL3));
    System.out.println("GL3bc  : " + GLProfile.isAvailable(GLProfile.GL3bc));
    System.out.println("GL4    : " + GLProfile.isAvailable(GLProfile.GL4));
    System.out.println("GL4ES3 : " + GLProfile.isAvailable(GLProfile.GL4ES3));
    System.out.println("GL4bc  : " + GLProfile.isAvailable(GLProfile.GL4bc));

    // ------------------------------------------------------
    // Try invoking something

    
    gl.getGL2().glClear(0);
    
    gl.getGL4bc().glClear(0);
    

    // ------------------------------------------------------
    // We are done, release context for further work
    
    drawable.getContext().release();
  }

  public static String getDebugInfo(GL gl) {
    StringBuffer sb = new StringBuffer();
    sb.append("GL_VENDOR     : " + gl.glGetString(GL.GL_VENDOR) + "\n");
    sb.append("GL_RENDERER   : " + gl.glGetString(GL.GL_RENDERER) + "\n");
    sb.append("GL_VERSION    : " + gl.glGetString(GL.GL_VERSION) + "\n");
    
    String ext = gl.glGetString(GL.GL_EXTENSIONS);

    if(ext!=null) {
      String[] exts = ext.split(" ");
      sb.append("GL_EXTENSIONS : (" + exts.length + ")\n");
      /*for(String e: exts) {
        sb.append("\t" + e + "\n");
      }*/
    }
    else {
      sb.append("GL_EXTENSIONS : null\n");      
    }
    
    sb.append("GL INSTANCE : " + gl.getClass().getName() + "\n");
    
    return sb.toString();
  }
  
}
