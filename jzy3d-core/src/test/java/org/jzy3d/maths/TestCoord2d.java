package org.jzy3d.maths;

import org.junit.Assert;
import org.junit.Test;

public class TestCoord2d {
  @Test
  public void equals() {
    Assert.assertTrue(new Coord2d(1, 2).equals(new Coord2d(1, 2)));
    Assert.assertFalse(new Coord2d(1, 2).equals(new Coord2d(2, 1)));

    Assert.assertTrue(new Coord2d(-0, 2).equals(new Coord2d(0, 2)));
    Assert.assertTrue(new Coord2d(Float.NaN, 2).equals(new Coord2d(Float.NaN, 2)));
    Assert.assertTrue(
        new Coord2d(Float.POSITIVE_INFINITY, 2).equals(new Coord2d(Float.POSITIVE_INFINITY, 2)));
    Assert.assertTrue(
        new Coord2d(Float.NEGATIVE_INFINITY, 2).equals(new Coord2d(Float.NEGATIVE_INFINITY, 2)));

    Assert.assertFalse(
        new Coord2d(Float.POSITIVE_INFINITY, 2).equals(new Coord2d(Float.NEGATIVE_INFINITY, 2)));
  }
}
