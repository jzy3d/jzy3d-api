package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartView;
import org.jzy3d.chart.EmulGLSkin;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.plot3d.rendering.view.layout.EmulGLViewAndColorbarsLayout;
import org.jzy3d.plot3d.rendering.view.layout.NativeViewAndColorbarsLayout;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;
import org.jzy3d.tests.integration.ITTest.WT;


/**
 * Test non reg of colorbar and text layout with HiDPI EmulGL charts.
 */
public class ITTest_Colorbar {
  public static void main(String[] args) {
    new ITTest_Colorbar().whenColorbarShrinkBigFont(WT.EmulGL_AWT, HiDPI.ON).open();
  }
  
  /* ************************************************************************************************** */

  @Test
  public void whenColorbarIsModifiedByCustomFont() {
    whenColorbarIsModifiedByCustomFont(WT.EmulGL_AWT, HiDPI.ON);
    whenColorbarIsModifiedByCustomFont(WT.EmulGL_AWT, HiDPI.OFF);
    whenColorbarIsModifiedByCustomFont(WT.Native_AWT, HiDPI.OFF);
    //whenColorbarIsModifiedByCustomFont(WT.Native_AWT, HiDPI.ON);
  }

  public Chart whenColorbarIsModifiedByCustomFont(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = ITTest.chart(wt, hidpi);
    Shape surface = ITTest.surface();
    chart.add(surface);
    
    // When
    chart.getAxisLayout().setZAxisSide(ZAxisSide.LEFT);
    chart.getAxisLayout().setFont(Font.TimesRoman_24); 
    
    surface.setLegend(new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout()));

    // Then
    ITTest.assertChart(chart, ITTest.name(this, wt, chart.getQuality().getHiDPI(), "Font=TimesRoman24"));
    
    return chart;
  }
  
  /* ************************************************************************************************** */
  
  @Test
  public void whenColorbarHasMininumWidth() {
    whenColorbarHasMininumWidth(WT.EmulGL_AWT, HiDPI.ON);
    whenColorbarHasMininumWidth(WT.EmulGL_AWT, HiDPI.OFF);
    whenColorbarHasMininumWidth(WT.Native_AWT, HiDPI.OFF);
  }

  public Chart whenColorbarHasMininumWidth(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = ITTest.chart(wt, hidpi);
    Shape surface = ITTest.surface();
    chart.add(surface);

    // When
    chart.getAxisLayout().setZAxisSide(ZAxisSide.LEFT);
    chart.getAxisLayout().setFont(Font.TimesRoman_24); 
    
    AWTColorbarLegend legend = new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout());
    surface.setLegend(legend);
    legend.setMinimumWidth(300);    
    

    // Then
    ITTest.assertChart(chart, ITTest.name(this, wt, chart.getQuality().getHiDPI(), "Width=300"));
    
    return chart;
  }
  
  /* ************************************************************************************************** */
  
  @Test
  public void whenColorbarShrinkBigFont() {
    whenColorbarShrinkBigFont(WT.Native_AWT, HiDPI.OFF);
    whenColorbarShrinkBigFont(WT.EmulGL_AWT, HiDPI.ON);
    whenColorbarShrinkBigFont(WT.EmulGL_AWT, HiDPI.OFF);
  }


  public Chart whenColorbarShrinkBigFont(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = ITTest.chart(wt, hidpi);
    Shape surface = ITTest.surface();
    chart.add(surface);

    // When
    chart.getAxisLayout().setZAxisSide(ZAxisSide.LEFT);
    chart.getAxisLayout().setFont(Font.TimesRoman_24); 
    
    AWTColorbarLegend legend = new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout());
    surface.setLegend(legend);
    //legend.setMinimumWidth(300);    
    
    String name;
    
    if(WT.EmulGL_AWT.equals(wt)) {
      EmulGLSkin skin = EmulGLSkin.on(chart);
      EmulGLViewAndColorbarsLayout layout = skin.getLayout();
      layout.setShrinkColorbar(true);
      
      name = ITTest.name(this, wt, chart.getQuality().getHiDPI(), "Shrink=ON, Font=TimesRoman_24");
    }
    else {
      NativeViewAndColorbarsLayout layout  = (NativeViewAndColorbarsLayout)((ChartView)chart.getView()).getLayout();
      layout.setShrinkColorbar(true);
      
      name = ITTest.name(this, wt, chart.getQuality().getHiDPI(), "Shrink=ON, Font=TimesRoman_24");
      
    }
    
    // Then
    ITTest.assertChart(chart, name);
    
    return chart;
  }
  
  /* ************************************************************************************************** */
  
  @Test
  public void whenColorbarShrink() {
    whenColorbarShrink(WT.Native_AWT, HiDPI.OFF);
    whenColorbarShrink(WT.EmulGL_AWT, HiDPI.ON);
    whenColorbarShrink(WT.EmulGL_AWT, HiDPI.OFF);
  }


  public Chart whenColorbarShrink(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = ITTest.chart(wt, hidpi);
    Shape surface = ITTest.surface();
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
      layout.setShrinkColorbar(true);
      
      name = ITTest.name(this, wt, chart.getQuality().getHiDPI(), "Shrink=ON");
    }
    else {
      NativeViewAndColorbarsLayout layout  = (NativeViewAndColorbarsLayout)((ChartView)chart.getView()).getLayout();
      layout.setShrinkColorbar(true);
      
      name = ITTest.name(this, wt, chart.getQuality().getHiDPI(), "Shrink=ON");
      
    }
    
    // Then
    ITTest.assertChart(chart, name);
    
    return chart;
  }


}
