package org.jzy3d.factories;

import org.jzy3d.chart.ChartScene;

public class SceneFactory {
    public ChartScene getInstance(boolean sort){
        return new ChartScene(sort);
    }
}
