package org.jzy3d.maths.graphs;

import java.util.List;

public interface IGraph<V, E> {
  public void addVertex(V vertex);

  public void addEdge(E edge, V v1, V v2);

  public V getVertex(int i);

  public V getRandomVertex();
  // public E getEdge(int i);

  public List<V> getVertices();

  public List<E> getEdges();

  public V getEdgeStartVertex(E e);

  public V getEdgeStopVertex(E e);
}
