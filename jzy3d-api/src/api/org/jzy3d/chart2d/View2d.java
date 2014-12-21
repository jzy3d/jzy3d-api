package org.jzy3d.chart2d;

import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTView;

public class View2d extends AWTView {
    public View2d(IChartComponentFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        super(factory, scene, canvas, quality);
    }

    @Override
    public Coord3d computeSceneScaling() {
        return squarify();
    }
}
