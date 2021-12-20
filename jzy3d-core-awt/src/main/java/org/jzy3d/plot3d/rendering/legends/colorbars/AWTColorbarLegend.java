package org.jzy3d.plot3d.rendering.legends.colorbars;

import java.awt.image.BufferedImage;
import org.apache.logging.log4j.LogManager;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.legends.AWTLegend;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;

/**
 * Handle a colorbar rendered as an AWT {@link BufferedImage}.
 * 
 * <h2>Content and coloring</h2>
 * 
 * A colorbar is configured with
 * <ul>
 * <li>a colormap defined by the input {@link Drawable} if it is {@link IMultiColorable} and if it
 * has a {@link ColorMapper} defined.
 * <li>a set of axis ticks defined and formated by {@link IAxisLayout}.
 * </ul>
 * 
 * <h2>Layout</h2>
 * 
 * The size of the colorbar image is driven by
 * <ul>
 * <li>{@link #setViewPort(int, int, float, float)} which indicates the canvas dimension on screen
 * and the slice that the colorbar should occupy. The image width will be <code>imageWidth = width *
 * (right - left)</code>. The viewport is set by {@link ViewAndColorbarsLayout}.
 * </ul>
 * 
 * The position of the colorbar is defined by {@link ViewAndColorbarsLayout} which render the
 * prepared image at the layout given position for this colorbar (which is defined with left/right
 * parameters).
 * 
 * <img src="doc-files/colorbar-layout.png"/>
 * <a href="https://lucid.app/lucidchart/78ec260b-d2d1-430d-a363-a95089dae86d/edit?page=F_rAt-Q~kI-~#">Schema sources</a>
 * 
 * <h2>Rendering path</h2>
 * 
 * EmulGL and Native have different rendering pathes
 * <ul>
 * <li>Native chart have their colorbar image rendered by
 * {@link AWTColorbarLegend#render(IPainter)}.
 * <li>EmulGL chart have their colorbar image rendered by AWT as bypassed by
 * {@link EmulGLViewAndColorbarLayout}, which customize the way pixel scale should be handled for
 * rendering such image in a good layoout
 * </ul>
 * 
 * <img src="doc-files/colorbar-object-model.png"/>
 * 
 * <a href="https://lucid.app/lucidchart/78ec260b-d2d1-430d-a363-a95089dae86d/edit?page=_q-Nux3~IiKx#">Schema sources</a>
 */
public class AWTColorbarLegend extends AWTLegend implements IColorbarLegend {
  protected ITickProvider provider;
  protected ITickRenderer renderer;

  // remember asked width to be able to reset image
  // without processing multiple time the margin
  protected int askedWidth;
  protected int askedHeight;
  
  protected Font font;
  

  public AWTColorbarLegend(Drawable parent, Chart chart) {
    this(parent, chart.getView().getAxis().getLayout());
  }

  public AWTColorbarLegend(Drawable parent, IAxisLayout layout) {
    this(parent, layout.getZTickProvider(), layout.getZTickRenderer(), layout.getMainColor(),
        layout.getMainColor().negative());
  }

  public AWTColorbarLegend(Drawable parent, IAxisLayout layout, Color foreground) {
    this(parent, layout.getZTickProvider(), layout.getZTickRenderer(), foreground, null);
  }

  public AWTColorbarLegend(Drawable parent, IAxisLayout layout, Color foreground,
      Color background) {
    this(parent, layout.getZTickProvider(), layout.getZTickRenderer(), foreground, background);
  }

  public AWTColorbarLegend(Drawable parent, ITickProvider provider, ITickRenderer renderer) {
    this(parent, provider, renderer, Color.BLACK, Color.WHITE);

  }

  public AWTColorbarLegend(Drawable parent, ITickProvider provider, ITickRenderer renderer,
      Color foreground, Color background) {
    super(parent, foreground, background);
    this.provider = provider;
    this.renderer = renderer;
    this.minimumDimension = new Dimension(AWTColorbarImageGenerator.MIN_BAR_WIDTH,
        AWTColorbarImageGenerator.MIN_BAR_HEIGHT);

    initImageGenerator(parent, provider, renderer);
  }

  protected void initImageGenerator(Drawable parent, ITickProvider provider,
      ITickRenderer renderer) {
    if (parent != null && parent instanceof IMultiColorable) {
      IMultiColorable mc = ((IMultiColorable) parent);
      if (mc.getColorMapper() != null) {
        imageGenerator = new AWTColorbarImageGenerator(mc.getColorMapper(), provider, renderer);
      
        if(font!=null)
          imageGenerator.setFont(font);
      }
    }
    if (imageGenerator == null) {
      LogManager.getLogger(this.getClass()).info(
          "Passed a drawable object that is not IMultiColorable or has no ColorMapper defined");
    }
  }

  @Override
  public void render(IPainter painter) {
    painter.glEnable_Blend();
    super.render(painter);
  }

  @Override
  public BufferedImage toImage(int width, int height) {
    return toImage(width, height, margin, pixelScale);
  }

  public BufferedImage toImage(int width, int height, Dimension margin, Coord2d pixelScale) {

    if (imageGenerator != null) {
      setGeneratorColors();

      int choosenWidth;
      int choosenHeight;
      // using pixel scale is ugly with native
      // choosenWidth = (int)((width - margin.width) * pixelScale.x);
      // choosenHeight = (int)((height - margin.height) * pixelScale.y);
      choosenWidth = (int) ((width - margin.width));
      choosenHeight = (int) ((height - margin.height));

      askedWidth = width;
      askedHeight = height;
      return imageGenerator.toImage(Math.max(choosenWidth, 1), Math.max(choosenHeight, 1));
    }
    return null;
  }

  @Override
  public void updateImage() {
    setImage(toImage(askedWidth, askedHeight));
  }

  public Dimension getMargin() {
    return margin;
  }

  public void setMargin(Dimension margin) {
    if (image != null) {
      // updateImage();
      setImage(toImage(askedWidth, askedHeight, margin, pixelScale));

    }
    this.margin = margin;
  }

  /** Update the image with pixel scale if scale changed */
  @Override
  protected void updatePixelScale(Coord2d pixelScale) {
    if (!this.pixelScale.equals(pixelScale)) {
      this.pixelScale = pixelScale;
      getImageGenerator().setPixelScale(pixelScale);
      setImage(toImage(askedWidth, askedHeight, margin, pixelScale));
    }
  }
  
  /** Update image generator font */
  public void setFont(Font font) {
    this.font = font;
    
    if(getImageGenerator()!=null)
      getImageGenerator().setFont(font);
  }

  public Font getFont() {
    return getImageGenerator().getFont();
  }


  public AWTColorbarImageGenerator getImageGenerator() {
    return (AWTColorbarImageGenerator) imageGenerator;
  }

  @Override
  public int getWidth() {
    return askedWidth;
  }

  @Override
  public int getHeight() {
    return askedHeight;
  }
  
  

}
