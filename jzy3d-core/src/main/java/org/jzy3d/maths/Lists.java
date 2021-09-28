package org.jzy3d.maths;

import java.util.ArrayList;

public class Lists {
  public static <T> java.util.List<T> of(T... items) {
    java.util.List<T> set = new ArrayList<>(items.length);
    for (int i = 0; i < items.length; i++) {
      set.add(items[i]);
    }
    return set;
  }

}
