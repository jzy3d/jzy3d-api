package org.jzy3d.plot3d.rendering.view.layout;

import java.util.ArrayList;
// import java.awt.Rectangle;
import java.util.List;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.view.AbstractViewportManager;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.IViewOverlay;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportBuilder;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import org.jzy3d.plot3d.rendering.view.ViewportMode;

/**
 * This class handles the layout of a main 3D plot on the left with additional legends (colorbars)
 * on the right side.
 * 
 * The canvas is composed of two {@link AbstractViewportManager}
 * <ul>
 * <li>The {@link View} which handles its viewport with the {@link Camera}. If the view is and
 * {@link AWTView} or children, it also allows defining 2D {@link IViewOverlay} that can span on the
 * left 3D side only or on the full canvas (hence also covering the 2D right side).
 * <li>The {@link ILegend} objects which handle their viewport on their own. They are added to the
 * right of the chart according to the number of {@link Drawable} having a {@link ILegend} set such
 * as {@link AWTColorbarLegend}
 * </ul>
 * 
 * This allow making a composition of 3D and 2D content in a single screen.
 * 
 * @author Martin Pernollet
 */
public class ViewAndColorbarsLayout implements IViewportLayout {
  protected float screenSeparator = 1.0f;
  protected boolean hasColorbars = false;

  protected ViewportConfiguration sceneViewport;
  protected ViewportConfiguration backgroundViewport;

  // stored at render time for later layout processing
  protected float legendsWidth = 0;

  protected Chart chart;
  
  public Chart getChart() {
    return chart;
  }

  public void setChart(Chart chart) {
    this.chart = chart;
  }

  @Override
  public void update(Chart chart) {
    this.chart = chart;
    
    IPainter painter = chart.getPainter();
    ICanvas canvas = chart.getCanvas();
    List<ILegend> list = getLegends();

    computeSeparator(painter, canvas, list);
    
    sceneViewport = ViewportBuilder.column(canvas, 0, screenSeparator);
    backgroundViewport = new ViewportConfiguration(canvas);
  }

  // Separator only used for native since emulgl can not have two viewport side by side,
  // only a single viewport with images rendered on top
  protected void computeSeparator(IPainter painter, ICanvas canvas, List<ILegend> legends) {
    hasColorbars = legends.size() > 0;
    
    if (hasColorbars) {
      
      int minWidth = 0;
      
      for (ILegend legend : legends) {
        minWidth += updateAndGetMinDimensions(painter, legend);
        
        //System.out.println("ViewAndColorbarsLayout : legend.minDim : " + legend.getMinimumDimension() + " minWidth:" +minWidth );
      }

      screenSeparator = computeSeparator(canvas, minWidth);
    
    
    } else {
      screenSeparator = 1.0f;
    }
  }

  protected int updateAndGetMinDimensions(IPainter painter, ILegend legend) {
    // here we don't need to consider pixel scale as the colorbar
    // is already returning a dimension considering pixel scale.
    legend.updateMinimumDimension(painter);
    
    return legend.getMinimumDimension().width;
  }

  protected float computeSeparator(final ICanvas canvas, int minWidth) {
    int width = Math.round(canvas.getRendererWidth());
    
    if(width==0)
      throw new IllegalArgumentException("Canvas has no width!");

    return ((float) (width - minWidth)) / width;
  }

  /**
   * Once rendered, this layout knows the colorbar width which can be retrieved with {@link #getLegendsWidth()}
   */
  @Override
  public void render(IPainter painter, Chart chart) {
    renderView(painter, chart);
  }

  protected void renderView(IPainter painter, Chart chart) {
    View view = chart.getView();

    // System.out.println("ViewAndColorbarLayout w:" + chart.getCanvas().getRendererWidth() + " h:"
    // + chart.getCanvas().getRendererHeight());

    // Background
    view.renderBackground(backgroundViewport);

    // Underlay
    // if(view.getCamera().getLastViewPort()!=null)
    // view.renderOverlay(view.getCamera().getLastViewPort());

    // Scene
    view.renderScene(sceneViewport);

    // Legend
    renderLegends(painter, chart);

    // Overlay
    if (view.getCamera().getLastViewPort() != null) {
      view.renderOverlay(view.getCamera().getLastViewPort());
    } else {
      view.renderOverlay(); // ignore colorbar
    }
  }

