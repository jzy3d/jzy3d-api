package org.jzy3d.plot3d.rendering.view;

import java.util.List;
import org.jzy3d.maths.Area;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Margin;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.AxisLabelProcessor;
import org.jzy3d.plot3d.primitives.axis.AxisTickProcessor;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

/**
 * Process and store the layout of a 2D view having margins and axis labels defined by the
 * {@link View2DLayout} settings.
 * 
 * Storing the results is handy for classes needing to reuse intermediate processing of this margins
 * :
 * <ul>
 * <li>{@link View#computeCamera2D_RenderingSquare(Camera, ViewportConfiguration, BoundingBox3d)}
 * <li>{@link AxisLabelProcessor#axisLabelPosition_2D}
 * <li>{@link AxisTickProcessor#getTickLength3D_OrComputeTickLength2D}
 * </ul>
 * 
 * Processing margins relies on the computation of {@link #getModelToScreenRatio(Area, Area, Area)}
 * which has an extended documentation providing mathematical demonstration.
 * 
 * @see {@link View2DLayout_Debug} 2D post renderer to print the margins.
 * 
 * @author Martin Pernollet
 *
 */
public class View2DProcessing {
  protected View view;

  // each text occupation given in pixels
  protected float tickTextWidth = 0;
  protected float tickTextHeight = 0;
  protected float axisTextWidth = 0;
  protected float axisTextHeight = 0;
  
  // each distance multiplied by pixel scale
  protected float horizontalTickDistance;
  protected float horizontalAxisDistance;
  protected float verticalTickDistance;
  protected float verticalAxisDistance;
  
  // each margin given in pixels
  protected float marginLeftPxScaled = 0;
  protected float marginRightPxScaled = 0;
  protected float marginTopPxScaled = 0;
  protected float marginBottomPxScaled = 0;


  // each margin given in pixels
  protected float marginLeftPx = 0;
  protected float marginRightPx = 0;
  protected float marginTopPx = 0;
  protected float marginBottomPx = 0;

  // each margin given in world coordinates
  protected float marginLeftModel = 0;
  protected float marginRightModel = 0;
  protected float marginTopModel = 0;
  protected float marginBottomModel = 0;


  // the overall margins on the width and height, summing all margin of each dimension
  protected Area marginArea;
  // the bounds of the two dimensions of the 3D space that are currently viewed
  protected Area spaceArea;
  // the viewport dimensions
  protected Area screenArea;


  protected Coord2d modelToScreen;

  public View2DProcessing() {
  }

  public View2DProcessing(View view) {
    this.view = view;
  }

