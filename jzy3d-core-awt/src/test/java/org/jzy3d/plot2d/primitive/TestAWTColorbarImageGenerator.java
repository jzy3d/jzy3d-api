package org.jzy3d.plot2d.primitive;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.mocks.jzy3d.Mocks;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.providers.RegularTickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.DefaultDecimalTickRenderer;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;
import org.mockito.Mockito;

public class TestAWTColorbarImageGenerator {
  @Test
  public void whenPixelScaleLargerThan1_ThenBarWidthIsScaled() {
    int TEXT_WIDTH = 10;
    
    // Given
    ColorMapper mapper = new ColorMapper(new ColorMapRainbow(), -1, 1);
    ITickRenderer renderer = new DefaultDecimalTickRenderer();
    ITickProvider provider = new RegularTickProvider();
    
    AWTColorbarImageGenerator gen = new AWTColorbarImageGenerator(mapper, provider, renderer);
    
    // Given a mock painter returning 10 pix width for any string & font
    IPainter painter = Mocks.Painter();
    Mockito.when(painter.getTextLengthInPixels(Mockito.any(), Mockito.any())).thenReturn(TEXT_WIDTH);
    //Mockito.when(painter.getTextLengthInPixels(Font.Helvetica_12, "-1,00000")).thenReturn(20);
    
    // When
    int scale = 1;
    int barWidth = 30;
    int TEXT_TO_BAR = 2;

    gen.setPixelScale(new Coord2d(scale, scale));

    gen.toImage(1, 1, barWidth);

    // Then
    Assert.assertEquals(barWidth, gen.getScaledBarWidth());
    Assert.assertEquals(TEXT_TO_BAR, gen.getTextToBarHorizontalMargin());
    Assert.assertEquals(barWidth+TEXT_WIDTH+TEXT_TO_BAR, gen.getPreferredWidth(painter));
    
    // When pixel scale
    scale = 2;

    gen.setPixelScale(new Coord2d(scale, scale));

    gen.toImage(1, 1, barWidth);

    // Then preferred width adapts
    Assert.assertEquals(barWidth * scale, gen.getScaledBarWidth());
    Assert.assertEquals(barWidth + TEXT_WIDTH / scale + TEXT_TO_BAR, gen.getPreferredWidth(painter));
  }
}
