package org.jzy3d.plot3d.primitives.axis.layout;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.axis.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.providers.SmartTickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.DefaultDecimalTickRenderer;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.view.HiDPI;


public class AxisLayout implements IAxisLayout {
  protected boolean tickLineDisplayed = true;


  protected Font font = FONT_DEFAULT;
  protected Font fontNoHiDPI = Font.Helvetica_12;
  protected Font fontHiDPI = Font.Helvetica_18;

  
  protected Font fontMajorHiDPI = Font.Helvetica_18;
  protected Font fontMinorHiDPI = Font.Helvetica_12;
  protected Font fontMajorNoHiDPI = Font.Helvetica_12;
  protected Font fontMinorNoHiDPI = Font.Helvetica_10;

  // protected Font hiDPIMajourFont

  protected LabelOrientation xAxisLabelOrientation;
  protected LabelOrientation yAxisLabelOrientation;
  protected LabelOrientation zAxisLabelOrientation;
  protected String xAxeLabel;
  protected String yAxeLabel;
  protected String zAxeLabel;
  protected boolean xAxeLabelDisplayed;
  protected boolean yAxeLabelDisplayed;
  protected boolean zAxeLabelDisplayed;

  protected double xTicks[];
  protected double yTicks[];
  protected double zTicks[];

  protected ITickProvider xTickProvider;
  protected ITickProvider yTickProvider;
  protected ITickProvider zTickProvider;

  protected ITickRenderer xTickRenderer;
  protected ITickRenderer yTickRenderer;
  protected ITickRenderer zTickRenderer;

  protected Color xTickColor;
  protected Color yTickColor;
  protected Color zTickColor;

  protected boolean xTickLabelDisplayed;
  protected boolean yTickLabelDisplayed;
  protected boolean zTickLabelDisplayed;

  protected boolean faceDisplayed;

  protected Color quadColor;
  protected Color gridColor;

  protected double lastXmin = Float.NaN;
  protected double lastXmax = Float.NaN;
  protected double lastYmin = Float.NaN;
  protected double lastYmax = Float.NaN;
  protected double lastZmin = Float.NaN;
  protected double lastZmax = Float.NaN;

  protected Color mainColor;

  protected ZAxisSide zAxisSide = ZAxisSide.RIGHT;
  



  /** Default AxeBox layout */
  public AxisLayout() {
    setXAxisLabel("X");
    setYAxisLabel("Y");
    setZAxisLabel("Z");

    setXAxeLabelDisplayed(true);
    setYAxeLabelDisplayed(true);
    setZAxeLabelDisplayed(true);

    setXTickProvider(new SmartTickProvider(5));
    setYTickProvider(new SmartTickProvider(5));
    setZTickProvider(new SmartTickProvider(6));

    setXTickRenderer(new DefaultDecimalTickRenderer(4));
    setYTickRenderer(new DefaultDecimalTickRenderer(4));
    setZTickRenderer(new DefaultDecimalTickRenderer(6));

    setFaceDisplayed(false);
    setXTickLabelDisplayed(true);
    setYTickLabelDisplayed(true);
    setZTickLabelDisplayed(true);

    setMainColor(Color.BLACK);

    setZAxisSide(ZAxisSide.LEFT);
  }

  @Override
  public void setMainColor(Color color) {
    mainColor = color;
    setXTickColor(color);
    setYTickColor(color);
    setZTickColor(color);
    setGridColor(color);
    setQuadColor(color.negative());
  }

  @Override
  public Color getMainColor() {
    return mainColor;
  }

  @Override
  public double[] getXTicks(double min, double max) {
    lastXmin = min;
    lastXmax = max;
    xTicks = xTickProvider.generateTicks(min, max);
    return xTicks;
  }

  @Override
  public double[] getYTicks(double min, double max) {
    lastYmin = min;
    lastYmax = max;
    yTicks = yTickProvider.generateTicks(min, max);
    return yTicks;
  }

  @Override
  public double[] getZTicks(double min, double max) {
    lastZmin = min;
    lastZmax = max;
    zTicks = zTickProvider.generateTicks(min, max);
    return zTicks;
  }

  @Override
  public String getXAxisLabel() {
    return xAxeLabel;
  }

  @Override
  public void setXAxisLabel(String axeLabel) {
    xAxeLabel = axeLabel;
  }

  @Override
  public String getYAxisLabel() {
    return yAxeLabel;
  }

  @Override
  public void setYAxisLabel(String axeLabel) {
    yAxeLabel = axeLabel;
  }

