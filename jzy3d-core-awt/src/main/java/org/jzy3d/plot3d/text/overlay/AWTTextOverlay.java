package org.jzy3d.plot3d.text.overlay;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;


/**
 * Highly experimental text renderer.
 * 
 * The {@link AWTTextOverlay} allows computing the 2d position of a text in the window, according to
 * its required position in the 3d environment (using appenText method). As a 2nd pass, it might be
 * rendered into a java {@link Graphics} context. The interesting thing, compared to the currently
 * used {@link TextBitmapRenderer}, is that the user may select any java Font, whereas the
 * {@link TextBitmapRenderer} only relies on Helvetica font provided by opengl.
 * 
 * Actually no need to urgently correct these following todo: due to the problems related to
 * post/pre rendering (blinking), this text Renderer is not used. An alternative would be to render
 * the java text could be renderer into a Java Image that could in turn be rendered by opengl.
 * 
 * // TODO: Verify alignement constants // TODO: Complete text renderer: select font, size, zoom //
 * TODO: there may be a bug of non-rendering without resizing at init -> check that // TODO: the
 * 1<->N relation TextRenderer<->Canvas won't work because TextRenderer has only one target
 * Component.
 */
public class AWTTextOverlay implements AWTRenderer2d {
  public AWTTextOverlay(ICanvas canvas) {
    init(canvas);
  }

  protected void init(ICanvas canvas) {
    initComponent((Component) canvas);
  }

  protected void initComponent(Component c) {
    textList = new ArrayList<TextDescriptor>(50);
    target = c;
    target.addComponentListener(resizeListener);

    targetWidth = target.getWidth();
    targetHeight = target.getHeight();
  }

  public void dispose() {
    target.removeComponentListener(resizeListener);
  }

  /****************************************************************/

  public void appendText(IPainter painter, String s, Coord3d position, Horizontal halign, Vertical valign,
      Color color) {
    Coord3d posScreen = painter.getCamera().modelToScreen(painter, position);

    textList
        .add(new TextDescriptor(s, new Coord2d(posScreen.x, posScreen.y), color, halign, valign));
  }

  @Override
  public void paint(Graphics g, int canvasWidth, int canvasHeight) {
    int x;
    int y;


    FontMetrics metric = g.getFontMetrics();

    for (TextDescriptor t : textList) {

      // Convert from GL2 to J2D coordinates mode
      x = (int) t.position.x;
      y = targetHeight - (int) t.position.y;

      // Apply alignment
      Rectangle2D area = metric.getStringBounds(t.str, g);

      if (t.halign == Horizontal.RIGHT)
        ;// x = x;
      else if (t.halign == Horizontal.CENTER)
        x = x - (int) area.getWidth() / 2;
      else if (t.halign == Horizontal.LEFT)
        x = x - (int) area.getWidth();

      if (t.valign == Vertical.TOP)
        ;// y = y;
      else if (t.valign == Vertical.CENTER)
        y = y + (int) area.getHeight() / 2;
      else if (t.valign == Vertical.BOTTOM || t.valign == Vertical.GROUND)
        y = y + (int) area.getHeight();

      // Perform the actual text rendering
      g.setColor(new java.awt.Color(t.color.r, t.color.g, t.color.b, t.color.a));
      g.drawString(t.str, x, y);
    }
    textList.clear();
  }

  /***************************************************************************/

  @SuppressWarnings("unused")
  private int targetWidth;
  private int targetHeight;
  private Component target;

  private List<TextDescriptor> textList;

  private ComponentListener resizeListener = new ComponentListener() {
    @Override
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentResized(ComponentEvent e) {
      Component obj = (Component) e.getSource();
      targetWidth = obj.getWidth();
      targetHeight = obj.getHeight();
    }

    @Override
    public void componentShown(ComponentEvent e) {}
  };

  /***************************************************************************/

  private class TextDescriptor {
    public TextDescriptor(String str, Coord2d position, Color color, Horizontal halign, Vertical valign) {
      this.str = str;
      this.position = position;
      this.color = color;
      this.halign = halign;
      this.valign = valign;
    }

    public String str;
    public Color color;
    public Coord2d position;
    public Horizontal halign;
    public Vertical valign;
  }
}
