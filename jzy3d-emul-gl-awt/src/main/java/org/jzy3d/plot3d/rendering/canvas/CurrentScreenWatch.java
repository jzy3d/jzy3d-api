package org.jzy3d.plot3d.rendering.canvas;

import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public abstract class CurrentScreenWatch extends PixelScaleWatch {
  protected Component component;
  
  public CurrentScreenWatch(Component component) {
    super();
    this.component = component;
  }

  @Override
  protected void firePixelScaleChanged(double pixelScaleX, double pixelScaleY) {
    //System.out
    //    .println("EmulGLCanvas : Change screen size to " + pixelScaleX + "; " + pixelScaleY);
    fireScreenChange(pixelScaleX, pixelScaleY);

  }

  @Override
  protected void firePixelScaleInit(double pixelScaleX, double pixelScaleY) {
    //System.out
    //    .println("EmulGLCanvas : Init screen size to " + pixelScaleX + "; " + pixelScaleY);
    
    fireScreenChange(pixelScaleX, pixelScaleY);
  }
  
  protected abstract void fireScreenChange(double screenWidth, double screenHeight);
  
  

  @Override
  public double getPixelScaleX() {
    // Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    // return EmulGLCanvas.this.getWidth();

    // GraphicsDevice gd =
    // GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    GraphicsDevice gd;
    if (component.getGraphicsConfiguration() == null) {
      gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    } else {
      gd = component.getGraphicsConfiguration().getDevice();
    }

    return gd.getDisplayMode().getWidth();

  }


  @Override
  public double getPixelScaleY() {
    // Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    // return EmulGLCanvas.this.getHeight();

    // GraphicsDevice gd =
    // GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    // int width = gd.getDisplayMode().getWidth();
    GraphicsDevice gd;
    if (component.getGraphicsConfiguration() == null) {
      gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    } else {
      gd = component.getGraphicsConfiguration().getDevice();
    }

    // System.out.println(gd.getDisplayMode().getHeight());

    return gd.getDisplayMode().getHeight();
  }

}
