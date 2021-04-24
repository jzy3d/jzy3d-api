package org.jzy3d.plot3d.rendering.canvas;

public abstract class PixelScaleWatch implements Runnable {
  protected abstract void firePixelScaleChanged(double pixelScaleX, double pixelScaleY);
  public abstract double getPixelScaleX();
  public abstract double getPixelScaleY();
  
  double prevX = -1;
  double prevY = -1;
  
  @Override
  public void run() {
    while(true) {
      watchPixelScaleAndNotifyUponChange();

      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  protected void watchPixelScaleAndNotifyUponChange() {
    double x = getPixelScaleX(); 
    double y = getPixelScaleY();
    
    if((prevX!=-1)&&(prevY!=-1)) {
      if((x!=prevX)||(y!=prevY)) {
        firePixelScaleChanged(x, y);
      }
    }
    
    prevX = x;
    prevY = y;
    
  }

}