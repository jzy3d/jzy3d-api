package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.HiDPIProportionalFontSizePolicy;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;
import org.jzy3d.plot3d.text.drawable.DrawableText;


public class ITTest_Text extends ITTest{
  /** This main method is here to test manually a chart and keep it open until one close it explicitely. */
  public static void main(String[] args) {
    open(new ITTest_Text().whenCustomFont(WT.Native_AWT, HiDPI.ON));
    //open(new ITTest_Text().whenDrawableTextRenderer(WT.EmulGL_AWT, HiDPI.OFF));
    //open(new ITTest_Text().whenDrawableTextRenderer(WT.Native_AWT, HiDPI.OFF));
  }
  
  /* ************************************************************************************************** */

  /**
   * <img src="src/test/resources/"/>
   */
  @Test
  public void whenColorbar_IsModifiedByCustomFont() {
    System.out.println("ITTest : whenColorbar_IsModifiedByCustomFont");

    forEach((toolkit, resolution) -> whenCustomFont(toolkit, resolution));
  }

  public Chart whenCustomFont(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = chart(wt, hidpi);
    Shape surface = surface();
    chart.add(surface);
    
    // When
    AxisLayout layout = (AxisLayout)chart.getAxisLayout();
    layout.setZAxisSide(ZAxisSide.LEFT);
    layout.setFont(new Font("Apple Chancery", 24)); 
    layout.setFontSizePolicy(new HiDPIProportionalFontSizePolicy(chart.getView()));

    surface.setLegend(new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout()));

    // Then
    assertChart(chart, name(this, wt, chart.getQuality().getHiDPI(), "Font=AppleChancery24"));
    
    // For manual tests
    return chart;
  }
  
  /* ************************************************************************************************** */

  @Test
  public void whenDrawableTextRenderer() {
    System.out.println("ITTest : whenDrawableTextRenderer");

    forEach((toolkit, resolution) -> whenDrawableTextRenderer(toolkit, resolution));
  }
  
  public Chart whenDrawableTextRenderer(WT wt, HiDPI hidpi) {
    Chart chart = chart(wt, hidpi, new Rectangle(1200,600));
    
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
    assertChart(chart, name(this, "whenDrawableTextRenderer", wt, chart.getQuality().getHiDPI()));
    
    // For manual tests
    return chart;
    
  }

  private static void createDrawableText(Chart chart, Font font, Coord3d position, Color textColor,
      Color positionColor, Horizontal h, Vertical v) {
    DrawableText t1 = new DrawableText("Horizontal:" + h + "-Vertical:" + v, position, textColor);
    t1.setDefaultFont(font);
    t1.setHalign(h);
    t1.setValign(v);
    
    //t1.setRotation(45);
    Point p1 = new Point(position, positionColor);
    p1.setWidth(5);
    
    chart.add(t1);
    chart.add(p1);
  }
}
