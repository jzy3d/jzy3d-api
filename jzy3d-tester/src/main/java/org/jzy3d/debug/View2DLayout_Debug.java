package org.jzy3d.debug;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Margin;
import org.jzy3d.painters.AWTFont;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
import org.jzy3d.plot2d.rendering.AWTGraphicsUtils;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.AbstractAWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.View2DLayout;
import org.jzy3d.plot3d.rendering.view.View2DProcessing;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;

public class View2DLayout_Debug extends AbstractAWTRenderer2d implements AWTRenderer2d{

  View2DLayout layout;
  View2DProcessing processing;
  AxisLayout axisLayout;
  ViewAndColorbarsLayout viewportLayout;
  Color color = Color.CYAN;
  Color color2 = Color.MAGENTA;
  
  int interlinePx = 8;
  
  public View2DLayout_Debug() {
  }
  
  public View2DLayout_Debug(Color color) {
    this.color = color;
  }



  @Override
  public void setView(AWTView view) {
    super.setView(view);

    this.layout = view.get2DLayout();
    this.processing = view.get2DProcessing();
    this.axisLayout = view.getAxis().getLayout();
    this.viewportLayout = (ViewAndColorbarsLayout)view.getLayout();
    //((AWTChart)view.getChart()).getV;
  }


  @Override
  public void paint(Graphics g, int canvasWidth, int canvasHeight) {
    Graphics2D g2d = (Graphics2D) g;
    Coord2d pixelScale = view.getPixelScale();
    
    
    configureFontAndHiDPI(g2d, pixelScale);
    AWTGraphicsUtils.configureRenderingHints(g2d); // Text and aliasing settings


    drawViewMargins(g2d, pixelScale);
    drawColorbarMargins(g2d, pixelScale);
  }
  
  /** Cancel the HiDPI scaling consideration to draw debug stuff properly on either EmulGL or Native*/
  protected void configureFontAndHiDPI(Graphics2D g2d, Coord2d pixelScale) {
    Font font = AWTFont.toAWT(axisLayout.getFont());

    // Native 
    if(view.getCanvas().isNative()) {
      g2d.scale(1/pixelScale.x, 1/pixelScale.y);
    }
    // EmulGL
    else {
      int newSize = (int)(font.getSize() / pixelScale.y);
      font = new Font(font.getName(), font.getStyle(), newSize);
    }
    g2d.setFont(font);
  }

