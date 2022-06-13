package org.jzy3d.plot3d.rendering.view.layout;

import java.awt.image.BufferedImage;
import java.util.List;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Margin;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportBuilder;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import jgl.GL;

public class EmulGLViewAndColorbarsLayout extends ViewAndColorbarsLayout {
  boolean fixHiDPI = true;
  
  @Override
  public void update(Chart chart) {
    super.update(chart);
    
    // simply override to let scene viewport occupy full canvas
    // due to jGL limitation with viewport that are not canvas-wide
    sceneViewport = ViewportBuilder.column(chart.getCanvas(), 0, 1);
  }
  
  protected int updateAndGetMinDimensions(IPainter painter, ILegend legend) {
    
    if (legend instanceof AWTColorbarLegend) {
      AWTColorbarLegend awtLegend = (AWTColorbarLegend) legend;
      awtLegend.updatePixelScale(painter.getView().getPixelScale());
      awtLegend.setEmulGLUnscale(true);
    }    
    
    legend.updateMinimumDimension(painter);
    
    return legend.getMinimumDimension().width;
  }


  /**
   * This override allows
   * <ul>
   * <li>Shifting artificially the complete jGL viewport to let some place for a colorbar rendering.
   * <li>Rendering the image using jGL dedicated image management
   * ({@link GL#appendImageToDraw(BufferedImage, int, int)}).
   * </ul>
   */
  @Override
  protected void renderLegends(IPainter painter, float left, float right, List<ILegend> legends,
      ICanvas canvas) {

    int width = canvas.getRendererWidth();
    int height = canvas.getRendererHeight();

    
    View view = chart.getView();
    Coord2d pixelScale = view.getPixelScale();

    if (fixHiDPI) {
      width = (int) (sceneViewport.getWidth() * pixelScale.x);
      height = (int) (sceneViewport.getHeight() * pixelScale.y);
    }
    
    //System.out.println("EmulGLViewAndColorbar : legendWidth " + legendsWidth);

    // ---------------------------------------

    float slice = (right - left) / legends.size();
    int k = 0;
    for (ILegend legend : legends) {
      legend.setViewportMode(ViewportMode.STRETCH_TO_FILL);

      float from = left + slice * (k++);
      float to = from + slice;
      
      Font font = painter.getView().getAxis().getLayout().getFont();
      legend.setFont(font);

      // FALLBACK ON DIRECT AWT IMAGE RENDERING THROUGH jGL
      if (legend instanceof AWTColorbarLegend) {
        
        AWTColorbarLegend awtLegend = (AWTColorbarLegend) legend;
        awtLegend.setViewPort(width, height, from, to);
        
        BufferedImage legendImage = (BufferedImage) awtLegend.getImage();
        
        int legendWidth = legendImage.getWidth();
        
        // ---------------------------------------
        // Processing image offset with pixel scale
       
        Margin margin = awtLegend.getMargin();
        
        int xOffset = width - legendWidth * (k);
        xOffset += Math.round (pixelScale.x * margin.getLeft());
        xOffset -= Math.round (pixelScale.x * margin.getRight());

        int yOffset = 0;
        yOffset += Math.round (pixelScale.y * margin.getTop());
        
        // ---------------------------------------
        // Send image rendering directly to jGL
        
        // Display colorbar only if their remain space for plot that is wider than higher
        EmulGLPainter emulGL = (EmulGLPainter) painter;
        emulGL.getGL().appendImageToDraw(legendImage, xOffset, yOffset);

      }
      
      // BYPASSED IMAGE RENDERING THAT DOES NOT WORK WELL IN jGL BUT KEEP EXPECTED OPENGL WAY
      // FOR MEMORY
      else {
        legend.setViewPort(width, height, from, to);
        legend.render(painter);

      }
    }
  }

  public boolean isFixHiDPI() {
    return fixHiDPI;
  }
  public void setFixHiDPI(boolean fixHiDPI) {
    this.fixHiDPI = fixHiDPI;
  }
}
