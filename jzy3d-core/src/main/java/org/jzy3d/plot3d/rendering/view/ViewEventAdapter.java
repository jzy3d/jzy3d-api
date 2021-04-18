package org.jzy3d.plot3d.rendering.view;

import org.jzy3d.events.IViewEventListener;
import org.jzy3d.events.ViewIsVerticalEvent;

public class ViewEventAdapter implements IViewEventListener{
  @Override
  public void viewFirstRender() {
  }

  @Override
  public void viewVerticalReached(ViewIsVerticalEvent e) {
  }

  @Override
  public void viewVerticalLeft(ViewIsVerticalEvent e) {
  }
}
