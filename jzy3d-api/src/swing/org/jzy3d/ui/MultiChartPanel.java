package org.jzy3d.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;

import org.jzy3d.chart.Chart;

public class MultiChartPanel extends JPanel {
    private static final long serialVersionUID = 7519209038396190502L;

    // protected CameraThreadController currentThreadController;

    protected JTextField tf;
    protected JTextArea textArea;
    protected JScrollPane textPane;

    public static String WT = "awt";

    private int nComponent = 0;

    public MultiChartPanel(List<Chart> charts) throws IOException {
        LookAndFeel.apply();

        // Main layout
        String lines = "[300px]";
        String columns = "[500px,grow]";
        setLayout(new MigLayout("", columns, lines));
        // setBounds(0, 0, 700, 600);
        // demoList.setMinimumSize(new Dimension(200,200));
        // textPane.setMinimumSize(new Dimension(700, 50));
        for (Chart c : charts) {
            addChart(c);
        }
    }

    public JPanel addChart(Chart chart) {
        return addPanel((java.awt.Component) chart.getCanvas());
    }

    public JPanel addPanel(java.awt.Component panel) {
        JPanel chartPanel = new JPanel(new BorderLayout());
        Border b = BorderFactory.createLineBorder(Color.black);
        chartPanel.setBorder(b);
        chartPanel.add(panel, BorderLayout.CENTER);
        add(chartPanel, "cell 0 " + nComponent++ + ", grow");
        return chartPanel;
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
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });
    }
}
