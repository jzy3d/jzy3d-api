package org.jzy3d.chart2d;

import org.apache.logging.log4j.LogManager;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

public class AxisBox2d extends AxisBox {
  public AxisBox2d(BoundingBox3d bbox, IAxisLayout layout) {
    super(bbox, layout);
  }

  public AxisBox2d(BoundingBox3d bbox) {
    super(bbox);
    textRenderer = new TextBitmapRenderer();
    // txt = new JOGLTextRenderer(new DefaultTextStyle(java.awt.Color.BLACK));// ATTENTION AWT!!
  }

  /* */

  /**
   * Renders only X and Y ticks.
   */
  @Override
  public void drawTicksAndLabels(IPainter painter) {
    wholeBounds.reset();
    wholeBounds.add(boxBounds);

    drawTicksAndLabelsX(painter);
    drawTicksAndLabelsY(painter);
  }

  /** Force given X axis to be used for tick placement */
  @Override
  protected int findClosestXaxe(Camera cam) {
    return 0;
  }

  /** Force given Y axis to be used for tick placement */
  @Override
  protected int findClosestYaxe(Camera cam) {
    return 3;
  }

  
  /*
   * THESE TWO METHOD HAVE MOVED TO AxisLabelProcessor
   * @Override
  public Horizontal align(Horizontal hal, int direction, Camera cam, Coord3d tickPosition) {
    if (direction == AXE_X) {
      return Horizontal.CENTER;
    } else if (direction == AXE_Y) {
      return Horizontal.LEFT;
    } else {
      return Horizontal.LEFT;
    }
  }

  @Override
  public void drawAxisLabel(IPainter painter, int direction, Color color,
      BoundingBox3d ticksTxtBounds, Coord3d labelPosition, String axeLabel, float rotation, Coord2d offset) {
    BoundingBox3d labelBounds = null;

    if (isXAxeLabelDisplayed(direction)) {
      // doTransform(gl);
      labelBounds = textRenderer.drawText(painter, getLayout().getFont(), axeLabel,
          labelPosition, Horizontal.CENTER, Vertical.CENTER, color);
    } else if (isYAxeLabelDisplayed(direction)) {
      labelBounds = textRenderer.drawText(painter, getLayout().getFont(), axeLabel,
          labelPosition, Horizontal.CENTER, Vertical.CENTER, color);
      // labelBounds = txtRotation.drawText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER,
      // Valign.CENTER, color);
    }
    if (labelBounds != null)
      ticksTxtBounds.add(labelBounds);
    doTransform(painter);
  }*/

  /* VERTICAL ROTATION */

  protected RotatedTextBitmapRenderer txtRotation = new RotatedTextBitmapRenderer();

  /* ROTATED TEXT BITMAP RENDERER NOT WORKING PROPERLY */

  public class RotatedTextBitmapRenderer extends TextBitmapRenderer {
    @Override
    public BoundingBox3d drawText(IPainter painter, Font font, String text, Coord3d position,
        float rotation, Horizontal halign, Vertical valign, Color color, Coord2d screenOffset, Coord3d sceneOffset) {
      painter.color(color);

      // compute a corrected position according to layout
      Coord3d screen = painter.getCamera().modelToScreen(painter, position);
      float textWidth = painter.glutBitmapLength(font.getCode(), text);
      float textHeight = font.getHeight();
      
      float x = layout.computeXAlign(textWidth, halign, screen, 0.0f);
      float y = layout.computeYAlign(textHeight, valign, screen, 0.0f);
      Coord3d screenAligned = new Coord3d(x + screenOffset.x, y + screenOffset.y, screen.z);

      Coord3d posReal;
      try {
        posReal = painter.getCamera().screenToModel(painter, screenAligned);
      } catch (RuntimeException e) {
        LogManager.getLogger(TextBitmapRenderer.class)
            .error("TextBitmap.drawText(): could not process text position: " + screen + " "
                + screenAligned);
        return new BoundingBox3d();
      }

      // Draws actual string <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
      rotateText(painter, posReal); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<
      // CETTE ROTATION NE MARCHE PAS ET AFFECTE LE BON RENDU QUAND ON UTILISE BOUNDING POLICY!!

      glRasterPos(painter, sceneOffset, Coord3d.ORIGIN);
      painter.glutBitmapString(font.getCode(), text);

      return computeTextBounds(painter, font, screenAligned, textWidth);
    }

    // CUSTOM ROTATION

    public void rotateText(IPainter painter, Coord3d posReal) {
      painter.glPushMatrix();

      painter.glMatrixMode_ModelView();
      painter.glLoadIdentity();
      painter.glRotatef(90, 0, 0, 1);
      // rotateOf(gl, 90, AXE_Z);
      // translateTo(gl, posReal, false);

      if (false)
        painter.glTranslatef(-posReal.x, -posReal.y, -posReal.z);
      else
        painter.glTranslatef(posReal.x, posReal.y / 2, posReal.z);

      painter.glScalef(scale.x, scale.y, scale.z);
      painter.glPopMatrix();
    }
  }


  /*
   * public void vertical(GL gl, Coord3d currentPosition, int direction) { translateTo(gl,
   * currentPosition, false); rotateOf(gl, 90, direction); translateTo(gl, currentPosition, true); }
   * 
   * public void verticalSimple(GL gl) { gl.getGL2().glRotatef(90, 0, 0, 1); }
   */
}
