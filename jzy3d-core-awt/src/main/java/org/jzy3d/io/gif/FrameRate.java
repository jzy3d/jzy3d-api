package org.jzy3d.io.gif;

public class FrameRate {
  protected boolean continuous;
  protected double duration;
  protected int rate;
  
  public boolean isContinuous() {
    return continuous;
  }
  public double getDuration() {
    return duration;
  }
  public int getRate() {
    return rate;
  }
  
  public FrameRate() {
  }

  public FrameRate(boolean continuous, double duration) {
    this.continuous = continuous;
    this.duration = duration;
    this.rate = (int)Math.round(1000/duration);
  }

  public FrameRate(boolean continuous, int rate) {
    this.continuous = continuous;
    this.rate = rate;
    this.duration = Math.round(1000/rate);
  }
  
  public static FrameRate ContinuousDuration(int duration) {
    FrameRate f = new FrameRate();
    f.continuous = true;
    f.duration = duration;
    f.rate = (int) Math.round(1000/duration);
    return f;
  }

  public static FrameRate ContinuousRate(int rate) {
    FrameRate f = new FrameRate();
    f.continuous = true;
    f.duration = (int) Math.round(1000/rate);
    f.rate = rate;
    return f;
  }
  
  public static FrameRate VariableDuration(int minDuration) {
    FrameRate f = new FrameRate();
    f.continuous = false;
    f.duration = minDuration;
    f.rate = (int) Math.round(1000/minDuration);
    return f;
  }

  public static FrameRate VariableRate(int maxRate) {
    FrameRate f = new FrameRate();
    f.continuous = false;
    f.duration = (int) Math.round(1000/maxRate);
    f.rate = maxRate;
    return f;
  }
}
