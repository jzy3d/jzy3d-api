package org.jzy3d.factories;

import org.jzy3d.chart.ChartView;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;

public class ViewFactory {
    public View getInstance(Scene scene, ICanvas canvas, Quality quality){
        return new ChartView(scene, canvas, quality);
    }
}
