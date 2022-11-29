package org.jzy3d.chart;

import java.awt.Frame;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.junit.Ignore;
import org.junit.Test;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class TestAddRemoveFromFrame {
  Logger logger = Logger.getLogger(TestAddRemoveFromFrame.class.getSimpleName());
  
  int PAUSE_MS = 200;
  int RENDER_LOOP = 1;
  int FRAME_SIZE = 500;

  public static void main(String[] args) {
    AWTChartFactory factory = new AWTChartFactory();

    Frame awtFrame = new Frame();
    


    TestAddRemoveFromFrame t = new TestAddRemoveFromFrame();
    t.PAUSE_MS = 2000;
    t.RENDER_LOOP = 3;
    t.addRemove_Scenario(factory, awtFrame, "addRemove_AWTCanvas_FromAWTFrame");

  }

  @Test
  public void addRemove_AWTCanvas_FromAWTFrame() {

    AWTChartFactory factory = new AWTChartFactory();

    Frame awtFrame = new Frame();

    addRemove_Scenario(factory, awtFrame, "addRemove_AWTCanvas_FromAWTFrame");

  }

  @Ignore("Ignore this test that hangs on Ubuntu and should not happend in real life (Swing Canvas should be added to a Swing app or Swing frame")
  @Test
  public void addRemove_SwingCanvas_FromAWTFrame() {

    SwingChartFactory factory = new SwingChartFactory();

    Frame awtFrame = new Frame();

    addRemove_Scenario(factory, awtFrame, "addRemove_SwingCanvas_FromAWTFrame");

  }

  @Test
  public void addRemove_AWTCanvas_FromSwingFrame() {

    AWTChartFactory factory = new AWTChartFactory();

    JFrame swingFrame = new JFrame();

    addRemove_Scenario(factory, swingFrame, "addRemove_AWTCanvas_FromSwingFrame");

  }

  @Test
  public void addRemove_SwingCanvas_FromSwingFrame() {

    SwingChartFactory factory = new SwingChartFactory();

    JFrame swingFrame = new JFrame();

    addRemove_Scenario(factory, swingFrame, "addRemove_SwingCanvas_FromSwingFrame");

  }

  public void addRemove_Scenario(ChartFactory factory, Frame frame, String title) {
    info("-------------------------------------------------");

    // -------------------------------------------------
    // Given : a chart added to an application frame

    Quality q = Quality.Advanced().setAnimated(true);

    Chart chart = factory.newChart(q);
    chart.add(SampleGeom.surface());
    chart.addMouse();

    // chart.getView().getAxis().setTextRenderer(new TextBitmapRenderer());

    frame.setTitle(title);
    frame.add((java.awt.Component) chart.getCanvas());
    frame.pack();
    frame.setBounds(0, 0, FRAME_SIZE, FRAME_SIZE);
    frame.setVisible(true);

    // -------------------------------------------------
    // Given : we queried a repaint and left some time to AWT
    // to achieve it before we start removing the component

    chart.render();
    info(title + " : Should appear. Now waiting " + PAUSE_MS + " ms");
    chart.sleep(PAUSE_MS);

    // -------------------------------------------------
    // When : removing chart from the application frame

    frame.remove((java.awt.Component) chart.getCanvas());
    info(title + " : Should disappear. Now rendering " + RENDER_LOOP + " times");

    for (int i = 0; i < RENDER_LOOP; i++) {
      chart.render();
    }
    
    info(title + " : Should disappear. Now waiting " + PAUSE_MS + " ms");

    chart.sleep(PAUSE_MS);

    // Then no exception should occur

    // -------------------------------------------------
    // When adding the chart again

    frame.add((java.awt.Component) chart.getCanvas());
    info(title + " : Should re-appear. Now rendering " + RENDER_LOOP + " times");

    for (int i = 0; i < RENDER_LOOP; i++) {
      chart.render();
    }
    
    info(title + " : Should re-appear. Now waiting " + PAUSE_MS + " ms");

    chart.sleep(PAUSE_MS);
    
    info(title + " : done");

    chart.dispose();
    
    info(title + " : disposed");
  }

  public void info(String info) {
    System.out.println(Utils.dat2str("H:mm:ss:SSS") + "\t" + info );
    //logger.info(info);
  }

}
