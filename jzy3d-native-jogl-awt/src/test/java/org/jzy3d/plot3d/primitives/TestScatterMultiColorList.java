package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.mocks.jogl.GLMock;
import org.jzy3d.painters.NativeDesktopPainter;

public class TestScatterMultiColorList {
  @Test
  public void testScatterNoSpaceTransform() {
    // Given
    int size = 3;
    int offset = 1000; // will expect to find log10(1000)
    List<Coord3d> coords = makeTestScatter(size, offset);
    ScatterMultiColorList scatter;

    // ---------------------------------------------
    // Given
    scatter = new ScatterMultiColorList(coords,
        new ColorMapper(new ColorMapRainbow(), 0.1, 1.1, new Color(1, 1, 1, .5f)));
    testScatter(coords, scatter);


    // ---------------------------------------------
    // Given
    scatter = new ConcurrentScatterMultiColorList(coords,
        new ColorMapper(new ColorMapRainbow(), 0.1, 1.1, new Color(1, 1, 1, .5f)));
    testScatter(coords, scatter);
  }



  public static void testScatter(List<Coord3d> expected, ScatterMultiColorList scatter) {
    // When
    GLMock glMock = new GLMock();

    NativeDesktopPainter painter = new NativeDesktopPainter();
    painter.setGL(glMock);

    scatter.draw(painter);

    // Then
    assertScatterGLCalls(expected, glMock);
  }



  public static void assertScatterGLCalls(List<Coord3d> expected, GLMock glMock) {
    Assert.assertEquals(expected.size(), glMock.getGL2Mock().getVertex3f().size());
    for (Coord3d c : expected) {
      Assert.assertTrue(glMock.vertex3f_contains(c));
    }
  }



  public static List<Coord3d> makeTestScatter(int size, int offset) {
    List<Coord3d> coords = new ArrayList<Coord3d>();
    Color[] colors = new Color[size];

    Random r = new Random();
    r.setSeed(0);
    for (int i = 0; i < size; i++) {
      coords.add(new Coord3d(i + offset, i + offset, i + offset));
      colors[i] = new Color(i + offset, i + offset, i + offset, i + offset);
    }
    return coords;
  }
}