  /**
   * Apply current view's {@link View2DLayout} and process all margins in 3D.
   */
  public void apply(ViewportConfiguration viewport, BoundingBox3d bounds) {
    IPainter painter = view.getPainter();
    Coord2d pixelScale = view.getPixelScale();
    AxisLayout axisLayout = view.getAxis().getLayout();
    View2DLayout view2DLayout = view.get2DLayout();

    Font font = axisLayout.getFont();
    
    boolean isEmulGL = !view.getCanvas().isNative();

    // ---------------------------------------------------
    // initialize all margins according to configuration
    
    marginLeftPxScaled = view2DLayout.getMargin().getLeft() * pixelScale.x;
    marginRightPxScaled = view2DLayout.getMargin().getRight() * pixelScale.x;
    marginTopPxScaled = view2DLayout.getMargin().getTop() * pixelScale.y;
    marginBottomPxScaled = view2DLayout.getMargin().getBottom() * pixelScale.y;
    
    if(isEmulGL) {
      marginLeftPxScaled /= pixelScale.x;
      marginRightPxScaled /= pixelScale.x;
      marginTopPxScaled /= pixelScale.y;
      marginBottomPxScaled /= pixelScale.y;
    }
    
    marginLeftPx = marginLeftPxScaled;
    marginRightPx = marginRightPxScaled;
    marginTopPx = marginTopPxScaled;
    marginBottomPx = marginBottomPxScaled;
    
    //System.err.println("View2DP : Margin : " + view2DLayout.getMargin());

    // ---------------------------------------------------
    // compute pixel occupation of ticks and axis labels

    if (view2DLayout.textAddMargin) {

      // consider space occupied by tick labels
      tickTextHeight = font.getHeight();

      if(isXY()) {
        tickTextWidth = axisLayout.getMaxYTickLabelWidth(painter);
      } else if(isXZ() || isYZ()) {
        tickTextWidth = axisLayout.getMaxZTickLabelWidth(painter);
      }
      
      // consider space occupied by the vertical axis label
      String leftAxisLabel = null;
      LabelOrientation leftAxisOrientation = null;
      
      if(isXY()) {
        leftAxisLabel = axisLayout.getYAxisLabel();
        leftAxisOrientation = axisLayout.getYAxisLabelOrientation();
      } else if(isXZ() || isYZ()) {
        leftAxisLabel = axisLayout.getZAxisLabel();
        leftAxisOrientation = axisLayout.getZAxisLabelOrientation();
      }
      
      if (LabelOrientation.HORIZONTAL.equals(leftAxisOrientation)) {
        // horizontal Y axis involves considering the axis label width
        axisTextWidth = painter.getTextLengthInPixels(font, leftAxisLabel);
      } else {
        // vertical Y axis involves considering the axis label font height
        axisTextWidth = font.getHeight();
      }

      // consider space occupied by the X axis label, which is always horizontal
      axisTextHeight = font.getHeight();
      
    } else {
      tickTextWidth = 0;
      tickTextHeight = 0;
      axisTextWidth = 0;
      axisTextHeight = 0;
    }

    // ------------------------------------------------
    // Handling a difference between native and emulGL
    // The text in the EmulGL case must be considered smaller 
    // according to pixel scale
    // ------------------------------------------------

    if (isEmulGL) {
      
      // Accurate Vertical axis layout
      tickTextWidth /= pixelScale.x;
      axisTextWidth /= pixelScale.x;
      
      // Accurate Horizontal axis layout
      tickTextHeight /= (pixelScale.y);
      axisTextHeight /= (pixelScale.y);
    }

    // ---------------------------------------------------
    // add space for text to the left margin

    verticalTickDistance = view2DLayout.verticalTickLabelsDistance * pixelScale.x;
    verticalAxisDistance = view2DLayout.verticalAxisLabelsDistance * pixelScale.x;
    
    horizontalTickDistance = view2DLayout.horizontalTickLabelsDistance * pixelScale.y;
    horizontalAxisDistance = view2DLayout.horizontalAxisLabelsDistance * pixelScale.y;
    
    if(isEmulGL) {
      verticalTickDistance /= pixelScale.x;
      verticalAxisDistance /= pixelScale.x;
      horizontalTickDistance /= pixelScale.y;
      horizontalAxisDistance /= pixelScale.y;
    }
    
    
    marginLeftPx += verticalTickDistance; // add tick label distance
    marginLeftPx += tickTextWidth; // add maximum Y tick label width
    marginLeftPx += verticalAxisDistance; // add axis label distance
    marginLeftPx += axisTextWidth; // add text width of axis label

    // add space for text to the bottom margin
    marginBottomPx += horizontalTickDistance;
    marginBottomPx += tickTextHeight;
    marginBottomPx += horizontalAxisDistance;
    marginBottomPx += axisTextHeight;
    

    // ---------------------------------------------------
    // case of a symetric layout requirement
    
    if (view2DLayout.isSymetricHorizontalMargin()) {
      marginRightPx = marginLeftPx;
    }

    if (view2DLayout.isSymetricVerticalMargin()) {
      marginTopPx = marginBottomPx;
    }

    // ------------------------------------------------
    // Hack to avoid covering colorbar with emulgl
    // EmulGL main viewport can not occupy anything else than full canvas,
    // so the chart view need to leave a white space on the right to be covered
    // by the colorbar displayed ON TOP.
    // In the native case, the colorbar is displayed ON THE RIGHT of the chart
    // view's viewport
    // ------------------------------------------------
    
    if (isEmulGL) {
      List<ILegend> legends = view.getChart().getScene().getGraph().getLegends();
      
      for(ILegend legend: legends) {
        legend.updateMinimumDimension(painter);
        Dimension minDim = legend.getMinimumDimension();
        marginRightPx += minDim.width;
      }
    }

    // ---------------------------------------------------
    // The actual processing of margin
    
    marginArea = new Area(marginLeftPx + marginRightPx, marginTopPx + marginBottomPx);
    screenArea = new Area(viewport.getWidth(), viewport.getHeight());
    
    
    // XY 2D view
    if(isXY()) {
      spaceArea = new Area(bounds.getXRange().getRange(), bounds.getYRange().getRange());
    }
    // XZ 2D view
    else if(isXZ()){
      spaceArea = new Area(bounds.getXRange().getRange(), bounds.getZRange().getRange());
    }
    // YX 2D view
    else if(isYZ()){
      spaceArea = new Area(bounds.getYRange().getRange(), bounds.getZRange().getRange());
    }
    else {
      throw new IllegalArgumentException("Irrelevant view mode : '" + view.getViewMode() + "'");
    }
    
    modelToScreen = getModelToScreenRatio(spaceArea, screenArea, marginArea);

    
    // convert pixel margin to world coordinate to add compute the additional 3D space to grasp with
    // the camera
    marginLeftModel = marginLeftPx * modelToScreen.x;
    marginRightModel = marginRightPx * modelToScreen.x;
    marginTopModel = marginTopPx * modelToScreen.y;
    marginBottomModel = marginBottomPx * modelToScreen.y;
  }

