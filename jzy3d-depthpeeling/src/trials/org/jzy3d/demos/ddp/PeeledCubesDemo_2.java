package org.jzy3d.demos.ddp;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.factories.NativePainterFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.factories.DepthPeelingChartFactory;
import org.jzy3d.factories.DepthPeelingPainterFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite.PolygonType;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ddp.DepthPeelingChart;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;


public class PeeledCubesDemo_2 {
  public static void main(String[] args) {

    GLProfile profile = GLProfile.get(GLProfile.GL4);
    GLCapabilities caps = NativePainterFactory.getOffscreenCapabilities(profile);
    DepthPeelingPainterFactory p = new DepthPeelingPainterFactory(caps);
    DepthPeelingChartFactory f = new DepthPeelingChartFactory(PeelingMethod.DUAL_PEELING_MODE);
    Chart chart = f.newChart();
    chart.getView().setAxisDisplayed(false);
    // chart.setAnimated(false);

    createStack(chart, 0.01f, 0.01f, Coord3d.ORIGIN, Color.BLUE /* no alpha */, Color.BLACK);
    createStack(chart, 0.01f, 0.01f, new Coord3d(0.005f, 0.005f, 0.005f),
        new Color(1f, 0f, 0f, 0.5f), Color.BLACK);
    createStack(chart, 0.01f, 0.01f, new Coord3d(0.01f, 0.01f, 0.01f), new Color(0f, 1f, 0f, 0.5f),
        Color.BLACK);
    
    
    ChartLauncher.openChart(chart, new Rectangle(0, 0, 600, 600));
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
