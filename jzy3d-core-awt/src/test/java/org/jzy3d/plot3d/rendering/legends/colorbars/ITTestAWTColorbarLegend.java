package org.jzy3d.plot3d.rendering.legends.colorbars;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapGrayscale;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
import org.jzy3d.plot3d.primitives.axis.layout.providers.StaticTickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.DefaultDecimalTickRenderer;

public class ITTestAWTColorbarLegend {
  public static void main(String[] args) throws IOException {
    
    //ChartTester tester = new ChartTester();
    
    dumpColormap(new ColorMapGrayscale(), "target/ColorMapGrayscale.png");
    /*dumpColormap(new ColorMapHotCold(), "target/ColorMapHotCold.png");
    dumpColormap(new ColorMapRainbow(), "target/ColorMapRainbow.png");
    dumpColormap(new ColorMapRainbowNoBorder(), "target/ColorMapRainbowNoBorder.png");
    dumpColormap(new ColorMapRBG(), "target/ColorMapRBG.png");
    dumpColormap(new ColorMapRedAndGreen(), "target/ColorMapRedAndGreen.png");
    dumpColormap(new ColorMapWhiteBlue(), "target/ColorMapWhiteBlue.png");
    dumpColormap(new ColorMapWhiteGreen(), "target/ColorMapWhiteGreen.png");
    dumpColormap(new ColorMapWhiteRed(), "target/ColorMapWhiteRed.png");*/
  }

  protected static void dumpColormap(IColorMap colormap, String file) throws IOException {
    colormap.setDirection(false);

    
    double[] values = {-2,-1,0,1,2};

    ColorMapper mapper = new ColorMapper(colormap, -2, 2);
    
    StaticTickProvider p = new StaticTickProvider(values);
    AWTColorbarImageGenerator g = new AWTColorbarImageGenerator(mapper, p, new DefaultDecimalTickRenderer());
    g.setBackgroundColor(Color.WHITE);
    g.setHasBackground(true);
    g.setPixelScale(new Coord2d(3,3));
    
    BufferedImage i = g.toImage(200, 300, 20);
    ImageIO.write(i, "png", new File(file));
    System.out.println("Dumped " + file);
  }
}
