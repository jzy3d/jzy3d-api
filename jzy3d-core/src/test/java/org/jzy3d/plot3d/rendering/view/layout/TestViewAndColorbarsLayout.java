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
import org.jzy3d.plot3d.rendering.view.TestView2DProcessing;
import org.jzy3d.plot3d.rendering.view.ViewportMode;

/**
 * This verify that the {@link ViewAndColorbarsLayout} is able to split the canvas in two area, the
 * view area on the left and the colorbar area on the right.
 * 
 * Other tests relating to layout can be found at
 * <ul>
 * <li>core/{@link TestView2DProcessing}
 * <li>core/TestCamera_EmulGL_Onscreen
 * <li>core-awt/TestAWTColorbarLegend
 * <li>emulgl-awt/ITTestAWTColorbarLegend
 * <li>emulgl-awt/TestCamera_EmulGL_Offscreen
 * <li>emulgl-awt/TestCameraNative_Viewport
 * <li>native-jogl-core/TestCameraNative_Projection
 * </ul>
 * 
 * @author Martin
 *
 */
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
    ViewAndColorbarsLayout layout = new ViewAndColorbarsLayout();

    whenColorbars_ThenScreenSeparatorIsProcessed(layout, false);

  }

  public void whenColorbars_ThenScreenSeparatorIsProcessed(ViewAndColorbarsLayout layout,
      boolean isEmulGL) {
    int WIDTH = 800;
    int HEIGHT = 600;
    float SCALE = 1.5f; // pixel ratio not 1

    int CBAR_WIDTH = 100; // width of a single colorbar
    int CBAR_HEIGHT = 0; // useless in this test

    int numberOfColorbars = 2; // multiple colorbars

    // --------------------------------
    // Check object under test

    // Assert.assertFalse(layout.isShrinkColorbar());

    // --------------------------------
    // Given a chart with colorbars

    Chart chart = Mocks.Chart(WIDTH, HEIGHT, SCALE);


    // AWTColorbarLegend legend1 = new AWTC
    // ILegend legend = mock(ILegend.class);
    ILegend legend = spy(ILegend.class);
    when(legend.getMinimumDimension()).thenReturn(new Dimension(CBAR_WIDTH, CBAR_HEIGHT));

    // List<ILegend> legends = colorbars(numberOfColorbars, legend);

    Camera camera = spy(Camera.class);

    when(chart.getView().getCamera()).thenReturn(camera);

    when(chart.getScene().getGraph().getLegends()).thenReturn(colorbars(numberOfColorbars, legend));


    // --------------------------------
    // When updating layout

    layout.update(chart);


    // --------------------------------
    // Then layout configured for TWO colorbar

    int expectRightSideWidth = Math.round(CBAR_WIDTH * numberOfColorbars);
    int expectLeftSideWidth;

    if (isEmulGL) {
      expectLeftSideWidth = WIDTH;
    } else {
      expectLeftSideWidth = WIDTH - expectRightSideWidth;
    }

    // with scene occupying the whole the canvas MINUS the colorbar widths
    Assert.assertEquals(expectLeftSideWidth, layout.sceneViewport.getWidth());
    Assert.assertEquals(HEIGHT, layout.sceneViewport.getHeight());

    // with background occupying full canvas
    Assert.assertEquals(WIDTH, layout.backgroundViewport.getWidth());
    Assert.assertEquals(HEIGHT, layout.backgroundViewport.getHeight());

    Assert.assertTrue(layout.hasColorbars);


    float expectSeparator = 1f * (WIDTH - expectRightSideWidth) / WIDTH;

    Assert.assertNotEquals(1, expectSeparator);
    Assert.assertEquals(expectSeparator, layout.screenSeparator, 0.1);


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
    float mid = from + (1 - expectSeparator) / 2;
    float to = 1;

    if (isEmulGL) {
      verify(legend).setViewPort(1200, 900, from, mid);
      verify(legend).setViewPort(1200, 900, mid, to);
      //verify(legend, times(1)).setViewportMode(ViewportMode.STRETCH_TO_FILL);
    } else {
      verify(legend).setViewPort(800, 600, from, mid);
      verify(legend).setViewPort(800, 600, mid, to);
      verify(legend, times(2)).setViewportMode(ViewportMode.STRETCH_TO_FILL);
    }

  }



  private List<ILegend> colorbars(int numberOfColorbars, ILegend legend) {
    List<ILegend> legends = new ArrayList<>();

    for (int i = 0; i < numberOfColorbars; i++) {
      legends.add(legend);
    }
    return legends;
  }


}
