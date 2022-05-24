package org.jzy3d.maths;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;

public class TestAngle3d {
  @Test
  public void testAngle90and45_Vertical() {

    // Given on horizontal square
    Polygon p1 = new Polygon();
    p1.add(new Point(new Coord3d(0, 0, 0), Color.BLUE)); // P1 left bottom
    p1.add(new Point(new Coord3d(1, 0, 0), Color.BLUE)); // neighbor edge will be dropped
    p1.add(new Point(new Coord3d(1, 1, 0), Color.BLUE)); // neighbor edge will be dropped
    p1.add(new Point(new Coord3d(0, 1, 0), Color.BLUE)); // P1 left top


    // When computing angle

    Angle3d a1 = new Angle3d(p1.get(0).xyz, p1.get(1).xyz, p1.get(2).xyz);


    Angle3d a2 = new Angle3d(p1.get(0).xyz, p1.get(1).xyz, p1.get(3).xyz);

    // Then
    Assert.assertEquals(Angle3d.DEGREE_90, a1.angle(), 0.0001);
    Assert.assertEquals(Angle3d.DEGREE_45, a2.angle(), 0.0001);

  }
  
  @Test
  public void testAngle90and45_Horizontal() {

    // Given on horizontal square
    Polygon p1 = new Polygon();
    p1.add(new Point(new Coord3d(0, 0, 0), Color.BLUE)); // P1 left bottom
    p1.add(new Point(new Coord3d(0, 0, 1), Color.BLUE)); // neighbor edge will be dropped
    p1.add(new Point(new Coord3d(0, 1, 1), Color.BLUE)); // neighbor edge will be dropped
    p1.add(new Point(new Coord3d(0, 1, 0), Color.BLUE)); // P1 left top


    // When computing angle

    Angle3d a1 = new Angle3d(p1.get(0).xyz, p1.get(1).xyz, p1.get(2).xyz);


    Angle3d a2 = new Angle3d(p1.get(0).xyz, p1.get(1).xyz, p1.get(3).xyz);

    // Then
    Assert.assertEquals(Angle3d.DEGREE_90, a1.angle(), 0.0001);
    Assert.assertEquals(Angle3d.DEGREE_45, a2.angle(), 0.0001);

  }
  
  @Test
  public void whenSumingAnglesOfPolygon_TriangleIs180_SquareIs360() {
    // square
    Coord3d c1 = new Coord3d(0,0,0); // bottom left
    Coord3d c2 = new Coord3d(1,0,0); // bottom right
    Coord3d c3 = new Coord3d(1,1,0); // top right
    Coord3d c4 = new Coord3d(0,1,0);

    Assert.assertEquals(2*Math.PI, Angle3d.angleSum(Coord3d.list(c1,c2,c3,c4)), 0.0001);
    Assert.assertEquals(Math.PI, Angle3d.angleSum(Coord3d.list(c1,c2,c3)), 0.0001);
  }
  
  
  @Test(expected=IllegalArgumentException.class)
  public void sumOfIncompleteAngles() {
    Angle3d.angleSum(Coord3d.list(Coord3d.IDENTITY));
  }

  @Test(expected=IllegalArgumentException.class)
  public void sumOfIncompleteAngles2() {
    Angle3d.angleSum(Coord3d.list(Coord3d.IDENTITY, Coord3d.IDENTITY));
  }
  
  @Test
  public void sumOfAnglesExpect() {
    Assert.assertEquals(180, Angle3d.angleSumOfNonIntersectingPolygon(3), 0.0001);
    Assert.assertEquals(360, Angle3d.angleSumOfNonIntersectingPolygon(4), 0.0001);
  }

  /*@Test
  public void testAngle90and45_nonPlanar() {

    // Given
    Polygon p1 = new Polygon();
    p1.add(new Point(new Coord3d(0, 0, 0), Color.BLUE)); // P1 left bottom
    p1.add(new Point(new Coord3d(1, 0, 1), Color.BLUE)); // neighbor edge will be dropped
    p1.add(new Point(new Coord3d(1, 1, 0), Color.BLUE)); // neighbor edge will be dropped
    p1.add(new Point(new Coord3d(0, 1, 0), Color.BLUE)); // P1 left top


    // When computing angle

    Angle3d a1 = new Angle3d(p1.get(0).xyz, p1.get(1).xyz, p1.get(2).xyz);


    Angle3d a2 = new Angle3d(p1.get(0).xyz, p1.get(1).xyz, p1.get(3).xyz);

    // Then
    Assert.assertEquals(Angle3d.DEGREE_90, a1.angle(), 0.0001);
    Assert.assertEquals(Angle3d.DEGREE_45, a2.angle(), 0.0001);

  }*/
}
