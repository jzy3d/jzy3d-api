package org.jzy3d.plot3d.rendering.legends.colorbars;

import org.junit.Test;
import org.jzy3d.colors.colormaps.ColorMapGrayscale;
import org.jzy3d.colors.colormaps.ColorMapHotCold;
import org.jzy3d.colors.colormaps.ColorMapRBG;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.colors.colormaps.ColorMapRainbowNoBorder;
import org.jzy3d.colors.colormaps.ColorMapRedAndGreen;
import org.jzy3d.colors.colormaps.ColorMapWhiteBlue;
import org.jzy3d.colors.colormaps.ColorMapWhiteGreen;
import org.jzy3d.colors.colormaps.ColorMapWhiteRed;
import org.jzy3d.junit.ChartTester;

/**
 * Actually part of core-awt. Test moved to emulgl-awt to access ChartTester without having cyclic
 * redundancy between core-awt and chart-tester.
 *
 */
public class ITTestAWTColorbarLegend {

  @Test
  public void test() {

    ChartTester tester = new ChartTester();

    tester.assertSimilar(new ColorMapGrayscale(), tester.path(ColorMapGrayscale.class));
    
    tester.assertSimilar(new ColorMapHotCold(), tester.path(ColorMapHotCold.class));
    tester.assertSimilar(new ColorMapRainbow(), tester.path(ColorMapRainbow.class));
    tester.assertSimilar(new ColorMapRainbowNoBorder(), tester.path(ColorMapRainbowNoBorder.class));
    tester.assertSimilar(new ColorMapRBG(), tester.path(ColorMapRBG.class));
    
    tester.assertSimilar(new ColorMapRedAndGreen(), tester.path(ColorMapRedAndGreen.class));

    tester.assertSimilar(new ColorMapWhiteBlue(), tester.path(ColorMapWhiteBlue.class));
    tester.assertSimilar(new ColorMapWhiteGreen(), tester.path(ColorMapWhiteGreen.class));
    tester.assertSimilar(new ColorMapWhiteRed(), tester.path(ColorMapWhiteRed.class));

  }

}
