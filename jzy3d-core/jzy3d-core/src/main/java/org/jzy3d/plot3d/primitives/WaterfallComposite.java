package org.jzy3d.plot3d.primitives;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.plot3d.builder.concrete.WaterfallTessellator;

/**
 * A drawable Waterfall using <a href="">Matlab style</a>
 * 
 * @see WaterfallTessellator
 * 
 * @author Jacob Filik
 */
public class WaterfallComposite extends Shape {

  public void add(ColoredWireframePolygon outline, Shape fill) {

    List<Drawable> drawables = fill.getDrawables();
    for (Drawable dr : drawables) {
      if (dr instanceof Polygon) {
        Polygon p = (Polygon) dr;
        p.setPolygonOffsetFactor(1);
        p.setPolygonOffsetUnit(1);
        p.setPolygonOffsetFillEnable(true);
        p.setWireframeDisplayed(false);
        p.setColor(Color.WHITE);
      }
    }

    outline.setPolygonOffsetFillEnable(true);
    outline.setPolygonOffsetFactor(-1);
    outline.setPolygonOffsetUnit(-1);
    outline.setWireframeColor(Color.BLACK);
    outline.setFaceDisplayed(false);

    add(fill);
    add(outline);
  }

  @Override
  public void setWireframeWidth(float width) {
    if (getDrawables() == null)
      return;
    for (Drawable d : getDrawables()) {
      if (d instanceof ColoredWireframePolygon) {
        ((ColoredWireframePolygon) d).setWireframeWidth(width);
      }
    }
  }

  @Override
  public void setColorMapper(ColorMapper mapper) {
    for (Drawable d : getDrawables()) {
      if (d instanceof ColoredWireframePolygon) {
        ((ColoredWireframePolygon) d).setColorMapper(mapper);
      }
    }
  }

  @Override
  public void setColor(Color color) {
    for (Drawable d : getDrawables()) {
      if (d instanceof ColoredWireframePolygon) {
        ((ColoredWireframePolygon) d).setWireframeColor(color);
      }
    }
  }

}
