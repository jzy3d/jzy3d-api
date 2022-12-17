package org.jzy3d.javafx.offscreen;

import java.awt.image.BufferedImage;
import org.jzy3d.plot3d.rendering.view.AWTImageRenderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class JavaFXOffscreenRenderer3d extends AWTImageRenderer3d {
  protected Image javafxImage;

  public JavaFXOffscreenRenderer3d() {
    super();
  }

  public JavaFXOffscreenRenderer3d(View view, boolean traceGL, boolean debugGL) {
    super(view, traceGL, debugGL);
  }

  public JavaFXOffscreenRenderer3d(View view) {
    super(view);
  }

  @Override
  public void display(GLAutoDrawable canvas) {
    GL gl = canvas.getGL();

    if (view != null) {
      view.clear();
      view.render();

      // Convert as JavaFX Image 
      javafxImage = makeScreenshotAsJavaFXImage(gl);
      
      // Notify all listeners
      fireDisplay(javafxImage);

      if (doScreenshotAtNextDisplay) {
        // image already here, no need to do anything
        doScreenshotAtNextDisplay = false;
      }
    }
  }
  
  @Override
  public void init(GLAutoDrawable canvas) {
    super.init(canvas);
    //System.out.println("INIT");
  }
  

  @Override
  public void reshape(GLAutoDrawable canvas, int x, int y, int width, int height) {
    //System.out.println("RESHAPE");
    super.reshape(canvas, x, y, width, height);
  }

  protected Image makeScreenshotAsJavaFXImage(GL gl) {
    BufferedImage i = makeScreenshotAsBufferedImage(gl);
    return SwingFXUtils.toFXImage(i, null);
  }
  
  public Image getLastJavaFXScreenshotImage() {
    return javafxImage;
  }
}

