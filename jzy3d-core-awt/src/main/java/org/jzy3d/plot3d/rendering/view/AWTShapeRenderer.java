package org.jzy3d.plot3d.rendering.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.rendering.legends.overlay.LegendLayout;
import org.jzy3d.plot3d.rendering.legends.overlay.LegendLayout.Corner;

/** An AWT post renderer able to render an image on top of a chart according to a legend */
public class AWTShapeRenderer extends AbstractAWTRenderer2d implements AWTRenderer2d {
  protected Shape shape;
  protected LegendLayout layout = new LegendLayout();
  protected Color color;

  protected int shapeWidth;
  protected int shapeHeight;

  public AWTShapeRenderer(Shape shape) {
    this.shape = shape;
    
    this.shapeWidth = (int)shape.getBounds2D().getWidth();
    this.shapeHeight = (int)shape.getBounds2D().getHeight();
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
      x = canvasWidth - shapeWidth/2 - layout.getMargin().getRight();
      y = layout.getMargin().getTop();
    } else if (Corner.BOTTOM_LEFT.equals(layout.getCorner())) {
      x = layout.getMargin().getLeft();
      y = canvasHeight - shapeHeight/2 - layout.getMargin().getBottom();
    } else if (Corner.BOTTOM_RIGHT.equals(layout.getCorner())) {
      x = canvasWidth - shapeWidth/2 - layout.getMargin().getRight();
      y = canvasHeight - shapeHeight/2 - layout.getMargin().getBottom();
    }

    
    g2d.setColor(AWTColor.toAWT(color));
    g2d.translate(x, y);
    g2d.draw(shape);
  }

  public LegendLayout getLayout() {
    return layout;
  }

  public void setLayout(LegendLayout layout) {
    this.layout = layout;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }
}
