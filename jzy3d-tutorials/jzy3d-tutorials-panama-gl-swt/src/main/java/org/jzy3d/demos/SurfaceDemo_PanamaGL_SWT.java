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
package org.jzy3d.demos;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.PanamaGLSWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Func3D;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * A surface chart rendered with PanamaGL and embedded in an SWT Shell.
 *
 * VM ARGS: --enable-native-access=ALL-UNNAMED -XstartOnFirstThread (macOS only)
 *
 * -Djava.library.path=.:/System/Library/Frameworks/OpenGL.framework/Versions/Current/Libraries/
 *
 * or
 * -Djava.library.path=.:/usr/lib/x86_64-linux-gnu/
 *
 * or
 * -Djava.library.path="C:\Windows\system32;C:\Users\Martin\Downloads\freeglut-MSVC-3.0.0-2.mp\freeglut\bin\x64"
 *
 * @author Martin Pernollet
 */
public class SurfaceDemo_PanamaGL_SWT {
  static final float ALPHA_FACTOR = 0.75f;

  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());

    Quality q = Quality.Advanced().setAnimated(false);
    Chart chart = new PanamaGLSWTChartFactory(shell).newChart(q);
    chart.add(surface());

    AxisLayout layout = chart.getAxisLayout();
    layout.setXTickColor(Color.RED);
    layout.setYTickColor(Color.GREEN);
    layout.setZTickColor(Color.BLUE);

    chart.addMouse();

    shell.setText("Jzy3d - PanamaGL - SWT - Surface");
    shell.setSize(800, 600);
    shell.open();

    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    chart.stopAnimation();
    display.dispose();
  }

  private static Shape surface() {
    SurfaceBuilder builder = new SurfaceBuilder();

    Func3D func = new Func3D((x, y) -> x * Math.sin(x * y));
    Range range = new Range(-3, 3);
    int steps = 50;

    Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps), func);

    ColorMapper colorMapper =
        new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, ALPHA_FACTOR));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    surface.setWireframeWidth(1);
    return surface;
  }
}
