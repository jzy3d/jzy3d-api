package org.jzy3d.demos.ddp;

import java.io.IOException;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.factories.DepthPeelingPainterFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite.PolygonType;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.awt.GLCanvas;

/**
 * Status of peeling methods on {macOS 10.12 + NVidia GPU} {Ubuntu 20.04 + Intel Iris GPU}
 * <ul>
 * <li>OK - F2B_PEELING_MODE
 * <li>OK - DUAL_PEELING_MODE
 * <li>OK - WEIGHTED_AVERAGE_MODE
 * <li>KO - WEIGHTED_SUM_MODE : no compilation problem BUT overlapping parts (translucent or opaque)
 * are black
 * </ul>
 * 
 * Status of peeling methods on macOS 11.4 + M1 (Silicon)
 * <ul>
 * <li>OK - DUAL_PEELING_MODE
 * <li>KO - WEIGHTED_AVERAGE_MODE : renders correctly BUT make opaque object appear translucent
 * (e.g. the blue cube of this demo)
 * <li>KO - WEIGHTED_SUM_MODE : no compilation problem BUT overlapping parts (translucent or opaque)
 * are black
 * <li>KO - F2B_PEELING_MODE : Hangs before display (reproduce with chart.get
 * </ul>
 *
 * @author martin
 *
 */
public class PeeledCubesDemo {
  public static void main(String[] args) throws InterruptedException, IOException {
    DepthPeelingPainterFactory p = new DepthPeelingPainterFactory();
    p.setPeelingMethod(PeelingMethod.DUAL_PEELING_MODE);

    AWTChartFactory f = new AWTChartFactory(p);
    Chart chart = f.newChart();

    Coord3d p1 = Coord3d.ORIGIN;
    Coord3d p2 = new Coord3d(0.005f, 0.005f, 0.005f);
    Coord3d p3 = new Coord3d(0.01f, 0.01f, 0.01f);

    cube(chart, 0.01f, p1, Color.BLUE /* no alpha */, Color.BLACK);
    cube(chart, 0.01f, p2, Color.RED.alpha(.5f), Color.BLACK);
    cube(chart, 0.01f, p3, Color.GREEN.alpha(.5f), Color.BLACK);

    chart.color(new Color(43/255f, 43/255f, 43/255f), new Color(169/255f, 183/255f, 198/255f));

    chart.open(800, 600);
    chart.getMouse();


    verifyVersions(chart);
    
  }

  public static void cube(Chart chart, float width, Coord3d position, Color face, Color wireframe) {
    BoundingBox3d bounds =
        new BoundingBox3d(position.x - width / 2, position.x + width / 2, position.y - width / 2,
            position.y + width / 2, position.z - width / 2, position.z + width / 2);
    ParallelepipedComposite p1 = new ParallelepipedComposite(bounds, PolygonType.SIMPLE);
    p1.setPolygonMode(PolygonMode.FRONT_AND_BACK);
    p1.setPolygonOffsetFill(true);
    p1.setColor(face);
    p1.setWireframeColor(wireframe);
    p1.setWireframeDisplayed(true);
    chart.getScene().add(p1);
  }


  private static void verifyVersions(Chart chart) throws InterruptedException {
    // Wait a bit and print currently selected versions
    Thread.sleep(100);

    GLContext context = ((CanvasAWT) chart.getCanvas()).getContext();

    System.out.println("GLSL Version : " + context.getGLSLVersionString().replace("\n", ""));
    System.out.println("GL   Version : " + context.getGLVersion());


    String info = ((GLCanvas) chart.getCanvas()).getContext().getGLExtensionsString();


    if (!info.contains("ARB_texture_rectangle")) {
      System.err.println("ARB_texture_rectangle is MISSING");
      System.err.println(info);
    } else {
      System.out.println("ARB_texture_rectangle is available");
    }

    if (!info.contains("ARB_draw_buffers")) {
      System.err.println("ARB_draw_buffers is MISSING");
      System.err.println(info);
    } else {
      System.out.println("ARB_draw_buffers is available");
    }
  }

}
