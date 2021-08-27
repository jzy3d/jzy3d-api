package org.jzy3d.plot3d.rendering.view.layout;

import java.awt.image.BufferedImage;
import java.util.List;
import org.jzy3d.chart.Chart;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
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
    

    if (fixHiDPI) {
      width = (int) (sceneViewport.getWidth() * emulGL.getGL().getPixelScaleX());
      height = (int) (sceneViewport.getHeight() * emulGL.getGL().getPixelScaleY());// canvas.getRendererHeight();
    }

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
        
        //awtLegend.getImageGenerator().setFont(font);
        
        // optimize to shrink colorbar to required width
        if(shrinkColorbar) {
          AWTColorbarImageGenerator gen =  awtLegend.getImageGenerator();
          int optimalColorbarWidth = (int)Math.ceil((gen.getPreferedWidth(painter)+colorbarRightMargin) * painter.getView().getPixelScale().x);
          
          // Random fix to avoid cutting text
          int pixelShiftFix = 1+AWTColorbarImageGenerator.BAR_WIDTH_DEFAULT;
          from = 1-((1f*optimalColorbarWidth+pixelShiftFix)/(1f*width));
        }
        
        legend.setViewPort(width, height, from, to);
        
        BufferedImage legendImage = (BufferedImage) awtLegend.getImage();
        
        int legendWidth = (int) (legendImage.getWidth() / emulGL.getGL().getPixelScaleX());

        // ---------------------------------------
        // Processing yoffset with pixel scale
        int yOffset = 0;
        if (emulGL.getGL().getPixelScaleY() == 1) {
          yOffset = (int) (awtLegend.getMargin().height / 2f);
        } else {
          yOffset =
              (int) ((emulGL.getGL().getPixelScaleY() - 1) * awtLegend.getMargin().height) / 2;
        }

        // ---------------------------------------
        // Send image rendering directly to jGL
        
        int xOffset = width - legendWidth * (k);
        
        // Display colorbar only if their remain space for plot that is wider than higher
        //if(xOffset>height) {
          emulGL.getGL().appendImageToDraw(legendImage, xOffset, yOffset);
        //}

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