  protected boolean isYZ() {
    return ViewPositionMode.YZ.equals(view.getViewMode());
  }

  protected boolean isXZ() {
    return ViewPositionMode.XZ.equals(view.getViewMode());
  }

  protected boolean isXY() {
    return ViewPositionMode.TOP.equals(view.getViewMode());
  }



  /**
   * A helper for processing 2D margins.
   * 
   * This method process a model-to-screen ratio able to indicate which range of real world
   * coordinates will be mapped to a single pixel, both on the width and height of the viewport.
   * 
   * It is used to evaluate the camera settings to apply to a 2D projection to allow keeping a white
   * space for drawing 2D margins.
   * 
   * The intuition lead to consider that this ratio R should be :
   * 
   * <pre>
   * <code>
   * R.x = scene.bounds.xrange() / viewport.width 
   * R.y = scene.bounds.yrange() / viewport.height
   * </code>
   * </pre>
   * 
   * This would allow to compute an extra space to add to world coordinates specified in pixel
   * units.
   * 
   * However this ratio does not allow considering margins in world space AND screen space
   * consistently (which then leads to inconsistent layout). Our need then become to ensure this
   * ratio remains the same in 2D (pixel space) and 3D (scene space).
   * 
   * To explain the formulae, we assuming the following case of 1000px wide canvas showing a scene
   * with a range of 20 along the X dimension. The largest rectangle represents the canvas, the
   * smallest represents the scene. We show a left and right margin made of 100px each.
   * 
   * <pre>
   * <code>
   *  100px       800px        100px
   * |-----+------------------+-----|
   * |                              |
   * |     |------------------|     |
   * |     |                  |     |
   * |     |     xrange=20    |     |
   * |     |------------------|     |
   * |    x=-10              x=10   |
   * |-----+------------------+-----|
   * </code>
   * </pre>
   * 
   * Let's start with our naive ratio formulae
   * 
   * <pre>
   * <code>
   * R.x = scene.xrange() / viewport.width = 20/1000 = 2/100
   * </code>
   * </pre>
   * 
   * Margins in scene space (3D) should then be
   * 
   * <pre>
   * <code>
   * margin3D.x = margin2D.x * R.x = 200 * 2/100 = 4
   * </code>
   * </pre>
   * 
   * Hence the range to consider to keep a white space around the 3D scene is x:[-12;12] instead of
   * the initial x:[-10;10]
   * 
   * We now want to ensure that the margin ratio in screen space remains the same than the one in
   * scene space :
   * 
   * <pre>
   * <code>
   * scene.xrange() / sceneWithMargin.xrange() = screen.xrange() / screenWithMargin.xrange()
   * 
   * in our example : 
   * 20 / 24 = 800 / 1000
   * </code>
   * </pre>
   * 
   * This constraint is represented by the following equation
   * 
   * <pre>
   * <code>
   * 
   *         scene.xrange               screen.xrange - margin2D.x     
   * -------------------------------  = --------------------------
   * R.x * margin2D.x + scene.xrange          screen.xrange
   * 
   * </code>
   * </pre>
   *
   * Solving this equation to get R.x yield to
   * 
   * <pre>
   * <code>
   *   screen.xrange * scene.xrange
   * ( ---------------------------- - scene.xrange ) * (1/margin2D.x)
   *    screen.xrange - margin2D.x
   * </code>
   * </pre>
   * 
   * Reasoning is the same for the Y dimension.
   * 
   * NB : the ratio will be 1 in the case margin is 0 or if canvas size is equal to margin size.
   * 
   * 
   * @param space
   * @param canvas
   * @param margins
   * @return
   */
  public Coord2d getModelToScreenRatio(Area space, Area canvas, Area margins) {
    float x = 1;


    if (marginArea.width != 0 && (canvas.width != margins.width)) {
      x = (((space.width * canvas.width) / (canvas.width - margins.width)) - space.width)
          / margins.width;
    }

    float y = 1;

    if (marginArea.height != 0 && (canvas.height != margins.height)) {
      y = (((space.height * canvas.height) / (canvas.height - margins.height)) - space.height)
          / margins.height;

    }


    return new Coord2d(x, y);
  }

