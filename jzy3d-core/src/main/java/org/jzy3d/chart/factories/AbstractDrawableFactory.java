package org.jzy3d.chart.factories;



import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.CubeComposite;
import org.jzy3d.plot3d.primitives.CubeGLUT;
import org.jzy3d.plot3d.primitives.Sphere;

public abstract class AbstractDrawableFactory implements IDrawableFactory{

  public static class Settings{
    public int sphereStacks = 16;
    public int sphereSlices = 32;
  }

  /*public AbstractDrawableFactory() {
    super();
  }*/

  public Sphere newSphere(Coord3d coord, Color color, Color wireframe, float radius, int slicing) {
    Sphere s = new Sphere(coord, radius, slicing, color);
    
    if (wireframe != null) {
      s.setWireframeColor(wireframe);
      s.setWireframeDisplayed(true);
    }
    else {
      s.setWireframeDisplayed(false);
    }
    
    s.setWireframeWidth(1f);
    return s;
  }

  public CubeGLUT newCube(Coord3d coord, Color color, Color wireframe, float radius) {
    CubeGLUT cube = new CubeGLUT(coord, radius);
    cube.setColor(color);
  
    if (wireframe != null) {
      cube.setWireframeColor(wireframe);
      cube.setWireframeDisplayed(true);
    }
    else {
      cube.setWireframeDisplayed(false);
    }
    
    cube.setWireframeWidth(1f);
    return cube;
  }

  public CubeComposite newCubeComposite(Coord3d coord, Color color, Color wireframe, float radius) {
    CubeComposite cube = new CubeComposite(coord, radius);
    cube.setColor(color);
  
    if (wireframe != null) {
      cube.setWireframeColor(wireframe);
      cube.setWireframeDisplayed(true);
    }
    else {
      cube.setWireframeDisplayed(false);
    }
  
    cube.setWireframeWidth(1f);
    return cube;
  }

}
