package org.jzy3d.plot3d.rendering.view;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.mocks.jzy3d.Mocks;
import org.jzy3d.os.OperatingSystem;
import org.jzy3d.os.WindowingToolkit;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class TestWindowsHiDPI_Hack {
  @Test
  public void test() {

    int frameWidth = 400;
    int frameHeight = 200;

    // -----------------------------------------------
    // Given : a Camera under test

    Camera camera = new Camera();

    // -----------------------------------------------
    // Given : JVM and GPU pixel ratio

    Coord2d scaleGPU = new Coord2d(1, 1);
    Coord2d scaleJVM = new Coord2d(1, 1);

    // -----------------------------------------------
    // Given : HiDPI expectation to true
    Quality q = Quality.Advanced().setHiDPIEnabled(true);

    // -----------------------------------------------
    // Given : a painter set with these parameters and
    // recording invocations

    CheckViewport check = new CheckViewport();

    NativeDesktopPainter painter = mockPainter("Windows 10", WindowingToolkit.AWT, scaleGPU, scaleJVM, check);

    // -----------------------------------------------
    // Given no pixel scale

    scaleGPU.set(1, 1);
    scaleJVM.set(1, 1);

    // When applying viewport parameters (due to window size)
    camera.setViewPort(frameWidth, frameHeight, 0, 1);
    camera.applyViewport(painter);

    // Then NO hack scale applied
    Assert.assertEquals(frameWidth, check.width);
    Assert.assertEquals(frameHeight, check.height);

    // -----------------------------------------------
    // Given : JVM has a pixel scale but not the JOGL canvas,
    // (case of Windows 10 bug ), we want to manually scale viewport

    int PIXEL_RATIO = 2;

    scaleGPU.set(1, 1);
    scaleJVM.set(PIXEL_RATIO, PIXEL_RATIO);
    //q.setHiDPI(HiDPI.ON);

    // When applying viewport parameters (due to window size)
    camera.setViewPort(frameWidth, frameHeight, 0, 1);
    camera.applyViewport(painter);

    // Then hack scale IS applied
    Assert.assertEquals(frameWidth * PIXEL_RATIO, check.width);
    Assert.assertEquals(frameHeight * PIXEL_RATIO, check.height);
    
    // -----------------------------------------------
    // Ensure this does not apply to Swing chart

    painter = mockPainter("Windows 10", WindowingToolkit.Swing, scaleGPU, scaleJVM, check);

    
    scaleGPU.set(1, 1);
    scaleJVM.set(PIXEL_RATIO, PIXEL_RATIO);
    //q.setHiDPI(HiDPI.ON);

    // When applying viewport parameters (due to window size)
    camera.setViewPort(frameWidth, frameHeight, 0, 1);
    camera.applyViewport(painter);

    // Then hack scale IS applied
    Assert.assertEquals(frameWidth * 1, check.width); // not PIXEL_RATIO!
    Assert.assertEquals(frameHeight * 1, check.height);


    // -----------------------------------------------
    // Given : the same on macOS : nothing applied
    
    painter = mockPainter("Mac OS X", WindowingToolkit.AWT, scaleGPU, scaleJVM, check);

    scaleGPU.set(1, 1);
    scaleJVM.set(PIXEL_RATIO, PIXEL_RATIO);
    //q.setHiDPI(HiDPI.ON);

    // When applying viewport parameters (due to window size)
    camera.setViewPort(frameWidth, frameHeight, 0, 1);
    camera.applyViewport(painter);

    // Then hack scale IS applied
    Assert.assertEquals(frameWidth, check.width);
    Assert.assertEquals(frameHeight, check.height);


  }


  ///////////////////////////////////////////////////////
  //
  // TEST UTILITIES
  //
  ///////////////////////////////////////////////////////


  class CheckViewport {
    public int x;
    public int y;
    public int width;
    public int height;

  }


  protected NativeDesktopPainter mockPainter(String osName, WindowingToolkit wt, Coord2d scaleHard,
      Coord2d scaleVM, CheckViewport check) {
    NativeDesktopPainter painter = new NativeDesktopPainter() {
      @Override
      public OperatingSystem getOS() {
        return new OperatingSystem(osName);
      }
      
      @Override
      public WindowingToolkit getWindowingToolkit() {
        return wt;
      }
      
      @Override
      public void glViewport(int x, int y, int width, int height) {
        check.x = x;
        check.y = x;
        check.width = width;
        check.height = height;
      }
    };

    painter.setCanvas(Mocks.Canvas(scaleHard, scaleVM));
    return painter;
  }


}
