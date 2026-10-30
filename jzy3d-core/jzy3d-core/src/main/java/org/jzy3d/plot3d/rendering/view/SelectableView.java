/*
 * Copyright (c) Since 2010, Martin Pernollet All rights reserved.
 *
 * Redistribution in binary form, with or without modification, is permitted. Edition of source
 * files is allowed. Redistribution of original or modified source files is forbidden.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jzy3d.plot3d.rendering.view;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

/**
 * An {@ InteractiveView} handles 2d projection updates to ensure mouse is always computing
 * intersection with objects in a relevant state.
 *
 * @author Martin Pernollet
 */
public class SelectableView extends View {
  public SelectableView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    super(factory, scene, canvas, quality);
  }

  /**
   * If chart is rendered for the first time, or rendered because it, was resized, we need to update
   * a projection for the CellSelector.
   */
  @Override
  public void render() {
    if (dimensionDirty)
      wasDirty = true;

    super.render(); // need to render before projecting to have coherent viewport in selector

    if (firstRender) {
      project();
      firstRender = false;
    }
    if (wasDirty) {
      wasDirty = false;
      project();
    }
  }

  public boolean hasRenderedOnce() {
    return !firstRender;
  }

  protected boolean firstRender = true;
  protected boolean wasDirty = false;
}
