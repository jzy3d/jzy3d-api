/*******************************************************************************
 * Copyright (c) 2022, 2023 Martin Pernollet & contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 *******************************************************************************/
package org.jzy3d.painters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import panamagl.factory.PanamaGLFactory;
import panamagl.offscreen.FBO;
import panamagl.opengl.GL;
import panamagl.opengl.GLContext;

/**
 * Run {@link PanamaGLPainter} against a real OpenGL context rendering into an offscreen FBO and
 * check the rendered pixels.
 *
 * Skipped if no OpenGL context can be created (e.g. no display).
 */
public class TestPanamaGLPainter_NativeGL {
  static final int WIDTH = 64;
  static final int HEIGHT = 64;

  PanamaGLFactory factory;
  GLContext context;
  GL gl;
  FBO fbo;
  PanamaGLPainter painter;

  @Before
  public void before() {
    try {
      factory = PanamaGLFactory.select();
      context = factory.newGLContext();
      gl = factory.newGL();
      fbo = factory.newFBO(WIDTH, HEIGHT);
      fbo.prepare(gl);
    } catch (Throwable t) {
      Assume.assumeNoException("No OpenGL context available", t);
    }

    painter = new PanamaGLPainter();
    painter.setGL(gl);
    painter.setGLThread(Thread.currentThread());

    // A 2D projection from [-1;1] to the whole FBO
    painter.glViewport(0, 0, WIDTH, HEIGHT);
    painter.glMatrixMode_Projection();
    painter.glLoadIdentity();
    painter.glOrtho(-1, 1, -1, 1, -1, 1);
    painter.glMatrixMode_ModelView();
    painter.glLoadIdentity();

    painter.glClearColor(0, 0, 0, 1);
    painter.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
  }

  @After
  public void after() {
    if (factory != null && context != null) {
      fbo.release(gl);
      factory.destroyContext();
    }
  }

  @Test
  public void gluSphereIsRendered() {
    painter.color(Color.RED);
    painter.gluSphere(0.5, 16, 16);

    assertNoGLError();
    assertPixel(WIDTH / 2, HEIGHT / 2, 255, 0, 0);
    assertPixel(1, 1, 0, 0, 0);
  }

  @Test
  public void gluDiskIsRendered() {
    painter.color(Color.GREEN);
    painter.gluDisk(0, 0.5, 16, 2);

    assertNoGLError();
    assertPixel(WIDTH / 2, HEIGHT / 2, 0, 255, 0);
  }

  @Test
  public void polygonOffset() {
    painter.glEnable_PolygonOffsetFill();
    painter.glPolygonOffset(1, 1);

    assertNoGLError();
  }

  @Test
  public void drawImage() {
    // A 2x2 blue image
    ByteBuffer image = ByteBuffer.allocate(2 * 2 * 4);
    for (int i = 0; i < 4; i++)
      image.put(new byte[] {0, 0, (byte) 255, (byte) 255});
    image.rewind();

    // Zoom by 8 at the center of the FBO
    painter.drawImage(image, 2, 2, new Coord2d(8, 8), new Coord3d(0, 0, 0));

    assertNoGLError();
    assertPixel(WIDTH / 2 + 4, HEIGHT / 2 + 4, 0, 0, 255);
    assertPixel(WIDTH / 2 + 20, HEIGHT / 2 + 20, 0, 0, 0);
  }

  @Test
  public void bitmapStringIsRendered() {
    painter.glutBitmapString(Font.Helvetica_18, "HHHH", new Coord3d(-0.9, -0.5, 0), Color.WHITE);

    assertNoGLError();
    assertTrue("text should have white pixels", countPixels(255, 255, 255) > 0);
  }

  @Test
  public void drawTextPartiallyOutOfViewport() {
    // Text starting 10 pixels at the left of the viewport : the visible part must be drawn
    painter.drawText(Font.Helvetica_18, "HHHHHH", new Coord3d(-1 - 10f / (WIDTH / 2), 0, 0),
        Color.WHITE, 0);

    assertNoGLError();
    assertTrue("text should have white pixels", countPixels(255, 255, 255) > 0);
  }

