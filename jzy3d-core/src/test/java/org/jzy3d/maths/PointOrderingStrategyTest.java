package org.jzy3d.maths;

import java.util.ArrayList;
import java.util.Random;
import org.junit.Test;
import org.jzy3d.plot3d.rendering.ordering.PointOrderingStrategy;
import org.jzy3d.plot3d.rendering.view.Camera;

public class PointOrderingStrategyTest {
  @Test
  public void pointOrdering() throws Exception {
    ArrayList<Coord3d> points = new ArrayList<Coord3d>();
    Random random = new java.util.Random();
    for (int i = 0; i < 10000; i++) {
      points.add(new Coord3d(random.nextInt() % 10, random.nextInt() % 10, random.nextInt() % 10));
    }
    new PointOrderingStrategy().sort(points, new Camera(new Coord3d(0, 0, 0))); // <-- Here occurs
                                                                                // an exception in
                                                                                // TimSort@JDK7 when
                                                                                // illegal sorting
                                                                                // algorithm is used
  }
}
