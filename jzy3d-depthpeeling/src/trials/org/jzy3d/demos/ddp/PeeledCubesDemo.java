package org.jzy3d.demos.ddp;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite.PolygonType;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ddp.DepthPeelingChart;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;


/**
 * 
 * 
 * 
 * -------------------------
 * no pb avec simple polygon:
 * <li>gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
 * <li>gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
 * 
 * Probleme avec polygon normal > probleme enleve si p1.setWireframeDisplayed(false); > l'ordre dans
 * le scene graph n'a pas l'air d'avoir d'impact
 * 
 * Probleme avec simple polygon qd create stack avant ou apres
 * 
 * 
 * Dans la vue decomposee renderer.display, le rendering devient tres lent si
 * view.renderOverlay(gl); n'est pas appele
 */
public class PeeledCubesDemo {
  public static void main(String[] args) {
    Chart chart = DepthPeelingChart.get(Quality.Fastest(), "awt", PeelingMethod.WEIGHTED_SUM_MODE);
    chart.getView().setAxisDisplayed(false);
chart.setAnimated(false);

    

    createStack(chart, 0.01f, 0.01f, Coord3d.ORIGIN, Color.BLUE /* no alpha */, Color.BLACK);
    createStack(chart, 0.01f, 0.01f, new Coord3d(0.005f, 0.005f, 0.005f),
        new Color(1f, 0f, 0f, 0.5f), Color.BLACK);
    createStack(chart, 0.01f, 0.01f, new Coord3d(0.01f, 0.01f, 0.01f), new Color(0f, 1f, 0f, 0.5f),
        Color.BLACK);

    
    chart.open(800, 600);
    chart.getMouse();
    
    String info = chart.getCanvas().getDebugInfo();
    
    if(!info.contains("ARB_texture_rectangle")) {
      System.out.println(info);      
    }
    else {
      System.out.println("ARB_texture_rectangle is here!!!");
    }
    
    if(!info.contains("ARB_draw_buffers")) {
      System.out.println(info);      
    }
    else {
      System.out.println("ARB_draw_buffers is here!!!");

    }
  }

  public static void createStack(Chart chart, float width, float height, Coord3d position,
      Color face, Color wireframe) {
    BoundingBox3d bounds =
        new BoundingBox3d(position.x - width / 2, position.x + width / 2, position.y - width / 2,
            position.y + width / 2, position.z - height / 2, position.z + height / 2);
    ParallelepipedComposite p1 = new ParallelepipedComposite(bounds, PolygonType.SIMPLE);
    p1.setPolygonMode(PolygonMode.FRONT_AND_BACK);
    p1.setPolygonOffsetFill(true);
    p1.setColor(face);
    p1.setWireframeColor(wireframe);
    p1.setWireframeDisplayed(true);
    chart.getScene().add(p1);
  }
}