  protected void drawColorbarMargins(Graphics2D g2d, Coord2d pixelScale) {
    g2d.setColor(AWTColor.toAWT(color2));

    //viewportLayout.getColorbarRightMargin();
    
    // print me
    viewportLayout.getLegendsWidth();
    
    for(ILegend legend : viewportLayout.getLegends()) {
      if(legend instanceof AWTColorbarLegend) {
        AWTColorbarLegend colorbar = (AWTColorbarLegend)legend;
        
        // print me
        colorbar.getRectangle();
        
        // for native
        ViewportConfiguration viewport = colorbar.getLastViewPort();
        
        
        /*if(!view.getCanvas().isNative()) {
          EmulGLCanvas c = view.getCanvas();
        }*/
        
        if(viewport==null) {
          return;
        }
        
        int x = viewport.getX();
        int y = viewport.getY();
        int w = viewport.getWidth()-1;
        int h = viewport.getHeight()-1;
        
        int lineHeight = getLineHeigth(pixelScale, 0);

        g2d.drawString("Colorbar.Viewport", x, y+lineHeight);

        
        g2d.drawRect(x, y, w, h);

        //System.out.println("H=" + h);
        Margin margin = colorbar.getMargin();
        x+=margin.getLeft()*pixelScale.x;
        w-=margin.getWidth()*pixelScale.x;
        y+=margin.getTop()*pixelScale.y;
        h-=margin.getHeight()*pixelScale.y;
        
        g2d.drawString("Colorbar.Margins (" + margin.getWidth() + "," + margin.getHeight() + ")", x, y);
        g2d.drawRect(x, y, w, h);
        
        colorbar.getImage().getWidth();
        
        
        AWTColorbarImageGenerator gen = colorbar.getImageGenerator();

        // Bar
        w = gen.getScaledBarWidth();
        g2d.drawRect(x, y, w, h);
        
        int yBar = y+lineHeight;
        g2d.drawString("Colorbar.Bar", x, yBar);
        
        // MinDim
        g2d.setColor(AWTColor.toAWT(Color.ORANGE));
        
        x= viewport.getX();
        int yminDim= yBar+lineHeight;
        w =Math.round(colorbar.getMinimumDimension().width*pixelScale.getX());
        h = Math.round(colorbar.getMinimumDimension().height+pixelScale.getY());

        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, yminDim, w-1, h);
        
        //maxTextWidth = 
        
        g2d.drawString("Colorbar.MinDim", x, yminDim);

        g2d.setStroke(new BasicStroke(1));
       
        
        
        //System.out.println("H=" + h + " (" +margin.getBottom());
        
      }
    }
    
    
  }



  protected void drawViewMargins(Graphics2D g2d, Coord2d pixelScale) {
    
    g2d.setColor(AWTColor.toAWT(color));

    
    // ------------------------
    // Draw debug info

    int height = view.getCanvas().getRendererHeight();
    int width = view.getCanvas().getRendererWidth();
    
    // ------------------------
    // Horizontal lines

    int txtSize = axisLayout.getFont().getHeight();
    
    drawViewMarginsHorizontalLines(g2d, height, width, txtSize);
    
    
    // ------------------------
    // Vertical lines

    int lineHeight = getLineHeigth(pixelScale);

    drawViewMarginsVerticalLines(g2d, height, lineHeight);
  }

  private int getLineHeigth(Coord2d pixelScale) {
    int topMargin = 8;
    
    return getLineHeigth(pixelScale, topMargin);
  }

  private int getLineHeigth(Coord2d pixelScale, int topMargin) {
    int txtSize = axisLayout.getFont().getHeight();
    return Math.round(txtSize + topMargin*pixelScale.y);
  }



  protected void drawViewMarginsHorizontalLines(Graphics2D g2d, int height, int width, int txtSize) {
    FontMetrics fm = g2d.getFontMetrics();
    
    Coord2d ps = view.getPixelScale();

    int x;
    int y;
    // under axis label
    y = height-Math.round(layout.getMargin().getBottom()*ps.y);
    g2d.drawLine(0, y, width, y);
    
    x = Math.round(layout.getMargin().getLeft()*ps.x);
   
    String info = "Axis Label Bottom (" + y + ")";
    g2d.drawString(info, x, y);
    int shift = fm.stringWidth(info);


    // up to axis label
    y = height-Math.round(layout.getMargin().getBottom()*ps.y+txtSize);
    g2d.drawLine(0, y, width, y);

    info = "Axis Label Top (" + y + ")";
    g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);
    
    // down to tick label
    y = height-Math.round(layout.getMargin().getBottom()*ps.y+txtSize+layout.getHorizontalAxisLabelsDistance()*ps.y);
    g2d.drawLine(0, y, width, y);

    info = "Tick Label Bottom ("  + y + ")";
    g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);
    
    // up to tick label
    y = height-Math.round(layout.getMargin().getBottom()*ps.y+txtSize+layout.getHorizontalAxisLabelsDistance()*ps.y+txtSize);
    g2d.drawLine(0, y, width, y);
    
    info = "Tick Label Top (" + y + ")";
    g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);

    // up to tick label margin
    y = height-Math.round(layout.getMargin().getBottom()*ps.y+txtSize+layout.getHorizontalAxisLabelsDistance()*ps.y+txtSize+layout.getHorizontalTickLabelsDistance()*ps.y);
    g2d.drawLine(0, y, width, y);

    info = "Chart bottom border (" + y + ")";
    g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);

    // Horizontal top margin
    y = Math.round(layout.getMargin().getTop()*ps.y);
    g2d.drawLine(0, y, width, y);
    
    info = "Chart top border (" + y + ")";
    g2d.drawString(info, shift, y);

  }



  protected void drawViewMarginsVerticalLines(Graphics2D g2d, int height, int lineHeight) {
    FontMetrics fm = g2d.getFontMetrics();

    Coord2d ps = view.getPixelScale();

    int x;
    // left to axis label
    x = Math.round(layout.getMargin().getLeft()*ps.x);
    g2d.drawLine(x, 0, x, height);

    g2d.drawString("Axis Label Left Side / Left Margin (" + x + ")", x, lineHeight*1);

    // right to axis label
    x = Math.round(layout.getMargin().getLeft()*ps.x+view.get2DProcessing().getAxisTextHorizontal());
    g2d.drawLine(x, 0, x, height);

    g2d.drawString("Axis Label Right Side (" + x + ")", x, lineHeight*2);

    
    // left to tick label
    x = Math.round(x+layout.getVerticalAxisLabelsDistance()*ps.x);
    g2d.drawLine(x, 0, x, height);

    g2d.drawString("Tick Label Left Side ("  + x + ")", x, lineHeight*3);

    
    // right to tick label
    x = Math.round(x+view.get2DProcessing().getTickTextHorizontal());
    g2d.drawLine(x, 0, x, height);

    g2d.drawString("Tick Label Right Side (" + x + ")", x, lineHeight*4);
    
    // on chart border
    x = Math.round(x+layout.getVerticalTickLabelsDistance()*ps.x);
    g2d.drawLine(x, 0, x, height);
    
    g2d.drawString("Chart left border (" + x + ")", x, lineHeight*5);

    x = Math.round(view.getCamera().getLastViewPort().getWidth()-layout.getMargin().getRight()*ps.x);
    
    // Using the processed right margin that differs between Native and EmulGL 
    // because of the different colobar management
    x = view.getCamera().getLastViewPort().getWidth()-processing.getMarginPx().getRight();
    
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
