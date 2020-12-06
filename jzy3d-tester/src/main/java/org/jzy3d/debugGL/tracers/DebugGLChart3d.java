package org.jzy3d.debugGL.tracers;


import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.debugGL.primitives.Cube;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/**
 * Display elements of a 3d scene
 * <ul>
 * <li>axis box
 * <li>camera parameters (eye, target, up)
 * </ul>
 * 
 * @author martin
 */
public class DebugGLChart3d {

    // watched
    Chart watchedChart;
    SpaceTransformer spaceTransform;
    
    // debug
    Chart debugChart;
    Point cameraEye;
    Point cameraTarget;
    Point cameraUp;
    Cube viewBox;
    Cube axisBox;

    
    public DebugGLChart3d(Chart watchedChart, ChartFactory debugChartFactory){
        this.watchedChart = watchedChart;
        this.debugChart = debugChartFactory.newChart(Quality.Advanced);
        this.debugChart.getView().setSquared(false);
        
        spaceTransform = watchedChart.getView().getSpaceTransformer();
        
        //watchViewBounds();
        watchAxis();
        watchCamera();

        
        
        ((AWTView)debugChart.getView()).addRenderer2d(new AWTRenderer2d(){
            @Override
            public void paint(Graphics g, int canvasWidth, int canvasHeight) {
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(java.awt.Color.BLACK);
                
                if(watchedChart!=null && watchedChart.getView()!=null && watchedChart.getView().getCamera()!=null){
                    g2d.drawString("watched.near=" + watchedChart.getView().getCamera().getNear(), 5, 20);
                    g2d.drawString("watched.far=" + watchedChart.getView().getCamera().getFar(), 5, 40);
                    g2d.drawString("watched.radius=" + watchedChart.getView().getCamera().getRenderingSphereRadius(), 5, 60);
                    g2d.drawString("watched.up.z=" + watchedChart.getView().getCamera().getUp().z, 5, 80);
                    g2d.drawString("watched.axe=" + watchedChart.getView().getAxis().getBoxBounds().toString(), 5, 100);
                    g2d.drawString("transformed axe=" + axisBox.getBounds().toString(), 5, 120);
                }
            }
        });

    }

    public void watchViewBounds() {
        BoundingBox3d viewBounds = watchedChart.getView().getBounds();
        if(spaceTransform!=null){
            viewBounds = spaceTransform.compute(viewBounds);
        }
        viewBox = new Cube(viewBounds, Color.CYAN, Color.BLUE.alphaSelf(0.5f));
        viewBox.setWireframeWidth(2);
        
        debugChart.add(viewBox);
    }

    public void watchAxis() {
        BoundingBox3d axisBounds = watchedChart.getView().getAxis().getBoxBounds();
        if(spaceTransform!=null){
            axisBounds = spaceTransform.compute(axisBounds);
        }
        System.out.println(axisBounds);
        axisBox = new Cube(axisBounds, Color.YELLOW, Color.BLUE.alphaSelf(0.5f));
        axisBox.setWireframeWidth(3);
        
        debugChart.add(axisBox);
    }

    public void watchCamera() {
        cameraEye = new Point(watchedChart.getView().getCamera().getEye(), Color.RED);
        cameraEye.setWidth(10);
        
        cameraTarget = new Point(watchedChart.getView().getCamera().getTarget(), Color.GREEN);
        cameraTarget.setWidth(10);

        cameraUp = new Point(watchedChart.getView().getCamera().getUp(), Color.MAGENTA);
        cameraUp.setWidth(10);

        debugChart.add(cameraEye);
        debugChart.add(cameraTarget);
        debugChart.add(cameraUp);
    }
    
    public void open(Rectangle rectangle){
        debugChart.open("GL Debug", rectangle);
        debugChart.addMouseCameraController();
        
        startUpdater();
    }
    
    protected void startUpdater() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (watchedChart!=null && watchedChart.getView()!=null) {
                    watchCameraUpdate();

                    if(debugChart.getView()!=null){
                        debugChart.getView().updateBounds();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }


        }).start();
    }

    public void watchCameraUpdate() {
        Coord3d eye = watchedChart.getView().getCamera().getEye();
        Coord3d target = watchedChart.getView().getCamera().getTarget();
        Coord3d up = watchedChart.getView().getCamera().getUp();
        
        
        cameraEye.setData(eye);
        cameraTarget.setData(target);
        cameraUp.setData(up.add(eye));
    }
}
