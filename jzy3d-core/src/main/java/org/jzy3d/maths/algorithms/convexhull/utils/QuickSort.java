package org.jzy3d.maths.algorithms.convexhull.utils;

import java.util.Random;

/**
 * Pikalajittelu
 *
 * @author Teemu Linkosaari
 *
 */
public class QuickSort {

  /**
   * satunnaislukugeneraattori *
   */
  private static Random gen = new Random();

  /**
   *
   */
  private static <T> void quickSort(T[] A, int p, int r, IComparator<? super T> c) {
    if (p < r) {
      int q = randomPartition(A, p, r, c);
      quickSort(A, p, q - 1, c);
      quickSort(A, q + 1, r, c);
    }
  }

  /**
   *
   */
  private static <T> int randomPartition(T[] A, int p, int r, IComparator<? super T> c) {

    assert r >= p;
    assert c != null;

    int i = p + gen.nextInt(r - p + 1);

    assert i >= p;
    assert i <= r;

    T temp = A[r];
    A[r] = A[i];
    A[i] = temp;

    return partition(A, p, r, c);
  }

  /**
   *
   */
  private static <T> int partition(T[] A, int p, int r, IComparator<? super T> c) {

    assert A != null : "Error: ";
    assert p >= 0 : "Error: ";
    assert r < A.length : "Error: ";

    T x = A[r];
    int i = p - 1;

    for (int j = p; j < r; j++) {

      if (c.compare(A[j], x) <= 0) {
        i += 1;

        // swap
        T temp = A[i];
        A[i] = A[j];
        A[j] = temp;
      }
    }

    T temp = A[i + 1];
    A[i + 1] = A[r];
    A[r] = temp;

    return i + 1;
  }

  /**
   * @.pre { FORALL( i : a[t] != null ) }
   */
  public static <T> void sort(T[] a, IComparator<? super T> c) {
    assert c != null;
    quickSort(a, 0, a.length - 1, c);
  }
}
