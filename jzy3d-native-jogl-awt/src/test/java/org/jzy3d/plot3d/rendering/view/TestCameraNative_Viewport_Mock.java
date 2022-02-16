package org.jzy3d.plot3d.rendering.view;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.os.OperatingSystem;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.ICanvasListener;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class TestCameraNative_Viewport_Mock {
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

    NativeDesktopPainter painter = mockWithViewportCheck("Windows 10", scaleGPU, scaleJVM, check);

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
    // Given : the same on macOS : nothing applied
    
    painter = mockWithViewportCheck("Mac OS X", scaleGPU, scaleJVM, check);

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


  protected NativeDesktopPainter mockWithViewportCheck(String osName, Coord2d scaleHard,
      Coord2d scaleVM, CheckViewport check) {
    NativeDesktopPainter painter = new NativeDesktopPainter() {
      @Override
      public OperatingSystem getOS() {
        return new OperatingSystem(osName);
      }
      
      @Override
      public void glViewport(int x, int y, int width, int height) {
        check.x = x;
        check.y = x;
        check.width = width;
        check.height = height;
      }
    };

    painter.setCanvas(mockCanvas(scaleHard, scaleVM));
    return painter;
  }



  protected ICanvas mockCanvas(Coord2d scaleHard, Coord2d scaleVM) {
    return new ICanvas() {
      @Override
      public View getView() {
        return null;
      }

      @Override
      public int getRendererWidth() {
        return 0;
      }

      @Override
      public int getRendererHeight() {
        return 0;
      }

      @Override
      public void screenshot(File file) throws IOException {}

      @Override
      public Object screenshot() {
        return null;
      }

      @Override
      public void forceRepaint() {}

      @Override
      public void dispose() {}

      @Override
      public void addMouseController(Object o) {}

      @Override
      public void addKeyController(Object o) {}

      @Override
      public void removeMouseController(Object o) {}

      @Override
      public void removeKeyController(Object o) {}

      @Override
      public String getDebugInfo() {
        return null;
      }

      @Override
      public void setPixelScale(float[] scale) {}

      @Override
      public Coord2d getPixelScale() {
        return scaleHard;
      }

      @Override
      public Coord2d getPixelScaleJVM() {
        return scaleVM;
      }

      @Override
      public double getLastRenderingTimeMs() {
        return 0;
      }

      @Override
      public void addCanvasListener(ICanvasListener listener) {}

      @Override
      public void removeCanvasListener(ICanvasListener listener) {}

      @Override
      public List<ICanvasListener> getCanvasListeners() {
        return null;
      }
    };
  }
}
