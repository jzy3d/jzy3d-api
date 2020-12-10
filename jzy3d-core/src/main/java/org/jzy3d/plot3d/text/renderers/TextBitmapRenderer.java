package org.jzy3d.plot3d.text.renderers;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Painter;
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
    
    // Font constants below are picked from GLU object in JOGL
	public static final int STROKE_ROMAN = 0;
	public static final int STROKE_MONO_ROMAN = 1;
	public static final int BITMAP_9_BY_15 = 2;
	public static final int BITMAP_8_BY_13 = 3;
	public static final int BITMAP_TIMES_ROMAN_10 = 4;
	public static final int BITMAP_TIMES_ROMAN_24 = 5;
	public static final int BITMAP_HELVETICA_10 = 6;
	public static final int BITMAP_HELVETICA_12 = 7;
	public static final int BITMAP_HELVETICA_18 = 8;

    
    /**
     * GL Font code and size in pixel to initialize rendeer.
     */
    public enum Font{
    	
        Helvetica_10(BITMAP_HELVETICA_10, 10),
        Helvetica_12(BITMAP_HELVETICA_12, 12),
        Helvetica_18(BITMAP_HELVETICA_18, 18),
        TimesRoman_10(BITMAP_TIMES_ROMAN_10, 10),
        TimesRoman_24(BITMAP_TIMES_ROMAN_24, 24);
        
        Font(int code, int height){
            this.code = code;
            this.height = height;
        }
        
        protected int code;
        protected int height;
    }
    /**
     * The TextBitmap class provides support for drawing ASCII characters Any
     * non ascii caracter will be replaced by a square.
     */
    public TextBitmapRenderer() {
        this(Font.Helvetica_10);
    }

    public TextBitmapRenderer(Font font) {
        this(font.code, font.height);
    }
    
    public TextBitmapRenderer(int font, int fontSize) {
        super();
        this.fontId = font;
        this.fontHeight = fontSize;
    }

    @Override
    public void drawSimpleText(Painter painter, Camera cam, String s, Coord3d position, Color color) {
        glRaster(painter, position, color);
    	
        painter.glutBitmapString(fontId, s);
    }

    /**
     * Draw a string at the specified position and compute the 3d volume
     * occupied by the string according to the current Camera configuration.
     */
    @Override
    public BoundingBox3d drawText(Painter painter, String text, Coord3d position, Halign halign, Valign valign, Color color, Coord2d screenOffset, Coord3d sceneOffset) {
        painter.color(color);

        // compute a corrected position according to layout
        Coord3d posScreen = painter.getCamera().modelToScreen(painter, position);
        float strlen = painter.glutBitmapLength(fontId, text);
        float x = computeXWithAlign(halign, posScreen, strlen, 0.0f);
        float y = computeYWithAlign(valign, posScreen, 0.0f);
        Coord3d posScreenShifted = new Coord3d(x + screenOffset.x, y + screenOffset.y, posScreen.z);
        
        Coord3d posReal;
        try {
            posReal = painter.getCamera().screenToModel(painter, posScreenShifted);
        } catch (RuntimeException e) { 
            // TODO: solve this bug due to a Camera.PERSPECTIVE mode.
            LOGGER.error("could not process text position: " + posScreen + " " + posScreenShifted + e.getMessage());
            return new BoundingBox3d();
        }

        //LOGGER.info(text + " @ " + position + " projected to : " + posScreen + " shifted to " + posScreenShifted);
        //LOGGER.info(text + " @ " + posReal + " with offset : " + sceneOffset);
        
        // Draws actual string
        glRasterPos(painter, sceneOffset, posReal);
        painter.glutBitmapString(fontId, text);
        
        return computeTextBounds(painter, posScreenShifted, strlen);
    }

    public void glRasterPos(Painter painter, Coord3d sceneOffset, Coord3d posReal) {
        if(spaceTransformer!=null){
            posReal = spaceTransformer.compute(posReal);
        }
        
        painter.raster(posReal.add(sceneOffset), null);
    }

    protected BoundingBox3d computeTextBounds(Painter painter, Coord3d posScreenShifted, float strlen) {
        Coord3d botLeft = new Coord3d();
        Coord3d topRight = new Coord3d();
        botLeft.x = posScreenShifted.x;
        botLeft.y = posScreenShifted.y;
        botLeft.z = posScreenShifted.z;
        topRight.x = botLeft.x + strlen;
        topRight.y = botLeft.y + fontHeight;
        topRight.z = botLeft.z;

        BoundingBox3d txtBounds = new BoundingBox3d();
        txtBounds.add(painter.getCamera().screenToModel(painter, botLeft));
        txtBounds.add(painter.getCamera().screenToModel(painter, topRight));
        return txtBounds;
    }

    public float computeYWithAlign(Valign valign, Coord3d posScreen, float y) {
        if (valign == Valign.TOP)
            y = posScreen.y;
        else if (valign == Valign.GROUND)
            y = posScreen.y;
        else if (valign == Valign.CENTER)
            y = posScreen.y - fontHeight / 2;
        else if (valign == Valign.BOTTOM)
            y = posScreen.y - fontHeight;
        return y;
    }

    public float computeXWithAlign(Halign halign, Coord3d posScreen, float strlen, float x) {
        if (halign == Halign.RIGHT)
            x = posScreen.x;
        else if (halign == Halign.CENTER)
            x = posScreen.x - strlen / 2;
        else if (halign == Halign.LEFT)
            x = posScreen.x - strlen;
        return x;
    }

    /* */

    protected int fontHeight;
    protected int fontId;
}
