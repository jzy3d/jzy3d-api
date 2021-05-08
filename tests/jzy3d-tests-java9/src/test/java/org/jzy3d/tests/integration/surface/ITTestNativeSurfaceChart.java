package org.jzy3d.tests.integration.surface;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.AWTPainterFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.tests.integration.ITTest;
import org.jzy3d.utils.LoggerUtils;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

public class ITTestNativeSurfaceChart {
  @Test
  public void surfaceTest() {
    LoggerUtils.minimal();

    // When
    GLCapabilities c = new GLCapabilities(GLProfile.get(GLProfile.GL2));
    c.setOnscreen(false);
    c.setDoubleBuffered(false);
    c.setAlphaBits(8);
    c.setRedBits(8);
    c.setBlueBits(8);
    c.setGreenBits(8);



    IPainterFactory p = new AWTPainterFactory(c);
    IChartFactory factory = new AWTChartFactory(p);

    // AWTChartFactory factory = new AWTChartFactory();
    factory.getPainterFactory().setOffscreen(700, 600);

    // HIDPI TO AVOID AN FAILING ACCESS OF JOGL TO JAVA.DESKTOP
    Chart chart = factory.newChart(Quality.Advanced().setHiDPIEnabled(false));

    chart.add(ITTest.surface());

    // Then
    ITTest.assertChart(chart, this.getClass());
  }
}
