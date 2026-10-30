package org.jzy3d.plot3d.rendering.view;

import java.util.Random;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jzy3d.maths.BoundingBox2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.mocks.jzy3d.Mocks;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.rendering.view.modes.ProjectionMode;
import org.mockito.Mockito;

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
  
  @Test
  public void renderingVolume() {
    // Given a camera with eye-target lying on the X axis {Y=0,Z=0}
    // looking in the direction where X grows

    int Y = 0;
    int Z = 0;
    
    float EYE_TARGET_DISTANCE = 10;
    
    float SPHERE_RADIUS = 11;

    Camera camera = new Camera();
    camera.setEye(new Coord3d(0, Y, Z));
    camera.setTarget(new Coord3d(EYE_TARGET_DISTANCE, Y, Z));
    camera.setUp(camera.getEye().add(0, 0, 1));
    
    IPainter painter = Mocks.Painter();
    Mockito.when(painter.getView()).thenReturn(Mocks.View());

    // --------------------------------------------
    // When configuring a rendering sphere
    
    camera.setRenderingSphereRadius(SPHERE_RADIUS);
    
    // Then clipping planes and sphere radius are defined accordingly
    
    Assert.assertEquals(SPHERE_RADIUS, camera.getRenderingSphereRadius(), 0.1);
    Assert.assertEquals(EYE_TARGET_DISTANCE - SPHERE_RADIUS*2 , camera.getNear(), 0.1);
    Assert.assertEquals(EYE_TARGET_DISTANCE + SPHERE_RADIUS*2, camera.getFar(), 0.1);
    Assert.assertEquals(ProjectionMode.Projection3D, camera.getProjectionMode());
    
    // --------------------------------------------
    // When shooting in orthographic projection
    
    camera.setViewportMode(ViewportMode.SQUARE); // won't change main processing
    camera.setViewportMode(ViewportMode.STRETCH_TO_FILL); // won't change main processing
    
    camera.shoot(painter, CameraMode.ORTHOGONAL);
    
    // Then painter.glOrtho is called with these parameters
    Assert.assertEquals(-(double)SPHERE_RADIUS, camera.ortho.left, 0.1);
    Assert.assertEquals(+(double)SPHERE_RADIUS, camera.ortho.right, 0.1);
    Assert.assertEquals(-(double)SPHERE_RADIUS, camera.ortho.bottom, 0.1);
    Assert.assertEquals(+(double)SPHERE_RADIUS, camera.ortho.top, 0.1);
    Assert.assertEquals(EYE_TARGET_DISTANCE - SPHERE_RADIUS*2 , camera.ortho.near, 0.1);
    Assert.assertEquals(EYE_TARGET_DISTANCE + SPHERE_RADIUS*2, camera.ortho.far, 0.1);


    // --------------------------------------------
    // When shooting in orthographic projection with a no stretch setting on a non square canvas    
    
    float RATIO = 2;
    float WIDTH = 600;
    float HEIGHT = WIDTH / RATIO;
    
    camera.setViewportMode(ViewportMode.RECTANGLE_NO_STRETCH);
    camera.setViewPort((int)WIDTH, (int)HEIGHT); // change main processing
    
    camera.shoot(painter, CameraMode.ORTHOGONAL);

    
    // Then painter.glOrtho is called with these parameters
    Assert.assertEquals(-(double)SPHERE_RADIUS * RATIO, camera.ortho.left, 0.1);
    Assert.assertEquals(+(double)SPHERE_RADIUS * RATIO, camera.ortho.right, 0.1);
    Assert.assertEquals(-(double)SPHERE_RADIUS, camera.ortho.bottom, 0.1);
    Assert.assertEquals(+(double)SPHERE_RADIUS, camera.ortho.top, 0.1);
    Assert.assertEquals(EYE_TARGET_DISTANCE - SPHERE_RADIUS*2 , camera.ortho.near, 0.1);
    Assert.assertEquals(EYE_TARGET_DISTANCE + SPHERE_RADIUS*2, camera.ortho.far, 0.1);

    
    // -------------------------------------------
    // When shooting in perspective projection

    camera.shoot(painter, CameraMode.PERSPECTIVE);

    // Then painter.gluPerspective is called instead
    
    //System.out.println(camera.ortho);

  }
  
  @Test
  public void renderingPlane() {
    int X = 0;
    int Y = 0;
    
    float EYE_TARGET_DISTANCE = 10;
    
//    float SPHERE_RADIUS = 11;

    Camera camera = new Camera();
    camera.setEye(new Coord3d(X, Y, -EYE_TARGET_DISTANCE));
    camera.setTarget(new Coord3d(X, Y, 0));
    camera.setUp(camera.getEye().add(0, 1, 0));
    
    IPainter painter = Mocks.Painter();

    // ------------------------------------
    // When configure a rendering square
    
    //float CLIP_DIST = 1;
    BoundingBox2d SQUARE = new BoundingBox2d(-5, +5, -5, +5);
    
    camera.setRenderingSquare(SQUARE);
    
    // Then
    Assert.assertEquals(SQUARE, camera.getRenderingSquare());
    Assert.assertEquals(0, camera.getNear(), 0.1);
    Assert.assertEquals(20, camera.getFar(), 0.1);
    Assert.assertEquals(ProjectionMode.Projection2D, camera.getProjectionMode());

    
    // --------------------------------------------
    // When shooting in orthographic projection
    
    camera.setViewportMode(ViewportMode.SQUARE); // won't change main processing
    camera.setViewportMode(ViewportMode.STRETCH_TO_FILL); // won't change main processing
    
    camera.shoot(painter, CameraMode.ORTHOGONAL);

    
    // Then
    
    Assert.assertEquals(SQUARE.xmin(), camera.ortho.left, 0.1);
    Assert.assertEquals(SQUARE.xmax(), camera.ortho.right, 0.1);
    Assert.assertEquals(SQUARE.ymin(), camera.ortho.bottom, 0.1);
    Assert.assertEquals(SQUARE.ymax(), camera.ortho.top, 0.1);
    Assert.assertEquals(0, camera.ortho.near, 0.1);
    Assert.assertEquals(20, camera.ortho.far, 0.1);


  }
}