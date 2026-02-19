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

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.FrameSwing;
import org.jzy3d.chart.factories.PanamaGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.PanamaGLPainter;
import org.jzy3d.plot3d.builder.Func3D;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.HiDPIProportionalFontSizePolicy;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import panamagl.utils.GraphicsUtils;

/**
 * Demo an surface chart made with PanamaGL.
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
// DO NOT USE -XstartOnFirstThread!!
// Making context current in MacOSXCGLContext line 1474 
public class SurfaceDemo_PanamaGL {
  static final float ALPHA_FACTOR = 0.75f;// .61f;

  public static void main(String[] args) throws InterruptedException {
//Thread.sleep(5000);
    ChartFactory factory = new PanamaGLChartFactory();

    Quality q = Quality.Advanced().setAnimated(false);
    Chart chart = factory.newChart(q);
    chart.add(surface());

    AxisLayout layout = chart.getAxisLayout();
    //layout.setFont(new Font("Apple Chancery", 20));
    layout.setFont(new Font("Courrier", 16));
    layout.setFontSizePolicy(new HiDPIProportionalFontSizePolicy(chart.getView()));

    /*layout.setXAxisLabel("My X axis label is a little long to draw");
    layout.setYAxisLabel("My Y axis label is a little long to draw");
    layout.setZAxisLabel("My Z axis label is a little long to draw");*/

    /*layout.setZAxisSide(ZAxisSide.LEFT);
    layout.setZAxisLabelOrientation(LabelOrientation.VERTICAL);
    layout.setYAxisLabelOrientation(LabelOrientation.PARALLEL_TO_AXIS);
    layout.setXAxisLabelOrientation(LabelOrientation.PARALLEL_TO_AXIS);*/
    
    //layout.setAxisLabelOffsetAuto(true);
    //layout.setAxisLabelOffsetMargin(20);
    
    layout.setXTickColor(Color.RED);
    layout.setYTickColor(Color.GREEN);
    layout.setZTickColor(Color.BLUE);
    
    System.out.println("Before open");
    FrameSwing frame = (FrameSwing)chart.open(800,600);
    System.out.println("After open");
    frame.setSize(800, 600);
    System.out.println("pixel ratio: " + GraphicsUtils.getPixelScaleX(frame));

    chart.addMouse();    
    
    PanamaGLPainter painter = (PanamaGLPainter)chart.getPainter();
    
    
    System.out.println(painter.getContext().getProfile());
    
  }


  private static Shape surface() {
    SurfaceBuilder builder = new SurfaceBuilder();

    Func3D func = new Func3D((x, y) -> x * Math.sin(x * y));
    Range range = new Range(-3, 3);
    int steps = 50;

    Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps), func);

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, ALPHA_FACTOR));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    surface.setWireframeWidth(1);
    return surface;
  }

}
