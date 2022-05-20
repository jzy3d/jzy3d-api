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
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;

/**
 * //tester toute l'api //dimension //size // //legend.getBackground() //legend.setFont(font);
 * 
 * // Tester si taille viewport trop petite
 * 
 * 
 * // Bazard entre imageWidth, minDimension,
 * 
 * // comportements - generate image once with setViewport, but not 2 times -
 */
public class TestAWTColorbarLegend {
  private static final double DELTA = 0.1;


  @Test
  public void whenLegendViewportDimensionSet_ThenDisplayedImageIsSmallerAccordingToViewportMargin() {

    // Given a colorbar
    AWTColorbarLegend legend = new AWTColorbarLegend(SampleGeom.surface(), new AxisLayout());


    // When setting viewport to a size and having a non 0 margin
    Dimension margin = legend.getMargin();
    margin.width = 2;
    margin.height = 2;

    int width = 300;
    int height = 600;
    float left = 0.8f;
    float right = 1f;

    legend.setViewPort(width, height, left, right);

    // Then actual image width is smaller according to horizontal margin
    int actualImageWidth = legend.getImage().getWidth(null);
    int expectWidth = (int) (width * (right - left)) - margin.width;
    Assert.assertEquals(expectWidth, actualImageWidth, DELTA);

    // ------------------------
    // When update image

    legend.updateImage();

    // Then size remain the same
    Assert.assertEquals(expectWidth, legend.getImage().getWidth(null), DELTA);

    // ------------------------
    // By default, configuration states to ignore pixel scale

    Assert.assertFalse(legend.isUsePixelScale());

    // ------------------------
    // When updating pixel scale with an ignored pixel scale

    legend.updatePixelScale(new Coord2d(2, 2));
    legend.updateImage();

    // Then image size remains the same
    Assert.assertEquals(expectWidth, legend.getImage().getWidth(null), DELTA);

    // ------------------------
    // When updating pixel scale

    legend.setUsePixelScale(true);
    legend.updatePixelScale(new Coord2d(2, 2));
    legend.updateImage();

    // Then image size is doubled
    Assert.assertEquals(expectWidth * 2, legend.getImage().getWidth(null), DELTA);

  }

  public void whenViewportSmallerThanMinWidth_ThenImageIsSizedAtMinWidth() {
    
    // Given a colorbar
    AWTColorbarLegend legend = new AWTColorbarLegend(SampleGeom.surface(), new AxisLayout());
   
    // When setting viewport to size smaller than min width
    int MIN_WIDTH = 200;
    int width = MIN_WIDTH/2; 
    int height = 600;
    float left = 0.8f;
    float right = 1f;
    
    legend.setMinimumWidth(MIN_WIDTH);
    legend.setViewPort(width, height, left, right);

    // Then generated image has predefined min width
    Assert.assertEquals(MIN_WIDTH, legend.getMinimumDimension().width);
    
    int actualWidth = legend.getImage().getWidth(null);
    Assert.assertEquals(MIN_WIDTH, actualWidth);
    
  }

}
