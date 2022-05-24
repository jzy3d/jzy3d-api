package org.jzy3d.chart2d;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;

public class Chart2dFactory extends AWTChartFactory {
  @Override
  public IChartFactory getFactory() {
    return this;
  }

  @Override
  public Chart2d newChart(IChartFactory factory, Quality quality) {
    return new Chart2d(factory, quality);
  }

  @Override
  public Chart2d newChart(Quality quality) {
    return new Chart2d(getFactory(), quality);
  }

  @Override
  public AxisBox2d newAxe(BoundingBox3d box, View view) {
    AxisBox2d axe = new AxisBox2d(box);
    // axe.setTextRenderer(new TextBitmapRenderer());
    return axe;
  }

  @Override
  public View2d newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    return new View2d(factory, scene, canvas, quality);
  }

  /* */

  public static Chart2d chart() {
    return chart(Quality.Intermediate());
  }

  public static Chart2d chart(Quality quality) {
    return f.newChart(quality);
  }

  public static Chart2d chart(String toolkit) {
    return f.newChart(Chart.DEFAULT_QUALITY);
  }

  static Chart2dFactory f = new Chart2dFactory();
}
