package org.jzy3d.plot3d.rendering.view;
public enum HiDPI{
    ON,OFF;
    
    public static HiDPI from(boolean hidpi) {
      if(hidpi) return ON; return OFF;
    }
  }