  @Override
  public String getZAxisLabel() {
    return zAxeLabel;
  }

  @Override
  public void setZAxisLabel(String axeLabel) {
    zAxeLabel = axeLabel;
  }

  @Override
  public double[] getXTicks() {
    return xTicks;
  }

  @Override
  public double[] getYTicks() {
    return yTicks;
  }

  @Override
  public double[] getZTicks() {
    return zTicks;
  }

  @Override
  public ITickProvider getXTickProvider() {
    return xTickProvider;
  }

  @Override
  public void setXTickProvider(ITickProvider tickProvider) {
    xTickProvider = tickProvider;

    if (lastXmin != Float.NaN) // update ticks if we can
      getXTicks(lastXmin, lastXmax);
  }

  @Override
  public ITickProvider getYTickProvider() {
    return yTickProvider;
  }

  @Override
  public void setYTickProvider(ITickProvider tickProvider) {
    yTickProvider = tickProvider;

    if (lastYmin != Float.NaN) // update ticks if we can
      getYTicks(lastYmin, lastYmax);
  }

  @Override
  public ITickProvider getZTickProvider() {
    return zTickProvider;
  }

  @Override
  public void setZTickProvider(ITickProvider tickProvider) {
    zTickProvider = tickProvider;

    if (lastZmin != Float.NaN) // update ticks if we can
      getZTicks(lastZmin, lastZmax);
  }

  @Override
  public ITickRenderer getXTickRenderer() {
    return xTickRenderer;
  }

  @Override
  public void setXTickRenderer(ITickRenderer tickRenderer) {
    xTickRenderer = tickRenderer;
  }

  @Override
  public ITickRenderer getYTickRenderer() {
    return yTickRenderer;
  }

  @Override
  public void setYTickRenderer(ITickRenderer tickRenderer) {
    yTickRenderer = tickRenderer;
  }

  @Override
  public ITickRenderer getZTickRenderer() {
    return zTickRenderer;
  }

  @Override
  public void setZTickRenderer(ITickRenderer tickRenderer) {
    zTickRenderer = tickRenderer;
  }

  @Override
  public Color getXTickColor() {
    return xTickColor;
  }

  @Override
  public void setXTickColor(Color tickColor) {
    xTickColor = tickColor;
  }

  @Override
  public Color getYTickColor() {
    return yTickColor;
  }

  @Override
  public void setYTickColor(Color tickColor) {
    yTickColor = tickColor;
  }

  @Override
  public Color getZTickColor() {
    return zTickColor;
  }

  @Override
  public void setZTickColor(Color tickColor) {
    zTickColor = tickColor;
  }

  @Override
  public boolean isFaceDisplayed() {
    return faceDisplayed;
  }

  @Override
  public void setFaceDisplayed(boolean faceDisplayed) {
    this.faceDisplayed = faceDisplayed;
  }

  @Override
  public Color getQuadColor() {
    return quadColor;
  }

  @Override
  public void setQuadColor(Color quadColor) {
    this.quadColor = quadColor;
  }

  @Override
  public Color getGridColor() {
    return gridColor;
  }

  @Override
  public void setGridColor(Color gridColor) {
    this.gridColor = gridColor;
  }

  @Override
  public boolean isXAxeLabelDisplayed() {
    return xAxeLabelDisplayed;
  }

  @Override
  public void setXAxeLabelDisplayed(boolean axeLabelDisplayed) {
    xAxeLabelDisplayed = axeLabelDisplayed;
  }

  @Override
  public boolean isYAxeLabelDisplayed() {
    return yAxeLabelDisplayed;
  }

  @Override
  public void setYAxeLabelDisplayed(boolean axeLabelDisplayed) {
    yAxeLabelDisplayed = axeLabelDisplayed;
  }

  @Override
  public boolean isZAxeLabelDisplayed() {
    return zAxeLabelDisplayed;
  }

  @Override
  public void setZAxeLabelDisplayed(boolean axeLabelDisplayed) {
    zAxeLabelDisplayed = axeLabelDisplayed;
  }

  @Override
  public boolean isXTickLabelDisplayed() {
    return xTickLabelDisplayed;
  }

  @Override
  public void setXTickLabelDisplayed(boolean tickLabelDisplayed) {
    xTickLabelDisplayed = tickLabelDisplayed;
  }

  @Override
  public boolean isYTickLabelDisplayed() {
    return yTickLabelDisplayed;
  }

