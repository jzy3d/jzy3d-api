package org.jzy3d.chart.factories;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.CubeComposite;
import org.jzy3d.plot3d.primitives.CubeGLUT;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Sphere;


public interface IDrawableFactory {

  public Point newPointRound(Coord3d coord, Color color, float width);

  public Point newPointSquare(Coord3d coord, Color color, float width);

  public Sphere newSphere(Coord3d coord, Color color, Color wireframe, float radius, int slicing);

  public CubeGLUT newCube(Coord3d coord, Color color, Color wireframe, float radius);

  public CubeComposite newCubeComposite(Coord3d coord, Color color, Color wireframe, float radius);

}
