package org.jzy3d.plot3d.primitives.axis;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.emulgl.opengl.TestDepthRange;
import org.jzy3d.mocks.jgl.GLMock_DepthRange;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * This test "manually" mocks the GL.glDepthRange() method since Mockito.spy() on objects that are
 * called frequently (e.g. Painter) are awfully slow (most probably because ALL method calls are
 * intercepted, including any call to glVertex, glColor etc).
 * 
 * This replace {@link TestDepthRange} which is kept @Ignore for history.
 * 
 * @author Martin Pernollet
 *
 */
public class TestAxisBox {
  @Test
  public void whenRenderAxis_DepthRangeModifiedByAxis() {
    GLMock_DepthRange glMock = new GLMock_DepthRange();

    // Given a surface chart with a mock GL injected for spying calls to glDepthRange
    EmulGLChartFactory factory = EmulGLChartFactory.forGL(glMock);
    Chart chart = factory.newChart(Quality.Advanced());
    Shape surface = SampleGeom.surface();
    chart.add(surface);
    glMock.clear_glDepthRange();

    // ----------------------------------------------
    // When : render axis
    chart.getView().getAxis().draw(chart.getPainter());

    // ----------------------------------------------
    // Then axis will update depth range

    // Called that way to push the depth range farther
    double[] configForAxis = {AxisBox.NO_OVERLAP_DEPTH_RATIO, 1};
    double[] configForNothing = {0, 1};

    // Array.print(glMock.verify_glDepthRange().get(0));
    // Array.print(glMock.verify_glDepthRange().get(1));
    // Array.print(glMock.verify_glDepthRange().get(2));
    // Array.print("configForFace:", configForFace);

    Assert.assertTrue(Arrays.equals(glMock.verify_glDepthRange().get(0), configForAxis));

    // Called that way for reset
    Assert.assertTrue(Arrays.equals(glMock.verify_glDepthRange().get(1), configForNothing));

  }
}
