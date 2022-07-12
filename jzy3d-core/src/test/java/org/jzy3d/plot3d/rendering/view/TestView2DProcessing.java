package org.jzy3d.plot3d.rendering.view;

import static org.mockito.Mockito.when;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.mocks.jzy3d.Mocks;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

public class TestView2DProcessing {

    @Test
    public void testView2DProcessing() {
      // text
      int FONT_HEIGHT = 12;
      int Y_TICK_MAX_WIDTH = 35;
      int Y_AXIS_WIDTH = 100;
      
      // canvas
      int VIEWPORT_WIDTH = 1000;
      int VIEWPORT_HEIGHT = VIEWPORT_WIDTH/2;

      // scale
      int pixScale = 1;
      
      // layout
      int MARGIN = 10; // distance to canvas border
      int TICK_DIST = 20; // tick distance to axis border
      int AXIS_DIST = 30; // axis label distance to tick label
      
      
      String yAxisLabel = "yLabel_100px_Width";
      
      // ----------------------------------------
      // Given a font
      
      Font font = new Font("Helvetica", FONT_HEIGHT);
      
      // Given a painter returning text width
      IPainter painter = Mocks.Painter();
      when(painter.getTextLengthInPixels(font, yAxisLabel)).thenReturn(Y_AXIS_WIDTH);
      
      // Given an axis layout returning a tick label width and default font
      AxisLayout axisLayout = Mocks.AxisLayout();
      when(axisLayout.getFont()).thenReturn(font);
      when(axisLayout.getYAxisLabel()).thenReturn(yAxisLabel);
      when(axisLayout.getMaxYTickLabelWidth(painter)).thenReturn(Y_TICK_MAX_WIDTH);
      
      // Given a view with axis, painter and native canvas
      View view = Mocks.View(Mocks.Axis(axisLayout), painter, Mocks.Canvas(true));
      
      
      View2DLayout layout = new View2DLayout();
      layout.setMargin(MARGIN);
      layout.setTickLabelDistance(TICK_DIST);
      layout.setAxisLabelDistance(AXIS_DIST);
      layout.setTextAddMargin(true); // by default always true
      layout.setSymetricMargin(false);
      
      when(view.get2DLayout()).thenReturn(layout);
      
      
      View2DProcessing processing = new View2DProcessing(view);
      
      // -----------------------------------------------------------------
      // ----------------------------------------
      // When processing margins with a vertical Y AXIS, pixel scale = 1
      
      when(view.getPixelScale()).thenReturn(new Coord2d(pixScale,pixScale));
      when(view.getViewMode()).thenReturn(ViewPositionMode.TOP);

      when(axisLayout.getYAxisLabelOrientation()).thenReturn(LabelOrientation.VERTICAL);

      ViewportConfiguration viewport = new ViewportConfiguration(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, 0, 0);
      BoundingBox3d bounds = new BoundingBox3d(-10, 10, -10, 10, -1, 1);
      
      processing.apply(viewport, bounds);
     
      // ----------------------------------------
      // Then margin appropriately processed
      
      Coord2d ratio = new Coord2d(0.02265006, 0.049261093);
      
      Assert.assertEquals(ratio, processing.getModelToScreen());

      
      float xpectWidth = MARGIN*2 + TICK_DIST + Y_TICK_MAX_WIDTH + AXIS_DIST + FONT_HEIGHT;
      float xpectHeight = MARGIN*2 + TICK_DIST + FONT_HEIGHT + AXIS_DIST + FONT_HEIGHT;
      
      Assert.assertEquals(xpectWidth, processing.getArea().width, 0.1);
      Assert.assertEquals(xpectHeight, processing.getArea().height, 0.1);
      
      
      // ----------------------------------------
      // When processing margins with a vertical Y AXIS, pixel scale = 2
      
      pixScale = 2;
      when(view.getPixelScale()).thenReturn(new Coord2d(pixScale,pixScale));
      when(view.getViewMode()).thenReturn(ViewPositionMode.TOP);

      processing.apply(viewport, bounds);

      // ----------------------------------------
      // Then margins are larger

      xpectWidth = 2*MARGIN*2 + 2*TICK_DIST + Y_TICK_MAX_WIDTH + 2*AXIS_DIST + FONT_HEIGHT;
      xpectHeight = 2*MARGIN*2 + 2*TICK_DIST + FONT_HEIGHT + 2*AXIS_DIST + FONT_HEIGHT;

      Assert.assertEquals(xpectWidth, processing.getArea().width, 0.1);
      Assert.assertEquals(xpectHeight, processing.getArea().height, 0.1);

      // -----------------------------------------------------------------
      // ----------------------------------------
      // When processing margins with a horizontal Y AXIS, pixel scale = 1
      
      pixScale = 1;

      when(view.getPixelScale()).thenReturn(new Coord2d(pixScale,pixScale));
      when(axisLayout.getYAxisLabelOrientation()).thenReturn(LabelOrientation.HORIZONTAL);
      
      processing.apply(viewport, bounds);

      // ----------------------------------------
      // Then
      
      ratio.x = 0.02515723f;
      
      Assert.assertEquals(ratio, processing.getModelToScreen());

      
      xpectWidth = MARGIN*2 + TICK_DIST + Y_TICK_MAX_WIDTH + AXIS_DIST + Y_AXIS_WIDTH;
      xpectHeight = MARGIN*2 + TICK_DIST + FONT_HEIGHT + AXIS_DIST + FONT_HEIGHT;

      Assert.assertEquals(xpectWidth, processing.getArea().width, 0.1);
      Assert.assertEquals(xpectHeight, processing.getArea().height, 0.1);
      
      // ----------------------------------------
      // When processing margins with a horizontal Y AXIS, pixel scale = 2
      
      pixScale = 2;

      when(view.getPixelScale()).thenReturn(new Coord2d(pixScale,pixScale));
      
      processing.apply(viewport, bounds);

      // ----------------------------------------
      // Then margins are larger

      xpectWidth = 2*MARGIN*2 + 2*TICK_DIST + Y_TICK_MAX_WIDTH + 2*AXIS_DIST + Y_AXIS_WIDTH;
      xpectHeight = 2*MARGIN*2 + 2*TICK_DIST + FONT_HEIGHT + 2*AXIS_DIST + FONT_HEIGHT;

      Assert.assertEquals(xpectWidth, processing.getArea().width, 0.1);
      Assert.assertEquals(xpectHeight, processing.getArea().height, 0.1);
    }
    
    
    @Test
    public void whenNoMargin_ThenNo_NAN_factor() {
      // text
      int FONT_HEIGHT = 12;
      int Y_TICK_MAX_WIDTH = 35;
      int Y_AXIS_WIDTH = 100;
      
      // canvas
      int VIEWPORT_WIDTH = 1000;
      int VIEWPORT_HEIGHT = VIEWPORT_WIDTH/2;

      // layout
      int MARGIN = 0;
      int TICK_DIST = 0;
      int AXIS_DIST = 0;
      boolean TEXT_ADD_MARGIN = false;
      
      
      String yAxisLabel = "yLabel_100px_Width";
      
      // ----------------------------------------
      // Given a font
      
      Font font = new Font("Helvetica", FONT_HEIGHT);
      
      // Given a painter returning text width
      IPainter painter = Mocks.Painter();
      when(painter.getTextLengthInPixels(font, yAxisLabel)).thenReturn(Y_AXIS_WIDTH);
      
      // Given an axis layout returning a tick label width and default font
      AxisLayout axisLayout = Mocks.AxisLayout();
      when(axisLayout.getFont()).thenReturn(font);
      when(axisLayout.getYAxisLabel()).thenReturn(yAxisLabel);
      when(axisLayout.getMaxYTickLabelWidth(painter)).thenReturn(Y_TICK_MAX_WIDTH);
      
      // Given a view with settings
      View view = Mocks.View(Mocks.Axis(axisLayout), painter, Mocks.Canvas(true));
      when(view.getPixelScale()).thenReturn(new Coord2d(1,1));
      when(view.getViewMode()).thenReturn(ViewPositionMode.TOP);

      View2DLayout layout = new View2DLayout();
      layout.setMargin(MARGIN);
      layout.setTickLabelDistance(TICK_DIST);
      layout.setAxisLabelDistance(AXIS_DIST);
      layout.setTextAddMargin(TEXT_ADD_MARGIN); // by default always true
      layout.setSymetricMargin(false);
      
      when(view.get2DLayout()).thenReturn(layout);
      
      
      View2DProcessing processing = new View2DProcessing(view);
      
      // ----------------------------------------
      // When processing margins with a vertical Y AXIS
      
      when(axisLayout.getYAxisLabelOrientation()).thenReturn(LabelOrientation.VERTICAL);

      ViewportConfiguration viewport = new ViewportConfiguration(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, 0, 0);
      BoundingBox3d bounds = new BoundingBox3d(-10, 10, -10, 10, -1, 1);
      
      processing.apply(viewport, bounds);
     
      // ----------------------------------------
      // Then
      
      Assert.assertEquals(1f, processing.getModelToScreen().x, 0.1);
      Assert.assertEquals(1f, processing.getModelToScreen().y, 0.1);
      Assert.assertEquals(0, processing.getArea().width, 0.1);
      Assert.assertEquals(0, processing.getArea().height, 0.1);
    }
    
