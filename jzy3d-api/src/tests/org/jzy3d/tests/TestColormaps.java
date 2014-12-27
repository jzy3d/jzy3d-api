package org.jzy3d.tests;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.Test;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapGrayscale;
import org.jzy3d.colors.colormaps.ColorMapHotCold;
import org.jzy3d.colors.colormaps.ColorMapRBG;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.colors.colormaps.ColorMapRainbowNoBorder;
import org.jzy3d.colors.colormaps.ColorMapRedAndGreen;
import org.jzy3d.colors.colormaps.ColorMapWhiteBlue;
import org.jzy3d.colors.colormaps.ColorMapWhiteGreen;
import org.jzy3d.colors.colormaps.ColorMapWhiteRed;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.junit.ChartTest;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
import org.jzy3d.utils.LoggerUtils;

public class TestColormaps extends ChartTest {
	@Test
	public void testColormaps() throws IOException {
		LoggerUtils.minimal();

		execute(new ColorMapGrayscale(), true);
		execute(new ColorMapHotCold(), true);
		execute(new ColorMapRainbow(), true);
		execute(new ColorMapRainbowNoBorder(), true);
		execute(new ColorMapRBG(), true);
		execute(new ColorMapRedAndGreen(), true);
		execute(new ColorMapWhiteBlue(), true);
		execute(new ColorMapWhiteGreen(), true);
		execute(new ColorMapWhiteRed(), true);

		execute(new ColorMapGrayscale(), false);
		execute(new ColorMapHotCold(), false);
		execute(new ColorMapRainbow(), false);
		execute(new ColorMapRainbowNoBorder(), false);
		execute(new ColorMapRBG(), false);
		execute(new ColorMapRedAndGreen(), false);
		execute(new ColorMapWhiteBlue(), false);
		execute(new ColorMapWhiteGreen(), false);
		execute(new ColorMapWhiteRed(), false);
	}

	public void execute(IColorMap colormap, boolean direction)
			throws IOException {
		String file = "colormaps/" + colormap.getClass().getSimpleName();
		if (!direction)
			file += "-inv";
//		execute(makeColormapImage(colormap, direction),
//				getTestCaseFileName(file));
	}

	protected BufferedImage makeColormapImage(IColorMap colormap,
			boolean direction) throws IOException {
		colormap.setDirection(direction);
		ColorMapper mapper = new ColorMapper(colormap, 0, 1);
		AWTColorbarImageGenerator g = new AWTColorbarImageGenerator(mapper, null,
				null);
		return g.toImage(20, 300, 19);
	}
}
