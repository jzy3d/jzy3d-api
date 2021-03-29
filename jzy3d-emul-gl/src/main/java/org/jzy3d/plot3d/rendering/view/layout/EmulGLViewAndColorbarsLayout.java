package org.jzy3d.plot3d.rendering.view.layout;

import java.awt.image.BufferedImage;
import java.util.List;

import org.jzy3d.chart.Chart;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportBuilder;
import org.jzy3d.plot3d.rendering.view.ViewportMode;

import jgl.GL;

public class EmulGLViewAndColorbarsLayout extends ViewAndColorbarsLayout {
  boolean doShiftjGLForColorbar = false;
  boolean doFixHiDPI = true;

  public EmulGLViewAndColorbarsLayout() {
  }

  @Override
  public void render(IPainter painter, Chart chart) {
    View view = chart.getView();
    view.renderBackground(backgroundViewport);

    // Here we force the scene to be rendered on the entire screen to avoid a GRAY
    // (=CLEAR COLOR?) BAND
    // that can't be overriden by legend image
    sceneViewport = ViewportBuilder.column(chart.getCanvas(), 0, 1);// screenSeparator);
    //sceneViewport = ViewportBuilder.column(chart.getCanvas(), 0, screenSeparator);

    view.renderScene(sceneViewport);

    renderLegends(painter, chart);

    // fix overlay on top of chart
    view.renderOverlay(view.getCamera().getLastViewPort());
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
    EmulGLPainter emulGL = (EmulGLPainter) painter;

    
    

    int width = canvas.getRendererWidth();
    int height = canvas.getRendererHeight();

    if (doFixHiDPI) {
    width = (int)(sceneViewport.getWidth()*emulGL.getGL().getPixelScaleX());
    height = (int)(sceneViewport.getHeight()*emulGL.getGL().getPixelScaleY());//canvas.getRendererHeight();
    }
    
    // ---------------------------------------
    /// HAAACKKKKYYYYY : SHIFT THE VIEWPORT TO LET SPACE FOR COLORBAR

    if(doShiftjGLForColorbar) {
      float shift = (right - left) * width / 2;

      emulGL.getGL().setShiftHorizontally((int) -shift);
    }
    
    // ---------------------------------------
    
    
    float slice = (right - left) / legends.size();
    int k = 0;
    for (ILegend legend : legends) {
      legend.setViewportMode(ViewportMode.STRETCH_TO_FILL);
      
      float from = left + slice * (k++);
      float to = from + slice;
      
      
      
      legend.setViewPort(width, height, from, to);

      // legend.render(painter); // BYPASS IMAGE RENDERING THAT DOES NOT WORK WELL IN
      // jGL
      // legend.get

      if (legend instanceof AWTColorbarLegend) {
        AWTColorbarLegend awtLegend = (AWTColorbarLegend) legend;
        BufferedImage legendImage =  (BufferedImage) awtLegend.getImage();
        int legendWidth = (int)(legendImage.getWidth() / emulGL.getGL().getPixelScaleX());
        
        //System.out.println("leg.width=" + legendWidth + " canvas.hidpi.width=" + width + " canvas.width=" + canvas.getRendererWidth());
        
        emulGL.getGL().appendImageToDraw(legendImage, width-legendWidth*(k), 0);
      }
    }
  }

}