  @Test
  public void drawTextRotated() {
    painter.drawText(Font.Helvetica_18, "HHHH", new Coord3d(0, 0, 0), Color.WHITE,
        (float) (Math.PI / 2));

    assertNoGLError();

    int[] box = whiteBoundingBox();
    int width = box[2] - box[0];
    int height = box[3] - box[1];
    assertTrue("rotated text should be taller (" + height + ") than wide (" + width + ")",
        height > width);
  }

  /** Bounding box of white pixels : xmin, ymin, xmax, ymax */
  protected int[] whiteBoundingBox() {
    int[] box = {WIDTH, HEIGHT, -1, -1};
    try (Arena arena = Arena.ofConfined()) {
      MemorySegment pixels = arena.allocate(WIDTH * HEIGHT * 4);
      gl.glReadPixels(0, 0, WIDTH, HEIGHT, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pixels);

      for (int y = 0; y < HEIGHT; y++) {
        for (int x = 0; x < WIDTH; x++) {
          if ((pixels.get(ValueLayout.JAVA_BYTE, (y * WIDTH + x) * 4L) & 0xFF) > 128) {
            box[0] = Math.min(box[0], x);
            box[1] = Math.min(box[1], y);
            box[2] = Math.max(box[2], x);
            box[3] = Math.max(box[3], y);
          }
        }
      }
    }
    return box;
  }

  @Test
  public void debugGLThrowsOnRealError() {
    try {
      PanamaGLDebug.debug(gl).glEnable(0x1234);
      org.junit.Assert.fail("expect an exception");
    } catch (PanamaGLDebug.GLErrorException e) {
      assertEquals(GL.GL_INVALID_ENUM, e.getError());
    }
  }

  @Test
  public void selectBufferReturnsHits() {
    IntBuffer select = ByteBuffer.allocateDirect(64 * Integer.BYTES)
        .order(ByteOrder.nativeOrder()).asIntBuffer();

    painter.glSelectBuffer(64, select);
    painter.glRenderMode(RenderMode.SELECT);
    painter.glInitNames();
    painter.glPushName(0);

    painter.glLoadName(42);
    painter.glBegin_Quad();
    painter.glVertex3f(-0.5f, -0.5f, 0);
    painter.glVertex3f(0.5f, -0.5f, 0);
    painter.glVertex3f(0.5f, 0.5f, 0);
    painter.glVertex3f(-0.5f, 0.5f, 0);
    painter.glEnd();

    int hits = painter.glRenderMode(RenderMode.RENDER);

    assertNoGLError();
    assertEquals(1, hits);
    // hit record : number of names, min depth, max depth, names
    assertEquals(1, select.get(0));
    assertEquals(42, select.get(3));
  }

  // ---------------------------------------------------------------------------------------------
  // GPU resources

  @Test
  public void gpuResources() {
    // uses the 64x64 FBO prepared before each test
    new org.jzy3d.junit.PainterGPUConformance(painter).checkAll();
  }

  // ---------------------------------------------------------------------------------------------

  protected void assertNoGLError() {
    assertEquals(GL.GL_NO_ERROR, gl.glGetError());
  }

  protected void assertPixel(int x, int y, int r, int g, int b) {
    try (Arena arena = Arena.ofConfined()) {
      MemorySegment pixel = arena.allocate(4);
      gl.glReadPixels(x, y, 1, 1, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pixel);

      String at = "pixel at " + x + "," + y;
      assertEquals(at + " red", r, pixel.get(ValueLayout.JAVA_BYTE, 0) & 0xFF);
      assertEquals(at + " green", g, pixel.get(ValueLayout.JAVA_BYTE, 1) & 0xFF);
      assertEquals(at + " blue", b, pixel.get(ValueLayout.JAVA_BYTE, 2) & 0xFF);
    }
  }

  protected int countPixels(int r, int g, int b) {
    try (Arena arena = Arena.ofConfined()) {
      MemorySegment pixels = arena.allocate(WIDTH * HEIGHT * 4);
      gl.glReadPixels(0, 0, WIDTH, HEIGHT, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pixels);

      int n = 0;
      for (int i = 0; i < WIDTH * HEIGHT * 4; i += 4) {
        if ((pixels.get(ValueLayout.JAVA_BYTE, i) & 0xFF) == r
            && (pixels.get(ValueLayout.JAVA_BYTE, i + 1) & 0xFF) == g
            && (pixels.get(ValueLayout.JAVA_BYTE, i + 2) & 0xFF) == b)
          n++;
      }
      return n;
    }
  }
}
