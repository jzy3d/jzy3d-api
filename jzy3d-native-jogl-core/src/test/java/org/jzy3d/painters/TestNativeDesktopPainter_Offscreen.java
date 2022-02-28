package org.jzy3d.painters;

import static org.mockito.Mockito.mock;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.os.WindowingToolkit;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;

public class TestNativeDesktopPainter_Offscreen {
  @Test
  public void detectAWT() {
    NativeDesktopPainter p = new NativeDesktopPainter();
    
    p.setCanvas(mock(OffscreenCanvas.class));
    
    Assert.assertEquals(WindowingToolkit.Offscreen, p.getWindowingToolkit());
  }
}
