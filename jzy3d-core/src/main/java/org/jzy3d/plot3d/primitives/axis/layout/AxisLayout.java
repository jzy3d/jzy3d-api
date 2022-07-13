package org.jzy3d.plot3d.primitives.axis.layout;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout.FontType;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.IFontSizePolicy;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.StaticFontSizePolicy;
import org.jzy3d.plot3d.primitives.axis.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.providers.SmartTickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.DefaultDecimalTickRenderer;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.view.HiDPI;


public class AxisLayout {
  public static enum FontType{
    Major,Minor
  }



  public static final Font FONT_DEFAULT = Font.Helvetica_12;

  protected IFontSizePolicy fontSizePolicy = new StaticFontSizePolicy();

  protected Font font = FONT_DEFAULT;

  protected Font fontMajorHiDPI = Font.Helvetica_18;
  protected Font fontMinorHiDPI = Font.Helvetica_12;
  protected Font fontMajorNoHiDPI = Font.Helvetica_12;
  protected Font fontMinorNoHiDPI = Font.Helvetica_10;

  // protected Font hiDPIMajourFont

  protected LabelOrientation xAxisLabelOrientation = LabelOrientation.HORIZONTAL;
  protected LabelOrientation yAxisLabelOrientation = LabelOrientation.HORIZONTAL;
  protected LabelOrientation zAxisLabelOrientation = LabelOrientation.HORIZONTAL;
  protected String xAxeLabel;
  protected String yAxeLabel;
  protected String zAxeLabel;
  protected boolean xAxeLabelDisplayed;
  protected boolean yAxeLabelDisplayed;
  protected boolean zAxeLabelDisplayed;

  protected boolean tickLineDisplayed = true;

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

  protected boolean axisLabelOffsetAuto = false;

  protected int axisLabelOffsetMargin = 0;

  protected float tickLengthRatio = 20f;

  protected float axisLabelDistance = 2.5f;

  /** Default AxeBox layout */
  public AxisLayout() {
    setXAxisLabel("X");
    setYAxisLabel("Y");
    setZAxisLabel("Z");

    setXAxisLabelDisplayed(true);
    setYAxisLabelDisplayed(true);
    setZAxisLabelDisplayed(true);

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

    setMainColor(Color.BLACK.clone());

    setZAxisSide(ZAxisSide.LEFT);
  }



  
  public void setMainColor(Color color) {
    mainColor = color;
    setXTickColor(color);
    setYTickColor(color);
    setZTickColor(color);
    setGridColor(color);
    setQuadColor(color.negative());
  }

  
  public Color getMainColor() {
    return mainColor;
  }

  /**
   * Return the maximum text length in pixel as displayed on screen, given the current ticks and
   * renderer
   */
  public int getMaxXTickLabelWidth(IPainter painter) {
    int maxWidth = 0;

    for (double t : getXTicks()) {
      String label = getXTickRenderer().format(t);

      int width = painter.getTextLengthInPixels(font, label);
      if (width > maxWidth) {
        maxWidth = width;
      }
    }
    return maxWidth;

  }
  
  public int getRightMostXTickLabelWidth(IPainter painter) {
    double t = getXTicks()[getXTicks().length-1];
    String label = getXTickRenderer().format(t);
    return painter.getTextLengthInPixels(font, label);

  }

  /**
   * Return the maximum text length in pixel as displayed on screen, given the current ticks and
   * renderer on the Y axis
   */
  public int getMaxYTickLabelWidth(IPainter painter) {
    int maxWidth = 0;

    for (double t : getYTicks()) {
      String label = getYTickRenderer().format(t);

      int width = painter.getTextLengthInPixels(font, label);
      if (width > maxWidth) {
        maxWidth = width;
      }
    }
    return maxWidth;

  }

  /**
   * Return the maximum text length in pixel as displayed on screen, given the current ticks and
   * renderer on the Z axis
   */
  public int getMaxZTickLabelWidth(IPainter painter) {
    int maxWidth = 0;

    for (double t : getZTicks()) {
      String label = getZTickRenderer().format(t);

      int width = painter.getTextLengthInPixels(font, label);
      if (width > maxWidth) {
        maxWidth = width;
      }
    }
    return maxWidth;

  }


