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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Before;
import org.junit.Test;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.mockito.InOrder;
import panamagl.opengl.GL;

/**
 * Check that {@link PanamaGLPainter} forwards {@link IPainter} calls to the expected {@link GL}
 * methods, using a mock GL so that no native GL context is required.
 */
public class TestPanamaGLPainter_GL {
  GL gl;
  PanamaGLPainter painter;

  @Before
  public void before() {
    gl = mock(GL.class);
    painter = new PanamaGLPainter();
    painter.setGL(gl);
  }

  // ---------------------------------------------------------------------------------------------
  // Mappings

  @Test
  public void polygonOffset() {
    painter.glPolygonOffset(1.5f, 2.5f);

    verify(gl).glPolygonOffset(1.5f, 2.5f);
  }

  @Test
  public void pixelZoom() {
    painter.glPixelZoom(2, 3);

    verify(gl).glPixelZoom(2, 3);
  }

  @Test
  public void pixelStore() {
    painter.glPixelStore(PixelStore.PACK_ALIGNMENT, 1);
    painter.glPixelStore(PixelStore.UNPACK_ALIGNMENT, 4);

    verify(gl).glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);
    verify(gl).glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 4);
  }

  @Test
  public void stencilFunc() {
    StencilFunc[] funcs = {StencilFunc.GL_ALWAYS, StencilFunc.GL_EQUAL, StencilFunc.GL_GEQUAL,
        StencilFunc.GL_GREATER, StencilFunc.GL_LEQUAL, StencilFunc.GL_LESS, StencilFunc.GL_NEVER,
        StencilFunc.GL_NOTEQUAL};
    int[] glFuncs = {GL.GL_ALWAYS, GL.GL_EQUAL, GL.GL_GEQUAL, GL.GL_GREATER, GL.GL_LEQUAL,
        GL.GL_LESS, GL.GL_NEVER, GL.GL_NOTEQUAL};

    for (int i = 0; i < funcs.length; i++) {
      painter.glStencilFunc(funcs[i], 1, 0xFF);
      verify(gl).glStencilFunc(glFuncs[i], 1, 0xFF);
    }
    verify(gl, never()).glStencilOp(anyInt(), anyInt(), anyInt());
  }

  @Test
  public void stencilTest() {
    painter.glEnable_Stencil();
    painter.glDisable_Stencil();

    verify(gl).glEnable(GL.GL_STENCIL_TEST);
    verify(gl).glDisable(GL.GL_STENCIL_TEST);
  }

  @Test
  public void disableLight() {
    painter.glEnable_Light(1);
    painter.glDisable_Light(1);

    verify(gl).glEnable(GL.GL_LIGHT1);
    verify(gl).glDisable(GL.GL_LIGHT1);
  }

  @Test
  public void newListCompileOnlyOnce() {
    painter.glNewList(1, ListMode.COMPILE);

    verify(gl).glNewList(1, GL.GL_COMPILE);
    verify(gl, never()).glNewList(1, GL.GL_COMPILE_AND_EXECUTE);
  }

  @Test
  public void isList() {
    when(gl.glIsList(3)).thenReturn((byte) 1);

    assertTrue(painter.glIsList(3));
    assertFalse(painter.glIsList(4));
  }

  @Test
  public void clipPlaneReceivesGLPlaneId() {
    double[] equation = {1, 2, 3, 4};
    AtomicReference<double[]> received = new AtomicReference<>();

    doAnswer(i -> {
      received.set(((MemorySegment) i.getArgument(1)).toArray(ValueLayout.JAVA_DOUBLE));
      return null;
    }).when(gl).glClipPlane(eq(GL.GL_CLIP_PLANE2), any());

    // AbstractPainter.clip(..) gives the GL plane id
    painter.glClipPlane(painter.clipPlaneId(2), equation);

    assertArrayEquals(equation, received.get(), 0);
  }

  @Test
  public void lightParameters() {
    AtomicReference<float[]> received = new AtomicReference<>();

    doAnswer(i -> {
      received.set(((MemorySegment) i.getArgument(2)).toArray(ValueLayout.JAVA_FLOAT));
      return null;
    }).when(gl).glLightfv(eq(GL.GL_LIGHT0), eq(GL.GL_DIFFUSE), any());

    painter.glLight_Diffuse(0, new Color(0.1f, 0.2f, 0.3f, 0.4f));

    assertArrayEquals(new float[] {0.1f, 0.2f, 0.3f, 0.4f}, received.get(), 0);
  }

  @Test
  public void configureDepthWithLessOrEqual() {
    painter.setCanvas(null);
    org.jzy3d.plot3d.rendering.canvas.Quality q =
        org.jzy3d.plot3d.rendering.canvas.Quality.Advanced();

    painter.configureGL(q);

    verify(gl).glDepthFunc(GL.GL_LEQUAL);
  }

  @Test
  public void getFloatsWithOffset() {
    doAnswer(i -> {
      MemorySegment s = i.getArgument(1);
      s.setAtIndex(ValueLayout.JAVA_FLOAT, 0, 7f);
      s.setAtIndex(ValueLayout.JAVA_FLOAT, 1, 8f);
      return null;
    }).when(gl).glGetFloatv(eq(GL.GL_DEPTH_RANGE), any());

    float[] values = new float[3];
    painter.glGetFloatv(GL.GL_DEPTH_RANGE, values, 1);

    assertArrayEquals(new float[] {0, 7, 8}, values, 0);
  }

  // ---------------------------------------------------------------------------------------------
  // GLU

  @Test
  public void gluQuadricsAreDrawnAndReleased() {
    MemorySegment quadric = MemorySegment.ofAddress(1234);
    when(gl.gluNewQuadric()).thenReturn(quadric);

    painter.gluDisk(1, 2, 10, 2);
    painter.gluSphere(3, 10, 10);
    painter.gluCylinder(1, 2, 3, 10, 2);

    verify(gl).gluDisk(quadric, 1, 2, 10, 2);
    verify(gl).gluSphere(quadric, 3, 10, 10);
    verify(gl).gluCylinder(quadric, 1, 2, 3, 10, 2);
    verify(gl, times(3)).gluDeleteQuadric(quadric);
  }

  // ---------------------------------------------------------------------------------------------
  // Pixels

  @Test
  public void drawPixelsFromHeapByteBuffer() {
    ByteBuffer pixels = ByteBuffer.wrap(new byte[] {1, 2, 3, 4, 5, 6, 7, 8});
    assertDrawnPixels(pixels, new byte[] {1, 2, 3, 4, 5, 6, 7, 8});
  }

  @Test
  public void drawPixelsFromDirectByteBuffer() {
    ByteBuffer pixels = ByteBuffer.allocateDirect(8);
    pixels.put(new byte[] {1, 2, 3, 4, 5, 6, 7, 8}).rewind();
    assertDrawnPixels(pixels, new byte[] {1, 2, 3, 4, 5, 6, 7, 8});
  }

  @Test
  public void drawImageWithZoom() {
    ByteBuffer pixels = ByteBuffer.wrap(new byte[] {1, 2, 3, 4, 5, 6, 7, 8});

    painter.drawImage(pixels, 2, 1, new Coord2d(2, 3), new Coord3d(4, 5, 6));

    InOrder order = inOrder(gl);
    order.verify(gl).glPixelZoom(2, 3);
    order.verify(gl).glRasterPos3f(4, 5, 6);
    order.verify(gl).glDrawPixels(eq(2), eq(1), eq(GL.GL_RGBA), eq(GL.GL_UNSIGNED_BYTE), any());
  }

  protected void assertDrawnPixels(ByteBuffer pixels, byte[] expected) {
    AtomicReference<byte[]> received = new AtomicReference<>();

    doAnswer(i -> {
      MemorySegment s = i.getArgument(4);
      assertTrue("GL must receive native memory", s.isNative());
      received.set(s.toArray(ValueLayout.JAVA_BYTE));
      return null;
    }).when(gl).glDrawPixels(eq(2), eq(1), eq(GL.GL_RGBA), eq(GL.GL_UNSIGNED_BYTE), any());

    painter.glDrawPixels(2, 1, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pixels);

    assertArrayEquals(expected, received.get());
  }

  @Test
  public void bitmap() {
    AtomicReference<byte[]> received = new AtomicReference<>();

    doAnswer(i -> {
      received.set(((MemorySegment) i.getArgument(6)).toArray(ValueLayout.JAVA_BYTE));
      return null;
    }).when(gl).glBitmap(eq(8), eq(2), eq(0f), eq(0f), eq(9f), eq(0f), any());

    painter.glBitmap(8, 2, 0, 0, 9, 0, new byte[] {0, 1, 2, 3}, 2);

    assertArrayEquals(new byte[] {2, 3}, received.get());
  }

  // ---------------------------------------------------------------------------------------------
  // Selection and feedback buffers

  @Test
  public void selectBufferIsFilledWhenLeavingSelectMode() {
    IntBuffer select = ByteBuffer.allocateDirect(4 * Integer.BYTES)
        .order(ByteOrder.nativeOrder()).asIntBuffer();

    AtomicReference<MemorySegment> glSelect = new AtomicReference<>();
    doAnswer(i -> {
      glSelect.set(i.getArgument(1));
      return null;
    }).when(gl).glSelectBuffer(eq(4), any());

    // GL writes hits in the select buffer and returns their count when leaving select mode
    when(gl.glRenderMode(GL.GL_RENDER)).thenAnswer(i -> {
      for (int k = 0; k < 4; k++)
        glSelect.get().setAtIndex(ValueLayout.JAVA_INT, k, 10 + k);
      return 1;
    });

    painter.glSelectBuffer(4, select);
    painter.glRenderMode(RenderMode.SELECT);
    int hits = painter.glRenderMode(RenderMode.RENDER);

    assertEquals(1, hits);
    assertEquals(10, select.get(0));
    assertEquals(13, select.get(3));
  }

  @Test
  public void feedbackBufferIsFilledWhenLeavingFeedbackMode() {
    FloatBuffer feedback = FloatBuffer.allocate(3);

    AtomicReference<MemorySegment> glFeedback = new AtomicReference<>();
    doAnswer(i -> {
      glFeedback.set(i.getArgument(2));
      return null;
    }).when(gl).glFeedbackBuffer(eq(3), eq(GL.GL_3D_COLOR), any());

    when(gl.glRenderMode(GL.GL_RENDER)).thenAnswer(i -> {
      glFeedback.get().setAtIndex(ValueLayout.JAVA_FLOAT, 2, 0.5f);
      return 3;
    });

    painter.glFeedbackBuffer(3, GL.GL_3D_COLOR, feedback);
    painter.glRenderMode(RenderMode.FEEDBACK);
    painter.glRenderMode(RenderMode.RENDER);

    assertEquals(0.5f, feedback.get(2), 0);
  }

  // ---------------------------------------------------------------------------------------------
  // Text

  @Test
  public void bitmapLengthUsesFontMetrics() {
    int length = painter.glutBitmapLength(Font.Helvetica_12.getCode(), "Hello");

    assertEquals(painter.getTextLengthInPixels(Font.Helvetica_12, "Hello"), length);
    assertTrue(length > 0);
  }

  @Test
  public void bitmapStringDrawsAtRasterPositionAndMovesIt() {
    doAnswer(i -> {
      MemorySegment s = i.getArgument(1);
      s.setAtIndex(ValueLayout.JAVA_FLOAT, 0, 1f); // red raster color
      s.setAtIndex(ValueLayout.JAVA_FLOAT, 3, 1f);
      return null;
    }).when(gl).glGetFloatv(eq(GL.GL_CURRENT_RASTER_COLOR), any());

    AtomicReference<byte[]> pixels = new AtomicReference<>();
    AtomicReference<Integer> width = new AtomicReference<>();
    doAnswer(i -> {
      width.set(i.getArgument(0));
      pixels.set(((MemorySegment) i.getArgument(4)).toArray(ValueLayout.JAVA_BYTE));
      return null;
    }).when(gl).glDrawPixels(anyInt(), anyInt(), eq(GL.GL_RGBA), eq(GL.GL_UNSIGNED_BYTE), any());

    painter.glutBitmapString(Font.Helvetica_12.getCode(), "Hello");

    // Then text is drawn
    int advance = painter.getTextLengthInPixels(Font.Helvetica_12, "Hello");
    assertEquals(advance, (int) width.get());
    assertTrue("some pixels must be red", containsRedPixel(pixels.get()));

    // Then the raster position is moved to the end of the text
    verify(gl).glBitmap(eq(0), eq(0), eq(0f), eq(0f), eq((float) advance), anyFloat(), any());
  }

  @Test
  public void bitmapStringIgnoresEmptyString() {
    painter.glutBitmapString(Font.Helvetica_12.getCode(), "");

    verify(gl, never()).glDrawPixels(anyInt(), anyInt(), anyInt(), anyInt(), any());
  }

  private static float anyFloat() {
    return org.mockito.ArgumentMatchers.anyFloat();
  }

  private boolean containsRedPixel(byte[] rgba) {
    for (int i = 0; i < rgba.length; i += 4) {
      if ((rgba[i] & 0xFF) > 200 && (rgba[i + 1] & 0xFF) < 50 && (rgba[i + 3] & 0xFF) > 200)
        return true;
    }
    return false;
  }

  // ---------------------------------------------------------------------------------------------
  // Threads

  @Test
  public void acquireGLOnlyOnRenderingThread() throws Exception {
    // No rendering thread yet
    assertNull(painter.acquireGL());

    // Rendering thread is the current one
    painter.setGLThread(Thread.currentThread());
    assertSame(gl, painter.acquireGL());

    // Another thread can't acquire GL
    AtomicReference<Object> other = new AtomicReference<>("not called");
    Thread t = new Thread(() -> other.set(painter.acquireGL()));
    t.start();
    t.join();
    assertNull(other.get());
  }

  @Test
  public void paintFromAnotherThreadThanTheOneThatBuiltThePainter() throws Exception {
    AtomicReference<Throwable> error = new AtomicReference<>();

    Thread renderingThread = new Thread(() -> {
      try {
        for (int k = 0; k < 1000; k++) {
          painter.glLight_Diffuse(0, Color.RED);
          painter.glMaterial(org.jzy3d.plot3d.rendering.lights.MaterialProperty.DIFFUSE,
              Color.BLUE, true);
        }
      } catch (Throwable t) {
        error.set(t);
      }
    });
    renderingThread.start();
    renderingThread.join();

    assertNull(String.valueOf(error.get()), error.get());
    verify(gl, times(1000)).glLightfv(eq(GL.GL_LIGHT0), eq(GL.GL_DIFFUSE), any());
  }
}
