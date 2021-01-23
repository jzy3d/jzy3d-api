package org.jzy3d.maths.algorithms.convexhull.utils;


/**
 *
 * @author Teemu
 */
/**
 * Solmualkio.
 * 
 * @author Teemu
 * @param T
 */
public class Node<T> {

  /** */
  private T data;

  /** */
  private Node<T> next;

  public Node(T data, Node<T> next) {
    this.data = data;
    this.next = next;
  }

  public T get() {
    return data;
  }

  public Node<T> next() {
    return next;
  }

  public void setNext(Node<T> next) {
    this.next = next;
  }

  public void setData(T data) {
    this.data = data;
  }
}
