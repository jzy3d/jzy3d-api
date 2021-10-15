package jgl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import jgl.context.gl_util;

public class Util {
  public static void debugWriteImageTo(GL gl, String file) {
    if (gl instanceof jgl.wt.awt.GL) {
      debugWriteImageTo(file, ((jgl.wt.awt.GL) gl).getRenderedImage());
    }
  }

  private static void debugWriteImageTo(String file, RenderedImage image) {
    try {
      ImageIO.write(image, "png", new File(file));
      System.err.println("GL write image buffer to : " + file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static BufferedImage debugDepthBufferToBufferedImage(GL gl, String depthBufferOutput) {
    BufferedImage depthOut = debugDepthBufferToBufferedImage(gl);
    debugWriteImageTo(depthBufferOutput, (RenderedImage) depthOut);
    return depthOut;
  }

  public static BufferedImage debugDepthBufferToBufferedImage(GL gl) {
    // Draw depth buffer
    float minDepth = Integer.MAX_VALUE;
    float maxDepth = -Integer.MAX_VALUE;
    int[] color = new int[gl.Context.DepthBuffer.Buffer.length];
    for (int i = 0; i < gl.Context.DepthBuffer.Buffer.length; i++) {
      float depth = gl.Context.DepthBuffer.Buffer[i];
      color[i] = gl_util.RGBAtoI(depth, depth, depth, 1);
      if (minDepth > depth)
        minDepth = depth;
      if (maxDepth < depth)
        maxDepth = depth;
    }
    System.out.println("Max depth" + maxDepth);
    System.out.println("Min depth" + minDepth);
    // Context.
    MemoryImageSource depthBuffer = new MemoryImageSource(gl.Context.Viewport.Width,
        gl.Context.Viewport.Height, color, 0, gl.Context.Viewport.Width);
    if (gl instanceof jgl.wt.awt.GL) {
      jgl.wt.awt.GL awt = (jgl.wt.awt.GL) gl;
      Image jGLDepthBuffer = awt.glJGetComponent().createImage(depthBuffer);
      BufferedImage depthOut = new BufferedImage(jGLDepthBuffer.getWidth(null),
          jGLDepthBuffer.getHeight(null), BufferedImage.TYPE_INT_ARGB);
      ((Graphics2D) depthOut.getGraphics()).drawImage(jGLDepthBuffer, 0, 0, null);
      return depthOut;
    } else {
      return null;
    }
  }
}
