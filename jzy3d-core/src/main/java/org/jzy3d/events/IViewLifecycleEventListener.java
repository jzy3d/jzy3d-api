package org.jzy3d.events;

public interface IViewLifecycleEventListener {
  public void viewHasInit(ViewLifecycleEvent e);

  public void viewWillRender(ViewLifecycleEvent e);
}
