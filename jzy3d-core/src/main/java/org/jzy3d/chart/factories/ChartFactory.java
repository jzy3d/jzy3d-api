package org.jzy3d.chart.factories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.chart.ChartView;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadControllerWithTime;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IBoundingPolicy;
import org.jzy3d.plot2d.primitives.LineSerie2d;
import org.jzy3d.plot2d.primitives.LineSerie2dSplitted;
import org.jzy3d.plot2d.primitives.ScatterPointSerie2d;
import org.jzy3d.plot2d.primitives.ScatterSerie2d;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.primitives.axis.IAxis;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.ordering.BarycentreOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;

/**
 * This {@link IChartFactory} returns non-displayable charts.
 * 
 * @see {@link AWTChartFactory} for a working implementation
 */
public class ChartFactory implements IChartFactory {
  public static String SCREENSHOT_FOLDER = "./data/screenshots/";

  static Logger logger = LogManager.getLogger(ChartFactory.class);

  IPainterFactory painterFactory;

  public ChartFactory() {
    this(null);
  }

  public ChartFactory(IPainterFactory painterFactory) {
    setPainterFactory(painterFactory);
  }

  @Override
  public IPainterFactory getPainterFactory() {
    return painterFactory;
  }

  @Override
  public void setPainterFactory(IPainterFactory painterFactory) {
    this.painterFactory = painterFactory;

    if (painterFactory != null)
      this.painterFactory.setChartFactory(this);
  }



  @Override
  public Chart newChart() {
    return newChart(Quality.Advanced());
  }

  @Override
  public Chart newChart(Quality quality) {
    return newChart(getFactory(), quality);
  }

  @Override
  public Chart newChart(IChartFactory factory, Quality quality) {
    return new Chart(factory, quality);
  }

  @Override
  public ChartScene newScene(boolean sort) {
    return new ChartScene(sort, getFactory());
  }

  @Override
  public Graph newGraph(Scene scene, AbstractOrderingStrategy strategy, boolean sort) {
    return new Graph(scene, strategy, sort);
  }

  @Override
  public View newView(Scene scene, ICanvas canvas, Quality quality) {
    return newView(getFactory(), scene, canvas, quality);
  }

  @Override
  public View newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    return new ChartView(factory, scene, canvas, quality);
  }

  @Override
  public Camera newCamera(Coord3d center) {
    return new Camera(center);
  }

  @Override
  public IAxis newAxe(BoundingBox3d box, View view) {
    AxisBox axe = new AxisBox(box);
    axe.setView(view);
    return axe;
  }

  @Override
  public AbstractOrderingStrategy newOrderingStrategy() {
    return new BarycentreOrderingStrategy();
  }

  @Override
  public IBoundingPolicy newBoundingPolicy() {
    return null;
  }

  @Override
  public Serie2d newSerie(String name, Serie2d.Type type) {
    if (Serie2d.Type.LINE.equals(type))
      return new LineSerie2d(name);
    else if (Serie2d.Type.LINE_ON_OFF.equals(type))
      return new LineSerie2dSplitted(name);
    else if (Serie2d.Type.SCATTER.equals(type))
      return new ScatterSerie2d(name);
    else if (Serie2d.Type.SCATTER_POINTS.equals(type))
      return new ScatterPointSerie2d(name);
    else
      throw new IllegalArgumentException("Unsupported serie type " + type);
  }

  @Override
  public CameraThreadController newCameraThreadController(Chart chart) {
    //return new CameraThreadController(chart);
    return new CameraThreadControllerWithTime(chart, 10);
  }

  /*
   * @Override public Renderer newRenderer(View view) { return newRenderer(view, false, false); }
   */


  /* UTILS */

  @Override
  public IChartFactory getFactory() {
    return this;
  }


}
