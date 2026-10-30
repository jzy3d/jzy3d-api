package org.jzy3d.chart;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;

public class AWTScreenshotUtils {
  public BufferedImage toImage(GL2 gl, int w, int h) {

    gl.glReadBuffer(GL.GL_FRONT); // or GL.GL_BACK

    ByteBuffer glBB = ByteBuffer.allocate(3 * w * h);
    gl.glReadPixels(0, 0, w, h, GL2GL3.GL_BGR, GL.GL_BYTE, glBB);

    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    int[] bd = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int b = 2 * glBB.get();
        int g = 2 * glBB.get();
        int r = 2 * glBB.get();

        bd[(h - y - 1) * w + x] = (r << 16) | (g << 8) | b | 0xFF000000;
      }
    }

    return bi;
  }
}
