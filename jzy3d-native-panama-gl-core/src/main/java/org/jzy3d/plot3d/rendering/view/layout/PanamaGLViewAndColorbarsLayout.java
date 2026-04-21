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
package org.jzy3d.plot3d.rendering.view.layout;

import java.util.List;
import org.jzy3d.chart.Chart;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportBuilder;

public class PanamaGLViewAndColorbarsLayout extends ViewAndColorbarsLayout {
  boolean fixHiDPI = true;

  @Override
  public void render(IPainter painter, Chart chart) {
    View view = chart.getView();
    view.renderBackground(backgroundViewport);

    // Here we force the scene to be rendered on the entire screen to avoid a GRAY
    // (=CLEAR COLOR?) BAND that can't be overriden by legend image
    sceneViewport = ViewportBuilder.column(chart.getCanvas(), 0, 1);

    view.renderScene(sceneViewport);

    // fix overlay on top of chart
    view.renderOverlay(view.getCamera().getLastViewPort());
  }

  /**
   * Disabled legend rendering for now
   */
  @Override
  protected void renderLegends(IPainter painter, float left, float right, List<ILegend> legends,
      ICanvas canvas) {
  }

  public boolean isFixHiDPI() {
    return fixHiDPI;
  }
  public void setFixHiDPI(boolean fixHiDPI) {
    this.fixHiDPI = fixHiDPI;
  }
}
