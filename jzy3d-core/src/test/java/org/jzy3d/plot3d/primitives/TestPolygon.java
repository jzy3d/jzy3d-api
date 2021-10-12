package org.jzy3d.plot3d.primitives;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Normal.NormalPer;
import org.jzy3d.painters.IPainter;
import org.mockito.Mockito;

public class TestPolygon {

  ///////////////////////////////////////////////////
  //
  // NORMALS MANAGEMENT WHEN SPLIT TRIANGLES
  //
  ///////////////////////////////////////////////////

  @Test
  public void whenBuildWithPointOnly_ThenNormalAreProcessedAutomatically_SPLIT_TRIANGLE() {
    // Given polygon and painter
    Polygon p = new Polygon();
    p.add(new Coord3d(0, 0, 0));
    p.add(new Coord3d(1, 0, 0));
    p.add(new Coord3d(1, 1, 0));
    p.add(new Coord3d(0, 1, 0));

    p.splitInTriangles = true; // IMPORTANT FOR THIS TEST


    IPainter painter = Mockito.spy(IPainter.class);

    // --------------------------------------------------
    // When drawing with default settings - WITHOUT LIGHT
    Assert.assertTrue(p.isNormalProcessingAutomatic());
    Assert.assertFalse(p.isReflectLight());

    p.draw(painter);

    // Then no normal processing
    verify(painter, times(0)).normal(new Coord3d());

    // --------------------------------------------------
    // When enabling light reflection before drawing
    p.setReflectLight(true);
    Assert.assertTrue(p.isReflectLight());
    Assert.assertTrue(p.splitInTriangles);
    Assert.assertTrue(p.isNormalProcessingAutomatic());

    p.draw(painter);

    // Then no normal processing
    verify(painter, times(1)).normal(new Coord3d(0, 0, 1));
    verify(painter, times(1)).normal(new Coord3d(0, -0, 1));

  }

  @Test
  public void whenBuildWithPointAndNormalPerVertex_ThenSuppliedNormalAreApplied_SPLIT_TRIANGLE() {
    // Given polygon and painter
    Polygon p = new Polygon();
    p.add(new Coord3d(0, 0, 0));
    p.add(new Coord3d(1, 0, 0));
    p.add(new Coord3d(1, 1, 0));
    p.add(new Coord3d(0, 1, 0));


    p.splitInTriangles = true; // IMPORTANT FOR THIS TEST


    List<Coord3d> customNormals = new ArrayList<>();
    customNormals.add(new Coord3d(.5, .5, .5));
    customNormals.add(new Coord3d(.6, .6, .6));
    customNormals.add(new Coord3d(.7, .7, .7));
    customNormals.add(new Coord3d(.8, .8, .8));

    p.setNormals(customNormals, NormalPer.POINT);


    IPainter painter = Mockito.spy(IPainter.class);

    // --------------------------------------------------
    // When drawing with default settings - WITHOUT LIGHT
    Assert.assertFalse(p.isNormalProcessingAutomatic());
    Assert.assertFalse(p.isReflectLight());

    p.draw(painter);

    // Then no normal processing
    verify(painter, times(0)).normal(new Coord3d());

    // --------------------------------------------------
    // When enabling light reflection before drawing
    p.setReflectLight(true);
    Assert.assertTrue(p.isReflectLight());
    Assert.assertFalse(p.isNormalProcessingAutomatic());

    p.draw(painter);

    // Then invoke normal with supplied normals
    verify(painter, times(2)).normal(new Coord3d(.5, .5, .5)); // shared by two triangles
    verify(painter, times(1)).normal(new Coord3d(.6, .6, .6));
    verify(painter, times(2)).normal(new Coord3d(.7, .7, .7)); // shared by two triangles
    verify(painter, times(1)).normal(new Coord3d(.8, .8, .8)); 

  }
  
