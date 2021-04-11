package org.jzy3d.maths.graphs;

import java.util.Random;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.plot3d.primitives.graphs.layout.DefaultGraphLayout2d;


public class StringGraphGenerator {
  public static IGraph<String, String> getGraph(int nodes, int edges) {
    IGraph<String, String> graph = new DefaultGraph<String, String>();

    for (int i = 0; i < nodes; i++) {
      graph.addVertex("vertex " + i);
    }

    for (int i = 0; i < edges; i++) {
      String v1 = graph.getRandomVertex();
      String v2 = graph.getRandomVertex();
      graph.addEdge("edge " + v1 + v2, v1, v2);
    }
    return graph;
  }

  public static DefaultGraphLayout2d<String> getRandomLayout(IGraph<String, String> graph,
      float size) {
    DefaultGraphLayout2d<String> layout = new DefaultGraphLayout2d<String>();
    Random rng = new Random();
    rng.setSeed(0);

    for (String v : graph.getVertices()) {
      float x = rng.nextFloat() * size - size / 2;
      float y = rng.nextFloat() * size - size / 2;
      layout.setVertexPosition(v, new Coord2d(x, y));
    }
    return layout;
  }
}
