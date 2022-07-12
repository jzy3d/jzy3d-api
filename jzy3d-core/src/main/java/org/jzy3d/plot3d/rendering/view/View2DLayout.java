package org.jzy3d.plot3d.rendering.view;

import org.apache.commons.collections4.Get;
import org.jzy3d.maths.Margin;

/**
 * Allows configuring the layout of a view when the chart enters a 2D rendering mode.
 * 
 * <img src="doc-files/layout2D.png"/>
 */
public class View2DLayout {
  
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
  protected float horizontalTickLabelsDistance = 0;
  /** Distance between tick labels and axis label */
  protected float horizontalAxisLabelsDistance = 0;
  /** Distance between axis and tick labels (hence, length of the tick) */
  protected float verticalTickLabelsDistance = 0;
  /** Distance between tick labels and axis label */
  protected float verticalAxisLabelsDistance = 0;
  /** Distance between canvas content and canvas border (i.e. empty borders dimension) */
  protected Margin margin = new Margin();
  /** If true, reverse direction of horizontal axis */
  protected boolean horizontalAxisFlip = false;
  /** If true, reverse direction of vertical axis */  
  protected boolean verticalAxisFlip = false;


  public View2DLayout() {
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
    setHorizontalTickLabelsDistance(dist);
    setVerticalTickLabelsDistance(dist);
  }

  public void setAxisLabelDistance(float dist) {
    setHorizontalAxisLabelsDistance(dist);
    setVerticalAxisLabelsDistance(dist);
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

  public float getHorizontalTickLabelsDistance() {
    return horizontalTickLabelsDistance;
  }

  public void setHorizontalTickLabelsDistance(float horizontalAxisLabelsDistance) {
    this.horizontalTickLabelsDistance = horizontalAxisLabelsDistance;
  }

  public float getHorizontalAxisLabelsDistance() {
    return horizontalAxisLabelsDistance;
  }

  /** Distance between tick labels and axis label */
  public void setHorizontalAxisLabelsDistance(float horizontalAxisLabelsDistance) {
    this.horizontalAxisLabelsDistance = horizontalAxisLabelsDistance;
  }

  public float getVerticalTickLabelsDistance() {
    return verticalTickLabelsDistance;
  }

  public void setVerticalTickLabelsDistance(float verticalAxisLabelsDistance) {
    this.verticalTickLabelsDistance = verticalAxisLabelsDistance;
  }

  public float getVerticalAxisLabelsDistance() {
    return verticalAxisLabelsDistance;
  }

  /** Distance between tick labels and axis label */
  public void setVerticalAxisLabelsDistance(float verticalAxisLabelsDistance) {
    this.verticalAxisLabelsDistance = verticalAxisLabelsDistance;
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

  public boolean isNoAxisFlipped() {
    return !isHorizontalAxisFlip() && !isVerticalAxisFlip();
  }

  public boolean isBothAxisFlipped() {
    return isHorizontalAxisFlip() && isVerticalAxisFlip();
  }

  public boolean isHorizontalAxisFlipOnly() {
    return isHorizontalAxisFlip() && !isVerticalAxisFlip();
  }

  public boolean isVerticalAxisFlipOnly() {
    return !isHorizontalAxisFlip() && isVerticalAxisFlip();
  }

  public boolean isHorizontalAxisFlip() {
    return horizontalAxisFlip;
  }

  public void setHorizontalAxisFlip(boolean horizontalAxisFlip) {
    this.horizontalAxisFlip = horizontalAxisFlip;
  }

  public boolean isVerticalAxisFlip() {
    return verticalAxisFlip;
  }

  public void setVerticalAxisFlip(boolean verticalAxisFlip) {
    this.verticalAxisFlip = verticalAxisFlip;
  }

  public void setBothAxisFlip(boolean axisFlip) {
    setHorizontalAxisFlip(axisFlip);
    setVerticalAxisFlip(axisFlip);
  }

  public View2DLayout clone() {
    View2DLayout to = new View2DLayout();
    return copy(this, to);
  }

  public void applySettings(View2DLayout source) {
    copy(source, this);
  }

  protected View2DLayout copy(View2DLayout from, View2DLayout to) {
    
    to.setHorizontalAxisLabelsDistance(from.getHorizontalAxisLabelsDistance());
    to.setVerticalAxisLabelsDistance(from.getVerticalAxisLabelsDistance());

    to.setHorizontalTickLabelsDistance(from.getHorizontalTickLabelsDistance());
    to.setVerticalTickLabelsDistance(from.getVerticalTickLabelsDistance());

    to.setSymetricHorizontalMargin(from.isSymetricHorizontalMargin());
    to.setSymetricVerticalMargin(from.isSymetricVerticalMargin());
    
    to.setTextAddMargin(from.isTextAddMargin());
    
    to.setMargin(from.getMargin());

    to.setHorizontalAxisFlip(from.isHorizontalAxisFlip());
    to.setVerticalAxisFlip(from.isVerticalAxisFlip());

    return to;
  }
}
