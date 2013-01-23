package org.jzy3d.chart.graphs;

import java.util.List;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.MousePickingController;
import org.jzy3d.chart.factories.ChartComponentFactory;
import org.jzy3d.picking.IObjectPickedListener;
import org.jzy3d.picking.PickingSupport;

public class GraphChartComponentFactory extends ChartComponentFactory{
    @Override
    public ICameraMouseController newMouseController(Chart chart){
        ICameraMouseController mouse = null;
        if(!chart.getWindowingToolkit().equals("newt"))
            mouse = newAWTMouseController(chart);
        else
            throw new IllegalArgumentException("newt not supported");
        return mouse;
    }
    
    public ICameraMouseController newAWTMouseController(final Chart chart){
        MousePickingController<String,String> mouse = new MousePickingController<String,String>(chart);
        mouse.getPickingSupport().addObjectPickedListener(new IObjectPickedListener() {
            @Override
            public void objectPicked(List<? extends Object> vertices, PickingSupport picking) {
                for(Object vertex: vertices){
                    System.out.println("picked: " + vertex);
                    //dGraph.setVertexHighlighted((String)vertex, true);
                }
                chart.render();
            }
        });
        return mouse;
    }
}
