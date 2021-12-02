package org.jzy3d.demos.ddp;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.NativePainterFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.factories.DepthPeelingChartFactory;
import org.jzy3d.factories.DepthPeelingPainterFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite.PolygonType;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLProfile;

/**
 * WEIGHTED_SUM_MODE
 * no compilation problem BUT overlapping translucent part are black
 * 
 * 
 * WEIGHTED_AVERAGE_MODE
 * extension 'ARB_draw_buffers' is not supported - despite available
 * '-' does not operate on 'int' and 'float'
 * 
 * DUAL_PEELING_MODE
 * extension 'ARB_draw_buffers' is not supported - despite available
 * '==' does not operate on 'float' and 'int'
 * 
 * 
 * F2B_PEELING_MODE
 * FIXED : '+' does not operate on 'vec4' and 'vec3' 
 * BUT : not displaying / freezing window
 * 
 * 
 * @author martin
 *
 */
public class PeeledCubesDemo {
  public static void main(String[] args) throws InterruptedException {

    GLProfile profile = GLProfile.get(GLProfile.GL2); //GL4bcImpl fail to downcast to GL2 on Mac
    GLCapabilities caps = NativePainterFactory.getDefaultCapabilities(profile);
    DepthPeelingPainterFactory p = new DepthPeelingPainterFactory(caps);
    DepthPeelingChartFactory f = new DepthPeelingChartFactory(p, PeelingMethod.WEIGHTED_SUM_MODE);
    Chart chart = f.newChart();

    
    chart.getView().setAxisDisplayed(false);
    // chart.setAnimated(false);

    cube(chart, 0.01f, 0.01f, Coord3d.ORIGIN, Color.BLUE /* no alpha */, Color.BLACK);
    cube(chart, 0.01f, 0.01f, new Coord3d(0.005f, 0.005f, 0.005f),
        new Color(1f, 0f, 0f, 0.5f), Color.BLACK);
    cube(chart, 0.01f, 0.01f, new Coord3d(0.01f, 0.01f, 0.01f), new Color(0f, 1f, 0f, 0.5f),
        Color.BLACK);
    

    
    chart.open(800,600);

    // Wait a bit and print currently selected versions
    Thread.sleep(100);
    
    GLContext context = ((CanvasAWT)chart.getCanvas()).getContext();
    
    System.out.println("GLSL Version : " + context.getGLSLVersionString());
    System.out.println("GL   Version : " + context.getGLVersion());

    chart.getMouse();
    
    String info = chart.getCanvas().getDebugInfo();
    
    if(!info.contains("ARB_texture_rectangle")) {
      System.err.println("ARB_texture_rectangle is MISSING");
      System.err.println(info);      
    }
    else {
      System.out.println("ARB_texture_rectangle is here!!!");
    }
    
    if(!info.contains("ARB_draw_buffers")) {
      System.err.println("ARB_draw_buffers is MISSING");
      System.err.println(info);      
    }
    else {
      System.out.println("ARB_draw_buffers is here!!!");
    }

  }

  public static void cube(Chart chart, float width, float height, Coord3d position,
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
