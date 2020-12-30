package org.jzy3d.plot3d.primitives.axes.symbols;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.symbols.SymbolHandler;

/**
 * Create {@link DrawableTexture} symbols based on an {@link java.awt.Shape}
 * 
 * @author martin
 */
public class EmulGLSymbolHandler extends SymbolHandler{
    protected Shape awtShape;
    
    public EmulGLSymbolHandler(int n, Shape awtShape) {
        super(n);
        this.awtShape = awtShape;
    }

    @Override
    public void addSymbolOn(Point point) {
        Color face = point.rgb;
        float size = 1;
        Coord3d position = point.xyz;

        /*List<Coord2d> zmapping = TexturedCube.makeZPlaneTextureMapping(position, size);
        
        // TODO : let the SAME buffered image instance be used by all DrawableTextures
        BufferedImage image = getImage(awtShape, 100, 100);
        BufferedImageTexture t = new BufferedImageTexture(image);
        DrawableTexture dt = new DrawableTexture(t, PlaneAxis.Z, position.z, zmapping, face);*/

        //symbols.add(dt);
    }

    public static BufferedImage getImage(Shape shape, int width, int height) {
        return getImage(shape, width, height, BufferedImage.TYPE_4BYTE_ABGR);
    }
    
    public static BufferedImage getImage(Shape shape, int width, int height, int imageType) {
        BufferedImage bimage = new BufferedImage(width, height, imageType);
        Graphics2D g2d = bimage.createGraphics();
        g2d.fill(shape);
        g2d.dispose();
        return bimage;
    }

}
