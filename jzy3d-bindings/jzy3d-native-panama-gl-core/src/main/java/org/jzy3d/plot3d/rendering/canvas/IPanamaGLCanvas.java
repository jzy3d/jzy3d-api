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
package org.jzy3d.plot3d.rendering.canvas;

import panamagl.GLEventListener;
import panamagl.canvas.GLCanvas;

/**
 * Common contract of every Jzy3D canvas backed by a PanamaGL {@link GLCanvas},
 * regardless of the hosting windowing toolkit (Swing, JavaFX, SWT, ...).
 *
 * This interface lets toolkit-agnostic code (e.g. {@link org.jzy3d.painters.PanamaGLPainter})
 * retrieve the underlying PanamaGL canvas without casting to a toolkit-specific class.
 */
public interface IPanamaGLCanvas extends IScreenCanvas {
  GLCanvas getGLCanvas();

  GLEventListener getGLEventListener();

  void setGLEventListener(GLEventListener listener);
}