  // ********************* TICKS PROPERTIES ************************ //


  
  public double[] getXTicks(double min, double max) {
    lastXmin = min;
    lastXmax = max;
    xTicks = xTickProvider.generateTicks(min, max);
    return xTicks;
  }

  
  public double[] getYTicks(double min, double max) {
    lastYmin = min;
    lastYmax = max;
    yTicks = yTickProvider.generateTicks(min, max);
    return yTicks;
  }

  
  public double[] getZTicks(double min, double max) {
    lastZmin = min;
    lastZmax = max;
    zTicks = zTickProvider.generateTicks(min, max);
    return zTicks;
  }

  
  public double[] getXTicks() {
    return xTicks;
  }

  
  public double[] getYTicks() {
    return yTicks;
  }

  
  public double[] getZTicks() {
    return zTicks;
  }

  
  public ITickProvider getXTickProvider() {
    return xTickProvider;
  }

  
  public void setXTickProvider(ITickProvider tickProvider) {
    xTickProvider = tickProvider;

    if (lastXmin != Float.NaN) // update ticks if we can
      getXTicks(lastXmin, lastXmax);
  }

  
  public ITickProvider getYTickProvider() {
    return yTickProvider;
  }

  
  public void setYTickProvider(ITickProvider tickProvider) {
    yTickProvider = tickProvider;

    if (lastYmin != Float.NaN) // update ticks if we can
      getYTicks(lastYmin, lastYmax);
  }

  
  public ITickProvider getZTickProvider() {
    return zTickProvider;
  }

  
  public void setZTickProvider(ITickProvider tickProvider) {
    zTickProvider = tickProvider;

    if (lastZmin != Float.NaN) // update ticks if we can
      getZTicks(lastZmin, lastZmax);
  }

  
  public ITickRenderer getXTickRenderer() {
    return xTickRenderer;
  }

  
  public void setXTickRenderer(ITickRenderer tickRenderer) {
    xTickRenderer = tickRenderer;
  }

  
  public ITickRenderer getYTickRenderer() {
    return yTickRenderer;
  }

  
  public void setYTickRenderer(ITickRenderer tickRenderer) {
    yTickRenderer = tickRenderer;
  }

  
  public ITickRenderer getZTickRenderer() {
    return zTickRenderer;
  }

  
  public void setZTickRenderer(ITickRenderer tickRenderer) {
    zTickRenderer = tickRenderer;
  }

  // TICK COLORS

  
  public Color getXTickColor() {
    return xTickColor;
  }

  
  public void setXTickColor(Color tickColor) {
    xTickColor = tickColor;
  }

  
  public Color getYTickColor() {
    return yTickColor;
  }

  
  public void setYTickColor(Color tickColor) {
    yTickColor = tickColor;
  }

  
  public Color getZTickColor() {
    return zTickColor;
  }

  
  public void setZTickColor(Color tickColor) {
    zTickColor = tickColor;
  }

  // TICK LABELS

  
  public boolean isXTickLabelDisplayed() {
    return xTickLabelDisplayed;
  }

  /**
   * Supported by EmulGL only
   */
  
  public void setXTickLabelDisplayed(boolean tickLabelDisplayed) {
    xTickLabelDisplayed = tickLabelDisplayed;
  }

  
  public boolean isYTickLabelDisplayed() {
    return yTickLabelDisplayed;
  }

  /**
   * Supported by EmulGL only
   */
  
  public void setYTickLabelDisplayed(boolean tickLabelDisplayed) {
    yTickLabelDisplayed = tickLabelDisplayed;
  }

  
  public boolean isZTickLabelDisplayed() {
    return zTickLabelDisplayed;
  }

  /**
   * Supported by EmulGL only
   */
  
  public void setZTickLabelDisplayed(boolean tickLabelDisplayed) {
    zTickLabelDisplayed = tickLabelDisplayed;
  }

  
  public boolean isTickLineDisplayed() {
    return tickLineDisplayed;
  }

  
  public void setTickLineDisplayed(boolean tickLineDisplayed) {
    this.tickLineDisplayed = tickLineDisplayed;
  }

  // ********************* AXIS LABELS ************************ //

  
  public boolean isAxisLabelOffsetAuto() {
    return axisLabelOffsetAuto;
  }

  /**
   * When enabled, the axis will have the X, Y and Z axis label shifted to avoid covering the tick
   * labels.
   */
  
  public void setAxisLabelOffsetAuto(boolean isAuto) {
    this.axisLabelOffsetAuto = isAuto;
  }

  public int getAxisLabelOffsetMargin() {
    return axisLabelOffsetMargin;
  }

