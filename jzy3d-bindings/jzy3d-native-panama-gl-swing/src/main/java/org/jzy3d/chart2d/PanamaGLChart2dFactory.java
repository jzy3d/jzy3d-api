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
package org.jzy3d.chart2d;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.chart.factories.PanamaGLSwingChartFactory;
import org.jzy3d.chart.factories.PanamaGLSwingPainterFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;

/** Build {@link Chart2d} rendered with PanamaGL in Swing, like Chart2dFactory does with JOGL. */
public class PanamaGLChart2dFactory extends PanamaGLSwingChartFactory {
  public PanamaGLChart2dFactory() {
    super(new PanamaGLSwingPainterFactory());
  }

  public PanamaGLChart2dFactory(IPainterFactory painterFactory) {
    super(painterFactory);
  }

  @Override
  public IChartFactory getFactory() {
    return this;
  }

  @Override
  public Chart2d newChart(IChartFactory factory, Quality quality) {
    return new Chart2d(factory, quality);
  }

  @Override
  public Chart2d newChart(Quality quality) {
    return new Chart2d(getFactory(), quality);
  }

  @Override
  public Chart2d newChart() {
    return newChart(Quality.Advanced());
  }

  @Override
  public AxisBox2d newAxe(BoundingBox3d box, View view) {
    return new AxisBox2d(box);
  }

  @Override
  public View2d newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    return new View2d(factory, scene, canvas, quality);
  }

  /* */

  public static Chart2d chart() {
    return chart(Quality.Intermediate());
  }

  public static Chart2d chart(Quality quality) {
    return new PanamaGLChart2dFactory().newChart(quality);
  }

  public static Chart2d chart(String toolkit) {
    return chart(Chart.DEFAULT_QUALITY);
  }
}