  @Override
  public void setYTickLabelDisplayed(boolean tickLabelDisplayed) {
    yTickLabelDisplayed = tickLabelDisplayed;
  }

  @Override
  public boolean isZTickLabelDisplayed() {
    return zTickLabelDisplayed;
  }

  @Override
  public void setZTickLabelDisplayed(boolean tickLabelDisplayed) {
    zTickLabelDisplayed = tickLabelDisplayed;
  }

  @Override
  public boolean isTickLineDisplayed() {
    return tickLineDisplayed;
  }

  @Override
  public void setTickLineDisplayed(boolean tickLineDisplayed) {
    this.tickLineDisplayed = tickLineDisplayed;
  }

  @Override
  public ZAxisSide getZAxisSide() {
    return zAxisSide;
  }

  @Override
  public void setZAxisSide(ZAxisSide zAxisSide) {
    this.zAxisSide = zAxisSide;
  }


  @Override
  public Font getFont() {
    return font;
  }

  @Override
  public void setFont(Font font) {
    this.font = font;
  }

  /**
   * Get registered font according to conditions
   * 
   * @param type the major/minor font case
   * @param hidpi the HiDPI context for this font, allowing to define bigger fonts in case screen
   *        resolution is high (and text small)
   */
  @Override
  public Font getFont(FontType type, HiDPI hidpi) {
    if (type == null) {
      // most frequent case so keep first in this selection
      if (HiDPI.ON.equals(hidpi)) {
        return fontMajorHiDPI;
      } else if (HiDPI.OFF.equals(hidpi)) {
        return fontMajorNoHiDPI;
      }
    } else if (FontType.Major.equals(type)) {
      if (HiDPI.ON.equals(hidpi)) {
        return fontMajorHiDPI;
      } else if (HiDPI.OFF.equals(hidpi)) {
        return fontMajorNoHiDPI;
      }
    } else if (FontType.Minor.equals(type)) {
      if (HiDPI.ON.equals(hidpi)) {
        return fontMinorHiDPI;
      } else if (HiDPI.OFF.equals(hidpi)) {
        return fontMinorNoHiDPI;
      }
    }
    return null;
    // return font;
  }
  
  


  /**
   * Get font according to a given context
   * 
   * @param font the font to use for the context
   * @param type the major/minor font possibilites that a drawable or colorbar may use
   * @param hidpi the HiDPI context for this font, allowing to define bigger fonts in case screen
   *        resolution is high (and text small)
   */
  @Override
  public void setFont(Font font, FontType type, HiDPI hidpi) {
    if (FontType.Major.equals(type)) {
      if (HiDPI.ON.equals(hidpi)) {
        fontMajorHiDPI = font;
      } else if (HiDPI.OFF.equals(hidpi)) {
        fontMajorNoHiDPI = font;
      }
    } else if (FontType.Minor.equals(type)) {
      if (HiDPI.ON.equals(hidpi)) {
        fontMinorHiDPI = font;
      } else if (HiDPI.OFF.equals(hidpi)) {
        fontMinorNoHiDPI = font;
      }
    }
  }

  @Override
  public Font getFont(HiDPI hidpi) {
    if (HiDPI.ON.equals(hidpi)) {
      return fontHiDPI;
    } else if (HiDPI.OFF.equals(hidpi)) {
      return fontNoHiDPI;
    }
    return font;
  }

  @Override
  public void setFont(Font font, HiDPI hidpi) {
    if (HiDPI.ON.equals(hidpi)) {
      fontHiDPI = font;
    } else if (HiDPI.OFF.equals(hidpi)) {
      fontNoHiDPI = font;
    }
  }

  @Override
  public LabelOrientation getXAxisLabelOrientation() {
    return xAxisLabelOrientation;
  }

  @Override
  public void setXAxisLabelOrientation(LabelOrientation xAxisLabelOrientation) {
    this.xAxisLabelOrientation = xAxisLabelOrientation;
  }

  @Override
  public LabelOrientation getYAxisLabelOrientation() {
    return yAxisLabelOrientation;
  }

  @Override
  public void setYAxisLabelOrientation(LabelOrientation yAxisLabelOrientation) {
    this.yAxisLabelOrientation = yAxisLabelOrientation;
  }

  @Override
  public LabelOrientation getzAxisLabelOrientation() {
    return zAxisLabelOrientation;
  }

  @Override
  public void setZAxisLabelOrientation(LabelOrientation zAxisLabelOrientation) {
    this.zAxisLabelOrientation = zAxisLabelOrientation;
  }
  
  
}
