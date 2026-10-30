package org.jzy3d.plot3d.rendering.view.layout;

import org.junit.Test;

public class TestEmulGLViewAndColorbarsLayout extends TestViewAndColorbarsLayout{
  @Test
  public void whenColorbars_ThenScreenSeparatorIsProcessed() {
    EmulGLViewAndColorbarsLayout layout = new EmulGLViewAndColorbarsLayout();
    
    whenColorbars_ThenScreenSeparatorIsProcessed(layout, true);
    
  }
}
