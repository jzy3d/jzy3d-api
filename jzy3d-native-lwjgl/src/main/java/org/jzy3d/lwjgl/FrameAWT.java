package org.jzy3d.lwjgl;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingUtilities;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.maths.Rectangle;
import org.lwjgl.opengl.awt.AWTGLCanvas;

public class FrameAWT extends java.awt.Frame implements IFrame {
  private static final long serialVersionUID = -4482149010771554002L;

  protected Chart chart;

  // public constructor for easier construction by reflexion
  public FrameAWT() {}

  public FrameAWT(Chart chart, Rectangle bounds, String title) {
    initialize(chart, bounds, title);
  }

  public FrameAWT(Chart chart, Rectangle bounds, String title, String message) {
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

    //this.setLayout(new BorderLayout());

    AWTGLCanvas canvas = (AWTGLCanvas) chart.getCanvas();
    this.add(canvas);//, BorderLayout.CENTER);
    this.pack();
    this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);

    this.setVisible(true);
    //this.transferFocus();


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
