package org.jzy3d.plot3d.text.renderers;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.IPainter.Font;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.AbstractTextRenderer;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Halign;
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
	 * The TextBitmap class provides support for drawing ASCII characters Any non
	 * ascii caracter will be replaced by a square.
	 */
	public TextBitmapRenderer() {
		this(Font.Helvetica_10);
	}

	public TextBitmapRenderer(Font font) {
		this.font = font;
	}

	@Override
	public void drawSimpleText(IPainter painter, Camera cam, String s, Coord3d position, Color color) {
		glRaster(painter, position, color);

		painter.glutBitmapString(font.getCode(), s);
	}

	/**
	 * Draw a string at the specified position and return the 3d volume occupied by
	 * the string according to the current Camera configuration.
	 */
	@Override
	public BoundingBox3d drawText(IPainter painter, String text, Coord3d position, Halign halign, Valign valign,
			Color color, Coord2d screenOffset, Coord3d sceneOffset) {
		painter.color(color);

		// compute a corrected position according to layout
		float stringLength = painter.glutBitmapLength(font.getCode(), text);

		Coord3d screenPosition = painter.getCamera().modelToScreen(painter, position);
		Coord3d screenPositionAligned = alignScreenPosition(halign, valign, screenOffset, screenPosition, stringLength);
		Coord3d screenPositionAligned3d = toModelViewPosition(painter, screenPosition, screenPositionAligned);

		// process space stransform if any (log, etc)
		if (spaceTransformer != null) {
			screenPositionAligned3d = spaceTransformer.compute(screenPositionAligned3d);
		}
		screenPositionAligned3d = screenPositionAligned3d.add(sceneOffset);

		// Draws actual string
		painter.glutBitmapString(font, text, screenPositionAligned3d, Color.BLACK);

		// Return text bounds
		return computeTextBounds(painter, screenPositionAligned, stringLength);
	}

	/** Left as a helper for subclasses*/
	protected void glRasterPos(IPainter painter, Coord3d sceneOffset, Coord3d screenPositionAligned3d) {
		if (spaceTransformer != null) {
			screenPositionAligned3d = spaceTransformer.compute(screenPositionAligned3d);
		}

		painter.raster(screenPositionAligned3d.add(sceneOffset), null);
	}

	protected Coord3d toModelViewPosition(IPainter painter, Coord3d screenPosition, Coord3d screenPositionAligned) {
		Coord3d screenPositionAligned3d;
		try {
			screenPositionAligned3d = painter.getCamera().screenToModel(painter, screenPositionAligned);
		} catch (RuntimeException e) {
			// TODO: solve this bug due to a Camera.PERSPECTIVE mode.
			LOGGER.error("could not process text position: " + screenPosition + " " + screenPositionAligned
					+ e.getMessage());
			return new Coord3d();
		}
		return screenPositionAligned3d;
	}

	protected Coord3d alignScreenPosition(Halign halign, Valign valign, Coord2d screenOffset, Coord3d screenPosition,
			float stringLength) {
		float x = computeXAlign(halign, screenPosition, stringLength, 0.0f);
		float y = computeYAlign(valign, screenPosition, 0.0f);
		Coord3d screenPositionAligned = new Coord3d(x + screenOffset.x, y + screenOffset.y, screenPosition.z);
		return screenPositionAligned;
	}

	protected BoundingBox3d computeTextBounds(IPainter painter, Coord3d posScreenShifted, float strlen) {
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

	protected float computeYAlign(Valign valign, Coord3d posScreen, float y) {
		if (valign == Valign.TOP)
			y = posScreen.y;
		else if (valign == Valign.GROUND)
			y = posScreen.y;
		else if (valign == Valign.CENTER)
			y = posScreen.y - font.getHeight() / 2;
		else if (valign == Valign.BOTTOM)
			y = posScreen.y - font.getHeight();
		return y;
	}

	protected float computeXAlign(Halign halign, Coord3d posScreen, float strlen, float x) {
		if (halign == Halign.RIGHT)
			x = posScreen.x;
		else if (halign == Halign.CENTER)
			x = posScreen.x - strlen / 2;
		else if (halign == Halign.LEFT)
			x = posScreen.x - strlen;
		return x;
	}
}
