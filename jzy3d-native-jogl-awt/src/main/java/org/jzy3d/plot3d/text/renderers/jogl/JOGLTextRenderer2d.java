package org.jzy3d.plot3d.text.renderers.jogl;

import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.AWTFont;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.text.AbstractTextRenderer;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.AWTTextLayout;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;
import org.jzy3d.plot3d.text.renderers.jogl.style.DefaultTextStyle;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.awt.TextRenderer.RenderDelegate;

/**
 * Render 2D texts using JOGL {@link TextRenderer}.
 * 
 * The text color is defined either by {@link TextRenderer#setColor(java.awt.Color)} or inside the
 * implementation of {@link RenderDelegate#draw(java.awt.Graphics2D, String, int, int)}
 * if the {@link TextRenderer} is initialized with a non null {@link RenderDelegate}.
 * 
 * Note that Jzy3d and JOGL behave differently with text rendering
 * <ul>
 * <li>Jzy3d's text color is defined for each string.
 * <li>JOGL's text color is defined globally and can not be changed consistently afterward if it
 * uses a {@link TextRenderer.RenderDelegate}.
 * </ul>
 * Wrapping a JOGL text renderer hence requires recreating a new TextRenderer if the color of the
 * strings changes.
 * 
 * @author Martin Pernollet
 */
public class JOGLTextRenderer2d extends AbstractTextRenderer implements ITextRenderer {
  protected java.awt.Font awtFont;
  protected TextRenderer renderer;
  protected TextRenderer.RenderDelegate renderDelegate;
  protected float scaleFactor = 0.01f;
  
  protected AWTTextLayout layout = new AWTTextLayout();

  public JOGLTextRenderer2d() {
    this(new Font("Arial", 16));
  }

  public JOGLTextRenderer2d(Font font) {
    this(font, null);
  }

  /**
   * @param font text font, style and size.
   * @param renderDelegate may be null if no particular custom styling should be applied.
   */
  public JOGLTextRenderer2d(Font font, RenderDelegate renderDelegate) {
    this.awtFont = AWTFont.toAWT(font);
    this.renderer = new TextRenderer(awtFont, true, true, renderDelegate);
    this.renderDelegate = renderDelegate;
  }

  @Override
  public BoundingBox3d drawText(IPainter painter, Font font, String s, Coord3d position,
      float rotation, Horizontal horizontal, Vertical vertical, Color color, Coord2d screenOffset, Coord3d sceneOffset) {
    // configureRenderer();
    resetTextColor(color);

    // Reset to a polygon mode suitable for rendering the texture handling the text
    painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.FILL);

    drawText2D(painter, font, s, position, color, horizontal, vertical);

    return null;
  }

  /** Draws a 2D text (facing camera) at the specified 3D position. */
  protected void drawText2D(IPainter painter, Font font, String text, Coord3d position,
      Color color, Horizontal horizontal, Vertical vertical) {

    // Canvas size
    int width = painter.getView().getCanvas().getRendererWidth();
    int height = painter.getView().getCanvas().getRendererHeight();

    // Text screen position
    Coord3d screen = painter.getCamera().modelToScreen(painter, position);
    Coord2d textSize = layout.getBounds(text, awtFont, renderer.getFontRenderContext());
    screen = layout.align(textSize.x, textSize.y, horizontal, vertical, screen);

    // Render text
    renderer.setColor(color.r, color.g, color.b, color.a);
    renderer.beginRendering(width, height);
    
    
    renderer.draw(text, (int) screen.x, (int) screen.y);
    renderer.flush();
    renderer.endRendering();
  }
  
  



  // not efficient : will reset text renderer at each rendering rendering loop for each string
  // just to be able to reset color... while setting color is rare.
  protected void resetTextColor(Color color) {
    if (lastColor != null && !lastColor.equals(color)) {
      if (renderDelegate != null) {
        if (renderDelegate instanceof DefaultTextStyle) {
          ((DefaultTextStyle) renderDelegate).setColor(AWTColor.toAWT(color));
          renderer = new TextRenderer(awtFont, true, true, renderDelegate);
        }
      }
    }

    lastColor = color;
  }

  Color lastColor;

  protected void configureRenderer() {
    // some GPU do not handle smoothing well
    renderer.setSmoothing(false);
    // some GPU do not handle VBO properly
    renderer.setUseVertexArrays(false);
  }
}
