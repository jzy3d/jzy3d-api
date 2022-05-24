package org.jzy3d.chart.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.jzy3d.chart.Chart;
import org.jzy3d.events.ControllerEvent;
import org.jzy3d.events.ControllerEventListener;

public class AbstractController {
  protected List<Chart> targets;
  protected Vector<ControllerEventListener> controllerListeners =
      new Vector<ControllerEventListener>(1);

  public AbstractController() {}

  public AbstractController(Chart chart) {
    register(chart);
  }

  public void register(Chart chart) {
    if (targets == null)
      targets = new ArrayList<Chart>(1);
    targets.add(chart);
  }

  public void unregister(Chart chart) {
    if (targets != null) {
      targets.remove(chart);
    }
  }

  public Chart getChart() {
    return targets.get(0);
  }

  public List<Chart> getCharts() {
    return targets;
  }

  public void dispose() {
    targets.clear();
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
