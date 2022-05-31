package org.jzy3d.plot3d.rendering.view.layout;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Dimension;
import org.jzy3d.mocks.jzy3d.Mocks;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.ViewportMode;

public class TestViewAndColorbarsLayout {

  @Test
  public void whenNoColorbar_ThenNoColorbarSpace() {

    int WIDTH = 800;
    int HEIGHT = 600;
    float SCALE = 1;

    // -------------------------------
    // Given a chart with NO colorbar

    Chart chart = Mocks.Chart(WIDTH, HEIGHT, SCALE);
    when(chart.getScene().getGraph().getLegends()).thenReturn(new ArrayList<>());


    // -------------------------------
    // When

    ViewAndColorbarsLayout layout = new ViewAndColorbarsLayout();
    layout.update(chart);


    // --------------------------------
    // Then layout configured for NO colorbar
    // and scene will occupy full canvas

    Assert.assertEquals(WIDTH, layout.sceneViewport.getWidth());
    Assert.assertEquals(HEIGHT, layout.sceneViewport.getHeight());

    Assert.assertEquals(WIDTH, layout.backgroundViewport.getWidth());
    Assert.assertEquals(HEIGHT, layout.backgroundViewport.getHeight());

    Assert.assertFalse(layout.hasColorbars);
    Assert.assertEquals(1, layout.screenSeparator, 0.1);

  }

  @Test
  public void whenColorbars_ThenScreenSeparatorIsProcessed() {
    whenColorbars_ThenScreenSeparatorIsProcessed(new ViewAndColorbarsLayout());
  }
  
  public void whenColorbars_ThenScreenSeparatorIsProcessed(ViewAndColorbarsLayout layout) {
    int WIDTH = 800;
    int HEIGHT = 600;
    float SCALE = 1.5f; // pixel ratio not 1

    int CBAR_WIDTH = 100; // width of a single colorbar
    int CBAR_HEIGHT = 0; // useless in this test

    int numberOfColorbars = 2; // multiple colorbars

    // --------------------------------
    // Object under test

    //ViewAndColorbarsLayout layout = new ViewAndColorbarsLayout();


    // --------------------------------
    // Given a chart with colorbars

    Chart chart = Mocks.Chart(WIDTH, HEIGHT, SCALE);


    //AWTColorbarLegend legend1 = new AWTC
    //ILegend legend = mock(ILegend.class);
    ILegend legend = spy(ILegend.class);
    when(legend.getMinimumDimension()).thenReturn(new Dimension(CBAR_WIDTH, CBAR_HEIGHT));
    
    //List<ILegend> legends = colorbars(numberOfColorbars, legend);
    
    Camera camera = spy(Camera.class);

    when(chart.getView().getCamera()).thenReturn(camera);
    
    when(chart.getScene().getGraph().getLegends()).thenReturn(colorbars(numberOfColorbars, legend));


    // --------------------------------
    // When updating layout

    layout.update(chart);


    // --------------------------------
    // Then layout configured for TWO colorbar

    int expectRightSideWidth = (int) (CBAR_WIDTH * SCALE * numberOfColorbars);
    int expectLeftSideWidth = WIDTH - expectRightSideWidth;

    // with scene occupying the whole the canvas MINUS the colorbar widths
    Assert.assertEquals(expectLeftSideWidth, layout.sceneViewport.getWidth());
    Assert.assertEquals(HEIGHT, layout.sceneViewport.getHeight());

    // with background occupying full canvas
    Assert.assertEquals(WIDTH, layout.backgroundViewport.getWidth());
    Assert.assertEquals(HEIGHT, layout.backgroundViewport.getHeight());

    Assert.assertTrue(layout.hasColorbars);


    float expectSeparator = 1f * expectLeftSideWidth / WIDTH;

    Assert.assertNotEquals(1, expectSeparator);
    Assert.assertEquals(expectSeparator, layout.screenSeparator, 0.1);

    // --------------------------------
    // When not rendered already

    // DO NOTHING

    // --------------------------------
    // Then legend width is not known yet

    Assert.assertEquals(0, layout.legendsWidth, 0);

    // --------------------------------
    // When rendering

    layout.render(chart.getPainter(), chart);

    
    // --------------------------------
    // Then legend width processed is known
    
    float expectLegendWidth = expectRightSideWidth;
    Assert.assertEquals(expectLegendWidth, layout.legendsWidth, 0);


    // --------------------------------
    // Then colorbars had their viewport defined
    
    float from = expectSeparator;
    float mid = from + (1-expectSeparator)/2;
    float to = 1;

    verify(legend).setViewPort(800, 600, from, mid);
    verify(legend).setViewPort(800, 600, mid, to);

    verify(legend, times(2)).setViewportMode(ViewportMode.STRETCH_TO_FILL);
    
  }



  private List<ILegend> colorbars(int numberOfColorbars, ILegend legend) {
    List<ILegend> legends = new ArrayList<>();

    for (int i = 0; i < numberOfColorbars; i++) {
      legends.add(legend);
    }
    return legends;
  }


}
