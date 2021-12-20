package org.jzy3d.emulgl.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Disk;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class ITTestEmulGLDisk {
  @Test
  public void whenDiskChart_ThenMatchBaselineImagePixelwise() {

    // Given
    EmulGLChartFactory factory = new EmulGLChartFactory();
    Chart chart = factory.newChart(Quality.Advanced().setHiDPIEnabled(false));

    // When
    chart.add(new Disk(new Coord3d(0,0,00), 3.f, 11.f, 24, 5, Color.BLUE));
    chart.add(new Disk(new Coord3d(0,0,10), 3.f, 11.f, 24, 5, Color.RED));

    // Then
    ChartTester tester = new ChartTester();
    tester.assertSimilar(chart,
        ChartTester.EXPECTED_IMAGE_FOLDER + this.getClass().getSimpleName() + ".png");
  }

}
