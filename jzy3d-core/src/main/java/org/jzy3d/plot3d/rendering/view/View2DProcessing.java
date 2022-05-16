package org.jzy3d.plot3d.rendering.view;

import org.jzy3d.maths.Area;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.AxisLabelProcessor;
import org.jzy3d.plot3d.primitives.axis.AxisTickProcessor;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;

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
  protected float tickTextHorizontal = 0;
  protected float tickTextVertical = 0;
  protected float axisTextHorizontal = 0;
  protected float axisTextVertical = 0;

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
  protected Area margin;

  protected Coord2d modelToScreen;



  public View2DProcessing(View view) {
    super();
    this.view = view;
  }

  /**
   * Apply current view's {@link View2DLayout} and process all margins in 3D.
   * 
   * 
   * 
   */
  public void apply(ViewportConfiguration viewport, BoundingBox3d bounds) {
    IPainter painter = view.getPainter();
    View2DLayout view2DLayout = view.get2DLayout();
    AxisLayout axisLayout = view.getAxis().getLayout();
    Font font = axisLayout.getFont();


    // initialize all margins according to configuration
    marginLeftPx = view2DLayout.marginLeft;
    marginRightPx = view2DLayout.marginRight;
    marginTopPx = view2DLayout.marginTop;
    marginBottomPx = view2DLayout.marginBottom;

    // compute pixel occupation of ticks and axis labels

    if (view2DLayout.textAddMargin) {

      // consider space occupied by tick labels
      tickTextHorizontal = axisLayout.getMaxYTickLabelWidth(painter);
      tickTextVertical = font.getHeight();


      // consider space occupied by the Y axis label
      if (LabelOrientation.HORIZONTAL.equals(axisLayout.getYAxisLabelOrientation())) {
        // horizontal Y axis involves considering the axis label width
        axisTextHorizontal = painter.getTextLengthInPixels(font, axisLayout.getYAxisLabel());
      } else {
        // vertical Y axis involves considering the axis label font height
        axisTextHorizontal = font.getHeight();
      }

      // consider space occupied by the X axis label, which is always horizontal
      axisTextVertical = font.getHeight();
    }
    else {
      tickTextHorizontal = 0;
      tickTextVertical = 0;
      axisTextHorizontal = 0;
      axisTextVertical = 0;
    }

    // add space for text to the left margin
    marginLeftPx += view2DLayout.yTickLabelsDistance; // add tick label distance
    marginLeftPx += tickTextHorizontal; // add maximum Y tick label width
    marginLeftPx += view2DLayout.yAxisLabelsDistance; // add axis label distance
    marginLeftPx += axisTextHorizontal; // add text width of axis label

    // add space for text to the bottom margin
    marginBottomPx += view2DLayout.xTickLabelsDistance;
    marginBottomPx += tickTextVertical;
    marginBottomPx += view2DLayout.xAxisLabelsDistance;
    marginBottomPx += axisTextVertical;

    if (view2DLayout.isSymetricHorizontalMargin()) {
      marginRightPx = marginLeftPx;
    }

    if (view2DLayout.isSymetricVerticalMargin()) {
      marginTopPx = marginBottomPx;
    }


    margin = new Area(marginLeftPx + marginRightPx, marginTopPx + marginBottomPx);

    //bounds = view.getSceneGraphBounds();
    modelToScreen = getModelToScreenRatio(bounds, viewport, margin);


    // convert pixel margin to world coordinate to add compute the additional 3D space to grasp with
    // the camera
    marginLeftModel = marginLeftPx * modelToScreen.x;
    marginRightModel = marginRightPx * modelToScreen.x;
    marginTopModel = marginTopPx * modelToScreen.y;
    marginBottomModel = marginBottomPx * modelToScreen.y;
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
    
    
    if(margin.width!=0 && (canvas.width!=margins.width)){
      x = (((space.width * canvas.width) / (canvas.width - margins.width)) - space.width)
          / margins.width;
    }
    
    float y = 1;
    
    if(margin.height!=0 && (canvas.height!=margins.height)){
      y = (((space.height * canvas.height) / (canvas.height - margins.height)) - space.height)
          / margins.height;

    }
    
    
    return new Coord2d(x, y);
  }

  /**
   * @see {@link #getModelToScreenRatio(Area, Area, Area)}
   */
  public Coord2d getModelToScreenRatio(BoundingBox3d bounds, ViewportConfiguration viewport,
      Area margin) {
    Area space = new Area(bounds.getXRange().getRange(), bounds.getYRange().getRange());
    Area screen = new Area(viewport.getWidth(), viewport.getHeight());
    return getModelToScreenRatio(space, screen, margin);
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
  public Area getMargin() {
    return new Area(margin);
  }

  /**
   * Return the tick label width in pixel that was processed at the latest call to {@link #apply()}
   * according to the axis and view layout settings
   */
  public float getTickTextHorizontal() {
    return tickTextHorizontal;
  }

  /**
   * Return the tick label height in pixel that was processed at the latest call to {@link #apply()}
   * according to the axis and view layout settings
   */
  public float getTickTextVertical() {
    return tickTextVertical;
  }

  /**
   * Return the axis label width in pixel that was processed at the latest call to {@link #apply()}
   * according to the axis and view layout settings
   */
  public float getAxisTextHorizontal() {
    return axisTextHorizontal;
  }

  /**
   * Return the axis label height in pixel that was processed at the latest call to {@link #apply()}
   * according to the axis and view layout settings
   */
  public float getAxisTextVertical() {
    return axisTextVertical;
  }

  /**
   * Return the model-to-screen ratio that that was processed at the latest call to {@link #apply()}
   */
  public Coord2d getModelToScreen() {
    return modelToScreen;
  }

}
