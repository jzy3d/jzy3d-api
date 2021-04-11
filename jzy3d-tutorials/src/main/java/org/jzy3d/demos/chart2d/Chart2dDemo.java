package org.jzy3d.demos.chart2d;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart2d.Chart2d;
import org.jzy3d.colors.Color;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot3d.primitives.ConcurrentLineStrip;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.providers.PitchTickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.PitchTickRenderer;
import org.jzy3d.ui.LookAndFeel;
import net.miginfocom.swing.MigLayout;

/**
 * Showing a pair of 2d charts to represent pitch and amplitude variation of an audio signal.
 * 
 * Noticed problems on chart resize. Suspect "wrong stuffs" around miglayout or jogl.
 * 
 * FIXME : use ChartGroup to build interface. Miglayout/JOGL interaction causes problem when
 * downsizing windows
 * 
 * @author Martin Pernollet
 */
public class Chart2dDemo {
  public static float duration = 60f;
  /** milisecond distance between two generated samples */
  public static int interval = 50;
  public static int maxfreq = 880;
  public static int nOctave = 5;

  public static void main(String[] args) throws Exception {
    PitchAndAmplitudeCharts log = new PitchAndAmplitudeCharts(duration, maxfreq, nOctave);
    new TimeChartWindow(log.getCharts());

    generateSamplesInTime(log);
    // generateSamples(log, 500000);
  }

  public static void generateSamples(PitchAndAmplitudeCharts log, int n)
      throws InterruptedException {
    System.out.println("will generate " + n + " samples");

    for (int i = 0; i < n; i++) {
      // Random audio info
      double pitch = Math.random() * maxfreq;
      double ampli = Math.random();

      // Add to time series
      log.seriePitch.add(time(n, i), pitch);
      log.serieAmpli.add(time(n, i), ampli);
    }
  }

  public static double time(int n, int i) {
    return ((double) i / n) * duration;
  }

  public static void generateSamplesInTime(PitchAndAmplitudeCharts log)
      throws InterruptedException {
    System.out.println("will generate approx. " + duration * 1000 / interval + " samples");

    start();

    while (elapsed() < duration) {
      // Random audio info
      double pitch = Math.random() * maxfreq;
      double ampli = Math.random();

      // Add to time series
      log.seriePitch.add(elapsed(), pitch);
      log.serieAmpli.add(elapsed(), ampli);

      // Wait a bit
      Thread.sleep(interval);
    }
  }

  /** Hold 2 charts, 2 time series, and 2 drawable lines */
  public static class PitchAndAmplitudeCharts {
    public Chart2d pitchChart;
    public Chart2d ampliChart;
    public Serie2d seriePitch;
    public Serie2d serieAmpli;
    public ConcurrentLineStrip pitchLineStrip;
    public ConcurrentLineStrip amplitudeLineStrip;

    public PitchAndAmplitudeCharts(float timeMax, int freqMax, int nOctave) {
      pitchChart = new Chart2d();
      pitchChart.asTimeChart(timeMax, 0, freqMax, "Time", "Frequency");

      IAxisLayout axe = pitchChart.getAxisLayout();
      axe.setYTickProvider(new PitchTickProvider(nOctave));
      axe.setYTickRenderer(new PitchTickRenderer());

      seriePitch = pitchChart.getSerie("frequency", Serie2d.Type.LINE);
      seriePitch.setColor(Color.BLUE);
      pitchLineStrip = (ConcurrentLineStrip) seriePitch.getDrawable();

      ampliChart = new Chart2d();
      ampliChart.asTimeChart(timeMax, 0, 1.1f, "Time", "Amplitude");
      serieAmpli = ampliChart.getSerie("amplitude", Serie2d.Type.LINE);
      serieAmpli.setColor(Color.RED);
      amplitudeLineStrip = (ConcurrentLineStrip) serieAmpli.getDrawable();
    }

    public List<Chart> getCharts() {
      List<Chart> charts = new ArrayList<Chart>();
      charts.add(pitchChart);
      charts.add(ampliChart);
      return charts;
    }
  }

  /** A frame to show a list of charts */
  public static class TimeChartWindow extends JFrame {
    private static final long serialVersionUID = 7519209038396190502L;

    public TimeChartWindow(List<Chart> charts) throws IOException {
      LookAndFeel.apply();
      String lines = "[300px]";
      String columns = "[500px,grow]";
      setLayout(new MigLayout("", columns, lines));
      int k = 0;
      for (Chart c : charts) {
        addChart(c, k++);
      }
      windowExitListener();
      this.pack();
      show();
      setVisible(true);
    }

    public void addChart(Chart chart, int id) {
      Component canvas = (java.awt.Component) chart.getCanvas();

      JPanel chartPanel = new JPanel(new BorderLayout());
      /*
       * chartPanel.setMaximumSize(null); chartPanel.setMinimumSize(null);
       * canvas.setMinimumSize(null); canvas.setMaximumSize(null);
       */

      Border b = BorderFactory.createLineBorder(java.awt.Color.black);
      chartPanel.setBorder(b);
      chartPanel.add(canvas, BorderLayout.CENTER);
      add(chartPanel, "cell 0 " + id + ", grow");
    }

    public void windowExitListener() {
      addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          TimeChartWindow.this.dispose();
          System.exit(0);
        }
      });
    }
  }

  /** Simple timer */
  protected static long start;

  public static void start() {
    start = System.nanoTime();
  }

  public static double elapsed() {
    return (System.nanoTime() - start) / 1000000000.0;
  }
}
