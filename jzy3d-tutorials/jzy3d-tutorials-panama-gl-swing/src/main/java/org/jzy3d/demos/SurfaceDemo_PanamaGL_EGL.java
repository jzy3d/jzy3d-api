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
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Func3D;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * Demo a surface chart rendered by PanamaGL through a headless EGL context on Linux, which lets
 * OpenGL run over Vulkan with Mesa Zink.
 *
 * <p>The demo sets the system property <code>panamagl.linux.context=egl</code> before the PanamaGL
 * factory is created. Run it in a window, or offscreen without any X display :
 *
 * <pre>
 * # Window, OpenGL over Vulkan
 * GALLIUM_DRIVER=zink java ... org.jzy3d.demos.SurfaceDemo_PanamaGL_EGL
 *
 * # Offscreen, no X display needed, image saved to a file
 * GALLIUM_DRIVER=zink java -Djava.awt.headless=true ... org.jzy3d.demos.SurfaceDemo_PanamaGL_EGL --offscreen target/surface.png
 *
 * # Computer without GPU : let Zink use the lavapipe CPU Vulkan driver (package mesa-vulkan-drivers)
 * LIBGL_ALWAYS_SOFTWARE=1 GALLIUM_DRIVER=zink ...
 * </pre>
 *
 * VM ARGS : --enable-native-access=ALL-UNNAMED -Djava.library.path=.:/usr/lib/x86_64-linux-gnu/
 *
 * <p>Requires a PanamaGL version providing the EGL context of Linux : older versions ignore the
 * property and use GLX.
 */
public class SurfaceDemo_PanamaGL_EGL {
  /** The PanamaGL property selecting the kind of OpenGL context on Linux : glx or egl. */
  public static final String CONTEXT_PROPERTY = "panamagl.linux.context";

  public static void main(String[] args) throws Exception {
    // Must be set before the PanamaGL factory is created by the chart factory
    if (System.getProperty(CONTEXT_PROPERTY) == null)
      System.setProperty(CONTEXT_PROPERTY, "egl");

    String offscreenFile = args.length > 1 && "--offscreen".equals(args[0]) ? args[1] : null;

    PanamaGLSwingChartFactory factory = new PanamaGLSwingChartFactory();
    if (offscreenFile != null)
      factory.getPainterFactory().setOffscreen(800, 600);

    AWTChart chart = factory.newChart(Quality.Advanced());
    chart.add(surface());

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
      chart.open("Jzy3d - PanamaGL - EGL", 800, 600);
      chart.addMouse();

      // The OpenGL context is created once the window shows the canvas
      for (int i = 0; i < 100 && chart.getCanvas().getDebugInfo() == null; i++)
        Thread.sleep(50);
      System.out.println(chart.getCanvas().getDebugInfo());
    }
  }

  static Shape surface() {
    Func3D func = new Func3D((x, y) -> x * Math.sin(x * y));
    Shape surface = new SurfaceBuilder().orthonormal(new OrthonormalGrid(new Range(-3, 3), 50), func);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, .75f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }
}
