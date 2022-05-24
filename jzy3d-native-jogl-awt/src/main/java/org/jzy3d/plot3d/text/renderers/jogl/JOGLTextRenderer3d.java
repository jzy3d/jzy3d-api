package org.jzy3d.plot3d.text.renderers.jogl;


import java.awt.geom.Rectangle2D;
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
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.AbstractTextRenderer;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.AWTTextLayout;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;
import org.jzy3d.plot3d.text.renderers.jogl.style.DefaultTextStyle;
import org.jzy3d.plot3d.transform.Rotate;
import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * Render texts using JOGL {@link TextRenderer}.
 * 
 * The text color is defined either by {@link TextRenderer#setColor(java.awt.Color)} or inside the
 * implementation of {@link TextRenderer.RenderDelegate#draw(java.awt.Graphics2D, String, int, int)}
 * if the {@link TextRenderer} is initialized with a non null {@link TextRenderer}.
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
public class JOGLTextRenderer3d extends AbstractTextRenderer implements ITextRenderer {
  protected java.awt.Font awtFont;
  protected TextRenderer renderer;
  protected TextRenderer.RenderDelegate renderDelegate;
  protected float scaleFactor = 0.01f;

  protected Rotate rotate = new Rotate(0, new Coord3d(0, 0, 0));

  protected AWTTextLayout layout = new AWTTextLayout();


  public JOGLTextRenderer3d() {
    this(new Font("Arial", 16));
  }

  public JOGLTextRenderer3d(Font font) {
    this(font, null);
  }

  /**
   * 
   * @param font the text font, style and size.
   * @param renderDelegate
   * @param is3D the text will be facing camera if false.
   */
  public JOGLTextRenderer3d(Font font, TextRenderer.RenderDelegate renderDelegate) {
    this.awtFont = AWTFont.toAWT(font);
    this.renderer = new TextRenderer(awtFont, true, true, renderDelegate);
    this.renderDelegate = renderDelegate;
  }

  @Override
  public BoundingBox3d drawText(IPainter painter, Font font, String s, Coord3d position,
      float rotation, Horizontal halign, Vertical valign, Color color, Coord2d screenOffset, Coord3d sceneOffset) {
    // configureRenderer();
    resetTextColor(color);

    // Reset to a polygon mode suitable for rendering the texture handling the text
    painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.FILL);


    drawText3D(painter, s, position, halign, valign, color);

    return null;
  }


  /* TEXT PLACED AS 3D OBJECT (ROTATE WITH CAM) */

  protected void drawText3D(IPainter painter, String text, Coord3d position, Horizontal halign,
      Vertical valign, Color color) {
    
    Camera cam = painter.getCamera();
    
    
    

    /*Coord3d screen = painter.getCamera().modelToScreen(painter, position);
    Coord2d textSize = layout.getBounds(text, font, renderer.getFontRenderContext());
    screen = layout.align(textSize.x, textSize.y, halign, valign, screen);
    Coord3d aligned = painter.getCamera().screenToModel(painter, screen);
    
    Coord3d zero = aligned.sub(position);//new Coord3d();*/
    
    Coord3d zero = new Coord3d();

    renderer.setColor(color.r, color.g, color.b, color.a);
    renderer.begin3DRendering();
    
    renderer.draw3D(text, zero.x, zero.y, zero.z, scaleFactor);

    painter.glTranslatef(position.x, position.y, position.z);
    
    /*if(Halign.LEFT.equals(halign)) {
      Coord2d textSize = layout.getBounds(text, font, renderer.getFontRenderContext());
      
      // eval shift in 3D required to make 2D layout of a text renderer at 0
      Coord3d shift2D = layout.align(textSize.x, textSize.y, halign, valign, new Coord3d());
      Coord3d shift3D = painter.getCamera().screenToModel(painter, shift2D);
      
      painter.glTranslatef(shift3D.x, shift3D.y, shift3D.y);
    }*/
    
    
    /*if (rotate != null) {
      rotate.execute(painter);
    }*/
    

    renderer.flush();
    renderer.end3DRendering();

  }


  
  
  // work in progress, used to compute bounding box of the text
  protected void drawText3DWithLayout(IPainter painter, String s, Coord3d position,
      Coord3d sceneOffset) {
    Rectangle2D d = renderDelegate.getBounds(s, awtFont, renderer.getFontRenderContext());
    Coord3d left2d = painter.getCamera().modelToScreen(painter, position);
    Coord2d right2d =
        new Coord2d(left2d.x + (float) d.getWidth(), left2d.y + (float) d.getHeight());
    Coord3d right3d = painter.getCamera().screenToModel(painter, new Coord3d(right2d, 0));
    Coord3d offset3d = right3d.sub(position).div(2);
    Coord3d real = position.add(sceneOffset).sub(offset3d);

    renderer.draw3D(s, real.x, real.y, real.z, scaleFactor);
  }

  public Rotate getRotate() {
    return rotate;
  }

  /** Set the rotation of the text if the text is 3D. */
  public void setRotate(Rotate rotate) {
    this.rotate = rotate;
  }


  /* */

  // not efficient : will reset text renderer at each rendering rendering loop for each string
  // just to be able to reset color... while setting color is rare.
  private void resetTextColor(Color color) {
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
