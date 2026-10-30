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

import org.eclipse.swt.widgets.Composite;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * PanamaGL chart factory for SWT.
 *
 * Holds the parent {@link Composite} required to instantiate the SWT widget
 * hierarchy. The painter factory retrieves it via {@link ISWTChartFactory} when
 * building the canvas.
 */
public class PanamaGLSWTChartFactory extends PanamaGLChartFactory implements ISWTChartFactory {

  protected Composite parent;

  public PanamaGLSWTChartFactory(Composite parent) {
    super(new PanamaGLSWTPainterFactory());
    this.parent = parent;
  }

  public PanamaGLSWTChartFactory(Composite parent, IPainterFactory painterFactory) {
    super(painterFactory);
    this.parent = parent;
  }

  public static Chart chart(Composite parent) {
    return new PanamaGLSWTChartFactory(parent).newChart(Quality.Intermediate());
  }

  public static Chart chart(Composite parent, Quality quality) {
    return new PanamaGLSWTChartFactory(parent).newChart(quality);
  }

  @Override
  public Composite getComposite() {
    return parent;
  }
}
