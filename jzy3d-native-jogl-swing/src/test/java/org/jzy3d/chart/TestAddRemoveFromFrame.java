package org.jzy3d.chart;

import java.awt.Frame;
import javax.swing.JFrame;
import org.junit.Test;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import com.jogamp.opengl.awt.GLCanvas;

public class TestAddRemoveFromFrame {
  //@Test
  public void addRemove_AWTCanvas_from_SwingFrame() {
    
    // Given : a chart added to an application frame
    AWTChartFactory factory = new AWTChartFactory();
    Chart chart = factory.newChart();
    chart.add(SampleGeom.surface());
   
    JFrame frame = new JFrame();
    frame.setVisible(true); 
    
    frame.getContentPane().add((java.awt.Component)chart.getCanvas());
    chart.sleep(PAUSE);
    
    // When : removing chart from the application frame
    frame.remove((java.awt.Component)chart.getCanvas());
    frame.repaint();
    chart.render();
    chart.sleep(PAUSE);   
    
    // Then no exception should occur
    
    
    frame.add((java.awt.Component)chart.getCanvas());
    chart.sleep(PAUSE);

  }
  
  int PAUSE = 1000;
  
  @Test
  public void addRemove_AWTCanvas_from_AWTFrame() {

    // Given : a chart added to an application frame
    AWTChartFactory factory = new AWTChartFactory();
    
    Quality q = Quality.Advanced().setAnimated(true);
    
    Chart chart = factory.newChart(q);
    chart.add(SampleGeom.surface());

    Frame frame = new Frame();
    frame.add((java.awt.Component)chart.getCanvas());
    frame.pack();
    frame.setBounds(0,0,300,300);
    frame.setVisible(true);
    
    // -------------------------------------------------
    // Given we queried a repaint and left some time to AWT
    // to achieve it before we start removing the component
    
    chart.render();
    System.out.println("Should appear");
    chart.sleep(PAUSE);

    // -------------------------------------------------
    // When : removing chart from the application frame
    
    frame.remove((java.awt.Component)chart.getCanvas());    
    System.out.println("Should disappear");

    for (int i = 0; i < 250; i++) {
      //chart.render();
      ((GLCanvas)chart.getCanvas()).display();
    }
    chart.sleep(PAUSE);    
    
    // Then no exception should occur

    // -------------------------------------------------
    // When adding the chart again
    
    frame.add((java.awt.Component)chart.getCanvas());
    System.out.println("Should re-appear");
    chart.render();
    chart.sleep(PAUSE);

  }
  
  //@Test
  public void addRemove_AWTCanvas_from_Nowhere() {
    AWTChartFactory factory = new AWTChartFactory();
    
    Quality q = Quality.Advanced().setAnimated(true);
    
    Chart chart = factory.newChart(q);
    chart.add(SampleGeom.surface());
    
    for (int i = 0; i < 50; i++) {
      chart.render();
    }


  }
}
