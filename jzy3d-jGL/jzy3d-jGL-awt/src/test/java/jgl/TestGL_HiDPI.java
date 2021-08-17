package jgl;

import org.junit.Assert;
import org.junit.Test;

import jgl.wt.awt.GL;

public class TestGL_HiDPI {
  @Test
  public void whenPixelScaleIsGiven_ThenActualWidthIsScaled() {
    // Given openGL
	GL gl = new GL();

    int width = 200;
    int height = 100;
    float pixelScale = 2;
    // which may occur if HiDPI AND gl.autoAdaptToHiDPI = true;

    gl.pixelScaleX = pixelScale;
    gl.pixelScaleY = pixelScale;

    // ----------------------------------------------------
    // When invoke viewport

    gl.glViewport(0, 0, width, height);


    // ----------------------------------------------------
    // Then appropriate size is computed

    Assert.assertEquals(width, gl.desiredWidth);
    Assert.assertEquals(height, gl.desiredHeight);
    Assert.assertEquals((int) (pixelScale * width), gl.actualWidth);
    Assert.assertEquals((int) (pixelScale * height), gl.actualHeight);

    Assert.assertEquals((int) (width * pixelScale * height * pixelScale),
        gl.getContext().ColorBuffer.Buffer.length);

  }
}
