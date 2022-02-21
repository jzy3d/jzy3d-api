package org.jzy3d.painters;

import static org.mockito.Mockito.mock;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.os.WindowingToolkit;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;

public class TestNativeDesktopPainter_AWT {
  @Test
  public void detectAWT() {
    NativeDesktopPainter p = new NativeDesktopPainter();
    
    p.setCanvas(mock(CanvasAWT.class));
    
    Assert.assertEquals(WindowingToolkit.AWT, p.getWindowingToolkit());
  }
}
