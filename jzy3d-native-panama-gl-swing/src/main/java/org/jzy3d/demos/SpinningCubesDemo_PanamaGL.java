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
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.FrameSwing;
import org.jzy3d.chart.factories.PanamaGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Geometry;
import org.jzy3d.plot3d.primitives.RandomGeom;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.HiDPI;

/**
 * Demo a cube chart made with PanamaGL.
 *
 * VM ARGS : --enable-native-access=ALL-UNNAMED 
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
public class SpinningCubesDemo_PanamaGL {
  
  
  public static void main(String[] args) throws Exception {
    Quality q = Quality.Intermediate();
    q.setHiDPI(HiDPI.ON);
    q.setAlphaActivated(false); 
    
    RandomGeom r = new RandomGeom();
    List<Composite> drawables = r.spinningCubes(4, 45, 0.08f);
    
    for(Composite c: drawables) {
      c.setReflectLight(true);
      c.setColor(Color.ORANGE);
    }

    //ChartFactory factory = new AWTChartFactory();
    ChartFactory factory = new PanamaGLChartFactory();
    Chart chart = factory.newChart(q);
    chart.add(drawables);
    chart.addMouse();
    chart.addLightOnCamera();
    chart.getView().setAxisDisplayed(false);
    //chart.getView().setViewPositionMode(ViewPositionMode.PROFILE);
    Geometry.SHOW_NORMALS = false;
    
    
    ((FrameSwing)chart.open(800,600)).setSize(800,600);
    
    
  }
}