  /*@Test
  public void whenBuildWithPointAndSingleNormalPerGeometry_ThenSuppliedNormalIsApplied_SPLIT_TRIANGLE() {
    // Given polygon and painter
    Polygon p = new Polygon();
    p.add(new Coord3d(0, 0, 0));
    p.add(new Coord3d(1, 0, 0));
    p.add(new Coord3d(1, 1, 0));
    p.add(new Coord3d(0, 1, 0));

    p.splitInTriangles = true; // IMPORTANT FOR THIS TEST

    List<Coord3d> customNormals = new ArrayList<>();
    customNormals.add(new Coord3d(.5, .5, .5));
    customNormals.add(new Coord3d(.6, .6, .6));

    p.setNormals(customNormals, NormalPer.GEOMETRY);

    IPainter painter = Mockito.spy(IPainter.class);

    // --------------------------------------------------
    // When drawing with default settings - WITHOUT LIGHT
    Assert.assertFalse(p.isNormalProcessingAutomatic());
    Assert.assertFalse(p.isReflectLight());

    p.draw(painter);

    // Then no normal processing
    verify(painter, times(0)).normal(new Coord3d());

    // --------------------------------------------------
    // When enabling light reflection before drawing
    p.setReflectLight(true);
    Assert.assertTrue(p.isReflectLight());
    Assert.assertFalse(p.isNormalProcessingAutomatic());

    p.draw(painter);

    // Then invoke normal with supplied normals
    verify(painter, times(1)).normal(new Coord3d(.5, .5, .5));
    verify(painter, times(1)).normal(new Coord3d(.6, .6, .6));
  }*/

  ///////////////////////////////////////////////////
  //
  // NORMALS MANAGEMENT WHEN NO SPLIT TRIANGLES
  //
  ///////////////////////////////////////////////////

  @Test
  public void whenBuildWithPointOnly_ThenNormalAreProcessedAutomatically_NO_SPLIT_TRIANGLE() {
    // Given polygon and painter
    Polygon p = new Polygon();
    p.add(new Coord3d(0, 0, 0));
    p.add(new Coord3d(1, 0, 0));
    p.add(new Coord3d(1, 1, 0));
    p.add(new Coord3d(0, 1, 0));

    p.splitInTriangles = true; // IMPORTANT FOR THIS TEST


    IPainter painter = Mockito.spy(IPainter.class);

    // --------------------------------------------------
    // When drawing with default settings - WITHOUT LIGHT
    Assert.assertTrue(p.isNormalProcessingAutomatic());
    Assert.assertFalse(p.isReflectLight());

    p.draw(painter);

    // Then no normal processing
    verify(painter, times(0)).normal(new Coord3d());

    // --------------------------------------------------
    // When enabling light reflection before drawing
    p.setReflectLight(true);
    Assert.assertTrue(p.isReflectLight());
    Assert.assertTrue(p.splitInTriangles);
    Assert.assertTrue(p.isNormalProcessingAutomatic());

    p.draw(painter);

    // Then no normal processing
    verify(painter, times(1)).normal(new Coord3d(0, 0, 1));
  }


  @Test
  public void whenBuildWithPointAndNormalPerVertex_ThenSuppliedNormalAreApplied_NO_SPLIT_TRIANGLE() {
    // Given polygon and painter
    Polygon p = new Polygon();
    p.add(new Coord3d(0, 0, 0));
    p.add(new Coord3d(1, 0, 0));
    p.add(new Coord3d(1, 1, 0));
    p.add(new Coord3d(0, 1, 0));


    p.splitInTriangles = false; // IMPORTANT FOR THIS TEST

    List<Coord3d> customNormals = new ArrayList<>();
    customNormals.add(new Coord3d(.5, .5, .5));
    customNormals.add(new Coord3d(.6, .6, .6));
    customNormals.add(new Coord3d(.7, .7, .7));
    customNormals.add(new Coord3d(.8, .8, .8));

    p.setNormals(customNormals, NormalPer.POINT);

    IPainter painter = Mockito.spy(IPainter.class);

    // --------------------------------------------------
    // When drawing with default settings - WITHOUT LIGHT
    Assert.assertFalse(p.isNormalProcessingAutomatic());
    Assert.assertFalse(p.isReflectLight());

    p.draw(painter);

    // Then no normal processing
    verify(painter, times(0)).normal(new Coord3d());

    // --------------------------------------------------
    // When enabling light reflection before drawing
    p.setReflectLight(true);
    Assert.assertTrue(p.isReflectLight());
    Assert.assertFalse(p.isNormalProcessingAutomatic());

    p.draw(painter);

    // Then invoke normal with supplied normals
    verify(painter, times(1)).normal(new Coord3d(.5, .5, .5));
    verify(painter, times(1)).normal(new Coord3d(.6, .6, .6));
    verify(painter, times(1)).normal(new Coord3d(.7, .7, .7));
    verify(painter, times(1)).normal(new Coord3d(.8, .8, .8));

  }
  
