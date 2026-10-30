package jgl.wt.awt;

import java.awt.geom.AffineTransform;
import org.junit.Assert;
import org.junit.Test;

public class TestGL_HiDPI {
  @Test
  public void whenPixelScaleIsGiven_ThenActualWidthIsScaled() {
    double pixelScale = 2;

    // Given GL instance working on a Canvas' graphics instance with HiDPI activated
	GL gl = new GL(){
	  protected double getPixelScaleX(AffineTransform globalTransform) {
	    return pixelScale;
	  }

	  protected double getPixelScaleY(AffineTransform globalTransform) {
	    return pixelScale;
	  }
	};

    int width = 200;
    int height = 100;
    // which may occur if HiDPI AND gl.autoAdaptToHiDPI = true;


    // ----------------------------------------------------
    // When invoke viewport with adaptive HiDPI behaviour

    gl.setAutoAdaptToHiDPI(true);
    gl.glViewport(0, 0, width, height);


    // ----------------------------------------------------
    // Then a double size pixel buffer is computed

    Assert.assertEquals(width, gl.getDesiredWidth());
    Assert.assertEquals(height, gl.getDesiredHeight());
    Assert.assertEquals((int) (pixelScale * width), gl.getActualWidth());
    Assert.assertEquals((int) (pixelScale * height), gl.getActualHeight());

    Assert.assertEquals((int) (width * pixelScale * height * pixelScale),
        gl.getContext().ColorBuffer.Buffer.length);

    
    
    // ----------------------------------------------------
    // When invoke viewport with NO adaptive HiDPI behaviour

    gl.setAutoAdaptToHiDPI(false);
    gl.glViewport(0, 0, width, height);


    // ----------------------------------------------------
    // Then a single size pixel buffer is computed

    Assert.assertEquals(width, gl.getDesiredWidth());
    Assert.assertEquals(height, gl.getDesiredHeight());
    Assert.assertEquals((int) width, gl.getActualWidth());
    Assert.assertEquals((int) height, gl.getActualHeight());

    Assert.assertEquals((int) (width * height ),
        gl.getContext().ColorBuffer.Buffer.length);

  }
}
