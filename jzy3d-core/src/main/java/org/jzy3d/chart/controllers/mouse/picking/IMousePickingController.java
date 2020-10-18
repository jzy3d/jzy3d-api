package org.jzy3d.chart.controllers.mouse.picking;

import org.jzy3d.picking.PickingSupport;

public interface IMousePickingController {

    /****************/

    public abstract PickingSupport getPickingSupport();

    public abstract void setPickingSupport(PickingSupport picking);

}