    @Test
    public void whenMarginEqualsCanvas_ThenNo_NAN_factor() {
      // text
      int FONT_HEIGHT = 12;
      int Y_TICK_MAX_WIDTH = 35;
      int Y_AXIS_WIDTH = 100;
      
      // canvas
      int VIEWPORT_WIDTH = 1000;
      int VIEWPORT_HEIGHT = VIEWPORT_WIDTH;

      // layout
      int MARGIN = VIEWPORT_WIDTH/2;
      int TICK_DIST = 0;
      int AXIS_DIST = 0;
      boolean TEXT_ADD_MARGIN = false;
      
      
      String yAxisLabel = "yLabel_100px_Width";
      
      // ----------------------------------------
      // Given a font
      
      Font font = new Font("Helvetica", FONT_HEIGHT);
      
      // Given a painter returning text width
      IPainter painter = Mocks.Painter();
      when(painter.getTextLengthInPixels(font, yAxisLabel)).thenReturn(Y_AXIS_WIDTH);
      
      // Given an axis layout returning a tick label width and default font
      AxisLayout axisLayout = Mocks.AxisLayout();
      when(axisLayout.getFont()).thenReturn(font);
      when(axisLayout.getYAxisLabel()).thenReturn(yAxisLabel);
      when(axisLayout.getMaxYTickLabelWidth(painter)).thenReturn(Y_TICK_MAX_WIDTH);
      
      // Given a view with settings
      View view = Mocks.View(Mocks.Axis(axisLayout), painter, Mocks.Canvas(true));
      when(view.getPixelScale()).thenReturn(new Coord2d(1,1));
      when(view.getViewMode()).thenReturn(ViewPositionMode.TOP);

      View2DLayout layout = new View2DLayout();
      layout.setMargin(MARGIN);
      layout.setTickLabelDistance(TICK_DIST);
      layout.setAxisLabelDistance(AXIS_DIST);
      layout.setTextAddMargin(TEXT_ADD_MARGIN); // by default always true
      layout.setSymetricMargin(false);
      
      when(view.get2DLayout()).thenReturn(layout);
      
      
      View2DProcessing processing = new View2DProcessing(view);
      
      // ----------------------------------------
      // When processing margins with a vertical Y AXIS
      
      when(axisLayout.getYAxisLabelOrientation()).thenReturn(LabelOrientation.VERTICAL);

      ViewportConfiguration viewport = new ViewportConfiguration(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, 0, 0);
      BoundingBox3d bounds = new BoundingBox3d(-10, 10, -10, 10, -1, 1);
      
      processing.apply(viewport, bounds);
     
      // ----------------------------------------
      // Then
      //System.out.println(processing.getModelToScreen());
      Assert.assertTrue(Float.isFinite(processing.getModelToScreen().x));
      Assert.assertTrue(Float.isFinite(processing.getModelToScreen().y));

    }
}
