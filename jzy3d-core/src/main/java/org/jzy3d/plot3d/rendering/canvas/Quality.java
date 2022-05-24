package org.jzy3d.plot3d.rendering.canvas;

import java.awt.Canvas;
import org.jzy3d.chart.IAnimator;
import org.jzy3d.painters.ColorModel;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.plot3d.rendering.view.View;


/**
 * Provides a structure for setting the rendering quality, i.e., the tradeoff between computation
 * speed, and graphic quality. Following mode have an impact on the way the {@link View} makes its
 * GL2 initialization. The {@link Quality} may also activate an {@link AbstractOrderingStrategy}
 * algorithm that enables clean alpha results.
 * 
 * <ul>
 * <li>Fastest: No transparency, no color shading, just handle depth buffer.
 * <li>Intermediate: include Fastest mode abilities, Color shading, mainly usefull to have
 * interpolated colors on polygons.
 * <li>Advanced: include Intermediate mode abilities, Transparency (GL2 alpha blending + polygon
 * ordering in scene graph)
 * <li>Nicest: include Advanced mode abilities, Anti aliasing on wires
 * </ul>
 * 
 * Toggling rendering model: one may either choose to have a repaint-on-demand or
 * repaint-continuously model. Setting isAnimated(false) will desactivate the {@link IAnimator}
 * updating the choosen {@link ICanvas} implementation.
 * 
 * setAutoSwapBuffer(false) will configure the {@link ICanvas}.
 * 
 * @author Martin Pernollet
 */
public class Quality {

  /**
   * Enables alpha, color interpolation and antialiasing on lines, points, and polygons.
   */
  public static final Quality Nicest() {
    return Nicest.clone().setHiDPIEnabled(true).setAnimated(false);
  }

  /**
   * Enables alpha and color interpolation. Set HiDPI on by default
   */
  public static final Quality Advanced() {
    return Advanced.clone().setHiDPIEnabled(true).setAnimated(false);
  }

  /**
   * Enables color interpolation. Keep HiDPI off by default
   */
  public static final Quality Intermediate() {
    return Intermediate.clone().setAnimated(false);
  }

  /**
   * Minimal quality to allow fastest rendering (no alpha, interpolation or antialiasing).
   */
  public static final Quality Fastest() {
    return Fastest.clone();
  }

  protected static final Quality Nicest = new Quality(true, true, true, true, true, true, true);
  protected static final Quality Advanced =
      new Quality(true, true, true, false, false, false, true);
  protected static final Quality Intermediate =
      new Quality(true, false, true, false, false, false, true);
  protected static final Quality Fastest =
      new Quality(true, false, false, false, false, false, true);

  // ****************************************************************** //

  protected boolean depthActivated;
  protected boolean alphaActivated;
  protected boolean smoothColor;
  protected boolean smoothPoint;
  protected boolean smoothLine;
  protected boolean smoothPolygon;
  protected boolean disableDepthTestWhenAlpha;
  protected boolean isAnimated = true;
  protected boolean isAutoSwapBuffer = true;

  protected boolean preserveViewportSize = DEFAULT_PRESERVE_VIEWPORT;

  public static boolean DEFAULT_PRESERVE_VIEWPORT = true;


  /** Initialize a Quality configuration for a View. */
  public Quality(boolean depthActivated, boolean alphaActivated, boolean smoothColor,
      boolean smoothPoint, boolean smoothLine, boolean smoothPolygon,
      boolean disableDepthTestWhenAlphaActivated) {
    this.depthActivated = depthActivated;
    this.alphaActivated = alphaActivated;
    this.smoothColor = smoothColor;
    this.smoothPoint = smoothPoint;
    this.smoothLine = smoothLine;
    this.smoothPolygon = smoothPolygon;
    this.disableDepthTestWhenAlpha = disableDepthTestWhenAlphaActivated;
  }

  public boolean isDepthActivated() {
    return depthActivated;
  }

  public Quality setDepthActivated(boolean depthActivated) {
    this.depthActivated = depthActivated;
    return this;
  }

