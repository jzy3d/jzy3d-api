package org.jzy3d.plot3d.text.renderers;

import java.awt.Graphics2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.text.AbstractTextRenderer;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.TextLayout;
import org.jzy3d.plot3d.text.align.Vertical;

/**
 * The {@link ITextRenderer} is able to draw basic OpenGL font as supported by
 * {@link IPainter#glutBitmapString(Font, String, Coord3d, Color)}.
 * 
 * Concrete painters might rely on
 * <ul>
 * <li>EmulGL : AWT Font rendering by {@link Graphics2D#drawString(String, float, float)} by using
 * jGL AWT helpers.
 * <li>Native : GLUT.glutBitmapString(Font, String, Coord3d, Color)
 * </ul>
 * 
 * As EmulGL painter relies on AWT, much more font than the base OpenGL one can be used as it is
 * backed by AWT.
 */
public class TextBitmapRenderer extends AbstractTextRenderer implements ITextRenderer {
  protected static Logger LOGGER = LogManager.getLogger(TextBitmapRenderer.class);

  protected TextLayout layout = new TextLayout();

  /**
   * Draw a string at the specified position and return the 3d volume occupied by the string
   * according to the current Camera configuration.
   */
  @Override
  public BoundingBox3d drawText(IPainter painter, Font font, String text, Coord3d position,
      float rotation, Horizontal halign, Vertical valign, Color color, Coord2d screenOffset, Coord3d sceneOffset) {
    if(text==null) {
      return null;
    }
    
    painter.color(color);

    // compute a corrected 3D position according to the 2D layout on screen
    float textHeight = font.getHeight();
    float textWidth = painter.getTextLengthInPixels(font, text);

    Coord3d screen = painter.getCamera().modelToScreen(painter, position);
    Coord3d screenAligned =
        layout.align(textWidth, textHeight, halign, valign, screenOffset, screen);

    // process the aligned position in 3D coordinates
    Coord3d positionAligned = to3D(painter, screenAligned);

    // process space stransform if any (log, etc)
    if (spaceTransformer != null) {
      positionAligned = spaceTransformer.compute(positionAligned);
    }
    positionAligned = positionAligned.add(sceneOffset);

    // Draws actual string
    //painter.glutBitmapString(font, text, positionAligned, color);
    
    painter.drawText(font, text, positionAligned, color, rotation);
    
    // Return text bounds
    return computeTextBounds(painter, font, screenAligned, textWidth);
  }

  /** Left as a helper for subclasses. TODO delete me and subclass */
  protected void glRasterPos(IPainter painter, Coord3d sceneOffset,
      Coord3d screenPositionAligned3d) {
    if (spaceTransformer != null) {
      screenPositionAligned3d = spaceTransformer.compute(screenPositionAligned3d);
    }

    painter.raster(screenPositionAligned3d.add(sceneOffset), null);
  }

  /** Convert a 2D screen position to 3D world coordinate */
  protected Coord3d to3D(IPainter painter, Coord3d screen) {
    Coord3d model;
    try {
      model = painter.getCamera().screenToModel(painter, screen);
    } catch (RuntimeException e) {
      // TODO: solve this bug due to a Camera.PERSPECTIVE mode.
      LOGGER.error("could not process text position: " + screen + e.getMessage());
      return new Coord3d();
    }
    return model;
  }

  protected BoundingBox3d computeTextBounds(IPainter painter, Font font,
      Coord3d posScreenShifted, float strlen) {
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
