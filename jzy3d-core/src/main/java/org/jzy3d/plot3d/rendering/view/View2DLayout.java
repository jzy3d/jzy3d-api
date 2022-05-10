package org.jzy3d.plot3d.rendering.view;

public class View2DLayout {
  protected float marginLeft = 0;
  protected float marginRight = 0;
  protected float marginTop = 0;      
  protected float marginBottom = 0; 
  
  protected boolean keepTextVisible = true;
  
  public View2DLayout() {
    setMarginHorizontal(10);
    setMarginVertical(10);
  }

  public void setMarginHorizontal(float margin) {
    setMarginLeft(margin);
    setMarginRight(margin);
  }
  
  public void setMarginVertical(float margin) {
    setMarginTop(margin);
    setMarginBottom(margin);
  }
  
  public float getMarginLeft() {
    return marginLeft;
  }

  public void setMarginLeft(float marginLeft) {
    this.marginLeft = marginLeft;
  }

  public float getMarginRight() {
    return marginRight;
  }

  public void setMarginRight(float marginRight) {
    this.marginRight = marginRight;
  }

  public float getMarginTop() {
    return marginTop;
  }

  public void setMarginTop(float marginTop) {
    this.marginTop = marginTop;
  }

  public float getMarginBottom() {
    return marginBottom;
  }

  public void setMarginBottom(float marginBottom) {
    this.marginBottom = marginBottom;
  }

  public boolean isKeepTextVisible() {
    return keepTextVisible;
  }

  public void setKeepTextVisible(boolean keepTextVisible) {
    this.keepTextVisible = keepTextVisible;
  }

  
}
