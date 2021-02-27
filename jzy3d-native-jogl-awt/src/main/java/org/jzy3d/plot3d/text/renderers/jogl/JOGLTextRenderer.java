package org.jzy3d.plot3d.text.renderers.jogl;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.text.AbstractTextRenderer;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;
import org.jzy3d.plot3d.text.renderers.jogl.style.DefaultTextStyle;
import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * Render texts using JOGL {@link TextRenderer}.
 * 
 * The text color is defined either by {@link TextRenderer#setColor(java.awt.Color)} or
 * inside the implementation of {@link TextRenderer.RenderDelegate#draw(java.awt.Graphics2D, String, int, int)}
 * if the {@link TextRenderer} is initialized with a non null {@link TextRenderer}.
 * 
 * Note that Jzy3d and JOGL behave differently with text rendering
 * <ul>
 * <li>Jzy3d's text color is defined for each string.
 * <li>JOGL's text color is defined globally and can not be changed consistently afterward if it uses a {@link TextRenderer.RenderDelegate}.
 * </ul>
 * Wrapping a JOGL text renderer hence requires recreating a new TextRenderer if the color of the strings changes.
 * 
 * @author Martin Pernollet
 */
public class JOGLTextRenderer extends AbstractTextRenderer implements ITextRenderer {
  protected boolean is3D = false;
  protected Font font;
  protected TextRenderer renderer;
  protected TextRenderer.RenderDelegate renderDelegate;
  protected float scaleFactor = 0.01f;


  public JOGLTextRenderer() {
    this(new Font("Arial", Font.PLAIN, 16));
  }

  public JOGLTextRenderer(Font font) {
    this(font, false);
  }

  public JOGLTextRenderer(Font font, boolean is3D) {
    this(font, null, is3D);
  }

  /**
   * 
   * @param font the text font, style and size.
   * @param renderDelegate
   * @param is3D the text will be facing camera if false.
   */
  public JOGLTextRenderer(Font font, TextRenderer.RenderDelegate renderDelegate, boolean is3D) {
    this.font = font;
    this.renderer = new TextRenderer(font, true, true, renderDelegate);
    this.renderDelegate = renderDelegate;
    this.is3D = is3D;
  }

  @Override
  public BoundingBox3d drawText(IPainter painter, String s, Coord3d position, Halign halign, Valign valign, Color color, Coord2d screenOffset, Coord3d sceneOffset) {
    //configureRenderer();
    resetTextColor(color);
    
    // Reset to a polygon mode suitable for rendering the texture handling the text
    painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.FILL);

    if(is3D) {
      drawText3D(painter, s, position, color, sceneOffset);
    }
    else {
      drawText2D(painter, s, position, color);
    }

    return null;
  }

  // not efficient : will reset text renderer at each rendering rendering loop for each string
  // just to be able to reset color... while setting color is rare.
  private void resetTextColor(Color color) {
    if(lastColor!=null && !lastColor.equals(color)) {
      if(renderDelegate!=null) {
        if(renderDelegate instanceof DefaultTextStyle) {
          ((DefaultTextStyle) renderDelegate).setColor(AWTColor.toAWT(color));
          renderer = new TextRenderer(font, true, true, renderDelegate);
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

  /* TEXT PLACED AS 2D OBJECT (ALWAYS FACING CAMERA) */
  
  protected void drawText2D(IPainter painter, String s, Coord3d position, Color color) {
    
    int width = painter.getView().getCanvas().getRendererWidth();
    int height = painter.getView().getCanvas().getRendererHeight();
    Coord3d screen = painter.getCamera().modelToScreen(painter, position);

    renderer.setColor(color.r, color.g, color.b, color.a);
    renderer.beginRendering(width, height);
    renderer.draw(s, (int)screen.x, (int)screen.y);
    renderer.flush();
    renderer.endRendering();
  }

  /* TEXT PLACED AS 3D OBJECT (ROTATE WITH CAM) */
  
  protected void drawText3D(IPainter painter, String s, Coord3d position, Color color, Coord3d sceneOffset) {
    renderer.setColor(color.r, color.g, color.b, color.a);
    renderer.begin3DRendering();
    drawText3D(s, position, sceneOffset);
    //drawText3DWithLayout(painter, s, position, sceneOffset);
    renderer.flush();
    renderer.end3DRendering();
  }

  protected void drawText3D(String s, Coord3d position, Coord3d sceneOffset) {
    Coord3d real = position.add(sceneOffset);
    renderer.draw3D(s, real.x, real.y, real.z, scaleFactor);
  }

  //work in progress, used to compute bounding box of the text
  protected void drawText3DWithLayout(IPainter painter, String s, Coord3d position, Coord3d sceneOffset) {
    Rectangle2D d = renderDelegate.getBounds(s, font, renderer.getFontRenderContext());
    Coord3d left2d = painter.getCamera().modelToScreen(painter, position);
    Coord2d right2d = new Coord2d(left2d.x + (float) d.getWidth(), left2d.y + (float) d.getHeight());
    Coord3d right3d = painter.getCamera().screenToModel(painter, new Coord3d(right2d, 0));
    Coord3d offset3d = right3d.sub(position).div(2);
    Coord3d real = position.add(sceneOffset).sub(offset3d);
    
    renderer.draw3D(s, real.x, real.y, real.z, scaleFactor);
  }

}
