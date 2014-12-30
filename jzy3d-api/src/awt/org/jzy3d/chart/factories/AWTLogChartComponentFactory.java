package org.jzy3d.chart.factories;

import org.jzy3d.chart.Chart;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.primitives.axes.LogAxeBox;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public class AWTLogChartComponentFactory extends AWTChartComponentFactory {
    protected SpaceTransformer logTransformers;

    public AWTLogChartComponentFactory(SpaceTransformer transformers) {
        this.logTransformers = transformers;
    }

    public static Chart chart() {
        return chart(Quality.Intermediate);
    }

    public static Chart chart(Quality quality, SpaceTransformer transformers) {
        AWTLogChartComponentFactory f = new AWTLogChartComponentFactory(transformers);
        return f.newChart(quality, Toolkit.newt);
    }

    public static Chart chart(String toolkit, SpaceTransformer transformers) {
        AWTLogChartComponentFactory f = new AWTLogChartComponentFactory(transformers);
        return f.newChart(Chart.DEFAULT_QUALITY, toolkit);
    }

    public static Chart chart(Quality quality, Toolkit toolkit, SpaceTransformer transformers) {
        AWTLogChartComponentFactory f = new AWTLogChartComponentFactory(transformers);
        return f.newChart(quality, toolkit);
    }

    public static Chart chart(Quality quality, String toolkit, SpaceTransformer transformers) {
        AWTLogChartComponentFactory f = new AWTLogChartComponentFactory(transformers);
        return f.newChart(quality, toolkit);
    }

    @Override
    public IAxe newAxe(BoundingBox3d box, View view) {
        LogAxeBox axe = new LogAxeBox(box, logTransformers);
        axe.setScale(new Coord3d(10.0, 1.0, 1.0));
        axe.setView(view);
        return axe;
    }
}
