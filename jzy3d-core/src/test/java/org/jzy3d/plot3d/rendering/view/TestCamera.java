package org.jzy3d.plot3d.rendering.view;

import java.util.Random;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jzy3d.maths.Coord3d;

public class TestCamera {
  Random r = new Random();

  @Before
  public void initRNG() {
    r.setSeed(0);
  }

  @Test
  public void whenPointOnLeftOfCameraAxis_ThenSideReturnsTrue() {
    // Given a camera with eye-target lying on the X axis {Y=0,Z=0}
    // looking in the direction where X grows

    int Y = 0;
    int Z = 0;

    Camera camera = new Camera();
    camera.setEye(new Coord3d(0, Y, Z));
    camera.setTarget(new Coord3d(10, Y, Z));
    camera.setUp(camera.getEye().add(0, 0, 1));


    // ----------------------------------------
    // When a point "standing on the left" of
    // this eye-target axis
    // (which means a positive Y coordinate)
    Coord3d onLeft = new Coord3d(5, +100, 5);

    // Then seen seen on Left
    Assert.assertTrue(camera.side(onLeft));

    // Some more test on left side
    for (int i = 1; i < 1000; i++) {
      float v = r.nextFloat() * 1000;
      Assert.assertTrue(camera.side(new Coord3d(v, i, v)));
    }

    // ----------------------------------------
    // When a point "standing on the right" of
    // this eye-target axis
    // (which means a negative Y coordinate)
    Coord3d onRight = new Coord3d(5, -100, 5);

    // Then seen on right
    Assert.assertFalse(camera.side(onRight));

    // Some more test on right side
    for (int i = -1; i > -1000; i--) {
      float v = r.nextFloat() * 1000;
      Assert.assertFalse(camera.side(new Coord3d(v, i, v)));
    }
    
    
    // ----------------------------------------
    // Flip camera to check that object on left 
    // is now on right side.
    
    Coord3d eye = camera.getEye();
    Coord3d target = camera.getTarget();
    camera.setPosition(target, eye);
    
    Assert.assertFalse(camera.side(onLeft));
    Assert.assertTrue(camera.side(onRight));

  }

}