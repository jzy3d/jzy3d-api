package org.jzy3d.bridge.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jzy3d.chart.Chart;
import org.jzy3d.ui.LookAndFeel;

/** A frame to show a list of charts */
public class SwingChartGroupWindow extends JFrame {
  private static final long serialVersionUID = 7519209038396190502L;

  public SwingChartGroupWindow(Chart... charts) {
    this(Arrays.asList(charts));
  }

  public SwingChartGroupWindow(Collection<? extends Chart> charts) {
    LookAndFeel.apply();

    setGridLayout(charts);

    windowExitListener();
    this.pack();
    setVisible(true);
    setBounds(new java.awt.Rectangle(10, 10, 800, 600));
  }

  private void setGridLayout(Collection<? extends Chart> charts) {
    setLayout(new GridLayout(charts.size(), 1));

    for (Chart c : charts) {
      addChartToGridLayout(c);
    }
  }

  public void addChartToGridLayout(Chart chart) {
    JPanel chartPanel = new JPanel(new BorderLayout());
    // Border b = BorderFactory.createLineBorder(java.awt.Color.black);
    // chartPanel.setBorder(b);
    chartPanel.add((java.awt.Component) chart.getCanvas(), BorderLayout.CENTER);
    add(chartPanel);
  }

  public void windowExitListener() {
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        SwingChartGroupWindow.this.dispose();
        System.exit(0);
      }
    });
  }
}
