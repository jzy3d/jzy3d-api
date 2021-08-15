package jgl;

import org.junit.Assert;
import org.junit.Test;

import jgl.context.gl_util;
import jgl.wt.awt.GL;

public class TestGL_glClearColor {
  @Test
  public void whenClearColor_thenBufferIsResetToThisColor() {
    // Given openGL
	GL gl = new GL();

    gl.glEnable(GL.GL_ALPHA_TEST);

    float R = 1.0f;
    float G = 0.5f;
    float B = 0.0f;
    float A_query = 0.75f; // semi-alpha not registered by jGL
    float A_expect = 0.75f;// 1.0f; // which store a full alpha
    // see gl_colorbuffer.set_clear_color(...)

    // FIXED ALPHA


    // ----------------------------------------------------
    // When defining the clear color
    gl.glClearColor(R, G, B, A_query);

    // Then the clea color is properly set
    int clearColor = gl.getContext().ColorBuffer.IntClearColor;

    Assert.assertEquals((int) (R * 255), gl_util.ItoR(clearColor));
    Assert.assertEquals((int) (G * 255), gl_util.ItoG(clearColor));
    Assert.assertEquals((int) (B * 255), gl_util.ItoB(clearColor));
    Assert.assertEquals((int) (A_expect * 255), gl_util.ItoA(clearColor));


    // ----------------------------------------------------
    // When viewport not defined then color buffer not set

    Assert.assertNull(gl.getContext().ColorBuffer.Buffer);

    // ----------------------------------------------------
    // When defining viewport and calling glClear
    gl.glViewport(0, 0, 2, 5); // a 2x5 color buffer
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);

    // Then colorbuffer is set with no alpha value
    int[] colorBuffer = gl.getContext().ColorBuffer.Buffer;


    Assert.assertEquals((int) (R * 255), gl_util.ItoR(colorBuffer[0]));
    Assert.assertEquals((int) (G * 255), gl_util.ItoG(colorBuffer[0]));
    Assert.assertEquals((int) (B * 255), gl_util.ItoB(colorBuffer[0]));
    Assert.assertEquals((int) (A_expect * 255), gl_util.ItoA(colorBuffer[0]));
  }
}
