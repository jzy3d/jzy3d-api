package org.jzy3d.plot3d.rendering.legends.colorbars;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Range;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;

/**
//tester toute l'api
//dimension
//size
//
//legend.getBackground()
//legend.setFont(font);
 * 
 *     // Tester si taille viewport trop petite
    
    
    // Bazard entre imageWidth, minDimension, 

// 
 * comportements
 * - generate image once with setViewport, but not 2 times 
 * -
 */
public class TestAWTColorbarLegend {
  private static final double DELTA = 0.1;


  @Test
  public void whenViewport_then() {
    // Given
    AWTColorbarLegend legend = new AWTColorbarLegend(surface(), new AxisLayout());
    Dimension margin = legend.getMargin();
    
    // When setting viewport to a size
    int width = 300;
    int height = 600;
    float left = 0.8f;
    float right = 1f;
    legend.setViewPort(width, height, left, right);
    
    
    // Then 
    int expectWidth = (int)(width * (right-left)) - margin.width;
    Assert.assertEquals(expectWidth, legend.getImage().getWidth(null), DELTA);

    // When update image
    legend.updateImage();

    // Then size remain the same
    Assert.assertEquals(expectWidth, legend.getImage().getWidth(null), DELTA);

    // When update pixel scale
    legend.updatePixelScale(new Coord2d(2,2));

    // Then pixel scale remains the same otherwise it is awfull (for native charts only
    // has the image size is doubled size, then probably shrinked, then expanded again)
    Assert.assertEquals(expectWidth, legend.getImage().getWidth(null), DELTA);
    
    // But the [EmulGL]+ViewAndColorbarLayout will fix the layout due to pixel scale
  }
  
  public void whenMinimumDimension_Then() {
    AWTColorbarLegend legend = new AWTColorbarLegend(surface(), new AxisLayout());
    Dimension margin = legend.getMargin();
    
    Assert.assertEquals(AWTColorbarImageGenerator.MIN_BAR_WIDTH, legend.getMinimumDimension().width);
    Assert.assertEquals(AWTColorbarImageGenerator.MIN_BAR_HEIGHT, legend.getMinimumDimension().height);
    
    // When
    legend.setMinimumWidth(200); 
    // devrait être setPreferedWidth - 
    // minWidth doit ne jamais être violée
    // AutoWidth doit tout de même permettre de savoir 
    
    // Then
    Assert.assertEquals(200, legend.getMinimumDimension().width);

    
    // When setting viewport to a size
    int width = 300;
    int height = 600;
    float left = 0.8f;
    float right = 1f;
    legend.setViewPort(width, height, left, right);

    
    // Then / VIOLATE MINIMUM DIMENSION!!!
    int expectWidth = (int)(width * (right-left)) - margin.width;
    Assert.assertEquals(expectWidth, legend.getImage().getWidth(null), 0.1);

  }
  
  
  private static Shape surface() {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };
    Range range = new Range(-3, 3);
    int steps = 50;

    SurfaceBuilder builder = new SurfaceBuilder();
    Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }
}
