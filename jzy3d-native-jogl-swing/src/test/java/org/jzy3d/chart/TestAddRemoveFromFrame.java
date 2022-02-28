package org.jzy3d.chart;

import java.awt.Frame;
import javax.swing.JFrame;
import org.junit.Test;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

public class TestAddRemoveFromFrame {
  
  int PAUSE_MS = 500;
  int RENDER_LOOP = 300;
  int FRAME_SIZE = 500;
  
  public static void main(String[] args) {
    AWTChartFactory factory = new AWTChartFactory();
    
    Frame awtFrame = new Frame();
    
    new TestAddRemoveFromFrame().addRemove_FromFrame_Scenario(factory, awtFrame, "addRemove_FromAWTFrame_AWTCanvas");
    
  }
  
  @Test
  public void addRemove_FromAWTFrame_AWTCanvas() {

    AWTChartFactory factory = new AWTChartFactory();
    
    Frame awtFrame = new Frame();

    
    addRemove_FromFrame_Scenario(factory, awtFrame, "addRemove_FromAWTFrame_AWTCanvas");
    
  }
  
  @Test
  public void addRemove_FromAWTFrame_SwingCanvas() {
    
    SwingChartFactory factory = new SwingChartFactory();
    
    Frame awtFrame = new Frame();

        
    addRemove_FromFrame_Scenario(factory, awtFrame, "addRemove_FromAWTFrame_SwingCanvas");

  }

  @Test
  public void addRemove_FromSwingFrame_AWTCanvas() {

    AWTChartFactory factory = new AWTChartFactory();
    
    JFrame swingFrame = new JFrame();

    
    addRemove_FromFrame_Scenario(factory, swingFrame, "addRemove_FromAWTFrame_AWTCanvas");
    
  }
  
  @Test
  public void addRemove_FromSwingFrame_SwingCanvas() {
    
    SwingChartFactory factory = new SwingChartFactory();
        
    JFrame swingFrame = new JFrame();

    addRemove_FromFrame_Scenario(factory, swingFrame, "addRemove_FromAWTFrame_SwingCanvas");

  }

  private void addRemove_FromFrame_Scenario(ChartFactory factory, Frame frame, String title) {
    
    // -------------------------------------------------
    // Given : a chart added to an application frame

    Quality q = Quality.Advanced().setAnimated(true);
    
    Chart chart = factory.newChart(q);
    chart.add(SampleGeom.surface());
    chart.addMouse();
    
    //chart.getView().getAxis().setTextRenderer(new TextBitmapRenderer());

    frame.setTitle(title);
    frame.add((java.awt.Component)chart.getCanvas());
    frame.pack();
    frame.setBounds(0,0,FRAME_SIZE,FRAME_SIZE);
    frame.setVisible(true);
    
    // -------------------------------------------------
    // Given : we queried a repaint and left some time to AWT
    // to achieve it before we start removing the component
    
    chart.render();
    System.out.println("Should appear");
    chart.sleep(PAUSE_MS);

    // -------------------------------------------------
    // When : removing chart from the application frame
    
    frame.remove((java.awt.Component)chart.getCanvas());    
    System.out.println("Should disappear");

    for (int i = 0; i < RENDER_LOOP; i++) {
      chart.render();
      //((GLCanvas)chart.getCanvas()).display();
    }
    chart.sleep(PAUSE_MS);    
    
    // Then no exception should occur

    // -------------------------------------------------
    // When adding the chart again
    
    frame.add((java.awt.Component)chart.getCanvas());
    System.out.println("Should re-appear");

    for (int i = 0; i < RENDER_LOOP; i++) {
      chart.render();
      //((GLCanvas)chart.getCanvas()).display();
    }
    chart.sleep(PAUSE_MS);
  }
  


}
