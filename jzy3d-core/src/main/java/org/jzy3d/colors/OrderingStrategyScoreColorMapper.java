package org.jzy3d.colors;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Statistics;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Graph;

/**
 * A {@link ColorMapper} able to compute a <i>coordinate</i> color according to its score computed
 * by the current {@link Graph}'s {@link AbstractOrderingStrategy}.
 * 
 * Method {@link preDraw} is overriden to compute each {@link AbstractDrawables} score with the
 * ordering strategy, so that we have a range.
 * 
 * As colormapper may be shared by several components of a single {@link Composite}, on must provide
 * an update policy to state which objects are allowed to call the re-initilizer {@link preDraw}
 * method.
 * 
 * @author Martin Pernollet
 *
 */
public class OrderingStrategyScoreColorMapper extends ColorMapper {
  static Logger logger = LogManager.getLogger(OrderingStrategyScoreColorMapper.class);

  public OrderingStrategyScoreColorMapper(IColorMap colormap, IColorMapperUpdatePolicy policy,
      Graph sceneGraph, Color factor) {
    super(colormap, factor);
    this.sceneGraph = sceneGraph;
    this.policy = policy;
  }

  @Override
  public void preDraw(Object o) {
    super.preDraw(o);
    if (policy.acceptsPreDraw(o))
      doPreDraw();
  }

  private void doPreDraw() {
    AbstractOrderingStrategy s = sceneGraph.getStrategy();
    List<Drawable> drawable = sceneGraph.getDecomposition();

    double[] scores = new double[getNumCoordinates(drawable, false)];
    int k = 0;
    for (Drawable d : drawable) {
      scores[k++] = s.score(d);
      if (d instanceof Polygon) {
        Polygon p = (Polygon) d;
        for (Point pt : p.getPoints()) {
          scores[k++] = s.score(pt.getCoord());
        }
      }
    }

    min = Statistics.min(scores);
    max = Statistics.max(scores);

    if (min == max) {
      logger.warn("min = max = " + min);
    }
  }

  protected int getNumCoordinates(List<Drawable> drawables, boolean onlyBaryCenter) {
    if (onlyBaryCenter)
      return drawables.size();
    else {
      int n = drawables.size();

      for (Drawable d : drawables) {
        if (d instanceof Polygon) {
          Polygon p = (Polygon) d;
          n += p.size();
        }
      }
      return n;
    }

  }

  @Override
  public Color getColor(Coord3d coord) {
    AbstractOrderingStrategy s = sceneGraph.getStrategy();
    float score = (float) s.score(coord);

    Color out = colormap.getColor(this, 0, 0, score);
    if (factor != null)
      out.mul(factor);
    return out;
  }

  protected float off;
  protected Graph sceneGraph;
  protected IColorMapperUpdatePolicy policy;
}
