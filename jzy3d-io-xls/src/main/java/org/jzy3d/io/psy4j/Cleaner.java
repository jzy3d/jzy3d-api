package org.jzy3d.io.psy4j;

import java.util.Set;

public interface Cleaner {
  public String clean(String input);
  public Set<String> clean(Set<String> input);
}
