package org.jzy3d.bridge.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.maths.Rectangle;

public class FrameSwing extends JFrame implements IFrame {

  // public constructor for easier construction by reflexion
  public FrameSwing() {}

  public FrameSwing(Chart chart, Rectangle bounds, String title) {
    initialize(chart, bounds, title);
  }

  @Override
  public void initialize(Chart chart, Rectangle bounds, String title) {
    this.chart = chart;

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container contentPane = getContentPane();
    BorderLayout layout = new BorderLayout();
    contentPane.setLayout(layout);

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        FrameSwing.this.remove((java.awt.Component) FrameSwing.this.chart.getCanvas());
        FrameSwing.this.chart.stopAllThreads();
        FrameSwing.this.chart.dispose();
        FrameSwing.this.chart = null;
        FrameSwing.this.dispose();
      }
    });

    JPanel panel3d = new JPanel();
    panel3d.setLayout(new java.awt.BorderLayout());
    panel3d.add((JComponent) chart.getCanvas());

    contentPane.add((JComponent) chart.getCanvas(), BorderLayout.CENTER);
    setVisible(true);
    setTitle(title + "[Swing]");
    pack();
    setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  private Chart chart;
  private static final long serialVersionUID = 6474157681794629264L;

  @Override
  public void initialize(Chart chart, Rectangle bounds, String title, String message) {
    initialize(chart, bounds, title);
  }

  public void print(String file) throws IOException {
    BufferedImage i = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    // paint(i.getGraphics());
    print(i.getGraphics());
    ImageIO.write(i, "png", new File(file));
    System.out.println("Print Swing Frame to " + file);
  }

}
