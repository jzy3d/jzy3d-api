package org.jzy3d.plot3d.rendering.canvas;

public abstract class PixelScaleWatch implements Runnable {
  // does nothing by default
  protected void firePixelScaleInit(double pixelScaleX, double pixelScaleY) {    
  }

  protected abstract void firePixelScaleChanged(double pixelScaleX, double pixelScaleY);

  public abstract double getPixelScaleX();

  public abstract double getPixelScaleY();

  double prevX = -1;
  double prevY = -1;
  long watchIntervalMs = 10;

  @Override
  public void run() {
    while (true) {
      watchPixelScaleAndNotifyUponChange();

      try {
        Thread.sleep(watchIntervalMs);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  protected void watchPixelScaleAndNotifyUponChange() {
    double x = getPixelScaleX();
    double y = getPixelScaleY();

    if ((prevX != -1) && (prevY != -1)) {
      if ((x != prevX) || (y != prevY)) {
        firePixelScaleChanged(x, y);
      }
    }
    else {
      if ((x != prevX) || (y != prevY)) {
        firePixelScaleInit(x, y);
      }
    }

    prevX = x;
    prevY = y;

  }


  public long getWatchIntervalMs() {
    return watchIntervalMs;
  }

  public void setWatchIntervalMs(long watchIntervalMs) {
    this.watchIntervalMs = watchIntervalMs;
  }

}
