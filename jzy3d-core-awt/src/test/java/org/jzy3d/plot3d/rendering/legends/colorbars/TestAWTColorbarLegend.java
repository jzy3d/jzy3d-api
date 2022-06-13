package org.jzy3d.plot3d.rendering.legends.colorbars;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Margin;
import org.jzy3d.mocks.jzy3d.Mocks;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.rendering.view.View;
import org.mockito.Mockito;

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
    Margin margin = legend.getMargin();
    margin.set(2,2); // 1 pixel margin each side

    int width = 300;
    int height = 600;
    float left = 0.8f;
    float right = 1f;

    legend.setViewPort(width, height, left, right);

    // Then actual image width is smaller according to horizontal margin
    int actualImageWidth = legend.getImage().getWidth(null);
    int expectWidth = Math.round(width * (right - left) - margin.getWidth());
    Assert.assertEquals(expectWidth, actualImageWidth, DELTA);

    // ------------------------
    // When update image

    legend.updateImage();

    // Then size remain the same
    Assert.assertEquals(expectWidth, legend.getImage().getWidth(null), DELTA);

    // ------------------------
    // By default, configuration states to ignore pixel scale

    
    
    // ------------------------
    // When updating pixel scale with an ignored pixel scale

    legend.setUsePixelScale(false);
    legend.updatePixelScale(new Coord2d(2, 2));
    legend.updateImage();

    // Then image size remains the same
    //expectWidth = Math.round(width * (right - left) - margin.getWidth());

    Assert.assertEquals(expectWidth, legend.getImage().getWidth(null), DELTA);

    // ------------------------
    // When updating pixel scale with a NON ignored pixel scale
    


    legend.setUsePixelScale(true);
    legend.updatePixelScale(new Coord2d(2, 2));
    legend.updateImage();

    // Then image size is a bit smaller since margin is multiplied by pixel scale
    expectWidth = Math.round(width * (right - left) - margin.getWidth()*2);
    Assert.assertEquals(expectWidth, legend.getImage().getWidth(null), DELTA);
    

    // ------------------------
    // When getting legend min dim before rendering
    // Then dimension is default
    
    Assert.assertEquals(AWTColorbarImageGenerator.MIN_BAR_WIDTH, legend.getMinimumDimension().width);

    // ------------------------
    // When rendering with a painter computing static text width

    int TEXT_WIDTH = 20;

    expectWidth = 68;//+= TEXT_WIDTH;

    View v = Mocks.ViewAndPainter(2);
    IPainter painter = v.getPainter();
    Mockito.when(painter.getTextLengthInPixels(Mockito.any(), Mockito.any())).thenReturn(TEXT_WIDTH);


    // Then min dim is updated
    legend.render(v.getPainter());
    Assert.assertEquals(expectWidth, legend.getMinimumDimension().width);
    
    // TODO !! SHOULD UPDATE DIMENSION BEFORE RENDERING
    // TO REQUIRE LESS RENDER CYCLE
    
    
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
