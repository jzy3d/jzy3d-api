package org.jzy3d.plot3d.rendering.legends.series;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorAWT;
import org.jzy3d.maths.Dimension;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot2d.primitive.AWTAbstractImageGenerator;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot3d.rendering.legends.AWTLegend;
import org.jzy3d.plot3d.rendering.view.IImageViewport;

import com.jogamp.opengl.GL2;

public class AWTSeriesLegend extends AWTLegend implements IImageViewport{
    public AWTSeriesLegend() {
        this(new ArrayList<Serie2d>());
    }

    public AWTSeriesLegend(List<Serie2d> series) {
        this(series, Color.BLACK, Color.WHITE);
    }

    public AWTSeriesLegend(List<Serie2d> series, Color foreground, Color background) {
        super(null, foreground, background);
        this.series = series;
        this.minimumDimension = new Dimension(AWTAbstractImageGenerator.MIN_BAR_WIDTH, AWTAbstractImageGenerator.MIN_BAR_HEIGHT);
        for(Serie2d serie : series){
            addSerie(serie);
        }
        initImageGenerator();
    }
    
    public void addSerie(Serie2d serie){
        serie.getDrawable().setLegend(this);
        series.add(serie);
    }

    public void initImageGenerator() {
        imageGenerator = new AWTAbstractImageGenerator() {
            @Override
            public BufferedImage toImage(int width, int height) {

                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphic = image.createGraphics();
                configureText(graphic);

                int steps = series.size();
                int step = 15;
                int y = step;
                int legendBorderHeight = step * (steps+1);

                drawBackground(width, legendBorderHeight, graphic);

                // print series
                for (Serie2d serie : series) {
                    Color color = getSerieColor(serie);
                    String text = getSerieText(serie);
                    drawSerieLineAndNameAtY(color, text, graphic, y);
                    y += 15;
                }

                // draw legend border
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

            private String getSerieText(Serie2d serie) {
                if (serie != null) {
                    return serie.getName();
                }
                return "unknown";
            }

            public Color getSerieColor(Serie2d serie) {
                if (serie != null) {
                    return serie.getColor();
                } 
                return Color.GRAY;
            }
        };
        //this.font = new java.awt.Font("Helvetica",0,12);
        //imageGenerator.setF
        imageGenerator.setHasBackground(true);
        imageGenerator.setFont(new java.awt.Font("Helvetica",0,12));
    }

    @Override
    public void render(Painter painter) {
    	painter.glEnable(GL2.GL_BLEND);
        super.render(painter);
    }

    @Override
    public BufferedImage toImage(int width, int height) {
        if (imageGenerator != null) {
            setGeneratorColors();
            return imageGenerator.toImage(Math.max(width - margin, 1), Math.max(height - margin, 1));
        }
        return null;
    }

    protected int margin = 25;
    protected List<Serie2d> series;
}
