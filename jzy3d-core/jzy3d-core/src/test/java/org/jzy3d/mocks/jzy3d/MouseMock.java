package org.jzy3d.mocks.jzy3d;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JPanel;

public class MouseMock {
  protected static JPanel defaultComponent = new JPanel();
  


  public static MouseEvent event(int x, int y) {
    return event(defaultComponent, x, y, 0);
  }

  public static MouseEvent event(int x, int y, int modifiers) {
    return event(defaultComponent, x, y, modifiers);
  }
  
  public static MouseEvent event(Component sourceCanvas, int x, int y) {
    return event(sourceCanvas, x, y, 0);
  }
  
  /*public static MouseEvent event(Component sourceCanvas, int x, int y, boolean isLeftDown) {
    int modifiers = 0;
    
    if(isLeftDown) {
      modifiers = modifiers | InputEvent.BUTTON1_DOWN_MASK;
    }
    
    return event(sourceCanvas, x, y, modifiers);
  }*/

  public static MouseEvent event(Component sourceCanvas, int x, int y, int modifiers) {
    MouseEvent e = new MouseEvent(sourceCanvas, 0, System.nanoTime(), modifiers, x, y, x, y, 1, false, 0);    
    return e;
  }

  public static MouseWheelEvent wheel(int wheelRotation) {
    return wheel(defaultComponent, wheelRotation);
  }
  
  public static MouseWheelEvent wheel(Component sourceCanvas, int wheelRotation) {
    int x = 0;
    int y = 0;
    int scrollType = 0;
    int scrollAmount = 0;
    int modifiers = 0;
    MouseWheelEvent e = new MouseWheelEvent(sourceCanvas, 0, System.nanoTime(), modifiers, x, y, 100, 100, 1, false, scrollType, scrollAmount, wheelRotation);    
    return e;
  }

}
