package org.jzy3d.plot3d.primitives.symbols;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.rendering.view.Camera;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

/**
 * SymbolHandlers are used to configure a symbol when rendering a {@link LineStrip}.
 * 
 * @author martin
 *
 */
public abstract class SymbolHandler {
    protected List<AbstractDrawable> symbols = null;

    public SymbolHandler(int n) {
        symbols = new ArrayList<AbstractDrawable>(n);
    }


    public void clear() {
        symbols.clear();

    }
    public abstract void addSymbolOn(Point point);


    public void drawSymbols(GL gl, GLU glu, Camera cam) {
        for (AbstractDrawable d : symbols) {
            d.draw(gl, glu, cam);
        }
    }

}
