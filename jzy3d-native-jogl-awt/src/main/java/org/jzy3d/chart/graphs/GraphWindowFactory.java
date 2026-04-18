package org.jzy3d.chart.graphs;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.AWTMousePickingController;
import org.jzy3d.chart.controllers.mouse.picking.IObjectPickedListener;
import org.jzy3d.chart.controllers.mouse.picking.PickingSupport;
import org.jzy3d.chart.factories.AWTPainterFactory;

public class GraphWindowFactory extends AWTPainterFactory {
  static Logger logger = LogManager.getLogger(GraphChartFactory.class);

  @Override
  public ICameraMouseController newMouseCameraController(Chart chart) {
    AWTMousePickingController mouse = new AWTMousePickingController(chart);
    mouse.getPickingSupport().addObjectPickedListener(new IObjectPickedListener() {
      @Override
      public void objectPicked(List<? extends Object> vertices, PickingSupport picking) {
        for (Object vertex : vertices) {
          logger.info("picked: " + vertex);
          // dGraph.setVertexHighlighted((String)vertex, true);
        }
        chart.render();
      }
    });
    return mouse;
  }

}
