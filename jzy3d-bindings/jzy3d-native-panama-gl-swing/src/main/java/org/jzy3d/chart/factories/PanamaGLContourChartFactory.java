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
package org.jzy3d.chart.factories;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.primitives.axis.ContourAxisBox;
import org.jzy3d.plot3d.rendering.view.View;

/**
 * Build charts rendered with PanamaGL in Swing, having a {@link ContourAxisBox} able to display
 * contours on the bottom face of the axis box, like ContourChartFactory does with JOGL.
 */
public class PanamaGLContourChartFactory extends PanamaGLSwingChartFactory {
  public PanamaGLContourChartFactory() {
    super(new PanamaGLSwingPainterFactory());
  }

  public PanamaGLContourChartFactory(IPainterFactory painterFactory) {
    super(painterFactory);
  }

  @Override
  public AxisBox newAxe(BoundingBox3d box, View view) {
    ContourAxisBox axe = new ContourAxisBox(box);
    axe.setView(view);
    return axe;
  }
}
