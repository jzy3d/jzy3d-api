package org.jzy3d.demos.lines;

import java.util.concurrent.Executors;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.providers.SmartTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.IntegerTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.CroppingView;
import org.jzy3d.plot3d.rendering.view.View;

public class WorldMapDemo extends AbstractAnalysis {
    public static void main(String[] args) throws Exception {
        AnalysisLauncher.open(new WorldMapDemo());
    }

    public void init() throws Exception {
        // Create the world map chart
        AWTChartComponentFactory f = new AWTChartComponentFactory() {
            @Override
            public View newView(Scene scene, ICanvas canvas, Quality quality) {
                return new CroppingView(getFactory(), scene, canvas, quality);
            }
        };
        chart = f.newChart(Quality.Advanced, "awt");

        // Instantiate world map and parse the file
        WorldMapLoader worldMap = new WorldMapLoader();
        worldMap.parseFile("data/csvfiles/world_map.csv");

        // Add world map line stripe to chart
        chart.getScene().getGraph().add(worldMap.lineStrips);

        // Set axis labels for chart
        IAxeLayout axeLayout = chart.getAxeLayout();
        axeLayout.setXAxeLabel("Longitude (deg)");
        axeLayout.setYAxeLabel("Latitude (deg)");
        axeLayout.setZAxeLabel("Altitude (km)");

        // Set precision of tick values
        axeLayout.setXTickRenderer(new IntegerTickRenderer());
        axeLayout.setYTickRenderer(new IntegerTickRenderer());
        axeLayout.setZTickRenderer(new IntegerTickRenderer());

        // Define ticks for axis
        axeLayout.setXTickProvider(new SmartTickProvider(10));
        axeLayout.setYTickProvider(new SmartTickProvider(10));
        axeLayout.setZTickProvider(new SmartTickProvider(10));

        // Set map viewpoint
        chart.getView().setViewPoint(new Coord3d(-2 * Math.PI / 3, Math.PI / 4, 0));

        // Animate bounds change for demo
        Executors.newCachedThreadPool().execute(shiftBoundsTask());
    }

    private Runnable shiftBoundsTask() {
        return new Runnable() {
            int step = 1;

            @Override
            public void run() {
                while (true) {
                    
                    BoundingBox3d b = chart.getView().getBounds();
                    chart.getView().setScaleX(b.getXRange().add(step), false);
                    chart.getView().setScaleY(b.getYRange().add(step), false);
                    chart.getView().shoot();
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                    }
                }
            }

        };
    }
}