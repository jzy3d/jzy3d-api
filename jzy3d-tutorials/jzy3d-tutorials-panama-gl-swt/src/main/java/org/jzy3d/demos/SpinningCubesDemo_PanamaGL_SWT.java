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

import java.util.List;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.PanamaGLSWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Geometry;
import org.jzy3d.plot3d.primitives.RandomGeom;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.HiDPI;

/**
 * Spinning cubes rendered with PanamaGL and embedded in an SWT Shell.
 *
 * VM ARGS: --enable-native-access=ALL-UNNAMED -XstartOnFirstThread (macOS only)
 *
 * @author Martin Pernollet
 */
public class SpinningCubesDemo_PanamaGL_SWT {

  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());

    Quality q = Quality.Intermediate();
    q.setHiDPI(HiDPI.ON);
    q.setAlphaActivated(false);

    RandomGeom r = new RandomGeom();
    List<Composite> drawables = r.spinningCubes(4, 45, 0.08f);

    for (Composite c : drawables) {
      c.setReflectLight(true);
      c.setColor(Color.ORANGE);
    }

    Chart chart = new PanamaGLSWTChartFactory(shell).newChart(q);
    chart.add(drawables);
    chart.addLightOnCamera();
    chart.getView().setAxisDisplayed(false);
    Geometry.SHOW_NORMALS = false;

    shell.setText("Jzy3d - PanamaGL - SWT - Spinning Cubes");
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
}
