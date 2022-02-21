package org.jzy3d.os;

public enum WindowingToolkit {
  AWT, Swing, SWT, Offscreen, UNKOWN;
  
  public boolean isAWT() {
    return AWT.equals(this);
  }
  
  public boolean isSwing() {
    return Swing.equals(this);
  }

  public boolean isSWT() {
    return SWT.equals(this);
  }
  
  public boolean isOffscreen() {
    return Offscreen.equals(this);
  }

}
