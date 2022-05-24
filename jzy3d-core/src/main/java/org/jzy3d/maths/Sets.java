package org.jzy3d.maths;

import java.util.HashSet;

public class Sets {
  public static <T> java.util.Set<T> of(T... items) {
    java.util.Set<T> set = new HashSet<>(items.length);
    for (int i = 0; i < items.length; i++) {
      set.add(items[i]);
    }
    return set;
  }
}