  @Test
  public void whenBuildWithPointAndSingleNormalPerGeometry_ThenSuppliedNormalIsApplied_NO_SPLIT_TRIANGLE() {
    // Given polygon and painter
    Polygon p = new Polygon();
    p.add(new Coord3d(0, 0, 0));
    p.add(new Coord3d(1, 0, 0));
    p.add(new Coord3d(1, 1, 0));
    p.add(new Coord3d(0, 1, 0));


    p.splitInTriangles = false; // IMPORTANT FOR THIS TEST

    p.setNormal(new Coord3d(.5, .5, .5));

    IPainter painter = Mockito.spy(IPainter.class);

    // --------------------------------------------------
    // When drawing with default settings - WITHOUT LIGHT
    Assert.assertFalse(p.isNormalProcessingAutomatic());
    Assert.assertFalse(p.isReflectLight());

    p.draw(painter);

    // Then no normal processing
    verify(painter, times(0)).normal(new Coord3d());

    // --------------------------------------------------
    // When enabling light reflection before drawing
    p.setReflectLight(true);
    Assert.assertTrue(p.isReflectLight());
    Assert.assertFalse(p.isNormalProcessingAutomatic());

    p.draw(painter);

    // Then invoke normal with supplied normals
    verify(painter, times(1)).normal(new Coord3d(.5, .5, .5));
  }

  ///////////////////////////////////////////////////
  //
  // WRONG SUPPLIED NORMALS
  //
  ///////////////////////////////////////////////////

  @Test(expected = IllegalArgumentException.class)
  public void whenBuildWithPointAndInappropriateNumberOfNormal1() {
    // Given polygon and painter
    Polygon p = new Polygon();
    p.add(new Coord3d(0, 0, 0));
    p.add(new Coord3d(1, 0, 0));
    p.add(new Coord3d(1, 1, 0));
    p.add(new Coord3d(0, 1, 0));

    List<Coord3d> customNormals = new ArrayList<>();
    customNormals.add(new Coord3d(.5, .5, .5));
    customNormals.add(new Coord3d(.6, .6, .6));
    customNormals.add(new Coord3d(.7, .7, .7));

    // Expected four normal
    p.setNormals(customNormals, NormalPer.POINT);
  }

  @Test(expected = IllegalArgumentException.class)
  public void whenBuildWithPointAndInappropriateNumberOfNormal2() {
    // Given polygon and painter
    Polygon p = new Polygon();
    p.add(new Coord3d(0, 0, 0));
    p.add(new Coord3d(1, 0, 0));
    p.add(new Coord3d(1, 1, 0));
    p.add(new Coord3d(0, 1, 0));

    p.splitInTriangles = false; // IMPORTANT

    List<Coord3d> customNormals = new ArrayList<>();
    customNormals.add(new Coord3d(.5, .5, .5));
    customNormals.add(new Coord3d(.6, .6, .6));

    // Expected a single normal
    p.setNormals(customNormals, NormalPer.GEOMETRY);
  }


  @Test(expected = IllegalArgumentException.class)
  public void whenBuildWithPointAndInappropriateNumberOfNormal3() {
    // Given polygon and painter
    Polygon p = new Polygon();
    p.add(new Coord3d(0, 0, 0));
    p.add(new Coord3d(1, 0, 0));
    p.add(new Coord3d(1, 1, 0));
    p.add(new Coord3d(0, 1, 0));

    p.splitInTriangles = true; // IMPORTANT

    List<Coord3d> customNormals = new ArrayList<>();
    customNormals.add(new Coord3d(.5, .5, .5));
    customNormals.add(new Coord3d(.6, .6, .6));
    customNormals.add(new Coord3d(.7, .7, .7));

    // Expected a two normal
    p.setNormals(customNormals, NormalPer.GEOMETRY);
  }

}
