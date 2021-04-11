package org.jzy3d.plot3d.rendering.canvas;

import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import jgl.GL;
import jgl.context.gl_util;

public class jGLUtil {

  public static Color glIntToColor(int color) {
    float r = gl_util.ItoR(color);
    float g = gl_util.ItoG(color);
    float b = gl_util.ItoB(color);
    float a = gl_util.ItoA(color);

    return new Color(r, g, b, a);
  }

  public static Color getBackgroundColor(GL gl) {
    int clearColor = gl.getContext().ColorBuffer.IntClearColor;
    return glIntToColor(clearColor);
  }

  public static java.awt.Color getBackgroundColorAWT(GL gl) {
    return AWTColor.toAWT(getBackgroundColor(gl));
  }

}
