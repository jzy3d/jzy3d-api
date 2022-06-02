package org.jzy3d.debug;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.painters.AWTFont;
import org.jzy3d.plot2d.rendering.AWTGraphicsUtils;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.AbstractAWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.View2DLayout;

public class View2DLayout_Debug extends AbstractAWTRenderer2d implements AWTRenderer2d{

  View2DLayout layout;
  AxisLayout axisLayout;
  Color color = Color.BLUE;
  
  public View2DLayout_Debug() {
  }
  
  public View2DLayout_Debug(Color color) {
    this.color = color;
  }



  @Override
  public void setView(AWTView view) {
    super.setView(view);

    this.layout = view.get2DLayout();
    this.axisLayout = view.getAxis().getLayout();
  }


  @Override
  public void paint(Graphics g, int canvasWidth, int canvasHeight) {
    Graphics2D g2d = (Graphics2D) g;
    
    
    // Cancel the HiDPI scaling consideration to draw debug stuff properly
    //
    
    Font font = AWTFont.toAWT(axisLayout.getFont());

    // Native 
    if(view.getCanvas().isNative()) {
      g2d.scale(1/view.getPixelScale().x, 1/view.getPixelScale().y);
    }
    // EmulGL
    else {
      int newSize = (int)(font.getSize() / view.getPixelScale().y);
      font = new Font(font.getName(), font.getStyle(), newSize);
    }

    g2d.setColor(AWTColor.toAWT(color));
    g2d.setFont(font);

    // Text and aliasing settings
    AWTGraphicsUtils.configureRenderingHints(g2d);

    
    // ------------------------
    // Draw debug info

    int height = view.getCanvas().getRendererHeight();
    int width = view.getCanvas().getRendererWidth();
    
    // ------------------------
    // Horizontal lines

    int txtSize = axisLayout.getFont().getHeight();
    
    drawHorizontalLines(g2d, height, width, txtSize);
    
    
    // ------------------------
    // Vertical lines

    int lineHeight = txtSize + 8;

    drawVerticalLines(g2d, height, lineHeight);

  }



  private void drawHorizontalLines(Graphics2D g2d, int height, int width, int txtSize) {
    FontMetrics fm = g2d.getFontMetrics();

    int x;
    int y;
    // under axis label
    y = height-(int)layout.getMargin().getBottom();
    g2d.drawLine(0, y, width, y);
    
    x = (int)layout.getMargin().getLeft();
   
    String info = "Axis Label Bottom";
    g2d.drawString(info, x, y);
    int shift = fm.stringWidth(info);


    // up to axis label
    y = height-(int)(layout.getMargin().getBottom()+txtSize);
    g2d.drawLine(0, y, width, y);

    info = "Axis Label Top";
    g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);
    
    // down to tick label
    y = height-(int)(layout.getMargin().getBottom()+txtSize+layout.getxAxisLabelsDistance());
    g2d.drawLine(0, y, width, y);

    info = "Tick Label Bottom";
    g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);
    
    // up to tick label
    y = height-(int)(layout.getMargin().getBottom()+txtSize+layout.getxAxisLabelsDistance()+txtSize);
    g2d.drawLine(0, y, width, y);
    
    info = "Tick Label Top";
    g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);

    // up to tick label margin
    y = height-(int)(layout.getMargin().getBottom()+txtSize+layout.getxAxisLabelsDistance()+txtSize+layout.getxTickLabelsDistance());
    g2d.drawLine(0, y, width, y);

    info = "Chart bottom border";
    g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);

    // Horizontal top margin
    y = (int)layout.getMargin().getTop();
    g2d.drawLine(0, y, width, y);
    
    info = "Chart top border";
    g2d.drawString(info, shift, y);

  }



  private void drawVerticalLines(Graphics2D g2d, int height, int lineHeight) {
    FontMetrics fm = g2d.getFontMetrics();

    int x;
    // left to axis label
    x = (int)layout.getMargin().getLeft();
    g2d.drawLine(x, 0, x, height);

    g2d.drawString("Axis Label Left Side / Left Margin (" + x + ")", x, lineHeight*1);

    // right to axis label
    x = (int)(layout.getMargin().getLeft()+view.get2DProcessing().getAxisTextHorizontal());
    g2d.drawLine(x, 0, x, height);

    g2d.drawString("Axis Label Right Side (" + x + ")", x, lineHeight*2);

    
    // left to tick label
    x = (int)(x+layout.getyAxisLabelsDistance());
    g2d.drawLine(x, 0, x, height);

    g2d.drawString("Tick Label Left Side ("  + x + ")", x, lineHeight*3);

    
    // right to tick label
    x = (int)(x+view.get2DProcessing().getTickTextHorizontal());
    g2d.drawLine(x, 0, x, height);

    g2d.drawString("Tick Label Right Side (" + x + ")", x, lineHeight*4);
    
    // on chart border
    x = (int)(x+layout.getxTickLabelsDistance());
    g2d.drawLine(x, 0, x, height);
    
    g2d.drawString("Chart left border (" + x + ")", x, lineHeight*5);

    x = (int)(view.getCamera().getLastViewPort().getWidth()-layout.getMargin().getRight());
    
    g2d.drawLine(x, 0, x, height);
    
    String txt = "Chart right border (" + x + ")";
    
    g2d.drawString(txt, x - fm.stringWidth(txt), lineHeight*6);
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }
}
