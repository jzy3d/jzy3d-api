package org.jzy3d.plot3d.text.renderers.demo;


import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;
import org.jzy3d.plot3d.text.drawable.DrawableText;

/**
 * Test text layout.
 * 
 * @author Martin
 *
 */
public class TestTextRenderer {
  //@Test
  public static void main(String[] args) {
    Quality q = Quality.Advanced();
    q.setHiDPI(HiDPI.OFF);

    
    //ChartFactory factory = new EmulGLChartFactory();
    ChartFactory factory = new AWTChartFactory();
    Chart chart = factory.newChart(q);
    chart.addMouse();
    //chart.setViewMode(ViewPositionMode.TOP);
    //chart.view2d();
    chart.open(1200,600);
    
    // Given : a custom font
    
    Font font = new Font("Apple Chancery", 20);
    
    // Given : a custom axis box text setting
    AxisLayout layout = chart.getView().getAxis().getLayout();
    layout.setFont(font);
    layout.setXTickColor(Color.RED);
    layout.setYTickColor(Color.GREEN);
    layout.setZTickColor(Color.BLUE);
    layout.setZAxisLabelOrientation(LabelOrientation.VERTICAL);
    
    //chart.getView().setBoundManual(new BoundingBox3d(-3f, 3f, -3f, 3f, -3f, 3f));
    
    
    // Given : DrawableTextRenderers

    Color textColor = Color.GRAY;
    Color positionColor = Color.RED;
    
    Coord3d position;

    // Center
    position = new Coord3d(0,0,0);
    createDrawableText(chart, font, position, textColor, positionColor, Horizontal.CENTER, Vertical.BOTTOM);
    
    position = new Coord3d(0,0,1);
    createDrawableText(chart, font, position, textColor, positionColor, Horizontal.CENTER, Vertical.CENTER);

    position = new Coord3d(0,0,2);
    createDrawableText(chart, font, position, textColor, positionColor, Horizontal.CENTER, Vertical.TOP);

    // right    
    position = new Coord3d(1,0,0);
    createDrawableText(chart, font, position, textColor, positionColor, Horizontal.RIGHT, Vertical.BOTTOM);
    
    position = new Coord3d(1,0,1);
    createDrawableText(chart, font, position, textColor, positionColor, Horizontal.RIGHT, Vertical.CENTER);

    position = new Coord3d(1,0,2);
    createDrawableText(chart, font, position, textColor, positionColor, Horizontal.RIGHT, Vertical.TOP);

    // left
    position = new Coord3d(-1,0,0);
    createDrawableText(chart, font, position, textColor, positionColor, Horizontal.LEFT, Vertical.BOTTOM);
    
    position = new Coord3d(-1,0,1);
    createDrawableText(chart, font, position, textColor, positionColor, Horizontal.LEFT, Vertical.CENTER);

    position = new Coord3d(-1,0,2);
    createDrawableText(chart, font, position, textColor, positionColor, Horizontal.LEFT, Vertical.TOP);
  
  
    // Then
    
  }

  private static void createDrawableText(Chart chart, Font font, Coord3d position, Color textColor,
      Color positionColor, Horizontal h, Vertical v) {
    DrawableText t1 = new DrawableText("Horizontal:" + h + "-Vertical:" + v, position, textColor);
    t1.setDefaultFont(font);
    t1.setHalign(h);
    t1.setValign(v);
    Point p1 = new Point(position, positionColor);
    p1.setWidth(5);
    
    chart.add(t1);
    chart.add(p1);
  }

  private Shape surface() {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };

    Range range = new Range(-3, 3);
    int steps = 80;

    final Shape surface = new SurfaceBuilder().orthonormal(mapper, range, steps);
    surface
        .setColorMapper(new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.WHITE);
    return surface;
  }
}
