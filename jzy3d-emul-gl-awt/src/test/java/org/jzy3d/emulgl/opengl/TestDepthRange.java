package org.jzy3d.emulgl.opengl;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.Wireframeable;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * {@AxisBox} and 
 * @author martin
 *
 */
public class TestDepthRange {
  @Test
  public void whenRenderAxis_DepthRangeModifiedByAxis() {

    // When
    EmulGLChartFactory factory = new EmulGLChartFactory();
    Chart chart = factory.newChart(Quality.Advanced());
    
    Shape surface = surface();
    chart.add(surface);
    
    IPainter painter = spy(chart.getView().getPainter());
    
    // ----------------------------------------------
    // Then surface will NOT update depth range
    
    surface.setPolygonWireframeDepthTrick(false);
    surface.draw(painter);
    
    verify(painter, never()).glDepthRangef(0, 1);
    
    // ----------------------------------------------
    // Then axis will update depth range
    
    chart.getView().getAxis().draw(painter);

    // Called that way to push the depth range farther
    verify(painter, atLeast(1)).glDepthRangef(AxisBox.NO_OVERLAP_DEPTH_RATIO, 1);

    // Called that way for reset
    verify(painter, atLeast(1)).glDepthRangef(0, 1);
  }
  
  @Test
  public void whenRenderSurface_DepthRangeModifiedBySurface() {

    // When
    EmulGLChartFactory factory = new EmulGLChartFactory();
    Chart chart = factory.newChart(Quality.Advanced());
    
    Shape surface = surface();
    chart.add(surface);
    
    IPainter painter = spy(chart.getView().getPainter());
    
    // ----------------------------------------------
    // Then surface will NOT update depth range
    
    surface.setPolygonWireframeDepthTrick(false);
    surface.draw(painter);
    
    verify(painter, never()).glDepthRangef(0, 1);

    // ----------------------------------------------
    // Then surface WILL update depth range
    
    surface.setPolygonWireframeDepthTrick(true);
    surface.draw(painter);
    
    // config for face
    verify(painter, atLeast(1)).glDepthRangef(0, 1-Wireframeable.NO_OVERLAP_DEPTH_RATIO);
    // config for wireframe
    verify(painter, atLeast(1)).glDepthRangef(Wireframeable.NO_OVERLAP_DEPTH_RATIO, 1);
    // back to default config
    verify(painter, atLeast(1)).glDepthRangef(0, 1);

    
    // ----------------------------------------------
    // Then axis will update depth range
    
    chart.getView().getAxis().draw(painter);

    // Called that way to push the depth range farther
    verify(painter, atLeast(1)).glDepthRangef(AxisBox.NO_OVERLAP_DEPTH_RATIO, 1);

    // Called that way for reset
    verify(painter, atLeast(1)).glDepthRangef(0, 1);
    
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
        surface.getBounds().getZmax(), new Color(1, 1, 1, ALPHA_FACTOR));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }

  static final float ALPHA_FACTOR = 0.75f;


}
