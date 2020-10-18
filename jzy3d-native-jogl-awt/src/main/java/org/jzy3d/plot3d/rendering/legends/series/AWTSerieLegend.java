package org.jzy3d.plot3d.rendering.legends.series;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorAWT;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.maths.Dimension;
import org.jzy3d.plot2d.primitive.AWTAbstractImageGenerator;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.rendering.legends.AWTLegend;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class AWTSerieLegend extends AWTLegend {
    public AWTSerieLegend(Serie2d serie) {
        this(serie.getDrawable());
        this.serie = serie;
    }

    public AWTSerieLegend(AbstractDrawable drawable) {
        this(drawable, Color.BLACK, Color.WHITE);
    }

    public AWTSerieLegend(AbstractDrawable drawable, Color foreground, Color background) {
        super(drawable, foreground, background);
        this.minimumDimension = new Dimension(AWTAbstractImageGenerator.MIN_BAR_WIDTH, AWTAbstractImageGenerator.MIN_BAR_HEIGHT);

        drawable.setLegend(this);
        initImageGenerator();
        imageGenerator.setHasBackground(true);
        imageGenerator.setFont(new java.awt.Font("Helvetica", 0, 12));
        setGeneratorColors();

    }

    public void initImageGenerator() {
        imageGenerator = new AWTAbstractImageGenerator() {
            @Override
            public BufferedImage toImage(int width, int height) {
                Color color = getSerieColor();
                String text = getSerieText();

                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphic = image.createGraphics();
                configureText(graphic);

                int legendBorderHeight = 30;
                int y = 15;

                drawBackground(width, legendBorderHeight, graphic);
                drawSerieLineAndNameAtY(color, text, graphic, y);
                drawLegendBorder(graphic, width, legendBorderHeight);
                return image;
            }

            public void drawSerieLineAndNameAtY(Color color, String text, Graphics2D graphic, int y) {
                if (color != null) {
                    graphic.setColor(ColorAWT.toAWT(color));
                    graphic.drawLine(0, y, 15, y);
                }
                graphic.drawString(text, 20, y + 5);
            }

            private String getSerieText() {
                if(serie!=null){
                    return serie.getName();
                }
                return "unknown";
            }

            public Color getSerieColor() {
                if(serie!=null){
                    return serie.getColor();
                }
                else if (drawable instanceof ISingleColorable) {
                    return ((ISingleColorable) drawable).getColor();
                }
                return null;
            }

        };
    }

    @Override
    public void render(GL gl, GLU glu) {
        gl.glEnable(GL2.GL_BLEND);
        super.render(gl, glu);
    }

    @Override
    public BufferedImage toImage(int width, int height) {
        if (imageGenerator != null) {
            setGeneratorColors();
            return imageGenerator.toImage(Math.max(width - 25, 1), Math.max(height - 25, 1));
        }
        return null;
    }

    protected Serie2d serie;
}
