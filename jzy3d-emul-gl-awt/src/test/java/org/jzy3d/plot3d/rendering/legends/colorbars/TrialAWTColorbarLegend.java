package org.jzy3d.plot3d.rendering.legends.colorbars;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;

public class TrialAWTColorbarLegend {

  public static void main(String[] args) throws IOException {
    AWTColorbarLegend colorbar = new AWTColorbarLegend(SampleGeom.surface(), new AxisLayout());

    // When query image with margin and pixel scale
    Dimension margin = new Dimension(20,240);
    Coord2d pixelScale = new Coord2d(2,2);
    int width = 300;
    int height = 600;
    
    BufferedImage i = colorbar.toImage(width, height, margin, pixelScale);
    
    // Then image is smaller than queried, due to margin
    Assert.assertEquals(width - margin.width, i.getWidth(null));
    Assert.assertEquals(height - margin.height, i.getHeight(null));
    
    ImageIO.write(i, "png", new File("target/colorbar.png"));
    
    
    // Viewport of colobar is reponsible of rendering image in the center
  }

}
