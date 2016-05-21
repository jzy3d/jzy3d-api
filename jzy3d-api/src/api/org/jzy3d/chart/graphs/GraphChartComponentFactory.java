package org.jzy3d.chart.graphs;

import java.util.List;

import org.apache.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.NewtMousePickingController;
import org.jzy3d.chart.factories.ChartComponentFactory;
import org.jzy3d.picking.IObjectPickedListener;
import org.jzy3d.picking.PickingSupport;

public class GraphChartComponentFactory extends ChartComponentFactory{
    static Logger logger = Logger.getLogger(GraphChartComponentFactory.class);
    
    @Override
    public ICameraMouseController newMouseCameraController(Chart chart){
        ICameraMouseController mouse = null;
        if(!chart.getWindowingToolkit().equals("newt"))
            mouse = newAWTMouseController(chart);
        else
            throw new IllegalArgumentException("newt not supported");
        return mouse;
    }
    
    public ICameraMouseController newAWTMouseController(final Chart chart){
        NewtMousePickingController mouse = new NewtMousePickingController(chart);
        mouse.getPickingSupport().addObjectPickedListener(new IObjectPickedListener() {
            @Override
            public void objectPicked(List<? extends Object> vertices, PickingSupport picking) {
                for(Object vertex: vertices){
                    logger.info("picked: " + vertex);
                    //dGraph.setVertexHighlighted((String)vertex, true);
                }
                chart.render();
            }
        });
        return mouse;
    }
}
