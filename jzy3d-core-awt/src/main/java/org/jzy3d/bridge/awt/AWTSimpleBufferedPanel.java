package org.jzy3d.bridge.awt;

import java.awt.Graphics;
import java.awt.Panel;
import org.jzy3d.bridge.BufferedPanel;


public abstract class AWTSimpleBufferedPanel extends Panel implements BufferedPanel {

  @Override
  public abstract void draw(Graphics g);

  /**********************************************************************/

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    draw(g);
  }

  @Override
  public void update(Graphics g) {
    paint(g);
  }

  private static final long serialVersionUID = 1820167756982938374L;
}
