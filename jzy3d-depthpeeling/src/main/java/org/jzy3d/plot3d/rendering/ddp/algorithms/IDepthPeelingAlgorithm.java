package org.jzy3d.plot3d.rendering.ddp.algorithms;

import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.IGLRenderer;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * A {@link IDepthPeelingAlgorithm} has a GLEventListener-like interface to act as a delegate for a
 * depth peeling renderer.
 * 
 * @author Martin Pernollet
 */
public interface IDepthPeelingAlgorithm {
  public void init(IPainter painter, GL2 gl, int width, int height);

  public void display(IPainter painter, GL2 gl, GLU glu);

  public void reshape(IPainter painter, GL2 gl, int width, int height);

  public void dispose(IPainter painter, GL2 gl);

  public IGLRenderer getTasksToRender();

  public void setTasksToRender(IGLRenderer tasksToRender);
}
