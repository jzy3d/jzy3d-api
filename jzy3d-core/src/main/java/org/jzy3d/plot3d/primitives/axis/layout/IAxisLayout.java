package org.jzy3d.plot3d.primitives.axis.layout;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.IFontSizePolicy;
import org.jzy3d.plot3d.primitives.axis.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.view.HiDPI;


public interface IAxisLayout {
  public static final Font FONT_DEFAULT = Font.Helvetica_12;

  public void setMainColor(Color color);

  public Color getMainColor();

  // public void setTickDisplayed(boolean status);
  // public boolean isTickDisplayed();
  public Color getGridColor();

  public void setGridColor(Color gridColor);

  // not for axebase
  public void setFaceDisplayed(boolean status);
  
  public ZAxisSide getZAxisSide();

  public void setZAxisSide(ZAxisSide zAxisSide);

  public boolean isFaceDisplayed();

  public Color getQuadColor();

  public void setQuadColor(Color quadColor);
  
  
  public boolean isAxisLabelOffsetAuto();
  
  /** 
   * When enabled, the axis will have the X, Y and Z axis label shifted
   * to avoid covering the tick labels.
   */
  public void setAxisLabelOffsetAuto(boolean isAuto);
  
  public int getAxisLabelOffsetMargin();
  /**
   * When {@link #setAxisLabelOffsetAuto(true)}, use this margin
   * to define the horizontal margin to let between the ticks and the axis labels
   */
  public void setAxisLabelOffsetMargin(int margin);
  
  public void setXAxisLabel(String label);

  public void setYAxisLabel(String label);

  public void setZAxisLabel(String label);

  public String getXAxisLabel();

  public String getYAxisLabel();

  public String getZAxisLabel();

  public void setXAxeLabelDisplayed(boolean axeLabelDisplayed);

  public void setYAxeLabelDisplayed(boolean axeLabelDisplayed);

  public void setZAxeLabelDisplayed(boolean axeLabelDisplayed);

  public boolean isXAxeLabelDisplayed();

  public boolean isYAxeLabelDisplayed();

  public boolean isZAxeLabelDisplayed();

  public void setXTickLabelDisplayed(boolean tickLabelDisplayed);

  public void setYTickLabelDisplayed(boolean tickLabelDisplayed);

  public void setZTickLabelDisplayed(boolean tickLabelDisplayed);

  public boolean isXTickLabelDisplayed();

  public boolean isYTickLabelDisplayed();

  public boolean isZTickLabelDisplayed();

  public boolean isTickLineDisplayed();

  public void setTickLineDisplayed(boolean status);


  public void setXTickProvider(ITickProvider provider);

  public void setYTickProvider(ITickProvider provider);

  public void setZTickProvider(ITickProvider provider);

  public ITickProvider getXTickProvider();

  public ITickProvider getYTickProvider();

  public ITickProvider getZTickProvider();

  public void setXTickRenderer(ITickRenderer renderer);

  public void setYTickRenderer(ITickRenderer renderer);

  public void setZTickRenderer(ITickRenderer renderer);

  public ITickRenderer getXTickRenderer();

  public ITickRenderer getYTickRenderer();

  public ITickRenderer getZTickRenderer();

  /*
   * public void updateXTicks(float min, float max); public void updateYTicks(float min, float max);
   * public void updateZTicks(float min, float max);
   */

  public double[] getXTicks(double min, double max);

  public double[] getYTicks(double min, double max);

  public double[] getZTicks(double min, double max);

  public double[] getXTicks();

  public double[] getYTicks();

  public double[] getZTicks();

  public void setXTickColor(Color color);

  public void setYTickColor(Color color);

  public void setZTickColor(Color color);

  public Color getXTickColor();

  public Color getYTickColor();

  public Color getZTickColor();
  
  public Font getFont();
  public void setFont(Font font);

  public Font getFont(FontType type, HiDPI hidpi);
  public void setFont(Font font, FontType type, HiDPI hidpi);

  //public Font getFont(HiDPI hidpi);
  //public void setFont(Font font, HiDPI hidpi);

  public enum FontType{
    Major,Minor
  }
  
  public IFontSizePolicy getFontSizePolicy();

  public void setFontSizePolicy(IFontSizePolicy fontSizePolicy);

  public void applyFontSizePolicy();
  
  
  public LabelOrientation getXAxisLabelOrientation();

  public void setXAxisLabelOrientation(LabelOrientation xAxisLabelOrientation);

  public LabelOrientation getYAxisLabelOrientation();

  public void setYAxisLabelOrientation(LabelOrientation yAxisLabelOrientation);

  public LabelOrientation getZAxisLabelOrientation();

  public void setZAxisLabelOrientation(LabelOrientation zAxisLabelOrientation);

}
