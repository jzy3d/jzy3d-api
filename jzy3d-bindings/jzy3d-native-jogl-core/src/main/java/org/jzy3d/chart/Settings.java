package org.jzy3d.chart;

import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

/**
 * {@link Settings} is a singleton that holds general settings that configure Imaging classes
 * instantiation.
 */
public class Settings {
  private Settings() {
    this(detectProfile());
  }

  private Settings(GLProfile glProfile) {
    glCapabilities = new GLCapabilities(glProfile);
    // glCapabilities.setAlphaBits(8);
    setHardwareAccelerated(false);


    // TODO Choose here somehow between NewtDisplay & AWTDisplay
    // display = NewtFactory.createDisplay(null);
    // screen = NewtFactory.createScreen(display, 0);
  }

  public static GLProfile detectProfile() {
    if (!(GLProfile.isAvailable(GLProfile.GL2) || GLProfile.isAvailable(GLProfile.GL2ES2))) {
      throw new UnsupportedOperationException("Jzy3d requires OpenGL 2 or OpenGL 2 ES 2");
    }

    if (GLProfile.isAvailable(GLProfile.GL2)) {
      // Preferred profile = GL2
      return GLProfile.get(GLProfile.GL2);
    } else {
      // second option for Android = GL2ES2
      return GLProfile.get(GLProfile.GL2ES2);
    }
  }

  public static GLCapabilities getOffscreenCapabilities(GLProfile glp) {
    GLCapabilities caps = new GLCapabilities(glp);
    caps.setHardwareAccelerated(true);
    caps.setDoubleBuffered(false);
    caps.setAlphaBits(8);
    caps.setRedBits(8);
    caps.setBlueBits(8);
    caps.setGreenBits(8);
    caps.setOnscreen(false);
    return caps;
  }



  /** Return the single allowed instance of Settings. */
  public static Settings getInstance() {
    if (instance == null) {
      instance = new Settings();
    }
    return instance;
  }

  /**
   * Modifies the acceleration status for all {@link INativeCanvas.Canvas} instantiations. This
   * doesn't modify the status of canvases that have already been instantiated.
   */
  public void setHardwareAccelerated(boolean hardwareAccelerated) {
    glCapabilities.setHardwareAccelerated(hardwareAccelerated);
  }

  /** Returns true if hardware acceleration is used for 3d graphics. */
  public boolean isHardwareAccelerated() {
    return glCapabilities.getHardwareAccelerated();
  }

  /** Returns a copy of the current GL2 capabilities. */
  public GLCapabilities getGLCapabilitiesClone() {
    return (GLCapabilities) glCapabilities.clone();
  }



  public GLCapabilities getGLCapabilities() {
    return glCapabilities;
  }

  public void setGLCapabilities(GLCapabilities glCapabilities) {
    this.glCapabilities = glCapabilities;
  }

  @Override
  public String toString() {
    return "HardwareAcceleration = " + isHardwareAccelerated() + "\n";
  }

  private static Settings instance;

  protected GLCapabilities glCapabilities;
}
