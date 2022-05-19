package org.jzy3d.debug;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.painters.AWTFont;
import org.jzy3d.plot2d.rendering.AWTGraphicsUtils;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.View2DLayout;

public class View2DLayout_Debug implements AWTRenderer2d{

  View view;
  View2DLayout layout;
  AxisLayout axisLayout;
  
  public View2DLayout_Debug(View view) {
    this.view = view;
    this.layout = view.get2DLayout();
    this.axisLayout = view.getAxis().getLayout();
  }



  @Override
  public void paint(Graphics g, int canvasWidth, int canvasHeight) {
    Graphics2D g2d = (Graphics2D) g;
    
    g2d.setColor(AWTColor.toAWT(Color.BLUE));
    g2d.setFont(AWTFont.toAWT(axisLayout.getFont()));
    
    // Cancel the HiDPI scaling consideration to draw debug stuff properly
    if(view.getCanvas().isNative())
      g2d.scale(1/view.getPixelScale().x, 1/view.getPixelScale().y);

    AWTGraphicsUtils.configureRenderingHints(g2d);

    int height = view.getCanvas().getRendererHeight();
    int width = view.getCanvas().getRendererWidth();
    int txtSize = axisLayout.getFont().getHeight();
    
    int lineHeight = txtSize + 8;
    
    int x = 0;
    int y = 0;
    
    
    FontMetrics fm = g.getFontMetrics();
    
    // ------------------------
    // Horizontal lines

    // under axis label
    y = height-(int)layout.getMarginBottom();
    g2d.drawLine(0, y, width, y);
    
    x = (int)layout.getMarginLeft();
   
    String info = "Axis Label Bottom";
    g2d.drawString(info, x, y);
    int shift = fm.stringWidth(info);


    // up to axis label
    y = height-(int)(layout.getMarginBottom()+txtSize);
    g2d.drawLine(0, y, width, y);

    info = "Axis Label Top";
    g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);

    
    
    // down to tick label
    y = height-(int)(layout.getMarginBottom()+txtSize+layout.getxAxisLabelsDistance());
    g2d.drawLine(0, y, width, y);

    info = "Tick Label Bottom";
    g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);

    
    // up to tick label
    y = height-(int)(layout.getMarginBottom()+txtSize+layout.getxAxisLabelsDistance()+txtSize);
    g2d.drawLine(0, y, width, y);
    
    info = "Tick Label Top";
    g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);


    // up to tick label margin
    y = height-(int)(layout.getMarginBottom()+txtSize+layout.getxAxisLabelsDistance()+txtSize+layout.getxTickLabelsDistance());
    g2d.drawLine(0, y, width, y);

    info = "Chart border";
    g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);

    
    // Horizontal top margin
    y = (int)layout.getMarginTop();
    g2d.drawLine(0, y, width, y);


    
    
    // ------------------------
    // Vertical lines

    // left to axis label
    x = (int)layout.getMarginLeft();
    g2d.drawLine(x, 0, x, height);

    g2d.drawString("Axis Label Left Side / Left Margin (" + x + ")", x, lineHeight*1);

    // right to axis label
    x = (int)(layout.getMarginLeft()+view.get2DProcessing().getAxisTextHorizontal());
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
    
    g2d.drawString("Chart border (" + x + ")", x, lineHeight*5);


    x = (int)(view.getCamera().getLastViewPort().getWidth()-layout.getMarginRight());
    
    g2d.drawLine(x, 0, x, height);
    
    g2d.drawString("Margin (" + x + ")", x, lineHeight*6);

  }

}
