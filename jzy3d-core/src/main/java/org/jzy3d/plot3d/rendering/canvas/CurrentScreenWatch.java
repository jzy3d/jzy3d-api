package org.jzy3d.plot3d.rendering.canvas;

import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

/**
 * Check periodically if screen size has changed, hence allowing to check a changing monitor.
 * 
 * @author Martin Pernollet
 */
public abstract class CurrentScreenWatch extends PixelScaleWatch {
  protected abstract void fireScreenChange(double screenWidth, double screenHeight);

  protected Component component;
  
  public CurrentScreenWatch(Component component) {
    super();
    this.component = component;
  }

  @Override
  protected void firePixelScaleChanged(double pixelScaleX, double pixelScaleY) {
    fireScreenChange(pixelScaleX, pixelScaleY);

  }

  @Override
  protected void firePixelScaleInit(double pixelScaleX, double pixelScaleY) {
    fireScreenChange(pixelScaleX, pixelScaleY);
  }

  @Override
  public double getPixelScaleX() {
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
    GraphicsDevice gd;
    if (component.getGraphicsConfiguration() == null) {
      gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    } else {
      gd = component.getGraphicsConfiguration().getDevice();
    }
    return gd.getDisplayMode().getHeight();
  }

}
