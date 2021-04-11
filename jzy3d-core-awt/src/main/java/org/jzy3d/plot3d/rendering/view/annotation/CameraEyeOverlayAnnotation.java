package org.jzy3d.plot3d.rendering.view.annotation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.View;

/**
 * Draws debug information concerning camera and view
 * 
 * @author Martin
 *
 */
public class CameraEyeOverlayAnnotation implements AWTRenderer2d {
  public CameraEyeOverlayAnnotation(View view) {
    this.view = view;
  }

  @Override
  public void paint(Graphics g, int canvasWidth, int canvasHeight) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(java.awt.Color.BLACK);
    g2d.drawString("eye=" + view.getCamera().getEye(), 20, 20);
    g2d.drawString("scaling=" + view.getLastViewScaling(), 20, 50);
  }

  protected View view;
}
