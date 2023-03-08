package org.jzy3d.bridge.awt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.maths.Rectangle;
import org.lwjgl.opengl.awt.AWTGLCanvas;

public class FrameAWT extends JFrame implements IFrame {
  private static final long serialVersionUID = -4482149010771554002L;

  protected Chart chart;
  
  protected AWTGLCanvas canvas;

  // public constructor for easier construction by reflexion
  public FrameAWT() {
    super();
  }

  public FrameAWT(Chart chart, Rectangle bounds, String title) {
    super();
    initialize(chart, bounds, title);
  }

  public FrameAWT(Chart chart, Rectangle bounds, String title, String message) {
    super();
    initialize(chart, bounds, title, message);
  }

  @Override
  public void initialize(Chart chart, Rectangle bounds, String title) {
    initialize(chart, bounds, title, "[Awt]");
  }

  @Override
  public void initialize(Chart chart, Rectangle bounds, String title, String message) {
    this.chart = chart;

    if (message != null) {
      this.setTitle(title + message);
    } else {
      this.setTitle(title);
    }

    this.setLayout(new BorderLayout());
    this.setPreferredSize(new Dimension(bounds.width, bounds.height));

    this.canvas = (AWTGLCanvas) chart.getCanvas();
    this.add(canvas, BorderLayout.CENTER);
    this.pack();
    this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);

    //System.out.println(bounds);
    
    this.setVisible(true);
    this.transferFocus();


    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        if (FrameAWT.this.chart != null) {
          FrameAWT.this.remove((java.awt.Component) FrameAWT.this.chart.getCanvas());
          FrameAWT.this.chart.stopAllThreads();
          FrameAWT.this.chart.dispose();
          FrameAWT.this.chart = null;
          FrameAWT.this.dispose();
        }
      }
    });


    startLoop();
  }
  
  
  public void startLoop() {
    // Render loop differ from other 
    Runnable renderLoop = new Runnable() {
      public void run() {
        if (!canvas.isValid())
          return;
        canvas.render();
        SwingUtilities.invokeLater(this);
      }
    };
    SwingUtilities.invokeLater(renderLoop);

  }
}