  protected void renderLegends(IPainter painter, Chart chart) {
    if (hasColorbars) {
      updateLegendsWidth(chart);
      
      renderLegends(painter, screenSeparator, 1.0f, getLegends(), chart.getCanvas());
    } else {
      legendsWidth = 0;
    }
  }

  protected void updateLegendsWidth(Chart chart) {
    legendsWidth = (1-screenSeparator) * chart.getCanvas().getRendererWidth();
  }

  /**
   * Renders the legends within the screen slice given by the left and right parameters.
   */
  protected void renderLegends(IPainter painter, float left, float right, List<ILegend> legends,
      ICanvas canvas) {
    float slice = (right - left) / legends.size();
    int k = 0;
    for (ILegend legend : legends) {
      
      int width = canvas.getRendererWidth();
      int height = canvas.getRendererHeight();

      // This workaround for Windows+AWT (only) allow scaling viewport to
      // fix a pixel scale error. It must be taken into account to get
      // a colorbar properly scaled
      
      Coord2d s = AbstractViewportManager.getWindowsHiDPIScale_Workaround(painter);
      //System.out.println("ViewAndColorbars : hackScale:" + s);
      if(hackWindowsAWTColorbarWidth) {
        width = Math.round(canvas.getRendererWidth()*s.x);
      }
      if(hackWindowsAWTColorbarHeight) {
        height = Math.round(canvas.getRendererHeight()*s.y);
      }
      
      // create left/right per legend
      float theLeft = left + slice * (k++);
      float theRight = left + slice * k;
      
      //System.out.println("ViewAndColorbars : width:" + width + " height:" + height);
      //System.out.println("ViewAndColorbars : TheLeft:" + theLeft + " TheRight:" + theRight);
      
      //((AbstractViewportManager)legend).setApplyWindowsHiDPIWorkaround(true);
      
      legend.setFont(painter.getView().getAxis().getLayout().getFont());
      legend.setViewportMode(ViewportMode.STRETCH_TO_FILL);
      
      if(hackWindowsAWTColorbarHeight && s.y>1) {
        // We can not find a good yOffset so we keep this for later
        AbstractViewportManager legendViewportHack = (AbstractViewportManager)legend;
        
        int yOffset = Math.round(-canvas.getRendererHeight()/(s.y*2));
        
        //int yOffset = Math.round(-(canvas.getRendererHeight()+legend.getMargin().getBottom()/2f)/(s.y*2));
        //int yOffset = Math.round(-(canvas.getRendererHeight()-legend.getMargin().getTop()*s.y*2)/(s.y*2));
        
        //System.out.println("ViewAndColorbars : hackOffset:" + yOffset);
        legendViewportHack.setScreenYOffset(yOffset);
        // NEED TO COMMENT AbstractViewportManager.applyViewportRectangle / screenYOffset = 0
      }

      legend.setViewPort(width, height, theLeft, theRight);

      legend.render(painter);
      
    }
  }

  boolean hackWindowsAWTColorbarWidth = true;
  boolean hackWindowsAWTColorbarHeight = false;
  
  public List<ILegend> getLegends() {
    return getLegends(chart);
  }
  
  protected List<ILegend> getLegends(Chart chart) {
    
    List<ILegend> legends = null;
    
    if(chart != null && chart.getScene() != null && chart.getScene().getGraph() != null
        && chart.getScene().getGraph().getLegends() != null) {
      legends = chart.getScene().getGraph().getLegends();
    }else
      legends = new ArrayList<>();
    
    return legends;
  }

  /**
   * Return the scene viewport as it was processed according to the number of legends to display.
   */
  public ViewportConfiguration getSceneViewport() {
    return sceneViewport;
  }

  /** Return the scene viewport as it was processed to cover the whole canvas. */
  public ViewportConfiguration getBackgroundViewport() {
    return backgroundViewport;
  }

  /**
   * Return the legend width as it was processed at the rendering stage. Hence this value is defined
   * after a first rendering. It is used for processing the remaining part of the layout (other
   * viewport needing the colorbar width information).
   */
  public float getLegendsWidth() {
    return legendsWidth;
  }
}
