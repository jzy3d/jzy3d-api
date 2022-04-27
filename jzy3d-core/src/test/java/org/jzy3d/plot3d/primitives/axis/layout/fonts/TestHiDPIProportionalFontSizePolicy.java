package org.jzy3d.plot3d.primitives.axis.layout.fonts;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.rendering.view.View;

public class TestHiDPIProportionalFontSizePolicy {
  
  @Test
  public void whenPixelScaleIsTwo_ThenFontIsResizedByAFactorOfTwo() {
    int inputHeight = 10;
    float viewScale = 2;

    // Given a view with undefined pixel scale
    View view = mock(View.class);
    when(view.getPixelScale()).thenReturn(new Coord2d(viewScale, viewScale));
    
    // Given a layout with a known font height
    AxisLayout layout = new AxisLayout();
    layout.setFont(new Font("*", inputHeight));
    
    // When applying policy to the axis layout
    HiDPIProportionalFontSizePolicy policy = new HiDPIProportionalFontSizePolicy(view);
    int outputHeight = policy.apply(layout).getHeight();
    
    // Then font height remain unchanged
    Assert.assertEquals((int)(inputHeight * viewScale), outputHeight);
  }
  
  @Test
  public void whenPixelScaleIsNan_ThenFontIsNotResized() {
    int inputHeight = 10;
    float viewScale = Float.NaN;

    // Given a view with undefined pixel scale
    View view = mock(View.class);
    when(view.getPixelScale()).thenReturn(new Coord2d(viewScale, viewScale));
    
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
