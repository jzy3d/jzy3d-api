package org.jzy3d.chart.fallback;

import java.awt.Dimension;
import java.awt.Image;
import org.jzy3d.ui.views.ImagePanel;

public class FallbackChartImagePanel extends ImagePanel {
  private static final long serialVersionUID = -5528945465657978099L;

  @Override
  public void setImage(Image img) {
    this.img = img;
    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
    setPreferredSize(size);
    setSize(size);
    // do not specify min/max size to let miglayout resize component.
  }
}
