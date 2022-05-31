package org.jzy3d.plot3d.rendering.view.layout;

import org.junit.Test;

public class TestEmulGLViewAndColorbarsLayout extends TestViewAndColorbarsLayout{
  @Test
  public void whenColorbars_ThenScreenSeparatorIsProcessed() {
    EmulGLViewAndColorbarsLayout layout = new EmulGLViewAndColorbarsLayout();
    
    // TODO :REMOVE ME!!
    layout.fixHiDPI = false;
    
    //TestViewAndColorbarsLayout t = new TestViewAndColorbarsLayout();
    whenColorbars_ThenScreenSeparatorIsProcessed(layout);
  }
}
