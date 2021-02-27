package org.jzy3d.plot3d.text.renderers;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.IPainter.Font;
import org.jzy3d.plot3d.text.AbstractTextRenderer;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.TextLayout;
import org.jzy3d.plot3d.text.align.Valign;

/**
 * could enhance using http://www.angelcode.com/products/bmfont/
 * 
 * @author Martin
 */
public class TextBitmapRenderer extends AbstractTextRenderer implements ITextRenderer {
  protected static Logger LOGGER = Logger.getLogger(TextBitmapRenderer.class);

  protected Font font;

  /**
   * The TextBitmap class provides support for drawing ASCII characters Any non ascii caracter will
   * be replaced by a square.
   */
  public TextBitmapRenderer() {
    this(Font.Helvetica_10);
  }

  public TextBitmapRenderer(Font font) {
    this.font = font;
  }

  public Font getFont() {
    return font;
  }

  public void setFont(Font font) {
    this.font = font;
  }

  /**
   * Draw a string at the specified position and return the 3d volume occupied by the string
   * according to the current Camera configuration.
   */
  @Override
  public BoundingBox3d drawText(IPainter painter, String text, Coord3d position, Halign halign,
      Valign valign, Color color, Coord2d screenOffset, Coord3d sceneOffset) {
    painter.color(color);

    // compute a corrected 3D position according to the 2D layout on screen
    float textHeight = font.getHeight();
    float textWidth = painter.glutBitmapLength(font.getCode(), text);

    Coord3d screen = painter.getCamera().modelToScreen(painter, position);
    Coord3d screenAligned =
        TextLayout.align(textWidth, textHeight, halign, valign, screenOffset, screen);

    // process the aligned position in 3D coordinates
    Coord3d positionAligned = toModelViewPosition(painter, screen, screenAligned);

    // process space stransform if any (log, etc)
    if (spaceTransformer != null) {
      positionAligned = spaceTransformer.compute(positionAligned);
    }
    positionAligned = positionAligned.add(sceneOffset);

    // Draws actual string
    painter.glutBitmapString(font, text, positionAligned, color);

    // Return text bounds
    return computeTextBounds(painter, screenAligned, textWidth);
  }

  /** Left as a helper for subclasses. TODO delete me and subclass */
  protected void glRasterPos(IPainter painter, Coord3d sceneOffset,
      Coord3d screenPositionAligned3d) {
    if (spaceTransformer != null) {
      screenPositionAligned3d = spaceTransformer.compute(screenPositionAligned3d);
    }

    painter.raster(screenPositionAligned3d.add(sceneOffset), null);
  }

  protected Coord3d toModelViewPosition(IPainter painter, Coord3d screen, Coord3d screenAligned) {

    Coord3d screenAligned3d;
    try {
      screenAligned3d = painter.getCamera().screenToModel(painter, screenAligned);
    } catch (RuntimeException e) {
      // TODO: solve this bug due to a Camera.PERSPECTIVE mode.
      LOGGER.error(
          "could not process text position: " + screen + " " + screenAligned + e.getMessage());
      return new Coord3d();
    }
    return screenAligned3d;
  }

  protected BoundingBox3d computeTextBounds(IPainter painter, Coord3d posScreenShifted,
      float strlen) {
    Coord3d botLeft = new Coord3d();
    Coord3d topRight = new Coord3d();
    botLeft.x = posScreenShifted.x;
    botLeft.y = posScreenShifted.y;
    botLeft.z = posScreenShifted.z;
    topRight.x = botLeft.x + strlen;
    topRight.y = botLeft.y + font.getHeight();
    topRight.z = botLeft.z;

    BoundingBox3d txtBounds = new BoundingBox3d();
    txtBounds.add(painter.getCamera().screenToModel(painter, botLeft));
    txtBounds.add(painter.getCamera().screenToModel(painter, topRight));
    return txtBounds;
  }
}
