package org.jzy3d.chart.factories;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IBoundingPolicy;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot3d.primitives.axis.IAxis;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;

public interface IChartFactory {

  public View newView(Scene scene, ICanvas canvas, Quality quality);

  public View newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality);

  public Camera newCamera(Coord3d center);

  public Chart newChart();

  public Chart newChart(Quality quality);

  public Chart newChart(IChartFactory factory, Quality quality);

  public ChartScene newScene(boolean sort);

  public Graph newGraph(Scene scene, AbstractOrderingStrategy strategy, boolean sort);

  public IAxis newAxe(BoundingBox3d box, View view);



  // public Renderer newRenderer(View view);
  // public Renderer newRenderer(View view, boolean traceGL, boolean debugGL);

  public AbstractOrderingStrategy newOrderingStrategy();


  public CameraThreadController newCameraThreadController(Chart chart);


  public Serie2d newSerie(String name, Serie2d.Type type);

  public IBoundingPolicy newBoundingPolicy();

  // *************************************************************** //
  // 
  // SUB-FACTORIES 
  // 
  // *************************************************************** //

  public IPainterFactory getPainterFactory();

  public void setPainterFactory(IPainterFactory painterFactory);

  //public IDrawableFactory getDrawableFactory();

  //public void setDrawableFactory(IDrawableFactory drawableFactory);


  /**
   * usefull to override the current factory to call, especially for FactoryOverrider that must be
   * used as this instead of its wrapped delegate factory
   */
  public IChartFactory getFactory();
}
