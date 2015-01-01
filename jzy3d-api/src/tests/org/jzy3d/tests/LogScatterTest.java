package org.jzy3d.tests;

import java.util.ArrayList;
import java.util.Random;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.log.AxeTransformableConcurrentScatterMultiColorList;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.transform.space.SpaceTransformLog;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public class LogScatterTest extends AbstractAnalysis {
    public static void main(String[] args) throws Exception {
        AnalysisLauncher.open(new LogScatterTest());
    }

    SpaceTransformer spaceTransformer = new SpaceTransformer(null, null, new SpaceTransformLog());

    public void init() {
        int size = 500000;
        float x;
        float y;
        float z;
        float a;

        ArrayList<Coord3d> points = new ArrayList<Coord3d>();
        Color[] colors = new Color[size];

        Random r = new Random();
        r.setSeed(0);

        for (int i = 0; i < size; i++) {
            x = r.nextFloat() + 0.1f;
            y = r.nextFloat() + 0.1f;
            z = r.nextFloat() + 0.1f;
            points.add(new Coord3d(x, y, z));
            a = 0.25f;
            colors[i] = new Color(x, y, z, a);
        }

        AxeTransformableConcurrentScatterMultiColorList scatter = new AxeTransformableConcurrentScatterMultiColorList(points, new ColorMapper(new ColorMapRainbow(), 0.1, 1.1, new Color(1, 1, 1, .5f)), spaceTransformer);
        // chart = AWTLogChartComponentFactory.chart(Quality.Advanced, "awt",
        // spaceTransformer);
        chart = AWTChartComponentFactory.chart(Quality.Advanced, "awt");
        chart.getView().getAxe().setSpaceTransformer(spaceTransformer);
        chart.getView().setSpaceTransformer(spaceTransformer);
        chart.getScene().add(scatter);
    }
}