  public boolean isAlphaActivated() {
    return alphaActivated;
  }

  public Quality setAlphaActivated(boolean alphaActivated) {
    this.alphaActivated = alphaActivated;
    return this;
  }

  public boolean isSmoothColor() {
    return smoothColor;
  }

  public Quality setSmoothColor(boolean smoothColor) {
    this.smoothColor = smoothColor;
    return this;
  }

  public boolean isSmoothLine() {
    return smoothLine;
  }

  public Quality setSmoothEdge(boolean smoothLine) {
    this.smoothLine = smoothLine;
    return this;
  }

  public boolean isSmoothPoint() {
    return smoothPoint;
  }

  public Quality setSmoothPoint(boolean smoothPoint) {
    this.smoothPoint = smoothPoint;
    return this;
  }

  public boolean isSmoothPolygon() {
    return smoothPolygon;
  }

  public Quality setSmoothPolygon(boolean smoothPolygon) {
    this.smoothPolygon = smoothPolygon;
    return this;
  }

  public boolean isDisableDepthBufferWhenAlpha() {
    return disableDepthTestWhenAlpha;
  }

  public Quality setDisableDepthBufferWhenAlpha(boolean disableDepthBufferWhenAlpha) {
    this.disableDepthTestWhenAlpha = disableDepthBufferWhenAlpha;
    return this;
  }

  public boolean isAnimated() {
    return isAnimated;
  }

  public Quality setAnimated(boolean isAnimated) {
    this.isAnimated = isAnimated;
    return this;
  }

  public boolean isAutoSwapBuffer() {
    return isAutoSwapBuffer;
  }

  public Quality setAutoSwapBuffer(boolean isAutoSwapBuffer) {
    this.isAutoSwapBuffer = isAutoSwapBuffer;
    return this;
  }

  /**
   * Used by a {@link Canvas} to setup pixel ratio. This might be used to avoid mouse pointer errors
   * on Retina display as most canvas implementation will perform :
   * 
   * <code>
   * if(quality.isPreserveViewportSize())
          setPixelScale(new float[] { ScalableSurface.IDENTITY_PIXELSCALE, ScalableSurface.IDENTITY_PIXELSCALE });
   *  </<code>
   * 
   */
  public boolean isPreserveViewportSize() {
    return preserveViewportSize;
  }

  public Quality setPreserveViewportSize(boolean preserveViewportSize) {
    this.preserveViewportSize = preserveViewportSize;
    return this;
  }

  public boolean isHiDPIEnabled() {
    return !isPreserveViewportSize();
  }

  public HiDPI getHiDPI() {
    return isHiDPIEnabled()?HiDPI.ON:HiDPI.OFF;
  }

  /**
   * If true, states that the chart should make use of HiDPI or Retina capabilities to draw more
   * good looking charts due to higher number of physical pixels.
   * 
   * A convenient shortcut to <code>
   * setPreserveViewportSize(!hidpi)
   * </code>
   * 
   * @param hidpi
   */
  public Quality setHiDPIEnabled(boolean hidpi) {
    return setPreserveViewportSize(!hidpi);
  }

  public Quality setHiDPI(HiDPI hidpi) {
    return setHiDPIEnabled(HiDPI.ON.equals(hidpi));
  }
  
  public ColorModel getColorModel() {
    if(smoothColor) {
      return ColorModel.SMOOTH;
    }
    else {
      return ColorModel.FLAT;
    }

  }
  
  public void setColorModel(ColorModel model) {
    if(ColorModel.SMOOTH.equals(model)) {
      smoothColor = true;
    }
    else if(ColorModel.FLAT.equals(model)) {
      smoothColor = false;
    }
  }
  
  public Quality clone() {
    Quality copy = new Quality(depthActivated, alphaActivated, smoothColor, smoothPoint, smoothLine,
        smoothPolygon, disableDepthTestWhenAlpha);
    copy.isAnimated = isAnimated;
    copy.isAutoSwapBuffer = isAutoSwapBuffer;
    return copy;
  }



}
