package org.jzy3d.chart.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.jzy3d.chart.Chart;
import org.jzy3d.events.ControllerEvent;
import org.jzy3d.events.ControllerEventListener;
import org.jzy3d.maths.Lists;

public class AbstractController {
  protected Chart target;
  protected List<ControllerEventListener> controllerListeners = new ArrayList<ControllerEventListener>(1);

  public AbstractController() {}

  public AbstractController(Chart chart) {
    register(chart);
  }

  public void register(Chart chart) {
    target = chart;
  }

  public void unregister(Chart chart) {
    target = null;
  }

  public Chart getChart() {
	return target;
  }

  @Deprecated
  public List<Chart> getCharts() {
    return Lists.of(target);
  }

  public void dispose() {
    unregister(target);
    controllerListeners.clear();
  }

  public void addControllerEventListener(ControllerEventListener listener) {
    controllerListeners.add(listener);
  }

  public void removeControllerEventListener(ControllerEventListener listener) {
    controllerListeners.remove(listener);
  }

  protected void fireControllerEvent(ControllerType type, Object value) {
    ControllerEvent e = new ControllerEvent(this, type, value);
    for (ControllerEventListener listener : controllerListeners)
      listener.controllerEventFired(e);
  }
}
