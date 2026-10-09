/*******************************************************************************
 * Copyright (c) 2022, 2023 Martin Pernollet & contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA
 *******************************************************************************/
package org.jzy3d.painters;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import panamagl.opengl.GL;

/**
 * Wrap a {@link GL} so that an exception is thrown as soon as a GL call produces an error, as JOGL's
 * DebugGL does. Optionally print each call.
 * 
 * Errors are checked after each call, except between glBegin and glEnd where glGetError is not
 * allowed. This makes rendering slower : use it for debugging only.
 */
public class PanamaGLDebug implements InvocationHandler {
  protected GL gl;
  protected boolean trace;
  protected boolean inBeginEnd = false;

  /** Return a {@link GL} checking errors after each call to the given GL. */
  public static GL debug(GL gl) {
    return wrap(gl, false);
  }

  /** Return a {@link GL} printing each call and checking errors after each call. */
  public static GL trace(GL gl) {
    return wrap(gl, true);
  }

  protected static GL wrap(GL gl, boolean trace) {
    return (GL) Proxy.newProxyInstance(GL.class.getClassLoader(), new Class<?>[] {GL.class},
        new PanamaGLDebug(gl, trace));
  }

  protected PanamaGLDebug(GL gl, boolean trace) {
    this.gl = gl;
    this.trace = trace;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    String name = method.getName();

    if (trace) {
      System.out.println(name + (args == null ? "()" : Arrays.toString(args)));
    }

    Object out;
    try {
      out = method.invoke(gl, args);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    }

    if ("glBegin".equals(name)) {
      inBeginEnd = true;
    } else if ("glEnd".equals(name)) {
      inBeginEnd = false;
    }

    if (!inBeginEnd && name.startsWith("gl") && !"glGetError".equals(name)) {
      int error = gl.glGetError();

      if (error != GL.GL_NO_ERROR) {
        throw new GLErrorException(error, name, args);
      }
    }
    return out;
  }

  /** An OpenGL error raised by a call. */
  public static class GLErrorException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    protected int error;

    public GLErrorException(int error, String method, Object[] args) {
      super("OpenGL error 0x" + Integer.toHexString(error) + " (" + name(error) + ") after "
          + method + (args == null ? "()" : Arrays.toString(args)));
      this.error = error;
    }

    public int getError() {
      return error;
    }

    public static String name(int error) {
      switch (error) {
        case GL.GL_INVALID_ENUM:
          return "invalid enum";
        case GL.GL_INVALID_VALUE:
          return "invalid value";
        case GL.GL_INVALID_OPERATION:
          return "invalid operation";
        case GL.GL_STACK_OVERFLOW:
          return "stack overflow";
        case GL.GL_STACK_UNDERFLOW:
          return "stack underflow";
        case GL.GL_OUT_OF_MEMORY:
          return "out of memory";
        default:
          return "unknown";
      }
    }
  }
}
