package org.jzy3d.chart.controllers.mouse.picking;

public interface IMousePickingController {

  /****************/

  public abstract PickingSupport getPickingSupport();

  public abstract void setPickingSupport(PickingSupport picking);

}
