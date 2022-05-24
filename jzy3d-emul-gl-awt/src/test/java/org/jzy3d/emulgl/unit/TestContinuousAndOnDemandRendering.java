package org.jzy3d.emulgl.unit;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;

/**
 * Warning : execution is slow when using mocks
 * 
 * @author martin
 */
public class TestContinuousAndOnDemandRendering {
  @Ignore("This test fail if it is ran from CLI with all other tests in the module, but work when ran standalone (even from CLI)")
  @Test
  public void whenComponentResizeWithoutAnimator_thenViewRender() {

    EmulGLChartFactory factory = new EmulGLChartFactory() {
      @Override
      public CameraMock newCamera(Coord3d center) {
        return new CameraMock(center);
      }
    };

    Quality q = Quality.Nicest();
    q.setAlphaActivated(true);


    Chart chart = factory.newChart(q);
    chart.add(SampleGeom.surface());
    FrameAWT frame = (FrameAWT) chart.open();
    CameraMock cam = (CameraMock) chart.getView().getCamera();

    // Precondition
    Assert.assertFalse(q.isAnimated());

    // When Resize
    int nShootBeforeResize = cam.counter_doShoot;
    frame.setSize(654, 321);
    //frame.repaint();
    chart.sleep(500); // let time for resize to happen
    int nShootAfterResize = cam.counter_doShoot;

    System.out.println("cam.shoot Before : " + nShootBeforeResize);
    System.out.println("cam.shoot After  : " + nShootAfterResize);

    // Then camera updates
    Assert.assertTrue(nShootAfterResize > nShootBeforeResize);


  }

  class CameraMock extends Camera {

    public CameraMock() {
      super();
    }

    public CameraMock(Coord3d target) {
      super(target);
    }

    @Override
    public void doShoot(IPainter painter, CameraMode projection) {
      super.doShoot(painter, projection);
      counter_doShoot++;
    }

    protected int counter_doShoot = 0;
  }

}
