package org.jzy3d.plot3d.rendering.ddp.algorithms;

import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.IGLRenderer;

/**
 * A {@link IDepthPeelingAlgorithm} has a GLEventListener-like interface to act as a delegate for a
 * depth peeling renderer.
 * 
 * @author Martin Pernollet
 */
public interface IDepthPeelingAlgorithm {
  public void init(IPainter painter, int width, int height);

  public void display(IPainter painter);

  public void reshape(IPainter painter, int width, int height);

  public void dispose(IPainter painter);

  public IGLRenderer getTasksToRender();

  public void setTasksToRender(IGLRenderer tasksToRender);
  
  public void setBackground(float[] color);
  
  public float[] getBackground();
  
  public void setOpacity(float opacity);
  
  public float getOpacity();

}
