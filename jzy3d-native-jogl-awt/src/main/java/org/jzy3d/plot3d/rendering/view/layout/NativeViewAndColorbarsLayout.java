package org.jzy3d.plot3d.rendering.view.layout;

import java.util.List;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.ViewportMode;

/**
 * This implementation allows to shrink colorbar, assuming it is an AWT implementation from which we
 * can read image width
 */
public class NativeViewAndColorbarsLayout extends ViewAndColorbarsLayout {
  protected void renderLegends(IPainter painter, float from, float to, List<ILegend> legends,
      ICanvas canvas) {
    int width = canvas.getRendererWidth();
    int height = canvas.getRendererHeight();
    float slice = (to - from) / legends.size();
    int k = 0;
    for (ILegend legend : legends) {

      legend.setFont(painter.getView().getAxis().getLayout().getFont());
      legend.setViewportMode(ViewportMode.STRETCH_TO_FILL);


      if (legend instanceof AWTColorbarLegend) {
        AWTColorbarLegend awtLegend = (AWTColorbarLegend) legend;

        // optimize to shrink colorbar to required width
        if (shrinkColorbar) {
          int optimalColorbarWidth = awtLegend.getImageGenerator().getPreferedWidth(painter)
              + 1000;
          
          from = 1 - ((1f * optimalColorbarWidth) / (1f * width));
        }

        legend.setViewPort(width, height, from, to);

      } else {
        legend.setViewPort(canvas.getRendererWidth(), canvas.getRendererHeight(),
            from + slice * (k++), to + slice * k);
        legend.render(painter);
      }
    }
  }



}
