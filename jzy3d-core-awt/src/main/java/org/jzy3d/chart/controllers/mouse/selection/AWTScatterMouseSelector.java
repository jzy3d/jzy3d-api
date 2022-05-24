package org.jzy3d.chart.controllers.mouse.selection;

import java.awt.Graphics2D;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.plot3d.primitives.selectable.SelectableScatter;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;


public class AWTScatterMouseSelector extends AWTAbstractMouseSelector {
  protected SelectableScatter scatter;
  protected int width;
  protected int height;

  
  public AWTScatterMouseSelector(SelectableScatter scatter, Chart chart) {
    super();
    this.chart = chart;
    this.canvas =  chart.getCanvas();
    this.scatter = scatter;
  }

  /** Make projection and match points belonging to selection. */
  @Override
  protected void processSelection(Scene scene, View view, int width, int height) {
    view.project();
    Coord3d[] projection = scatter.getProjection();
    
    Coord2d pixScale = getPixelScale();
    IntegerCoord2d from = in.mul(pixScale);
    IntegerCoord2d to = out.mul(pixScale);
    
    for (int i = 0; i < projection.length; i++)
      if (matchRectangleSelection(from, to, projection[i], width, height))
        scatter.setHighlighted(i, true);
  }

  protected Coord2d getPixelScale() {
    return canvas.getPixelScale();
  }

  @Override
  protected void drawSelection(Graphics2D g2d, int width, int height) {
    this.width = width;
    this.height = height;

    Coord2d pixScale = getPixelScale();
    IntegerCoord2d from  =in.mul(pixScale);
    IntegerCoord2d to = out.mul(pixScale);

    if (dragging)
      drawRectangle(g2d, from, to);
  }

  @Override
  public void clearLastSelection() {}

}
