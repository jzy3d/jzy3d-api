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

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.factories.PanamaGLContourChartFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.axis.ContourAxisBox;

public class TestPanamaGLChart2dFactory {
  static final BoundingBox3d BOX = new BoundingBox3d(0, 10, -1, 1, -1, 1);

  @Test
  public void whenChart2dFactory_ThenBuildAxisBox2d() {
    PanamaGLChart2dFactory factory = new PanamaGLChart2dFactory();

    Assert.assertTrue(factory.newAxe(BOX, null) instanceof AxisBox2d);
    Assert.assertSame(factory, factory.getFactory());
  }

  @Test
  public void whenContourChartFactory_ThenBuildContourAxisBox() {
    PanamaGLContourChartFactory factory = new PanamaGLContourChartFactory();

    Assert.assertTrue(factory.newAxe(BOX, null) instanceof ContourAxisBox);
  }
}
