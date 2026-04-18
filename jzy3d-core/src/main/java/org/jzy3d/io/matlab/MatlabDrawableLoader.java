package org.jzy3d.io.matlab;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.io.ILoader;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.TicToc;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.enlightables.EnlightablePolygon;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLNumericArray;

/**
 * Load a Matlab (TM) .mat file, assuming it contains at least three arrays named "X", "Y" and "Z".
 * 
 * Returns a list of {@link EnlightablePolygon} which allow a an easier visualization of shapes made
 * of a large number of single-colored polygons.
 * 
 * For very large file, {@link MatlabVBOLoader} are preferred.
 * 
 * @author Martin Pernollet
 */
public class MatlabDrawableLoader implements ILoader {
  protected static int C1 = 0;
  protected static int C2 = 1;
  protected static int C3 = 2;

  @Override
  public List<Drawable> load(String filename) throws Exception {
    return load(filename, -1);
  }

  @SuppressWarnings("unchecked")
  public List<Drawable> load(String filename, int limit) throws Exception {
    TicToc t = new TicToc();
    t.tic();
    MatFileReader mfr = new MatFileReader(filename);
    LogManager.getLogger(MatlabDrawableLoader.class).info(t.toc() + " to read " + filename);
    LogManager.getLogger(MatlabDrawableLoader.class)
        .info("Containing arrays: " + mfr.getContent().keySet());

    MLNumericArray<Float> x = (MLNumericArray<Float>) mfr.getMLArray("X");
    MLNumericArray<Float> y = (MLNumericArray<Float>) mfr.getMLArray("Y");
    MLNumericArray<Float> z = (MLNumericArray<Float>) mfr.getMLArray("Z");

    List<Drawable> polygons = new ArrayList<Drawable>();

    int n = x.getN();
    if (limit > 0)
      n = Math.min(n, limit);

    t.tic();
    for (int i = 0; i < n; i++) {
      try {
        Coord3d coord1 = new Coord3d(x.getReal(C1, i), y.getReal(C1, i), z.getReal(C1, i));
        Coord3d coord2 = new Coord3d(x.getReal(C2, i), y.getReal(C2, i), z.getReal(C2, i));
        Coord3d coord3 = new Coord3d(x.getReal(C3, i), y.getReal(C3, i), z.getReal(C3, i));

        Point point1 = new Point(coord1);
        Point point2 = new Point(coord2);
        Point point3 = new Point(coord3);

        EnlightablePolygon polygon = new EnlightablePolygon();
        polygon.add(point1);
        polygon.add(point2);
        polygon.add(point3);

        polygons.add(polygon);
      } catch (Exception e) {
        throw new Exception("failed at line " + i + "/" + n, e);
      }
    }
    LogManager.getLogger(MatlabDrawableLoader.class).info(t.toc() + " to build polygon list");
    return polygons;
  }
}
