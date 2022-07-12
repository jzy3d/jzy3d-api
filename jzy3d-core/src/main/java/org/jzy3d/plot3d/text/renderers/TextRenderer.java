package org.jzy3d.plot3d.text.renderers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.text.AbstractTextRenderer;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.TextLayout;
import org.jzy3d.plot3d.text.align.Vertical;

/**
 * The {@link ITextRenderer} computes text layout according to {@link Horizontal}, {@link Vertical}
 * settings, text length, font size and text position. This is achieved with the help of
 * {@link TextLayout} processor.
 * 
 * It can be given a 2D offset in screen coordinates and a 3D offset in world coordinates that are
 * applied after the initial layout is processed according to the settings, the text length, the
 * font size and the text position.
 * 
 * It supports Unicode characters and all fonts made available by {@link java.awt.Font}.
 * 
 * Rendering text relies on {@link IPainter#drawText(Font, String, Coord3d, Color, float)} which was
 * introduced as of Jzy3D 2.0 and offers much more flexibility than the initial simple
 * {@link IPainter#glutBitmapString(int, String)} which only support two fonts.
 * 
 * Rotation of a text is always made from the center of the text, whatever the LEFT/CENTER/RIGHT
 * layout of the text, which lead to all these possible layouts.
 * 
 * <p><img src="doc-files/text-rotation.png"></p>
 * 
 * This image was generated with {@link ITTest_Text#main()}
 */
public class TextRenderer extends AbstractTextRenderer implements ITextRenderer {
  protected static Logger LOGGER = LogManager.getLogger(TextRenderer.class);

  protected TextLayout layout = new TextLayout();

  protected boolean showPosition = false;
  protected Color positionColor = Color.BLACK;
  protected float positionWidth = 3;
  
  protected boolean useGlutBitmap = false;

  
  /**
   * Draw a string at the specified position and return the 3d volume occupied by the string
   * according to the current Camera configuration.
   */
  @Override
  public BoundingBox3d drawText(IPainter painter, Font font, String text, Coord3d position,
      float rotation, Horizontal halign, Vertical valign, Color color, Coord2d screenOffset,
      Coord3d sceneOffset) {
    if (text == null) {
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
    if(!useGlutBitmap) {
      painter.drawText(font, text, positionAligned, color, rotation);
    }
    else {
      // Fallback on former way of rendering text
      painter.glutBitmapString(font, text, positionAligned, color); 
    }

    if (showPosition) {
      Point p = new Point(position, positionColor);
      p.setWidth(positionWidth);
      p.draw(painter);
    }
    // Return text bounds
    return computeTextBounds(painter, font, screenAligned, textWidth);
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

  protected BoundingBox3d computeTextBounds(IPainter painter, Font font, Coord3d posScreenShifted,
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

  public boolean isShowPosition() {
    return showPosition;
  }

  public void setShowPosition(boolean showPosition) {
    this.showPosition = showPosition;
  }

  public Color getPositionColor() {
    return positionColor;
  }

  public void setPositionColor(Color positionColor) {
    this.positionColor = positionColor;
  }

  public float getPositionWidth() {
    return positionWidth;
  }

  public void setPositionWidth(float positionWidth) {
    this.positionWidth = positionWidth;
  }

  public boolean isUseGlutBitmap() {
    return useGlutBitmap;
  }

  public void setUseGlutBitmap(boolean useGlutBitmap) {
    this.useGlutBitmap = useGlutBitmap;
  }
}
