package org.jzy3d.debugGL.tracers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart2d.Chart2d;
import org.jzy3d.chart2d.Chart2dFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Timer;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot2d.primitives.Serie2d.Type;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ElapsedTimeTickRenderer;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

/**
 * Display parameters of jzy3d components.
 * 
 * Unfortunately in native because Chart2D are built with a NATIVE FACTORY.
 * 
 * 
 * @author martin
 *
 */
public class DebugGLChart2d {
  // watched
  Chart watchedChart;

  // WatcherFactory wf = new WatcherFactory();
  Chart2d debugChart = new Chart2dFactory().newChart(Quality.Advanced());
  Timer timer = new Timer();

  public DebugGLChart2d(Chart watchedChart) {
    super();
    this.watchedChart = watchedChart;

    timer.start();
    // debugChart.asTimeChart(120f, 0f, (float)Math.PI*2, "X", "Y");
    IAxisLayout axe = debugChart.getAxisLayout();
    debugChart.getView().setBoundManual(new BoundingBox3d(0, 120f, 0, 1, -1, 1));
    debugChart.getView().setBoundMode(ViewBoundMode.AUTO_FIT);
    axe.setXTickRenderer(new ElapsedTimeTickRenderer());
    axe.setYTickRenderer(new ITickRenderer() {

      @Override
      public String format(double value) {
        return String.format("%f", value);
      }

    });
  }

  public void open(Rectangle rectangle) {
    debugChart.open("GL Debug 2d", rectangle);

    startUpdater();
  }

  protected void startUpdater() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          for (Watcher w : watchers) {
            w.update();
          }
          // System.out.println("upd bounds");
          if (debugChart.getView() != null)
            debugChart.getView().updateBounds();
          try {
            Thread.sleep(20);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

        }
      }
    }).start();
  }

  protected List<Watcher> watchers = new ArrayList<DebugGLChart2d.Watcher>();

  public void watch(String name, Color color, Function<Chart, Float> watched) {
    Watcher w = new Watcher();
    w.watch(name, color, watched);
    watchers.add(w);
  }


  public class Watcher {
    protected Function<Chart, Float> watched;
    protected String name;

    protected Serie2d serie;

    public Watcher() {}

    public void watch(String name, Color color, Function<Chart, Float> watched) {
      this.serie = debugChart.getSerie(name, Type.LINE);
      this.serie.setColor(color);
      this.name = name;
      this.watched = watched;
    }

    public void update() {
      plot(serie, watched.apply(watchedChart));
    }

    protected void plot(Serie2d serie, float value) {
      if (Double.isFinite(value))
        serie.add(timer.elapsed(), value);
    }
  }
}
