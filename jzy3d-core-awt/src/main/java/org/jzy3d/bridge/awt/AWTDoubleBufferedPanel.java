package org.jzy3d.bridge.awt;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import org.jzy3d.bridge.BufferedPanel;


public abstract class AWTDoubleBufferedPanel extends Panel implements BufferedPanel {

  @Override
  public abstract void draw(Graphics g);

  /**********************************************************************/

  @Override
  public void paint(Graphics g) {
    if (mustInit())
      initBuffer();
    if (buffer != null) {
      super.paint(buffer);
      draw(buffer);
      g.drawImage(offscreen, 0, 0, this);
    }
  }

  @Override
  public void update(Graphics g) {
    paint(g);
  }

  /**********************************************************************/

  private void initBuffer() {
    Dimension dim = getSize();
    if (dim != null) {
      offscreen = createImage(dim.width, dim.height);
      buffer = offscreen.getGraphics();
    }
  }

  private boolean mustInit() {
    // case of non existing offscreen image
    if (offscreen == null)
      return true;
    // case of image size change
    if (offscreen.getHeight(null) != getSize().height
        || offscreen.getWidth(null) != getSize().width)
      return true;
    return false;
  }

  /**********************************************************************/

  private Image offscreen = null;
  private Graphics buffer = null;

  private static final long serialVersionUID = 6735935224474752355L;

}
