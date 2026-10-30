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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.painters.PanamaGLDebug.GLErrorException;
import panamagl.opengl.GL;

public class TestPanamaGLDebug {

  @Test
  public void whenCallRaisesError_ThenThrow() {
    GL gl = mock(GL.class);
    when(gl.glGetError()).thenReturn(GL.GL_INVALID_ENUM);

    GL debug = PanamaGLDebug.debug(gl);

    try {
      debug.glEnable(0x1234);
      Assert.fail("expect an exception");
    } catch (GLErrorException e) {
      Assert.assertEquals(GL.GL_INVALID_ENUM, e.getError());
      Assert.assertTrue(e.getMessage(), e.getMessage().contains("glEnable"));
      Assert.assertTrue(e.getMessage(), e.getMessage().contains("invalid enum"));
    }
    verify(gl).glEnable(0x1234);
  }

  @Test
  public void whenNoError_ThenReturnResult() {
    GL gl = mock(GL.class);
    when(gl.glGetError()).thenReturn(GL.GL_NO_ERROR);
    when(gl.glGenLists(1)).thenReturn(7);

    Assert.assertEquals(7, PanamaGLDebug.debug(gl).glGenLists(1));
  }

  @Test
  public void errorsAreNotCheckedBetweenBeginAndEnd() {
    GL gl = mock(GL.class);
    when(gl.glGetError()).thenReturn(GL.GL_NO_ERROR);
    GL debug = PanamaGLDebug.debug(gl);

    debug.glBegin(GL.GL_POINTS);
    debug.glVertex3f(0, 0, 0);

    // glGetError is not allowed between glBegin and glEnd
    verify(gl, never()).glGetError();

    debug.glEnd();
    verify(gl).glGetError();
  }

  @Test
  public void factoryCreatesDebugPainters() {
    org.jzy3d.chart.factories.APanamaGLPainterFactory factory =
        mock(org.jzy3d.chart.factories.APanamaGLPainterFactory.class,
            org.mockito.Mockito.CALLS_REAL_METHODS);

    Assert.assertFalse(factory.isDebugGL());
    factory.setDebugGL(true);
    Assert.assertTrue(factory.isDebugGL());
  }
}
