package org.jzy3d.painters;

import static org.mockito.Mockito.mock;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.os.WindowingToolkit;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;

public class TestNativeDesktopPainter_Swing {
  @Test
  public void detectSwing() {
    NativeDesktopPainter p = new NativeDesktopPainter();
    
    p.setCanvas(mock(CanvasSwing.class));
    
    Assert.assertEquals(WindowingToolkit.Swing, p.getWindowingToolkit());
  }
}
