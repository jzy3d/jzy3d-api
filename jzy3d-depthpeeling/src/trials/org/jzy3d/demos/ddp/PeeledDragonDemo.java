package org.jzy3d.demos.ddp;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.colors.Color;
import org.jzy3d.io.obj.OBJFileLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite.PolygonType;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ddp.DepthPeelingChart;


public class PeeledDragonDemo {
    public static void main(String[] args) {
        System.err.println("May require vm argument -Xmx1024m");

        Chart chart = DepthPeelingChart.get(Quality.Fastest, "awt");

        OBJFileLoader loader = new OBJFileLoader("models/dragon.obj");
        chart.getScene().add(new DrawableVBO(loader));
        //chart.getScene().add(new Jzy3dDrawableOBJFile("models/bun_zipper.ply"));
        
        
        //createStack(chart, 0.01f, 0.01f, Coord3d.ORIGIN, Color.BLUE /*no alpha*/, Color.BLACK);
        
        ChartLauncher.openChart(chart, new Rectangle(0,0,600,600));
    }
    
    public static void createStack(Chart chart, float width, float height, Coord3d position, Color face, Color wireframe) {
        BoundingBox3d bounds = new BoundingBox3d(position.x-width/2, position.x+width/2, position.y-width/2, position.y+width/2, position.z-height/2, position.z+height/2);
        ParallelepipedComposite p1 = new ParallelepipedComposite(bounds, PolygonType.SIMPLE);
        p1.setPolygonMode(PolygonMode.FRONT_AND_BACK);
        p1.setPolygonOffsetFill(true);
        p1.setColor(face);
        p1.setWireframeColor(wireframe);
        p1.setWireframeDisplayed(true);
        chart.getScene().add(p1);
    }    
}
