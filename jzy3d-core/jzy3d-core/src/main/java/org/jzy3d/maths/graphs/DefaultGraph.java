package org.jzy3d.maths.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultGraph<V, E> implements IGraph<V, E> {
  @Override
  public void addVertex(V vertex) {
    vertices.add(vertex);
  }

  @Override
  public V getVertex(int i) {
    return vertices.get(i);
  }

  @Override
  public List<V> getVertices() {
    return vertices;
  }

  /*********************************/

  @Override
  public void addEdge(E edge, V v1, V v2) {
    edges.add(edge);
    edgeStart.put(edge, v1);
    edgeStop.put(edge, v2);
  }

  @Override
  public List<E> getEdges() {
    return edges;
  }

  @Override
  public V getEdgeStartVertex(E e) {
    return edgeStart.get(e);
  }

  @Override
  public V getEdgeStopVertex(E e) {
    return edgeStop.get(e);
  }

  /*********************************/

  @Override
  public V getRandomVertex() {
    int id = (int) (Math.random() * vertices.size());
    return getVertex(id);
  }

  /*********************************/

  protected List<V> vertices = new ArrayList<V>();
  protected List<E> edges = new ArrayList<E>();
  protected Map<E, V> edgeStart = new HashMap<E, V>();
  protected Map<E, V> edgeStop = new HashMap<E, V>();


}
