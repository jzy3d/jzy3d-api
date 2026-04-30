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
import org.jzy3d.chart.factories.PanamaGLJavaFXPainterFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Geometry;
import org.jzy3d.plot3d.primitives.RandomGeom;
import org.jzy3d.plot3d.rendering.canvas.PanamaGLJavaFXCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.HiDPI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Spinning cubes rendered with PanamaGL and embedded in a JavaFX Stage.
 *
 * VM ARGS: --enable-native-access=ALL-UNNAMED
 *
 * @author Martin Pernollet
 */
//--module-path /Library/Java/JavaVirtualMachines/javafx-sdk-19.0.2.1/lib --add-modules javafx.controls --add-exports=java.desktop/sun.awt=ALL-UNNAMED
//--module-path /Users/martin/Dev/javafx-sdk-17/lib --add-modules javafx.controls --add-exports=java.desktop/sun.awt=ALL-UNNAMED
//--module-path "C:\Program Files\Java\javafx-sdk-17.0.6\lib"  --add-modules javafx.controls --add-exports=java.desktop/sun.awt=ALL-UNNAMED
public class SpinningCubesDemo_PanamaGL_JavaFX {

  public static void main(String[] args) {
    Application.launch(App.class, args);
  }

  public static class App extends Application {
    @Override
    public void start(Stage stage) {
      Quality q = Quality.Intermediate();
      q.setHiDPI(HiDPI.ON);
      q.setAlphaActivated(false);

      RandomGeom r = new RandomGeom();
      List<Composite> drawables = r.spinningCubes(4, 45, 0.08f);

      for (Composite c : drawables) {
        c.setReflectLight(true);
        c.setColor(Color.ORANGE);
      }

      
      ChartFactory factory = new ChartFactory(new PanamaGLJavaFXPainterFactory());
      
      Chart chart = factory.newChart(q);
      chart.add(drawables);
      chart.addMouse();
      chart.addLightOnCamera();
      chart.getView().setAxisDisplayed(false);
      Geometry.SHOW_NORMALS = false;

      PanamaGLJavaFXCanvas canvas = (PanamaGLJavaFXCanvas) chart.getCanvas();
      Scene scene = new Scene(canvas, 800, 600);

      stage.setTitle("Jzy3d - PanamaGL - JavaFX - Spinning Cubes");
      stage.setScene(scene);
      stage.show();
    }
  }
}
