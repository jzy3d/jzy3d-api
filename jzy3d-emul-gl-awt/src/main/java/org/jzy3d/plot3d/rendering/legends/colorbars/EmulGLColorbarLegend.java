package org.jzy3d.plot3d.rendering.legends.colorbars;

import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import jgl.wt.awt.GL;

public class EmulGLColorbarLegend extends AWTColorbarLegend{

  public EmulGLColorbarLegend(Drawable parent, AxisLayout layout, Color foreground,
      Color background) {
    super(parent, layout, foreground, background);
  }

  public EmulGLColorbarLegend(Drawable parent, AxisLayout layout, Color foreground) {
    super(parent, layout, foreground);
  }

  public EmulGLColorbarLegend(Drawable parent, AxisLayout layout) {
    super(parent, layout);
  }

  public EmulGLColorbarLegend(Drawable parent, Chart chart) {
    super(parent, chart);
  }

  public EmulGLColorbarLegend(Drawable parent, ITickProvider provider, ITickRenderer renderer,
      Color foreground, Color background) {
    super(parent, provider, renderer, foreground, background);
  }

  public EmulGLColorbarLegend(Drawable parent, ITickProvider provider, ITickRenderer renderer) {
    super(parent, provider, renderer);
  }

  @Override
  protected void renderImage(IPainter painter) {
    if (imageData == null)
      return;

    ImageLayout iLayout = computeLayout(painter);

    // Draw

    GL gl = ((EmulGLPainter)painter).getGL();
    
    gl.appendImageToDraw(image, (int)iLayout.position.x, (int)iLayout.position.y);
    
    //painter.drawImage(imageData, imageWidth, imageHeight, iLayout.zoom, iLayout.position);
  }
  
  public ViewportConfiguration applyViewport(IPainter painter) {

    // Workaround for https://github.com/jzy3d/jogl/issues/8
    if(apply_WindowsHiDPI_Workaround) {
      if(painter.getOS().isWindows() && painter.getWindowingToolkit().isAWT()) {
        // We here scale the viewport by either 1 or by the ratio indicated by the JVM
        // if only the JVM is able to detect the pixel ratio and if JOGL
        // can't guess it (which is the case for Windows 10).
        Coord2d scaleHardware = painter.getCanvas().getPixelScale();
        Coord2d scaleJVM = painter.getCanvas().getPixelScaleJVM();
  
        //System.out.println("HiDPI : " + isHiDPIEnabled);
        //System.out.println("GPU   : " + scaleHardware);
        //System.out.println("JVM   : " + scaleJVM);
        
        if (painter.isJVMScaleLargerThanNativeScale(scaleHardware, scaleJVM)) {
          Coord2d scale = scaleJVM.div(scaleHardware);
          //System.out.println("Scale : " + scale);
          screenWidth = (int) (screenWidth * scale.x);
          screenHeight = (int) (screenHeight * scale.y);
        }
      }
    }
    
    // Stretch projection on the whole viewport
    if (ViewportMode.STRETCH_TO_FILL.equals(mode)
        || ViewportMode.RECTANGLE_NO_STRETCH.equals(mode)) {
      screenXOffset = screenLeft;
      screenYOffset = 0;


      //painter.glViewport(screenXOffset, screenYOffset, screenWidth, screenHeight);

      lastViewPort =
          new ViewportConfiguration(screenWidth, screenHeight, screenXOffset, screenYOffset);
      lastViewPort.setMode(mode);
    }
    // Set the projection into the largest square area centered in the
    // window slice
    else if (ViewportMode.SQUARE.equals(mode)) {
      screenSquaredDim = Math.min(screenWidth, screenHeight);
      screenXOffset = screenLeft + screenWidth / 2 - screenSquaredDim / 2;
      screenYOffset = screenBottom + screenHeight / 2 - screenSquaredDim / 2;

      //painter.glViewport(screenXOffset, screenYOffset, screenSquaredDim, screenSquaredDim);

      lastViewPort = new ViewportConfiguration(screenSquaredDim, screenSquaredDim, screenXOffset,
          screenYOffset);
      lastViewPort.setMode(mode);
    } else {
      throw new IllegalArgumentException("unknown mode " + mode);
    }

    // Render the screen grid if required
    if (screenGridDisplayed)
      renderSubScreenGrid(painter);

    return lastViewPort;
  }

}
