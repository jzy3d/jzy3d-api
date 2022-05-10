package org.jzy3d.plot3d.rendering.view;


/**
 * <img src="doc-files/layout2D.png"/> 
 * 
 * // PB1 : avec 200 ou 500 samples, view2D calcule une camera qui n'affiche rien
 * 
 * 
 * DONE : changement 2D/3D applique la modif immédiatement, même si animation stop
 * 
 * ------------------------
 * DONE : axe en double sur emulGL, ajoute un fix sur le delete texte (should delete image also if ledend)
 * 
 * ------------------------
 * DONE : configurer la vue avec des paramètres 2D
 * 
 * TODO : considérer la taille des textes.
 * 
 * 
 * ------------------------
 * PB5 : native prend en compte la colorbar sur le côté, mais pas emulgl qui a un viewport qui s'étale sur toute la longueur.
 * 
 * >> soit on évite d'appliquer le stretch qui force la vue 2D à étaler jusqu'au bord de l'écran
 * >> soit on adapte NativeGL pour que la colorbar s'affiche au dessus comme EmulGL
 * >> soit on adapte EmulGL pour pouvoir composer le viewport avec la colorbar sur le côté, en s'appuyant sur la formule PIX x (bounds/canvas)
 *    c'est pratique de pouvoir stretch la 3D sans réfléchir à la position de la colorbar et avoir garantie de non débordement.
 * >> soit on adapte EmuLGL pour prendre des bounds plus grand qui vont permettre de créer un décallage à droite
 * 
 *
 *
 * TO BE TESTED
 * - Y axis rotate / or not
 * - negative values
 * 
 * Take values with x range != y range, include negative and positive values
 * 
 *
 */
public class View2DLayout {
  protected View view;
  
  protected float marginLeft = 0;
  protected float marginRight = 0;
  protected float marginTop = 0;      
  protected float marginBottom = 0;
  
  protected float xAxisTickLabelsDistance = 0;
  protected float xAxisNameLabelsDistance = -1;

  protected float yAxisTickLabelsDistance = 0;
  protected float yAxisNameLabelsDistance = -1;

  protected boolean keepTextVisible = true;
  
  public View2DLayout(View view) {
    this.view = view;
    
    setMarginHorizontal(10);
    setMarginVertical(10);
    setTickLabelDistance(10);
  }
  
  public void setTickLabelDistance(float dist) {
    setxAxisTickLabelsDistance(dist);
    setyAxisTickLabelsDistance(dist);
  }

  public void setMargin(float margin) {
    setMarginHorizontal(margin);
    setMarginVertical(margin);
  }

  /**
   * Set the same margin to left and right canvas borders
   */
  public void setMarginHorizontal(float margin) {
    setMarginLeft(margin);
    setMarginRight(margin);
  }

  /**
   * Set the same margin to top and bottom canvas borders
   */
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
  
  

  public float getxAxisTickLabelsDistance() {
    return xAxisTickLabelsDistance;
  }

  public void setxAxisTickLabelsDistance(float xAxisTickLabelsDistance) {
    this.xAxisTickLabelsDistance = xAxisTickLabelsDistance;
  }

  public float getxAxisNameLabelsDistance() {
    return xAxisNameLabelsDistance;
  }

  public void setxAxisNameLabelsDistance(float xAxisNameLabelsDistance) {
    this.xAxisNameLabelsDistance = xAxisNameLabelsDistance;
  }

  public float getyAxisTickLabelsDistance() {
    return yAxisTickLabelsDistance;
  }

  public void setyAxisTickLabelsDistance(float yAxisTickLabelsDistance) {
    this.yAxisTickLabelsDistance = yAxisTickLabelsDistance;
  }

  public float getyAxisNameLabelsDistance() {
    return yAxisNameLabelsDistance;
  }

  public void setyAxisNameLabelsDistance(float yAxisNameLabelsDistance) {
    this.yAxisNameLabelsDistance = yAxisNameLabelsDistance;
  }

  public void apply() {
    view.getChart().render();
  }
}
