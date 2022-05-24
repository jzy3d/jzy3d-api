package org.jzy3d.plot3d.primitives.axis.layout.fonts;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.mocks.jzy3d.Mocks;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.rendering.view.View;

public class TestHiDPIProportionalFontSizePolicy {
  
  @Test
  public void whenPixelScaleIsTwo_ThenFontIsResizedByAFactorOfTwo() {
    int inputHeight = 10;
    float viewScale = 2;

    // Given a view with undefined pixel scale
    String os = "MacOs";
    String version = "10";
    Coord2d jvmScale = new Coord2d(1,1);
    Coord2d gpuScale = new Coord2d(1.5,1.5);
    
    View view = Mocks.ViewAndPainter(viewScale, os, version, jvmScale, gpuScale);

    
    /*ICanvas canvas = mock(ICanvas.class);
    when(canvas.getPixelScale()).thenReturn(new Coord2d(1,1));
    when(canvas.getPixelScaleJVM()).thenReturn(new Coord2d(1.5,1.5));*/
    
    // Given a layout with a known font height
    AxisLayout layout = new AxisLayout();
    layout.setFont(new Font("*", inputHeight));
    
    // When applying policy to the axis layout
    HiDPIProportionalFontSizePolicy policy = new HiDPIProportionalFontSizePolicy(view);
    int outputHeight = policy.apply(layout).getHeight();
    
    System.out.println("scale   = " + viewScale);
    System.out.println("height.i= "+inputHeight);
    System.out.println("height.o= "+outputHeight);

    // Then font height remain unchanged
    Assert.assertEquals((int)(inputHeight * viewScale), outputHeight);
  }
  
  /**
   * Special case of Windows
   */
  
@Ignore  
  @Test
  public void givenWindows_whenPixelScaleIsTwo_ThenFontIsResizedByAFactorOfTwo() {
    int inputHeight = 10;
    float viewScale = 2.0f/3;

    // Given a view with undefined pixel scale
    String os = "Windows";
    String version = "10";
    Coord2d jvmScale = new Coord2d(1.5,1.5);
    Coord2d gpuScale = new Coord2d(1,1);
    
    View view = Mocks.ViewAndPainter(viewScale, os, version, jvmScale, gpuScale);
    
    // Given a layout with a known font height
    AxisLayout layout = new AxisLayout();
    layout.setFont(new Font("*", inputHeight));
    
    // When applying policy to the axis layout
    HiDPIProportionalFontSizePolicy policy = new HiDPIProportionalFontSizePolicy(view);
    int outputHeight = policy.apply(layout).getHeight();
    
    System.out.println("scale    = " + viewScale);
    System.out.println("height.i = "+inputHeight);
    System.out.println("height.o = "+outputHeight);
    
    // Then font height remain unchanged
    Assert.assertEquals((int)(inputHeight * viewScale), outputHeight);
  }

  
  
  @Test
  public void whenPixelScaleIsNan_ThenFontIsNotResized() {
    int inputHeight = 10;
    float viewScale = Float.NaN;

    // Given a view with undefined pixel scale
    View view = Mocks.ViewAndPainter(viewScale);
    
    // Given a layout with a known font height
    AxisLayout layout = new AxisLayout();
    layout.setFont(new Font("*", inputHeight));
    
    // When applying policy to the axis layout
    HiDPIProportionalFontSizePolicy policy = new HiDPIProportionalFontSizePolicy(view);
    int outputHeight = policy.apply(layout).getHeight();
    
    // Then font height remain unchanged
    Assert.assertEquals(inputHeight, outputHeight);
  }

}
