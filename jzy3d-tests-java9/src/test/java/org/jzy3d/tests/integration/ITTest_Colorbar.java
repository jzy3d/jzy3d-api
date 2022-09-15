package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.HiDPI;


/**
 * Test non reg of colorbar and text layout.
 */
public class ITTest_Colorbar extends ITTest{
  /** This main method is here to test manually a chart and keep it open until one close it explicitely. */
  public static void main(String[] args) {
    open(new ITTest_Colorbar().whenColorbar_IsModifiedByCustomFont(WT.EmulGL_AWT, HiDPI.ON));
  }
  
  /* ************************************************************************************************** */

  /**
   * Trying to get image by hovering the test method name in my IDE but no luck yet
   * 
   * <img src="src/test/resources/ITTest_Colorbar,%20EmulGL_AWT,%20HiDPI=OFF,%20Font=AppleChancery24.png"/>
   * <img src="src/test/resources/ITTest_Colorbar,%20EmulGL_AWT,%20HiDPI=ON,%20Font=AppleChancery24.png"/>
   */
  @Test
  public void whenColorbar_IsModifiedByCustomFont() {
    System.out.println("ITTest : whenColorbar_IsModifiedByCustomFont");

    forEach((toolkit, resolution) -> whenColorbar_IsModifiedByCustomFont(toolkit, resolution));
  }

  public Chart whenColorbar_IsModifiedByCustomFont(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = chart(wt, hidpi);
    Shape surface = surface();
    chart.add(surface);
    
    // When
    chart.getAxisLayout().setZAxisSide(ZAxisSide.LEFT);
    chart.getAxisLayout().setFont(new Font("Apple Chancery", 24)); 
    
    surface.setLegend(new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout()));

    // Then
    assertChart(chart, name(this, "IsModifiedByCustomFont", wt, chart.getQuality().getHiDPI()));
    
    return chart;
  }
  
  public String font(Chart chart) {
    Font f = chart.getAxisLayout().getFont();
    return "Font"+KV+"" + f.getName().replace(" ", "") + f.getHeight();
  }
  
  /* ************************************************************************************************** */
  
  /*@Test
  public void whenColorbar_HasMininumWidth() {
    System.out.println("ITTest : whenColorbar_HasMininumWidth");
        
    // this show the colorbar almost in the middle of the chart which is normal
    // with the tested configuration (which yielded to letting the colorbar shrink
    // properly to the right
    //whenColorbar_HasMininumWidth(WT.EmulGL_AWT, HiDPI.OFF);
    
    forEach((WT toolkit, HiDPI resolution)->whenColorbar_HasMininumWidth(toolkit, resolution));
  }

  public Chart whenColorbar_HasMininumWidth(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = chart(wt, hidpi);
    Shape surface = surface();
    chart.add(surface);

    // When
    AxisLayout layout = (AxisLayout)chart.getAxisLayout();
    layout.setZAxisSide(ZAxisSide.LEFT);
    layout.setFont(Font.TimesRoman_10); 
    layout.setFontSizePolicy(new HiDPIProportionalFontSizePolicy(chart.getView()));
    
    AWTColorbarLegend legend = new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout());
    surface.setLegend(legend);
    legend.setMinimumWidth(300);    
    

    // Then
    assertChart(chart, name(this, "HasMinimumWidth", wt, chart.getQuality().getHiDPI()));
    
    return chart;
  }*/
  
  /* ************************************************************************************************** */
  
  /*@Test
  public void whenColorbar_ShrinkBigFont() {
    System.out.println("ITTest : whenColorbar_ShrinkBigFont");

    forEach((toolkit, resolution) -> whenColorbar_ShrinkBigFont(toolkit, resolution));
  }


  public Chart whenColorbar_ShrinkBigFont(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = chart(wt, hidpi);
    Shape surface = surface();
    chart.add(surface);

    // When
    chart.getAxisLayout().setZAxisSide(ZAxisSide.LEFT);
    chart.getAxisLayout().setFont(Font.TimesRoman_24); 
    
    AWTColorbarLegend legend = new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout());
    surface.setLegend(legend);
    
    String name;
    
    if(WT.EmulGL_AWT.equals(wt)) {
      EmulGLSkin skin = EmulGLSkin.on(chart);
      EmulGLViewAndColorbarsLayout layout = skin.getLayout();
      
      name = name(this, "ShrinkBigFont", wt, chart.getQuality().getHiDPI());
    }
    else {
      name = name(this, "ShrinkBigFont", wt, chart.getQuality().getHiDPI());
    }
    
    // Then
    assertChart(chart, name);
    
    return chart;
  }*/
  
  /* ************************************************************************************************** */
  
  /*@Test
  public void whenColorbar_Shrink() {
    System.out.println("ITTest : whenColorbar_Shrink");

    forEach((WT toolkit, HiDPI resolution) -> whenColorbar_Shrink(toolkit, resolution));
  }


  public Chart whenColorbar_Shrink(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = chart(wt, hidpi);
    Shape surface = surface();
    chart.add(surface);

    // When
    chart.getAxisLayout().setZAxisSide(ZAxisSide.LEFT);
    
    AWTColorbarLegend legend = new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout());
    surface.setLegend(legend);
    //legend.setMinimumWidth(300);    
    
    String name;
    
    if(WT.EmulGL_AWT.equals(wt)) {
      EmulGLSkin skin = EmulGLSkin.on(chart);
      EmulGLViewAndColorbarsLayout layout = skin.getLayout();
      
      name = name(this, "Shrink", wt, chart.getQuality().getHiDPI());
    }
    else {
      name = name(this, "Shrink", wt, chart.getQuality().getHiDPI());
    }
    
    // Then
    assertChart(chart, name);
    
    return chart;
  }*/


}
