package org.jzy3d.plot3d.rendering.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.jzy3d.plot3d.rendering.legends.overlay.LegendLayout;
import org.jzy3d.plot3d.rendering.legends.overlay.LegendLayout.Corner;

/** An AWT post renderer able to render an image on top of a chart according to a legend */
public class AWTImageRenderer extends AbstractAWTRenderer2d implements AWTRenderer2d {
  protected BufferedImage image;
  protected LegendLayout layout = new LegendLayout();

  protected int imageWidth;
  protected int imageHeight;

  public AWTImageRenderer(BufferedImage image) {
    this.image = image;
    this.imageWidth = image.getWidth(null);
    this.imageHeight = image.getHeight(null);
  }

  @Override
  public void paint(Graphics g, int canvasWidth, int canvasHeight) {
    Graphics2D g2d = (Graphics2D) g;
    
    //System.out.println("AWTImageRenderer : scale : " + AWTGraphicsUtils.getPixelScale(g2d)); 
    
    // Ensure native overlay will place image at the appropriate location
    // Since native and emulgl deal differently with overlay when hiDPI
    if(view!=null && view.getCanvas().isNative()) {
      canvasHeight /= view.getPixelScale().y;
      canvasWidth /= view.getPixelScale().x;
    }

    // Image position
    int x = 0;
    int y = 0;

    if (Corner.TOP_LEFT.equals(layout.getCorner())) {
      x = layout.getMargin().getLeft();
      y = layout.getMargin().getTop();
    } else if (Corner.TOP_RIGHT.equals(layout.getCorner())) {
      x = canvasWidth - imageWidth - layout.getMargin().getRight();
      y = layout.getMargin().getTop();
    } else if (Corner.BOTTOM_LEFT.equals(layout.getCorner())) {
      x = layout.getMargin().getLeft();
      y = canvasHeight - imageHeight - layout.getMargin().getBottom();
    } else if (Corner.BOTTOM_RIGHT.equals(layout.getCorner())) {
      x = canvasWidth - imageWidth - layout.getMargin().getRight();
      y = canvasHeight - imageHeight - layout.getMargin().getBottom();
    }

    g2d.drawImage(image, x, y, null);
  }

  public LegendLayout getLayout() {
    return layout;
  }

  public void setLayout(LegendLayout layout) {
    this.layout = layout;
  }
}
