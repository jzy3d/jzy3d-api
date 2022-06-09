package org.jzy3d.plot3d.rendering.view;

import org.apache.commons.collections4.Get;
import org.jzy3d.maths.Margin;

/**
 * Allows configuring the layout of a view when the chart enters a 2D rendering mode.
 * 
 * <img src="doc-files/layout2D.png"/>
 */
public class View2DLayout {
  protected View view;

  protected boolean textAddMargin = true;

  /**
   * When true, the global left margin (including text) will equal the global right margin. Allows
   * the embedding axis to appear horizontally centered in the canvas.
   */
  protected boolean symetricHorizontalMargin = false;

  /**
   * When true, the global left margin (including text) will equal the global right margin. Allows
   * the embedding axis to appear horizontally centered in the canvas.
   */
  protected boolean symetricVerticalMargin = false;

  /** Distance between axis and tick labels (hence, length of the tick) */
  protected float xTickLabelsDistance = 0;
  /** Distance between tick labels and axis label */
  protected float xAxisLabelsDistance = 0;
  /** Distance between axis and tick labels (hence, length of the tick) */
  protected float yTickLabelsDistance = 0;
  /** Distance between tick labels and axis label */
  protected float yAxisLabelsDistance = 0;

  protected Margin margin = new Margin();


  public View2DLayout(View view) {
    this.view = view;

    setMarginHorizontal(10);
    setMarginVertical(10);
    setTickLabelDistance(10);
    setAxisLabelDistance(10);
  }
  
  public void setSymetricMargin(boolean symetricMargin) {
    setSymetricHorizontalMargin(symetricMargin);
    setSymetricVerticalMargin(symetricMargin);
  }


  public void setTickLabelDistance(float dist) {
    setxTickLabelsDistance(dist);
    setyTickLabelsDistance(dist);
  }

  public void setAxisLabelDistance(float dist) {
    setxAxisLabelsDistance(dist);
    setyAxisLabelsDistance(dist);
  }

  /**
   * A convenient shortcut to set the same margin to left, right, bottom and right canvas borders.
   * 
   * @see {@link #getMargin()}
   */
  public void setMargin(int margin) {
    setMarginHorizontal(margin);
    setMarginVertical(margin);
  }

  /**
   * A convenient shortcut to set the same margin to left and right canvas borders.
   * 
   * @see {@link #getMargin()}
   */
  public void setMarginHorizontal(int margin) {
    this.margin.setWidth(margin*2);
  }

  /**
   * A convenient shortcut to set the same margin to top and bottom canvas borders.
   * 
   * @see {@link #getMargin()}
   */
  public void setMarginVertical(int margin) {
    this.margin.setHeight(margin*2);
  }


  public Margin getMargin() {
    return margin;
  }

  public void setMargin(Margin margin) {
    this.margin = margin;
  }
  
  public boolean isTextAddMargin() {
    return textAddMargin;
  }

  /** Only usefull for debugging purpose, should not be used. */
  public void setTextAddMargin(boolean keepTextVisible) {
    this.textAddMargin = keepTextVisible;
  }



  public float getxTickLabelsDistance() {
    return xTickLabelsDistance;
  }

  public void setxTickLabelsDistance(float xAxisTickLabelsDistance) {
    this.xTickLabelsDistance = xAxisTickLabelsDistance;
  }

  public float getxAxisLabelsDistance() {
    return xAxisLabelsDistance;
  }

  /** Distance between tick labels and axis label */
  public void setxAxisLabelsDistance(float xAxisNameLabelsDistance) {
    this.xAxisLabelsDistance = xAxisNameLabelsDistance;
  }

  public float getyTickLabelsDistance() {
    return yTickLabelsDistance;
  }

  public void setyTickLabelsDistance(float yAxisTickLabelsDistance) {
    this.yTickLabelsDistance = yAxisTickLabelsDistance;
  }

  public float getyAxisLabelsDistance() {
    return yAxisLabelsDistance;
  }

  /** Distance between tick labels and axis label */
  public void setyAxisLabelsDistance(float yAxisNameLabelsDistance) {
    this.yAxisLabelsDistance = yAxisNameLabelsDistance;
  }
  
  public boolean isSymetricHorizontalMargin() {
    return symetricHorizontalMargin;
  }

  public void setSymetricHorizontalMargin(boolean symetricHorizontalMargin) {
    this.symetricHorizontalMargin = symetricHorizontalMargin;
  }

  public boolean isSymetricVerticalMargin() {
    return symetricVerticalMargin;
  }

  public void setSymetricVerticalMargin(boolean symetricVerticalMargin) {
    this.symetricVerticalMargin = symetricVerticalMargin;
  }

  public void apply() {
    view.getChart().render();
  }
}
