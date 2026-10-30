package org.jzy3d.io;

import java.util.List;
import org.jzy3d.plot3d.primitives.Drawable;

public interface ILoader {
  public List<Drawable> load(String file) throws Exception;
}
