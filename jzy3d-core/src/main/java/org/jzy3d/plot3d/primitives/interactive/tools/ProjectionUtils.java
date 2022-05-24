package org.jzy3d.plot3d.primitives.interactive.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.TicToc;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.rendering.scene.Decomposition;
import org.jzy3d.plot3d.rendering.scene.Graph;



public class ProjectionUtils {
  static Logger logger = LogManager.getLogger(ProjectionUtils.class);

  public static List<PolygonProjection> project(Chart chart) {
    return project(chart.getView().getPainter(), chart.getScene().getGraph());
  }

  public static List<PolygonProjection> project(IPainter painter, Graph g) {
    return project(painter, g.getAll());
  }

  public static List<PolygonProjection> project(IPainter painter, List<Drawable> list) {
    return project(painter, Decomposition.getDecomposition(list));
  }

  public static List<PolygonProjection> project(IPainter painter, Composite c) {
    List<Drawable> monotypes = Decomposition.getDecomposition(c);
    return project(painter, monotypes);
  }

  public static List<PolygonProjection> project(IPainter painter, ArrayList<Drawable> monotypes) {
    final TicToc t = new TicToc();
    String report = "";

    // prepare a more efficient datastructure
    ArrayList<ArrayList<Coord3d>> polygons = new ArrayList<ArrayList<Coord3d>>(monotypes.size());
    ArrayList<ArrayList<Color>> colors = new ArrayList<ArrayList<Color>>(monotypes.size());
    for (Drawable d : monotypes) {
      if (d instanceof Polygon) {
        polygons.add(ProjectionUtils.getCoordinatesAsArrayList((Polygon) d));
        colors.add(ProjectionUtils.getColorsAsArrayList((Polygon) d));
      } else
        logger.warn("Only polygons are supported. Ignoring :" + d.getClass());
    }

    // project
    t.tic();
    ArrayList<ArrayList<Coord3d>> projections =
        painter.getCamera().modelToScreen(painter, polygons);
    t.toc();
    report += " Projections :" + t.elapsedMilisecond();

    // gather polygon and its colors in a data structure
    int k = 0;
    List<PolygonProjection> polygonProjections = new ArrayList<>();
    for (ArrayList<Coord3d> p : projections) {
      polygonProjections.add(new PolygonProjection(p, colors.get(k++)));
    }

    // sort according to z
    t.tic();
    Collections.sort(polygonProjections, new ProjectionComparator());
    t.tocShow(report + " Sort : ");
    return polygonProjections;
  }

  /************************************************/

  public static ArrayList<Coord3d> getCoordinatesAsArrayList(Polygon p) {
    ArrayList<Coord3d> coords = new ArrayList<Coord3d>(p.size());
    for (int i = 0; i < p.size(); i++)
      coords.add(p.get(i).xyz);
    return coords;
  }

  public static List<Coord3d> getCoordinates(Polygon p) {
    List<Coord3d> coords = new Vector<Coord3d>(p.size());
    for (int i = 0; i < p.size(); i++)
      coords.add(p.get(i).xyz);
    return coords;
  }

  public static ArrayList<Color> getColorsAsArrayList(Polygon p) {
    ArrayList<Color> colors = new ArrayList<Color>(4);
    for (int i = 0; i < p.size(); i++) {
      if (p.getColorMapper() != null)
        colors.add(p.getColorMapper().getColor(p.get(i).xyz)); // TODO: cache, maybe in polygon
                                                               // itself
      else
        colors.add(p.get(i).rgb);
    }
    return colors;
  }

  public static List<Color> getColors(Polygon p) {
    List<Color> colors = new Vector<Color>(4);
    for (int i = 0; i < p.size(); i++) {
      if (p.getColorMapper() != null)
        colors.add(p.getColorMapper().getColor(p.get(i).xyz)); // TODO: cache, maybe in polygon
                                                               // itself
      else
        colors.add(p.get(i).rgb);
    }
    return colors;
  }
}
