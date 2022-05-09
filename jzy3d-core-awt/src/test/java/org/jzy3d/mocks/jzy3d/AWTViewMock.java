package org.jzy3d.mocks.jzy3d;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTView;

public class AWTViewMock extends AWTView {

  public AWTViewMock(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    super(factory, scene, canvas, quality);
  }
  
  protected int counter_shoot = 0;
  protected int counter_initInstance = 0;
  
  @Override
  public void initInstance(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    super.initInstance(factory, scene, canvas, quality);
    
    counter_initInstance+=1;
  }
  
  @Override
  public void shoot() {
    super.shoot();
    
    counter_shoot++;
  }

  public int getCounter_shoot() {
    return counter_shoot;
  }

  public void resetCounter_shoot() {
    this.counter_shoot = 0;
  }

  public int getCounter_initInstance() {
    return counter_initInstance;
  }

  //public void resetCounter_initInstance() {
  //  this.counter_initInstance = 0;
 // }
  
  

  /*
   * verify(chart.getView(), atLeast(1)).initInstance(factory, chart.getScene(), canvas,
        chart.getQuality());
    verify(chart.getView(), atLeast(1)).shoot();

    // undesired
    verify(chart.getView(), atLeast(1)).shoot(); // VIEW IS CALLED 2 OR 3 TIMES !!!!!!!!!!!!!!!!!!!!!!

    // Then camera was called at least once
    verify(chart.getView().getCamera(), atLeast(1)).shoot(chart.getPainter(),
        chart.getView().getCameraMode());
   * 
   */
}
