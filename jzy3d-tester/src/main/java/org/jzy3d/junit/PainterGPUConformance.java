package org.jzy3d.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.jzy3d.colors.Color;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;

/**
 * Verify that a painter supports the GPU resources API of {@link IPainter} (buffer objects,
 * shaders, textures, framebuffers, queries) by drawing in its current GL context and reading back
 * pixels.
 * 
 * The painter must have a current GL context with a framebuffer of at least {@link #WIDTH} x
 * {@link #HEIGHT} pixels. Each check prepares the context with {@link #prepare()}.
 */
public class PainterGPUConformance {
  public static final int WIDTH = 64;
  public static final int HEIGHT = 64;

  protected IPainter painter;

  public PainterGPUConformance(IPainter painter) {
    this.painter = painter;
  }

  /** Run all checks. */
  public void checkAll() {
    String[] checks = {"vertexBufferObjectsDrawElements", "multiDrawElementsWithOffsets",
        "shaderProgramWithUniform", "texture2D", "framebufferAndQuery"};
    for (String check : checks) {
      prepare();
      try {
        getClass().getMethod(check).invoke(this);
      } catch (java.lang.reflect.InvocationTargetException e) {
        if (e.getCause() instanceof AssertionError)
          throw new AssertionError(check + " : " + e.getCause().getMessage(), e.getCause());
        throw new RuntimeException(check, e.getCause());
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /** Clear in black and use a 2D projection from [-1;1] to the whole viewport. */
  public void prepare() {
    painter.glViewport(0, 0, WIDTH, HEIGHT);
    painter.glMatrixMode_Projection();
    painter.glLoadIdentity();
    painter.glOrtho(-1, 1, -1, 1, -1, 1);
    painter.glMatrixMode_ModelView();
    painter.glLoadIdentity();
    painter.glDisable(GLConstants.GL_DEPTH_TEST);
    painter.glDisable(GLConstants.GL_BLEND);
    painter.glDisable(GLConstants.GL_LIGHTING);
    painter.glClearColor(0, 0, 0, 1);
    painter.glClear(GLConstants.GL_COLOR_BUFFER_BIT | GLConstants.GL_DEPTH_BUFFER_BIT);
  }

  public void vertexBufferObjectsDrawElements() {
    // A quad made of two triangles, drawn from buffer objects
    java.nio.FloatBuffer vertices = ByteBuffer.allocateDirect(4 * 3 * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
    vertices.put(new float[] {-0.5f, -0.5f, 0, 0.5f, -0.5f, 0, 0.5f, 0.5f, 0, -0.5f, 0.5f, 0});
    vertices.rewind();
    IntBuffer elements = IntBuffer.wrap(new int[] {0, 1, 2, 0, 2, 3});

    int[] ids = new int[2];
    painter.glGenBuffers(2, ids, 0);
    assertTrue(ids[0] > 0 && ids[1] > 0);

    painter.glBindBuffer(GLConstants.GL_ARRAY_BUFFER, ids[0]);
    painter.glBufferData(GLConstants.GL_ARRAY_BUFFER, 4 * 3 * 4, vertices,
        GLConstants.GL_STATIC_DRAW);
    painter.glBindBuffer(GLConstants.GL_ELEMENT_ARRAY_BUFFER, ids[1]);
    painter.glBufferData(GLConstants.GL_ELEMENT_ARRAY_BUFFER, 6 * 4, elements,
        GLConstants.GL_STATIC_DRAW);

    painter.color(Color.RED);
    painter.glEnableClientState(GLConstants.GL_VERTEX_ARRAY);
    painter.glVertexPointer(3, GLConstants.GL_FLOAT, 0, 0);
    painter.glDrawElements(GLConstants.GL_TRIANGLES, 6, GLConstants.GL_UNSIGNED_INT, 0);
    painter.glDisableClientState(GLConstants.GL_VERTEX_ARRAY);

    painter.glBindBuffer(GLConstants.GL_ARRAY_BUFFER, 0);
    painter.glBindBuffer(GLConstants.GL_ELEMENT_ARRAY_BUFFER, 0);
    painter.glDeleteBuffers(2, ids, 0);

    assertNoGLError();
    assertPixel(WIDTH / 2, HEIGHT / 2, 255, 0, 0);
    assertPixel(2, 2, 0, 0, 0);
  }

  public void multiDrawElementsWithOffsets() {
    // Two triangles drawn from two ranges of the same element buffer
    java.nio.FloatBuffer vertices = java.nio.FloatBuffer
        .wrap(new float[] {-1, -1, 0, -0.1f, -1, 0, -1, 1, 0, 0.1f, -1, 0, 1, -1, 0, 1, 1, 0});
    IntBuffer elements = IntBuffer.wrap(new int[] {0, 1, 2, 3, 4, 5});

    int[] ids = new int[2];
    painter.glGenBuffers(2, ids, 0);
    painter.glBindBuffer(GLConstants.GL_ARRAY_BUFFER, ids[0]);
    painter.glBufferData(GLConstants.GL_ARRAY_BUFFER, 18 * 4, vertices, GLConstants.GL_STATIC_DRAW);
    painter.glBindBuffer(GLConstants.GL_ELEMENT_ARRAY_BUFFER, ids[1]);
    painter.glBufferData(GLConstants.GL_ELEMENT_ARRAY_BUFFER, 6 * 4, elements,
        GLConstants.GL_STATIC_DRAW);

    painter.color(Color.GREEN);
    painter.glEnableClientState(GLConstants.GL_VERTEX_ARRAY);
    painter.glVertexPointer(3, GLConstants.GL_FLOAT, 0, 0);
    painter.glMultiDrawElements(GLConstants.GL_TRIANGLES, IntBuffer.wrap(new int[] {3, 3}),
        GLConstants.GL_UNSIGNED_INT, java.nio.LongBuffer.wrap(new long[] {0, 3 * 4}), 2);
    painter.glDisableClientState(GLConstants.GL_VERTEX_ARRAY);
    painter.glBindBuffer(GLConstants.GL_ARRAY_BUFFER, 0);
    painter.glBindBuffer(GLConstants.GL_ELEMENT_ARRAY_BUFFER, 0);
    painter.glDeleteBuffers(2, ids, 0);

    assertNoGLError();
    // left triangle and right triangle are drawn
    assertPixel(4, HEIGHT / 4, 0, 255, 0);
    assertPixel(WIDTH - 4, HEIGHT / 4, 0, 255, 0);
  }

  public void shaderProgramWithUniform() {
    int vertex = painter.glCreateShader(GLConstants.GL_VERTEX_SHADER);
    painter.glShaderSource(vertex,
        new String[] {"#version 110\n", "void main() { gl_Position = gl_Vertex; }"});
    painter.glCompileShader(vertex);

    int fragment = painter.glCreateShader(GLConstants.GL_FRAGMENT_SHADER);
    painter.glShaderSource(fragment, new String[] {
        "#version 110\n uniform vec4 color; void main() { gl_FragColor = color; }"});
    painter.glCompileShader(fragment);

    int[] status = new int[1];
    painter.glGetShaderiv(fragment, GLConstants.GL_COMPILE_STATUS, status, 0);
    assertEquals(painter.glGetShaderInfoLog(fragment), GLConstants.GL_TRUE, status[0]);

    int program = painter.glCreateProgram();
    painter.glAttachShader(program, vertex);
    painter.glAttachShader(program, fragment);
    painter.glLinkProgram(program);
    painter.glGetProgramiv(program, GLConstants.GL_LINK_STATUS, status, 0);
    assertEquals(painter.glGetProgramInfoLog(program), GLConstants.GL_TRUE, status[0]);

    painter.glUseProgram(program);
    int location = painter.glGetUniformLocation(program, "color");
    assertTrue(location >= 0);
    painter.glUniform4fv(location, 1, new float[] {0, 0, 1, 1}, 0);

    painter.glBegin_Quad();
    painter.glVertex3f(-0.5f, -0.5f, 0);
    painter.glVertex3f(0.5f, -0.5f, 0);
    painter.glVertex3f(0.5f, 0.5f, 0);
    painter.glVertex3f(-0.5f, 0.5f, 0);
    painter.glEnd();

    painter.glUseProgram(0);
    painter.glDeleteProgram(program);
    painter.glDeleteShader(vertex);
    painter.glDeleteShader(fragment);

    assertNoGLError();
    assertPixel(WIDTH / 2, HEIGHT / 2, 0, 0, 255);
  }

  public void texture2D() {
    // A 2x2 texture with a white pixel
    java.nio.ByteBuffer pixels = ByteBuffer.allocate(2 * 2 * 4);
    pixels.put(new byte[] {(byte) 255, (byte) 255, (byte) 255, (byte) 255});
    pixels.rewind();

    int[] ids = new int[1];
    painter.glGenTextures(1, ids, 0);
    painter.glActiveTexture(GLConstants.GL_TEXTURE0);
    painter.glBindTexture(GLConstants.GL_TEXTURE_2D, ids[0]);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_2D, GLConstants.GL_TEXTURE_MIN_FILTER,
        GLConstants.GL_NEAREST);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_2D, GLConstants.GL_TEXTURE_MAG_FILTER,
        GLConstants.GL_NEAREST);
    painter.glTexImage2D(GLConstants.GL_TEXTURE_2D, 0, GLConstants.GL_RGBA, 2, 2, 0,
        GLConstants.GL_RGBA, GLConstants.GL_UNSIGNED_BYTE, pixels);

    painter.glEnable(GLConstants.GL_TEXTURE_2D);
    painter.color(Color.WHITE);
    painter.glBegin_Quad();
    painter.glTexCoord2f(0, 0);
    painter.glVertex3f(-1, -1, 0);
    painter.glTexCoord2f(1, 0);
    painter.glVertex3f(1, -1, 0);
    painter.glTexCoord2f(1, 1);
    painter.glVertex3f(1, 1, 0);
    painter.glTexCoord2f(0, 1);
    painter.glVertex3f(-1, 1, 0);
    painter.glEnd();
    painter.glDisable(GLConstants.GL_TEXTURE_2D);
    painter.glDeleteTextures(1, ids, 0);

    assertNoGLError();
    // bottom left texel is white, others are transparent black
    assertPixel(4, 4, 255, 255, 255);
    assertPixel(WIDTH - 4, HEIGHT - 4, 0, 0, 0);
  }

  public void framebufferAndQuery() {
    int[] texture = new int[1];
    painter.glGenTextures(1, texture, 0);
    painter.glBindTexture(GLConstants.GL_TEXTURE_2D, texture[0]);
    painter.glTexImage2D(GLConstants.GL_TEXTURE_2D, 0, GLConstants.GL_RGBA, 16, 16, 0,
        GLConstants.GL_RGBA, GLConstants.GL_UNSIGNED_BYTE, null);

    int[] fbo = new int[1];
    painter.glGenFramebuffers(1, fbo, 0);
    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, fbo[0]);
    painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_COLOR_ATTACHMENT0,
        GLConstants.GL_TEXTURE_2D, texture[0], 0);
    assertEquals(GLConstants.GL_FRAMEBUFFER_COMPLETE,
        painter.glCheckFramebufferStatus(GLConstants.GL_FRAMEBUFFER));
    painter.glDrawBuffers(1, new int[] {GLConstants.GL_COLOR_ATTACHMENT0}, 0);

    // Count samples passing when drawing in the framebuffer
    int[] query = new int[1];
    painter.glGenQueries(1, query, 0);
    painter.glViewport(0, 0, 16, 16);
    painter.glBeginQuery(GLConstants.GL_SAMPLES_PASSED, query[0]);
    painter.glBegin_Quad();
    painter.glVertex3f(-1, -1, 0);
    painter.glVertex3f(1, -1, 0);
    painter.glVertex3f(1, 1, 0);
    painter.glVertex3f(-1, 1, 0);
    painter.glEnd();
    painter.glEndQuery(GLConstants.GL_SAMPLES_PASSED);

    int[] samples = new int[1];
    painter.glGetQueryObjectuiv(query[0], GLConstants.GL_QUERY_RESULT, samples, 0);
    assertEquals(16 * 16, samples[0]);

    painter.glDeleteQueries(1, query, 0);
    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, 0);
    painter.glDeleteFramebuffers(1, fbo, 0);
    painter.glDeleteTextures(1, texture, 0);

    assertNoGLError();
    assertTrue(painter.glGetString(GLConstants.GL_VERSION).length() > 0);
  }

  // ---------------------------------------------------------------------------------------------

  protected void assertNoGLError() {
    assertEquals(GLConstants.GL_NO_ERROR, painter.glGetError());
  }

  protected void assertPixel(int x, int y, int r, int g, int b) {
    ByteBuffer pixel = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder());
    painter.glReadPixels(x, y, 1, 1, GLConstants.GL_RGBA, GLConstants.GL_UNSIGNED_BYTE, pixel);

    String at = "pixel at " + x + "," + y;
    assertEquals(at + " red", r, pixel.get(0) & 0xFF);
    assertEquals(at + " green", g, pixel.get(1) & 0xFF);
    assertEquals(at + " blue", b, pixel.get(2) & 0xFF);
  }
}
