package org.jzy3d.events;

public interface IViewEventListener {
  public void viewFirstRenderStarts();

  public void viewVerticalReached(ViewIsVerticalEvent e);

  public void viewVerticalLeft(ViewIsVerticalEvent e);
}
