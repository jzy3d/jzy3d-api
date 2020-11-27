package org.jzy3d.plot3d.rendering.legends.series;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.maths.Dimension;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot2d.primitive.AWTAbstractImageGenerator;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot3d.rendering.legends.AWTLegend;
import org.jzy3d.plot3d.rendering.view.IImageViewport;

public class AWTSeriesLegend extends AWTLegend implements IImageViewport{
    private static final int LINE_HEIGHT = 15;
    private static final int FONT_HEIGHT = 12;
	public AWTSeriesLegend() {
        this(new ArrayList<Serie2d>());
    }

    public AWTSeriesLegend(Serie2d... series) {
        this(Arrays.asList( series), Color.BLACK, Color.WHITE);
    }
    
    public AWTSeriesLegend(List<Serie2d> series) {
        this(series, Color.BLACK, Color.WHITE);
    }

    public AWTSeriesLegend(List<Serie2d> series, Color foreground, Color background) {
        super(null, foreground, background);
        this.series = new ArrayList<>();
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
                    y += LINE_HEIGHT;
                }
                
                

                // draw legend border
                drawLegendBorder(graphic, width, legendBorderHeight);
                return image;
            }

            public void drawSerieLineAndNameAtY(Color color, String text, Graphics2D graphic, int y) {
                if (color != null) {
                    graphic.setColor(AWTColor.toAWT(color));
                    graphic.drawLine(0, y, LINE_HEIGHT, y);
                }
                graphic.drawString(text, LINE_HEIGHT+5, y + 5);
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
        imageGenerator.setFont(new java.awt.Font("Helvetica",0,FONT_HEIGHT));
    }

    @Override
    public void render(Painter painter) {
    	painter.glEnable_Blend();
        super.render(painter);
    }

    @Override
    public BufferedImage toImage(int width, int height) {
        if (imageGenerator != null) {
            setGeneratorColors();
            
            
            int iWidth = Math.max(width - margin, 1);
            int iHeight = 50;//Math.max(height - margin, 1);
            
            //setViewPort(iWidth, iHeight, float left, float right);
            
            return imageGenerator.toImage(iWidth, iHeight);
        }
        return null;
    }

    protected int margin = 25;
    protected List<Serie2d> series;
}
