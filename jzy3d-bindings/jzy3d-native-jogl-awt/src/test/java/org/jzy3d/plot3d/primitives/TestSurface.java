package org.jzy3d.plot3d.primitives;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.mocks.jogl.GLMock;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;

public class TestSurface {
  @Test
  public void testPointNoTransform() {
    // Given
    Mapper f3d = makeTestFunction3d();
    float rangeMax = 2;
    Shape surface = makeTestSurface(f3d, 2);

    // When
    GLMock glMock = new GLMock();

    NativeDesktopPainter painter = new NativeDesktopPainter();
    painter.setGL(glMock);

    surface.draw(painter);

    // Then
    Coord3d expect = new Coord3d(rangeMax, rangeMax, (float) f3d.f(rangeMax, rangeMax));
    Assert.assertTrue(glMock.vertex3f_contains(expect));
  }

  public static Shape makeTestSurface(Mapper mapper, float rangeMax) {
    Range range = new Range(0, rangeMax);
    int steps = 80;

    final Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(false);

    return surface;
  }

  public static Mapper makeTestFunction3d() {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return Math.pow(10, x + y);
      }
    };
    return mapper;
  }
}
