package org.jzy3d.plot3d.rendering.scene;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.RandomGeom;

public class TestDecomposition {
  @Test
  public void givenComposite_whenDecomposition_thenSplitInMonotype() {
    RandomGeom g = new RandomGeom();
    

    // ---------------------
    // When
    Composite cube = g.cube();
    cube.setDisplayed(false);

    //Then
    Assert.assertEquals(0, Decomposition.getDecomposition(cube).size());

    // ---------------------
    // When
    cube.setDisplayed(true);
    
    // Then
    Assert.assertEquals(6, Decomposition.getDecomposition(cube).size());

    // ---------------------
    // When
    cube.setDisplayed(true);
    cube.setFaceDisplayed(false);
    cube.setWireframeDisplayed(true);
    cube.setBoundingBoxDisplayed(false);
    
    // Then
    Assert.assertEquals(1, Decomposition.getDecomposition(cube).size());

    // ---------------------
    // When
    cube.setDisplayed(true);
    cube.setFaceDisplayed(false);
    cube.setWireframeDisplayed(false);
    cube.setBoundingBoxDisplayed(true);
    
    // Then
    Assert.assertEquals(1, Decomposition.getDecomposition(cube).size());

  }

}
