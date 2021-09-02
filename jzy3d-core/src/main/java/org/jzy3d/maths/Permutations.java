package org.jzy3d.maths;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate all possible permutations of the input list
 * 
 * May study https://www.quickperm.org/ as an faster alternative.
 * 
 * @author martin
 *
 */
public class Permutations {
  public static <E> List<List<E>> of(List<E> original) {
    if (original.isEmpty()) {
      List<List<E>> result = new ArrayList<>();
      result.add(new ArrayList<>());
      return result;
    }
    else {
      E firstElement = original.remove(0);
      List<List<E>> returnValue = new ArrayList<>();
      List<List<E>> permutations = of(original);
      for (List<E> smallerPermutated : permutations) {
        for (int index = 0; index <= smallerPermutated.size(); index++) {
          List<E> temp = new ArrayList<>(smallerPermutated);
          temp.add(index, firstElement);
          returnValue.add(temp);
        }
      }
      return returnValue;
    }
  }
}