  /**
   * When {@link #setAxisLabelOffsetAuto(true)}, use this margin to define the horizontal margin to
   * let between the ticks and the axis labels
   */
  public void setAxisLabelOffsetMargin(int margin) {
    this.axisLabelOffsetMargin = margin;
  }


  
  public String getXAxisLabel() {
    return xAxeLabel;
  }

  
  public void setXAxisLabel(String axeLabel) {
    xAxeLabel = axeLabel;
  }

  
  public String getYAxisLabel() {
    return yAxeLabel;
  }

  
  public void setYAxisLabel(String axeLabel) {
    yAxeLabel = axeLabel;
  }

  
  public String getZAxisLabel() {
    return zAxeLabel;
  }

  
  public void setZAxisLabel(String axeLabel) {
    zAxeLabel = axeLabel;
  }

  
  public boolean isXAxisLabelDisplayed() {
    return xAxeLabelDisplayed;
  }

  
  public void setXAxisLabelDisplayed(boolean axeLabelDisplayed) {
    xAxeLabelDisplayed = axeLabelDisplayed;
  }

  
  public boolean isYAxisLabelDisplayed() {
    return yAxeLabelDisplayed;
  }

  
  public void setYAxisLabelDisplayed(boolean axeLabelDisplayed) {
    yAxeLabelDisplayed = axeLabelDisplayed;
  }

  
  public boolean isZAxisLabelDisplayed() {
    return zAxeLabelDisplayed;
  }

  
  public void setZAxisLabelDisplayed(boolean axeLabelDisplayed) {
    zAxeLabelDisplayed = axeLabelDisplayed;
  }

  
  public LabelOrientation getXAxisLabelOrientation() {
    return xAxisLabelOrientation;
  }

  
  public void setXAxisLabelOrientation(LabelOrientation xAxisLabelOrientation) {
    this.xAxisLabelOrientation = xAxisLabelOrientation;
  }

  
  public LabelOrientation getYAxisLabelOrientation() {
    return yAxisLabelOrientation;
  }

  
  public void setYAxisLabelOrientation(LabelOrientation yAxisLabelOrientation) {
    this.yAxisLabelOrientation = yAxisLabelOrientation;
  }

  
  public LabelOrientation getZAxisLabelOrientation() {
    return zAxisLabelOrientation;
  }

  
  public void setZAxisLabelOrientation(LabelOrientation zAxisLabelOrientation) {
    this.zAxisLabelOrientation = zAxisLabelOrientation;
  }

  
  public ZAxisSide getZAxisSide() {
    return zAxisSide;
  }

  
  public void setZAxisSide(ZAxisSide zAxisSide) {
    this.zAxisSide = zAxisSide;
  }


  // ********************************************* //


  
  public boolean isFaceDisplayed() {
    return faceDisplayed;
  }

  
  public void setFaceDisplayed(boolean faceDisplayed) {
    this.faceDisplayed = faceDisplayed;
  }

  
  public Color getQuadColor() {
    return quadColor;
  }

  
  public void setQuadColor(Color quadColor) {
    this.quadColor = quadColor;
  }

  
  public Color getGridColor() {
    return gridColor;
  }

  
  public void setGridColor(Color gridColor) {
    this.gridColor = gridColor;
  }



  // ************************* FONT *********************** //

  
  public IFontSizePolicy getFontSizePolicy() {
    return fontSizePolicy;
  }

  
  public void setFontSizePolicy(IFontSizePolicy fontSizePolicy) {
    this.fontSizePolicy = fontSizePolicy;
  }

  
  public void applyFontSizePolicy() {
    if (fontSizePolicy != null) {
      fontSizePolicy.apply(this);
    }
  }

  
  public Font getFont() {
    return font;
  }

  
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
  
  public Font getFont(AxisLayout.FontType type, HiDPI hidpi) {
    if (type == null) {
      // most frequent case so keep first in this selection
      if (HiDPI.ON.equals(hidpi)) {
        return fontMajorHiDPI;
      } else if (HiDPI.OFF.equals(hidpi)) {
        return fontMajorNoHiDPI;
      }
    } else if (AxisLayout.FontType.Major.equals(type)) {
      if (HiDPI.ON.equals(hidpi)) {
        return fontMajorHiDPI;
      } else if (HiDPI.OFF.equals(hidpi)) {
        return fontMajorNoHiDPI;
      }
    } else if (AxisLayout.FontType.Minor.equals(type)) {
      if (HiDPI.ON.equals(hidpi)) {
        return fontMinorHiDPI;
      } else if (HiDPI.OFF.equals(hidpi)) {
        return fontMinorNoHiDPI;
      }
    }
    return null;
    // return font;
  }

  
  public float getTickLengthRatio() {
    return tickLengthRatio;
  }

