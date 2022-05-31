package org.jzy3d.plot3d.rendering.legends.colorbars;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Range;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;


public class TestAWTColorbarLegend {
  @Test
  public void whenViewport_then() {
    // Given
    AWTColorbarLegend legend = new AWTColorbarLegend(SampleGeom.surface(), new AxisLayout());
    Dimension legendMargin = legend.getMargin();
    
    // When setting viewport to a size
    int width = 300;
    int height = 600;
    float left = 0.8f;
    float right = 1f;
    legend.setViewPort(width, height, left, right);
    
    
    // Then / VIOLATE MINIMUM DIMENSION!!!
    float colorbarWidth = width * (right-left);
    
    System.out.println(colorbarWidth);
    
    int expectedImageWidth = (int)(colorbarWidth - legendMargin.width);
    
    System.out.println(expectedImageWidth);
    
    Assert.assertEquals(expectedImageWidth, legend.getImage().getWidth(null));
    
    //legend.updatePixelScale(new Coord2d(2,2));

    
    
  }
  
  public void whenSetMinimumWidth_ThenMinimumDimensionIsSet() {
    AWTColorbarLegend legend = new AWTColorbarLegend(SampleGeom.surface(), new AxisLayout());
    
    Assert.assertEquals(AWTColorbarImageGenerator.MIN_BAR_WIDTH, legend.getMinimumDimension().width);
    Assert.assertEquals(AWTColorbarImageGenerator.MIN_BAR_HEIGHT, legend.getMinimumDimension().height);
    
    // When
    legend.setMinimumWidth(200); 
    // devrait être setPreferedWidth - 
    // minWidth doit ne jamais être violée
    // AutoWidth doit tout de même permettre de savoir 
    
    // Then
    Assert.assertEquals(200, legend.getMinimumDimension().width);

  }
  
  
}
