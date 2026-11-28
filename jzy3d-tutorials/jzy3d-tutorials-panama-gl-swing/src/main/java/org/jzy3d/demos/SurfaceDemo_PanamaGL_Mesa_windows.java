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
package org.jzy3d.demos;

import java.io.File;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.factories.PanamaGLSwingChartFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * Demo a surface chart rendered by PanamaGL with Mesa on Windows, embedded by the
 * <code>panama-gl-bindings-windows-mesa</code> jar (a runtime dependency of this module on Windows).
 * It renders with OpenGL 4.x on any Windows computer, including those without GPU driver, whose
 * own OpenGL is limited to version 1.1.
 *
 * <p>The first argument selects the OpenGL implementation, set as the system property
 * <code>panamagl.windows.opengl</code> before any OpenGL call :
 *
 * <pre>
 * java ... org.jzy3d.demos.SurfaceDemo_PanamaGL_Mesa_windows mesa-d3d12           # Direct3D 12 : GPU, or WARP (default)
 * java ... org.jzy3d.demos.SurfaceDemo_PanamaGL_Mesa_windows mesa-llvmpipe        # the CPU
 * java ... org.jzy3d.demos.SurfaceDemo_PanamaGL_Mesa_windows mesa-zink            # Vulkan driver of the GPU
 * java ... org.jzy3d.demos.SurfaceDemo_PanamaGL_Mesa_windows mesa-zink-lavapipe   # Vulkan over the CPU
 * java ... org.jzy3d.demos.SurfaceDemo_PanamaGL_Mesa_windows system               # opengl32.dll of Windows
 * java ... org.jzy3d.demos.SurfaceDemo_PanamaGL_Mesa_windows mesa-llvmpipe --offscreen target/surface.png
 * </pre>
 *
 * VM ARGS : --enable-native-access=ALL-UNNAMED -Djava.library.path=C:\Windows\system32
 *
 * <p>Requires a PanamaGL version providing Mesa on Windows : older versions ignore the property and
 * use the opengl32.dll of Windows.
 */
public class SurfaceDemo_PanamaGL_Mesa_windows {
  /** The PanamaGL property selecting the OpenGL implementation on Windows. */
  public static final String OPENGL_PROPERTY = "panamagl.windows.opengl";

  public static void main(String[] args) throws Exception {
    String opengl = args.length > 0 && !args[0].startsWith("--") ? args[0] : "mesa-d3d12";
    int offscreenArg = java.util.Arrays.asList(args).indexOf("--offscreen");
    String offscreenFile = offscreenArg >= 0 && offscreenArg + 1 < args.length ? args[offscreenArg + 1] : null;

    // Must be set before the first OpenGL call : the opengl32.dll of a process cannot be replaced
    if (System.getProperty(OPENGL_PROPERTY) == null)
      System.setProperty(OPENGL_PROPERTY, opengl);

    PanamaGLSwingChartFactory factory = new PanamaGLSwingChartFactory();
    if (offscreenFile != null)
      factory.getPainterFactory().setOffscreen(800, 600);

    AWTChart chart = factory.newChart(Quality.Advanced());
    chart.add(SurfaceDemo_PanamaGL_EGL.surface());

    if (offscreenFile != null) {
      System.out.println(chart.getCanvas().getDebugInfo());
      File file = new File(offscreenFile);
      if (file.getParentFile() != null)
        file.getParentFile().mkdirs();
      chart.screenshot(file);
      System.out.println("Image : " + file.getAbsolutePath());
      chart.dispose();
      System.exit(0);
    } else {
      chart.open("Jzy3d - PanamaGL - " + System.getProperty(OPENGL_PROPERTY), 800, 600);
      chart.addMouse();
      for (int i = 0; i < 100 && chart.getCanvas().getDebugInfo() == null; i++)
        Thread.sleep(50);
      System.out.println(chart.getCanvas().getDebugInfo());
    }
  }
}
