package org.jzy3d.plot3d.transform;


public class Rotator {
  public Rotator(int sleep, final Rotate rotate) {
    this.sleep = sleep;
    this.rotate = rotate;

    this.t = new Thread(new Runnable() {
      @Override
      public void run() {
        while (running) {
          Rotator.this.rotate.setAngle(Rotator.this.rotate.getAngle() + 1);
          try {
            Thread.sleep(Rotator.this.sleep);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });
  }

  protected boolean running = false;

  public void start() {
    running = true;
    t.start();
  }


  public void stop() {
    running = false;
  }

  public int getSleep() {
    return sleep;
  }

  public void setSleep(int sleep) {
    this.sleep = sleep;
  }

  public Rotate getRotate() {
    return rotate;
  }

  public void setRotate(Rotate rotate) {
    this.rotate = rotate;
  }

  protected Thread t;
  protected int sleep;
  protected Rotate rotate;
}
