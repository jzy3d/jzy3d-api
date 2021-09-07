package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;

public class RandomGeom {
  Random r = new Random();
  boolean alpha = false;

  public RandomGeom() {}

  public RandomGeom(long seed) {
    r.setSeed(seed);
  }

  public boolean isAlpha() {
    return alpha;
  }

  public void setAlpha(boolean alpha) {
    this.alpha = alpha;
  }


  // GENERATOR


  public Coord3d coord() {
    return new Coord3d(aFloat(), aFloat(), aFloat());
  }

  public Coord3d coord(float xrange, float yrange, float zrange) {
    return new Coord3d(aFloat(xrange), aFloat(yrange), aFloat(zrange));
  }

  public Color color() {
    return new Color(aFloat(), aFloat(), aFloat(), alpha ? aFloat() : 1f);
  }

  public Color color(Coord3d coord, float xrange, float yrange, float zrange) {
    return new Color(aFloat() * coord.x / xrange, aFloat() * coord.y / xrange,
        aFloat() * coord.x / zrange, alpha ? aFloat() : 1f);
  }

  /**
   * Build a color based on the input coord values.
   */
  public Color color(Coord3d coord) {
    return new Color(coord.x, coord.y, coord.z, aFloat());
  }

  /** Generate a float between 0 and 1 */
  public float aFloat() {
    return r.nextFloat();
  }

  /** Generate a float between 0 and range */
  public float aFloat(float range) {
    return aFloat() * range;
  }

  public List<Drawable> spinningCubes(int cubes, float spin) {
    return spinningCubes(cubes, spin, 0);
  }

  /**
   * Generate a stack of cubes, where the top face is spinned w.r.t. the bottom face. The left,
   * right, near and far faces are consequently not planar which is usefull to validate some
   * algorithms.
   * 
   * Each cube is a {@link Composite} which in returned in a list of {@link Drawable}s.
   * 
   * @param cubes number of cubes in the stack
   * @param spin the rotation angle in degree between the bottom and top face
   * @param randomize is the factor applied to a random number generator before adding a "shaking"
   *        offset to each coordinate
   */
  public List<Drawable> spinningCubes(int cubes, float spin, float randomize) {
    Color wf = Color.BLACK;
    Color fc = Color.BLUE;


    Coord3d axis = new Coord3d(0, 0, 1);
    Coord3d start = new Coord3d(0, 0, 0);
    int i = 1;

    // Coord3d pt1 = new Coord3d()

    List<Drawable> drawables = new ArrayList<>();

    Coord3d pt1_1 = new Coord3d(start.x + 0, start.y + 0, start.z);
    Coord3d pt2_1 = new Coord3d(start.x + i, start.y + 0, start.z);
    Coord3d pt3_1 = new Coord3d(start.x + i, start.y + i, start.z);
    Coord3d pt4_1 = new Coord3d(start.x + 0, start.y + i, start.z);

    if (randomize != 0) {
      pt1_1.addSelf(r.nextFloat() * randomize, r.nextFloat() * randomize,
          r.nextFloat() * randomize);
      pt2_1.addSelf(r.nextFloat() * randomize, r.nextFloat() * randomize,
          r.nextFloat() * randomize);
      pt3_1.addSelf(r.nextFloat() * randomize, r.nextFloat() * randomize,
          r.nextFloat() * randomize);
      pt4_1.addSelf(r.nextFloat() * randomize, r.nextFloat() * randomize,
          r.nextFloat() * randomize);
    }


    for (int j = 0; j < cubes; j++) {
      Coord3d pt1_2 = pt1_1.rotate(spin, axis).add(0, 0, i);
      Coord3d pt2_2 = pt2_1.rotate(spin, axis).add(0, 0, i);
      Coord3d pt3_2 = pt3_1.rotate(spin, axis).add(0, 0, i);
      Coord3d pt4_2 = pt4_1.rotate(spin, axis).add(0, 0, i);

      if (randomize != 0) {
        pt1_2.addSelf(r.nextFloat() * randomize, r.nextFloat() * randomize,
            r.nextFloat() * randomize);
        pt2_2.addSelf(r.nextFloat() * randomize, r.nextFloat() * randomize,
            r.nextFloat() * randomize);
        pt3_2.addSelf(r.nextFloat() * randomize, r.nextFloat() * randomize,
            r.nextFloat() * randomize);
        pt4_2.addSelf(r.nextFloat() * randomize, r.nextFloat() * randomize,
            r.nextFloat() * randomize);
      }


      Composite cube = new Composite();

      cube.add(new Polygon(wf, fc, pt1_1, pt2_1, pt3_1, pt4_1)); // bottom
      cube.add(new Polygon(wf, fc, pt1_2, pt2_2, pt3_2, pt4_2)); // top
      cube.add(new Polygon(wf, fc, pt1_1, pt2_1, pt2_2, pt1_2)); // left
      cube.add(new Polygon(wf, fc, pt3_1, pt4_1, pt4_2, pt3_2)); // right
      cube.add(new Polygon(wf, fc, pt2_1, pt3_1, pt3_2, pt2_2)); // far
      cube.add(new Polygon(wf, fc, pt1_1, pt4_1, pt4_2, pt1_2)); // near

      drawables.add(cube);

      pt1_1 = pt1_2;
      pt2_1 = pt2_2;
      pt3_1 = pt3_2;
      pt4_1 = pt4_2;
    }

    return drawables;
  }
}
