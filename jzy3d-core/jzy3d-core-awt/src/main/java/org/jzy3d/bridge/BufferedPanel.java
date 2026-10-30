package org.jzy3d.bridge;

import java.awt.Graphics;

/**
 * A {@link BufferedPanel} provides a common interface for AWT or Swing, and Simple or Double
 * buffered panels.
 */
public interface BufferedPanel {
  /** The actual draw method that must be defined by a concrete BufferedPanel. */
  public void draw(Graphics g);

  /** Supposed to be provided by the implementing abstract class. */
  public Graphics getGraphics();

  /** Supposed to be provided by the implementing abstract class. */
  public int getWidth();

  /** Supposed to be provided by the implementing abstract class. */
  public int getHeight();
}