  /**
   * Set the length of ticks, given as a ratio of the scene bounds.
   * 
   * If scene bounding box range is 100 and tickLengthRatio is 20, then the actual tick length on
   * screen will be of 5 pixels.
   */
  
  public void setTickLengthRatio(float tickLengthRatio) {
    this.tickLengthRatio = tickLengthRatio;
  }

  
  public float getAxisLabelDistance() {
    return axisLabelDistance;
  }


  
  public void setAxisLabelDistance(float axisLabelDistance) {
    this.axisLabelDistance = axisLabelDistance;
  }



  /**
   * Get font according to a given context
   * 
   * @param font the font to use for the context
   * @param type the major/minor font possibilites that a drawable or colorbar may use
   * @param hidpi the HiDPI context for this font, allowing to define bigger fonts in case screen
   *        resolution is high (and text small)
   */
  
  public void setFont(Font font, AxisLayout.FontType type, HiDPI hidpi) {
    if (AxisLayout.FontType.Major.equals(type)) {
      if (HiDPI.ON.equals(hidpi)) {
        fontMajorHiDPI = font;
      } else if (HiDPI.OFF.equals(hidpi)) {
        fontMajorNoHiDPI = font;
      }
    } else if (AxisLayout.FontType.Minor.equals(type)) {
      if (HiDPI.ON.equals(hidpi)) {
        fontMinorHiDPI = font;
      } else if (HiDPI.OFF.equals(hidpi)) {
        fontMinorNoHiDPI = font;
      }
    }
  }
  
  public AxisLayout clone() {
    AxisLayout to = new AxisLayout();
    AxisLayout from = this;
    return copy(from, to);
  }

  public void applySettings(AxisLayout from) {
    copy(from, this);
  }



  protected AxisLayout copy(AxisLayout from, AxisLayout to) {
    // font
    to.setFont(from.getFont());
    to.setFontSizePolicy(from.getFontSizePolicy());
    to.fontMajorHiDPI = from.fontMajorHiDPI;
    to.fontMinorHiDPI = from.fontMinorHiDPI;
    to.fontMajorNoHiDPI = from.fontMajorNoHiDPI;
    to.fontMinorNoHiDPI = from.fontMinorNoHiDPI;
    
    // color
    to.setMainColor(from.getMainColor());
    to.setGridColor(from.getGridColor());
    to.setQuadColor(from.getQuadColor());

    to.setFaceDisplayed(from.isFaceDisplayed());

    // axis
    to.setAxisLabelDistance(from.getAxisLabelDistance());
    to.setAxisLabelOffsetAuto(from.isAxisLabelOffsetAuto());
    to.setAxisLabelOffsetMargin(from.getAxisLabelOffsetMargin());
    
    // ticks
    to.setTickLengthRatio(from.getTickLengthRatio());
    to.setTickLineDisplayed(from.isTickLineDisplayed());
    
    // x
    to.setXAxisLabelDisplayed(from.isXAxisLabelDisplayed());
    to.setXAxisLabel(from.getXAxisLabel());
    to.setXAxisLabelOrientation(from.getXAxisLabelOrientation());
    to.setXTickColor(from.getXTickColor());
    to.setXTickProvider(from.getXTickProvider());
    to.setXTickRenderer(from.getXTickRenderer());

    // y
    to.setYAxisLabelDisplayed(from.isYAxisLabelDisplayed());
    to.setYAxisLabel(from.getYAxisLabel());
    to.setYAxisLabelOrientation(from.getYAxisLabelOrientation());
    to.setYTickColor(from.getYTickColor());
    to.setYTickProvider(from.getYTickProvider());
    to.setYTickRenderer(from.getYTickRenderer());

    // z
    to.setZAxisLabelDisplayed(from.isZAxisLabelDisplayed());
    to.setZAxisLabel(from.getZAxisLabel());
    to.setZAxisLabelOrientation(from.getZAxisLabelOrientation());
    to.setZTickColor(from.getZTickColor());
    to.setZTickProvider(from.getZTickProvider());
    to.setZTickRenderer(from.getZTickRenderer());
    
    return to;
  }
}
