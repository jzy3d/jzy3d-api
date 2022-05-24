package org.jzy3d.painters;

import static org.mockito.Mockito.mock;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.factories.CanvasNewtSWT;
import org.jzy3d.os.WindowingToolkit;

public class TestNativeDesktopPainter_SWT {
  @Test
  public void detectSwt() {
    NativeDesktopPainter p = new NativeDesktopPainter();
    
    p.setCanvas(mock(CanvasNewtSWT.class));
    
    Assert.assertEquals(WindowingToolkit.SWT, p.getWindowingToolkit());
  }
}
