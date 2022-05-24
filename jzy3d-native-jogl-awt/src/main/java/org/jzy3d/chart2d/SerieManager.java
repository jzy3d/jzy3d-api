package org.jzy3d.chart2d;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.jzy3d.plot2d.primitives.LineSerie2d;
import org.jzy3d.plot2d.primitives.ScatterPointSerie2d;
import org.jzy3d.plot2d.primitives.ScatterSerie2d;
import org.jzy3d.plot2d.primitives.Serie2d;

public class SerieManager {
  static SerieManager instance;

  public static SerieManager get() {
    if (instance == null)
      instance = new SerieManager();
    return instance;
  }

  public Collection<Serie2d> getSeries() {
    return series.values();
  }

  public Serie2d getSerie(String name, Serie2d.Type type) {
    Serie2d serie = null;
    if (!series.keySet().contains(name)) {
      serie = newSerie(name, type, serie);
    } else {
      serie = series.get(name);
    }
    return serie;
  }

  protected Serie2d newSerie(String name, Serie2d.Type type, Serie2d serie) {
    if (Serie2d.Type.LINE.equals(type))
      serie = new LineSerie2d(name);
    else if (Serie2d.Type.SCATTER.equals(type))
      serie = new ScatterSerie2d(name);
    else if (Serie2d.Type.SCATTER_POINTS.equals(type))
      serie = new ScatterPointSerie2d(name);
    else
      throw new IllegalArgumentException("Unsupported serie type " + type);
    return serie;
  }

  protected Map<String, Serie2d> series = new HashMap<String, Serie2d>();

}
