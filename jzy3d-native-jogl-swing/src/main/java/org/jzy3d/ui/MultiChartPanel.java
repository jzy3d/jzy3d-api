package org.jzy3d.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import org.jzy3d.ui.views.ImagePanel;
import net.miginfocom.swing.MigLayout;

public class MultiChartPanel extends JPanel {
  private static final long serialVersionUID = 7519209038396190502L;

  protected JTextField tf;
  protected JTextArea textArea;
  protected JScrollPane textPane;

  protected int lineHeightPX = 300;
  protected int columnWidthPX = 500;
  protected boolean growCol = true;
  protected boolean growLine = false;

  // protected List<Chart> charts;

  protected boolean vertical;

  public static String WT = "awt";

  private int nComponent = 0;

  public static void ui(String[] headers, Chart[][] charts) throws IOException {
    MultiChartPanel monitorPanel =
        new MultiChartPanel(charts, headers, null, false, 100, 100, false, false);
    monitorPanel.ui();
  }

  public static void ui(String[] headers, Chart[][] charts, int width, int height)
      throws IOException {
    MultiChartPanel monitorPanel =
        new MultiChartPanel(charts, headers, null, false, width, height, false, false);
    monitorPanel.ui();
  }

  public MultiChartPanel(List<Chart> charts) {
    this(charts, true);
  }

  public MultiChartPanel(List<Chart> charts, boolean vertical) {
    this(charts, vertical, 500, 300);
  }

  public MultiChartPanel(List<Chart> charts, boolean vertical, int width, int height) {
    this(charts, vertical, width, height, true, false);
  }

  public MultiChartPanel(List<Chart> charts, boolean vertical, int width, int height,
      boolean growCol, boolean growLine) {
    LookAndFeel.apply();

    // Main layout
    this.vertical = vertical;
    this.lineHeightPX = height;
    this.columnWidthPX = width;
    this.growCol = growCol;
    this.growLine = growLine;

    String lines = lineInstruction();
    String columns = columnInstruction();
    setLayout(new MigLayout(insets0(), columns, lines));

    for (Chart c : charts) {
      addChart(c);
    }
  }

  public MultiChartPanel(Chart[][] charts, boolean vertical, int width, int height, boolean growCol,
      boolean growLine) throws IOException {
    this(charts, null, null, vertical, width, height, growCol, growLine);
  }

  public MultiChartPanel(Chart[][] charts, String[] header, String[] row, boolean vertical,
      int width, int height, boolean growCol, boolean growLine) throws IOException {
    LookAndFeel.apply();

    // Main layout
    this.vertical = vertical;
    this.lineHeightPX = height;
    this.columnWidthPX = width;
    this.growCol = growCol;
    this.growLine = growLine;

    boolean hasHeader = header != null;

    String lines = null;// lineInstruction();

    if (growLine) {
      if (hasHeader)
        lines = "[20px][" + lineHeightPX + "px,grow]";
      else
        lines = "[" + lineHeightPX + "px,grow]";

    } else {
      if (hasHeader)
        lines = "[20px][" + lineHeightPX + "px]";
      else
        lines = "[" + lineHeightPX + "px]";
    }

    String columns = columnInstruction();
    setLayout(new MigLayout(insets0(), columns, lines));
    // setBounds(0, 0, 700, 600);
    // demoList.setMinimumSize(new Dimension(200,200));
    // textPane.setMinimumSize(new Dimension(700, 50));

    if (hasHeader) {
      for (int i = 0; i < header.length; i++) {
        addPanelAt(new JLabel(header[i]), 0, i);
      }
    }

    for (int i = 0; i < charts.length; i++) {
      for (int j = 0; j < charts[i].length; j++) {
        if (charts[i][j] != null) {
          int r = hasHeader ? i + 1 : i;
          int c = j;
          addChartAt(charts[i][j], r, c);
        } else {

        }
      }
    }

    // setPreferredSize(new Dimension((charts[0].length + 5)* columnWidthPX, (charts.length + 5) *
    // lineHeightPX));
  }

  public String insetsDefault() {
    return "";
  }

  public String insets0() {
    return "insets 0 0 0 0";
  }


  public String lineInstruction() {
    if (growLine) {
      return "[" + lineHeightPX + "px,grow]";
    } else {
      return "[" + lineHeightPX + "px]";
    }
  }

  public String columnInstruction() {
    if (growCol) {
      return "[" + columnWidthPX + "px,grow]";
    } else {
      return "[" + columnWidthPX + "px]";
    }
  }

  public JPanel addChart(Chart chart) {
    Component component = getChartAsComponent(chart);

    return addPanel(component);
  }

  public JPanel addChartAt(Chart chart, int nlin, int ncol) {
    Component component;

    if (chart.getCanvas() instanceof OffscreenCanvas) {
      component = getChartScreenshotAsComponent(chart);
    } else
      component = getChartAsComponent(chart);
    return addPanelAt(component, nlin, ncol);
  }

  public JPanel getChartScreenshotAsComponent(Chart chart) {
    try {
      chart.screenshot();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    AWTRenderer3d renderer = (AWTRenderer3d) ((INativeCanvas) chart.getCanvas()).getRenderer();
    BufferedImage i = renderer.getLastScreenshotImage();
    JPanel component = new ImagePanel(i);
    return component;
  }

  public Component getChartAsComponent(Chart chart) {
    return (Component) chart.getCanvas();
  }

  public JPanel addPanel(java.awt.Component panel) {
    if (vertical) {
      int ncol = 0;
      int nlin = nComponent++;
      return addPanelAt(panel, nlin, ncol);
    } else {
      int nlin = 0;
      int ncol = nComponent++;
      return addPanelAt(panel, nlin, ncol);
    }
  }

  public JPanel addPanelAt(java.awt.Component panel, int nlin, int ncol) {
    JPanel chartPanel = new JPanel(new BorderLayout());
    Border b = BorderFactory.createLineBorder(Color.black);
    chartPanel.setBorder(b);
    chartPanel.add(panel, BorderLayout.CENTER);
    add(chartPanel, "cell " + ncol + " " + nlin + ", grow");
    return chartPanel;
  }

  public JFrame ui() {
    JScrollPane pane = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    JPanel parent = new JPanel();
    parent.add(pane);
    return MultiChartPanel.frame(parent);
  }


  public JFrame frame() {
    return frame(this);
  }

  public static JFrame frame(JPanel panel) {
    JFrame frame = new JFrame();
    windowExitListener(frame);
    frame.add(panel);
    frame.pack();
    frame.show();
    frame.setVisible(true);
    return frame;
  }



  public static void windowExitListener(final JFrame frame) {
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        frame.dispose();
        System.exit(0);
      }
    });
  }
}
