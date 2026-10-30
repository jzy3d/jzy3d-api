package org.jzy3d.plot3d.rendering.view;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Margin;
import org.jzy3d.painters.AWTFont;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
import org.jzy3d.plot2d.rendering.AWTGraphicsUtils;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.AbstractAWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.View2DProcessing;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;

public class View2DLayout_Debug extends AbstractAWTRenderer2d implements AWTRenderer2d{

  View2DProcessing processing;
  AxisLayout axisLayout;
  ViewAndColorbarsLayout viewportLayout;
  Color color = Color.CYAN;
  Color color2 = Color.MAGENTA;
  
  String info = "";
  
  boolean enabled = true;
  boolean enableColorMarginDebug = true;
  boolean enableViewMarginDebug = true;
  boolean showText = false;
  
  int interlinePx = 8;
  
  public View2DLayout_Debug() {
  }
  
  public View2DLayout_Debug(Color color) {
    this.color = color;
  }

  @Override
  public void setView(AWTView view) {
    super.setView(view);

    this.processing = view.get2DProcessing();
    this.axisLayout = view.getAxis().getLayout();
    this.viewportLayout = (ViewAndColorbarsLayout)view.getLayout();
  }


  @Override
  public void paint(Graphics g, int canvasWidth, int canvasHeight) {
    if(!enabled)
      return;
    
    Graphics2D g2d = (Graphics2D) g;
    Coord2d pixelScale = view.getPixelScale();
    
    
    configureFontAndHiDPI(g2d, pixelScale);
    AWTGraphicsUtils.configureRenderingHints(g2d); // Text and aliasing settings

    // Draw viewport info
    ViewportConfiguration v = view.getCamera().getLastViewPort();
    String info = "Viewport (" + v.getWidth() + "," + v.getHeight() + ")";
    
    if(!view.getCanvas().isNative()) {
      EmulGLPainter p = (EmulGLPainter)view.getPainter();
      BufferedImage i = p.getGL().getRenderedImage();
      
      if(i!=null) {
        info += " img:" + i.getWidth() + "," + i.getHeight();
      }
    }
    
    g2d.setColor(java.awt.Color.black);
    
    if(showText)
      g2d.drawString(info, 0, view.getAxisLayout().getFont().getHeight());

    if(enableViewMarginDebug)
      drawViewMargins(g2d, pixelScale);
    
    if(enableColorMarginDebug)
      drawColorbarMargins(g2d, pixelScale);
    
    // draw other debug info
    if(this.info!=null) {
      g2d.setColor(java.awt.Color.white);
      g2d.drawString(this.info, canvasWidth/2, canvasHeight/2);
    }
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
        
        //System.out.println("View2DLayout_Debug : CBar viewport " + viewport);
        
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

        String info = "Colorbar.Viewport (" + viewport.getWidth()+","+viewport.getHeight()+")";
        if(showText)
          g2d.drawString(info, x, y+lineHeight);

        
        g2d.drawRect(x, y, w, h);

        //System.out.println("H=" + h);
        Margin margin = colorbar.getMargin();
        x+=margin.getLeft()*pixelScale.x;
        w-=margin.getWidth()*pixelScale.x;
        y+=margin.getTop()*pixelScale.y;
        h-=margin.getHeight()*pixelScale.y;
        
        //info = "Colorbar.Margins (" + margin.getWidth() + "," + margin.getHeight() + ")";
        
        BufferedImage image = colorbar.getImage();

        
        info = "Colorbar.Image (" + image.getWidth() + "," + image.getHeight() + ")";
        if(showText)
          g2d.drawString(info, x, y);
        g2d.drawRect(x, y, w, h);
        
        
        
        AWTColorbarImageGenerator gen = colorbar.getImageGenerator();

        // Bar
        w = gen.getScaledBarWidth();
        g2d.drawRect(x, y, w, h);
        
        int yBar = y+lineHeight;
        if(showText)
          g2d.drawString("Colorbar.Bar", x, yBar);
        
        // MinDim
        g2d.setColor(AWTColor.toAWT(Color.ORANGE));
        
        x= viewport.getX();
        int yminDim= Math.round((yBar+lineHeight+100) * pixelScale.y);
        w = Math.round(colorbar.getMinimumDimension().width*pixelScale.getX());
        h = Math.round(colorbar.getMinimumDimension().height*pixelScale.getY());

        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, yminDim, w-1, h);
        
        //maxTextWidth = 
        
        if(showText) {
          g2d.drawString("Colorbar.MinDim", x, yminDim);
          g2d.drawString(colorbar.getMinimumDimension().toString(), x, yminDim+g2d.getFont().getSize()+1);
        }
        
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
    
    
    boolean isEmulGL = !view.getCanvas().isNative();

