package org.jzy3d.plot3d.text.renderers;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.AbstractTextRenderer;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * could enhance using http://www.angelcode.com/products/bmfont/
 * 
 * @author Martin
 */
public class TextBitmapRenderer extends AbstractTextRenderer implements ITextRenderer {
    /**
     * The TextBitmap class provides support for drawing ASCII characters Any
     * non ascii caracter will be replaced by a square.
     */
    public TextBitmapRenderer() {
        super();
        font = GLUT.BITMAP_HELVETICA_10;
        fontHeight = 10;
    }

    @Override
    public void drawSimpleText(GL gl, GLU glu, Camera cam, String s, Coord3d position, Color color) {
        if (gl.isGL2()) {
            gl.getGL2().glColor3f(color.r, color.g, color.b);
            gl.getGL2().glRasterPos3f(position.x, position.y, position.z);
        } else {
            GLES2CompatUtils.glColor3f(color.r, color.g, color.b);
            GLES2CompatUtils.glRasterPos3f(position.x, position.y, position.z);
        }

        glut.glutBitmapString(font, s);
    }

    /**
     * Draw a string at the specified position and compute the 3d volume
     * occupied by the string according to the current Camera configuration.
     */
    @Override
    public BoundingBox3d drawText(GL gl, GLU glu, Camera cam, String text, Coord3d position, Halign halign, Valign valign, Color color, Coord2d screenOffset, Coord3d sceneOffset) {
        color(gl, color);

        // compute a corrected position according to layout
        Coord3d posScreen = cam.modelToScreen(gl, glu, position);
        float strlen = glut.glutBitmapLength(font, text);
        float x = computeXWithAlign(halign, posScreen, strlen, 0.0f);
        float y = computeYWithAlign(valign, posScreen, 0.0f);
        Coord3d posScreenShifted = new Coord3d(x + screenOffset.x, y + screenOffset.y, posScreen.z);
        
        Coord3d posReal;
        try {
            posReal = cam.screenToModel(gl, glu, posScreenShifted);
        } catch (RuntimeException e) { 
            // TODO: solve this bug due to a Camera.PERSPECTIVE mode.
            Logger.getLogger(TextBitmapRenderer.class).error("TextBitmap.drawText(): could not process text position: " + posScreen + " " + posScreenShifted);
            return new BoundingBox3d();
        }

        // Draws actual string
        glRasterPos(gl, sceneOffset, posReal);
        glut.glutBitmapString(font, text);
        return computeTextBounds(gl, glu, cam, posScreenShifted, strlen);
    }

    public void glRasterPos(GL gl, Coord3d sceneOffset, Coord3d posReal) {
        if (gl.isGL2()) {
            gl.getGL2().glRasterPos3f(posReal.x + sceneOffset.x, posReal.y + sceneOffset.y, posReal.z + sceneOffset.z);
        } else {
            GLES2CompatUtils.glRasterPos3f(posReal.x + sceneOffset.x, posReal.y + sceneOffset.y, posReal.z + sceneOffset.z);
        }
    }

    public BoundingBox3d computeTextBounds(GL gl, GLU glu, Camera cam, Coord3d posScreenShifted, float strlen) {
        Coord3d botLeft = new Coord3d();
        Coord3d topRight = new Coord3d();
        botLeft.x = posScreenShifted.x;
        botLeft.y = posScreenShifted.y;
        botLeft.z = posScreenShifted.z;
        topRight.x = botLeft.x + strlen;
        topRight.y = botLeft.y + fontHeight;
        topRight.z = botLeft.z;

        BoundingBox3d txtBounds = new BoundingBox3d();
        txtBounds.add(cam.screenToModel(gl, glu, botLeft));
        txtBounds.add(cam.screenToModel(gl, glu, topRight));
        return txtBounds;
    }

    public void color(GL gl, Color color) {
        if (gl.isGL2()) {
            gl.getGL2().glColor3f(color.r, color.g, color.b);
        } else {
            GLES2CompatUtils.glColor3f(color.r, color.g, color.b);
        }
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

    protected static GLUT glut = new GLUT();
    protected int fontHeight;
    protected int font;
}
