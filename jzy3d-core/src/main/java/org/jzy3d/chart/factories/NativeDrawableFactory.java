package org.jzy3d.chart.factories;

import org.jzy3d.chart.factories.AbstractDrawableFactory;
import org.jzy3d.chart.factories.IDrawableFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.vbo.drawable.SphereVBO;

public class NativeDrawableFactory extends AbstractDrawableFactory implements IDrawableFactory {
  public Point newPointRound(Coord3d coord, Color color, float width) {
    Point p = new Point(coord, color) {
      @Override
      public void draw(IPainter painter) {
        painter.glEnable(GLConstants.GL_POINT_SMOOTH);
        super.draw(painter);
      }
    };
    p.setWidth(width);
    return p;
  }

  public Point newPointSquare(Coord3d coord, Color color, float width) {
    Point p = new Point(coord, color) {
      @Override
      public void draw(IPainter painter) {
        painter.glDisable(GLConstants.GL_POINT_SMOOTH);
        super.draw(painter);
      }
    };
    p.setWidth(width);
    return p;
  }

  public SphereVBO newSphereVBO(Coord3d coord, Color color, Color wireframe, float radius,
      int stacks, int slices) {
    SphereVBO s = new SphereVBO(coord, radius, stacks, slices, color);
    
    return s;
  }
}