    if (isEmulGL) {
      txtSize /= view.getPixelScale().y;
    }
    
    int x;
    int y;
    

    x = processing.getMarginPxScaled().getLeft();

    
    // ---------------------------
    // under axis label
    
    y = height - processing.getMarginPxScaled().getBottom();
    g2d.drawLine(0, y, width, y);
    
    String info = "Axis Label Bottom (" + y + ")";
    if(showText)
      g2d.drawString(info, x, y);
    int shift = fm.stringWidth(info);


    // up to axis label
    y = height-(processing.getMarginPxScaled().getBottom()+txtSize);
    g2d.drawLine(0, y, width, y);

    info = "Axis Label Top (" + y + ")";
    if(showText)
      g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);
    
    // ---------------------------
    // down to tick label
    y = height-Math.round(processing.getMarginPxScaled().getBottom()+txtSize+processing.getHorizontalAxisDistance());
    g2d.drawLine(0, y, width, y);

    info = "Tick Label Bottom ("  + y + ")";
    if(showText)
      g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);
    
    // up to tick label
    y = height-Math.round(processing.getMarginPxScaled().getBottom()+txtSize+processing.getHorizontalAxisDistance()+txtSize);
    g2d.drawLine(0, y, width, y);
    
    info = "Tick Label Top (" + y + ")";
    if(showText)
      g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);

    // ---------------------------
    // up to tick label margin
    y = height-Math.round(processing.getMarginPxScaled().getBottom()+txtSize+processing.getHorizontalAxisDistance()+txtSize+processing.getHorizontalTickDistance());
    g2d.drawLine(0, y, width, y);

    info = "Chart bottom border (" + y + ")";
    if(showText)
      g2d.drawString(info, shift, y);
    shift += fm.stringWidth(info);

    // Horizontal top margin
    y = processing.getMarginPxScaled().getTop();
    g2d.drawLine(0, y, width, y);
    
    info = "Chart top border (" + y + ")";
    if(showText)
      g2d.drawString(info, shift, y);

  }



  protected void drawViewMarginsVerticalLines(Graphics2D g2d, int height, int lineHeight) {
    FontMetrics fm = g2d.getFontMetrics();

    int x;
    // left to axis label
    x = processing.getMarginPxScaled().getLeft();
    g2d.drawLine(x, 0, x, height);

    if(showText)
      g2d.drawString("Axis Label Left Side / Left Margin (" + x + ")", x, lineHeight*1);

    // right to axis label
    x = Math.round(processing.getMarginPxScaled().getLeft() + processing.getAxisTextWidth());
    g2d.drawLine(x, 0, x, height);

    if(showText)
      g2d.drawString("Axis Label Right Side (" + x + ")", x, lineHeight*2);

    
    // left to tick label
    x = Math.round(x+processing.getVerticalAxisDistance());
    g2d.drawLine(x, 0, x, height);

    if(showText)
      g2d.drawString("Tick Label Left Side ("  + x + ")", x, lineHeight*3);

    
    // right to tick label
    x = Math.round(x+processing.getTickTextWidth());
    g2d.drawLine(x, 0, x, height);

    if(showText)
      g2d.drawString("Tick Label Right Side (" + x + ")", x, lineHeight*4);
    
    // on chart border
    x = Math.round(x+processing.getVerticalTickDistance());
    g2d.drawLine(x, 0, x, height);
    
    if(showText)
      g2d.drawString("Chart left border (" + x + ")", x, lineHeight*5);

    x = Math.round(view.getCamera().getLastViewPort().getWidth()-processing.getMarginPxScaled().getRight());
    
    // Using the processed right margin that differs between Native and EmulGL 
    // because of the different colobar management
    x = view.getCamera().getLastViewPort().getWidth()-processing.getMarginPx().getRight();
    
    g2d.drawLine(x, 0, x, height);
    
    String txt = "Chart right border (" + x + ")";
    
    if(showText)
      g2d.drawString(txt, x - fm.stringWidth(txt), lineHeight*6);
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnableColorMarginDebug() {
    return enableColorMarginDebug;
  }

  public void setEnableColorMarginDebug(boolean enableColorMarginDebug) {
    this.enableColorMarginDebug = enableColorMarginDebug;
  }

  public boolean isEnableViewMarginDebug() {
    return enableViewMarginDebug;
  }

  public void setEnableViewMarginDebug(boolean enableViewMarginDebug) {
    this.enableViewMarginDebug = enableViewMarginDebug;
  }
  
  
}
