package org.jzy3d.bridge.swing;

import java.awt.Graphics;
import javax.swing.JPanel;
import org.jzy3d.bridge.BufferedPanel;


public abstract class SwingSimpleBufferedPanel extends JPanel implements BufferedPanel {

  @Override
  public abstract void draw(Graphics g);

  /**********************************************************************/

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  @Override
  public void update(Graphics g) {
    paintComponent(g);
  }

  private static final long serialVersionUID = -7867814672865717594L;
}
