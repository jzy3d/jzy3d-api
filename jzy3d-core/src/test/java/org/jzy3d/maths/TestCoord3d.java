package org.jzy3d.maths;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests cross product and AxisAngle rotation
 *
 * @author Florian Enner < florian @ enner.org >
 * @since 01.01.14
 */
public class TestCoord3d {
  float delta = 1E-6f;

  @Test
  public void testCross() throws Exception {
    Coord3d x = new Coord3d(1, 0, 0);
    Coord3d y = new Coord3d(0, 1, 0);
    Coord3d z = new Coord3d(0, 0, 1);
    assertEqualCoord(z, x.cross(y));
  }

  @Test
  public void rotateX() throws Exception {
    Coord3d input = new Coord3d(0, 1, 0);
    Coord3d axis = new Coord3d(1, 0, 0);
    float angle = 90f;
    Coord3d expected = new Coord3d(0, 0, 1);
    assertEqualCoord(expected, input.rotate(angle, axis));
  }

  @Test
  public void rotateY() throws Exception {
    Coord3d input = new Coord3d(1, 0, 0);
    Coord3d axis = new Coord3d(0, 1, 0);
    float angle = 90f;
    Coord3d expected = new Coord3d(0, 0, -1);
    assertEqualCoord(expected, input.rotate(angle, axis));
  }

  @Test
  public void rotateZ() throws Exception {
    Coord3d input = new Coord3d(0, 1, 0);
    Coord3d axis = new Coord3d(0, 0, 1);
    float angle = 90f;
    Coord3d expected = new Coord3d(-1, 0, 0);
    assertEqualCoord(expected, input.rotate(angle, axis));
  }

  void assertEqualCoord(Coord3d expected, Coord3d actual) {
    Assert.assertEquals(expected.x, actual.x, delta);
    Assert.assertEquals(expected.y, actual.y, delta);
    Assert.assertEquals(expected.z, actual.z, delta);
    // Assert.assertEquals(expected, actual);
  }

  @Test
  public void minMax() {
    Coord3d c1 = new Coord3d(1f, 2f, 3f);
    Coord3d c2 = new Coord3d(2f, 3f, 1f);
    Coord3d c3 = new Coord3d(3f, 1f, 2f);
    List<Coord3d> cs = Coord3d.list(c1, c2, c3);

    Coord3d min = Coord3d.min(cs);
    Coord3d max = Coord3d.max(cs);
    Pair<Coord3d, Coord3d> minmax = Coord3d.minMax(cs);

    Coord3d expectedMin = new Coord3d(1f, 1f, 1f);
    Coord3d expectedMax = new Coord3d(3f, 3f, 3f);
    Pair<Coord3d, Coord3d> expectedMinmax = new Pair<Coord3d, Coord3d>(min, max);

    Assert.assertEquals(expectedMin, min);
    Assert.assertEquals(expectedMax, max);
    Assert.assertEquals(expectedMinmax, minmax);

  }

  @Test
  public void equals() {
    Assert.assertTrue(new Coord3d(1, 2, 3).equals(new Coord3d(1, 2, 3)));
    
    Assert.assertFalse(new Coord3d(1, 2, 3).equals(new Coord3d(2, 1, 3)));

    Assert.assertTrue(new Coord3d(-0, 2, 3).equals(new Coord3d(0, 2, 3)));
    
    Assert.assertTrue(new Coord3d(Float.NaN, 2, 3).equals(new Coord3d(Float.NaN, 2, 3)));
    
    Assert.assertTrue(new Coord3d(Float.POSITIVE_INFINITY, 2, 3)
        .equals(new Coord3d(Float.POSITIVE_INFINITY, 2, 3)));
    
    Assert.assertTrue(new Coord3d(Float.NEGATIVE_INFINITY, 2, 3)
        .equals(new Coord3d(Float.NEGATIVE_INFINITY, 2, 3)));

    Assert.assertFalse(new Coord3d(Float.POSITIVE_INFINITY, 2, 3)
        .equals(new Coord3d(Float.NEGATIVE_INFINITY, 2, 3)));
  }
  
  
}
