package org.jzy3d.plot2d.primitive;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.plot3d.primitives.axis.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.providers.RegularTickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.DefaultDecimalTickRenderer;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;

public class TestAWTColorbarImageGenerator {
  @Test
  public void whenPixelScaleLargerThan1_ThenBarWidthIsScaled() {
    
    // Given
    ColorMapper mapper = new ColorMapper(new ColorMapRainbow(), -1, 1);
    ITickRenderer renderer = new DefaultDecimalTickRenderer();
    ITickProvider provider = new RegularTickProvider();
    
    AWTColorbarImageGenerator gen = new AWTColorbarImageGenerator(mapper, provider, renderer);
    
    //gen.setFont(Font.Helvetica_18);
    
    // When
    int scale = 1;
    int barWidth = 30;

    gen.setPixelScale(new Coord2d(scale, scale));

    gen.toImage(1, 1, barWidth);

    // Then
    Assert.assertEquals(barWidth, gen.getScaledBarWidth());
    
    //gen.g
    
    // When 
    scale = 2;

    gen.setPixelScale(new Coord2d(scale, scale));

    gen.toImage(1, 1, barWidth);

    // Then
    Assert.assertEquals(barWidth * scale, gen.getScaledBarWidth());

  }
}