  // -------------------------------------
  //
  // RESULTS
  //
  // -------------------------------------

  /**
   * Return the overall margin that was processed at the latest call to {@link #apply()} according
   * to the axis and view layout settings
   */
  public Area getArea() {
    return new Area(marginArea);
  }

  /**
   * Return the tick label width in pixel that was processed at the latest call to {@link #apply()}
   * according to the axis and view layout settings
   */
  public float getTickTextWidth() {
    return tickTextWidth;
  }

  /**
   * Return the tick label height in pixel that was processed at the latest call to {@link #apply()}
   * according to the axis and view layout settings
   */
  public float getTickTextHeight() {
    return tickTextHeight;
  }

  /**
   * Return the axis label width in pixel that was processed at the latest call to {@link #apply()}
   * according to the axis and view layout settings
   */
  public float getAxisTextWidth() {
    return axisTextWidth;
  }

  /**
   * Return the axis label height in pixel that was processed at the latest call to {@link #apply()}
   * according to the axis and view layout settings
   */
  public float getAxisTextHeight() {
    return axisTextHeight;
  }

  /**
   * Return the model-to-screen ratio that that was processed at the latest call to {@link #apply()}
   */
  public Coord2d getModelToScreen() {
    return modelToScreen;
  }
  
  /** Return the margin in pixels, as defined in settings */
  public Margin getMarginPx() {
    return new Margin(marginLeftPx, marginRightPx, marginTopPx, marginBottomPx);
  }

  /** Return the margin in pixels, already scaled according to pixel scale */
  public Margin getMarginPxScaled() {
    return new Margin(marginLeftPxScaled, marginRightPxScaled, marginTopPxScaled, marginBottomPxScaled);
  }

  /** Return the distance of tick labels to axis, already scaled according to pixel scale */
  public float getHorizontalTickDistance() {
    return horizontalTickDistance;
  }

  /** Return the distance of axis label to tick labels, already scaled according to pixel scale */
  public float getHorizontalAxisDistance() {
    return horizontalAxisDistance;
  }

  /** Return the distance of tick labels to axis, already scaled according to pixel scale */
  public float getVerticalTickDistance() {
    return verticalTickDistance;
  }

  /** Return the distance of axis label to tick labels, already scaled according to pixel scale */
  public float getVerticalAxisDistance() {
    return verticalAxisDistance;
  }

  public View getView() {
    return view;
  }

  public void setView(View view) {
    this.view = view;
  }
}
