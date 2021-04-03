package org.jzy3d.tests.nonreg;

import org.jzy3d.analysis.AWTAbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class ScatterAxisHasZLabelFlippingSides extends AWTAbstractAnalysis {
  public static void main(String[] args) throws Exception {
    AnalysisLauncher.open(new ScatterAxisHasZLabelFlippingSides());
  }

  public void init() {
    Coord3d[] points = new Coord3d[8];

    points[0] = new Coord3d(7.0404835, 905.0, 53.014378);
    points[1] = new Coord3d(7.0043426, 935.0, 53.014378);
    points[2] = new Coord3d(7.0404835, 905.0, 53.014378);
    points[3] = new Coord3d(6.9886136, 885.0, 151.9809);
    points[4] = new Coord3d(7.0043426, 935.0, 53.014378);
    points[5] = new Coord3d(6.96367, 915.0, 153.10077);
    points[6] = new Coord3d(6.9886136, 885.0, 151.9809);
    points[7] = new Coord3d(6.96367, 915.0, 153.10077);

    Scatter scatter = new Scatter(points, Color.BLUE, 5.0f);
    chart = AWTChartFactory.chart(Quality.Advanced);
    chart.getScene().add(scatter);
  }
}