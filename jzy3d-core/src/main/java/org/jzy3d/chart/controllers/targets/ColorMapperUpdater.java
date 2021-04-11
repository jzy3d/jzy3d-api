package org.jzy3d.chart.controllers.targets;

import java.util.List;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.scene.Graph;


public class ColorMapperUpdater {
  public static void update(Graph g, Scale scale) {
    List<Drawable> dlist = g.getAll();

    for (Drawable d : dlist) {
      if (d instanceof IMultiColorable) {
        IMultiColorable mc = (IMultiColorable) d;
        mc.getColorMapper().setScale(scale);
      }
    }
  }
}
